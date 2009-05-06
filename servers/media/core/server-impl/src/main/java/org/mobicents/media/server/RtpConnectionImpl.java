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
package org.mobicents.media.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import javax.sdp.MediaDescription;
import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SessionDescription;

import net.java.stun4j.StunException;

import org.mobicents.media.Format;
import org.mobicents.media.server.impl.resource.Demultiplexer;
import org.mobicents.media.server.impl.resource.Multiplexer;
import org.mobicents.media.server.impl.rtp.RtpFactory;
import org.mobicents.media.server.impl.rtp.RtpSocket;
import org.mobicents.media.server.impl.rtp.sdp.RTPFormat;
import org.mobicents.media.server.impl.rtp.sdp.RTPFormatParser;
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
    private HashMap<String, RtpSocket> rtpSockets = new HashMap<String, RtpSocket>();
    private Channel txChannel;
    private Channel rxChannel;
    private Demultiplexer demux;
    private Multiplexer mux;

    public RtpConnectionImpl(EndpointImpl endpoint, ConnectionMode mode) throws ResourceUnavailableException {
        super(endpoint, mode);
        sdpFactory = endpoint.getSdpFactory();

        Hashtable<String, RtpFactory> factories = endpoint.getRtpFactory();
        Set<String> mediaTypes = factories.keySet();

        for (String mediaType : mediaTypes) {
            try {
                rtpSockets.put(mediaType, factories.get(mediaType).getRTPSocket());
            } catch (SocketException e) {
                throw new ResourceUnavailableException(e);
            } catch (IOException e) {
                e.printStackTrace();
                throw new ResourceUnavailableException(e);
            } catch (StunException e) {
                e.printStackTrace();
                throw new ResourceUnavailableException(e);
            }
        }

        // create demux and join with txChannel
        demux = new Demultiplexer("Mux[rtpCnnection=" + this.getId() + "]");
        
        // join demux and rtp sockets
        Collection<RtpSocket> sockets = rtpSockets.values();
        for (RtpSocket socket : sockets) {
            demux.connect(socket.getSendStream());
        }

        //creating tx channel
        try {
            txChannel = endpoint.createTxChannel(this);
        } catch (Exception e) {
            throw new ResourceUnavailableException(e);
        }
        
        //connect tx channel with Demultiplexer Input
        //and demux will split data between rtp sockets
        Format[] fmts = txChannel.connect(demux.getInput());


        // when demux already connected to channel
        // all supported formats are known and we can generate
        // local descriptor and update rtp map
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
            sdp.setOrigin(sdpFactory.createOrigin(userName, sessionID, sessionVersion, networkType, addressType,
                    address));
            sdp.setSessionName(sdpFactory.createSessionName("session"));
            sdp.setConnection(sdpFactory.createConnection(networkType, addressType, address));

            Vector descriptions = new Vector();

            // encode formats
            Set<String> mediaTypes = rtpSockets.keySet();
            for (String mediaType : mediaTypes) {
                RtpSocket rtpSocket = rtpSockets.get(mediaType);

                HashMap<Integer, Format> rtpMap = rtpSocket.getRtpMap();
                HashMap<Integer, Format> subset = subset(rtpMap, supported);

                int port = rtpSocket.getLocalPort();
                descriptions.add(createMediaDescription(mediaType, port, subset));
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

        try {
            rxChannel = ((EndpointImpl)getEndpoint()).createRxChannel(this);
        } catch (Exception e) {
            throw new ResourceUnavailableException(e);
        }
        
        SessionDescription sdp = sdpFactory.createSessionDescription(descriptor);

        // add peer to RTP socket
        mux = new Multiplexer("MUX-" + this.getId());
        Format[] supported = rxChannel.connect(mux.getOutput());
        
        InetAddress address = InetAddress.getByName(sdp.getConnection().getAddress());

        Vector<MediaDescription> mediaDescriptions = sdp.getMediaDescriptions(false);
        for (MediaDescription md : mediaDescriptions) {
            String mediaType = md.getMedia().getMediaType();
            RtpSocket rtpSocket = rtpSockets.get(mediaType);

            HashMap<Integer, Format> offer = RTPFormatParser.getFormats(md);            
            HashMap<Integer, Format> subset = this.subset(offer, supported);

            if (subset.isEmpty()) {
                throw new IOException("Codecs are not negotiated");
            }

            int port = md.getMedia().getMediaPort();
            rtpSocket.setPeer(address, port);
            updateRtpMap(rtpSocket, subset);
            rtpSocket.getReceiveStream().connect(mux);
        }
        setState(ConnectionState.OPEN);
    }

    public void setOtherParty(Connection other) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private int[] getFormatList(HashMap<Integer, Format> fmts) {
        int[] list = new int[fmts.size()];
        int i = 0;
        for (Integer key : fmts.keySet()) {
            list[i++] = key;
        }
        return list;
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

    private MediaDescription createMediaDescription(String mediaType, int port, HashMap<Integer, Format> formats)
            throws SdpException {
        int[] fmtList = getFormatList(formats);
        MediaDescription md = sdpFactory.createMediaDescription(mediaType, port, 1, "RTP/AVP", fmtList);

        // set attributes for formats
        Vector attributes = new Vector();
        for (int i = 0; i < fmtList.length; i++) {
            RTPFormat format = (RTPFormat) formats.get(fmtList[i]);
            attributes.addAll(format.encode());
        }

        // generate descriptor
        md.setAttributes(attributes);
        return md;
    }
}
