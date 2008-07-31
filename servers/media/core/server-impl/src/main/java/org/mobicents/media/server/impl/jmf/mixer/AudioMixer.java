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
package org.mobicents.media.server.impl.jmf.mixer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.format.UnsupportedFormatException;
import org.mobicents.media.protocol.PushBufferStream;
import org.apache.log4j.Logger;

/**
 *
 * @author Oleg Kulikov
 */
public class AudioMixer implements Serializable {
    
    private static int ID_GENERATOR = 0;
    
    private final static AudioFormat LINEAR = new AudioFormat(
            AudioFormat.LINEAR, 8000, 16, 1,
            AudioFormat.LITTLE_ENDIAN,
            AudioFormat.SIGNED);
    
    private String ID;
    private TimerTask mixer;
    protected Timer timer;
    
    private MixerOutputStream outputStream;
    private List<MixerInputStream> buffers = Collections.synchronizedList(new ArrayList());
    private boolean started = false;
    
    private AudioFormat fmt = LINEAR;
    
    private int packetSize;
    private int packetPeriod;
    private int jitter;
    
    private double targetGain = 1;
    private double currentGain = 1;
    private static double maxStepDown = 1./22; // 51 samples transition from gain 1 to gain 0
    private static double maxStepUp = 1./4000; // 3000 samples transition from gain 1 to gain 0
    
    private Logger logger = Logger.getLogger(AudioMixer.class);
    
    /** 
     * Creates a new instance of AudioMixer.
     * 
     * @param packetPeriod packetization period in milliseconds. 
     */
/*    public AudioMixer(Timer timer, int packetPeriod, int jitter) {
        this.timer = timer;
        this.packetPeriod = packetPeriod;
        this.jitter = jitter;
        
        if (ID_GENERATOR == Integer.MAX_VALUE) ID_GENERATOR = 0;
        this.ID = Integer.toHexString(ID_GENERATOR++);
        
        try {
            this.init();
        } catch (UnsupportedFormatException e) {
        }
    }
*/
    /** 
     * Creates a new instance of AudioMixer.
     * 
     * @param packetPeriod packetization period in milliseconds. 
     * @param fmt format of the output stream.
     */
    public AudioMixer(Timer timer, int packetPeriod, int jitter, AudioFormat fmt) throws UnsupportedFormatException {
        this.timer = timer;
        this.packetPeriod = packetPeriod;
        this.jitter = jitter;
        this.fmt = fmt;
        
        if (ID_GENERATOR == Integer.MAX_VALUE) ID_GENERATOR = 0;
        this.ID = Integer.toHexString(ID_GENERATOR++);
        
        this.init();
    }
    
    /**
     * Initializes audio mixer.
     * 
     * @throws javax.media.format.UnsupportedFormatException
     */
    private void init() throws UnsupportedFormatException {
        this.packetSize = 16 * packetPeriod;        
        outputStream = new MixerOutputStream(this, fmt, packetPeriod);
    }
    
    /**
     * Gets the numbers of input streams used for mixing.
     * 
     * @return the numbers of streams.
     */
    public int size() {
        return buffers.size();
    }

    /**
     * Adds specified stream to this mixer.
     * 
     * @param stream the stream for mixing.
     * @throws UnsupportedFormatException if format of the specified stream
     * can not be mixed or decoded.
     */
    public synchronized void addInputStream(PushBufferStream stream) throws UnsupportedFormatException {
        MixerInputStream buffer = new MixerInputStream(this, stream, jitter);
        buffers.add(buffer);
    }

    /**
     * Remove specified stream from mixer.
     * 
     * @param stream the stream to be removed.
     */
    public synchronized void removeInputStream(PushBufferStream stream) {
        MixerInputStream buff = null;
        for (MixerInputStream buffer : buffers) {
            if (buffer.stream == stream) {
                buff = buffer;
                break;
            }
        }

        if (buff != null) {
            buffers.remove(buff);
        }
    }

    /**
     * Gets the output stream which is a result of mixing input streams.
     * 
     * @return the result of the mixing of input streams.
     */
    public PushBufferStream getOutputStream() {
        return outputStream;
    }

    /**
     * Starts mixer.
     */
    public void start() {
        if (!started) {
            started = true;
            mixer = new Mixer();
            timer.scheduleAtFixedRate(mixer, packetPeriod, packetPeriod);
        }
    }

    /**
     * Terminates mixer.
     */
    public void stop() {
        if (started) {
            started = false;
            mixer.cancel();
            timer.purge();
        }
    }
    
    @Override
    public String toString() {
        return "AudioMixer[ID=" + ID + "]";
    }
    
    /**
     * Implements mixing procedure for a buffer of audio.
     */
    private class Mixer extends TimerTask {
    	
    	
    	public short[] byteToShortArray(byte[] input) {
    		short[] output = new short[input.length>>1];
    		for(int q=0; q<input.length; q+=2) {
    			short f = (short) (((input[q + 1]) << 8) | (input[q] & 0xff));
    			output[q>>1] = f;
    		}
    		return output;
    	}
    	
    	public byte[] mix(ArrayList<byte[]> input) {
    		int numSamples = packetSize>>1;
    		short[][] inputs = new short[input.size()][];
    		for(int q=0; q<input.size(); q++) {
    			inputs[q] = byteToShortArray(input.get(q));
    		}
    		
    		int[] mixed = new int[numSamples];
    		
    		for(int q=0; q<numSamples; q++) {
    			for(int w=0; w<input.size(); w++) {
    				mixed[q] += inputs[w][q];
    			}
    		}
    		
    		int numExceeding = 0;
    		int maxExcess = 0;
    		
    		for(int q=0; q<numSamples; q++) {
    			int excess = 0;
    			int overflow = mixed[q] - Short.MAX_VALUE;
    			int underflow = mixed[q] - Short.MIN_VALUE;
    			
    			if(overflow>0) excess = overflow;
    			else if(underflow<0) excess = -underflow;
    			
    			if(excess>0) numExceeding++;
    			maxExcess = Math.max(maxExcess, excess);
    		}
    		
    		if(numExceeding > numSamples>>5) {
    			targetGain = (float)(Short.MAX_VALUE)/(float)(Short.MAX_VALUE + maxExcess + 2000);
    		} else 
    			targetGain = 1;
    		
    		byte[] data = new byte[packetSize];
    		int l = 0;
    		for(int q=0; q<numSamples; q++) {
    			mixed[q] *= currentGain;
    			if(targetGain-currentGain>=maxStepUp) currentGain += maxStepUp;
    			else if (currentGain-targetGain>maxStepDown) currentGain -= maxStepDown;
    			short s = (short) (mixed[q]);
                data[l++] = (byte) (s);
                data[l++] = (byte) (s >> 8);
    		}
    		return data;
    	}
    	
        public synchronized void run() {

            ArrayList<byte[]> frames = new ArrayList();
            for (MixerInputStream buffer : buffers) {
                if (buffer.isReady()) {
                    frames.add(buffer.read(packetPeriod));
                } 
            }

            byte[] data = mix(frames);

            outputStream.push(data);
        }
    }
}
