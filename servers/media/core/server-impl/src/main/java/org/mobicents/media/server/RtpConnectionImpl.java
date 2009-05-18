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
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.impl.resource.Demultiplexer;
import org.mobicents.media.server.impl.resource.Multiplexer;
import org.mobicents.media.server.impl.rtp.RtpFactory;
import org.mobicents.media.server.impl.rtp.RtpSocket;
import org.mobicents.media.server.impl.rtp.RtpSocketListener;
import org.mobicents.media.server.impl.rtp.sdp.RTPFormat;
import org.mobicents.media.server.impl.rtp.sdp.RTPFormatParser;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.ConnectionState;
import org.mobicents.media.server.spi.ResourceUnavailableException;

/**
 * 
 * @author kulikov
 */
public class RtpConnectionImpl extends ConnectionImpl implements RtpSocketListener {

    private SdpFactory sdpFactory;
    private String localDescriptor;
    private String remoteDescriptor;
    private HashMap<String, RtpSocket> rtpSockets = new HashMap<String, RtpSocket>();
    private Demultiplexer demux;
    private Multiplexer mux;
    private Format[] formats;
    
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
                throw new ResourceUnavailableException(e);
            } catch (StunException e) {
                throw new ResourceUnavailableException(e);
            }
        }

        // create demux and join with txChannel
        demux = new Demultiplexer("Demux[rtpCnnection=" + this.getId() + "]");
        demux.setConnection(this);
        
        mux = new Multiplexer("Mux[rtpCnnection=" + this.getId() + "]");
        mux.setConnection(this);
        
        // join demux and rtp sockets
        Collection<RtpSocket> sockets = rtpSockets.values();
        for (RtpSocket socket : sockets) {
            mux.connect(socket.getReceiveStream());
            demux.connect(socket.getSendStream());
        }

        //creating tx channel
        try {
            txChannel = endpoint.createTxChannel(this);
            rxChannel = endpoint.createRxChannel(this);
        } catch (Exception e) {
            throw new ResourceUnavailableException(e);
        }
        
        //connect tx channel with Demultiplexer Input
        //and demux will split data between rtp sockets
        Format[] rxFormats = null;
        if (rxChannel != null && mode != ConnectionMode.SEND_ONLY) {
            rxFormats = rxChannel.connect(mux.getOutput());
        }

        Format[] txFormats = null;
        if (txChannel != null) {
            txFormats = txChannel.connect(demux.getInput());
        }
        
        formats = rxFormats != null? rxFormats : txFormats;
        // when demux already connected to channel
        // all supported formats are known and we can generate
        // local descriptor and update rtp map
                
        createLocalDescriptor(formats);
        setMode(mode);
        setState(ConnectionState.HALF_OPEN);
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
            sdp.setMediaDescriptions(descriptions);
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
        Format[] supported = formats;
        
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
            rtpSocket.getReceiveStream().start();
        }
        
        demux.start();
        mux.getOutput().start();
        
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
        //rtpSocket.getRtpMap().clear();
        //rtpSocket.getRtpMap().putAll(offer);
        rtpSocket.setRtpMap(offer);
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
    
    @Override
    protected void close() {
        int count = ((EndpointImpl)getEndpoint()).getConnections().size();
        if (count == 0) {
        	//FIXME: this should be done in channels, shouldnt it?
        	MediaSource source = ((EndpointImpl)getEndpoint()).getSource();
        	if(source!=null)
        		source.stop();
        }
        
        Collection<RtpSocket> sockets = rtpSockets.values();
        for (RtpSocket socket : sockets) {
            socket.getReceiveStream().stop();
            
            mux.disconnect(socket.getReceiveStream());
            demux.disconnect(socket.getSendStream());
            
            socket.release();
        }
        
        if (rxChannel != null) {
            rxChannel.disconnect(mux.getOutput());
        }

        if (txChannel != null) {
            txChannel.connect(demux.getInput());
        }
                
        super.close();
    }

    public void error(Exception e) {
        getEndpoint().deleteConnection(this.getId());
    }
    
}
