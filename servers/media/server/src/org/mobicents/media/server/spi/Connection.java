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

/**
 *
 * @author Oleg Kulikov
 */
public interface Connection extends Serializable {
    public final static int MODE_RECV_ONLY = 1;
    public final static int MODE_SEND_ONLY = 2;
    public final static int MODE_SEND_RECV = 3;
        
   /**
     * Gets the identifier of this connection.
     *
     * @return hex view of the integer.
     */
    public String getId();
    
    /**
     * Gets the current mode of this connection.
     *
     * @return integer constant indicating mode.
     */
    public int getMode();
    
    /**
     * Modify mode of this connection.
     *
     * @param mode the new value of the mode.
     */
    public void setMode(int mode);
    
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
     */
    public void setRemoteDescriptor(String descriptor) throws SdpException, IOException;
    
    /**
     * Joins localy this and other connections.
     *
     * @param other the other connectio to join with.
     */
    public void setOtherParty(Connection other) throws IOException;
    
    public void addListener(ConnectionListener listener);
    public void removeListener(ConnectionListener listener);
    /**
     * Closes this connection.
     */
    public void close();
}
