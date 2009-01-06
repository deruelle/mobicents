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
package org.mobicents.media.server.impl.rtp;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.rtp.sdp.RTPAudioFormat;

/**
 *
 * @author Oleg Kulikov
 */
public class RtpFactory implements Serializable {

    private Integer period;
    private Integer jitter;
    
    private InetAddress bindAddress;
    private int lowPortNumber;
    private int highPortNumber;
    
    private String stunServerAddress;
    private int stunServerPort;
    private boolean useStun = false;
    private boolean usePortMapping = true;
    private String publicAddressFromStun = null;
    
    private HashMap<Integer, Format> audioFormats;
    
    private transient Logger logger = Logger.getLogger(RtpFactory.class);

    public RtpFactory() {
        try {
            bindAddress = InetAddress.getLocalHost();
        } catch (Exception e) {
            logger.error("Failed to get the IP address for host", e);
        }
        lowPortNumber = 1024;
        highPortNumber = 65535;

        period = 20;
        jitter = 80;

        audioFormats = new HashMap<Integer, Format>();
        audioFormats.put(0, new RTPAudioFormat(0, AudioFormat.ULAW, 8000, 8, 1));
        audioFormats.put(8, new RTPAudioFormat(8, AudioFormat.ALAW, 8000, 8, 1));
    }

    /**
     * Gets the IP address to which trunk is bound.
     * All endpoints of the trunk use this address for RTP connection.
     *
     * @return the IP address string to which this trunk is bound.
     */
    public String getBindAddress() {
        return bindAddress != null ? bindAddress.getHostAddress() : null;
    }

    /**
     * Modify the bind address.
     * All endpoints of the trunk use this address for RTP connection.
     *
     * @param the IP address string.
     */
    public void setBindAddress(String bindAddress) throws UnknownHostException {
        this.bindAddress = InetAddress.getByName(bindAddress);
    }

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
    public Integer getJitter() {
        return jitter;
    }

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
    public void setJitter(Integer jitter) {
        this.jitter = jitter;
    }

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
    public Integer getPacketizationPeriod() {
        return period;
    }

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
    public void setPacketizationPeriod(Integer period) {
        this.period = period;
    }

    /**
     * Gets the available port range.
     *
     * @return the string in format "lowPort-highPort".
     */
    public String getPortRange() {
        return lowPortNumber + "-" + highPortNumber;
    }

    /**
     * Modify port used to create RTP stream. 
     *
     * @param portRange the string in format "lowPort-highPort"
     */
    public void setPortRange(String portRange) {
        String[] tokens = portRange.split("-");
        lowPortNumber = Integer.parseInt(tokens[0]);
        highPortNumber = Integer.parseInt(tokens[1]);
    }

    /**
     * Gets the STUN server address.
     * 
     * @return the address of the server as string.
     */
    public String getStunServerAddress() {
        return stunServerAddress;
    }

    /**
     * Modify STUN server address.
     * 
     * @param stunServerAddress the valid address of the server
     */
    public void setStunServerAddress(String stunServerAddress) {
        this.stunServerAddress = stunServerAddress;
    }

    /**
     * Gets the port of the STUN server.
     * 
     * @return the port number.
     */
    public int getStunServerPort() {
        return stunServerPort;
    }

    /**
     * Configures port of the STUN server.
     * 
     * @param stunServerPort port number.
     */
    public void setStunServerPort(int stunServerPort) {
        this.stunServerPort = stunServerPort;
    }

    /**
     * Shows does STUN enabled.
     * 
     * @return true if STUN is used.
     */
    public boolean isUseStun() {
        return useStun;
    }

    /**
     * Enables/disables STUN usage.
     * 
     * @param useStun true to use STUN or false otherwise.
     */
    public void setUseStun(boolean useStun) {
        this.useStun = useStun;
    }

    /**
     * Shows does NAT uses port mapping.
     * 
     * @return true if NAT uses port mapping.
     */
    public boolean isUsePortMapping() {
        return usePortMapping;
    }

    /**
     * Configures NAT traversal procedure.
     *  
     * @param usePortMapping true if NAT uses port mapping.
     */
    public void setUsePortMapping(boolean usePortMapping) {
        this.usePortMapping = usePortMapping;
    }

    public String getPublicAddressFromStun() {
        return publicAddressFromStun;
    }

    public void setPublicAddressFromStun(String publicAddressFromStun) {
        this.publicAddressFromStun = publicAddressFromStun;
    }

    /**
     * Gets the list of supported RTP audioFormats.
     * 
     * @return map where key is payload number and value is Format object.
     */
    public HashMap<Integer, Format> getAudioFormats() {
        return audioFormats;
    }

    /**
     * Sets a list of supported RTPFormats.
     * 
     * @param audioFormats a map where key is payload number and value is Format object.
     */
    public void setAudioFormats(HashMap<Integer, Format> formats) {
        this.audioFormats = formats;
    }

    /**
     * Constructs new RTP socket.
     * 
     * @return the RTPSocketInstance.
     * @throws java.net.SocketException
     */
    public RtpSocket getRTPSocket(BaseEndpoint endpoint) throws SocketException {
        RtpSocketImpl rtpSocket = null;
        if (this.isUseStun()) {
            rtpSocket = new RtpSocketImpl(period, jitter, stunServerAddress, stunServerPort,
                    usePortMapping, publicAddressFromStun);
        } else {
            rtpSocket = new RtpSocketImpl(endpoint, period, jitter, audioFormats);
        }

        rtpSocket.init(bindAddress, lowPortNumber, highPortNumber);
//        rtpSocket.getRtpMap().putAll(audioFormats);
        return rtpSocket;
    }
}
