package org.mobicents.javax.media.mscontrol.mixer;

import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.media.mscontrol.Configuration;
import javax.media.mscontrol.MediaConfig;
import javax.media.mscontrol.MediaEvent;
import javax.media.mscontrol.MediaEventListener;
import javax.media.mscontrol.MediaObject;
import javax.media.mscontrol.MsControlException;
import javax.media.mscontrol.Parameter;
import javax.media.mscontrol.Parameters;
import javax.media.mscontrol.join.Joinable;
import javax.media.mscontrol.mixer.MediaMixer;
import javax.media.mscontrol.mixer.MixerAdapter;
import javax.media.mscontrol.mixer.MixerEvent;
import javax.media.mscontrol.resource.Action;
import javax.media.mscontrol.resource.AllocationEventListener;
import javax.media.mscontrol.resource.enums.ParameterEnum;

import org.apache.log4j.Logger;
import org.mobicents.javax.media.mscontrol.AbstractJoinableContainer;
import org.mobicents.javax.media.mscontrol.MediaConfigImpl;
import org.mobicents.javax.media.mscontrol.MediaObjectState;
import org.mobicents.javax.media.mscontrol.MediaSessionImpl;
import org.mobicents.javax.media.mscontrol.ParametersImpl;
import org.mobicents.javax.media.mscontrol.resource.ExtendedParameter;
import org.mobicents.jsr309.mgcp.MgcpWrapper;

/**
 * 
 * @author amit bhayani
 * 
 */
public class MediaMixerImpl extends AbstractJoinableContainer implements MediaMixer {

	public static Logger logger = Logger.getLogger(MediaMixerImpl.class);

	private String CONF_ENDPOINT_NAME = "/mobicents/media/cnf/$";

	private URI uri = null;
	private MediaConfigImpl config = null;
	private Parameters params = null;

	protected CopyOnWriteArrayList<MediaEventListener<? extends MediaEvent<?>>> mediaEventListenerList = new CopyOnWriteArrayList<MediaEventListener<? extends MediaEvent<?>>>();

	public MediaMixerImpl(MediaSessionImpl mediaSession, MgcpWrapper mgcpWrapper, MediaConfigImpl config) {
		super();
		this.mediaSession = mediaSession;
		this.mgcpWrapper = mgcpWrapper;

		this.endpoint = (String) config.getParameters().get(ExtendedParameter.ENDPOINT_LOCAL_NAME);
		this.CONF_ENDPOINT_NAME = this.endpoint;

		this.maxJoinees = Integer.parseInt((String) config.getParameters().get(ParameterEnum.MAX_PORTS));

		this.config = config;
		try {
			this.uri = new URI(mediaSession.getURI().toString() + "/MediaMixer." + this.id);
		} catch (URISyntaxException e) {
			// Ignore
		}

	}

	public MediaMixerImpl(MediaSessionImpl mediaSession, MgcpWrapper mgcpWrapper, MediaConfigImpl config,
			Parameters params) {
		this(mediaSession, mgcpWrapper, config);
		this.params = params;
	}

	public MixerAdapter createMixerAdapter(Configuration<MixerAdapter> paramConfiguration) throws MsControlException {
		// TODO Auto-generated method stub
		return null;
	}

	public MixerAdapter createMixerAdapter(Configuration<MixerAdapter> paramConfiguration, Parameters paramParameters)
			throws MsControlException {
		// TODO Auto-generated method stub
		return null;
	}

	public MixerAdapter createMixerAdapter(MediaConfig paramMediaConfig, Parameters paramParameters)
			throws MsControlException {
		// TODO Auto-generated method stub
		return null;
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

	public void confirm() throws MsControlException {
		// TODO Auto-generated method stub

	}

	public MediaConfig getConfig() {
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

		this.mediaSession.getMedMxrList().remove(this);
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
