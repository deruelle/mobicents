package org.mobicents.javax.media.mscontrol.mediagroup;

import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;

import java.net.URI;
import java.net.URISyntaxException;

import javax.media.mscontrol.Joinable;
import javax.media.mscontrol.MsControlException;
import javax.media.mscontrol.mediagroup.MediaGroup;
import javax.media.mscontrol.mediagroup.MediaGroupConfig;
import javax.media.mscontrol.mediagroup.Player;
import javax.media.mscontrol.mediagroup.Recorder;
import javax.media.mscontrol.mediagroup.signals.SignalDetector;
import javax.media.mscontrol.mediagroup.signals.SignalGenerator;
import javax.media.mscontrol.resource.Action;
import javax.media.mscontrol.resource.Parameter;
import javax.media.mscontrol.resource.Parameters;

import org.apache.log4j.Logger;
import org.mobicents.javax.media.mscontrol.AbstractJoinableContainer;
import org.mobicents.javax.media.mscontrol.MediaObjectState;
import org.mobicents.javax.media.mscontrol.MediaSessionImpl;
import org.mobicents.javax.media.mscontrol.mediagroup.signals.SignalDetectorImpl;
import org.mobicents.javax.media.mscontrol.resource.ParametersImpl;
import org.mobicents.jsr309.mgcp.MgcpWrapper;

/**
 * 
 * @author amit bhayani
 * 
 */
public class MediaGroupImpl extends AbstractJoinableContainer implements MediaGroup {
	public static Logger logger = Logger.getLogger(MediaGroupImpl.class);

	private static final String LOOP_ENDPOINT_NAME = "media/test/trunk/Loopback/$";
	private static final String IVR_ENDPOINT_NAME = "media/test/trunk/IVR/$";
	private URI uri = null;
	protected Player player = null;
	protected SignalDetector detector = null;
	protected Recorder recorder = null;
	public ConnectionIdentifier thisConnId = null;

	public MediaGroupImpl(MediaSessionImpl mediaSession, MgcpWrapper mgcpWrapper) throws MsControlException {
		super(mediaSession, mgcpWrapper, 1, IVR_ENDPOINT_NAME);
		player = new PlayerImpl(this, mgcpWrapper);
		recorder = new RecorderImpl(this, mgcpWrapper);
		detector = new SignalDetectorImpl(this, mgcpWrapper);
		try {
			this.uri = new URI(mediaSession.getURI().toString() + "/MediaGroup." + this.id);
		} catch (URISyntaxException e) {
			logger.warn(e);
		}
	}

	// MediaGroup Methods
	public Player getPlayer() throws MsControlException {
		checkState();
		return player;
	}

	public Recorder getRecorder() throws MsControlException {
		return this.recorder;
	}

	public SignalDetector getSignalDetector() throws MsControlException {
		return this.detector;
	}

	public SignalGenerator getSignalGenerator() throws MsControlException {
		return null;
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

	public MediaGroupConfig getConfig() {
		return null;
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
		return null;
	}

	public URI getURI() {
		return this.uri;
	}

	public void release() {
		checkState();
		this.player.stop();
		this.recorder.stop();
		this.detector.stop();
		try {
			Joinable[] joinableArray = this.getJoinees();
			for (Joinable joinable : joinableArray) {
				this.unjoinInitiate(joinable, this);
			}
		} catch (MsControlException e) {
			logger.error("release of MediaGroup failed ", e);
		}
		this.state = MediaObjectState.RELEASED;
	}

	public void setParameters(Parameters params) {

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
	protected MediaObjectState getState() {
		return this.state;
	}

}
