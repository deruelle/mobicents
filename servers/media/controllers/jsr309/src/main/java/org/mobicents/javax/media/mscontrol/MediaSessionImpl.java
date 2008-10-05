package org.mobicents.javax.media.mscontrol;

import java.net.URI;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import javax.media.mscontrol.MediaConfig;
import javax.media.mscontrol.MediaSession;
import javax.media.mscontrol.MscontrolException;
import javax.media.mscontrol.resource.ConfigSymbol;
import javax.media.mscontrol.resource.Parameters;
import javax.media.mscontrol.resource.ResourceContainer;
import javax.media.mscontrol.resource.Symbol;
import javax.media.mscontrol.vxml.VxmlDialog;

public class MediaSessionImpl implements MediaSession {
	private Parameters p;
	private ConcurrentHashMap<String, Object> attributes = new ConcurrentHashMap<String, Object>();
	public MediaSessionImpl(){
		
	}
	
	public MediaSessionImpl(Parameters p){
		this.p = p;
	}

	public <C extends MediaConfig, T extends ResourceContainer<? extends C>> T createContainer(ConfigSymbol<C> arg0)
			throws MscontrolException {
		// TODO Auto-generated method stub
		return null;
	}

	public <T extends ResourceContainer<? extends MediaConfig>> T createContainer(Class<T> arg0, Parameters arg1)
			throws MscontrolException {
		// TODO Auto-generated method stub
		return null;
	}

	public <C extends MediaConfig, T extends ResourceContainer<? extends C>> T createContainer(C arg0, String arg1)
			throws MscontrolException {
		// TODO Auto-generated method stub
		return null;
	}

	public VxmlDialog createVxmlDialog(Parameters arg0) throws MscontrolException {
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

}
