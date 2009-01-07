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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Collection;
import java.util.HashMap;

import net.java.stun4j.StunAddress;
import net.java.stun4j.client.NetworkConfigurationDiscoveryProcess;
import net.java.stun4j.client.StunDiscoveryReport;

import org.mobicents.media.Format;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.rtp.sdp.RTPAudioFormat;
import org.mobicents.media.server.local.management.WorkDataGatherer;

/**
 *
 * @author Oleg Kulikov
 */
public class RtpSocketImpl implements RtpSocket {

//    private ArrayList<RtpSocketListener> listeners = new ArrayList<RtpSocketListener>();
    private transient DatagramSocket socket;
    private int port;
    protected Peer peer = null;
    //holder for dynamic payloads.
    private HashMap<Integer, Format> rtpMap = new HashMap<Integer, Format>();
    private HashMap<Integer, Format> rtpMapOriginal = new HashMap<Integer, Format>();;
    protected int period = 20;
    private int jitter = 60;
    
    private String localAddress;
    private String publicAddressFromStun = null;
    private int publicPortFromStun;
    private boolean useStun = false;
    private String stunServerAddress;
    private int stunServerPort;
    private boolean usePortMapping = true;
    //handler for receiver thread
    private ReceiveStream receiveStream;
    private SendStreamImpl sendStream;

    /**
     * Creates a new instance of RtpSocketImpl
     */
    public RtpSocketImpl() {
    }

    /**
     * Creates a new instance of RtpSocketImpl
     */
    public RtpSocketImpl(BaseEndpoint endpoint, int period, int jitter, HashMap<Integer, Format> rtpMap) {
        this.period = period;
        this.jitter = jitter;
        rtpMapOriginal.putAll(rtpMap);
        this.rtpMap.putAll(rtpMap);        
        sendStream = new SendStreamImpl(this);
        receiveStream = new ReceiveStream(endpoint.getReceiverThread(), this, period, jitter);
    }

    /**
     * Creates a new instance of RtpSocketImpl
     */
    public RtpSocketImpl(int period, int jitter, String stunServerAddress, int stunServerPort,
            boolean usePortMapping, String knownPublicAddressFromStun) {
        this.period = period;
        this.jitter = jitter;
        this.stunServerAddress = stunServerAddress;
        this.stunServerPort = stunServerPort;
        this.useStun = true; // If we are using this constructor we are stunning
        this.usePortMapping = usePortMapping;
        this.publicAddressFromStun = knownPublicAddressFromStun;
    }

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.impl.rtp.RtpSocket#init(InetAddress, int);
     */
    public int init(InetAddress localAddress, int lowPort, int highPort) throws SocketException {
        boolean bound = false;
        this.localAddress = localAddress.getHostAddress();
        port = lowPort;
        while (!bound) {
            try {
                InetSocketAddress bindAddress = new InetSocketAddress(localAddress, port);
                socket = new DatagramSocket(bindAddress);
                if (useStun) {
                    if (usePortMapping) {
                        socket.disconnect();
                        socket.close();
                        socket = null;
                        if (mapStun(port, bindAddress.getHostName())) {
                            bound = true;
                        }
                        socket = new DatagramSocket(bindAddress);
                    } else {
                        // publicAddressFromStun is already set
                        this.publicPortFromStun = port;
                        bound = true;
                    }
                }
                socket.setSoTimeout(100);
                bound = true;
            } catch (SocketException e) {
                port++;
                if (port > highPort) {
                    throw e;
                }
            }
        }

        return port;
    }

