/*
 * BaseConnection.java
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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.format.UnsupportedFormatException;
import org.mobicents.media.protocol.PushBufferStream;
import javax.sdp.MediaDescription;
import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SessionDescription;

import org.apache.log4j.Logger;
import org.jboss.util.id.UID;
import org.mobicents.media.server.impl.sdp.RTPFormat;
import org.mobicents.media.server.impl.common.ConnectionMode;
import org.mobicents.media.server.impl.common.ConnectionState;
import org.mobicents.media.server.impl.rtp.AdaptorListener;
import org.mobicents.media.server.impl.rtp.RtpSocketAdaptor;
import org.mobicents.media.server.impl.rtp.RtpSocketAdaptorImpl;
import org.mobicents.media.server.impl.rtp.SendStream;
import org.mobicents.media.server.spi.*;
import org.mobicents.media.server.spi.dtmf.DTMF;
import org.mobicents.media.server.impl.common.*;
import org.mobicents.media.server.impl.common.dtmf.*;
/**
 * 
 * @author Oleg Kulikov
 */
public class BaseConnection implements Connection, AdaptorListener {

	private final static Random rnd = new Random();
	private String id;
	private int port;
	private int period = 20;
	private BaseEndpoint endpoint;
	private String endpointName;
	private ConnectionMode mode;
	private ArrayList<ConnectionListener> listeners = new ArrayList();
	private SessionDescription localSDP;
	private SessionDescription remoteSDP;
	private BaseConnection otherConnection;
	private RtpSocketAdaptor rtpSocket;
	private AudioFormat audioFormat = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1);
	private DTMFType dtmfFormat = DTMFType.INBAND;
	private SdpFactory sdpFactory = SdpFactory.getInstance();
	private HashMap codecs;
	private ConnectionState state = ConnectionState.NULL;
	private transient Logger logger = Logger.getLogger(BaseConnection.class);

	/**
	 * Creates a new instance of BaseConnection.
	 * 
	 * @param endpoint
	 *            the endpoint executing this connection.
	 * @param mode
	 *            the mode of this connection.
	 */
	public BaseConnection(Endpoint endpoint, ConnectionMode mode)
			throws ResourceUnavailableException {
		this.id = genID();
		this.mode = mode;

		this.endpoint = (BaseEndpoint) endpoint;
		this.endpointName = endpoint.getLocalName();

		rtpSocket = new RtpSocketAdaptorImpl(this.endpoint.packetizationPeriod,
				this.endpoint.jitter);
		try {
			port = rtpSocket.init(endpoint.getBindAddress(),
					this.endpoint.lowPortNumber, this.endpoint.highPortNumber);
			rtpSocket.addAdaptorListener(this);

			if (logger.isDebugEnabled()) {
				logger.debug(this + " Bound RTP socket to "
						+ endpoint.getBindAddress() + ":" + port);
			}

			rtpSocket.start();
		} catch (SocketException e) {
			logger.error(this + "Fail while binding RTP socket", e);
			throw new ResourceUnavailableException(e.getMessage());
		}
	}

	/**
	 * Generates unique identifier for this connection.
	 * 
	 * @return hex view of the unique integer.
	 */
	private String genID() {
		return (new UID()).toString();
	}

	/**
	 * (Non-Javadoc).
	 * 
	 * @see org.mobicents.media.server.spi.Connection#getId();
	 */
	public String getId() {
		return id;
	}

	/**
	 * (Non-Javadoc).
	 * 
	 * @see org.mobicents.media.server.spi.Connection#getMode();
	 */
	public ConnectionMode getMode() {
		return mode;
	}

	/**
	 * (Non-Javadoc).
	 * 
	 * @see org.mobicents.media.server.spi.Connection#setMode(int);
	 */
	public void setMode(ConnectionMode mode) {
		this.mode = mode;
		// @todo rebuilt send/recv streams.
	}

	/**
	 * (Non-Javadoc).
	 * 
	 * @see org.mobicents.media.server.spi.Connection#getEndpoint(int);
	 */
	public Endpoint getEndpoint() {
		return endpoint;
	}

	public DTMFType getDtmfFormat() {
		return this.dtmfFormat;
	}

	public ConnectionState getState() {
		return this.state;
	}

	private void setState(ConnectionState newState) {
		ConnectionState oldState = this.state;
		this.state = newState;
		for (ConnectionListener cl : this.getListeners()) {
			cl.onStateChange(this, oldState);
		}

	}

	/**
	 * (Non-Javadoc).
	 * 
	 * @see org.mobicents.media.server.spi.Connection#getLocalDescriptor();
	 */
	public String getLocalDescriptor() {
		String userName = "MediaServer";
		long sessionID = System.currentTimeMillis() & 0xffffff;
		long sessionVersion = sessionID;

		String networkType = javax.sdp.Connection.IN;
		String addressType = javax.sdp.Connection.IP4;
		String address = endpoint.getBindAddress().getHostAddress();

		try {
			localSDP = sdpFactory.createSessionDescription();
			localSDP.setVersion(sdpFactory.createVersion(0));
			localSDP.setOrigin(sdpFactory.createOrigin(userName, sessionID,
					sessionVersion, networkType, addressType, address));
			localSDP.setSessionName(sdpFactory.createSessionName("session"));
			localSDP.setConnection(sdpFactory.createConnection(networkType,
					addressType, address));

			Vector descriptions = new Vector();

			// encode formats
			HashMap fmts = codecs != null ? codecs : endpoint.getFormats();
			Object[] payloads = getPayloads(fmts).toArray();

			int[] formats = new int[payloads.length];
			for (int i = 0; i < formats.length; i++) {
				formats[i] = ((Integer) payloads[i]).intValue();
			}

			// generate media descriptor
			MediaDescription md = sdpFactory.createMediaDescription("audio",
					port, 1, "RTP/AVP", formats);

			// set attributes for formats
			Vector attributes = new Vector();
			for (int i = 0; i < formats.length; i++) {
				Format format = (Format) fmts.get(new Integer(formats[i]));
				attributes.add(sdpFactory.createAttribute("rtpmap", format
						.toString()));
			}

			// generate descriptor
			md.setAttributes(attributes);
			descriptions.add(md);

			localSDP.setMediaDescriptions(descriptions);
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
	 * Gets used audio format.
	 * 
	 * @return format used by this connection.
	 */
	public AudioFormat getAudioFormat() {
		return this.audioFormat;
	}

	/**
	 * Extracts address and port of the remote party.
	 * 
	 * @param sdp
	 *            session description.
	 * @return socket address of the remote party.
	 */
	private InetSocketAddress getPeer(SessionDescription sdp)
			throws SdpException {
		javax.sdp.Connection connection = sdp.getConnection();

		Vector list = remoteSDP.getMediaDescriptions(false);
		MediaDescription md = (MediaDescription) list.get(0);

		try {
			InetAddress address = InetAddress
					.getByName(connection.getAddress());
			int localPort = md.getMedia().getMediaPort();
			return new InetSocketAddress(address, localPort);
		} catch (UnknownHostException e) {
			throw new SdpException(e);
		}
	}

	/**
	 * (Non-Javadoc).
	 * 
	 * @see org.mobicents.media.server.spi.Connection#setRemoteDescriptor();
	 */
	public void setRemoteDescriptor(String descriptor) throws SdpException,
			IOException {
		remoteSDP = sdpFactory.createSessionDescription(descriptor);

		// add peer to RTP socket
		InetSocketAddress peer = getPeer(remoteSDP);
		rtpSocket.setPeer(peer.getAddress(), peer.getPort());

		if (logger.isDebugEnabled()) {
			logger.debug(this + " Set peer: " + peer);
		}

		// negotiate codecs
		HashMap offer = RTPFormat.getFormats(remoteSDP);
		if (logger.isDebugEnabled()) {
			logger.debug(this + " Offered formats: " + offer);
		}

		codecs = select(endpoint.getFormats(), offer);
		if (logger.isDebugEnabled()) {
			logger.debug(this + " Selected formats: " + codecs);
		}

		if (codecs.size() == 0) {
			throw new IOException("Codecs are not negotiated");
		}

		this.audioFormat = (AudioFormat) getDefaultAudioFormat(codecs);

		if (logger.isDebugEnabled()) {
			logger.debug(this
					+ " Codecs are negotiated, default audio format : "
					+ this.audioFormat);
		}

		applyCodecs(rtpSocket, codecs);

		try {
			this.period = getPacketizationPeriod(remoteSDP);
		} catch (Exception e) {
		}

		// configuring DTMF detector if supported
		try {
			Properties dtmfConfig = getDTMFConfig(codecs);
			if (logger.isDebugEnabled()) {
				logger.debug(this + " DTMF config: " + dtmfConfig);
			}
			endpoint.configure(MediaResourceType.DTMF_DETECTOR, this,
					dtmfConfig);
		} catch (UnknownMediaResourceException e) {
		}

		// refresh output stream
		if (mode != ConnectionMode.RECV) {
			new Thread(new Sender(this)).start();
		}
	}

	private int getPacketizationPeriod(SessionDescription sd)
			throws SdpException {
		MediaDescription md = (MediaDescription) sd.getMediaDescriptions(false)
				.get(0);
		String value = md.getAttribute("ptime");
		return Integer.parseInt(value);

	}

	private Format getDefaultAudioFormat(HashMap codecs) {
		Collection payloads = this.getPayloads(codecs);
		Integer pt = (Integer) payloads.iterator().next();
		return (Format) codecs.get(pt);
	}

	public void addListener(ConnectionListener listener) {
		listeners.add(listener);
	}

	public void removeListener(ConnectionListener listener) {
		listeners.remove(listener);
	}

	public Collection<ConnectionListener> getListeners() {
		return listeners;
	}

	/**
	 * (Non-Javadoc).
	 * 
	 * @see org.mobicents.media.server.spi.Connection#setRemoteDescriptor();
	 */
	public void setOtherParty(Connection other) throws IOException {
		otherConnection = (BaseConnection) other;
		if (mode != ConnectionMode.RECV) {
			new Thread(new Sender(this)).start();
		}
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

	/**
	 * Selects codecs shared between supported and offered.
	 * 
	 * @param supported
	 *            the map with the supported codecs.
	 * @param offered
	 *            the list with the offered codecs.
	 */
	private HashMap select(HashMap supported, HashMap offered) {
		HashMap formats = new HashMap();
		Set<Integer> offer = offered.keySet();
		for (Integer po : offer) {
			Format ofmt = (Format) offered.get(po);
			Set<Integer> local = supported.keySet();
			for (Integer pl : local) {
				Format sfmt = (Format) supported.get(pl);
				if (sfmt.matches(ofmt)) {
					formats.put(po, sfmt);
				}
			}
		}
		return formats;
	}

	/**
	 * Apply specified codecs for RTP manager.
	 * 
	 * @param manager
	 *            the RTP manager.
	 * @param codecs
	 *            the list of codecs.
	 */
	private void applyCodecs(RtpSocketAdaptor rtpSocket, HashMap codecs) {
		Set<Integer> payloads = codecs.keySet();
		for (Integer p : payloads) {
			Format fmt = (Format) codecs.get(p);
			rtpSocket.addFormat(p, fmt);
		}
	}

	private Properties getDTMFConfig(HashMap codecs) {
		Properties properties = new Properties();

		boolean is2833 = false;
		Collection<Format> list = codecs.values();
		for (Format fmt : list) {
			if (fmt.getEncoding().equals("telephone-event")) {
				is2833 = true;
				break;
			}
		}

		if (is2833) {
			properties.setProperty("dtmf.format", "rfc2833");
		} else {
			properties.setProperty("dtmf.format", "inband");
		}

		return properties;
	}

	/**
	 * Releases all resources requested by this connection.
	 */
	public void close() {
		if (logger.isDebugEnabled()) {
			logger.debug(this + " Close RTP socket");
		}
		synchronized (this.state) {
			this.setState(ConnectionState.CLOSED);
		}
		rtpSocket.close();
		this.endpoint = null;
	}

	/**
	 * This method is called when new receive stream is initialized.
	 * 
	 * @param stream
	 *            the new receive stream.
	 */
	public void newReceiveStream(PushBufferStream stream) {
		synchronized (this.state) {
			Format fmt = stream.getFormat();
			if (logger.isDebugEnabled()) {
				logger.debug(this + " New receive stream: " + fmt);
			}

			// do nothing if mode is SEND_ONLY
			if (this.mode == ConnectionMode.SEND) {
				logger.warn(this + " Unexpected media stream, mode=SEND_ONLY");
				return;
			}

			// prepare RFC 2833 DTFM detector
			if (fmt.getEncoding().equals("telephone-event")) {
				try {
					endpoint.prepare(MediaResourceType.DTMF_DETECTOR, getId(),
							stream);
					return;
				} catch (UnsupportedFormatException e) {
					logger.error("Could not prepare DTMF detector: ", e);
				}
			}

			// prepare audio processing
			if (fmt instanceof AudioFormat) {
				try {
					endpoint
							.configure(MediaResourceType.AUDIO_SINK, this, null);
					if (logger.isDebugEnabled()) {
						logger.debug(this + " Configured audio sink");
					}

					MediaSink audioSink = (MediaSink) endpoint.getResource(
							MediaResourceType.AUDIO_SINK, getId());
					if (audioSink == null) {
						return;
					}

					audioSink.prepare(stream);
					if (logger.isDebugEnabled()) {
						logger.debug(this + " Prepared audio sink");
					}

					// FIXME: Other media type has to utilize this.
					// If we are only RECV connection - we transit from NULL to
					// OPEN, if not, to half open - in case we are SEND_RECV
					if (getState() == ConnectionState.NULL) {
						if (getMode() == ConnectionMode.RECV) {
							this.setState(ConnectionState.OPEN);
						} else {
							this.setState(ConnectionState.HALF_OPEN);
						}

					} else if (getState() == ConnectionState.HALF_OPEN) {
						this.setState(ConnectionState.OPEN);
					} else {
						// ERROR?
					}

					// audioSink.start();
				} catch (UnknownMediaResourceException e) {
					logger.error("Unexpected error", e);
				} catch (UnsupportedFormatException e) {
					logger.error("Could not initialize audio sink", e);
					return;
				}
			}
		}
	}

	/**
	 * Gets the text representation of the connection.
	 * 
	 * @return text representation of the connection.
	 */
	@Override
	public String toString() {
		return "(connectionID=" + id + ", endpoint=" + endpointName + ")";
	}

	public void error(Exception e) {
		logger.error("Facility error", e);
	}

	private class Sender implements Runnable {

		private BaseConnection connection;

		public Sender(BaseConnection connection) {
			this.connection = connection;
		}

		private void startTransmission(PushBufferStream stream) {
			if (otherConnection != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("Start transmission  " + stream
							+ " to local connection " + otherConnection);
				}
				otherConnection.newReceiveStream(stream);
				return;
			}

			try {
				SendStream sendStream = rtpSocket.createSendStream(stream);
				sendStream.start();
				if (logger.isDebugEnabled()) {
					logger.debug("Start transmission  " + stream
							+ " to remote peer:" + getPeer(remoteSDP));
				}
			} catch (UnsupportedFormatException e) {
			} catch (SdpException e) {
			}
		}

		public void run() {
			// start audio
			synchronized (state) {
				try {
					Properties config = new Properties();
					config
							.put("conf.connection.format",
									connection.audioFormat);

					if (logger.isDebugEnabled()) {
						logger.debug(connection + " Configuring audio source:");
					}
					endpoint.configure(MediaResourceType.AUDIO_SOURCE,
							connection, config);

					if (logger.isDebugEnabled()) {
						logger.debug(connection + " Preparing audio source:");
					}
					MediaSource audioSource = (MediaSource) endpoint
							.getResource(MediaResourceType.AUDIO_SOURCE,
									getId());

					if (getState() == ConnectionState.NULL) {
						if (getMode() == ConnectionMode.SEND) {
							setState(ConnectionState.OPEN);
						} else {
							setState(ConnectionState.HALF_OPEN);
						}
					} else if (getState() == ConnectionState.HALF_OPEN) {
						setState(ConnectionState.OPEN);
					} else {
						// ERROR?
					}

					startTransmission(audioSource.prepare());

					// audioSource.start();
				} catch (UnknownMediaResourceException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
