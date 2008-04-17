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

import java.util.ArrayList;
import java.util.List;

import org.mobicents.media.server.impl.common.MediaResourceState;
import org.mobicents.media.server.spi.MediaResource;
import org.mobicents.media.server.spi.ResourceStateListener;

/**
 * Basic representation of the Media resource.
 * 
 * @author Oleg Kulikov
 */
public abstract class BaseResource implements MediaResource {

    private MediaResourceState state=MediaResourceState.NULL;
    private List <ResourceStateListener> stateListeners = new ArrayList();
    
    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.server.spi.MediaResource#getState().
     */
    public MediaResourceState getState() {
        return state;
    }
    
    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.server.spi.MediaResource#setState(int).
     */
    public void setState(MediaResourceState state) {
        this.state = state;
        for (ResourceStateListener listener: stateListeners) {
            listener.onStateChange(this, state);
        }
    }
    
    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.server.spi.MediaResource#addStateListener(ResourceStateListener).
     */    
    public void addStateListener(ResourceStateListener listener) {
        stateListeners.add(listener);
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.server.spi.MediaResource#removeStateListener(ResourceStateListener).
     */    
    public void removeStateListener(ResourceStateListener listener) {
        stateListeners.remove(listener);
    }

}
