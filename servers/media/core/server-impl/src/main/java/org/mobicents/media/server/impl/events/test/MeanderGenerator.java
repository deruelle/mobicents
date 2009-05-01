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
public class MeanderGenerator extends AbstractSource implements Runnable {
    
    private final static AudioFormat LINEAR_AUDIO = new AudioFormat(
            AudioFormat.LINEAR, 8000, 16, 1,
            AudioFormat.LITTLE_ENDIAN,
            AudioFormat.SIGNED);
    
    private final static Format[] formats = new Format[] {LINEAR_AUDIO};
    
    private boolean started = false;
    private byte[] silence;
    private byte[] sound;
    private long seqNumber = 0;
    
    private int sizeInBytes;
    private TimerImpl timer = new TimerImpl();
    private boolean isSilence = false;
    
    public MeanderGenerator() {
    	super("MeanderGenerator");
        sizeInBytes = (int) ((LINEAR_AUDIO.getSampleRate() / 1000) *
                (LINEAR_AUDIO.getSampleSizeInBits() / 8) * 20);
        
        silence = new byte[sizeInBytes];
        sound = new byte[sizeInBytes];

        int k = 0;
        for (int i = 0; i < sound.length / 2; i++) {
            sound[k++] = (byte) (Short.MAX_VALUE >> 8);
            sound[k++] = (byte) (Short.MAX_VALUE);
        }
    }

    public void start() {
        if (!started) {
//            timer.start();
            started = true;
        }
    }

    public void stop() {
        if (started) {
//            timer.stop();
            started = false;
        }
    }

    public void run() {
        byte[] media = isSilence ? silence : sound;
        Buffer buffer = new Buffer();
        buffer.setOffset(0);
        buffer.setLength(media.length);
        buffer.setSequenceNumber(seqNumber);
//        buffer.setDuration(Quartz.HEART_BEAT);
//        buffer.setTimeStamp(seqNumber * Quartz.HEART_BEAT); 
        buffer.setData(media);
        buffer.setFormat(LINEAR_AUDIO);
        seqNumber++;
        isSilence = !isSilence;
    }

    public Format[] getFormats() {
        return formats;
    }
    

}
