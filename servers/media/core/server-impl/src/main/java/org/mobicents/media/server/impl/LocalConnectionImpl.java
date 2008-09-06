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

import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SessionDescription;

import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.common.ConnectionMode;
import org.mobicents.media.server.impl.common.ConnectionState;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.ResourceUnavailableException;

/**
 * 
 * @author Oleg Kulikov
 */
public class LocalConnectionImpl extends BaseConnection {

    private SessionDescription localSDP;
    private SessionDescription remoteSDP;
    private LocalConnectionImpl otherConnection;
    private SdpFactory sdpFactory = SdpFactory.getInstance();

    /**
     * Creates a new instance of RtpConnectionImpl.
     * 
     * @param endpoint
     *            the endpoint executing this connection.
     * @param mode
     *            the mode of this connection.
     */
    public LocalConnectionImpl(Endpoint endpoint, ConnectionMode mode) throws ResourceUnavailableException {
        super(endpoint, mode);
        logger = Logger.getLogger(LocalConnectionImpl.class);
        setState(ConnectionState.HALF_OPEN);
    }

    @Override
    protected void setState(ConnectionState newState) {
        ConnectionState oldState = this.state;
        this.state = newState;

        switch (state) {
            case HALF_OPEN:
                break;
            case OPEN:
                System.out.println("OTHER =" + otherConnection);
                demux.getInput().connect(otherConnection.mux.getOutput());
                otherConnection.mux.getOutput().start();
                break;
            case CLOSED:
                if (otherConnection != null) {
                    otherConnection.mux.getOutput().stop();
                    demux.getInput().disconnect(otherConnection.mux.getOutput());
                }
//                if (otherConnection != null && 
//                        otherConnection.getState() == ConnectionState.OPEN) {
//                    otherConnection.otherConnection = null;
//                    otherConnection.setState(ConnectionState.CLOSED);
//                }
//                otherConnection = null;
                break;

        }
        super.setState(newState);
    }

    /**
     * Gets the reference to a connection joined with this one.
     * 
     * @return connection object or null if this connection not joined.
     */
    public Connection getOtherParty() {
        return this.otherConnection;
    }

    /**
     * (Non-Javadoc).
     * 
     * @see org.mobicents.media.server.spi.Connection#getLocalDescriptor();
     */
    public String getLocalDescriptor() {
        if (state == ConnectionState.NULL || state == ConnectionState.CLOSED) {
            throw new IllegalStateException("State is " + state);
        }

        String userName = "MediaServer";
        long sessionID = System.currentTimeMillis() & 0xffffff;
        long sessionVersion = sessionID;

        String networkType = javax.sdp.Connection.IN;
        String addressType = "EPN";
        String address = endpoint.getLocalName();
        try {
            localSDP = sdpFactory.createSessionDescription();
            localSDP.setVersion(sdpFactory.createVersion(0));
            localSDP.setOrigin(sdpFactory.createOrigin(userName, sessionID, sessionVersion, networkType, addressType, address));
            localSDP.setSessionName(sdpFactory.createSessionName("session"));
            localSDP.setConnection(sdpFactory.createConnection(networkType, addressType, address));

        /*            Vector descriptions = new Vector();
        // encode formats
        HashMap fmts = codecs != null ? codecs : audioFormats;
        Object[] payloads = getPayloads(fmts).toArray();
        int[] formats = new int[payloads.length];
        for (int i = 0; i < formats.length; i++) {
        formats[i] = ((Integer) payloads[i]).intValue();
        }
        // generate media descriptor
        MediaDescription md = sdpFactory.createMediaDescription("audio", 0, 1, "RTP/AVP", formats);
        boolean g729 = false;
        // set attributes for formats
        Vector attributes = new Vector();
        for (int i = 0; i < formats.length; i++) {
        Format format = (Format) fmts.get(new Integer(formats[i]));
        attributes.add(sdpFactory.createAttribute("rtpmap", format.toString()));
        if (format.getEncoding().contains("g729")) {
        g729 = true;
        }
        }
        if (g729) {
        attributes.add(sdpFactory.createAttribute("fmtp", "18 annexb=no"));
        }
        // generate descriptor
        md.setAttributes(attributes);
        descriptions.add(md);
        localSDP.setMediaDescriptions(descriptions);
         */
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
     * (Non-Javadoc).
     * 
     * @see org.mobicents.media.server.spi.Connection#setRemoteDescriptor();
     */
    public void setRemoteDescriptor(String descriptor) throws SdpException, IOException {
        if (state != ConnectionState.HALF_OPEN && state != ConnectionState.OPEN) {
            throw new IllegalStateException("State is " + state);
        }
        setState(ConnectionState.OPEN);
    }

    /**
     * (Non-Javadoc).
     * 
     * @throws InterruptedException
     * 
     * @see org.mobicents.media.server.spi.Connection#setRemoteDescriptor();
     */
    public void setOtherParty(Connection other) throws IOException {
        try {
            this.lockState();
            if (other != null) {
                if (otherConnection == other) {
                    return;
                }

                this.otherConnection = (LocalConnectionImpl) other;
                setState(ConnectionState.OPEN);

                if (otherConnection.getState() != ConnectionState.OPEN) {
                    otherConnection.setOtherParty(this);
                }
            } else if (otherConnection != null) {
                setState(ConnectionState.CLOSED);
                if (otherConnection.getState() != ConnectionState.CLOSED) {
                    otherConnection.close();
                }
                otherConnection = null;
            }
        } catch (InterruptedException e) {
            setState(ConnectionState.CLOSED);
        } finally {
            this.releaseState();
        }
    }

    @Override
    public void close() {
        try {
            setOtherParty(null);
        } catch (IOException e) {
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
        return "(connectionID=" + id + ", endpoint=" + endpointName + ", state=" + state + ")";
    }

    public void error(Exception e) {
        logger.error("Facility error", e);
    }
}
