package org.mobicents.javax.media.mscontrol;

import jain.protocol.ip.mgcp.message.parms.CallIdentifier;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.media.mscontrol.MediaConfig;
import javax.media.mscontrol.MediaSession;
import javax.media.mscontrol.MsControlException;
import javax.media.mscontrol.networkconnection.NetworkConnection;
import javax.media.mscontrol.networkconnection.NetworkConnectionConfig;
import javax.media.mscontrol.resource.ConfigSymbol;
import javax.media.mscontrol.resource.Parameters;
import javax.media.mscontrol.resource.ResourceContainer;
import javax.media.mscontrol.resource.Symbol;
import javax.media.mscontrol.vxml.VxmlDialog;

import org.apache.log4j.Logger;
import org.mobicents.javax.media.mscontrol.networkconnection.NetworkConnectionImpl;
import org.mobicents.javax.media.mscontrol.resource.ParametersImpl;
import org.mobicents.jsr309.mgcp.MgcpWrapper;
import org.mobicents.mgcp.stack.JainMgcpStackProviderImpl;

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

	public NetworkConnection createNetworkConnection() {
		NetworkConnectionImpl networkConnectionImpl = new NetworkConnectionImpl(this, mgcpWrapper);
		netConnList.add(networkConnectionImpl);
		return networkConnectionImpl;
	}

	public <C extends MediaConfig, T extends ResourceContainer<? extends C>> T createContainer(ConfigSymbol<C> symbol)
			throws MsControlException {
		if (symbol.equals(NetworkConnectionConfig.c_Basic)) {
			// NetworkConnectionImpl networkConnectionImpl = new
			// NetworkConnectionImpl(this, jainMgcpStackProviderImpl);
			//			return null;// (T) networkConnectionImpl;
		} else if (symbol.equals(NetworkConnectionConfig.c_DtmfConversion)) {

		} else if (symbol.equals(NetworkConnectionConfig.c_EchoCancel)) {

		}
		return null;

	}

	public <T extends ResourceContainer<? extends MediaConfig>> T createContainer(Class<T> arg0, Parameters arg1)
			throws MsControlException {
		// TODO Auto-generated method stub
		return null;
	}

	public <C extends MediaConfig, T extends ResourceContainer<? extends C>> T createContainer(C arg0, String arg1)
			throws MsControlException {
		// TODO Auto-generated method stub
		return null;
	}

	public VxmlDialog createVxmlDialog(Parameters arg0) throws MsControlException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getAttribute(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Enumeration<String> getAttributeNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeAttribute(String arg0) {
		// TODO Auto-generated method stub

	}

	public void setAttribute(String arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	public Parameters createParameters() {
		return new ParametersImpl();
	}

	public Parameters getParameters(Symbol[] arg0) {
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
	}

	public void setParameters(Parameters arg0) {
		// TODO Auto-generated method stub

	}

	public CallIdentifier getCallIdentifier() {
		return this.callIdentifier;
	}

}
