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
import java.util.Set;
import java.util.TimerTask;
import java.util.Vector;

import javax.sdp.MediaDescription;
import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SessionDescription;

import org.apache.log4j.Logger;
import org.jboss.util.id.UID;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.format.UnsupportedFormatException;
import org.mobicents.media.protocol.PushBufferStream;
import org.mobicents.media.server.impl.common.ConnectionMode;
import org.mobicents.media.server.impl.common.ConnectionState;
import org.mobicents.media.server.impl.common.MediaResourceType;
import org.mobicents.media.server.impl.common.dtmf.DTMFType;
import org.mobicents.media.server.impl.rtp.AdaptorListener;
import org.mobicents.media.server.impl.rtp.RtpSocketAdaptor;
import org.mobicents.media.server.impl.rtp.RtpSocketAdaptorImpl;
import org.mobicents.media.server.impl.rtp.SendStream;
import org.mobicents.media.server.impl.sdp.AVProfile;
import org.mobicents.media.server.impl.sdp.RTPAudioFormat;
import org.mobicents.media.server.impl.sdp.RTPFormat;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionListener;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.MediaSource;
import org.mobicents.media.server.spi.ResourceUnavailableException;
import org.mobicents.media.server.spi.UnknownMediaResourceException;

import EDU.oswego.cs.dl.util.concurrent.QueuedExecutor;
import EDU.oswego.cs.dl.util.concurrent.ReentrantLock;

/**
 * 
 * @author Oleg Kulikov
 */
public class BaseConnection implements Connection, AdaptorListener {

	public static int CONNECTION_TIMEOUT = 30;

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
	private QueuedExecutor eventQueue = new QueuedExecutor();
	private ReentrantLock stateLock = new ReentrantLock();
	private TimerTask closeTask;
	private boolean timerStarted = false;

	private transient Logger logger = Logger.getLogger(BaseConnection.class);

