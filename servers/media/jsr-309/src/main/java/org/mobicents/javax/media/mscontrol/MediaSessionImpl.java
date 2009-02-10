package org.mobicents.javax.media.mscontrol;

import jain.protocol.ip.mgcp.JainMgcpListener;
import jain.protocol.ip.mgcp.message.parms.CallIdentifier;

import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.media.mscontrol.MediaConfig;
import javax.media.mscontrol.MediaSession;
import javax.media.mscontrol.MscontrolException;
import javax.media.mscontrol.networkconnection.NetworkConnection;
import javax.media.mscontrol.networkconnection.NetworkConnectionConfig;
import javax.media.mscontrol.resource.ConfigSymbol;
import javax.media.mscontrol.resource.Parameters;
import javax.media.mscontrol.resource.ResourceContainer;
import javax.media.mscontrol.resource.Symbol;
import javax.media.mscontrol.vxml.VxmlDialog;

import org.mobicents.javax.media.mscontrol.networkconnection.NetworkConnectionImpl;
import org.mobicents.jsr309.mgcp.JainMgcpExtendedListenerImpl;
import org.mobicents.mgcp.stack.JainMgcpStackProviderImpl;

/**
 * 
 * @author amit bhayani
 * 
 */
public class MediaSessionImpl implements MediaSession {
	
	//The CallIdentifier will act as MediaSessionId
	private CallIdentifier callIdentifier = null;
	private Parameters p;
	private ConcurrentHashMap<String, Object> attributes = new ConcurrentHashMap<String, Object>();
	private JainMgcpStackProviderImpl jainMgcpStackProviderImpl;
	private JainMgcpExtendedListenerImpl jainMgcpListenerImpl;

	protected List<? extends ResourceContainer<? extends MediaConfig>> connections = new ArrayList();

	public MediaSessionImpl(JainMgcpStackProviderImpl jainMgcpStackProviderImpl,
			JainMgcpExtendedListenerImpl jainMgcpListenerImpl) {
		this.jainMgcpStackProviderImpl = jainMgcpStackProviderImpl;
		this.jainMgcpListenerImpl = jainMgcpListenerImpl;
		
		this.callIdentifier = jainMgcpStackProviderImpl.getUniqueCallIdentifier();	
	}

	public MediaSessionImpl(JainMgcpStackProviderImpl jainMgcpStackProviderImpl,
			JainMgcpExtendedListenerImpl jainMgcpListenerImpl, Parameters p) {
		this(jainMgcpStackProviderImpl, jainMgcpListenerImpl);
		this.p = p;
	}

	public <C extends MediaConfig, T extends ResourceContainer<? extends C>> T createContainer(
			ConfigSymbol<C> symbol) throws MscontrolException {
		if (symbol.equals(NetworkConnectionConfig.c_Basic)) {
			NetworkConnectionImpl networkConnectionImpl = new NetworkConnectionImpl(
					this, jainMgcpStackProviderImpl, jainMgcpListenerImpl);
			return networkConnectionImpl;
		} else if (symbol.equals(NetworkConnectionConfig.c_DtmfConversion)) {

		} else if (symbol.equals(NetworkConnectionConfig.c_EchoCancel)) {

		}
		return null;
	}

	public <T extends ResourceContainer<? extends MediaConfig>> T createContainer(
			Class<T> arg0, Parameters arg1) throws MscontrolException {
		// TODO Auto-generated method stub
		return null;
	}

	public <C extends MediaConfig, T extends ResourceContainer<? extends C>> T createContainer(
			C arg0, String arg1) throws MscontrolException {
		// TODO Auto-generated method stub
		return null;
	}

	public VxmlDialog createVxmlDialog(Parameters arg0)
			throws MscontrolException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getAttribute(String key) {
		return attributes.get(key);
	}

	public Enumeration<String> getAttributeNames() {
		return attributes.keys();
	}

	public void removeAttribute(String key) {
		attributes.remove(key);

	}

	public void setAttribute(String key, Object value) {
		attributes.put(key, value);

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
	
	public CallIdentifier getCallIdentifier(){
		return this.callIdentifier;
	}

}
