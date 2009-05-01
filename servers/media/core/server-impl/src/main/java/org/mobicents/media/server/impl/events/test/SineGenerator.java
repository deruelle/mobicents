/*
 * Generator.java
 *
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
package org.mobicents.media.server.impl.events.test;

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.clock.TimerImpl;

/**
 *
 * @author Oleg Kulikov
 */
public class SineGenerator extends AbstractSource implements Runnable {

    private byte[] data;
    
    private final static AudioFormat LINEAR_AUDIO = new AudioFormat(
            AudioFormat.LINEAR, 8000, 16, 1,
            AudioFormat.LITTLE_ENDIAN,
            AudioFormat.SIGNED);
    private final static Format formats[] = new Format[] {LINEAR_AUDIO};
    
    private TimerImpl timer = new TimerImpl();
    private int sizeInBytes;
    private int offset;
    private int seq;
    
    /** Creates a new instance of Generator */
    public SineGenerator(int freq) {
    	super("SineGenerator");
        data = new byte[(int)
                LINEAR_AUDIO.getSampleRate() * 
                LINEAR_AUDIO.getSampleSizeInBits()/8];

        sizeInBytes = (int) (LINEAR_AUDIO.getSampleRate() * 
                (LINEAR_AUDIO.getSampleSizeInBits() / 8)/1000 * 20); // Duration
        
        int len = data.length / 2;
        int k = 0;

        for (int i = 0; i < len; i++) {
            short s = (short) (Short.MAX_VALUE * Math.sin(2 * Math.PI * freq * i / len));
            data[k++] = (byte) s;
            data[k++] = (byte) (s >> 8);
        }
    }
    
    public void start() {
//        timer.setListener(this);
//        timer.start();
    }

    public void stop() {
//        timer.stop();
    }

    public void run() {
        byte[] media = new byte[sizeInBytes];

        int count = Math.min(data.length - offset, sizeInBytes);
        System.arraycopy(data, offset, media, 0, count);
        offset += count;
        if (offset == data.length) {
            offset = 0;
        }
        
        Buffer buffer = new Buffer();        
        buffer.setOffset(0);
        buffer.setLength(media.length);
        buffer.setSequenceNumber(seq);
//        buffer.setDuration(Quartz.HEART_BEAT);
//        buffer.setTimeStamp(seq * Quartz.HEART_BEAT); 
        buffer.setData(media);
        buffer.setFormat(LINEAR_AUDIO);
        seq++;
        
        if (otherParty != null) {
            otherParty.receive(buffer);
        }
    }

    public Format[] getFormats() {
        return formats;
    }

    public void started() {
    }

    public void ended() {
    }

    
}
