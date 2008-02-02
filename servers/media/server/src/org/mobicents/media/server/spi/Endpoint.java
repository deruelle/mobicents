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
import java.net.InetAddress;
import java.util.Timer;

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

    public final static String RESOURCE_DTMF_DETECTOR = "org.mobicents.dtmf.detector";
    public final static String RESOURCE_DTMF_GENERATOR = "org.mobicents.dtmf.generator";
    
    /**
     * Timer instance
     */
    public final static Timer TIMER = new Timer();

    /**
     * Gets the local name attribute.
     *
     * @return the local name.
     */
    public String getLocalName();

    /**
     * Gets the IP address to which endpoint is bound.
     *
     * @return the IP address to which this endpoint is bound.
     */
    public InetAddress getBindAddress();

    /**
     * Modify the bind address.
     *
     * @param the IP address object.
     */
    public void setBindAddress(InetAddress bindAddress);

    /**
     * Gets packetization period.
     * 
     * The packetization period is the period over which encoded voice bits 
     * are collected for encapsulation in packets.
     * 
     * Higher period will reduce VoIP overhead allowing higher channel utilization
     * 
     * @return packetion period for media in milliseconds.
     */
    public Integer getPacketizationPeriod();

    /**
     * Modify packetization period.
     * 
     * The packetization period is the period over which encoded voice bits 
     * are collected for encapsulation in packets.
     * 
     * Higher period will reduce VoIP overhead allowing higher channel utilization
     * 
     * @param period the value of the packetization period in milliseconds.
     */
    public void setPacketizationPeriod(Integer period);

    /**
     * Gets the size of the jitter buffer in milliseconds.
     * 
     * Jitter buffer is used at the receiving ends of a VoIP connection. 
     * A jitter buffer stores received, time-jittered VoIP packets, that arrive 
     * within its time window. It then plays stored packets out, in sequence, 
     * and at a constant rate for subsequent decoding. A jitter buffer is
     * typically filled half-way before playing out packets to allow early, 
     * or late, packet-arrival jitter compensation.
     * 
     * Choosing a large jitter buffer reduces packet dropping from jitter but
     * increases VoIP path delay
     * 
     * @return the size of the buffer in milliseconds.
     */
    public Integer getJitter();

    /**
     * Modify size of the jitter buffer.
     * 
     * Jitter buffer is used at the receiving ends of a VoIP connection. 
     * A jitter buffer stores received, time-jittered VoIP packets, that arrive 
     * within its time window. It then plays stored packets out, in sequence, 
     * and at a constant rate for subsequent decoding. A jitter buffer is
     * typically filled half-way before playing out packets to allow early, 
     * or late, packet-arrival jitter compensation.
     * 
     * Choosing a large jitter buffer reduces packet dropping from jitter but
     * increases VoIP path delay
     * 
     * @param jitter the new buffer's size in milliseconds
     */
    public void setJitter(Integer jitter);

    /**
     * Gets the range of the available port used for RTP connections.
     * 
     * @return the range of available port in the follwing format: low-high
     */
    public String getPortRange();

    /**
     * Sets the range of the available port used for RTP connections.
     * 
     * @param portRange string indicating range in the follwing format: low-high
     */
    public void setPortRange(String portRange);

    /**
     * Creates new connection with specified mode.
     *
     * @param mode the constant which identifies mode of the connection to be created.
     */
    public Connection createConnection(int mode)
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
    public void play(int signalID, String[] params, String connectionID,
            NotificationListener listener, boolean keepAlive) throws UnknownSignalException;

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
    public void subscribe(int eventID, NotificationListener listener,
            boolean persistent);

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
    public void subscribe(int eventID, String connectionID,String params[], 
            NotificationListener listener);
}
