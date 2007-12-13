/***************************************************
 *                                                 *
 *  Mobicents: The Open Source VoIP Platform       *
 *                                                 *
 *  Distributable under LGPL license.              *
 *  See terms of license at gnu.org.               *
 *                                                 *
 ***************************************************/
package org.mobicents.slee.resource.media.ra;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.dgc.VMID;
import java.rmi.server.UID;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import javax.media.CannotRealizeException;

import javax.media.Controller;
import javax.media.ControllerListener;
import javax.media.DataSink;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoDataSinkException;
import javax.media.NoProcessorException;
import javax.media.NotRealizedError;
import javax.media.Processor;
import javax.media.ProcessorModel;
import javax.media.control.TrackControl;
import javax.media.format.AudioFormat;
import javax.media.format.UnsupportedFormatException;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;
import javax.media.rtp.InvalidSessionAddressException;
import javax.media.rtp.RTPManager;
import javax.media.rtp.ReceiveStreamListener;
import javax.media.rtp.SendStream;
import javax.media.rtp.SessionAddress;
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
import javax.sdp.TimeDescription;
import javax.sdp.Version;

import org.apache.log4j.Logger;
import org.mobicents.slee.resource.media.events.SessionResultEvent;
import org.mobicents.slee.resource.media.ratype.MediaSession;

/**
 *
 * Key abstraction for maintaining an active conversation
 * between a media client and the SLEE. It is originated
 * by an SBB and corresponds to the SLEE activity for a media conversation.
 *
 * @author torosvi
 * @author Ivelin Ivanov
 *
 */
public class MediaSessionImpl implements MediaSession {
    private static Logger logger = Logger.getLogger(MediaSessionImpl.class);
    private static final String LOCAL_HOST = "org.mobicents.LOCAL_HOST";
    
    
    private final int dtmfPayload = 101;
    
    // Info for the session
    private String sessionID;
    private String attribute = "sendrecv"; //sendrecv, recvonly or sendonly
    private URL fileRcv;
    boolean dtmf;
    private String sdpDescription;
    private String negotiatedSdpFormatCode;
    private InetAddress localHost;
    private InetAddress remoteHost;
    private int srcPort = -1; // unititialized port. It will be known at the time when the session is established
    private int dstPort;
    private Vector supportedFormats;
    private Vector clientSupportedMediaFormats;
    private Format remoteClientFormat;
    private String result;
    
    // SDP
    private SdpFactory sdpFactory;
    private SessionDescription sessionDescription = null;
    
    // Listeners
    private MediaListener mediaListener;
    private ControllerListener controllerListener;
    private ReceiveStreamListener receiveStreamListener;
    
    // Media Session
    private Processor outboundProcessor = null;
    private javax.media.protocol.DataSource outboundFileDataSource = null;
    RTPManager rtpSessionManager;
    private MediaLocator outboundFileLocator = null;
    
    // Reference to the Media Resource Adaptor
    private MediaResourceAdaptor ra;
    private boolean isMediaSessionReady = false;
    private boolean isSessionStartRequested = false;
    
    // Plug-in RTPSessionMgr class
    private static boolean jdkInit = false;
    private static Method forName3ArgsM;
    private static Method getSystemClassLoaderM;
    private static ClassLoader systemClassLoader;
    private static Method getContextClassLoaderM;
    
    protected DataSource inboundMediaSource;
    private Processor recordProcessor;
    private DataSink dataSink;
    
    // Constructor
    public MediaSessionImpl(MediaResourceAdaptor ra) {
    	try {
    		this.sessionID = (new UID()).toString();
    		this.sdpFactory = SdpFactory.getInstance();
    		this.ra = ra;
    		this.localHost = InetAddress.getByName(this.ra.properties.getProperty(LOCAL_HOST));
    	} catch (UnknownHostException e) {
    		logger.error(e.getMessage(), e);
 		}	
    }
    
