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
import java.util.concurrent.ConcurrentHashMap;

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSource;
import org.mobicents.media.Outlet;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.spi.Timer;

/**
 * 
 * @author Oleg Kulikov
 */
public class AudioMixer extends AbstractSink implements Outlet {

    protected final static AudioFormat LINEAR = new AudioFormat(
            AudioFormat.LINEAR, 8000, 16, 1,
            AudioFormat.LITTLE_ENDIAN, 
            AudioFormat.SIGNED);
    protected final static Format[] formats = new Format[]{LINEAR};
    private ConcurrentHashMap<MediaSource, MixerInputStream> inputs = new ConcurrentHashMap<MediaSource, MixerInputStream>();
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
     * Gets the numbers of input streams used for mixing.
     * 
     * @return the numbers of streams.
     */
    public int size() {
        return inputs.size();
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
    public void connect(MediaSource stream) {
        MixerInputStream input = new MixerInputStream(this, jitter);
        inputs.put(stream, input);
        input.connect(stream);
        input.start();
    }

    @Override
    public void disconnect(MediaSource stream) {
        MixerInputStream input = (MixerInputStream) inputs.remove(stream);
        if (input != null) {
            input.stop();
            input.disconnect(stream);
        }
    }

    /**
     * Gets the number of active input streams.
     * 
     * @return the number of active streams.
     */
    public int getInputCount() {
        return inputs.size();
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
    public String toString() {
        return "AudioMixer[" + getName() + "]";
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
    public void evolve(Buffer buffer, long seq) {
        Collection<MixerInputStream> streams = inputs.values();
        ArrayList<byte[]> frames = new ArrayList();
        for (MixerInputStream stream : streams) {
            if (stream.isReady()) {
                frames.add(stream.read(packetPeriod));
            }
        }

        byte[] data = mix(frames);
        
        buffer.setData(data);
        buffer.setOffset(0);
        buffer.setLength(data.length);
        buffer.setDuration(packetPeriod);
        buffer.setTimeStamp(packetPeriod * seq);
        buffer.setSequenceNumber(seq);

        buffer.setFormat(LINEAR);
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
}
