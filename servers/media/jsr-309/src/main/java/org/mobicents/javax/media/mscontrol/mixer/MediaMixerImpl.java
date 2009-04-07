package org.mobicents.javax.media.mscontrol.mixer;

import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.media.mscontrol.Joinable;
import javax.media.mscontrol.MsControlException;
import javax.media.mscontrol.mixer.MediaMixer;
import javax.media.mscontrol.mixer.MixerAdapter;
import javax.media.mscontrol.mixer.MixerAdapterConfig;
import javax.media.mscontrol.mixer.MixerConfig;
import javax.media.mscontrol.mixer.MixerEvent;
import javax.media.mscontrol.resource.Action;
import javax.media.mscontrol.resource.Configuration;
import javax.media.mscontrol.resource.MediaEvent;
import javax.media.mscontrol.resource.MediaEventListener;
import javax.media.mscontrol.resource.Parameter;
import javax.media.mscontrol.resource.Parameters;

import org.apache.log4j.Logger;
import org.mobicents.javax.media.mscontrol.AbstractJoinableContainer;
import org.mobicents.javax.media.mscontrol.MediaObjectState;
import org.mobicents.javax.media.mscontrol.MediaSessionImpl;
import org.mobicents.javax.media.mscontrol.resource.ParametersImpl;
import org.mobicents.jsr309.mgcp.MgcpWrapper;

public class MediaMixerImpl extends AbstractJoinableContainer implements MediaMixer {

	public static Logger logger = Logger.getLogger(MediaMixerImpl.class);

	private static final String CONF_ENDPOINT_NAME = "media/test/trunk/Conference/$";
	protected static final int MAX_CONNECTION = 5;

	private URI uri = null;

	protected CopyOnWriteArrayList<MediaEventListener<? extends MediaEvent<?>>> mediaEventListenerList = new CopyOnWriteArrayList<MediaEventListener<? extends MediaEvent<?>>>();

	public MediaMixerImpl(MediaSessionImpl mediaSession, MgcpWrapper mgcpWrapper) {
		super(mediaSession, mgcpWrapper, MAX_CONNECTION, CONF_ENDPOINT_NAME);

		try {
			this.uri = new URI(mediaSession.getURI().toString() + "/MediaMixer." + this.id);
		} catch (URISyntaxException e) {
			// Ignore
		}

	}

	@Override
	protected void checkState() {
		if (this.state.equals(MediaObjectState.RELEASED)) {
			throw new IllegalStateException("State of container " + this.getURI() + "is released");
		}
	}

	@Override
	protected MediaObjectState getState() {
		return this.state;
	}

	@Override
	public URI getURI() {
		return this.uri;
	}

	@Override
	protected void joined(ConnectionIdentifier thisConnId, ConnectionIdentifier otherConnId) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void resetContainer() {
		this.endpoint = CONF_ENDPOINT_NAME;
	}

	@Override
	protected void unjoined(ConnectionIdentifier thisConnId, ConnectionIdentifier otherConnId) {
		// TODO Auto-generated method stub

	}

	public MixerAdapter createMixerAdapter(Configuration<MixerAdapterConfig> predefinedConfig)
			throws MsControlException {
		// TODO Auto-generated method stub
		return null;
	}

	public MixerAdapter createMixerAdapter(Parameters params) throws MsControlException {
		// TODO Auto-generated method stub
		return null;
	}

	public MixerAdapter createMixerAdapter(MixerAdapterConfig config, String containerId) throws MsControlException {
		// TODO Auto-generated method stub
		return null;
	}

	public void confirm() throws MsControlException {
		// TODO Auto-generated method stub

	}

	public MixerConfig getConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	public <R> R getResource(Class<R> resource) throws MsControlException {
		// TODO Auto-generated method stub
		return null;
	}

	public void triggerRTC(Action rtca) {
		// TODO Auto-generated method stub

	}

	public Parameters createParameters() {
		return new ParametersImpl();
	}

	public Parameters getParameters(Parameter[] params) {
		// TODO Auto-generated method stub
		return null;
	}

	public void release() {
		checkState();

		try {
			Joinable[] joinableArray = this.getJoinees();
			for (Joinable joinable : joinableArray) {
				this.unjoinInitiate(joinable, this);
			}
		} catch (MsControlException e) {
			logger.error("release of NetworkConnection failed ", e);
		}

		this.state = MediaObjectState.RELEASED;
	}

	public void setParameters(Parameters params) {
		// TODO Auto-generated method stub

	}

	public void addListener(MediaEventListener<MixerEvent> listener) {
		this.mediaEventListenerList.add(listener);
	}

	public void removeListener(MediaEventListener<MixerEvent> listener) {
		this.mediaEventListenerList.remove(listener);
	}

}
