/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;
import javax.sdp.MediaDescription;
import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SessionDescription;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.Demultiplexer;
import org.mobicents.media.server.impl.Multiplexer;
import org.mobicents.media.server.impl.rtp.RtpFactory;
import org.mobicents.media.server.impl.rtp.RtpSocket;
import org.mobicents.media.server.impl.rtp.sdp.RTPAudioFormat;
import org.mobicents.media.server.impl.rtp.sdp.RTPFormat;
import org.mobicents.media.server.resource.Channel;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.ConnectionState;
import org.mobicents.media.server.spi.ResourceUnavailableException;

/**
 *
 * @author kulikov
 */
public class RtpConnectionImpl extends ConnectionImpl {

    private SdpFactory sdpFactory;
    
    private String localDescriptor;
    private String remoteDescriptor;
    
    private HashMap<String, RtpSocket> rtpSockets;
    
    private Channel txChannel;
    private Channel rxChannel;
    
    private Demultiplexer demux;
    private Multiplexer mux;

    public RtpConnectionImpl(EndpointImpl endpoint, ConnectionMode mode) throws ResourceUnavailableException {
        super(endpoint, mode);
        sdpFactory = endpoint.getSdpFactory();

        Hashtable<String, RtpFactory> factories = endpoint.getRtpFactory();
        Set<String> mediaTypes = factories.keySet();
        
        for(String mediaType: mediaTypes) {
            try {
                rtpSockets.put(mediaType, factories.get(mediaType).getRTPSocket());
            } catch (SocketException e) {
                throw new ResourceUnavailableException(e);
            }
        }
        
        //create demux and join with txChannel
        demux = new Demultiplexer();
        txChannel.connect(demux.getInput());

        //join demux and rtp sockets
        Collection<RtpSocket> sockets = rtpSockets.values();
        for (RtpSocket socket : sockets) {
            demux.connect(socket.getSendStream());
        }
        
        //when demux already connected to channel
        //all supported formats are known and we can generate 
        //local descriptor and update rtp map
        Format[] fmts = demux.getFormats();
        createLocalDescriptor(fmts);
    }

    private String createLocalDescriptor(Format[] supported) {
        SessionDescription sdp = null;
        String userName = "MediaServer";

        long sessionID = System.currentTimeMillis() & 0xffffff;
        long sessionVersion = sessionID;

        String networkType = javax.sdp.Connection.IN;
        String addressType = javax.sdp.Connection.IP4;

        String address = rtpSockets.get("audio").getLocalAddress();

        try {
            sdp = sdpFactory.createSessionDescription();
            sdp.setVersion(sdpFactory.createVersion(0));
            sdp.setOrigin(sdpFactory.createOrigin(userName, sessionID, sessionVersion, networkType, addressType, address));
            sdp.setSessionName(sdpFactory.createSessionName("session"));
            sdp.setConnection(sdpFactory.createConnection(networkType, addressType, address));

            Vector descriptions = new Vector();

            // encode formats

            if (rtpSockets.containsKey("audio")) {
                HashMap<Integer, Format> rtpMap = rtpSockets.get("audio").getRtpMap();
                HashMap<Integer, Format> subset = subset(rtpMap, supported);


                int port = rtpSockets.get("audio").getLocalPort();
                descriptions.add(createAudioDescription(port, subset));
            }

            if (rtpSockets.containsKey("video")) {
                HashMap<Integer, Format> rtpMap = rtpSockets.get("video").getRtpMap();
                HashMap<Integer, Format> subset = subset(rtpMap, supported);
                
                int port = rtpSockets.get("video").getLocalPort();
                descriptions.add(createVideoDescription(port, subset));
            }
        } catch (SdpException e) {
        }
        localDescriptor = sdp.toString();
        return localDescriptor;
    }

    public String getLocalDescriptor() {
        if (getState() == ConnectionState.NULL || getState() == ConnectionState.CLOSED) {
            throw new IllegalStateException("State is " + getState());
        }
        return this.localDescriptor;
    }

