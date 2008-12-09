/*
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
package org.mobicents.media.server.impl.enp.cnf;

import java.util.Collection;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.BaseConnection;
import org.mobicents.media.server.impl.BaseVirtualEndpoint;
import org.mobicents.media.server.impl.Demultiplexer;
import org.mobicents.media.server.impl.Generator;
import org.mobicents.media.server.impl.events.announcement.AudioPlayer;
import org.mobicents.media.server.impl.events.connection.parameters.ConnectionParametersGenerator;
import org.mobicents.media.server.impl.events.dtmf.BaseDtmfDetector;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionListener;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.ConnectionState;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.events.pkg.Announcement;
import org.mobicents.media.server.spi.events.pkg.ConnectionParameters;

/**
 * 
 * @author Oleg Kulikov
 */
public class ConfEndpointImpl extends BaseVirtualEndpoint implements ConnectionListener {

	private transient Logger logger = Logger.getLogger(ConfEndpointImpl.class);
	private HashMap mixers = new HashMap();

	// private transient Logger logger =
	// Logger.getLogger(ConfEndpointImpl.class);
	public ConfEndpointImpl(String localName, HashMap<String, Endpoint> endpointsMap) {
		super(localName, endpointsMap);
		this.setMaxConnectionsAvailable(1000);
		this.addConnectionListener(this);
	}

	@Override
	public Endpoint doCreateEndpoint(String localName) {
		return new ConfEndpointImpl(localName, super.endpoints);
	}

	@Override
	public HashMap initMediaSources() {
		HashMap map = new HashMap();
		// init audio player
		map.put(Generator.AUDIO_PLAYER, new AudioPlayer());
		map.put(Generator.CONNECTION_PARAMETERS, new ConnectionParametersGenerator());
		return map;
	}

	@Override
	public HashMap initMediaSinks() {
		HashMap map = new HashMap();
		// init audio player
		// map.put(Generator.AUDIO_RECORDER, new Recorder(""));
		map.put(Generator.DTMF_DETECTOR, new BaseDtmfDetector());

		return map;
	}

	/**
	 * Attaches connection's receiver stream to other connections.
	 * 
	 * @param connection
	 *            the connection which receiver stream will be attached.
	 */
	private void attachReceiver(Connection connection) {
		Demultiplexer demux = ((BaseConnection) connection).getDemux();
		Collection<Connection> connections = this.getConnections();
		for (Connection conn : connections) {
			if (!conn.getId().equals(connection.getId())) {
				AudioMixer mixer = (AudioMixer) mixers.get(conn.getId());
				if (mixer != null) {
					demux.connect(mixer);
				}
			}
		}
	}

	/**
	 * Attach receiver streams from "otrher" connections to current mixer.
	 * Register mixer as secondary source for current primary source.
	 * 
	 * @param connection
	 *            the connection which mixer will be used to attach to.
	 */
	private void attachSender(Connection connection) {
		Collection<Connection> connections = getConnections();
		AudioMixer mixer = new AudioMixer(connection.getId());
		mixers.put(connection.getId(), mixer);

		// detach player from MUX and attach to Mixer.
		AudioPlayer player = (AudioPlayer) this.getMediaSource(Generator.AUDIO_PLAYER, connection);
		player.disconnect(((BaseConnection) connection).getMux());
		mixer.connect(player);

		mixer.start();

		for (Connection conn : connections) {
			if (!conn.getId().equals(connection.getId())) {
				((BaseConnection) conn).getDemux().connect(mixer);
			}
		}

		((BaseConnection) connection).getMux().connect(mixer.getOutput());
	}

	public void detachReceiver(Connection connection) {
		// Mixer exist only when ConnectionState is OPEN

		Demultiplexer demux = ((BaseConnection) connection).getDemux();
		Collection<Connection> connections = getConnections();
		for (Connection conn : connections) {
			if (!(conn.getState().equals(ConnectionState.HALF_OPEN)) && !conn.getId().equals(connection.getId())) {
				AudioMixer mixer = (AudioMixer) mixers.get(conn.getId());
				demux.disconnect(mixer);
			}

		}
	}

	public void detachSender(Connection connection) {
		// Mixer exist only when ConnectionState is OPEN
		AudioMixer mixer = (AudioMixer) mixers.get(connection.getId());
		if (mixer != null) {
			mixer.stop();

			Collection<Connection> connections = getConnections();

			for (Connection conn : connections) {
				if (!conn.getId().equals(connection.getId())) {
					((BaseConnection) conn).getDemux().disconnect(mixer);
				}
			}
		}

	}

	public synchronized void onStateChange(Connection connection, ConnectionState oldState) {
		switch (connection.getState()) {
		// endpoint can receive media, so all existing mixers should
		// be registered as secondary sources for primary source.
		case HALF_OPEN:
			if (logger.isDebugEnabled()) {
				logger.debug("localName=" + getLocalName() + ", Attaching receiver");
			}
			attachReceiver(connection);
			break;
		case OPEN:
			if (logger.isDebugEnabled()) {
				logger.debug("localName=" + getLocalName() + ", Attaching sender");
			}
			attachSender(connection);
			break;
		case CLOSED:
			if (logger.isDebugEnabled()) {
				logger.debug("localName=" + getLocalName() + ", Detaching receiver");
			}
			detachReceiver(connection);
			if (logger.isDebugEnabled()) {
				logger.debug("localName=" + getLocalName() + ", Detaching sender");
			}
			detachSender(connection);
			break;
		}
	}

	public String[] getSupportedPackages() {
		return new String[] { Announcement.PACKAGE_NAME, org.mobicents.media.server.spi.events.pkg.DTMF.PACKAGE_NAME ,ConnectionParameters.PACKAGE_NAME};
	}

	public void onModeChange(Connection connection, ConnectionMode oldMode) {
	}
}
