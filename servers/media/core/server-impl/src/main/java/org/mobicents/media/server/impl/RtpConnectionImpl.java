/*
 * RtpConnectionImpl.java
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
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import javax.sdp.MediaDescription;
import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SessionDescription;

import org.apache.log4j.Logger;
import org.mobicents.media.Component;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.rtp.RtpSocket;
import org.mobicents.media.server.impl.rtp.sdp.RTPAudioFormat;
import org.mobicents.media.server.impl.rtp.sdp.RTPFormatParser;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.ConnectionState;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.ResourceUnavailableException;

/**
 * 
 * @author Oleg Kulikov
 */
public class RtpConnectionImpl extends BaseConnection {
	
	//public static transient final Logger logger = Logger.getLogger(RtpConnectionImpl.class);  

    private String localAddress;
    private String localDescriptor;
    private String remoteDescriptor;
    private transient RtpSocket rtpSocket;
    private transient SdpFactory sdpFactory;

    /**
     * Creates a new instance of RtpConnectionImpl.
     * 
     * @param endpoint
     *            the endpoint executing this connection.
     * @param connectionID the identifier of the connection to be created
     * @param mode
     *            the mode of this connection.
     */
    public RtpConnectionImpl(Endpoint endpoint, ConnectionMode mode) throws ResourceUnavailableException {
        super(endpoint, mode);
        
        sdpFactory = this.endpoint.getSdpFactory();
        rtpSocket = this.endpoint.allocateRtpSocket(this);
        
        rtpSocket.resetRtpMap();
        
        if (this.endpoint.getPrimarySink(this) != null) {
            this.endpoint.getPrimarySink(this).connect(rtpSocket.getReceiveStream());
            if (mode == ConnectionMode.RECV_ONLY || mode == ConnectionMode.SEND_RECV) {
                rtpSocket.getReceiveStream().start();
            }
        }
        
        
        setState(ConnectionState.HALF_OPEN);
    }

    @Override
    public void setMode(ConnectionMode mode) {
        if (mode == ConnectionMode.RECV_ONLY) {
            endpoint.getPrimarySource(this).stop();
            rtpSocket.getReceiveStream().start();
        } else if (mode == ConnectionMode.SEND_ONLY) {
            endpoint.getPrimarySource(this).start();
            rtpSocket.getReceiveStream().stop();
        } else {
            endpoint.getPrimarySource(this).start();
            rtpSocket.getReceiveStream().start();
        }
        super.setMode(mode);
    }    
    /**
     * Checks is format presented in the list.
     * 
     * @param fmts the list of formats to check
     * @param fmt the format instance to check.
     * @return true if fmt is in list of fmts.
     */
    private boolean contains(Format[] fmts, Format fmt) {
        for (int i = 0; i < fmts.length; i++) {
            if (fmts[i].matches(fmt)) {
                return true;
            }
        }
        return false;
    }

