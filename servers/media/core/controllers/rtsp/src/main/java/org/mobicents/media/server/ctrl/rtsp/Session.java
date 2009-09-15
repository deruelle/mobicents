package org.mobicents.media.server.ctrl.rtsp;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;


import org.mobicents.media.server.spi.Connection;

/**
 * 
 * @author amit bhayani
 * 
 */
public class Session {

	private static int GEN = 1;

	private String id;
	private RtspController rtspController;
	private SessionState state = SessionState.INIT;
	
	private ConcurrentHashMap<String, ConnectionActivity> connections = new ConcurrentHashMap<String, ConnectionActivity>();

	protected Session(RtspController rtspController) {
		this.rtspController = rtspController;

		this.id = Integer.toHexString(GEN++);
		if (GEN == Integer.MAX_VALUE) {
			GEN = 1;
		}
	}

	protected String getId() {
		return this.id;
	}

	protected SessionState getState() {
		return state;
	}

	protected void setState(SessionState state) {
		this.state = state;
	}
	
    public ConnectionActivity addConnection(Connection connection) {
        ConnectionActivity l = new ConnectionActivity(this, connection);
        connections.put(l.getID(), l);
        return l;
    }
    
    public void removeConnection(String connectionID)  {
        ConnectionActivity l = connections.remove(connectionID);
        if (l == null) {
            //Some cleaning?
        }
        //l.close();
        if (connections.isEmpty()) {
        	rtspController.removeSession(this.id);
        }
    }
    
    public Collection<ConnectionActivity> getActivities() {
        return connections.values();
    }
}
