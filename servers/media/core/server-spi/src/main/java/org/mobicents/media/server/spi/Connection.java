/*
 * Connection.java
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

import java.io.IOException;
import java.io.Serializable;
import javax.sdp.SdpException;
import org.mobicents.media.Component;

/**
 *
 * @author Oleg Kulikov
 */
public interface Connection extends Serializable {

    /**
     * Gets the identifier of this connection.
     *
     * @return hex view of the integer.
     */
    public String getId();
    
    /**
     * Returns state of this connection
     * @return
     */
    public ConnectionState getState();

    /**
     * Gets the time to live of the connection.
     * 
     * @return the time in seconds.
     */
    public int getLifeTime();
    
    /**
     * Modify life time of the connection.
     * 
     * @param lifeTime the time value in seconds.
     */
    public void setLifeTime(int lifeTime);
    
    /**
     * Gets the current mode of this connection.
     *
     * @return integer constant indicating mode.
     */
    public ConnectionMode getMode();

    /**
     * Modify mode of this connection.
     *
     * @param mode the new value of the mode.
     */
    public void setMode(ConnectionMode mode);

    /**
     * Gets the endpoint which executes this connection.
     *
     * @return the endpoint object.
     */
    public Endpoint getEndpoint();

    /**
     * Gets the local descriptor of the connection.
     * The SDP format is used to encode the parameters of the connection.
     *
     * @return SDP descriptor.
     */
    public String getLocalDescriptor();

    /**
     * Gets the descriptor of the remote party.
     * The SDP format is used to encode the parameters of the connection.
     *
     * @return SDP descriptor.
     */
    public String getRemoteDescriptor();

    /**
     * Modify the descriptor of the remote party.
     * The SDP format is used to encode the parameters of the connection.
     *
     * @param remoteDescriptor the SDP descriptor of the remote party.
     * @throws ResourceUnavailableException 
     */
    public void setRemoteDescriptor(String descriptor) throws SdpException, IOException, ResourceUnavailableException;

    /**
     * Joins localy this and other connections.
     *
     * @param other the other connectio to join with.
     * @throws InterruptedException 
     */
    public void setOtherParty(Connection other) throws IOException;

    /**
     * Adds connection state listener.
     * 
     * @param listener to be registered
     */
    public void addListener(ConnectionListener listener);
    public void addNotificationListener(NotificationListener listener);

    /**
     * Removes connection state listener.
     * 
     * @param listener to be unregistered
     */
    public void removeListener(ConnectionListener listener);
    public void removeNotificationListener(NotificationListener listener);

    public Component getComponent(int resourceType);
    public Component getComponent(String name);
}
