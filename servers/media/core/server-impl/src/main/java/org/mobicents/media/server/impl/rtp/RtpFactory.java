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

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import net.java.stun4j.StunAddress;
import net.java.stun4j.StunException;

import net.java.stun4j.client.NetworkConfigurationDiscoveryProcess;
import net.java.stun4j.client.StunDiscoveryReport;
import org.apache.log4j.Logger;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.format.VideoFormat;
import org.mobicents.media.server.impl.rtp.sdp.RTPAudioFormat;
import org.mobicents.media.server.impl.rtp.sdp.RTPFormat;
import org.mobicents.media.server.impl.rtp.sdp.RTPVideoFormat;
import org.mobicents.media.server.spi.Timer;

/**
 * 
 * @author Oleg Kulikov
 */
public class RtpFactory {
    
    private Transceiver transceiver;
    
    
    private Integer jitter = 60;
    
    private InetAddress bindAddress;
    private int localPort;
    
    protected InetSocketAddress publicAddress;

    private String stunHost;
    private int stunPort = 3478;
    
    private Timer timer;    
    
    private Map<Integer, Format> formatMap;    
    protected volatile HashMap<SocketAddress, RtpSocket> rtpSockets = new HashMap();
    
    private transient Logger logger = Logger.getLogger(RtpFactory.class);

    /**
     * Creates RTP Factory instance
     */
    public RtpFactory() {
    }

    public int getLocalPort() {
        return localPort;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    /**
     * Gets the address of stun server if present.
     * 
     * @return the address of stun server or null if not assigned.
     */
    public String getStunAddress() {
        return stunHost == null ? null : stunPort == 3478 ? stunHost : (stunHost + ":" + stunPort);
    }

    /**
     * Assigns address of the STUN server.
     * 
     * @param address
     *            the address of the stun server in format host[:port]. if port
     *            is not set then default port is used.
     */
    public void setStunAddress(String address) {
        String tokens[] = address.split(":");
        stunHost = tokens[0];
        if (tokens.length == 2) {
            stunPort = Integer.parseInt(tokens[1]);
        }
    }

    public void start() throws SocketException, IOException, StunException  {
        InetSocketAddress address = new InetSocketAddress(bindAddress, localPort);
        publicAddress = stunHost != null ? getPublicAddress(address) : address;

        logger.info("Binding RTP transceiver to " + bindAddress + ":" + localPort);
        
        transceiver = new Transceiver(rtpSockets, bindAddress, localPort);
        transceiver.start();
        
        logger.info("Bound RTP transceiver to " + bindAddress + ":" + localPort +
                ", NAT public address is " + publicAddress);
    }

    public void stop() {
        System.out.println("++++++ !!!!!! STOPED RTPFACTORY ");
        transceiver.stop();
    }

    /**
     * Gets media processing timer used by RTP socket.
     * 
     * @return timer object.
     */
    public Timer getTimer() {
        return timer;
    }

    /**
     * Assigns media processing timer.
     * 
     * @param timer
     *            tmer object.
     */
    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    /**
     * Gets the IP address to which trunk is bound. All endpoints of the trunk
     * use this address for RTP connection.
     * 
     * @return the IP address string to which this trunk is bound.
     */
    public String getBindAddress() {
        return bindAddress != null ? bindAddress.getHostAddress() : null;
    }

    /**
     * Modify the bind address. All endpoints of the trunk use this address for
     * RTP connection.
     * 
     * @param bindAddress
     *            IP address as string or host name.
     */
    public void setBindAddress(String bindAddress) throws UnknownHostException {
        this.bindAddress = InetAddress.getByName(bindAddress);
    }

    /**
     * Gets the size of the jitter buffer in milliseconds.
     * 
     * Jitter buffer is used at the receiving ends of a VoIP connection. A
     * jitter buffer stores received, time-jittered VoIP packets, that arrive
     * within its time window. It then plays stored packets out, in sequence,
     * and at a constant rate for subsequent decoding. A jitter buffer is
     * typically filled half-way before playing out packets to allow early, or
     * late, packet-arrival jitter compensation.
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
     * Jitter buffer is used at the receiving ends of a VoIP connection. A
     * jitter buffer stores received, time-jittered VoIP packets, that arrive
     * within its time window. It then plays stored packets out, in sequence,
     * and at a constant rate for subsequent decoding. A jitter buffer is
     * typically filled half-way before playing out packets to allow early, or
     * late, packet-arrival jitter compensation.
     * 
     * Choosing a large jitter buffer reduces packet dropping from jitter but
     * increases VoIP path delay
     * 
     * @param jitter
     *            the new buffer's size in milliseconds
     */
    public void setJitter(Integer jitter) {
        this.jitter = jitter;
    }

    /**
     * Constructs new RTP socket.
     * 
     * @return the RTPSocketInstance.
     * @throws StunException
     * @throws IOException
     * @throws SocketException
     * @throws StunException
     * @throws IOException
     */
    public RtpSocket getRTPSocket() throws SocketException, IOException, StunException {
        RtpSocket rtpSocket = new RtpSocket(transceiver, timer, formatMap, this);
        return rtpSocket;
    }

    public void releaseRTPSocket(RtpSocket rtpSocket) {
        rtpSockets.remove(rtpSocket.remoteAddress);
    }

    public Map<Integer, Format> getFormatMap() {
        return this.formatMap;
    }

    public void setFormatMap(Map<Integer, Format> originalFormatMap) {

        this.formatMap = new HashMap<Integer, Format>();

        // now we have to switch, cause we use something that extends format,
        // without mms will crash
        for (Integer payloadType : originalFormatMap.keySet()) {
            Format _f = originalFormatMap.get(payloadType);
            Format convertedFormat = convert(payloadType, _f);
            this.formatMap.put(payloadType, convertedFormat);

        }
    }

    // --- Helper methods.
    private Format convert(Integer payloadType, Format _f) {

        Format converted = null;
        if (_f instanceof AudioFormat) {
            AudioFormat af = (AudioFormat) _f;
            RTPAudioFormat f = new RTPAudioFormat(payloadType, af.getEncoding(), af.getSampleRate(), af.getSampleSizeInBits(), af.getChannels(), af.getEndian(), af.getSigned());
            converted = f;
        } else if (_f instanceof VideoFormat) {
            VideoFormat vf = (VideoFormat) _f;
            RTPVideoFormat f = new RTPVideoFormat(payloadType, vf.getEncoding(), vf.getMaxDataLength(), vf.getFrameRate());

            converted = f;
        } else if (_f instanceof RTPFormat) {
            converted = _f;
        } else {
            throw new IllegalArgumentException("Unknown media format: " + _f.getClass());
        }
        return converted;
    }

    private InetSocketAddress getPublicAddress(InetSocketAddress localAddress) throws StunException {
        StunAddress local = new StunAddress(localAddress.getAddress(), localAddress.getPort());
        StunAddress stun = new StunAddress(stunHost, stunPort);

        // discovery stun server
        NetworkConfigurationDiscoveryProcess addressDiscovery = new NetworkConfigurationDiscoveryProcess(local, stun);
        addressDiscovery.start();

        // determine public addrees
        StunDiscoveryReport report = addressDiscovery.determineAddress();
        return report.getPublicAddress().getSocketAddress();
    }
    
}
