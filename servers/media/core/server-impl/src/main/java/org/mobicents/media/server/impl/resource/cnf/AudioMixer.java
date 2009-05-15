/*
 * Mobicents Media Gateway
 *
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */
package org.mobicents.media.server.impl.resource.cnf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.apache.log4j.Logger;
import org.mobicents.media.Buffer;
import org.mobicents.media.BufferFactory;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSource;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.Timer;

/**
 * 
 * @author Oleg Kulikov
 */
public class AudioMixer extends AbstractSink implements Serializable {

    private String name;
    protected final static AudioFormat LINEAR = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1,
            AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
    protected final static Format[] formats = new Format[]{LINEAR};
    
    private Timer timer;
    private transient ScheduledFuture worker;
    
    private ConcurrentHashMap<MediaSource, MixerInputStream> inputs = new ConcurrentHashMap<MediaSource, MixerInputStream>();
    private volatile boolean started = false;
    
    private AudioFormat fmt = LINEAR;
    private int packetSize;
    private int packetPeriod = 20;
    private int jitter = 60;
    private int seq;
    private MixerOutput mixerOutput;
    private double targetGain = 1;
    private double currentGain = 1;
    private static double maxStepDown = 1. / 22; // 51 samples transition
    // from gain 1 to gain 0
    private static double maxStepUp = 1. / 4000; // 3000 samples transition
    // from gain 1 to gain 0
    private transient Logger logger = Logger.getLogger(AudioMixer.class);
    
    private BufferFactory bufferFactory = null;

    /**
     * Creates a new instance of AudioMixer.
     * 
     * @param packetPeriod
     *            packetization period in milliseconds.
     * @param fmt
     *            format of the output stream.
     */
    public AudioMixer(Endpoint endpoint, String name) {
        super("AudioMixer[" + endpoint.getLocalName() + "]");
        bufferFactory = new BufferFactory(10, "AudioMixer[" + endpoint.getLocalName() + "]");
        this.name = "AudioMixer[" + endpoint.getLocalName() + "]/" + name;
        this.timer = endpoint.getTimer();
        this.mixerOutput = new MixerOutput();
        this.init();
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

    public MediaSource getOutput() {
        return mixerOutput;
    }

    @Override
    public void connect(MediaSource stream) {
        //super.connect(stream);
        MixerInputStream input = new MixerInputStream(this, jitter);
        inputs.put(stream, input);
        input.connect(stream);
    }

    @Override
    public void disconnect(MediaSource stream) {
        MixerInputStream input = (MixerInputStream) inputs.remove(stream);
        if (input != null) {
            input.disconnect(stream);
        }
        //super.disconnect(stream);
    }

    public int getInputCount() {
        return inputs.size();
    }

    /**
     * Starts mixer.
     */
    public void start() {
        started = true;
        worker = timer.synchronize(new Mixer());
    }

    /**
     * Terminates mixer.
     */
    public void stop() {
        started = false;
        if (worker != null) {
            worker.cancel(true);
        }
    }

    @Override
    public String toString() {
        return "AudioMixer[" + name + "]";
    }

    /**
     * Implements mixing procedure for a buffer of audio.
     */
    private class Mixer implements Runnable {

        public short[] byteToShortArray(byte[] input) {
            short[] output = new short[input.length >> 1];
            for (int q = 0; q < input.length; q += 2) {
                short f = (short) (((input[q + 1]) << 8) | (input[q] & 0xff));
                output[q >> 1] = f;
            }
            return output;
        }

        public byte[] mix(ArrayList<byte[]> input) {
            int numSamples = packetSize >> 1;
            short[][] inputs = new short[input.size()][];
            System.out.println("Input size=" + input.size());
            for (int q = 0; q < input.size(); q++) {
                inputs[q] = byteToShortArray(input.get(q));
            }

            int[] mixed = new int[numSamples];

            for (int q = 0; q < numSamples; q++) {
                for (int w = 0; w < input.size(); w++) {
                    mixed[q] += inputs[w][q];
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

        public void run() {
            Collection<MixerInputStream> buffers = inputs.values();
            ArrayList<byte[]> frames = new ArrayList();
            for (MixerInputStream buffer : buffers) {
                if (buffer.isReady()) {
                    frames.add(buffer.read(packetPeriod));
                }
            }

            byte[] data = mix(frames);
            push(data);
        }
    }

    private void push(byte[] data) {
        Buffer buffer = bufferFactory.allocate();
        buffer.setData(data);
        buffer.setOffset(0);
        buffer.setLength(data.length);
        buffer.setDuration(packetPeriod);
        buffer.setTimeStamp(packetPeriod * seq);
        buffer.setSequenceNumber(seq++);

        buffer.setFormat(fmt);
        mixerOutput.push(buffer);
    }

    public boolean isAcceptable(Format fmt) {
        return fmt.matches(LINEAR);
    }

    public void receive(Buffer buffer) {
        throw new UnsupportedOperationException();
    }

    public Format[] getFormats() {
        return formats;
    }
}
