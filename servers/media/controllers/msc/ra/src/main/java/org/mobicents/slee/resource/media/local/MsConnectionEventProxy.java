/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.slee.resource.media.local;

import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsConnectionEvent;
import org.mobicents.mscontrol.MsConnectionListener;

/**
 * 
 * @author Oleg Kulikov
 */
public class MsConnectionEventProxy implements MsConnectionListener {

	private MsProviderLocal provider;

	protected MsConnectionEventProxy(MsProviderLocal provider) {
		this.provider = provider;
	}

	public void connectionCreated(MsConnectionEvent event) {
		MsSessionLocal session = (MsSessionLocal) provider.sessions.get(event.getConnection().getSession().getId());
		MsConnectionLocal connection = new MsConnectionLocal(session, event.getConnection());
		session.connections.put(connection.getId(), connection);
		MsConnectionEventLocal evt = new MsConnectionEventLocal(event, connection);
		for (MsConnectionListener listener : provider.connectionListeners) {
			System.out.println("PROXY: Sending " + evt.getEventID() + " to " + listener);
			listener.connectionCreated(evt);
		}
		session.connectionActivityCreated();
	}

	public void connectionFailed(MsConnectionEvent event) {
		MsSessionLocal session = (MsSessionLocal) provider.sessions.get(event.getConnection().getSession().getId());
		MsConnection connection = (MsConnection) session.connections.get(event.getConnection().getId());
		MsConnectionEventLocal evt = new MsConnectionEventLocal(event, connection);
		for (MsConnectionListener listener : provider.connectionListeners) {
			listener.connectionFailed(evt);
		}
	}

	public void connectionHalfOpen(MsConnectionEvent event) {
		MsSessionLocal session = (MsSessionLocal) provider.sessions.get(event.getConnection().getSession().getId());
		MsConnection connection = (MsConnection) session.connections.get(event.getConnection().getId());
		MsConnectionEventLocal evt = new MsConnectionEventLocal(event, connection);
		for (MsConnectionListener listener : provider.connectionListeners) {
			listener.connectionHalfOpen(evt);
		}
	}

	public void connectionOpen(MsConnectionEvent event) {
		MsSessionLocal session = (MsSessionLocal) provider.sessions.get(event.getConnection().getSession().getId());
		MsConnection connection = (MsConnection) session.connections.get(event.getConnection().getId());
		MsConnectionEventLocal evt = new MsConnectionEventLocal(event, connection);
		for (MsConnectionListener listener : provider.connectionListeners) {
			listener.connectionOpen(evt);
		}
	}

	public void connectionDisconnected(MsConnectionEvent event) {
		String eventSessionId = event.getConnection().getSession().getId();

		MsSessionLocal session = (MsSessionLocal) provider.sessions.get(eventSessionId);
		MsConnection connection = null;
		if (session != null) {
			connection = (MsConnection) session.connections.remove(event.getConnection().getId());
		} else {
			connection = event.getConnection();
		}
		MsConnectionEventLocal evt = new MsConnectionEventLocal(event, connection);
		for (MsConnectionListener listener : provider.connectionListeners) {
			listener.connectionDisconnected(evt);
		}
	}
}
