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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import javax.sdp.MediaDescription;
import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SessionDescription;


import org.apache.log4j.Logger;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.impl.resource.Demultiplexer;
import org.mobicents.media.server.impl.resource.Multiplexer;
import org.mobicents.media.server.impl.rtp.RtpFactory;
import org.mobicents.media.server.impl.rtp.RtpSocket;
import org.mobicents.media.server.impl.rtp.RtpSocketListener;
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;
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

    private final static Logger logger = Logger.getLogger(RtpConnectionImpl.class);
    
    public RtpConnectionImpl(EndpointImpl endpoint, ConnectionMode mode) throws ResourceUnavailableException {
        super(endpoint, mode);
        sdpFactory = endpoint.getSdpFactory();

        Hashtable<String, RtpFactory> factories = endpoint.getRtpFactory();
        Set<String> mediaTypes = factories.keySet();

        //Creating RTP sockets for each media type
        for (String mediaType : mediaTypes) {
            RtpSocket rtpSocket = factories.get(mediaType).getRTPSocket();
            
            rtpSocket.getReceiveStream().setEndpoint(endpoint);
            rtpSocket.getReceiveStream().setConnection(this);
            
            rtpSocket.getSendStream().setEndpoint(endpoint);
            rtpSocket.getSendStream().setConnection(this);
            
            rtpSockets.put(mediaType, rtpSocket);
            
            if (logger.isDebugEnabled()) {
                logger.debug("Endpoint=" + endpoint.getLocalName() + 
                        ", index=" + getIndex() + ", allocated RTP socket[" + mediaType + "], RTP formats: " + rtpSocket.getRtpMap());
            }
        }

        //Incoming data should be multiplexed into single stream
        //Outgoing stream, up side down, have to be demultiplexed 
        //and streamed by different RTP sockets
        demux = new Demultiplexer("Demultiplexer[RTP]");
        demux.setEndpoint(endpoint);
        demux.setConnection(this);

        mux = new Multiplexer("Multiplexer[RTP]");
        mux.setEndpoint(endpoint);
        mux.setConnection(this);

        // join multiplexer and rtp receive stream
        Collection<RtpSocket> sockets = rtpSockets.values();
        for (RtpSocket socket : sockets) {
            mux.connect(socket.getReceiveStream());
            socket.setListener(this);
        }

        //creating tx channel using endpoint's factory
        try {
            txChannel = endpoint.createTxChannel(this);
            rxChannel = endpoint.createRxChannel(this);
        } catch (Exception e) {
            throw new ResourceUnavailableException(e);
        }

        //checks connecttion mode and which channels were actualy created
        if (mode == ConnectionMode.SEND_ONLY || mode == ConnectionMode.SEND_RECV) {
            if (txChannel == null) {
                throw new ResourceUnavailableException("Mode not supported");
            }
        }

        if (mode == ConnectionMode.RECV_ONLY || mode == ConnectionMode.SEND_RECV) {
            if (rxChannel == null) {
                throw new ResourceUnavailableException("Mode not supported");
            }
        }
        
        //determine formats supported Endpoint        
        //and connect channels with MUX/DEMUX
        Format[] rxFormats = new Format[0];
        if (rxChannel != null) {
            rxFormats = rxChannel.getInputFormats();
            if (logger.isDebugEnabled()) {
                logger.debug("Endpoint=" + endpoint.getLocalName() + 
                        ", index=" + getIndex() + ", rx formats=[" + 
                        getSupportedFormatList(rxFormats) + "]");
            }
            rxChannel.connect(mux.getOutput());
        }

        Format[] txFormats = new Format[0];
        if (txChannel != null) {
            txFormats = txChannel.getOutputFormats();
            if (logger.isDebugEnabled()) {
                logger.debug("Endpoint=" + endpoint.getLocalName() + 
                        ", index=" + getIndex() + ", tx formats=[" + 
                        getSupportedFormatList(txFormats) + "]");
            }
            txChannel.connect(demux.getInput());
        }

        //merge supported formats and create local descriptor
        formats = this.mergeFormats(rxFormats, txFormats);
        createLocalDescriptor(formats);
        
        setMode(mode);
        setState(ConnectionState.HALF_OPEN);
        
        if (logger.isDebugEnabled()) {
            String fs = "";
            for (Format fmt: formats) {
                fs = fs.length() == 0 ? fmt.toString() : fs + ";" + fmt.toString();
            }
            logger.info("Endpoint=" + endpoint.getLocalName() + 
                        ", index=" + getIndex() + 
                        ", successfully created, endpoint's formats{" + fs + "}, state=" + getState());
        }
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

                if (logger.isDebugEnabled()) {
                    logger.debug("Endpoint=" + getEndpoint().getLocalName() + 
                        ", index=" + getIndex() + ", RTP format subset[" + mediaType + "]: " + subset);
                }
                
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
        javax.sdp.Connection conn = null;


        Vector<MediaDescription> mediaDescriptions = sdp.getMediaDescriptions(false);
        for (MediaDescription md : mediaDescriptions) {
            String mediaType = md.getMedia().getMediaType();
            RtpSocket rtpSocket = rtpSockets.get(mediaType);

            //skip unnsupported media
            if (rtpSocket == null) {
                continue;
            }

            HashMap<Integer, Format> offer = RTPFormatParser.getFormats(md);
            HashMap<Integer, Format> subset = this.subset(offer, supported);
            
            if (logger.isDebugEnabled()) {
                logger.debug("Endpoint=" + getEndpoint().getLocalName() + 
                        ", index=" + getIndex() + " offer= " + offer + ", supported=" 
                        + this.getSupportedFormatList(supported) + ", subset=" + subset);
            }
            
            if (subset.isEmpty()) {
                throw new IOException("Codecs are not negotiated");
            }
            
            subset = this.prefferAudio(subset);
            if (logger.isDebugEnabled()) {
                logger.debug("Endpoint=" + getEndpoint().getLocalName() + 
                        ", index=" + getIndex() + " selected preffered = " + subset);
            }
            
            conn = md.getConnection();
            if(conn == null){
            	//Use session-level if media-level "c=" field is not defined
            	conn = sdp.getConnection();
            }
            InetAddress address = InetAddress.getByName(conn.getAddress());
            int port = md.getMedia().getMediaPort();
            rtpSocket.setPeer(address, port);
            updateRtpMap(rtpSocket, subset);

            
            Format[] negotiated = new Format[subset.size()];
            subset.values().toArray(negotiated);

            this.createLocalDescriptor(negotiated);

            //This is done in constructor
            //rtpSocket.getReceiveStream().connect(mux);
            demux.connect(rtpSocket.getSendStream());
            rtpSocket.getReceiveStream().start();
            rtpSocket.getSendStream().start();
        }

        demux.start();
        mux.getOutput().start();

        setState(ConnectionState.OPEN);
        if (logger.isDebugEnabled()) {
              logger.debug("Endpoint=" + getEndpoint().getLocalName() + 
                        ", index=" + getIndex() +  " state = " + getState());
        }
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
        int count = ((EndpointImpl) getEndpoint()).getConnections().size();
        if (count == 0) {
            //Connection stops endpoint.source if no more connections
            //channel is responsable for media path only
            MediaSource source = ((EndpointImpl) getEndpoint()).getSource();
            if (source != null) {
                source.stop();
            }
        }

        Collection<RtpSocket> sockets = rtpSockets.values();
        for (RtpSocket socket : sockets) {
            socket.getReceiveStream().stop();
            socket.getSendStream().stop();

            mux.disconnect(socket.getReceiveStream());
            demux.disconnect(socket.getSendStream());

            socket.getReceiveStream().setEndpoint(null);
            socket.getSendStream().setEndpoint(null);
            
            socket.getReceiveStream().setConnection(null);
            socket.getSendStream().setConnection(null);
            
            socket.release();
        }

        rtpSockets.clear();


        if (rxChannel != null) {
            rxChannel.disconnect(mux.getOutput());
//            rxChannel.setConnection(null);
        }

        if (txChannel != null) {
            txChannel.disconnect(demux.getInput());
//            txChannel.setConnection(null);
        }

        mux.setConnection(null);
        demux.setConnection(null);

        mux = null;
        demux = null;

        super.close();
    }

    public void error(Exception e) {
        getEndpoint().deleteConnection(this.getId());
    }

    private boolean isContains(ArrayList<Format> list, Format fmt) {
        for (Format format : list) {
            if (format.matches(fmt)) return true;
        }
        return false;
    }
    
    private Format[] mergeFormats(Format[] f1, Format[] f2) {
        ArrayList<Format> list = new ArrayList();
        for (Format f : f1) {
            list.add(f);
        }

        for (Format f : f2) {
            if (!isContains(list, f)) {
                list.add(f);
            }
        }

        Format[] res = new Format[list.size()];
        list.toArray(res);

        return res;
    }

    /**
     * Exclude all codecs except preffered one.
     * 
     * @param formats
     */
    private HashMap<Integer, Format> prefferAudio(HashMap<Integer, Format> formats) {
        //we will try to to use PCMU 
        HashMap<Integer, Format> preffered = new HashMap();
        Collection<Integer> list = formats.keySet();
        
        boolean found = false;
        for (Integer key : list) {
            Format fmt = formats.get(key);
            if (!found) {
                if (!fmt.matches(AVProfile.DTMF)) {
                    preffered.put(key, fmt);
                    found = true;
                }
            }
            if (fmt.matches(AVProfile.DTMF)) {
                preffered.put(key, fmt);
            }
        }
        
        return preffered;
    }
    
    public long getPacketsReceived(String media) {
        return rtpSockets.get(media).getReceiveStream().getPacketsTransmitted();
    }
    
    public long getPacketsTransmitted(String media) {
        return rtpSockets.get(media).getSendStream().getBytesReceived();
    }
    
    protected String getSupportedFormatList(Format[] formats) {
        String s = "";
        for (int i = 0; i < formats.length; i++) {
            s += formats[i] + ";";
        }
        return s;
    }
    
    @Override
    public String toString() {
        return "RTP Connection [" + getEndpoint().getLocalName() + ", idx=" + getIndex() + "]";
    }
    
}
