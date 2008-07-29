/*
 * BaseEndpoint.java
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

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.mobicents.media.format.UnsupportedFormatException;
import org.mobicents.media.protocol.PushBufferStream;

import org.apache.log4j.Logger;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.Connection;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentReaderHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.sdp.AVProfile;
import org.mobicents.media.server.spi.MediaResource;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.ResourceUnavailableException;
import org.mobicents.media.server.spi.TooManyConnectionsException;
import org.mobicents.media.server.spi.UnknownMediaResourceException;
import org.mobicents.media.server.spi.events.NotifyEvent;
import org.mobicents.media.server.impl.common.*;
import org.mobicents.media.server.impl.common.events.*;
import org.mobicents.media.server.spi.MediaSink;

/**
 * The basic implementation of the endpoint.
 * 
 * An BaseEndpoint is a logical representation of a physical entity, such as an
 * analog phone or a channel in a trunk. Endpoints are sources or sinks of data
 * and can be physical or virtual. Physical endpoint creation requires hardware
 * installation while software is sufficient for creating a virtual
 * BaseEndpoint. An interface on a gateway that terminates a trunk connected to
 * a PSTN switch is an example of a physical BaseEndpoint. An audio source in an
 * audio-content server is an example of a virtual BaseEndpoint.
 * 
 * @author Oleg Kulikov.
 */
public abstract class BaseEndpoint implements Endpoint {

	private String localName;
	private transient Timer timer = new Timer();
	private InetAddress bindAddress;
	protected int packetizationPeriod;
	protected int jitter;
	private boolean hasConnections;
	protected int lowPortNumber;
	protected int highPortNumber;
	private ConcurrentReaderHashMap connections = new ConcurrentReaderHashMap();
	private int maxConnections = 0;
	private ArrayList<NotificationListener> listeners = new ArrayList();
	private HashMap resources = new HashMap();
	protected HashMap formats = new HashMap();
	private HashMap configurations = new HashMap();
	private BaseResourceManager resourceManager;
	private String stunServerAddress;
	private int stunServerPort;
	private boolean useStun = false;
	private boolean usePortMapping = true;

	private String publicAddressFromStun = null;

	private transient Logger logger = Logger.getLogger(BaseEndpoint.class);

	protected static Timer connectionTimer = new Timer();

	public BaseEndpoint(String localName) {
		this.localName = localName;
		this.resourceManager = initResourceManager();
	}

	public Timer getTimer() {
		return timer;
	}

	public String getStunServerAddress() {
		return stunServerAddress;
	}

	public void setStunServerAddress(String stunServerAddress) {
		this.stunServerAddress = stunServerAddress;
	}

	public int getStunServerPort() {
		return stunServerPort;
	}

	public boolean isUsePortMapping() {
		return usePortMapping;
	}

	public void setUsePortMapping(boolean usePortMapping) {
		this.usePortMapping = usePortMapping;
	}

	public String getPublicAddressFromStun() {
		return publicAddressFromStun;
	}

	public void setPublicAddressFromStun(String publicAddressFromStun) {
		this.publicAddressFromStun = publicAddressFromStun;
	}

	public void setStunServerPort(int stunServerPort) {
		this.stunServerPort = stunServerPort;
	}

	public boolean isUseStun() {
		return useStun;
	}