    public String getLocalAddress() {
        return localAddress;
    }
    
    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.server.impl.rtp.RtpSocket#getPort();
     */
    public int getPort() {
        return port;
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.server.impl.rtp.RtpSocket#getPeriod();
     */
    public int getPeriod() {
        return this.period;
    }
    
    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.server.impl.rtp.RtpSocket#setPeriod(int);
     */
    public void setPeriod(int period) {
        this.period = period;
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.server.impl.rtp.RtpSocket#getJitter();
     */
    public int getJitter() {
        return this.jitter;
    }
    
    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.server.impl.rtp.RtpSocket#setJitter(int);
     */
    public void setJitter(int jitter) {
        this.jitter = jitter;
    }
    
    public MediaSource getReceiveStream() {
        return receiveStream;
    }

    public boolean isUseStun() {
        return useStun;
    }

    public void setUseStun(boolean useStun) {
        this.useStun = useStun;
    }

    public String getPublicAddressFromStun() {
        return publicAddressFromStun;
    }

    public int getPublicPortFromStun() {
        return publicPortFromStun;
    }

    public boolean mapStun(int localPort, String localAddress) {
        try {
            if (InetAddress.getByName(localAddress).isLoopbackAddress()) {
//                logger.warn("The Ip address provided is the loopback address, stun won't be enabled for it");
                this.publicAddressFromStun = localAddress;
                this.publicPortFromStun = localPort;
            } else {
                StunAddress localStunAddress = new StunAddress(localAddress,
                        localPort);

                StunAddress serverStunAddress = new StunAddress(
                        stunServerAddress, stunServerPort);

                NetworkConfigurationDiscoveryProcess addressDiscovery = new NetworkConfigurationDiscoveryProcess(
                        localStunAddress, serverStunAddress);
                addressDiscovery.start();
                StunDiscoveryReport report = addressDiscovery.determineAddress();
                if (report.getPublicAddress() != null) {
                    this.publicAddressFromStun = report.getPublicAddress().getSocketAddress().getAddress().getHostAddress();
                    this.publicPortFromStun = report.getPublicAddress().getPort();
                //TODO set a timer to retry the binding and provide a callback to update the global ip address and port
                } else {
                    useStun = false;
//                    logger.error("Stun discovery failed to find a valid public ip address, disabling stun !");
                }
//                logger.info("Stun report = " + report);
                addressDiscovery.shutDown();
            }
        } catch (Throwable t) {
//            logger.error("Stun lookup has failed: " + t.getMessage());
            return false;
        }
        return true;

    }

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.impl.rtp.RtpSocket#close();
     */
    public void close() {
        if (receiveStream != null) {
            receiveStream.stop();
        }
        
        if (socket != null) {
            socket.disconnect();
            socket.close();
        }
    }

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.impl.rtp.RtpSocket#setPeer(InetAddress, int);
     */
    public void setPeer(InetAddress address, int port) {
        this.peer = new Peer(this, address, port);
        peer.start();
    }

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.impl.rtp.RtpSocket#addFormat(int, Format);
     */
    public void addFormat(int pt, Format format) {
        rtpMap.put(new Integer(pt), format);
    }

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.impl.rtp.RtpSocket#removeFormat(int)
     */
    public void removeFormat(int pt) {
        rtpMap.remove(pt);
    }

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.impl.rtp.RtpSocket#getFormats();
     */
    public HashMap<Integer, Format> getRtpMap() {
        return rtpMap;
    }

    public void setRtpMap(HashMap<Integer, Format> rtpMap) {
        this.rtpMap = rtpMap;
        if (sendStream != null) {
            sendStream.formats = new Format[rtpMap.size()];
            rtpMap.values().toArray(sendStream.formats);
        }
    }
    
    public void resetRtpMap(){
    	this.rtpMap.clear();
    	this.rtpMap.putAll(rtpMapOriginal);
    }

    /**
     * Determines payload type for specified format.
     *
     * @param format the object representing format.
     * @return the integer identifier of the payload or -1 if specified format
     * was not previously registered with addFormat().
     */
    protected int getPayloadType(Format format) {
        Collection<Format> fmts = rtpMap.values();
        for (Format fmt : fmts) {
            if (fmt.equals(format)) {
                return ((RTPAudioFormat) fmt).getPayload();
            }
        }
        return -1;
    }

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.impl.rtp.RtpSocket#startSendStream(PushBufferDataSource);
     */
    public SendStream getSendStream() {
        return sendStream;
    }


    protected void receivePacket(DatagramPacket p) throws IOException {
        socket.receive(p);
    }

    /**
     * Send specified datagram packet.
     *
     * @param packet the datagram packet object to sent out.
     */
    protected void sendPacket(DatagramPacket packet) throws IOException {
        socket.send(packet);
    }

    public void setWorkDataGatherer(WorkDataGatherer g) {
        try {
            if (this.sendStream != null) {
                this.sendStream.setWorkDataSink(g);
            }

            if (this.receiveStream != null) {
                this.receiveStream.setWorkDataSink(g);
            }

        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }
}
