package org.mobicents.javax.media.mscontrol.mediagroup;

import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import javax.media.mscontrol.MediaConfig;
import javax.media.mscontrol.MediaObject;
import javax.media.mscontrol.MsControlException;
import javax.media.mscontrol.Parameter;
import javax.media.mscontrol.Parameters;
import javax.media.mscontrol.join.Joinable;
import javax.media.mscontrol.mediagroup.MediaGroup;
import javax.media.mscontrol.mediagroup.Player;
import javax.media.mscontrol.mediagroup.Recorder;
import javax.media.mscontrol.mediagroup.signals.SignalDetector;
import javax.media.mscontrol.mediagroup.signals.SignalGenerator;
import javax.media.mscontrol.resource.Action;
import javax.media.mscontrol.resource.AllocationEventListener;

import org.apache.log4j.Logger;
import org.mobicents.javax.media.mscontrol.AbstractJoinableContainer;
import org.mobicents.javax.media.mscontrol.MediaConfigImpl;
import org.mobicents.javax.media.mscontrol.MediaObjectState;
import org.mobicents.javax.media.mscontrol.MediaSessionImpl;
import org.mobicents.javax.media.mscontrol.ParametersImpl;
import org.mobicents.javax.media.mscontrol.mediagroup.signals.SignalDetectorImpl;
import org.mobicents.javax.media.mscontrol.resource.ExtendedParameter;
import org.mobicents.jsr309.mgcp.MgcpWrapper;

/**
 * 
 * @author amit bhayani
 * 
 */
public class MediaGroupImpl extends AbstractJoinableContainer implements MediaGroup {
	public static Logger logger = Logger.getLogger(MediaGroupImpl.class);

	private String IVR_ENDPOINT_NAME = null;
	private URI uri = null;
	protected Player player = null;
	protected SignalDetector detector = null;
	protected Recorder recorder = null;
	protected SignalGenerator generator = null;
	public ConnectionIdentifier thisConnId = null;

	private MediaConfigImpl config = null;
	private Parameters parameters = null;

	public MediaGroupImpl(MediaSessionImpl mediaSession, MgcpWrapper mgcpWrapper, MediaConfigImpl config)
			throws MsControlException {
		this.mediaSession = mediaSession;
		this.mgcpWrapper = mgcpWrapper;
		this.maxJoinees = 1;
		this.config = config;

		this.endpoint = (String) config.getParameters().get(ExtendedParameter.ENDPOINT_LOCAL_NAME);
		this.IVR_ENDPOINT_NAME = this.endpoint;

		if (config.isPlayer()) {
			this.player = new PlayerImpl(this, mgcpWrapper, config);

		}

		if (config.isRecorder()) {
			this.recorder = new RecorderImpl(this, mgcpWrapper, config);

		}

		if (config.isSignaldetector()) {
			this.detector = new SignalDetectorImpl(this, mgcpWrapper, config);

		}

		if (config.isSignalgenerator()) {

		}

		try {
			this.uri = new URI(mediaSession.getURI().toString() + "/MediaGroup." + this.id);
		} catch (URISyntaxException e) {
			logger.warn(e);
		}
	}

	public MediaGroupImpl(MediaSessionImpl mediaSession, MgcpWrapper mgcpWrapper, MediaConfigImpl config,
			Parameters params) throws MsControlException {
		this(mediaSession, mgcpWrapper, config);
		this.parameters = params;
	}

	// MediaGroup Methods
	public Player getPlayer() throws MsControlException {
		if (this.player != null) {
			checkState();
			return player;

		} else {
			throw new MsControlException(this.uri + " This MediaGroup contains no Player");
		}

	}

	public Recorder getRecorder() throws MsControlException {
		if (this.recorder != null) {
			checkState();
			return this.recorder;

		} else {
			throw new MsControlException(this.uri + " This MediaGroup contains no Recorder");
		}

	}

	public SignalDetector getSignalDetector() throws MsControlException {
		if (this.detector != null) {
			checkState();
			return this.detector;

		} else {
			throw new MsControlException(this.uri + " This MediaGroup contains no Signal Detector");
		}

	}

	public SignalGenerator getSignalGenerator() throws MsControlException {
		if (this.generator != null) {
			checkState();
			return this.generator;
		} else {
			throw new MsControlException(this.uri + " This MediaGroup contains no Signal Generator");
		}

	}

	public boolean stop() {
		boolean playerStop = this.player.stop();
		boolean recorderStop = this.recorder.stop();
		boolean signalDetStop = this.detector.stop();
		return (playerStop || recorderStop || signalDetStop);
	}

	// ResourceContainer methods
	public void confirm() throws MsControlException {

	}

	public MediaConfig getConfig() {
		return config;
	}

	public <R> R getResource(Class<R> arg0) throws MsControlException {
		return null;
	}

	public void triggerRTC(Action rtca) {

	}

	// MediaObject methods
	public Parameters createParameters() {
		return new ParametersImpl();
	}

	public Parameters getParameters(Parameter[] params) {
		Parameters tmpParameters = this.createParameters();

		if (this.parameters != null) {
			if (params != null && params.length > 0) {
				for (Parameter p : this.parameters.keySet()) {
					for (Parameter pArg : params) {
						if (p.equals(pArg)) {
							tmpParameters.put(p, this.parameters.get(p));
						}
					}
				}
			} else {
				tmpParameters.putAll(this.parameters);
			}
		}
		return tmpParameters;
	}

	public URI getURI() {
		return this.uri;
	}

	public void release() {
		checkState();
		if (this.player != null) {
			this.player.stop();
		}

		if (this.recorder != null) {
			this.recorder.stop();
		}

		if (this.detector != null) {
			this.detector.stop();
		}
		try {
			Joinable[] joinableArray = this.getJoinees();
			for (Joinable joinable : joinableArray) {
				this.unjoinInitiate(joinable, this);
			}
		} catch (MsControlException e) {
			logger.error("release of MediaGroup failed ", e);
		}
		this.state = MediaObjectState.RELEASED;
		
		this.mediaSession.getMedGrpList().remove(this);
	}

	public void setParameters(Parameters params) {
		this.parameters = params;
	}

	@Override
	protected void resetContainer() {
		this.endpoint = IVR_ENDPOINT_NAME;
	}

	protected void checkState() {
		if (this.state.equals(MediaObjectState.RELEASED)) {
			throw new IllegalStateException("State of container " + this.getURI() + "is released");
		}

	}

	public String getEndpoint() {
		return this.endpoint;
	}

	@Override
	protected void joined(ConnectionIdentifier thisConnId, ConnectionIdentifier otherConnId) {
		this.thisConnId = thisConnId;
	}

	@Override
	protected void unjoined(ConnectionIdentifier thisConnId, ConnectionIdentifier otherConnId) {
		this.thisConnId = null;
	}

	@Override
	public MediaObjectState getState() {
		return this.state;
	}

	public Iterator<MediaObject> getMediaObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	public <T extends MediaObject> Iterator<T> getMediaObjects(Class<T> paramClass) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addListener(AllocationEventListener paramAllocationEventListener) {
		// TODO Auto-generated method stub

	}

	public void removeListener(AllocationEventListener paramAllocationEventListener) {
		// TODO Auto-generated method stub

	}

}