	public void setUseStun(boolean useStun) {
		this.useStun = useStun;
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.spi.Endpoint#getLocalName();
	 */
	public String getLocalName() {
		return localName;
	}

	/**
	 * Gets the maximum amount of the connections that endpoint can implement.
	 * 
	 * @return the maximum available connections.
	 */
	public int getMaxConnectionsAvailable() {
		return maxConnections;
	}

	/**
	 * Sets the maximum amount of the connections that endpoint can implement.
	 * 
	 * @return the maximum available connections.
	 */
	public void setMaxConnectionsAvailable(int amount) {
		this.maxConnections = amount;
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.spi.Endpoint#getLocalName);
	 */
	public InetAddress getBindAddress() {
		return bindAddress;
	}

	public void setBindAddress(InetAddress bindAddress) {
		this.bindAddress = bindAddress;
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.spi.Endpoint#getPcketizationPeriod();
	 */
	public Integer getPacketizationPeriod() {
		return packetizationPeriod;
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.spi.Endpoint#setPcketizationPeriod(Integer);
	 */
	public void setPacketizationPeriod(Integer period) {
		packetizationPeriod = period;
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.spi.Endpoint#getJitter();
	 */
	public Integer getJitter() {
		return jitter;
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.spi.Endpoint#setJitter(Integer);
	 */
	public void setJitter(Integer jitter) {
		this.jitter = jitter;
	}

	/**
	 * Gets the supported formats.
	 * 
	 * @return the map where key is an RTP payload number and value is a format
	 *         instance.
	 */
	public HashMap getFormats() {
		return formats;
	}

	public BaseResourceManager initResourceManager() {
		return new BaseResourceManager();
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.spi.Endpoint#setDefaultConfig(MediaResourceType,
	 *      Properties);
	 */
	public void setDefaultConfig(MediaResourceType type, Properties config) {
		configurations.put(type, config);
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.spi.Endpoint#getDefaultConfig(MediaResourceType);
	 */
	public Properties getDefaultConfig(MediaResourceType type) {
		return (Properties) configurations.get(type);
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.spi.Endpoint#configure(String,
	 *      Properties);
	 */
	public void configure(MediaResourceType type, Properties config) throws UnknownMediaResourceException {
		MediaResource mediaResource = resourceManager.getResource(this, type, config);
		resources.put(type, mediaResource);
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.spi.Endpoint#configure(String, String,
	 *      Properties);
	 */
	public synchronized void configure(MediaResourceType type, Connection connection, Properties config)
			throws UnknownMediaResourceException {
		MediaResource mediaResource = resourceManager.getResource(this, type, connection, config);

		if (mediaResource == null) {
			return;
		}

		try {
			mediaResource.configure(config);
			resources.put(type + "_" + connection.getId(), mediaResource);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Cold not configure resource " + type + ", connection = " + connection, e);
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.spi.Endpoint#configure(String, String,
	 *      Properties);
	 */
	public synchronized void configure(MediaResourceType type, String connectionID, Properties config)
			throws UnknownMediaResourceException {
		Connection connection = this.getConnection(connectionID);
		MediaResource mediaResource = resourceManager.getResource(this, type, connection, config);

		if (mediaResource == null) {
			return;
		}

		try {
			mediaResource.configure(config);
			resources.put(type + "_" + connection.getId(), mediaResource);
		} catch (Exception e) {
			logger.error("Cold not configure resource " + type + ", connection = " + connection, e);
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	public synchronized void prepare(MediaResourceType type, String connectionID, PushBufferStream media)
			throws UnsupportedFormatException {
		MediaSink res = (MediaSink) resources.get(type + "_" + connectionID);
		if (res != null) {
			logger.info("Preparing Sink: " + res);
			res.prepare(this, media);
		}
	}

	public void addFormat(int pt, Format fmt) {
		formats.put(pt, fmt);
	}

	public void removeFormat(Format fmt) {
		formats.remove(getPayload(fmt));
	}

	public void setPCMU(int payload) {
		formats.put(payload, AVProfile.PCMU);
	}

	public int getPCMU() {
		return this.getPayload(AVProfile.PCMU);
	}

	public void setPCMA(int payload) {
		formats.put(payload, AVProfile.PCMA);
	}

	public int getPCMA() {
		return this.getPayload(AVProfile.PCMA);
	}

	protected int getPayload(Format fmt) {
		Collection<Integer> list = formats.values();
		for (Integer payload : list) {
			Format format = (Format) formats.get(payload);
			if (fmt.matches(format)) {
				return payload;
			}
		}
		return -1;
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.spi.Endpoint#getPortRange();
	 */
	public String getPortRange() {
		return lowPortNumber + "-" + highPortNumber;
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.spi.Endpoint#setPortRange(String);
	 */
	public void setPortRange(String portRange) {
		String[] tokens = portRange.split("-");
		lowPortNumber = Integer.parseInt(tokens[0]);
		highPortNumber = Integer.parseInt(tokens[1]);
	}

	/**
	 * Indicates that endpoint has connections.
	 * 
	 * @return true if endpoint executing one or more connection and false
	 *         otherwise.
	 */
	public boolean hasConnections() {
		return this.hasConnections;
	}

	/**
	 * Gets connection with specified identifier.
	 * 
	 * @param connectionID
	 *            the identifier of the connection to return
	 */
	public BaseConnection getConnection(String connectionID) {
		return (BaseConnection) connections.get(connectionID);
	}

	/**
	 * Gets all connections which are executed by this endpoint.
	 * 
	 * @return collection of BaseConnection objects.
	 */
	public Collection<BaseConnection> getConnections() {
		return connections.values();
	}

	public Object getResource(MediaResourceType type, String connectionID) {
		return resources.get(type + "_" + connectionID);
	}

	public Object getResource(MediaResourceType type) {
		return resources.get(type);
	}

	/**
	 * Used for internal connection creation.
	 * 
	 * @param endpoint
	 *            the endpoint which creates connection.
	 * @param mode
	 *            the mode of connection been created.
	 * @return Object implementing connection. *
	 * @throws org.mobicents.media.server.spi.ResourceUnavailableException
	 */
	private Connection doCreateConnection(Endpoint endpoint, ConnectionMode mode) throws ResourceUnavailableException {
		return new BaseConnection(endpoint, mode);
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.spi.Endpoint#createConnection(int);
	 */
	public synchronized Connection createConnection(ConnectionMode mode) throws TooManyConnectionsException,
			ResourceUnavailableException {
		hasConnections = true;
		try {
			if (connections.size() == maxConnections) {
				throw new TooManyConnectionsException("Maximum " + maxConnections + " connections allowed");
			}

			// Connection connection = new BaseConnection(this, mode);
			Connection connection = doCreateConnection(this, mode);
			connections.put(connection.getId(), connection);

			return connection;
		} finally {
			hasConnections = connections.size() > 0;
		}
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.spi.Endpoint#deleteConnection();
	 */
	public synchronized void deleteConnection(String connectionID) {
		Connection connection = (Connection) connections.remove(connectionID);

		if (connection != null) {
			connection.close();
		}

		hasConnections = connections.size() > 0;
		logger.info("Deleted connection " + connection);

		// clean all resources associated with this connection
		Set<String> names = resources.keySet();
		List<String> connectionResources = new ArrayList();

		for (String name : names) {
			if (name.endsWith(connection.getId())) {
				connectionResources.add(name);
				MediaResource mediaResource = (MediaResource) resources.get(name);
				if (logger.isDebugEnabled()) {
					logger.debug("Releasing resource: " + mediaResource);
				}
				mediaResource.stop();
				mediaResource.release();
			}
		}

		for (String name : connectionResources) {
			MediaResource mediaResource = (MediaResource) resources.remove(name);
			if (logger.isDebugEnabled()) {
				logger.debug("Disposed resource: " + mediaResource);
			}
		}
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.spi.Endpoint#deleteAllConnections();
	 */
	public synchronized void deleteAllConnections() {
		Iterator list = connections.values().iterator();
		while (list.hasNext()) {
			Connection connection = (Connection) list.next();
			deleteConnection(connection.getId());
		}
	}

	/**
	 * Adds a Listener.
	 * 
	 * @param listener
	 *            the listener instance to add.
	 */
	public void addNotifyListener(NotificationListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes a listener.
	 * 
	 * @param listener
	 *            the listener instance for remove.
	 */
	public void removeNotifyListener(NotificationListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Sends specified event to registered listeners.
	 * 
	 * @param event
	 *            the event to be sent.
	 */
	public synchronized void sendEvent(NotifyEvent event) {
		for (NotificationListener listener : listeners) {
			listener.update(event);
		}
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.spi.Endpoint#detect(int,
	 *      NotificationListener, boolean);
	 */
	public void subscribe(EventID eventID, NotificationListener listener, boolean persistent) {
		EventTrigger eventDetector = new EventTrigger(this, eventID, listener, persistent);
		this.addNotifyListener(eventDetector);
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.spi.Endpoint#detect(int,
	 *      NotificationListener, boolean);
	 */
	public void subscribe(EventID eventID, String connectionID, String params[], NotificationListener listener) {
	}

}
