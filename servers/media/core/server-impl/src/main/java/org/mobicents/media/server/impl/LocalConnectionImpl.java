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
import org.mobicents.media.server.impl.rtp.RtpSocketImpl;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.ConnectionState;
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
     * @param connectionID the unique identifier of the connection to be created
     * @param mode
     *            the mode of this connection.
     */
    public LocalConnectionImpl(Endpoint endpoint, ConnectionMode mode) throws ResourceUnavailableException {
        super(endpoint, mode);
        logger = Logger.getLogger(LocalConnectionImpl.class);
        setState(ConnectionState.HALF_OPEN);
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
        try {
            super.lockState();
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

            /*
             * Vector descriptions = new Vector(); // encode formats HashMap
             * fmts = codecs != null ? codecs : audioFormats; Object[]
             * payloads = getPayloads(fmts).toArray(); int[] formats = new
             * int[payloads.length]; for (int i = 0; i < formats.length;
             * i++) { formats[i] = ((Integer) payloads[i]).intValue(); } //
             * generate media descriptor MediaDescription md =
             * sdpFactory.createMediaDescription("audio", 0, 1, "RTP/AVP",
             * formats); boolean g729 = false; // set attributes for formats
             * Vector attributes = new Vector(); for (int i = 0; i <
             * formats.length; i++) { Format format = (Format) fmts.get(new
             * Integer(formats[i]));
             * attributes.add(sdpFactory.createAttribute("rtpmap",
             * format.toString())); if
             * (format.getEncoding().contains("g729")) { g729 = true; } } if
             * (g729) { attributes.add(sdpFactory.createAttribute("fmtp",
             * "18 annexb=no")); } // generate descriptor
             * md.setAttributes(attributes); descriptions.add(md);
             * localSDP.setMediaDescriptions(descriptions);
             */
            } catch (SdpException e) {
                logger.error("Could not create descriptor", e);
            }
            return localSDP.toString();
        } catch (InterruptedException e) {
            logger.error("Could not create descriptor", e);
            return null;
        } finally {
            super.releaseState();
        }
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
    public void setRemoteDescriptor(String descriptor) throws SdpException, IOException, ResourceUnavailableException {
        try {
            this.lockState();
            if (state != ConnectionState.HALF_OPEN && state != ConnectionState.OPEN) {
                throw new IllegalStateException("State is " + state);
            }
            if (state != ConnectionState.HALF_OPEN && state != ConnectionState.OPEN) {
                throw new IllegalStateException("State is " + state);
            }
            setState(ConnectionState.OPEN);
        } catch (InterruptedException e) {
            logger.error("Failed to lock connection due to exception, possibly server is shutting down.");
            e.printStackTrace();
            //FIXME: baranowb: shouldnt we close here instead?
            throw new ResourceUnavailableException(e);
        } finally {
            this.releaseState();
        }
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
            if (other == null) {
                throw new IllegalArgumentException("Other party can not be null");
            }

            if (state == ConnectionState.CLOSED) {
                throw new IllegalStateException("Connection is closed");
            }

            if (otherConnection == other) {
                return;
            }

            this.otherConnection = (LocalConnectionImpl) other;
            demux.getInput().connect(otherConnection.mux.getOutput());
            if (otherConnection.getMode() == ConnectionMode.SEND_ONLY || otherConnection.getMode() == ConnectionMode.SEND_RECV) {
                otherConnection.mux.getOutput().start();
            }
            
            if (getMode() == ConnectionMode.RECV_ONLY || getMode() == ConnectionMode.SEND_RECV) {
                demux.start();
            }    
            setState(ConnectionState.OPEN);
            otherConnection.setOtherParty(this);
        } catch (InterruptedException e) {
            logger.error("Could not set other party", e);
            close();
        } catch (Exception e) {
            logger.error("Could not set other party", e);
            throw new IOException(e.getMessage());
        } finally {
            this.releaseState();
        }
    }

    @Override
    public void close() {
        LocalConnectionImpl other = otherConnection;
        if (otherConnection != null) {
            otherConnection = null;
            demux.getInput().disconnect(other.mux.getOutput());
            other.close();
        }
        super.close();
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
        endpoint.deleteConnection(id);
        logger.error("Facility error", e);
    }

    public String getOtherEnd() throws IllegalArgumentException {
        try {

            if (otherConnection != null) {
                return otherConnection.getEndpoint().getLocalName();
            }
        } catch (NullPointerException e) {
            logger.error("Could not get other end", e);
        }
        return null;
    }
    
    public void setGatherStats(boolean gatherStats) {
		super.gatherStats = gatherStats;
		super.packetsReceived=0;
		super.packetsSent=0;
		super.octetsReceived=0;
		super.octetsSent=0;
		super.interArrivalJitter=0;
		super.packetsLost=0;
		if(super.gatherStats)
		{
			//We have to put ourselves into path
			this.demux.setWorkDataSink(this);
			this.mux.setWorkDataSink(this);
		}else
		{
			this.demux.setWorkDataSink(null);
			this.mux.setWorkDataSink(null);
		}
		
	}
    
}


