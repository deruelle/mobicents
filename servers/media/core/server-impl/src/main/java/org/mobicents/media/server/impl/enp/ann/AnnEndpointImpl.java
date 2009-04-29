/*
 * AnnEndpointImpl.java
 *
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
package org.mobicents.media.server.impl.enp.ann;

import org.mobicents.media.Component;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.impl.MediaResource;
import org.mobicents.media.server.impl.events.announcement.AudioPlayer;
import org.mobicents.media.server.impl.rtp.RtpSocket;
import org.mobicents.media.server.spi.Connection;

import org.mobicents.media.server.impl.BaseConnection;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.resource.Multiplexer;
import org.mobicents.media.server.impl.events.dtmf.DtmfGenerator;
import org.mobicents.media.server.impl.rtp.RtpFactory;
import org.mobicents.media.server.spi.ResourceUnavailableException;

/**
 * Implements Announcement access point.
 * 
 * @author Oleg Kulikov
 */
public class AnnEndpointImpl extends BaseEndpoint {

    private final static Format[] FORMATS = new Format[]{LINEAR_AUDIO, PCMA, PCMU, SPEEX, GSM};
    
    private AudioPlayer audioPlayer;
    private transient DtmfGenerator dtmfGenerator;
    private Multiplexer mux;
    
    private RtpSocket rtpSocket;
    
    /**
     * Creates a new instance of AnnEndpointImpl
     * 
     * @param localName the local name of the endpoint.
     * @param endpointsMap 
     */
    public AnnEndpointImpl(String localName) throws Exception {
        super(localName);
        setMaxConnectionsAvailable(1);
    }

    public Format[] getFormats() {
        return audioPlayer.getFormats();
    }
    
    @Override
    public void start() throws ResourceUnavailableException {
        super.start();
        
        startRtp();
        startPrimarySource();
        
        narrow(rtpSocket.getRtpMap(), getFormats());
        
        dtmfGenerator = new DtmfGenerator(this) ;
        dtmfGenerator.connect(mux);
    }
    
    protected void startRtp() throws ResourceUnavailableException {
        try {
            RtpFactory rtpFactory = getRtpFactory();
            rtpSocket = rtpFactory.getRTPSocket();
        } catch (Exception e) {
            throw new ResourceUnavailableException(e.getMessage());
        }
    }
    
    protected void startPrimarySource() {
        audioPlayer = new AudioPlayer(this);
        mux = new Multiplexer("");
        mux.connect(audioPlayer);
    }
    
    @Override
    public void stop() {
        rtpSocket.close();        
        mux.disconnect(audioPlayer);        
        mux.dispose();
        audioPlayer.dispose();
    }
    

    @Override
    public MediaSink getPrimarySink(Connection connection) {
        return null;
    }

    @Override
    public MediaSource getPrimarySource(Connection connection) {
        return mux.getOutput();
    }
    
    @Override
    protected MediaSource getMediaSource(MediaResource id, Connection connection) {
        if (id == MediaResource.AUDIO_PLAYER) {
            return audioPlayer;
        } else if (id == MediaResource.DTMF_GENERATOR) {
            return dtmfGenerator;
        }
        return null;
    }

    @Override
    public void allocateMediaSources(Connection connection, Format[] formats) {
        audioPlayer.addListener((BaseConnection)connection);
        dtmfGenerator.configure(formats);
        dtmfGenerator.addListener((BaseConnection)connection);
    }

    @Override
    public void allocateMediaSinks(Connection connection) {
    }

    @Override
    protected MediaSink getMediaSink(MediaResource id, Connection connection) {
        return null;
    }

    @Override
    public void releaseMediaSources(Connection connection) {
        audioPlayer.stop();
        audioPlayer.removeListener((BaseConnection)connection);
        dtmfGenerator.removeListener((BaseConnection)connection);
    }

    @Override
    public void releaseMediaSinks(Connection connection) {
    }

    @Override
    public RtpSocket allocateRtpSocket(Connection connection) throws ResourceUnavailableException {
        return rtpSocket;
    }

    @Override
    public void deallocateRtpSocket(RtpSocket rtpSocket, Connection connection) {
    }

    public Component getComponent(int resourceID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
