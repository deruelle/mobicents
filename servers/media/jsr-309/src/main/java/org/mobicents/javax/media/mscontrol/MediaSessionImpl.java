package org.mobicents.javax.media.mscontrol;

import jain.protocol.ip.mgcp.message.parms.CallIdentifier;

import java.net.URI;
import java.util.Enumeration;

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

import org.mobicents.javax.media.mscontrol.networkconnection.NetworkConnectionImpl;
import org.mobicents.mgcp.stack.JainMgcpStackProviderImpl;

/**
 * 
 * @author amit bhayani
 * 
 */
public class MediaSessionImpl implements MediaSession {

	private JainMgcpStackProviderImpl jainMgcpStackProviderImpl;

	private CallIdentifier callIdentifier = null;

	public MediaSessionImpl(JainMgcpStackProviderImpl jainMgcpStackProviderImpl) {
		this.jainMgcpStackProviderImpl = jainMgcpStackProviderImpl;


		this.callIdentifier = jainMgcpStackProviderImpl.getUniqueCallIdentifier();
	}

	public NetworkConnection createNetworkConnection() {
		NetworkConnectionImpl networkConnectionImpl = new NetworkConnectionImpl(this, jainMgcpStackProviderImpl);
		return networkConnectionImpl;
	}

	public <C extends MediaConfig, T extends ResourceContainer<? extends C>> T createContainer(ConfigSymbol<C> symbol)
			throws MsControlException {
		if (symbol.equals(NetworkConnectionConfig.c_Basic)) {
			NetworkConnectionImpl networkConnectionImpl = new NetworkConnectionImpl(this, jainMgcpStackProviderImpl);
			return null;// (T) networkConnectionImpl;
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
		// TODO Auto-generated method stub
		return null;
	}

	public Parameters getParameters(Symbol[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public URI getURI() {
		// TODO Auto-generated method stub
		return null;
	}

	public void release() {
		// TODO Auto-generated method stub

	}

	public void setParameters(Parameters arg0) {
		// TODO Auto-generated method stub

	}

	public CallIdentifier getCallIdentifier() {
		return this.callIdentifier;
	}

}
