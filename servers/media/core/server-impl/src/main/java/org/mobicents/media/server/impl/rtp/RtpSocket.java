/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package org.mobicents.media.server.impl.rtp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


import org.apache.log4j.Logger;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.spi.Timer;

/**
 * 
 * @author Oleg Kulikov
 */
public class RtpSocket {

    private int bufferSize = 8196;
    private byte[] senderBuffer = new byte[bufferSize];
    
    private int localPort;
    private String localAddress;
        
    protected InetSocketAddress remoteAddress = null;    // holder for dynamic payloads.
    
    private HashMap<Integer, Format> rtpMap = new HashMap<Integer, Format>();
    private HashMap<Integer, Format> rtpMapOriginal = new HashMap<Integer, Format>();    // packet jitter in milliseconds
    private int jitter = 60;    // stun support
    private ReceiveStream receiveStream;
    private SendStream sendStream;    // timer to synchronize from
    protected Timer timer;
    private RtpFactory rtpFactory = null;
    private RtpSocketListener listener;    // logger instance
    private Transceiver transceiver;
    private final static Logger logger = Logger.getLogger(RtpSocket.class);

    /**
     * Creates a new instance of RtpSocket
     * 
     * @param timer
     *            used to synchronize receiver stream.
     * @param rtpMap
     *            RTP payloads list.
     */
    public RtpSocket(Transceiver transceiver, Timer timer, Map<Integer, Format> rtpMap, RtpFactory rtpFactory) {
        this.transceiver = transceiver;
        this.timer = timer;
        this.jitter = rtpFactory.getJitter();

        rtpMapOriginal.putAll(rtpMap);
        this.rtpMap.putAll(rtpMap);

        sendStream = new SendStream(this);
        receiveStream = new ReceiveStream(this, jitter);

        this.rtpFactory = rtpFactory;
        this.localAddress = rtpFactory.publicAddress.getHostName();
        this.localPort = rtpFactory.getLocalPort();
    }

    /**
     * Gets address to which this socked is bound.
     * 
     * @return either local address to which this socket is bound or public
     *         address in case of NAT translation.
     */
    public String getLocalAddress() {
        return localAddress;
    }

    /**
     * Returns port number to which this socked is bound.
     * 
     * @return port number or -1 if socket not bound.
     */
    public int getLocalPort() {
        return localPort;
    }

    /**
     * Gets the jitter for time of packet arrival
     * 
     * @return the value of jitter in milliseconds.
     */
    public int getJitter() {
        return this.jitter;
    }

    /**
     * Assign new value of packet time arrival jitter.
     * 
     * @param jitter
     *            the value of jitter in milliseconds.
     */
    public void setJitter(int jitter) {
        this.jitter = jitter;
    }

    /**
     * Gets receiver stream.
     * 
     * @return receiver stream instance.
     */
    public MediaSource getReceiveStream() {
        return receiveStream;
    }

    public void release() {
        rtpFactory.releaseRTPSocket(this);
    }

    public RtpSocketListener getListener() {
        return listener;
    }

    public void setListener(RtpSocketListener listener) {
        this.listener = listener;
    }

    /**
     * Assigns remote end.
     * 
     * @param address
     *            the address of the remote party.
     * @param port
     *            the port number of the remote party.
     */
    public void setPeer(InetAddress address, int port) throws IOException {
        remoteAddress = new InetSocketAddress(address, port);
        rtpFactory.rtpSockets.put(remoteAddress, this);
    }

    /**
     * Gets relations between payload number and format used by this socket.
     * 
     * @return the map where keya are payload number and object stored under key
     *         is <code>Format</code> object
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

    public void resetRtpMap() {
        this.rtpMap.clear();
        this.rtpMap.putAll(rtpMapOriginal);
    }

    /**
     * Determines payload type for specified format.
     * 
     * @param format
     *            the object representing format.
     * @return the integer identifier of the payload or -1 if specified format
     *         was not previously registered with addFormat().
     */
    protected int getPayloadType(Format format) {
        Collection<Integer> keys = rtpMap.keySet();
        for (Integer key : keys) {
            Format fmt = rtpMap.get(key);
            if (fmt.matches(format)) {
                return key;
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
     * Sends media data to remote peer.
     * 
     * This method uses blocking sending to make sure that data is out in time.
     * 
     * @param buffer
     *            the buffer which contains media data
     * @throws java.io.IOException
     */
    public void send(Buffer buffer) throws IOException {
        try {
            RtpHeader h = (RtpHeader) buffer.getHeader();
            byte[] headerByte = h.toByteArray();

            int len = headerByte.length + buffer.getLength();

            senderBuffer = new byte[len];

            // combine RTP header and payload
            System.arraycopy(headerByte, 0, senderBuffer, 0, headerByte.length);
            System.arraycopy((byte[]) buffer.getData(), 0, senderBuffer, headerByte.length, buffer.getLength());

            // construct datagram packet and send synchronously it out
            // senderPacket.setData(senderBuffer, 0, len);
            transceiver.send(senderBuffer, len, remoteAddress);
        } finally {
            buffer.dispose();
        }
    }

    public void receive(RtpPacket rtpPacket) {
        receiveStream.push(rtpPacket);
    }

    protected void notify(Exception e) {
        if (listener != null) {
            listener.error(e);
        }
    }
}
