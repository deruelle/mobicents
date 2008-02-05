/*
 * BaseConnection.java
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
package org.mobicents.media.server.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import javax.media.Format;
import javax.media.format.AudioFormat;
import javax.media.format.UnsupportedFormatException;
import javax.media.protocol.PushBufferStream;
import javax.sdp.MediaDescription;
import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SessionDescription;

import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.rtp.RTPAudioFormat;
import org.mobicents.media.server.impl.rtp.RTPFormat;
import org.mobicents.media.server.impl.rtp.AdaptorListener;
import org.mobicents.media.server.impl.rtp.RtpSocketAdaptor;
import org.mobicents.media.server.impl.rtp.RtpSocketAdaptorImpl;
import org.mobicents.media.server.impl.rtp.SendStream;
import org.mobicents.media.server.spi.*;
import org.mobicents.media.server.spi.dtmf.DtmfDetector;

/**
 *
 * @author Oleg Kulikov
 */
public class BaseConnection implements Connection, AdaptorListener {

    private final static Random rnd = new Random();
    
    private String id;
    private int port;
    private int period = 20;
    private BaseEndpoint endpoint;
    private String endpointName;
    private int mode;
    private ArrayList<ConnectionListener> listeners = new ArrayList();
    private SessionDescription localSDP;
    private SessionDescription remoteSDP;
    private BaseConnection otherConnection;
    private RtpSocketAdaptor rtpSocket;
    
