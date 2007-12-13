/*
 * RtpMediaConnectionImpl.java
 *
 *
 * The Simple Media API RA
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
package org.mobicents.slee.resource.media.ra.rtp;


import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import java.util.Vector;
import javax.media.Format;
import javax.media.IncompatibleSourceException;
import javax.media.Manager;

import javax.media.Processor;
import javax.media.control.FormatControl;
import javax.media.control.TrackControl;
import javax.media.format.AudioFormat;
import javax.media.protocol.DataSource;
import javax.media.rtp.InvalidSessionAddressException;
import javax.media.rtp.RTPControl;
import javax.media.rtp.RTPManager;
import javax.media.rtp.ReceiveStreamListener;
import javax.media.rtp.SendStream;
import javax.media.rtp.SessionAddress;
import javax.media.rtp.event.ByeEvent;
import javax.media.rtp.event.NewReceiveStreamEvent;
import javax.media.rtp.event.ReceiveStreamEvent;
import javax.media.rtp.event.RemotePayloadChangeEvent;
import javax.media.rtp.event.StreamClosedEvent;
import javax.media.rtp.event.TimeoutEvent;
import javax.sdp.Attribute;

import javax.sdp.Connection;
import javax.sdp.Media;
import javax.sdp.MediaDescription;
import javax.sdp.Origin;
import javax.sdp.SdpConstants;
import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SdpParseException;
import javax.sdp.SessionDescription;
import javax.sdp.SessionName;
import org.apache.log4j.Logger;
import org.mobicents.slee.resource.media.ra.*;

import org.mobicents.slee.resource.media.ratype.Cause;
import org.mobicents.slee.resource.media.ratype.MediaConnection;
import org.mobicents.slee.resource.media.ratype.MediaContext;
import org.mobicents.slee.resource.media.ratype.RtpMediaConnection;

/**
 * Implements RTP connection for simple local media resource adaptor.
 *
 * @author Oleg Kulikov
 */
