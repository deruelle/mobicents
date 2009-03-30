package org.mobicents.javax.media.mscontrol.mediagroup;

import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;

import java.net.URI;

import javax.media.mscontrol.Joinable;
import javax.media.mscontrol.MsControlException;
import javax.media.mscontrol.mediagroup.MediaGroup;
import javax.media.mscontrol.mediagroup.MediaGroupConfig;
import javax.media.mscontrol.mediagroup.Player;
import javax.media.mscontrol.mediagroup.Recorder;
import javax.media.mscontrol.mediagroup.signals.SignalDetector;
import javax.media.mscontrol.resource.Parameters;
import javax.media.mscontrol.resource.Symbol;

import org.apache.log4j.Logger;
import org.mobicents.javax.media.mscontrol.AbstractJoinableContainer;
import org.mobicents.javax.media.mscontrol.MediaObjectState;
import org.mobicents.javax.media.mscontrol.MediaSessionImpl;
import org.mobicents.jsr309.mgcp.MgcpWrapper;

public class MediaGroupImpl extends AbstractJoinableContainer implements MediaGroup {
	public static Logger logger = Logger.getLogger(MediaGroupImpl.class);

	private static final String LOOP_ENDPOINT_NAME = "media/test/trunk/Loopback/$";
	private static final String IVR_ENDPOINT_NAME = "media/test/trunk/IVR/$";
	protected Player player = null;
	protected ConnectionIdentifier thisConnId = null;

	public MediaGroupImpl(MediaSessionImpl mediaSession, MgcpWrapper mgcpWrapper) throws MsControlException {
		super(mediaSession, mgcpWrapper, 1, LOOP_ENDPOINT_NAME);
		player = new PlayerImpl(this, mgcpWrapper);
	}

	public Player getPlayer() throws MsControlException {
		checkState();
		return player;
	}

	public Recorder getRecorder() throws MsControlException {
		return null;
	}

	public SignalDetector getSignalDetector() throws MsControlException {
		return null;
	}

	public void stop() {

	}

	public void confirm() throws MsControlException {

	}

	public MediaGroupConfig getConfig() {
		return null;
	}

	public <R> R getResource(Class<R> arg0) throws MsControlException {
		return null;
	}

	public void triggerRTC(Symbol arg0) {

	}

	public Parameters createParameters() {
		return null;
	}

	public Parameters getParameters(Symbol[] arg0) {
		return null;
	}

	public URI getURI() {
		return null;
	}

	public void release() {
		checkState();

		// this.player.stop();

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

	public void setParameters(Parameters arg0) {

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

	protected String getEndpoint() {
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