    /**
     * (Non-Javadoc).
     * 
     * @see org.mobicents.media.server.spi.Connection#getLocalDescriptor();
     */
    public String getLocalDescriptor() {
        SessionDescription localSDP = null;
        if (state == ConnectionState.NULL || state == ConnectionState.CLOSED) {
            throw new IllegalStateException("State is " + state);
        }
        if (state == ConnectionState.NULL || state == ConnectionState.CLOSED) {
            throw new IllegalStateException("State is " + state);
        }

        String userName = "MediaServer";
        long sessionID = System.currentTimeMillis() & 0xffffff;
        long sessionVersion = sessionID;

        String networkType = javax.sdp.Connection.IN;
        String addressType = javax.sdp.Connection.IP4;
        String address = null;

        RtpSocket rtpSocketImpl = this.rtpSocket;

        int audioPort = 0;
        
        address = rtpSocket.getLocalAddress();
        audioPort = rtpSocket.getLocalPort();

        try {
            localSDP = sdpFactory.createSessionDescription();
            localSDP.setVersion(sdpFactory.createVersion(0));
            localSDP.setOrigin(sdpFactory.createOrigin(userName, sessionID, sessionVersion, networkType, addressType, address));
            localSDP.setSessionName(sdpFactory.createSessionName("session"));
            localSDP.setConnection(sdpFactory.createConnection(networkType, addressType, address));

            Vector descriptions = new Vector();

            // encode formats
            HashMap<Integer, Format> rtpMap = rtpSocket.getRtpMap();
            Format[] supported = new Format[rtpMap.size()];
            rtpMap.values().toArray(supported);


            HashMap fmts = new HashMap();
            Set<Integer> map = rtpMap.keySet();

            for (Integer pt : map) {
                Format f = (Format) rtpMap.get(pt);
                if (contains(supported, f)) {
                    fmts.put(pt, f);
                }
            }

            Object[] payloads = getPayloads(fmts).toArray();

            int[] formats = new int[payloads.length];
            for (int i = 0; i < formats.length; i++) {
                formats[i] = ((Integer) payloads[i]).intValue();
            }

            // generate media descriptor
            MediaDescription md = sdpFactory.createMediaDescription("audio", audioPort, 1, "RTP/AVP", formats);

            boolean g729 = false;
            boolean dtmf = false;

            int g729payloadType = -1; // g729 payload type is usually 18
            int dtmfPayload = -1;

            // set attributes for formats
            Vector attributes = new Vector();
            for (int i = 0; i < formats.length; i++) {
                RTPAudioFormat format = (RTPAudioFormat) fmts.get(new Integer(formats[i]));
                attributes.add(sdpFactory.createAttribute("rtpmap", format.toSdp()));
                if (format.getEncoding().contains("g729")) {
                    g729 = true;
                    g729payloadType = format.getPayloadType(); // should be 18
                }
                if (format.getEncoding().equals("telephone-event/8000")) {
                    dtmf = true;
                    dtmfPayload = format.getPayloadType();
                }
            }

            // This options forces the remote g728 side to avoid using annexb, which is not supported right now
            if (g729) {
                attributes.add(sdpFactory.createAttribute("fmtp", g729payloadType + " annexb=no"));
            }
            if (dtmf) {
                attributes.add(sdpFactory.createAttribute("fmtp", dtmfPayload + " 0-15"));
            }
            // generate descriptor
            md.setAttributes(attributes);
            descriptions.add(md);

            localSDP.setMediaDescriptions(descriptions);
        } catch (SdpException e) {
            e.printStackTrace();
        }

        this.localDescriptor = localSDP.toString();
        return localDescriptor;
    }

    /**
     * (Non-Javadoc).
     * 
     * @see org.mobicents.media.server.spi.Connection#getRemoteDescriptor();
     */
    public String getRemoteDescriptor() {
        return remoteDescriptor;
    }

    /**
     * Extracts address and port of the remote party.
     * 
     * @param sdp
     *            session description.
     * @return socket address of the remote party.
     */
    private InetSocketAddress getPeer(SessionDescription sdp) throws SdpException {
        javax.sdp.Connection connection = sdp.getConnection();

        Vector list = sdp.getMediaDescriptions(false);
        MediaDescription md = (MediaDescription) list.get(0);

        try {
            InetAddress address = InetAddress.getByName(connection.getAddress());
            int port = md.getMedia().getMediaPort();
            return new InetSocketAddress(address, port);
        } catch (UnknownHostException e) {
            throw new SdpException(e);
        }
    }