public class RtpMediaConnectionImpl extends BaseMediaConnection
        implements Runnable, RtpMediaConnection, ReceiveStreamListener {
    
    /** SDP Factory instance */
    private SdpFactory sdpFactory = SdpFactory.getInstance();
    
    private int state = STATE_NULL;
    private String id = Long.toString(System.currentTimeMillis());
    
    private MediaResourceAdaptor ra;
    
    private String localDescriptor;
    private String remoteDescriptor;
    
    private SessionDescription localSdp;
    private SessionDescription remoteSdp;
    
    private RTPManager manager;
    private SessionAddress localAddress;
    private SessionAddress remoteAddress;
    
    //protected DataSource inputStream;
    //protected DataSource outputStream;
    
    private HashMap codecs;
    
    private Logger logger = Logger.getLogger(RtpMediaConnectionImpl.class);
    private String[] states = new String[] {"NULL", "CONNECTING", "CONNECTED", "DISCONNECTED", "FAILED"};
    
    private DtmfDetector dtmfDetector;
    private Format currentFormat;
    
    private SendStream sendStream;
    /**
     * Creates a new instance of RtpMediaConnectionImpl
     *
     * @param ra the resource adaptor entity.
     */
    public RtpMediaConnectionImpl(MediaResourceAdaptor ra) {
        this.ra = ra;
        logger.info("connection id = " + id + " created, state = NULL");
        
        try {
            localSdp = sdpFactory.createSessionDescription();
        } catch (SdpException e) {
            logger.error("Could not create local session description: ", e);
            setState(STATE_FAILED, Cause.INTERNAL_SERVER_ERROR);
            return;
        }
        
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            localAddress = new SessionAddress(localhost, SessionAddress.ANY_PORT);
            //localAddress = new SessionAddress(localhost, PORT++);
        } catch (UnknownHostException e) {
            //should never happen
        }
    }
    
    /**
     * Gets unique identifier of this connection.
     *
     * @return unique integer identifier.
     */
    public String getId() {
        return id;
    }
    
    /**
     * (Non Java-doc).
     *
     *
     * @see org.mobicents.slee.resource.media.ratype.RtpMediaConnection#setRemoteDescriptor(String).
     */
    public void setRemoteDescriptor(String sdp) throws IllegalArgumentException {
        this.remoteDescriptor = sdp;
        try {
            remoteSdp = sdpFactory.createSessionDescription(sdp);
            Connection connection = remoteSdp.getConnection();
            InetAddress remoteHost = null;
            
            MediaDescription md = null;
            
            remoteHost = InetAddress.getByName(connection.getAddress());;
            Vector  list = remoteSdp.getMediaDescriptions(false);
            md = (MediaDescription) list.get(0);
            
            int remotePort = md.getMedia().getMediaPort();
            remoteAddress = new SessionAddress(remoteHost, remotePort);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    /**
     * (Non Java-doc).
     *
     *
     * @see org.mobicents.slee.resource.media.ratype.RtpMediaConnection#getRemoteDescriptor().
     */
    public String getRemoteDescriptor() {
        return remoteDescriptor;
    }
    
    /**
     * (Non Java-doc).
     *
     *
     * @see org.mobicents.slee.resource.media.ratype.RtpMediaConnection#getLocalDescriptor().
     */
    public String getLocalDescriptor() {
        return localDescriptor;
    }
    
    public Format getFormat() {
        return currentFormat;
    }
    
    /**
     * Shifts state of this connection.
     *
     * @param state the new state to shift.
     * @param cause the cause of state shifting.
     */
    protected void setState(int state, int cause) {
        logger.info("(connection id  = " + getId() + ") current state = " +
                states[this.state] + ", target state = " + states[state] + ", cause: " + cause);
        
        this.state = state;
        
        ConnectionEventImpl evt = new ConnectionEventImpl(this, cause);
        switch (state) {
            case MediaConnection.STATE_CONNECTING :
                if (logger.isDebugEnabled()) {
                    logger.debug("(connection id = " + getId() + ") Fire CONNECTION_CONNECTING event");
                }
                ra.fireConnectionConnectingEvent(evt);
                break;
            case MediaConnection.STATE_CONNECTED :
                if (logger.isDebugEnabled()) {
                    logger.debug("(connection id = " + getId() + ") Fire CONNECTION_CONNECTED event");
                }
                getMediaContext().add(this);
                ra.fireConnectionConnectedEvent(evt);
                break;
            case MediaConnection.STATE_DISCONNECTED :
                if (logger.isDebugEnabled()) {
                    logger.debug("(connection id = " + getId() + ") Fire CONNECTION_DISCONNECTED event");
                }
                ra.fireConnectionDisconnectedEvent(evt);
                break;
            case MediaConnection.STATE_FAILED :
                if (logger.isDebugEnabled()) {
                    logger.debug("(connection id = " + getId() + ") Fire CONNECTION_FAILED event");
                }
                ra.fireConnectionFailedEvent(evt);
                release();
        }
    }
    
    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.slee.resource.media.ratype.Connection#getState().
     */
    public int getState() {
        return state;
    }
    
    
    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.slee.resource.media.ratype.Connection#init().
     */
    public void init() {
        new Thread(this).start();
    }
    
    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.slee.resource.media.ratype.Connection#init(String).
     */
    public void init(int contextType) {
        MediaContext mediaContext = MediaContextLocator.getContext(contextType, ra);
        if (mediaContext == null) {
            setState(STATE_FAILED, Cause.INTERNAL_SERVER_ERROR);
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("(connection id = " + getId() + ") acquired new media context: " + mediaContext);
        }
        setMediaContext(mediaContext);
        //mediaContext.add(this);
        init();
    }
    
    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.slee.resource.media.ratype.Connection#init(MediaContext).
     */
    public void init(MediaContext mediaContext) {
        setMediaContext(mediaContext);
        if (logger.isDebugEnabled()) {
            logger.debug("(connection id = " + getId() + ") initializing within: " + mediaContext);
        }
        //mediaContext.add(this);
        init();
    }
    
    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.slee.resource.media.ratype.Connection#release().
     */
    public void release() {
        new Thread(new ReleaseTx(this)).start();
    }
    
    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.slee.resource.media.ratype.Connection#setOutputStream(DataSource).
     */
    public void setOutputStream(DataSource outputStream) {
        if (logger.isDebugEnabled()) {
            logger.debug("(connection id = " + getId() + ") Apply output datasource " + outputStream);
        }
        super.setOutputStream(outputStream);
        try {
            sendStream = manager.createSendStream(outputStream, 0);
            sendStream.start();
            if (logger.isDebugEnabled()) {
                logger.debug("(connection id = " + getId() + ") Starting send stream " + sendStream);
            }
        } catch (Exception e) {
            setState(STATE_FAILED, Cause.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Fires DTMF event for specified symbol.
     *
     * @param symbol the symbol pressed on phone.
     */
    protected void onDtmf(String symbol) {
        if (symbol.equals("0")) {
            ra.fireConnectionDtmfEvent(new ConnectionEventImpl(this, Cause.DTMF_0));
        } else if (symbol.equals("1")) {
            ra.fireConnectionDtmfEvent(new ConnectionEventImpl(this, Cause.DTMF_1));
        } else if (symbol.equals("2")) {
            ra.fireConnectionDtmfEvent(new ConnectionEventImpl(this, Cause.DTMF_2));
        } else if (symbol.equals("3")) {
            ra.fireConnectionDtmfEvent(new ConnectionEventImpl(this, Cause.DTMF_3));
        } else if (symbol.equals("4")) {
            ra.fireConnectionDtmfEvent(new ConnectionEventImpl(this, Cause.DTMF_4));
        } else if (symbol.equals("5")) {
            ra.fireConnectionDtmfEvent(new ConnectionEventImpl(this, Cause.DTMF_5));
        } else if (symbol.equals("6")) {
            ra.fireConnectionDtmfEvent(new ConnectionEventImpl(this, Cause.DTMF_6));
        } else if (symbol.equals("7")) {
            ra.fireConnectionDtmfEvent(new ConnectionEventImpl(this, Cause.DTMF_7));
        } else if (symbol.equals("8")) {
            ra.fireConnectionDtmfEvent(new ConnectionEventImpl(this, Cause.DTMF_8));
        } else if (symbol.equals("9")) {
            ra.fireConnectionDtmfEvent(new ConnectionEventImpl(this, Cause.DTMF_9));
        } else if (symbol.equals("A")) {
            ra.fireConnectionDtmfEvent(new ConnectionEventImpl(this, Cause.DTMF_A));
        } else if (symbol.equals("B")) {
            ra.fireConnectionDtmfEvent(new ConnectionEventImpl(this, Cause.DTMF_B));
        }
    }
    
    
    /**
     * Executes tx.
     */
    public void run() {
        //if media context is present check its state.
//        if (getMediaContext() != null && getMediaContext().getState() == MediaContext.FAILED) {
//            setState(STATE_FAILED, getMediaContext().getCause());
//        }
        
        manager = RTPManager.newInstance();
        try {
            codecs = remoteDescriptor != null ? getSelectedCodecs() : getDefaultCodecs();
            applyCodecs(manager, codecs);
            manager.addFormat(new Format("DTMF"), 101);
        } catch (SdpParseException e){
            setState(STATE_FAILED, Cause.INVALID_OR_MISSING_REMOTE_DESCRIPTOR);
            return;
        } catch (SdpException ex) {
            setState(STATE_FAILED, Cause.INVALID_OR_MISSING_REMOTE_DESCRIPTOR);
            return;
        }
        
        
        try {
            manager.initialize(localAddress);
            if (logger.isDebugEnabled()) {
                logger.debug("(connection id = " + id + ", state = " + states[state] +  ") Intialized RTP manager: " + manager);
            }
        } catch (Exception e) {
            logger.error("(connection id = " + id + ", state = " + states[state] + ") Failed to initialize RTP manager. Caused by", e);
            setState(STATE_FAILED, Cause.INTERNAL_SERVER_ERROR);
            return;
        }
        
        try {
            manager.addTarget(localAddress);
            manager.addTarget(remoteAddress);
            if (logger.isDebugEnabled()) {
                logger.debug("(connection id = " + id + ", state = " + states[state] + ") Add target " + remoteAddress);
            }
        } catch (Exception e) {
            logger.error("(connection id = " + id + ", state = " + states[state] + ") Failed to add target. Caused by", e);
            setState(STATE_FAILED, Cause.INTERNAL_SERVER_ERROR);
            return;
        }
        
        try {
            createLocalDescriptor();
        } catch (Exception e) {
            logger.error("connection id = " + id + ", state = " + states[state] + " Failed to initialize RTP manager. Caused by", e);
            setState(STATE_FAILED, Cause.INTERNAL_SERVER_ERROR);
            return;
        }
        
        manager.addReceiveStreamListener(this);
        setState(STATE_CONNECTING, Cause.NORMAL);
    }
    
    /**
     * Apply specified codecs for RTP manager.
     *
     * @param manager the RTP manager.
     * @param codecs the list of codecs.
     */
    private void applyCodecs(RTPManager manager, HashMap codecs) {
        Iterator payloads = codecs.keySet().iterator();
        while (payloads.hasNext()) {
            Integer payload = (Integer) payloads.next();
            Format format = (Format) codecs.get(payload);
            manager.addFormat(format, payload.intValue());
        }
    }
    
    /**
     * Gets the list of supported codecs.
     * May be used for application originated connections.
     *
     * @return the map of the codecs stored under keys indicating RTP payload type.
     */
    private HashMap getDefaultCodecs() {
        HashMap codecs = new HashMap();
        codecs.put(new Integer(SdpConstants.PCMU), createFormat(SdpConstants.PCMU));
        codecs.put(new Integer(SdpConstants.G723), createFormat(SdpConstants.G723));
        codecs.put(new Integer(SdpConstants.GSM), createFormat(SdpConstants.GSM));
        return codecs;
    }
    
    /**
     * Selects supported codecs offered by remote party.
     *
     * @return a hash map where stored JMF formats for codecs under payload types.
     */
    private HashMap getSelectedCodecs() throws SdpParseException, SdpException {
        MediaDescription md = (MediaDescription) remoteSdp.getMediaDescriptions(false).get(0);
        return selectCodecs(md);
    }
    
    /**
     * Selects supported codecs from offer of remote SDP.
     *
     * @return the map of supported codecs offered by remote SDP where keys
     * are offered payload types and values are format objects.
     */
    private HashMap selectCodecs(MediaDescription md) throws SdpParseException {
        Media media = md.getMedia();
        HashMap codecs = new HashMap();
        
        Enumeration formats = media.getMediaFormats(false).elements();
        
        while (formats.hasMoreElements()) {
            int payload = Integer.parseInt((String) formats.nextElement());
            Format format = getFormat(md, payload);
            if (format != null) {
                codecs.put(new Integer(payload), format);
            }
        }
        
        return codecs;
    }
    
    private Format getFormat(MediaDescription md, int payload) throws SdpParseException {
        Enumeration attributes = md.getAttributes(false).elements();
        while (attributes.hasMoreElements()) {
            Attribute attribute = (Attribute) attributes.nextElement();
            
            String name = attribute.getName();
            String value = attribute.getValue();
            
            if (name.equals("rtpmap") && value.startsWith(Integer.toString(payload))) {
                return createFormat(value.substring(value.indexOf(" ") + 1));
            }
        }
        
        return createFormat(payload);
    }
    
    /**
     * Creates format using description.
     *
     * @param description an SDP description of the media format.
     * @return JMF format object related to given description or null if
     * not supported.
     */
    private Format createFormat(String description) {
        String desc = description.toLowerCase();
        if (desc.equals("pcmu/8000")) {
            return new AudioFormat(AudioFormat.ULAW_RTP, 8000, 8, 1);
        } else if (desc.equals("g723/8000")) {
            return new AudioFormat(AudioFormat.G723_RTP, 8000, 8, 1);
        } else if (desc.equals("gsm/8000")) {
            return new AudioFormat(AudioFormat.GSM_RTP, 8000, 8, 1);
        } else {
            return null;
        }
    }
    
    /**
     * Creates format using default values of the RTP payload type.
     *
     * @param payload the default value of the RTP payload.
     * @return JMF format object related to media identified by default
     * RTP payload type.
     */
    private Format createFormat( int payload) {
        switch (payload) {
            case SdpConstants.PCMU :
                return new AudioFormat(AudioFormat.ULAW_RTP, 8000, 8, 1);
            case SdpConstants.G723 :
                return new AudioFormat(AudioFormat.G723_RTP, 8000, 8, 1);
            case SdpConstants.GSM :
                return new AudioFormat(AudioFormat.GSM_RTP, 8000, 8, 1);
            default : return null;
        }
    }
    
    private String getFormatName(AudioFormat format) {
        String fmt = format.getEncoding().toLowerCase();
        if (fmt.startsWith("gsm")) {
            fmt = "gsm";
        } else if (fmt.startsWith("ulaw")) {
            fmt = "pcmu";
        } else if (fmt.startsWith("g723")) {
            fmt = "g723";
        }
        return fmt;
    }
    
    private void createLocalDescriptor() throws SdpException {
        localSdp = sdpFactory.createSessionDescription();
        long id = System.currentTimeMillis() & 0xffffff;
        
        localSdp.setVersion(sdpFactory.createVersion(0));
        localSdp.setOrigin(sdpFactory.createOrigin("MediaRA", id, id, "IN", "IP4",
                localAddress.getDataHostAddress()));
        localSdp.setSessionName(sdpFactory.createSessionName("session"));
        localSdp.setConnection(sdpFactory.createConnection(Connection.IN, Connection.IP4, localAddress.getDataHostAddress()));
        
        //encode formats
        Object[] payloads = codecs.keySet().toArray();
        int[] formats = new int[payloads.length + 1];
        for (int i = 0; i < formats.length - 1; i++) {
            formats[i] = ((Integer)payloads[i]).intValue();
        }
        formats[formats.length - 1] = 101;
        
        //generate media descriptor
        Vector descriptions = new Vector();
        MediaDescription md = sdpFactory.createMediaDescription("audio",
                localAddress.getDataPort(), 1, "RTP/AVP", formats);
        
        //set attributes for formats
        Vector attributes = new Vector();
        for (int i = 0; i < formats.length - 1; i++) {
            AudioFormat format = (AudioFormat) codecs.get(new Integer(formats[i]));
            attributes.add(sdpFactory.createAttribute("rtpmap",
                    formats[i] + " " + getFormatName(format) + "/" + (int) format.getSampleRate()));
        }
        
        //set attributes for dtmf
        attributes.add(sdpFactory.createAttribute("fmtp", "101 0-15"));
        attributes.add(sdpFactory.createAttribute("rtpmap", "101 telephone-event/8000"));
        
        //generate descriptor
        md.setAttributes(attributes);
        descriptions.add(md);
        localSdp.setMediaDescriptions(descriptions);
        localDescriptor = localSdp.toString();
    }
    
    
    /**
     * Initialize input stream.
     *
     * @param event the event object releated to new receive stream.
     */
    public void update(ReceiveStreamEvent event) {
        if (event instanceof NewReceiveStreamEvent) {
            logger.info("connection id = " + id + ", state = " + states[state] + ", JMF Event " + event);
            setInputStream(event.getReceiveStream().getDataSource());
            setState(STATE_CONNECTED, Cause.NORMAL);
        } else if (event instanceof RemotePayloadChangeEvent) {
            int payload = ((RemotePayloadChangeEvent) event).getNewPayload();
            if (payload == 101) {
                dtmfDetector = new DtmfDetector(this);
                try {
                    dtmfDetector.setSource(getInputStream());
                    dtmfDetector.start();
                } catch (IncompatibleSourceException e) {
                    logger.error("Cannot handle the output DataSource from the processor: ", e);
                }
            }
        } else if (event instanceof TimeoutEvent) {
            setState(STATE_FAILED, Cause.TIMEOUT);
        } else if (event instanceof ByeEvent) {
            String reason = ((ByeEvent) event).getReason();
            logger.info("(connection id = " + id + ") disconnected, reason: " + reason);
            setState(STATE_DISCONNECTED, Cause.NORMAL);
        }
    }
    
    private class ReleaseTx implements Runnable {
        private RtpMediaConnectionImpl connection;
        public ReleaseTx(RtpMediaConnectionImpl connection) {
            this.connection = connection;
        }
        
        public void run() {
            //subtract connection from media context.
            if (logger.isDebugEnabled()) {
                logger.debug("(connection " + getId() + ") Releasing...");
            }
            
            if (getMediaContext() != null) {
                logger.debug("connection " + getId() + ") Subtracting from media context " + getMediaContext().getId());
                connection.getMediaContext().subtract(connection);
            }
            
            try {
                manager.dispose();
            } catch (Exception e) {
            }
            
            logger.debug("connection " + getId() + ") RTP manager is disposed");
            setState(STATE_DISCONNECTED, Cause.NORMAL);
        }
    }
}
