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
import org.mobicents.media.server.impl.common.*;
//import org.mobicents.media.server.impl.common.events.*;
import org.mobicents.media.server.spi.events.EventID;
import org.mobicents.media.server.spi.events.Options;
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
 */
public interface Endpoint extends Serializable {
   
    /**
     * Gets the local name attribute.
     *
     * @return the local name.
     */
    public String getLocalName();


    
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

    /**
     * Apples specified signal.
     *
     * @param signalID the signal identifier for playing.
     * @param connectionID the identifier of the connection if signal should be 
     * applied to connection.
     * @param listener the callback interface.
     * @param keepAlive true if keep signal active.
     */
    public void play(String signalID, Options options, String connectionID,
            NotificationListener listener) throws UnknownSignalException, FacilityException;

    public void play(String signalID, Options options,
            NotificationListener listener) throws UnknownSignalException, FacilityException;
    
    /**
     * Asks the to detect requested event and report. 
     *
     * Such events may include, for example, fax tones, continuity tones, or 
     * on-hook transition. 
     * 
     * @param eventID the identifier of the event.
     * @param the Call Agent callback interface currently controlling that endpoint.
     * @persistent true if event is always detected on the endpoint.
     */
    public void subscribe(String eventID, Options options, NotificationListener listener)
            throws UnknownSignalException, FacilityException;

    /**
     * Asks the endpoint to detect requested event on a specified connection and report. 
     *
     * Such events may include, for example, fax tones, continuity tones, or 
     * on-hook transition. 
     * 
     * @param eventID the identifier of the event.
     * @param connectionID the identifier of the connection.
     * @param the Call Agent callback interface currently controlling that endpoint.
     */
    public void subscribe(String eventID, Options options, String connectionID,
            NotificationListener listener)throws UnknownSignalException, FacilityException;
    
	
}