    public MediaSessionImpl(MediaResourceAdaptor ra, String sdp) {
        init(ra);
        
        attribute = "sendrecv";
        sdpDescription = sdp;
        
        dtmf = true;
        
        clientSupportedMediaFormats = getClientSupportedFormats(sdp);
        remoteHost = getRemoteHost(sdp);
        dstPort = getDstPort(sdp);
        
        initSession();
    }
    
    private void init(MediaResourceAdaptor ra) {
        this.sessionID = (new UID()).toString();
        this.sdpFactory = SdpFactory.getInstance();
        this.ra = ra;
        
        try {
            localHost = InetAddress.getByName(ra.properties.getProperty(LOCAL_HOST));
        } catch (UnknownHostException e) {
            logger.error(e.getMessage(), e);
        }
        
        mediaListener = new MediaListener(this, dtmf);
        mediaListener.addListener(ra);
        controllerListener = mediaListener;

        // Create an RTP session to transmit the output of the
        // processor to the specified IP address and port, and
        // to receive the answer of the SIP Client.
        SessionAddress localAddr = null;
        rtpSessionManager = newRtpManager();
        
        if (!(attribute.equalsIgnoreCase("sendonly"))) {
            receiveStreamListener = mediaListener;
            rtpSessionManager.addReceiveStreamListener(receiveStreamListener);
        }
        
        try {
            // the media session should be bound to the local address configured at deployment time
            // but we don't care which port will be used
            localAddr = new SessionAddress(localHost, SessionAddress.ANY_PORT);
            
            rtpSessionManager.addFormat(new Format("DTMF"), 101);
            rtpSessionManager.initialize(localAddr);
            srcPort = localAddr.getDataPort(); // srcPort needs to be stored for the SDP part of the corresponding SIP response
            
            SessionAddress destAddr = null;
            
            destAddr = new SessionAddress(remoteHost, dstPort);
            rtpSessionManager.addTarget(destAddr);
            result = OK;            
        } catch (InvalidSessionAddressException  e) {
            logger.warn("Failed to establish media session", e);
            result = e.getMessage();
        } catch (Exception e) {
            logger.warn("Failed to establish media session", e);
            result = e.getMessage();
        }
        
        if (!result.equalsIgnoreCase(OK)) {
            negotiatedSdpFormatCode = null;
            outboundProcessor.close();
            outboundProcessor = null;
        }
        
        SessionResultEvent event = new SessionResultEvent();
        event.setResult(result);
        ra.onEvent(event, this);
    }
    
    public String getSessionId() {
        return sessionID;
    }
    
    public void initSession() {
        mediaListener = new MediaListener(this, dtmf);
        mediaListener.addListener(ra);
        controllerListener = mediaListener;
        
        result = createOutboundProcessor();
        
        if (result != OK) {
            negotiatedSdpFormatCode = null;
            remoteClientFormat = null;
        };
        
        inboundStreamInitialized();
        
        // Create an RTP session to transmit the output of the
        // processor to the specified IP address and port, and
        // to receive the answer of the SIP Client.
        SessionAddress localAddr = null;
        
        rtpSessionManager = this.newRtpManager();
        
        if (!(attribute.equalsIgnoreCase("sendonly"))) {
            receiveStreamListener = mediaListener;
            rtpSessionManager.addReceiveStreamListener(receiveStreamListener);
        }
        
        try {
            // the media session should be bound to the local address configured at deployment time
            // but we don't care which port will be used
            localAddr = new SessionAddress(localHost, SessionAddress.ANY_PORT);
            
            rtpSessionManager.addFormat(new Format("DTMF"), 101);
            rtpSessionManager.initialize(localAddr);
            srcPort = localAddr.getDataPort(); // srcPort needs to be stored for the SDP part of the corresponding SIP response
            result = OK;
            
            SessionAddress destAddr = null;
            
            destAddr = new SessionAddress(remoteHost, dstPort);
            rtpSessionManager.addTarget(destAddr);
            
            if (fileRcv != null)
                mediaListener.prepareRecording(remoteClientFormat, fileRcv);
            
        } catch (InvalidSessionAddressException  e) {
            logger.warn("Failed to establish media session", e);
            result = e.getMessage();
        } catch (Exception e) {
            logger.warn("Failed to establish media session", e);
            result = e.getMessage();
        }
        
        if (!result.equalsIgnoreCase(OK)) {
            negotiatedSdpFormatCode = null;
            outboundProcessor.close();
            outboundProcessor = null;
        }
        
        SessionResultEvent event = new SessionResultEvent();
        event.setResult(result);
        ra.onEvent(event, this);
    }
    
