/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.resource.ss7;

import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author kulikov
 */
public class LocalChannel {
    public final static int RX = 0;
    public final static int TX = 1;
    
    private Semaphore semaphore = new Semaphore(0);
    
    private byte[] localBuff = new byte[8192];
    private int pos;
    
    private volatile boolean blocked = false;
    private int direction = -1;
    
    private Mtp1 mtp1;
    
    public LocalChannel() {
    }
    
    public void setDirection(int direction) {
        this.direction = direction;
    }
    
    public int getDirection() {
        return direction;
    }
    
    
    public int read(byte[] buffer) throws IOException {
        if (pos == 0) {
            blocked = true;
            try {
                semaphore.tryAcquire(128, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                throw new IOException(e.getMessage());
            }
        }
        
        if (blocked) {
            return buffer.length;
        }
        
        int len = Math.min(pos, buffer.length);
        System.arraycopy(localBuff, 0, buffer, 0, len);
        System.arraycopy(localBuff, len, localBuff, 0, (pos -len));
        pos -= len;
        return len;
    }

    public Mtp1 getMtp1() {
        return mtp1;
    }

    public void setMtp1(Mtp1 mtp1) {
        this.mtp1 = mtp1;
    }
    
    
    public void push(byte[] buffer) {
        System.arraycopy(buffer, 0, localBuff, pos, buffer.length);
//        for (int i = 0; i < buffer.length; i++) {
//            localBuff[pos+i] = buffer[buffer.length - 1 - i];
//        }
        pos += buffer.length;
        if (blocked) {
            blocked = false;
            semaphore.release();
        }
    }
}
