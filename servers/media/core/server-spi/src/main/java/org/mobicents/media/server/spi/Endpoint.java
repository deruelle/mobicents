/*
 * Endpoint.java
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
package org.mobicents.media.server.spi;

import java.io.Serializable;

import org.mobicents.media.Component;
import org.mobicents.media.format.AudioFormat;

/**
 * The basic implementation of the endpoint.
 *
 * An Endpoint is a logical representation of a physical entity, such as an
 * analog phone or a channel in a trunk. Endpoints are sources or sinks of data
 * and can be physical or virtual. Physical endpoint creation requires hardware
 * installation while software is sufficient for creating a virtual Endpoint.
 * An interface on a gateway that terminates a trunk connected to a PSTN switch
 * is an example of a physical Endpoint. An audio source in an audio-content
 * server is an example of a virtual Endpoint.
 *
 * @author Oleg Kulikov.
 * @author amit.bhayani
 */
public interface Endpoint extends Serializable {

    public final static AudioFormat LINEAR_AUDIO = 
            new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1,AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
    public final static AudioFormat PCMA = new AudioFormat(AudioFormat.ALAW, 8000, 8, 1);
    public final static AudioFormat PCMU = new AudioFormat(AudioFormat.ULAW, 8000, 8, 1);
    public final static AudioFormat SPEEX = new AudioFormat(AudioFormat.SPEEX, 8000, 8, 1);
    public final static AudioFormat G729 = new AudioFormat(AudioFormat.G729, 8000, 8, 1);
    public final static AudioFormat GSM = new AudioFormat(AudioFormat.GSM, 8000, 8, 1);
    public final static AudioFormat DTMF = new AudioFormat("telephone-event/8000");
    
    /**
     * Gets the local name attribute.
     *
     * @return the local name.
     */
    public String getLocalName();

    public Timer getTimer();
    public void setTimer(Timer timer);
    
    /**
     * Starts endpoint.
     */
    public void start() throws ResourceUnavailableException ;
    
    /**
     * Terminates endpoint's execution.
     */
    public void stop();
    
    /**
     * Creates new connection with specified mode.
     *
     * @param mode the constant which identifies mode of the connection to be created.
     */
    public Connection createConnection(ConnectionMode mode)
            throws TooManyConnectionsException, ResourceUnavailableException;

    /**
     * Creates new connection with specified mode.
     *
     * @param mode the constant which identifies mode of the connection to be created.
     */
    public Connection createLocalConnection(ConnectionMode mode)
            throws TooManyConnectionsException, ResourceUnavailableException;

    /**
     * Deletes specified connection.
     *
     * @param connectionID the identifier of the connection to be deleted.
     */
    public void deleteConnection(String connectionID);

    /**
     * Deletes all connection associated with this Endpoint.
     */
    public void deleteAllConnections();

    /**
     * Indicates does this endpoint has connections.
     *
     * @return true if endpoint has connections.
     */
    public boolean hasConnections();
    
    public Connection getConnection(String connectionID);
    public Component getComponent(int resourceID);
    
    /**
     * Shows is this endpoint in use
     * 
     * @return true if this endpoint is in use.
     */
    public boolean isInUse();
    
    /**
     * Marks this endpoint as used/unsed.
     * 
     * @param inUse true if endpoint is in use.
     */
    public void setInUse(boolean inUse);
    
    /**
     * Register NotificationListener to listen for <code>MsNotifyEvent</code>
     * which are fired due to events detected by Endpoints like DTMF. Use above
     * execute methods to register for event passing appropriate
     * <code>RequestedEvent</code>
     * 
     * @param listener
     */
    public void addNotificationListener(NotificationListener listener);

    /**
     * Remove the NotificationListener
     * 
     * @param listener
     */
    public void removeNotificationListener(NotificationListener listener);

    /**
     * Register <code>ConnectionListener</code> to listen for changes in MsConnection state
     * handled by this Endpoint
     * 
     * @param listener
     */
    public void addConnectionListener(ConnectionListener listener);
    /**
     * Removes the ConnectionListener
     * 
     * @param listener
     */
    public void removeConnectionListener(ConnectionListener listener);

}
