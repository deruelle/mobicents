/*
 * MediaConnection.java
 *
 * The Simple Media API RA
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

package org.mobicents.slee.resource.media.ratype;

import java.io.Serializable;
import javax.media.protocol.DataSource;

/**
 *
 * @author Oleg Kulikov
 */
public interface MediaConnection extends Serializable {
    public final static int RTP = 10;
   
    public final static int STATE_NULL = 0;
    public final static int STATE_CONNECTING = 1;
    public final static int STATE_CONNECTED = 2;
    public final static int STATE_DISCONNECTED = 3;
    public final static int STATE_FAILED = 4;
    
    /**
     * Unique identifier of the connection.
     *
     * @return unique identifier of this connection.
     */
    public String getId();
    
    /**
     * Gets the current state of the connection.
     *
     * @return the integer constant indicating the state.
     */
    public int getState();
    
    /**
     * Initialize this connection.
     * Executes asynchronously.
     *
     * @param context the topology of the context in which this connection should 
     * be created. The topology may be IVR, Announcement, Conference and so on.
     */
    public void init(int mediaContext);
    
    public void init(MediaContext mediaContext);
    
    /**
     * Gets the media context to which this connection is attached.
     *
     * @return MediaContext object or null if this connection is not attached.
     */
    public MediaContext getMediaContext();
    
    /**
     * Release connection and free all resources.
     */
    public void release();
        
}