    /**
     * (Non-Javadoc).
     * 
     * @see org.mobicents.media.server.spi.Connection#setRemoteDescriptor();
     */
    public void setRemoteDescriptor(String descriptor) throws SdpException, IOException, ResourceUnavailableException {
        this.remoteDescriptor = descriptor;
        SessionDescription remoteSDP = null;
        if (state != ConnectionState.HALF_OPEN && state != ConnectionState.OPEN) {
            throw new IllegalStateException("State is " + state);
        }
        if (state != ConnectionState.HALF_OPEN && state != ConnectionState.OPEN) {
            throw new IllegalStateException("State is " + state);
        }

        remoteSDP = sdpFactory.createSessionDescription(descriptor);

        // add peer to RTP socket
        InetSocketAddress peer = getPeer(remoteSDP);
        rtpSocket.setPeer(peer.getAddress(), peer.getPort());

        // negotiate codecs
        HashMap<Integer, Format> offer = RTPFormatParser.getFormats(remoteSDP,"");
        //logger.debug("Codec offer = "+ offer);
        HashMap<Integer, Format> rtpMap = rtpSocket.getRtpMap();
        //logger.debug("Codec rtpMap = "+ rtpMap);
        
        HashMap<Integer, Format> subset = this.subset(offer, rtpMap);
        //logger.debug("Codec subset = "+ subset);

        if (subset.isEmpty()) {
            throw new IOException("Codecs are not negotiated");
        }

        this.updateRtpMap(subset);
        rtpSocket.getSendStream().setFormats(subset.values());

        try {
           // rtpSocket.setPeriod(getPacketizationPeriod(remoteSDP));
        } catch (Exception e) {
        }

        Format[] fmts = new Format[subset.size()];
        subset.values().toArray(fmts);
        
        endpoint.allocateMediaSources(this, fmts);
        if (getMode() == ConnectionMode.SEND_ONLY || getMode() == ConnectionMode.SEND_RECV) {
            endpoint.getPrimarySource(this).connect(rtpSocket.getSendStream());
            endpoint.getPrimarySource(this).start();
        }
        setState(ConnectionState.OPEN);
    }

    private HashMap subset(HashMap<Integer, Format> remote, HashMap<Integer, Format> local) {
        HashMap<Integer, Format> subset = new HashMap();
        for (Integer k : remote.keySet()) {
            Format rf = remote.get(k);
            for (Integer l : local.keySet()) {
                Format lf = local.get(l);
                if (lf.matches(rf)) {
                    subset.put(k, rf);
                }
            }
        }
        return subset;
    }

    private void updateRtpMap(HashMap<Integer, Format> offer) {
        rtpSocket.getRtpMap().clear();
        rtpSocket.getRtpMap().putAll(offer);
    }

    /**
     * Gets packetization period encoded in session descriptor.
     * 
     * @param sd
     *            the session descriptor
     * @return value of the packetization period in milliseconds.
     * @throws javax.sdp.SdpException
     */
    private int getPacketizationPeriod(SessionDescription sd) throws Exception {
        MediaDescription md = (MediaDescription) sd.getMediaDescriptions(false).get(0);
        String value = md.getAttribute("ptime");
        return Integer.parseInt(value);
    }

    /**
     * (Non-Javadoc).
     * 
     * @throws InterruptedException
     * 
     * @see org.mobicents.media.server.spi.Connection#setRemoteDescriptor();
     */
    public void setOtherParty(Connection other) {
    }

    /**
     * Gets the collection of payload types.
     * 
     * @param fmts
     *            the map with payload type as key and format as a value.
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
     * Releases all resources requested by this connection.
     * 
     * @throws InterruptedException
     */
    @Override
    protected void close() {
        endpoint.connections.remove(id);
        try {
            rtpSocket.getReceiveStream().stop();
            
            rtpSocket.getReceiveStream().disconnect(endpoint.getPrimarySink(this));
            rtpSocket.getSendStream().disconnect(endpoint.getPrimarySource(this));
            
            endpoint.deallocateRtpSocket(rtpSocket, this);
        } finally {
            super.close();
        }
    }

    /**
     * Gets the text representation of the connection.
     * 
     * @return text representation of the connection.
     */
    @Override
    public String toString() {
        return "(RTP Cconnection, ID=" + id + ", endpoint=" + endpointName + ", state=" + state + ")";
    }

    public void error(Exception e) {
        this.endpoint.deleteConnection(id);
    }

    public String getOtherEnd() throws IllegalArgumentException {
        return "Remote";
    }

    public boolean isGatherStats() {
        return false;
    }

    public void setGatherStats(boolean gatherStats) {
    }

    public int getPacketsSent() {
        return 0;
    }

    public int getPacketsReceived() {
        return 0;
    }

    public int getOctetsReceived() {
        return 0;
    }

    public int getOctetsSent() {
        return -1;
    }

    public int getInterArrivalJitter() {
        return -1;
    }

    public int getPacketsLost() {
        return -1;
    }

    public void addNotificationListener(NotificationListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeNotificationListener(NotificationListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Component getComponent(int resourceType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Component getComponent(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