    public String getRemoteDescriptor() {
        return this.remoteDescriptor;
    }

    public void setRemoteDescriptor(String descriptor) throws SdpException, IOException, ResourceUnavailableException {
        this.remoteDescriptor = descriptor;
        if (getState() != ConnectionState.HALF_OPEN && getState() != ConnectionState.OPEN) {
            throw new IllegalStateException("State is " + getState());
        }

        SessionDescription sdp = sdpFactory.createSessionDescription(descriptor);

        // add peer to RTP socket
        InetSocketAddress peer = getPeer(sdp, "audio");
        HashMap<Integer, Format> offer = RTPFormat.getFormats(sdp, "audio");
        
        Format[] supported = rxChannel.getFormats();
        HashMap<Integer, Format> subset = this.subset(offer, supported);

        if (subset.isEmpty()) {
            throw new IOException("Codecs are not negotiated");
        }

        mux = new Multiplexer("MUX-" + this.getId());
        
        updateRtpMap(rtpSockets.get("audio"), subset);
        rtpSockets.get("audio").getReceiveStream().connect(mux);
        
        rxChannel.connect(mux.getOutput());

        setState(ConnectionState.OPEN);
    }

    public void setOtherParty(Connection other) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
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

    private int[] getFormatList(HashMap<Integer, Format> fmts) {
        int[] list = new int[fmts.size()];
        int i = 0;
        for (Integer key : fmts.keySet()) {
            list[i++] = key;
        }
        return list;
    }

    /**
     * Extracts address and port of the remote party.
     * 
     * @param sdp
     *            session description.
     * @return socket address of the remote party.
     */
    private InetSocketAddress getPeer(SessionDescription sdp, String media) throws SdpException {
        javax.sdp.Connection connection = sdp.getConnection();

        Vector list = sdp.getMediaDescriptions(false);
        MediaDescription md = (MediaDescription) list.get(0);
        if (md.getMedia().getMediaType().equals("media")) {
            try {
                InetAddress address = InetAddress.getByName(connection.getAddress());
                int port = md.getMedia().getMediaPort();
                return new InetSocketAddress(address, port);
            } catch (UnknownHostException e) {
                throw new SdpException(e);
            }
        }
        return null;
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

    private HashMap subset(HashMap<Integer, Format> map, Format[] fmts) {
        HashMap<Integer, Format> subset = new HashMap();
        for (Integer k : map.keySet()) {
            Format rf = map.get(k);
            for (Format f : fmts) {
                if (f.matches(rf)) {
                    subset.put(k, rf);
                }
            }
        }
        return subset;
    }

    private void updateRtpMap(RtpSocket rtpSocket, HashMap<Integer, Format> offer) {
        rtpSocket.getRtpMap().clear();
        rtpSocket.getRtpMap().putAll(offer);
    }

    private MediaDescription createAudioDescription(int port, HashMap<Integer, Format> formats) throws SdpException {
        int[] fmtList = getFormatList(formats);
        MediaDescription md = sdpFactory.createMediaDescription("audio", port, 1, "RTP/AVP", fmtList);

        // set attributes for formats
        Vector attributes = new Vector();
        for (int i = 0; i < fmtList.length; i++) {
            RTPAudioFormat format = (RTPAudioFormat) formats.get(fmtList[i]);
            attributes.addAll(format.encode());
        }

        // generate descriptor
        md.setAttributes(attributes);
        return md;
    }
    
    private MediaDescription createVideoDescription(int port, HashMap<Integer, Format> formats) throws SdpException {
        int[] fmtList = getFormatList(formats);
        MediaDescription md = sdpFactory.createMediaDescription("audio", port, 1, "RTP/AVP", fmtList);

        // set attributes for formats
        Vector attributes = new Vector();
        for (int i = 0; i < fmtList.length; i++) {
            RTPAudioFormat format = (RTPAudioFormat) formats.get(fmtList[i]);
            attributes.addAll(format.encode());
        }

        // generate descriptor
        md.setAttributes(attributes);
        return md;
    }
    
}
