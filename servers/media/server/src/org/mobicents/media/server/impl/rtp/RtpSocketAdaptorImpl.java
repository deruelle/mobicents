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
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import javax.media.Format;
import javax.media.format.AudioFormat;
import javax.media.format.UnsupportedFormatException;
import javax.media.protocol.PushBufferStream;

/**
 *
 * @author Oleg Kulikov
 */
public class RtpSocketAdaptorImpl implements RtpSocketAdaptor, Runnable {

    private ArrayList<AdaptorListener> listeners = new ArrayList();
    private DatagramSocket socket;
    private boolean stopped = false;
    private HashMap receiveStreams = new HashMap();
    //handler for receiver thread
    private Thread receiverThread;
    //registered participants
    private Peer peer = null;
    //holder for dynamic payloads.
    private HashMap formats = new HashMap();
    private InetAddress localhost;
    private int period = 20;
    private int jitter = 60;

    /**
     * Creates a new instance of RtpSocketAdaptorImpl
     */
    public RtpSocketAdaptorImpl() {
    }

    /**
     * Creates a new instance of RtpSocketAdaptorImpl
     */
    public RtpSocketAdaptorImpl(int period, int jitter) {
        this.period = period;
        this.jitter = jitter;
    }

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.impl.rtp.RtpSocketAdaptor#init(InetAddress, int);
     */
    public int init(InetAddress localAddress, int lowPort, int highPort) throws SocketException {
        boolean bound = false;
        int port = lowPort;
        while (!bound) {
            try {
                InetSocketAddress bindAddress = new InetSocketAddress(localAddress, port);
                socket = new DatagramSocket(bindAddress);
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
        
        receiverThread = new Thread(this);
        receiverThread.setPriority(Thread.MAX_PRIORITY);
        
        return port;
    }

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.impl.rtp.RtpSocketAdaptor#start();
     */
    public void start() {
        stopped = false;
        receiverThread.start();
    }

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.impl.rtp.RtpSocketAdaptor#stop();
     */
    public void stop() {
        stopped = true;
        receiverThread.interrupt();
    }

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.impl.rtp.RtpSocketAdaptor#close();
     */
    public void close() {
        //terminate main receive loop
        stopped = true;
        socket.close();

        if (receiverThread.isAlive()) {
            receiverThread.interrupt();
        }

        //terminate all receive streams
        Collection<ReceiveStream> streams = receiveStreams.values();
        for (ReceiveStream stream : streams) {
            stream.disable();
        }
        streams.clear();
    }

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.impl.rtp.RtpSocketAdaptor#setPeer(InetAddress, int);
     */
    public void setPeer(InetAddress address, int port) {
        this.peer = new Peer(address, port);
    }

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.impl.rtp.RtpSocketAdaptor#addPeer(int, Format);
     */
    public void addFormat(int pt, Format format) {
        formats.put(new Integer(pt), format);
    }

    /**
     * Determines payload type for specified format.
     *
     * @param format the object representing format.
     * @return the integer identifier of the payload or -1 if specified format
     * was not previously registered with addFormat().
     */
    private int getPayloadType(Format format) {
        Set<Integer> keys = formats.keySet();
        for (Integer k : keys) {
            Format fmt = (Format) formats.get(k);
            if (fmt.matches(format)) {
                return k.intValue();
            }
        }
        return -1;
    }

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.impl.rtp.RtpSocketAdaptor#startSendStream(PushBufferDataSource);
     */
    public SendStream createSendStream(PushBufferStream stream) throws UnsupportedFormatException {
        if (peer == null) {
            throw new IllegalStateException("Peer is not present yet");
        }
        return new SendStreamImpl(stream);
    }

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.impl.rtp.RtpSocketAdaptor#addAdaptorListener(AdaptorListener);
     */
    public void addAdaptorListener(AdaptorListener listener) {
        listeners.add(listener);
    }

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.impl.rtp.RtpSocketAdaptor#removeAdaptorListener(AdaptorListener);
     */
    public void removeAdaptorListener(AdaptorListener listener) {
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
                ((peer.address.equals(address) && port == peer.port) ||
                address.equals(localhost));
    }

    /**
     * Gets the receive stream for specified payload type.
     * if this is a new stream, the new instsance of the ReceiveStream object
     * will be created and registed.
     *
     * @param pt the payload type.
     * @return ReceiveStream instance.
     */
    public ReceiveStream getReceiveStream(int pt) throws IllegalArgumentException {
        Format fmt = (Format) formats.get(new Integer(pt));
        if (fmt == null) {
            throw new IllegalArgumentException("Unknown payload");
        }

        ReceiveStream receiveStream = (ReceiveStream) receiveStreams.get(new Integer(pt));
        if (receiveStream == null) {
            receiveStream = new ReceiveStream(fmt, period, jitter);
            receiveStreams.put(new Integer(pt), receiveStream);

            //notify listeners
            for (AdaptorListener listener : listeners) {
                listener.newReceiveStream(receiveStream);
            }
        }

        return receiveStream;
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
            } catch (SocketTimeoutException e) {
                if (stopped) {
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
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
                //corrupted packet, ignore this one
                continue;
            }

            int pt = rtpPacket.getPayloadType();
            try {
                ReceiveStream stream = this.getReceiveStream(pt);
                stream.push(rtpPacket.getPayload());

                Thread.currentThread().yield();
            } catch (IllegalArgumentException e) {
                continue;
            }
        }
    }

    /**
     * Send specified datagram packet.
     *
     * @param packet the datagram packet object to sent out.
     */
    private synchronized void sendPacket(DatagramPacket packet) throws IOException {
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
        for (AdaptorListener listener : listeners) {
            listener.error(e);
        }
    }

    /**
     * Represents a participant.
     */
    private class Peer implements Serializable {

        private InetAddress address;
        private int port;

        /**
         * Creates new instance of the peer.
         *
         * @param address the address of the participant.
         * @param port the address of the participant.
         */
        public Peer(InetAddress address, int port) {
            this.address = address;
            this.port = port;
        }

        public synchronized void send(RtpPacket packet) throws IOException {
            byte[] data = packet.toByteArray();
            DatagramPacket dp = new DatagramPacket(data, data.length, address, port);
            sendPacket(dp);
        }
    }

    private class SendStreamImpl extends Thread implements SendStream {

        private boolean stopped = false;
        //payload type
        private int pt = 0;
        //sequence number
        private int seq = 0;
        //timestamp
        private int timestamp = 0;
        //source synchronization
        private final long ssrc = System.currentTimeMillis();
        //packetizer
        private Packetizer packetizer;
        //the amount of ticks in one milliseconds
        int ticks;

        public SendStreamImpl(PushBufferStream stream) throws UnsupportedFormatException {
            super("RTPSendStream");
            this.setPriority(Thread.MAX_PRIORITY);
            init(stream);
        }

        /**
         * (Non Java-doc).
         *
         * @see org.mobicents.media.server.impl.rtp.SendStream#setStream(PushBufferStream).
         */
        public void setStream(PushBufferStream stream) throws UnsupportedFormatException {
            init(stream);
        }

        /**
         * (Non Java-doc).
         *
         * @see org.mobicents.media.server.impl.rtp.SendStream#setStream(PushBufferStream).
         */
        private void init(PushBufferStream stream) throws UnsupportedFormatException {
            AudioFormat fmt = (AudioFormat) stream.getFormat();

            pt = getPayloadType(fmt);
            if (pt == -1) {
                throw new UnsupportedFormatException(fmt);
            }

            ticks = (int) fmt.getSampleRate() / 1000;
            packetizer = new Packetizer(16000, ticks * period);
            stream.setTransferHandler(packetizer);

        }

        /**
         * (Non Java-doc).
         *
         * @see org.mobicents.media.server.impl.rtp.SendStream#close().
         */
        public void close() {
            stopped = true;
            this.interrupt();
        }

        /**
         * Main loop.
         *
         * Reads data from packetizer and sends to all registered participants.
         * Silence is not transmitted, however the timestamp and sequence number
         * are still incremented monotonously during silence periods.
         */
        public void run() {
            this.stopped = false;
            int s = ticks * period;
            while (!stopped) {
                try {
                    byte[] data = packetizer.next(s);
                    RtpPacket p = new RtpPacket((byte) pt, seq++, timestamp, ssrc, data);
                    peer.send(p);
                    Thread.currentThread().sleep(10);
                    timestamp += s;
                } catch (InterruptedException e) {
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
