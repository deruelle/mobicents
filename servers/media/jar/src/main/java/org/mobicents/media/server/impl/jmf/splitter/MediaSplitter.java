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

package org.mobicents.media.server.impl.jmf.splitter;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import javax.media.Buffer;
import javax.media.Format;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.PushBufferStream;

/**
 * The MediaSplitter allows to connect one media stream to any number of 
 * destinations.
 * 
 * 
 * @author Oleg Kulikov
 */
public class MediaSplitter implements Serializable, BufferTransferHandler {

    private List <SplitterOutputStream> branches = Collections.synchronizedList(new ArrayList());
    private Format fmt;
    private Timer timer;
    
    public MediaSplitter() {
    }

    public MediaSplitter(Timer timer) {
        this.timer = timer;
    }
    
    public Timer getTimer() {
        return timer;
    }
    
    public void setTimer(Timer timer) {
        this.timer = timer;
    }
    
    public void setInputStream(PushBufferStream pushStream) {
        fmt = pushStream.getFormat();
        pushStream.setTransferHandler(this);
    }
    
    public int getSize() {
        return branches.size();
    }
    
    @SuppressWarnings("static-access")
    private void block(long delay) {
        synchronized (this) {
            try {
                Thread.currentThread().sleep(delay);
            } catch (InterruptedException e) {
            }
        }
    }
    
    public void close() {
        for (SplitterOutputStream branch: branches) {
            branch.close();
        }
        branches.clear();
    }
    
    public void transferData(PushBufferStream stream) {
        try {
            Buffer buffer = new Buffer();
            stream.read(buffer);
            
            for (SplitterOutputStream branch : branches) {
                branch.push((Buffer)buffer.clone());
            }
            
        } catch (IOException e) {
        }       
    }

    public synchronized PushBufferStream newBranch() {
        SplitterOutputStream s = new SplitterOutputStream(fmt);
        branches.add(s);
        return s;
    }
    
    public synchronized void closeBranch(PushBufferStream branch) {
        branches.remove(branch);
    }
}
