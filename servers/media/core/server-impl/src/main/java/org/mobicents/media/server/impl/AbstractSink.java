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

package org.mobicents.media.server.impl;

import java.io.IOException;
import java.util.ArrayList;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;

/**
 *
 * @author Oleg Kulikov
 */
public abstract class AbstractSink implements MediaSink {

    protected MediaSource mediaStream;
    protected ArrayList commonFormats;
    
    
    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.MediaSink#connect(MediaStream).
     */
    public void connect(MediaSource mediaStream) throws IOException {
        if (!this.checkFormats(this.getFormats(), mediaStream.getFormats())) {
            throw new IOException("No common formats");
        }
        
        this.mediaStream = mediaStream;
        
        if (((AbstractSource) mediaStream).sink == null) {
            mediaStream.connect(this);
        }
        //((AbstractSource) mediaStream).sink = this;
        //((AbstractSource) mediaStream).commonFormats = this.commonFormats;
    }
    
    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.MediaSink#disconnect(MediaStream).
     */
    public void disconnect(MediaSource mediaStream) {
        this.mediaStream = null;
        ((AbstractSource) mediaStream).sink = null;
        ((AbstractSource) mediaStream).commonFormats = null;
    }
    
    /**
     * Seraches common media formats in two lists.
     * Common formats are stored in <code>comminFormat</code> varibale.
     * 
     * @param list1 first list
     * @param list2 second list
     * @return true if both lists have common formats.
     */
    private boolean checkFormats(Format[] list1, Format[] list2) {
        commonFormats = new ArrayList();
        if (list1 == null && list2 == null) {
            return true;
        }
        
        if (list1 == null) {
            for (Format f : list2) {
                commonFormats.add(f);
            }
            return true;
        }

        if (list2 == null) {
            for (Format f : list1) {
                commonFormats.add(f);
            }
            return true;
        }
        
        for (Format f1 : list1 ) {
            for (Format f2 : list2) {
                if (f1.matches(f2)) {
                    commonFormats.add(f1);
                }
            }
        }
        return !commonFormats.isEmpty();
    }
    
}
