package org.mobicents.javax.media.mscontrol;

import jain.protocol.ip.mgcp.message.parms.CallIdentifier;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.mscontrol.MediaConfig;
import javax.media.mscontrol.MediaSession;
import javax.media.mscontrol.MsControlException;
import javax.media.mscontrol.mediagroup.MediaGroup;
import javax.media.mscontrol.mediagroup.MediaGroupConfig;
import javax.media.mscontrol.mixer.MediaMixer;
import javax.media.mscontrol.networkconnection.NetworkConnection;
import javax.media.mscontrol.networkconnection.NetworkConnectionConfig;
import javax.media.mscontrol.resource.Configuration;
import javax.media.mscontrol.resource.Parameter;
import javax.media.mscontrol.resource.Parameters;
import javax.media.mscontrol.resource.ResourceContainer;
import javax.media.mscontrol.vxml.VxmlDialog;

import org.apache.log4j.Logger;
import org.mobicents.javax.media.mscontrol.mediagroup.MediaGroupImpl;
import org.mobicents.javax.media.mscontrol.mixer.MediaMixerImpl;
import org.mobicents.javax.media.mscontrol.networkconnection.NetworkConnectionImpl;
import org.mobicents.javax.media.mscontrol.resource.ParametersImpl;
import org.mobicents.jsr309.mgcp.MgcpWrapper;

/**
 * 
 * @author amit bhayani
 * 
 */
public class MediaSessionImpl implements MediaSession {
	private static final Logger logger = Logger.getLogger(MediaSessionImpl.class);
	private URI uri = null;
	private MgcpWrapper mgcpWrapper;

	private CallIdentifier callIdentifier = null;

	List<NetworkConnection> netConnList = new ArrayList<NetworkConnection>();
	List<MediaGroup> medGrpList = new ArrayList<MediaGroup>();
	List<MediaMixer> medMxrList = new ArrayList<MediaMixer>();

	private Map attributeMap = new HashMap();

	public MediaSessionImpl(MgcpWrapper mgcpWrapper) {
		this.mgcpWrapper = mgcpWrapper;

		// callIdentifier acts as media session id
		this.callIdentifier = this.mgcpWrapper.getUniqueCallIdentifier();
		try {
			this.uri = new URI("mscontrol://" + this.mgcpWrapper.getPeerIp() + "/" + this.callIdentifier.toString());
		} catch (URISyntaxException e) {
			logger.error(e);
		}
	}

	// MediaObject Methods
	public Parameters createParameters() {
		return new ParametersImpl();
	}

	public Parameters getParameters(Parameter[] params) {
		// TODO Auto-generated method stub
		return null;
	}

	public URI getURI() {
		return this.uri;
	}

	public void release() {
		for (NetworkConnection nc : netConnList) {
			nc.release();
		}

		for (MediaGroup mg : medGrpList) {
			mg.release();
		}

		for (MediaMixer mx : medMxrList) {
			mx.release();
		}
		this.attributeMap.clear();
	}

	public void setParameters(Parameters arg0) {
		// TODO Auto-generated method stub

	}

	// MediaSession Methods
	public <T extends ResourceContainer<? extends MediaConfig>> T createContainer(Class<T> aClass, Parameters params)
			throws MsControlException {
		// TODO Auto-generated method stub
		return null;
	}

	public <C extends MediaConfig, T extends ResourceContainer<? extends C>> T createContainer(
			Configuration<C> predefinedConfig) throws MsControlException {
		// TODO Auto-generated method stub
		return null;
	}

	public <C extends MediaConfig, T extends ResourceContainer<? extends C>> T createContainer(C config,
			String containerId) throws MsControlException {
		// TODO Auto-generated method stub

		return null;
	}

	public VxmlDialog createVxmlDialog(Parameters parameters) throws MsControlException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getAttribute(String key) {
		return attributeMap.get(key);
	}

	public Enumeration<String> getAttributeNames() {
		return Collections.enumeration(attributeMap.keySet());
	}

	public void removeAttribute(String key) {
		attributeMap.remove(key);
	}

	public void setAttribute(String key, Object arg1) {		
		attributeMap.put(key, arg1);
	}

	// Custom Methods
	public <C extends MediaConfig> NetworkConnection createNetworkConnection(Configuration<C> predefinedConfig)
			throws MsControlException {
		NetworkConnectionConfig config = (NetworkConnectionConfig) MediaConfigFactory.getMediaConfig(predefinedConfig);
		NetworkConnectionImpl networkConnectionImpl = new NetworkConnectionImpl(this, mgcpWrapper, config);
		netConnList.add(networkConnectionImpl);
		return networkConnectionImpl;
	}

	public <C extends MediaConfig> MediaGroup createMediaGroup(Configuration<C> predefinedConfig)
			throws MsControlException {
		MediaGroupConfig config = (MediaGroupConfig) MediaConfigFactory.getMediaConfig(predefinedConfig);
		MediaGroupImpl mediaGroupImpl = new MediaGroupImpl(this, mgcpWrapper, config);
		medGrpList.add(mediaGroupImpl);
		return mediaGroupImpl;
	}

	public MediaMixer createMediaMixer() throws MsControlException {
		MediaMixerImpl mediaMixerImpl = new MediaMixerImpl(this, mgcpWrapper);
		medMxrList.add(mediaMixerImpl);
		return mediaMixerImpl;
	}

	public CallIdentifier getCallIdentifier() {
		return this.callIdentifier;
	}

}
