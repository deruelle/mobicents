package org.mobicents.javax.media.mscontrol;

import jain.protocol.ip.mgcp.message.parms.CallIdentifier;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.media.mscontrol.Configuration;
import javax.media.mscontrol.MediaConfig;
import javax.media.mscontrol.MediaObject;
import javax.media.mscontrol.MediaSession;
import javax.media.mscontrol.MsControlException;
import javax.media.mscontrol.Parameter;
import javax.media.mscontrol.Parameters;
import javax.media.mscontrol.mediagroup.MediaGroup;
import javax.media.mscontrol.mixer.MediaMixer;
import javax.media.mscontrol.networkconnection.NetworkConnection;
import javax.media.mscontrol.vxml.VxmlDialog;

import org.apache.log4j.Logger;
import org.mobicents.javax.media.mscontrol.mediagroup.MediaGroupImpl;
import org.mobicents.javax.media.mscontrol.mixer.MediaMixerImpl;
import org.mobicents.javax.media.mscontrol.networkconnection.NetworkConnectionImpl;
import org.mobicents.jsr309.mgcp.MgcpWrapper;

/**
 * 
 * @author amit bhayani
 * 
 */
public class MediaSessionImpl implements MediaSession {
	private static final Logger logger = Logger.getLogger(MediaSessionImpl.class);

	private MgcpWrapper mgcpWrapper;
	private CallIdentifier callIdentifier = null;

	private URI uri = null;

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

	public MediaGroup createMediaGroup(Configuration<MediaGroup> paramConfiguration) throws MsControlException {
		MediaGroup mediaGroupImpl = new MediaGroupImpl(this, mgcpWrapper, paramConfiguration);
		medGrpList.add(mediaGroupImpl);
		return mediaGroupImpl;
	}

	public MediaGroup createMediaGroup(Configuration<MediaGroup> paramConfiguration, Parameters paramParameters)
			throws MsControlException {
		MediaGroup mediaGroupImpl = new MediaGroupImpl(this, mgcpWrapper, paramConfiguration, paramParameters);
		medGrpList.add(mediaGroupImpl);
		return mediaGroupImpl;
	}

	public MediaGroup createMediaGroup(MediaConfig paramMediaConfig, Parameters paramParameters)
			throws MsControlException {
		// TODO Auto-generated method stub
		return null;
	}

	public MediaMixer createMediaMixer(Configuration<MediaMixer> paramConfiguration) throws MsControlException {
		MediaMixerImpl mediaMixerImpl = new MediaMixerImpl(this, mgcpWrapper, paramConfiguration);
		medMxrList.add(mediaMixerImpl);
		return mediaMixerImpl;
	}

	public MediaMixer createMediaMixer(Configuration<MediaMixer> paramConfiguration, Parameters paramParameters)
			throws MsControlException {
		MediaMixerImpl mediaMixerImpl = new MediaMixerImpl(this, mgcpWrapper, paramConfiguration, paramParameters);
		medMxrList.add(mediaMixerImpl);
		return mediaMixerImpl;
	}

	public MediaMixer createMediaMixer(MediaConfig paramMediaConfig, Parameters paramParameters)
			throws MsControlException {
		// TODO Auto-generated method stub
		return null;
	}

	public NetworkConnection createNetworkConnection(Configuration<NetworkConnection> paramConfiguration)
			throws MsControlException {
		NetworkConnectionImpl networkConnectionImpl = new NetworkConnectionImpl(this, mgcpWrapper, paramConfiguration);
		netConnList.add(networkConnectionImpl);
		return networkConnectionImpl;
	}

	public NetworkConnection createNetworkConnection(Configuration<NetworkConnection> paramConfiguration,
			Parameters paramParameters) throws MsControlException {
		NetworkConnectionImpl networkConnectionImpl = new NetworkConnectionImpl(this, mgcpWrapper, paramConfiguration,
				paramParameters);
		netConnList.add(networkConnectionImpl);
		return networkConnectionImpl;
	}

	public NetworkConnection createNetworkConnection(MediaConfig paramMediaConfig, Parameters paramParameters)
			throws MsControlException {
		// TODO Auto-generated method stub
		return null;
	}

	public VxmlDialog createVxmlDialog(Parameters paramParameters) throws MsControlException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getAttribute(String paramString) {
		return attributeMap.get(paramString);
	}

	public Iterator<String> getAttributeNames() {
		return attributeMap.keySet().iterator();
	}

	public void removeAttribute(String paramString) {
		attributeMap.remove(paramString);

	}

	public void setAttribute(String paramString, Object paramObject) {
		attributeMap.put(paramString, paramObject);

	}

	public Parameters createParameters() {
		return new ParametersImpl();
	}

	public Iterator<MediaObject> getMediaObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	public <T extends MediaObject> Iterator<T> getMediaObjects(Class<T> paramClass) {
		// TODO Auto-generated method stub
		return null;
	}

	public Parameters getParameters(Parameter[] paramArrayOfParameter) {
		// TODO Auto-generated method stub
		return null;
	}

	public URI getURI() {
		return this.uri;
	}

	public void release() {
		for (MediaGroup mg : medGrpList) {
			mg.release();
		}

		for (MediaMixer mx : medMxrList) {
			mx.release();
		}

		for (NetworkConnection nc : netConnList) {
			nc.release();
		}
		this.attributeMap.clear();

	}

	public void setParameters(Parameters paramParameters) {
		// TODO Auto-generated method stub

	}

	public CallIdentifier getCallIdentifier() {
		return this.callIdentifier;
	}

}