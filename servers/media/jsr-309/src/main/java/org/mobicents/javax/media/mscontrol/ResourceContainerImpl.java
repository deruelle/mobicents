package org.mobicents.javax.media.mscontrol;

import java.net.URI;

import javax.media.mscontrol.MediaConfig;
import javax.media.mscontrol.MscontrolException;
import javax.media.mscontrol.resource.Parameters;
import javax.media.mscontrol.resource.ResourceContainer;
import javax.media.mscontrol.resource.Symbol;

public class ResourceContainerImpl<C extends MediaConfig> implements ResourceContainer<C> {
	private C someVar = null;
	public ResourceContainerImpl(C someVar){
		this.someVar = someVar;
	}
	public void confirm() throws MscontrolException {
		// TODO Auto-generated method stub

	}

	public C getConfig() {
		// TODO Auto-generated method stub
		return someVar;
	}

	public Object getResource(Class resource) throws MscontrolException {
		// TODO Auto-generated method stub
		return null;
	}

	public void triggerRTC(Symbol rtca) {
		// TODO Auto-generated method stub

	}

	public Parameters getParameters(Symbol[] params) {
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

	public void setParameters(Parameters params) {
		// TODO Auto-generated method stub

	}

}