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
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import net.java.stun4j.StunAddress;
import net.java.stun4j.client.NetworkConfigurationDiscoveryProcess;
import net.java.stun4j.client.StunDiscoveryReport;

import org.apache.log4j.Logger;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.local.management.WorkDataGatherer;

/**
 *
 * @author Oleg Kulikov
 */
public class RtpSocketImpl implements RtpSocket, Runnable  {

	private transient Logger logger = Logger.getLogger(RtpSocketImpl.class);
	
    private ArrayList<RtpSocketListener> listeners = new ArrayList<RtpSocketListener>();
    private DatagramSocket socket;
    private int port;
    private boolean stopped = false;
    //registered participants
    protected Peer peer = null;
    //holder for dynamic payloads.
    private HashMap <Integer, Format> rtpMap = new HashMap<Integer, Format>();
    private InetAddress localhost;
    protected int period = 20;
    private int jitter = 60;
    //STUN variables
    private String publicAddressFromStun = null;
    private int publicPortFromStun;
    private boolean useStun = false;
    private String stunServerAddress;
    private int stunServerPort;
    private boolean usePortMapping = true;
  //handler for receiver thread
    private transient Thread receiverThread;
    private ReceiveStream receiveStream;
    private SendStreamImpl sendStream;
    public static final AtomicInteger rtpSocketThreadNumber = new AtomicInteger(1);
    public static final String rtpSocketThreadNamePrefix = "RtpSocketImpl-";
    

    /**
     * Creates a new instance of RtpSocketImpl
     */
    public RtpSocketImpl() {
    }

    /**
     * Creates a new instance of RtpSocketImpl
     */
    public RtpSocketImpl(int period, int jitter) {
        this.period = period;
        this.jitter = jitter;
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
                        if(mapStun(port, bindAddress.getHostName())) bound = true;
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

        try {
            localhost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
        }

        receiverThread = new Thread(this, rtpSocketThreadNamePrefix + rtpSocketThreadNumber.getAndIncrement());
        receiverThread.setPriority(Thread.MAX_PRIORITY);

        return port;
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
     * @see org.mobicents.media.server.impl.rtp.RtpSocket#setPeriod();
     */
    public void setPeriod(int period) {
        this.period = period;
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
                logger.warn("The Ip address provided is the loopback address, stun won't be enabled for it");
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
                    logger.error("Stun discovery failed to find a valid public ip address, disabling stun !");
                }
                logger.info("Stun report = " + report);
                addressDiscovery.shutDown();
            }
        } catch (Throwable t) {
            logger.error("Stun lookup has failed: " + t.getMessage());
            return false;
        }
        return true;

    }

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.impl.rtp.RtpSocket#start();
     */
    public void start() {
        stopped = false;
        receiverThread.start();

        receiveStream = new ReceiveStream(this, period, jitter);
        
        sendStream = new SendStreamImpl(this);
        sendStream.formats = new Format[rtpMap.size()];
        rtpMap.values().toArray(sendStream.formats);
    }

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.impl.rtp.RtpSocket#stop();
     */
    public void stop() {
        stopped = true;
        receiverThread.interrupt();
    }

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.impl.rtp.RtpSocket#close();
     */
    public void close() {
        stopped = true;
        if (socket != null) {
            socket.disconnect();
            socket.close();
        }
        if (receiveStream != null) {
            receiveStream.stop();
        }
    }

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.impl.rtp.RtpSocket#setPeer(InetAddress, int);
     */
    public void setPeer(InetAddress address, int port) {
        this.peer = new Peer(this, address, port);
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
    
    /**
     * Determines payload type for specified format.
     *
     * @param format the object representing format.
     * @return the integer identifier of the payload or -1 if specified format
     * was not previously registered with addFormat().
     */
    protected int getPayloadType(Format format) {
        Set<Integer> keys = rtpMap.keySet();
        for (Integer k : keys) {
            Format fmt = (Format) rtpMap.get(k);
            if (fmt.matches(format)) {
                return k.intValue();
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

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.impl.rtp.RtpSocket#addAdaptorListener(RtpSocketListener);
     */
    public void addAdaptorListener(RtpSocketListener listener) {
        listeners.add(listener);
    }

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.impl.rtp.RtpSocket#removeAdaptorListener(RtpSocketListener);
     */
    public void removeAdaptorListener(RtpSocketListener listener) {
        listeners.remove(listener);
    }

    /**
     * Reads actualy received data.
     *
     * @param packet the datagram packet object to read data from.
     * @return received data as array of bytes.
     */
    private byte[] readData(DatagramPacket packet) {
        byte[] data = new byte[packet.getLength()];
        System.arraycopy(packet.getData(), 0, data, 0, data.length);
        return data;
    }

    /**
     * Verify that this socket is authorized to receive data from the peer.
     *
     * @param address the address of the peer.
     * @port the port number of the peer.
     * @return true if this socket is authorized to receive data from peer
     * with specified address & port or false otherwise.
     */
    private boolean checkPeer(InetAddress address, int port) {
        return peer != null &&
                ((peer.getInetAddress().equals(address) && port == peer.getPort()) ||
                address.equals(localhost));
    }

    /**
     * The main reader loop.
     *
     * Reads incoming data from socket and distribute data between peers.
     */
    @SuppressWarnings("static-access")
    public void run() {
        while (!stopped) {
            byte[] buff = new byte[200];

            DatagramPacket udpPacket = new DatagramPacket(buff, buff.length);
            try {
                socket.receive(udpPacket);
                if (stopped) {
                    continue;
                }
            } catch (SocketTimeoutException e) {
                continue;
            } catch (IOException e) {
                this.sendError(e);
                continue;
            }

            //if peer is not present we do not authorize adaptor to
            //receive data
//            boolean authorized = checkPeer(udpPacket.getAddress(), udpPacket.getPort());
//            if (!authorized) {
//                System.out.println("not authorized " + udpPacket.getAddress());
//                continue;
//            }

            RtpPacket rtpPacket = null;
            try {
                rtpPacket = new RtpPacket(readData(udpPacket));
            } catch (IOException e) {
                e.printStackTrace();
                //corrupted packet, ignore this one
                continue;
            }
            try {
                receiveStream.push(rtpPacket);
                Thread.currentThread().yield();
            } catch (IllegalArgumentException e) {
                continue;
            }
        }

        socket.close();
        receiveStream.stop();
        if (logger.isDebugEnabled()) {
            logger.debug("Gracefully close RTP socket");
        }
    }

    /**
     * Send specified datagram packet.
     *
     * @param packet the datagram packet object to sent out.
     */
    protected synchronized void sendPacket(DatagramPacket packet) throws IOException {
        if (!stopped) {
            socket.send(packet);
        }
    }

    /**
     * Notify listeners about error
     * 
     * @param e the exception occured.
     */
    private void sendError(Exception e) {
        for (RtpSocketListener listener : listeners) {
            listener.error(e);
        }
    }
    
    public void setWorkDataGatherer(WorkDataGatherer g)
    {
    	try{
    		if(this.sendStream!=null)
    		{
    			this.sendStream.setWorkDataSink(g);
    		}
    		
    		if(this.receiveStream!=null)
    		{
    			this.receiveStream.setWorkDataSink(g);
    		}
    		
    	}catch(NullPointerException npe)
    	{
    		npe.printStackTrace();
    	}
    }
    
}