    /**
     * To transmit and receive audio stream.
     *
     * @param sdp
     * @param fileTx Route of the audio file you want to transmit.
     * @param fileRcv Route where the received audio will be stored. If it
     * is null, it will be considered that the media received does not want
     * to be stored.
     * @param dtmf True if you want process DTMF digits, otherwise False.
     */
    public void createTransmitterReceiver(String sdp, URL fileTx,
            URL fileRcv, boolean dtmf) {
        // Setting session info
        attribute = "sendrecv";
        sdpDescription = sdp;
        setMediaLocator(fileTx);
        this.fileRcv = fileRcv;
        this.dtmf = dtmf;
        clientSupportedMediaFormats = getClientSupportedFormats(sdp);
        remoteHost = getRemoteHost(sdp);
        dstPort = getDstPort(sdp);
        
        initSession();
        
        return;
    }
    
    /**
     * To transmit audio stream.
     *
     * @param sdp
     * @param fileTx Route of the audio file you want to transmit.
     * @return null if everything went well, if not returns the error message.
     */
    public String createTransmitter(String sdp, URL fileTx) {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * Play announcement from specified URL.
     *
     * @param file the URL to file to play.
     * @return null if everything went well, if not returns the error message.
     */
    public String play(URL file) {
        setMediaLocator(file);
        String res = createOutboundProcessor();
        
        if (res.equalsIgnoreCase(OK)) {
            inboundStreamInitialized();
            return null;
        }
        
        return res;
    }
    
    /**
     * Record data stream to file with specified URL.
     *
     * @param file the URL to record to
     * @return null if everything went well, if not returns the error message.
     */
    public String record(URL file) {
        try {
            FileTypeDescriptor ftd= new FileTypeDescriptor(FileTypeDescriptor.BASIC_AUDIO);
            Format [] formats = new Format[] {remoteClientFormat};
            
            // ds should not be null if the media control state machine is correctly implemented
            if (inboundMediaSource == null) throw new IllegalStateException("The inbound media stream cannot be null when recording begins.");
            
            ProcessorModel processorModel = new ProcessorModel(inboundMediaSource, formats, ftd);
            recordProcessor = Manager.createRealizedProcessor(processorModel);
            
            MediaLocator f = new MediaLocator(file);
            dataSink = Manager.createDataSink(recordProcessor.getDataOutput(), f);
            
            dataSink.open();
            recordProcessor.start();
            dataSink.start();
            
            return null;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return e.getMessage();
        }
    }
    
    /**
     * To receive audio stream.
     *
     * @param sdp
     * @param fileRcv Route where the received audio will be stored. If it
     * is null, it will be considered that the media received does not want
     * to be stored.
     * @param dtmf True if you want process DTMF digits, otherwise False.
     * @return null if everything went well, if not returns the error message.
     */
    public String createReceiver(String sdp, URL fileRcv, boolean dtmf) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public boolean isProcessorStopped() {
        return mediaListener.getProcessorStopped();
    }
    
    public void startRecording() {
        mediaListener.startRecording();
    }
    
    /**
     * Gets the Audio Source Port.
     */
    public int getSrcPort() {
        return srcPort;
    }
    
    /**
     * Gets the Audio Destiny Port.
     * @param sdp
     * @return
     */
    public int getDstPort(String sdp) {
        
        try {
            // Audio Port (Port number on which the Caller is ready to receive media)
            Vector mediaDescriptions = getSessionDescription(sdp).
                    getMediaDescriptions(true);
            
            MediaDescription mediaDescription = (MediaDescription) mediaDescriptions.get(0);
            Media media = mediaDescription.getMedia();
            dstPort = media.getMediaPort();
            
        } catch (SdpParseException e) {
            logger.error(e.getMessage(), e);
        } catch (SdpException e) {
            logger.error(e.getMessage(), e);
        }
        
        return dstPort;
    }
    
    /**
     * Gets the Local Host Address.
     * @return localHost
     */
    public InetAddress getLocalHost() {
        return localHost;
    }
    
    /**
     * Gets the Remote Host Address.
     * @param sdp
     * @return remoteHost
     */
    public InetAddress getRemoteHost(String sdp) {
        // Remote Address (Caller)
        try {        	
        	Connection sessionConnection = getSessionDescription(sdp).getConnection();            
            if (sessionConnection != null) {
                remoteHost = InetAddress.getByName(sessionConnection.getAddress());
            }
            
        } catch (SdpParseException e) {
            logger.error(e.getMessage(), e);
        } catch (UnknownHostException e) {
            logger.error(e.getMessage(), e);
        }
        
        return remoteHost;
    }
    
    /**
     * Gets Media Formats of Media Description information
     * sent in SDP by client.
     * @param inviteRequest
     * @return supportedFormats
     */
    public Vector getClientSupportedFormats(String sdp) {
        
        try {
            // Audio Port (Port number on which the Caller is ready to receive media)
            Vector mediaDescriptions = getSessionDescription(sdp).getMediaDescriptions(true);
            
            MediaDescription mediaDescription = (MediaDescription) mediaDescriptions.get(0);
            Media media = mediaDescription.getMedia();
            
            // Caller Supported Audio Formats
            supportedFormats = media.getMediaFormats(true);
            
        } catch (SdpParseException e) {
            logger.error(e.getMessage(), e);
        } catch (SdpException e) {
            logger.error(e.getMessage(), e);
        }
        
        return supportedFormats;
    }
    
    /**
     *
     * Make explicit request to start the session.
     * Actual media transfer will not be triggered until
     * after the media streams have been confirmed in both directions.
     * For example this method would be typically called when the signaling
     * negotiation completes - in response to SIP ACK. However the media (RTP) stream
     * is established in parallel to the signaling negotiation. Usually the media stream
     * is initialized when the SIP OK response is sent to the SIP client, but transfer should
     * not begin until after the client sends SIP ACK. Between the media initialization stage and
     * the time when transfer can begin, there is a period of time when the media streams handshake occurs.
     * During the handshake, the server receives events from the client confirming readiness for receiving and
     * sending media packets. It is important to wait on this handshake to complete.
     *
     */
    public void startSession() {
        
        // set this flag so that the file is played when the media stream is confirmed in both directions
        isSessionStartRequested  = true;
        
        considerStartingSession();
        
    }
    
    /**
     * If media transfer has been requested explicitly
     * via call to startSession() and the media handshake
     * has completed, then begin transfer.
     *
     */
    private void considerStartingSession() {
        if (isSessionStartRequested && isMediaSessionReady) {
            SendStream sendStream;
            try {
                sendStream = rtpSessionManager.createSendStream(outboundFileDataSource, 0);
                sendStream.start();
                mediaListener.setProcessorStopped(false);
                outboundProcessor.start();
            } catch (UnsupportedFormatException e) {
                logger.warn("Failed to establish outbound media stream", e);
            } catch (IOException e) {
                logger.warn("Failed to establish outbound media stream", e);
            }
        }
    }
        
    public void stopSession() {
        mediaListener.setStopped(true);
        
        if (outboundProcessor != null) {
            outboundProcessor.stop();
            outboundProcessor.close();
            outboundProcessor = null;
            
            if (!(attribute.equalsIgnoreCase("sendonly"))) {
                mediaListener.stopRecording();
                rtpSessionManager.removeReceiveStreamListener(receiveStreamListener);
            }
            
            rtpSessionManager.removeTargets("Session ended.");
            rtpSessionManager.dispose();
        }
        
        attribute = null;
        negotiatedSdpFormatCode = null;
    }
    
    public ControllerListener getControllerListener() {
        return controllerListener;
    }
    
    public Processor getProcessor() {
        return outboundProcessor;
    }
    
    public String getSdpFormatConstant() {
        return negotiatedSdpFormatCode;
    }
    
    public String getSdpDescription() {
        return sdpDescription;
    }
    
    public String generateSdpDescription(int version, String userName,
            String sessionName) {
        String sdp = null;
        
        if (attribute != null) {
            try {
                SessionDescription sessDescr = sdpFactory.createSessionDescription();
                //"v= version"
                Version v = sdpFactory.createVersion(version);
                InetAddress publicIpAddress = localHost;
                String addrType = publicIpAddress instanceof Inet6Address ?
                    "IP6" : "IP4";
                
                //spaces in the user name mess everything up.
                //bug report - Alessandro Melzi
                /*
                        Origin o = sdpFactory.createOrigin(
                            "vmail_robot", 0, 0, "IN",
                            // TODO: vmail_robot should be replaced with a dynamic user name
                            addrType, publicIpAddress.getHostAddress());
                 */
                
//              It would be better to use publicIpAddress.getAddress()  as base for createing this.
                long tmpID=System.currentTimeMillis();
                Origin o = sdpFactory.createOrigin(userName, tmpID, tmpID, "IN", addrType, publicIpAddress.getHostAddress());
                
                
                //"s=-"
                SessionName s = sdpFactory.createSessionName(sessionName);
                //c=
                Connection c = sdpFactory.createConnection(
                        "IN",
                        addrType,
                        publicIpAddress.getHostAddress());
                //"t=0 0"
                TimeDescription t = sdpFactory.createTimeDescription();
                Vector timeDescs = new Vector();
                timeDescs.add(t);
                //--------Audio media description
                //make sure preferred formats come first
                String[] formats;
                
                if (attribute.equalsIgnoreCase("sendonly")) {
                    formats = new String[] {negotiatedSdpFormatCode};
                } else {
                    formats = new String[] {negotiatedSdpFormatCode, Integer.toString(dtmfPayload)};
                }
                
                MediaDescription am = sdpFactory.createMediaDescription(
                        "audio", srcPort, 1, "RTP/AVP", formats);
                
                
                createMediaAttributes(am, formats);
                am.setAttribute(attribute, null);
                
                Vector mediaDescs = new Vector();
                
                mediaDescs.add(am);
                
                sessDescr.setVersion(v);
                sessDescr.setOrigin(o);
                sessDescr.setConnection(c);
                sessDescr.setSessionName(s);
                sessDescr.setTimeDescriptions(timeDescs);
                
                if(mediaDescs.size() > 0)
                    sessDescr.setMediaDescriptions(mediaDescs);
                
                if (logger.isInfoEnabled()) {
                    logger.info("Generated SDP - " + sessDescr.toString());
                }
                
                sdp = sessDescr.toString();
            } catch (SdpException e) {
                logger.error("An SDP exception occurred while " +
                        "generating local sdp description", e);
//		        throw new MediaException(
//		            "An SDP exception occurred while generating local sdp description! " +
//		            e.getMessage());
            }
//		    catch (UnknownHostException e) {
//		        logger.log(Level.SEVERE,
//		    	logger.debug(
//		            "Failed to initialize the media binding address. ", e);
//		        throw new MediaException(
//		            "Failed to initialize the media binding address! " + e.getMessage());
//		    }
            
        } else {
            logger.debug("You need to create a Transmitter, Receiver or " +
                    "TransmitterReceiver before using generateSdpDescription method");
        }
        
        return sdp;
    }
    
    /**
     * Sets the URL of the audio file to transmit.
     * @param audioFile
     */
    private void setMediaLocator(URL audioFile) {
        outboundFileLocator = new MediaLocator(audioFile);
    }
    
    private SessionDescription getSessionDescription(String sdp)
    throws SdpParseException {
        
        if (sessionDescription == null) {
            String sdpRemoteData = sdp;
            sessionDescription = sdpFactory.createSessionDescription(sdpRemoteData);            
        }
        
        return sessionDescription;
    }
    
    private String createOutboundProcessor() {
        if (outboundFileLocator == null)
            return "Locator is null";
        
        // Try to create a processor to handle the input media locator
        try {
            outboundProcessor = javax.media.Manager.createProcessor(outboundFileLocator);
        } catch (NoProcessorException npe) {
            return "Couldn't create processor";
        } catch (IOException ioe) {
            return "IOException creating processor";
        }
        
        // Wait for it to configure
        boolean result = ((MediaListener) controllerListener).waitForState(outboundProcessor,
                Processor.Configured);
        
        if (result == false)
            return "Couldn't configure processor";
        
        // Get the tracks from the processor
        TrackControl [] tracks = outboundProcessor.getTrackControls();
        
        // Do we have at least one track?
        if (tracks == null || tracks.length < 1)
            return "Couldn't find tracks in processor";
        
        // Set the output content descriptor to RAW_RTP
        // This will limit the supported formats reported from
        // Track.getSupportedFormats to only valid RTP formats.
        ContentDescriptor cd = new ContentDescriptor(ContentDescriptor.RAW_RTP);
        outboundProcessor.setContentDescriptor(cd);
        
        Format supported[];
        Format chosen = null;
        boolean atLeastOneTrack = false;
        
        // Program the tracks.
        for (int i = 0; i < tracks.length; i++) {
            
            if (tracks[i].isEnabled()) {
                
                supported = tracks[i].getSupportedFormats();
                
                // We've set the output content to the RAW_RTP.
                // So all the supported formats should work with RTP.
                // We'll just pick the first one.
                
                if (supported.length > 0) {
                    Iterator clientFormatIter =  clientSupportedMediaFormats.iterator();
                    while (clientFormatIter.hasNext() && (chosen == null)) {
                        negotiatedSdpFormatCode = (String) clientFormatIter.next();
                        remoteClientFormat = convertSdpToJmfFormat(negotiatedSdpFormatCode);
                        
                        if(remoteClientFormat != null) {
                            for (int n = 0; (n < supported.length) & (chosen == null); n++) {
                                if (supported[n].matches(remoteClientFormat)) {
                                    chosen = supported[n];
                                }
                            }
                        }
                    }
                    
                    if (chosen != null) {
                        tracks[i].setFormat(chosen);
                        logger.info("Track " + i + " is set to transmit as:");
                        logger.info("  " + chosen);
                        atLeastOneTrack = true;
                    } else {
                        return "Cannot transmit using any format supported by the SIP Client";
                    }
                    
                } else
                    tracks[i].setEnabled(false);
            } else
                tracks[i].setEnabled(false);
        }
        
        if (!atLeastOneTrack)
            return "Couldn't set any of the tracks to a valid RTP format";
        
        // Realize the processor.
        result = ((MediaListener) controllerListener).waitForState(outboundProcessor,
                Controller.Realized);
        
        if (result == false)
            return "Couldn't realize processor";
        
        // Get the output data source of the processor
        outboundFileDataSource = outboundProcessor.getDataOutput();
        
        return OK;
    }
    
    
    private void createMediaAttributes(MediaDescription am, String[] formats) {
        String value;
        String payload;
        Vector attributes = new Vector();
        
        try {
            for (int i = 0; i < formats.length; i++) {
                payload = formats[i];
                value = payload + " " + getAttributeValue(payload);
                attributes.add(sdpFactory.createAttribute("rtpmap", value));
            }
            
            am.setAttributes(attributes);
            
        } catch (SdpException e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    private String getAttributeValue(String payload) {
        int sdpFormat = -1;
        
        try {
            sdpFormat = Integer.parseInt(payload);
        } catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
        
        switch (sdpFormat) {
            case SdpConstants.PCMU:
                return "pcmu/8000";
            case SdpConstants.GSM:
                return "gsm/8000";
            case SdpConstants.G723:
                return "g723/8000";
            case SdpConstants.DVI4_8000:
                return "dvi4/8000";
            case SdpConstants.DVI4_16000:
                return "dvi4/16000";
            case SdpConstants.PCMA:
                return "pcma/8000";
            case SdpConstants.G728:
                return "g728/8000";
            case SdpConstants.DVI4_11025:
                return "dvi4/11025";
            case SdpConstants.DVI4_22050:
                return "dvi4/22050";
            case SdpConstants.G729:
                return "g729/8000";
            case dtmfPayload:
                return "telephone-event/8000";
            default:
                return null;
        }
    }
    
    /**
     * Create an <CODE>RTPManager</CODE> object for the underlying
     * implementation class.
     */
    private RTPManager newRtpManager() {
        RTPManager rtpManager= null;
        Enumeration rtpSessionManagerList = getRTPManagerList().elements();
        Throwable thr = null;
        
        while(rtpSessionManagerList.hasMoreElements()) {
            String protoClassName = (String)rtpSessionManagerList.nextElement();
            
            try {
                Class protoClass = getClassForName(protoClassName);
                rtpManager = (RTPManager)protoClass.newInstance();
                
            } catch (ClassNotFoundException e) {
                thr = e;
            } catch (InstantiationException e) {
                thr = e;
            } catch (IllegalAccessException e) {
                thr = e;
            } catch (Exception e) {
                thr = e;
            } catch (Error e) {
                thr = e;
            }
            
            if( rtpManager != null) {
                break;
            }
        }
        if (rtpManager == null)
            logger.warn("Failed to instantiate RTPManager", thr);
        
        return rtpManager;
    }
    
    /**
     * Build a list of <CODE>RTPManager</CODE> implementation classes.
     * The implemenation class must be named 'RTPSessionMgr' and is
     * required to extend from javax.media.rtp.RTPManager.
     * <p>
     * The first name in the list will always be:
     * <blockquote><pre>
     * media.rtp.RTPSessionMgr
     * </pre></blockquote>
     * <p>
     */
    private static Vector getRTPManagerList() {
        // The first element is the name of the protocol handler ...
        String sourceName = "media.rtp.RTPSessionMgr";
        Vector prefix = new Vector();
        prefix.add("org.mobicents.slee");
        
        return buildClassList(prefix, sourceName);
    }
    
    
    private Format convertSdpToJmfFormat(String sdpFormatStr) {
        int sdpFormat = -1;
        
        try {
            sdpFormat = Integer.parseInt(sdpFormatStr);
        } catch (NumberFormatException ex) {
            return null;
        }
        switch (sdpFormat) {
            case SdpConstants.PCMU:
                return new AudioFormat(AudioFormat.ULAW_RTP, 8000.0, AudioFormat.NOT_SPECIFIED, AudioFormat.NOT_SPECIFIED);
            case SdpConstants.GSM:
                return new AudioFormat(AudioFormat.GSM_RTP, 8000.0, AudioFormat.NOT_SPECIFIED, AudioFormat.NOT_SPECIFIED);
            case SdpConstants.DVI4_8000:
                return new AudioFormat(AudioFormat.DVI_RTP, 8000.0, AudioFormat.NOT_SPECIFIED, AudioFormat.NOT_SPECIFIED);
            case SdpConstants.DVI4_11025:
                return new AudioFormat(AudioFormat.DVI_RTP, 11025.0, AudioFormat.NOT_SPECIFIED, AudioFormat.NOT_SPECIFIED);
            case SdpConstants.DVI4_22050:
                return new AudioFormat(AudioFormat.DVI_RTP, 22050.0, AudioFormat.NOT_SPECIFIED, AudioFormat.NOT_SPECIFIED);
            default:
                return null;
        }
    }
    
    
    /**
     * Build a list of complete class names.
     *<p>
     *
     * For each element of the prefix-list
     * the following element is added to the list:
     * <blockquote><pre>
     *    &lt;prefix&gt;.&lt;name&gt;
     * </pre></blockquote>
     * These are added to the list in the same order as the prefixes appear
     * in the prefix-list.
     * </ol>
     *
     * @param prefixList The list of prefixes to prepend to the class name.
     * @param name The name of the class to build the list for.
     * @return A vector of class name strings.
     */
    private static Vector buildClassList(Vector prefixList, String name) {
        // New list which has the name as the first element ...
        Vector classList = new Vector();
        
        // Try and instance one directly from the classpath
        // if it's there.
        // $jdr: This has been objected to as confusing,
        // the argument for it's inclusion is that it
        // gives the user (via the classpath) a way
        // of modifying the search list at run time
        // for all applications.
        classList.addElement(name);
        
        // ... for each prefix append the name and put it
        // in the class list ...
        Enumeration prefix = prefixList.elements();
        
        while(prefix.hasMoreElements()) {
            String prefixName = (String)prefix.nextElement();
            classList.addElement(prefixName + "." + name);
        }
        
        // ... done
        return classList;
    }
    
    private static Class getClassForName(String className)
    throws ClassNotFoundException {
        /**
         *  Note: if we don't want this functionality
         *  just replace it with Class.forName(className)
         */
        try {
            return Class.forName(className);
            
        } catch (Exception e) {
            if (!checkIfJDK12())
                throw new ClassNotFoundException(e.getMessage());
        } catch (Error e) {
            if (!checkIfJDK12()) {
                throw e;
            }
        }
        
        /**
         *  In jdk1.2 application, when you have jmf.jar in the ext directory and
         *  you want to access a class that is not in jmf.jar but is in the CLASSPATH,
         *  you have to load it using the system class loader.
         */
        try {
            return (Class) forName3ArgsM.invoke(Class.class, new Object[] {
                className, new Boolean(true), systemClassLoader});
            
        } catch (Throwable e) {}
        
        /**
         *  In jdk1.2 applet, when you have jmf.jar in the ext directory and
         *  you want to access a class that is not in jmf.jar but applet codebase,
         *  you have to load it using the the context class loader.
         */
        try {
            // TODO: may need to invoke RuntimePermission("getClassLoader") privilege
            ClassLoader contextClassLoader =
                    (ClassLoader) getContextClassLoaderM.invoke(Thread.currentThread(), null);
            
            return (Class) forName3ArgsM.invoke(Class.class, new Object[] {
                className, new Boolean(true), contextClassLoader});
            
        } catch (Exception e) {
            throw new ClassNotFoundException(e.getMessage());
        } catch (Error e) {
            throw e;
        }
    }
    
    private static boolean checkIfJDK12() {
        if (jdkInit)
            return (forName3ArgsM != null);
        
        jdkInit = true;
        
        try {
            forName3ArgsM = Class.class.getMethod("forName", new Class[] {
                String.class, boolean.class, ClassLoader.class});
            
            getSystemClassLoaderM = ClassLoader.class.getMethod("getSystemClassLoader", null);
            
            // TODO: may need to invoke RuntimePermission("getClassLoader") privilege
            systemClassLoader = (ClassLoader) getSystemClassLoaderM.invoke(ClassLoader.class, null);
            
            getContextClassLoaderM = Thread.class.getMethod("getContextClassLoader", null);
            
            return true;
            
        } catch (Throwable t) {
            forName3ArgsM = null;
            return false;
        }
    }
    
    /**
     * Notifies the media session
     * that the inbound stream is ready
     *
     */
    void inboundStreamInitialized() {
        if (!isMediaSessionReady) {
            isMediaSessionReady = true;
            considerStartingSession();
        }
    }
}

