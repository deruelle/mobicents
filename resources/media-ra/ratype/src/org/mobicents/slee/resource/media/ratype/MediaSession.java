package org.mobicents.slee.resource.media.ratype;

import java.net.InetAddress;
import java.net.URL;
import java.util.Vector;

import javax.media.ControllerListener;
import javax.media.Processor;

public interface MediaSession {

	public final String OK = "ok";
	
	public String getSessionId() ;
    
    public void initSession() ;
    
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
            URL fileRcv, boolean dtmf);
    
    /**
     * To transmit audio stream.
     *
     * @param sdp
     * @param fileTx Route of the audio file you want to transmit.
     * @return null if everything went well, if not returns the error message.
     */
    public String createTransmitter(String sdp, URL fileTx) ;
    
    /**
     * Play announcement from specified URL.
     *
     * @param file the URL to file to play.
     * @return null if everything went well, if not returns the error message.
     */
    public String play(URL file) ;
    
    /**
     * Record data stream to file with specified URL.
     *
     * @param file the URL to record to
     * @return null if everything went well, if not returns the error message.
     */
    public String record(URL file) ;
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
    public String createReceiver(String sdp, URL fileRcv, boolean dtmf) ;
    
    public boolean isProcessorStopped() ;
    
    public void startRecording() ;
    
    /**
     * Gets the Audio Source Port.
     */
    public int getSrcPort() ;
    
    /**
     * Gets the Audio Destiny Port.
     * @param sdp
     * @return
     */
    public int getDstPort(String sdp) ;
    
    /**
     * Gets the Local Host Address.
     * @return localHost
     */
    public InetAddress getLocalHost() ;
    
    /**
     * Gets the Remote Host Address.
     * @param sdp
     * @return remoteHost
     */
    public InetAddress getRemoteHost(String sdp) ;
    
    /**
     * Gets Media Formats of Media Description information
     * sent in SDP by client.
     * @param inviteRequest
     * @return supportedFormats
     */
    public Vector getClientSupportedFormats(String sdp) ;
    
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
    public void startSession() ;
    
   
    
    public void stopSession() ;
    
    public ControllerListener getControllerListener() ;
    
    public Processor getProcessor() ;
    
    public String getSdpFormatConstant() ;
    
    public String getSdpDescription();
    
    public String generateSdpDescription(int version, String userName,
            String sessionName);   
    
   
}
