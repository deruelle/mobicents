/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package org.mobicents.media.server.impl.resource.cnf;

import java.util.ArrayList;
import java.util.Collection;

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.Outlet;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.AbstractSinkSet;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.Timer;

/**
 * 
 * @author Oleg Kulikov
 */
public class AudioMixer extends AbstractSinkSet implements Outlet {

    protected final static AudioFormat LINEAR = new AudioFormat(
            AudioFormat.LINEAR, 8000, 16, 1,
            AudioFormat.LITTLE_ENDIAN, 
            AudioFormat.SIGNED);
    protected final static Format[] formats = new Format[]{LINEAR};
    private int packetSize;
    private int packetPeriod = 20;
    private int jitter = 60;
    private MixerOutput mixerOutput;
    private double targetGain = 1;
    private double currentGain = 1;
    private static double maxStepDown = 1. / 22; // 51 samples transition
    // from gain 1 to gain 0
    private static double maxStepUp = 1. / 4000; // 3000 samples transition
    // from gain 1 to gain 0

    /**
     * Creates a new instance of AudioMixer.
     * 
     * @param packetPeriod
     *            packetization period in milliseconds.
     * @param fmt
     *            format of the output stream.
     */
    public AudioMixer(String name, Timer timer) {
        super(name);
        mixerOutput = new MixerOutput(this);
        mixerOutput.setSyncSource(timer);
        init();
    }

    /**
     * Initializes audio mixer.
     * 
     * @throws javax.media.format.UnsupportedFormatException
     */
    private void init() {
        this.packetSize = 16 * packetPeriod;
    }

    /**
     * (Non Java-doc.)
     * 
     * @see org.mobicents.media.Outlet#getOutput(). 
     */
    public MediaSource getOutput() {
        return mixerOutput;
    }

    @Override
    public void start() {
        mixerOutput.start();
    }

    @Override
    public void stop() {
        mixerOutput.stop();
    }

    @Override
    public void setEndpoint(Endpoint endpoint) {
        super.setEndpoint(endpoint);
        mixerOutput.setEndpoint(endpoint);
        System.out.println("Set endpoint " + endpoint);
        Collection<AbstractSink> streams = getStreams();
        for (AbstractSink stream : streams) {
            stream.setEndpoint(endpoint);
        }
    }

    @Override
    public void setConnection(Connection connection) {
        super.setConnection(connection);
        mixerOutput.setConnection(connection);
        Collection<AbstractSink> streams = getStreams();
        for (AbstractSink stream : streams) {
            stream.setConnection(connection);
        }
    }
    
    /**
     * Converts inner byte representation of the signal into 
     * 16bit per sample array
     * 
     * @param input the array where sample takes two elements.
     * @return array where sample takes one element.
     */
    public short[] byteToShortArray(byte[] input) {
        short[] output = new short[input.length >> 1];
        for (int q = 0; q < input.length; q += 2) {
            short f = (short) (((input[q + 1]) << 8) | (input[q] & 0xff));
            output[q >> 1] = f;
        }
        return output;
    }

    /**
     * Mixes input packets.
     * 
     * @param input collection of arras of samples of same length
     * @return array of result array of samples.
     */
    public byte[] mix(ArrayList<byte[]> input) {
        int numSamples = packetSize >> 1;
        short[][] streams = new short[input.size()][];
        for (int q = 0; q < input.size(); q++) {
            streams[q] = byteToShortArray(input.get(q));
        }

        int[] mixed = new int[numSamples];

        for (int q = 0; q < numSamples; q++) {
            for (int w = 0; w < input.size(); w++) {
                mixed[q] += streams[w][q];
            }
        }

        int numExceeding = 0;
        int maxExcess = 0;

        for (int q = 0; q < numSamples; q++) {
            int excess = 0;
            int overflow = mixed[q] - Short.MAX_VALUE;
            int underflow = mixed[q] - Short.MIN_VALUE;

            if (overflow > 0) {
                excess = overflow;
            } else if (underflow < 0) {
                excess = -underflow;
            }

            if (excess > 0) {
                numExceeding++;
            }
            maxExcess = Math.max(maxExcess, excess);
        }

        if (numExceeding > numSamples >> 5) {
            targetGain = (float) (Short.MAX_VALUE) / (float) (Short.MAX_VALUE + maxExcess + 2000);
        } else {
            targetGain = 1;
        }

        byte[] data = new byte[packetSize];
        int l = 0;
        for (int q = 0; q < numSamples; q++) {
            mixed[q] *= currentGain;
            if (targetGain - currentGain >= maxStepUp) {
                currentGain += maxStepUp;
            } else if (currentGain - targetGain > maxStepDown) {
                currentGain -= maxStepDown;
            }
            short s = (short) (mixed[q]);
            data[l++] = (byte) (s);
            data[l++] = (byte) (s >> 8);
        }
        return data;
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.server.impl.AbstractSource#evolve(org.mobicents.media.Buffer, long). 
     */
    public void evolve(Buffer buffer, long timestamp, long seq) {
        Collection<AbstractSink> streams = getStreams();
        ArrayList<byte[]> frames = new ArrayList();
        for (AbstractSink stream : streams) {
            MixerInputStream input = (MixerInputStream) stream;
            if (input.isReady()) {
                frames.add(input.read(packetPeriod));
            }
        }

        byte[] data = mix(frames);
        
        buffer.setData(data);
        buffer.setOffset(0);
        buffer.setLength(data.length);
        buffer.setDuration(packetPeriod);
        buffer.setTimeStamp(timestamp);
        buffer.setSequenceNumber(seq);

        buffer.setFormat(LINEAR);
        buffer.setEOM(false);
        buffer.setDiscard(false);
    }

    /**
     * (Non Java-doc.)
     * 
     * @see org.mobicents.media.MediaSink#isAcceptable(org.mobicents.media.Format) 
     */
    public boolean isAcceptable(Format fmt) {
        return fmt.matches(LINEAR);
    }

    /**
     * (Non Java-doc.)
     * @see org.mobicents.media.server.impl.AbstractSink#onMediaTransfer(org.mobicents.media.Buffer) 
     */
    public void onMediaTransfer(Buffer buffer) {
        throw new UnsupportedOperationException();
    }

    /**
     * (Non Java-doc.)
     * 
     * @see org.mobicents.media.MediaSink#getFormats() 
     */
    public Format[] getFormats() {
        return formats;
    }

    public void connect(MediaSink sink) {
        getOutput().connect(sink);
    }

    public void disconnect(MediaSink sink) {
        getOutput().disconnect(sink);
    }

    @Override
    public AbstractSink createSink(MediaSource otherParty) {
                MixerInputStream input = new MixerInputStream(this, jitter);
                return input;
    }

    @Override
    public void destroySink(AbstractSink sink) {
        ((MixerInputStream) sink).mixer = null;
    }

}