	private class CloseConnectionTask extends TimerTask {
		public void run() {
			logger.info("Connection timer expired, Disconnecting");
			timerStarted = false;
			try {
				close();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Creates a new instance of BaseConnection.
	 * 
	 * @param endpoint
	 *            the endpoint executing this connection.
	 * @param mode
	 *            the mode of this connection.
	 */
	public BaseConnection(Endpoint endpoint, ConnectionMode mode) throws ResourceUnavailableException {
		this.id = genID();
		this.mode = mode;

		this.endpoint = (BaseEndpoint) endpoint;
		this.endpointName = endpoint.getLocalName();
		this.closeTask = new CloseConnectionTask();

		if (logger.isDebugEnabled()) {
			logger.debug(this + " Initializing audio formats");
		}
		initAudioFormats();

		if (logger.isDebugEnabled()) {
			logger.debug(this + " Initializing RTP stack");
		}

		initRTPSocket();
		BaseEndpoint.connectionTimer.schedule(closeTask, 60 * CONNECTION_TIMEOUT * 1000);
		timerStarted = true;

		if (mode != ConnectionMode.SEND_ONLY) {
			if (logger.isDebugEnabled()) {
				logger.debug(this + " Configuring primary media sink");
			}
			configurePrimarySink();
		}

		setState(ConnectionState.HALF_OPEN);
	}

	/**
	 * Provides intialization of the RTP stack.
	 * 
	 * @throws org.mobicents.media.server.spi.ResourceUnavailableException
	 */
	private void initRTPSocket() throws ResourceUnavailableException {
		if (this.endpoint.isUseStun()) {
			rtpSocket = new RtpSocketAdaptorImpl(endpoint.packetizationPeriod, endpoint.jitter, endpoint.getStunServerAddress(), endpoint.getStunServerPort(), endpoint
					.isUsePortMapping(), endpoint.getPublicAddressFromStun());
		} else {
			rtpSocket = new RtpSocketAdaptorImpl(endpoint.packetizationPeriod, endpoint.jitter);
		}
		try {
			port = rtpSocket.init(endpoint.getBindAddress(), endpoint.lowPortNumber, endpoint.highPortNumber);
			rtpSocket.addAdaptorListener(this);

			if (logger.isDebugEnabled()) {
				logger.debug(this + " Bound RTP socket to " + endpoint.getBindAddress() + ":" + port);
			}

			rtpSocket.start();
		} catch (SocketException e) {
			logger.error(this + "Fail while binding RTP socket", e);
			throw new ResourceUnavailableException(e.getMessage());
		}
	}

	/**
	 * Initializes supported RTP audio formats.
	 */
	private void initAudioFormats() {
		// if DTMF mode is RFC2833 add telephone-event format to the list of
		// supported formats. The default payload type is 101.
		Properties config = endpoint.getDefaultConfig(MediaResourceType.DTMF_DETECTOR);
		if (config != null) {
			DTMFType dtmfMode = DTMFType.valueOf(config.getProperty("detector.mode"));
			if (dtmfMode != DTMFType.INBAND) {
				int pt = 101; // default value
				try {
					pt = Integer.parseInt(config.getProperty("dtmf.payload"));
				} catch (Exception e) {
				}
				endpoint.addFormat(pt, AVProfile.DTMF_FORMAT);
			}
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

	/**
	 * Gets the current state of this connection.
	 * 
	 * @return current state;
	 */
	public ConnectionState getState() {
		return this.state;
	}

	private void setState(ConnectionState newState) {
		if (logger.isDebugEnabled()) {
			logger.debug(this + " set state = " + newState);
		}
		ConnectionState oldState = this.state;
		this.state = newState;
		try {
			eventQueue.execute(new StateNotificator(this, oldState));
		} catch (InterruptedException e) {
		}
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
		String addressType = javax.sdp.Connection.IP4;
		String address = null;
		int audioPort = 0;
		RtpSocketAdaptorImpl rtpSocketImpl = (RtpSocketAdaptorImpl) this.rtpSocket;
		if (!rtpSocketImpl.isUseStun()) {
			address = endpoint.getBindAddress().getHostAddress();
			audioPort = this.port;
		} else {
			address = rtpSocketImpl.getPublicAddressFromStun();
			audioPort = rtpSocketImpl.getPublicPortFromStun();
		}
		try {
			localSDP = sdpFactory.createSessionDescription();
			localSDP.setVersion(sdpFactory.createVersion(0));
			localSDP.setOrigin(sdpFactory.createOrigin(userName, sessionID, sessionVersion, networkType, addressType, address));
			localSDP.setSessionName(sdpFactory.createSessionName("session"));
			localSDP.setConnection(sdpFactory.createConnection(networkType, addressType, address));

			Vector descriptions = new Vector();

			// encode formats
			HashMap fmts = codecs != null ? codecs : endpoint.getFormats();
			Object[] payloads = getPayloads(fmts).toArray();

			int[] formats = new int[payloads.length];
			for (int i = 0; i < formats.length; i++) {
				formats[i] = ((Integer) payloads[i]).intValue();
			}

			// generate media descriptor
			MediaDescription md = sdpFactory.createMediaDescription("audio", port, 1, "RTP/AVP", formats);

			// set attributes for formats
			Vector attributes = new Vector();
			for (int i = 0; i < formats.length; i++) {
				Format format = (Format) fmts.get(new Integer(formats[i]));
				attributes.add(sdpFactory.createAttribute("rtpmap", format.toString()));
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
	private InetSocketAddress getPeer(SessionDescription sdp) throws SdpException {
		javax.sdp.Connection connection = sdp.getConnection();

		Vector list = sdp.getMediaDescriptions(false);
		MediaDescription md = (MediaDescription) list.get(0);

		try {
			InetAddress address = InetAddress.getByName(connection.getAddress());
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
	public void setRemoteDescriptor(String descriptor) throws SdpException, IOException {
		if (state != ConnectionState.HALF_OPEN && state != ConnectionState.OPEN) {
			throw new IllegalStateException("State is " + state);
		}

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

		// @FIXME
		// DTMF may be negotiated but speech codecs no
		if (codecs.size() == 0) {
			throw new IOException("Codecs are not negotiated");
		}

		this.audioFormat = (AudioFormat) getDefaultAudioFormat(codecs);

		if (logger.isDebugEnabled()) {
			logger.debug(this + " Codecs are negotiated, default audio format : " + this.audioFormat);
		}

		applyCodecs(rtpSocket, codecs);

		try {
			this.period = getPacketizationPeriod(remoteSDP);
		} catch (Exception e) {
			// silence here
		}

		// configuring DTMF detector if supported
		try {
			Properties dtmfConfig = getCurrentDTMFConfig(codecs);
			if (logger.isDebugEnabled()) {
				logger.debug(this + " DTMF config: " + dtmfConfig);
			}
			endpoint.configure(MediaResourceType.DTMF_DETECTOR, this, dtmfConfig);
		} catch (UnknownMediaResourceException e) {
			logger.error("UnknownMediaResourceException while configuring the DTMF for Endpoint", e);
		}

		configurePrimarySource();
		setState(ConnectionState.OPEN);
		startSendStream();
	}

	/**
	 * Configures primary media source if required.
	 * 
	 * @throws java.io.IOException
	 */
	private void configurePrimarySource() throws IOException {
		if (mode != ConnectionMode.RECV_ONLY) {
			Properties config = new Properties();
			config.put("conf.connection.format", audioFormat);

			try {
				endpoint.configure(MediaResourceType.AUDIO_SOURCE, this, config);
			} catch (UnknownMediaResourceException e) {
				throw new IOException("Unknown media source");
			}
		}
	}

	/**
	 * Configures primary sink if required.
	 * 
	 * @throws org.mobicents.media.server.spi.ResourceUnavailableException
	 */
	private void configurePrimarySink() throws ResourceUnavailableException {
		try {
			endpoint.configure(MediaResourceType.AUDIO_SINK, this, null);
		} catch (UnknownMediaResourceException e) {
			throw new ResourceUnavailableException(e.getMessage());
		}
	}

	/**
	 * Starts send stream
	 * 
	 * @throws java.io.IOException
	 */
	private void startSendStream() {
		if (mode != ConnectionMode.RECV_ONLY) {
			new Thread(new Sender(this)).start();
		}
	}

	/**
	 * Gets packetization period encoded in session descriptor.
	 * 
	 * @param sd
	 *            the session descriptor
	 * @return value of the packetization period in milliseconds.
	 * @throws javax.sdp.SdpException
	 */
	private int getPacketizationPeriod(SessionDescription sd) throws Exception {
		MediaDescription md = (MediaDescription) sd.getMediaDescriptions(false).get(0);
		String value = md.getAttribute("ptime");
		return Integer.parseInt(value);

	}

	/**
	 * Gets the format which will be used as default for audio transmission.
	 * Note. Format of the receive stream may differ from this one.
	 * 
	 * @param codecs
	 *            the list of negotiated codecs
	 * @return AudioFormat object.
	 */
	private Format getDefaultAudioFormat(HashMap codecs) {
		Collection payloads = this.getPayloads(codecs);
		Integer pt = (Integer) payloads.iterator().next();
		return (Format) codecs.get(pt);
	}

	/**
	 * (Non-Javadoc).
	 * 
	 * @see org.mobicents.media.server.spi.Connection#addListener(ConnectionListener)
	 */
	public void addListener(ConnectionListener listener) {
		listeners.add(listener);
	}

	/**
	 * (Non-Javadoc).
	 * 
	 * @see org.mobicents.media.server.spi.Connection#removeListener(ConnectionListener)
	 */
	public void removeListener(ConnectionListener listener) {
		listeners.remove(listener);
	}

	/**
	 * (Non-Javadoc).
	 * 
	 * @throws InterruptedException
	 * 
	 * @see org.mobicents.media.server.spi.Connection#setRemoteDescriptor();
	 */
	public void setOtherParty(Connection other) throws IOException, InterruptedException {
		this.lockState();
		try {
			// synchronized (this.state) {
			otherConnection = (BaseConnection) other;
			if (logger.isDebugEnabled()) {
				logger.debug(this + " Configuring primary media source");
			}
			configurePrimarySource();

			setState(ConnectionState.OPEN);

			if (logger.isDebugEnabled()) {
				logger.debug(this + " Starting send stream");
			}
			startSendStream();
		} finally {
			this.releaseState();
		}
		// }
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
					formats.put(po, ofmt);
				}
			}
		}
		return formats;
	}

	/**
	 * Apply specified codecs to RTP socket.
	 * 
	 * @param rtpSocket
	 *            the RTP socket.
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

	/**
	 * Gets the negotiated DTMF configuration.
	 * 
	 * @param codecs
	 *            the list of negotiated formats.
	 * @return DTMF config;
	 */
	private Properties getCurrentDTMFConfig(HashMap codecs) {
		Properties config = endpoint.getDefaultConfig(MediaResourceType.DTMF_DETECTOR);

		boolean is2833 = false;
		int payload = 101;

		Collection<Format> list = codecs.values();
		for (Format fmt : list) {
			if (fmt.getEncoding().equals("telephone-event")) {
				is2833 = true;
				payload = ((RTPAudioFormat) fmt).getPayload();
				break;
			}
		}

		if (is2833) {
			config.setProperty("detector.mode", "RFC2833");
			config.setProperty("payload.type", Integer.toString(payload));
		} else {
			config.setProperty("detector.mode", "INBAND");
		}

		return config;
	}

	/**
	 * Releases all resources requested by this connection.
	 * 
	 * @throws InterruptedException
	 */
	public void close() throws InterruptedException {
		// synchronized (this.state) {
		this.lockState();
		try {
			if (timerStarted) {
				closeTask.cancel();
				BaseEndpoint.connectionTimer.purge();
			}

			if (logger.isDebugEnabled()) {
				logger.debug(this + " Close RTP socket");
			}

			this.setState(ConnectionState.CLOSED);

			rtpSocket.close();
			this.endpoint = null;
			// }
		} finally {
			this.releaseState();
		}
	}

	/**
	 * This method is called when new receive stream is initialized.
	 * 
	 * @param stream
	 *            the new receive stream.
	 * @throws InterruptedException 
	 */
	public void newReceiveStream(PushBufferStream stream) throws InterruptedException {
		// synchronized (this.state) {

		this.lockState();
		try {
			Format fmt = stream.getFormat();

			if (logger.isDebugEnabled()) {
				logger.debug(this + " New receive stream: " + fmt);
			}

			// do nothing if mode is SEND_ONLY
			if (this.mode == ConnectionMode.SEND_ONLY) {
				logger.warn(this + " Unexpected media stream, mode=SEND_ONLY");
				return;
			}

			try {
				if (fmt.getEncoding().equals("telephone-event")) {
					endpoint.prepare(MediaResourceType.DTMF_DETECTOR, getId(), stream);
				} else if (fmt instanceof AudioFormat) {
					endpoint.prepare(MediaResourceType.AUDIO_SINK, getId(), stream);
				}
			} catch (UnsupportedFormatException e) {
				logger.error("Could not initialize media resource", e);
			}
			// }
		} finally {
			this.releaseState();
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

	private class StateNotificator implements Runnable {
		private Connection connection;
		private ConnectionState oldState;

		public StateNotificator(Connection connection, ConnectionState oldState) {
			this.connection = connection;
			this.oldState = oldState;
		}

		public void run() {
			for (ConnectionListener cl : listeners) {
				cl.onStateChange(connection, oldState);
			}
		}
	}

	private class Sender implements Runnable {

		private BaseConnection connection;

		public Sender(BaseConnection connection) {
			this.connection = connection;
		}

		private void startTransmission(PushBufferStream stream) throws InterruptedException {
			if (otherConnection != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("Start transmission  " + stream + " to local connection " + otherConnection);
				}
				otherConnection.newReceiveStream(stream);
				return;
			}

			try {
				SendStream sendStream = rtpSocket.createSendStream(stream);
				sendStream.start();
				if (logger.isDebugEnabled()) {
					logger.debug("Start transmission  " + stream + " to remote peer:" + getPeer(remoteSDP));
				}
			} catch (UnsupportedFormatException e) {
				logger.error("UnsupportedFormatException while starting the transmission", e);
				setState(ConnectionState.CLOSED);
			} catch (SdpException e) {
				setState(ConnectionState.CLOSED);
				logger.error("SdpException while starting the transmission", e);
			}
		}

		public void run() {
			// start audio
			synchronized (state) {
				if (logger.isDebugEnabled()) {
					logger.debug(connection + " Preparing audio source:");
				}
				MediaSource audioSource = (MediaSource) endpoint.getResource(MediaResourceType.AUDIO_SOURCE, getId());

				try {
					startTransmission(audioSource.prepare(getEndpoint()));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void lockState() throws InterruptedException {
		this.stateLock.acquire();
	}

	public void releaseState() {
		this.stateLock.release();
	}

}