    private AudioFormat audioFormat = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1);
    private int dtmfFormat = DtmfDetector.RFC2833;
    
    private SdpFactory sdpFactory = SdpFactory.getInstance();
    private HashMap codecs;
    
    private static HashMap defaultCodecs = new HashMap();
    static {
        defaultCodecs.put(new Integer(0), new RTPAudioFormat(0, AudioFormat.ULAW, 8000, 8, 1));
        defaultCodecs.put(new Integer(8), new RTPAudioFormat(8, AudioFormat.ALAW, 8000, 8, 1));
//        defaultCodecs.put(new Integer(3), new RTPAudioFormat(3, AudioFormat.GSM, 8000, 13, 1));
//        defaultCodecs.put(new Integer(4), new RTPAudioFormat(4, AudioFormat.G723, 8000, 8, 1));
//        defaultCodecs.put(new Integer(5), new RTPAudioFormat(5, AudioFormat.DVI, 8000, 4, 1));
//        defaultCodecs.put(new Integer(16), new RTPAudioFormat(16, AudioFormat.DVI, 11025, 4, 1));
//        defaultCodecs.put(new Integer(16), new RTPAudioFormat(16, AudioFormat.DVI, 22050, 4, 1));
        defaultCodecs.put(new Integer(101), new RTPAudioFormat(101, "telephone-event"));
    }
    private transient Logger logger = Logger.getLogger(BaseConnection.class);

    /**
     * Creates a new instance of BaseConnection.
     *
     * @param endpoint the endpoint executing this connection.
     * @param mode the mode of this connection.
     */
    public BaseConnection(Endpoint endpoint, int mode) throws ResourceUnavailableException {
        this.id = genID();
        this.mode = mode;

        this.endpoint = (BaseEndpoint) endpoint;
        this.endpointName = endpoint.getLocalName();

        rtpSocket = new RtpSocketAdaptorImpl(
                this.endpoint.packetizationPeriod, 
                this.endpoint.jitter);
        try {
            port = rtpSocket.init(endpoint.getBindAddress(),
                    this.endpoint.lowPortNumber,
                    this.endpoint.highPortNumber);
            rtpSocket.addAdaptorListener(this);

            if (logger.isDebugEnabled()) {
                logger.debug(this + " Bound RTP socket to " +
                        endpoint.getBindAddress() + ":" + port);
            }

            rtpSocket.start();
        } catch (SocketException e) {
            logger.error(this + "Fail while binding RTP socket", e);
            throw new ResourceUnavailableException(e.getMessage());
        }
    }

    /**
     * Generates unique identifier for this connection.
     *
     * @return hex view of the unique integer.
     */
    private String genID() {
        return Long.toHexString(System.currentTimeMillis() & rnd.nextInt()).toUpperCase();
    }

    /**
     * (Non-Javadoc).
     *
     * @see org.mobicents.media.server.spi.Connection#getId();
     */
    public String getId() {
        return id;
    }

    /**
     * (Non-Javadoc).
     *
     * @see org.mobicents.media.server.spi.Connection#getMode();
     */
    public int getMode() {
        return mode;
    }

    /**
     * (Non-Javadoc).
     *
     * @see org.mobicents.media.server.spi.Connection#setMode(int);
     */
    public void setMode(int mode) {
        this.mode = mode;
    //@todo rebuilt send/recv streams.
    }

    /**
     * (Non-Javadoc).
     *
     * @see org.mobicents.media.server.spi.Connection#getEndpoint(int);
     */
    public Endpoint getEndpoint() {
        return endpoint;
    }

    public int getDtmfFormat() {
        return this.dtmfFormat;
    }
    
    /**
     * (Non-Javadoc).
     *
     * @see org.mobicents.media.server.spi.Connection#getLocalDescriptor();
     */
    public String getLocalDescriptor() {
        String userName = "MediaServer";
        long sessionID = System.currentTimeMillis() & 0xffffff;
        long sessionVersion = sessionID;

        String networkType = javax.sdp.Connection.IN;
        String addressType = javax.sdp.Connection.IP4;
        String address = endpoint.getBindAddress().getHostAddress();

        try {
            localSDP = sdpFactory.createSessionDescription();
            localSDP.setVersion(sdpFactory.createVersion(0));
            localSDP.setOrigin(sdpFactory.createOrigin(userName,
                    sessionID,
                    sessionVersion,
                    networkType,
                    addressType,
                    address));
            localSDP.setSessionName(sdpFactory.createSessionName("session"));
            localSDP.setConnection(sdpFactory.createConnection(
                    networkType,
                    addressType,
                    address));

            Vector descriptions = new Vector();

            //encode formats
            HashMap fmts = codecs != null ? codecs : defaultCodecs;
            Object[] payloads = getPayloads(fmts).toArray();

            int[] formats = new int[payloads.length];
            for (int i = 0; i < formats.length; i++) {
                formats[i] = ((Integer) payloads[i]).intValue();
            }

            //generate media descriptor
            MediaDescription md = sdpFactory.createMediaDescription("audio",
                    port, 1, "RTP/AVP", formats);

            //set attributes for formats
            Vector attributes = new Vector();
            for (int i = 0; i < formats.length; i++) {
                Format format = (Format) fmts.get(new Integer(formats[i]));
                attributes.add(sdpFactory.createAttribute("rtpmap", format.toString()));
            }

            //set attributes for dtmf
            attributes.add(sdpFactory.createAttribute("fmtp", "101 0-15"));

            //generate descriptor
            md.setAttributes(attributes);
            descriptions.add(md);

            localSDP.setMediaDescriptions(descriptions);
        } catch (SdpException e) {
            logger.error("Could not create descriptor", e);
        }
        return localSDP.toString();
    }

    /**
     * (Non-Javadoc).
     *
     * @see org.mobicents.media.server.spi.Connection#getRemoteDescriptor();
     */
    public String getRemoteDescriptor() {
        return remoteSDP != null ? remoteSDP.toString() : null;
    }

    /**
     * Gets used audio format.
     *
     * @return format used by this connection.
     */
    public AudioFormat getAudioFormat() {
        return this.audioFormat;
    }

    /**
     * Extracts address and port of the remote party.
     *
     * @param sdp session description.
     * @return socket address of the remote party.
     */
    private InetSocketAddress getPeer(SessionDescription sdp) throws SdpException {
        javax.sdp.Connection connection = sdp.getConnection();

        Vector list = remoteSDP.getMediaDescriptions(false);
        MediaDescription md = (MediaDescription) list.get(0);

        try {
            InetAddress address = InetAddress.getByName(connection.getAddress());
            int localPort = md.getMedia().getMediaPort();
            return new InetSocketAddress(address, localPort);
        } catch (UnknownHostException e) {
            throw new SdpException(e);
        }
    }

    /**
     * (Non-Javadoc).
     *
     * @see org.mobicents.media.server.spi.Connection#setRemoteDescriptor();
     */
    public void setRemoteDescriptor(String descriptor) throws SdpException, IOException {
        remoteSDP = sdpFactory.createSessionDescription(descriptor);

        //add peer to RTP socket
        InetSocketAddress peer = getPeer(remoteSDP);
        rtpSocket.setPeer(peer.getAddress(), peer.getPort());

        if (logger.isDebugEnabled()) {
            logger.debug(this + " Set peer: " + peer);
        }

        //negotiate codecs
        HashMap offer = RTPFormat.getFormats(remoteSDP);
        codecs = select(defaultCodecs, offer);
        if (codecs.size() == 0) {
            throw new IOException("Codecs are not negotiated");
        }

        this.audioFormat = (AudioFormat) getDefaultAudioFormat(codecs);

        if (logger.isDebugEnabled()) {
            logger.debug(this + " Codecs are negotiated, default audio format : " +
                    this.audioFormat);
        }

        applyCodecs(rtpSocket, codecs);

        try {
            this.period = getPacketizationPeriod(remoteSDP);
        } catch (Exception e) {
        }

        //refresh output stream
        if (mode != Connection.MODE_RECV_ONLY) {
            new Thread(new Sender(this)).start();
        }
    }

    private int getPacketizationPeriod(SessionDescription sd) throws SdpException {
        MediaDescription md = (MediaDescription) sd.getMediaDescriptions(false).get(0);
        String value = md.getAttribute("ptime");
        return Integer.parseInt(value);

    }

    private Format getDefaultAudioFormat(HashMap codecs) {
        Collection payloads = this.getPayloads(codecs);
        Integer pt = (Integer) payloads.iterator().next();
        return (Format) codecs.get(pt);
    }

    public void addListener(ConnectionListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ConnectionListener listener) {
        listeners.remove(listener);
    }

    public Collection<ConnectionListener> getListeners() {
        return listeners;
    }

    /**
     * (Non-Javadoc).
     *
     * @see org.mobicents.media.server.spi.Connection#setRemoteDescriptor();
     */
    public void setOtherParty(Connection other) throws IOException {
        otherConnection = (BaseConnection) other;
        if (mode != Connection.MODE_RECV_ONLY) {
            new Thread(new Sender(this)).start();
        }
    }

    /**
     * Gets the collection of payload types.
     *
     * @param fmts the map with payload type as key and format as a value.
     * @return sorted collection of payload types.
     */
    private Collection getPayloads(HashMap fmts) {
        Object[] payloads = fmts.keySet().toArray();

        ArrayList list = new ArrayList();
        for (int i = 0; i < payloads.length; i++) {
            list.add(payloads[i]);
        }

        Collections.sort(list);
        return list;
    }

    /**
     * Selects codecs shared between supported and offered.
     *
     * @param supported the map with the supported codecs.
     * @param offered the list with the offered codecs.
     */
    private HashMap select(HashMap supported, HashMap offered) {
        HashMap formats = new HashMap();
        Set<Integer> offer = offered.keySet();
        for (Integer po : offer) {
            Format ofmt = (Format) offered.get(po);
            Set<Integer> local = supported.keySet();
            for (Integer pl : local) {
                Format sfmt = (Format) supported.get(pl);
                if (sfmt.matches(ofmt)) {
                    formats.put(po, sfmt);
                    this.audioFormat = (AudioFormat) sfmt;
                }
            }
        }
        return formats;
    }

    /**
     * Apply specified codecs for RTP manager.
     *
     * @param manager the RTP manager.
     * @param codecs the list of codecs.
     */
    private void applyCodecs(RtpSocketAdaptor rtpSocket, HashMap codecs) {
        Set<Integer> payloads = codecs.keySet();
        for (Integer p : payloads) {
            rtpSocket.addFormat(p, (Format) codecs.get(p));
        }
    }

    /**
     * Releases all resources requested by this connection.
     */
    public void close() {
        if (logger.isDebugEnabled()) {
            logger.debug(this + " Close RTP socket");
        }
        rtpSocket.close();
        this.endpoint = null;
    }

    /**
     * This method is called when new receive stream is initialized.
     *
     * @param stream the new receive stream.
     */
    public void newReceiveStream(PushBufferStream stream) {
        Format fmt = stream.getFormat();
        if (logger.isDebugEnabled()) {
            logger.debug(this + " New receive stream: " + fmt);
        }

        if (this.mode == Connection.MODE_SEND_ONLY) {
            if (logger.isDebugEnabled()) {
                logger.debug(this + " Mode is SEND_ONLY, " +
                        "media transmission is not allowed");
            }
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug(this + " Strat transmission to the endpoint");
        }
        
        if (endpoint != null) {
            endpoint.addAudioStream(stream, this.getId());
        }

    }

    /**
     * Gets the text representation of the connection.
     *
     * @return text representation of the connection.
     */
    @Override
    public String toString() {
        return "(connectionID=" + id + ", endpoint=" + endpointName + ")";
    }

    public void error(Exception e) {
        logger.error("Facility error", e);
    }

    private class Sender implements Runnable {

        private BaseConnection connection;

        public Sender(BaseConnection connection) {
            this.connection = connection;
        }

        public void run() {
            Collection<PushBufferStream> streams = endpoint.getAudioStreams(connection);

            //local connection
            if (otherConnection != null) {
                for (PushBufferStream stream : streams) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Start transmission  " + stream + 
                                " to local connection " + otherConnection);
                    }
                    otherConnection.newReceiveStream(stream);
                }
                return;
            }

            for (PushBufferStream stream : streams) {
                try {
                    SendStream sendStream = rtpSocket.createSendStream(stream);
                    sendStream.start();
                    if (logger.isDebugEnabled()) {
                        logger.debug("Start transmission  " + stream + 
                                " to remote peer:" + getPeer(remoteSDP));
                    }
                } catch (UnsupportedFormatException e) {
                } catch (SdpException e) {
                }
            }
        }
    }
}

