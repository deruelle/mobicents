/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.slee.resource.media.local;

import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsConnectionListener;
import org.mobicents.mscontrol.MsEndpoint;
import org.mobicents.mscontrol.MsLink;
import org.mobicents.mscontrol.MsNotificationListener;
import org.mobicents.mscontrol.events.MsRequestedEvent;
import org.mobicents.mscontrol.events.MsRequestedSignal;

/**
 * 
 * @author Oleg Kulikov
 * @author amit.bhayani
 */
public class MsEndpointLocal implements MsEndpoint {
	private MsEndpoint endpoint;

	protected MsEndpointLocal(MsEndpoint endpoint) {
		this.endpoint = endpoint;
	}

	public String getLocalName() {
		return endpoint.getLocalName();
	}

	public void execute(MsRequestedSignal[] signals, MsRequestedEvent[] events) {
		endpoint.execute(signals, events);
	}

	public void execute(MsRequestedSignal[] signals, MsRequestedEvent[] events, MsConnection connection) {
		endpoint.execute(signals, events, ((MsConnectionLocal) connection).connection);
	}

	public void execute(MsRequestedSignal[] signals, MsRequestedEvent[] events, MsLink link) {
		endpoint.execute(signals, events, ((MsLinkLocal) link).link);
	}

	public void addConnectionListener(MsConnectionListener arg0) {
		throw new SecurityException("addConnectionListener is unsupported. Use event handlers of SBB");

	}

	public void addNotificationListener(MsNotificationListener arg0) {
		throw new SecurityException("MsNotificationListener is unsupported. Use event handlers of SBB");

	}

	public void removeConnectionListener(MsConnectionListener arg0) {
		throw new SecurityException("removeConnectionListener is unsupported.");

	}

	public void removeNotificationListener(MsNotificationListener arg0) {
		throw new SecurityException("removeNotificationListener is unsupported.");

	}

	public String[] getSupportedPackages() {		
		return this.endpoint.getSupportedPackages();
	}

}
