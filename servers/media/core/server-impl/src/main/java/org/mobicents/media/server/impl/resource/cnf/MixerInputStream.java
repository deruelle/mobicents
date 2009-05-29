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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.AbstractSink;

/**
 *
 * @author Oleg Kulikov
 */
public class MixerInputStream extends AbstractSink {

    private List<Buffer> buffers = Collections.synchronizedList(new ArrayList());
    private int jitter;
    private long duration;
    protected AudioMixer mixer;
        
    public MixerInputStream(AudioMixer mixer, int jitter) {
    	super("MixerInputStream");
        this.mixer = mixer;
        this.jitter = jitter;
    }

    @Override
    public String getId() {
        return mixer.getId();
    }
    
    public boolean isReady() {
        return !buffers.isEmpty();
    }

    /**
     * Gets the current jitter.
     * 
     * @return jitter value in milliseconds.
     */
    public long getJitter() {
        return duration;
    }

    public void receive(Buffer buffer) {
        if (mixer == null) {
        	buffer.dispose();
            return;
        }
        
        if (duration + buffer.getDuration() > jitter) {
            //silently discard packet
        	buffer.dispose();
            return;
        }

        duration += buffer.getDuration();
        buffers.add(buffer);
//        print(buffer);
    }
    private void print(Buffer buffer) {
        int len = buffer.getLength();
        int offset = buffer.getOffset();
        
        byte[] data = (byte[])buffer.getData();
        for (int i =offset; i < len; i++) {
            System.out.print(data[i] + " ");
        }
            System.out.println("");
    }
    
    /**
     * Reads media buffer from this stream with specified duration.
     * 
     * @param duration the duration of the requested buffer in milliseconds.
     * @return buffer which contains duration ms media for 8000Hz, 16bit, linear audio.
     */
    public byte[] read(int duration) {
        int size = 16 * duration;
        byte[] media = new byte[size];

        int count = 0;
        while (count < size && !buffers.isEmpty()) {
            Buffer buff = buffers.get(0);
            
            if (buff.isEOM() || buff.getData() == null) {
                buffers.remove(buff);
                this.duration -= buff.getDuration();
                buff.dispose();
                break;
            }
            
            int len = Math.min(size - count, buff.getLength() - buff.getOffset());
            System.arraycopy(buff.getData(), buff.getOffset(), media, count, len);

            count += len;
            buff.setOffset(buff.getOffset() + len);

            if (buff.getOffset() == buff.getLength()) {
                buffers.remove(buff);
                this.duration -= buff.getDuration();
                buff.dispose();
            }
        }

        return media;
    }
    
    @Override
    public String toString() {
        return mixer.toString();
    }

    /**
     * (Non Java-doc.)
     * 
     * @see org.mobicents.media.MediaSink.isAcceptable(Format).
     */
    public boolean isAcceptable(Format fmt) {
        return fmt.matches(AudioMixer.LINEAR);
    }

    public Format[] getFormats() {
        return AudioMixer.formats;
    }

}
