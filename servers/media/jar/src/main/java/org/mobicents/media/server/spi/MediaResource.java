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

package org.mobicents.media.server.spi;

import java.util.Properties;
import org.mobicents.media.server.impl.common.MediaResourceState;
/**
 * Respresents the media resource which may either media generator or 
 * media detector.
 * 
 * @author Oleg Kulikov
 */
public interface MediaResource {

    
    /**
     * Provides configuration of the media resource.
     * 
     * @param config parameters of the media resource.
     */
    public void configure(Properties config);
    
    /**
     * Releases this media resources
     */
    public void release();
    
    /**
     * Gets the current state of the media resource.
     * 
     * @return the integer which identifies the current state.
     */
    public MediaResourceState getState();
    
    /**
     * Registers listener for the resource.
     * 
     * @param listener the listener instance.
     */
    public void addListener(NotificationListener listener);
    
    /**
     * Unregister listener for the resource.
     * 
     * @param listener the listener instance.
     */
    public void removeListener(NotificationListener listener);

    /**
     * Registers listener for the resource.
     * 
     * @param listener the listener instance.
     */
    public void addStateListener(ResourceStateListener listener);
    
    /**
     * Unregister listener for the resource.
     * 
     * @param listener the listener instance.
     */
    public void removeStateListener(ResourceStateListener listener);
    
    /**
     * Starts media processing.
     */
    public void start();
    
    /**
     * Terminate media procesing.
     */
    public void stop();
    
    
}
