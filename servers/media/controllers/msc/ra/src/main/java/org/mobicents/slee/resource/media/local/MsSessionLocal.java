/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.slee.resource.media.local;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentReaderHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsLink;
import org.mobicents.mscontrol.MsLinkMode;
import org.mobicents.mscontrol.MsProvider;
import org.mobicents.mscontrol.MsSession;
import org.mobicents.mscontrol.MsSessionListener;
import org.mobicents.mscontrol.MsSessionState;

/**
 * 
 * @author Oleg Kulikov
 * @author amit.bhayani
 */
public class MsSessionLocal implements MsSession {
	private MsSession session;
	private MsProviderLocal provider;

	protected ConcurrentReaderHashMap connections = new ConcurrentReaderHashMap();
	protected ConcurrentReaderHashMap links = new ConcurrentReaderHashMap();

	private ReentrantLock blockState = new ReentrantLock();
	private Condition connectionActivityCreated = blockState.newCondition();
	private Condition linkActivityCreated = blockState.newCondition();

	protected MsSessionLocal(MsSession session, MsProviderLocal provider) {
		this.session = session;
		this.provider = provider;
	}

	public String getId() {
		return session.getId();
	}

	public MsProvider getProvider() {
		return provider;
	}

	public MsSessionState getState() {
		return session.getState();
	}

	public MsConnection createNetworkConnection(String endpointName) {
		blockState.lock();
		try {
			MsConnection connection = session.createNetworkConnection(endpointName);
			while (!connections.containsKey(connection.getId())) {
				try {
					connectionActivityCreated.await();
				} catch (InterruptedException e) {
					connection.release();
					return null;
				}
			}
			return (MsConnection) connections.get(connection.getId());
		} finally {
			blockState.unlock();
		}
	}

	public MsLink createLink(MsLinkMode mode) {
		blockState.lock();
		try {
			MsLink link = session.createLink(mode);
			while (!links.containsKey(link.getId())) {
				try {
					linkActivityCreated.await();
				} catch (InterruptedException e) {
					link.release();
					return null;
				}
			}
			return (MsLink) links.get(link.getId());
		} finally {
			blockState.unlock();
		}
	}

	public void addSessionListener(MsSessionListener listener) {
		throw new SecurityException("addSessionListener is unsupported. Use event handlers of SBB");
	}

	public void removeSessionListener(MsSessionListener listener) {
		throw new SecurityException("removeSessionListener is unsupported.");
	}

	public List<MsConnection> getConnections() {
		Collection<MsConnection> values = connections.values();
		ArrayList list = new ArrayList();
		for (MsConnection connection : values) {
			list.add(connection);
		}
		return list;
	}

	public void connectionActivityCreated() {
		blockState.lock();
		try {
			this.connectionActivityCreated.signalAll();
		} finally {
			blockState.unlock();
		}
	}

	public void linkActivityCreated() {
		blockState.lock();
		try {
			this.linkActivityCreated.signalAll();
		} finally {
			blockState.unlock();
		}
	}

	@Override
	public String toString() {
		return session.toString();
	}

}
