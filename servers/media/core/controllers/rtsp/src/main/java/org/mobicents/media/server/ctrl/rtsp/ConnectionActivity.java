package org.mobicents.media.server.ctrl.rtsp;

import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionListener;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.ConnectionState;

/**
 * 
 * @author amit bhayani
 *
 */
public class ConnectionActivity implements ConnectionListener {

    private static int GEN = 1;
    
    protected Connection connection;
    private final String id;
    private final Session session;
    
	public ConnectionActivity(Session session, Connection connection) {
		this.session = session;
		this.connection = connection;
		
        this.id = Integer.toHexString(GEN++);
        if (GEN == Integer.MAX_VALUE) {
            GEN = 1;
        }
        
        this.connection.addListener(this);
	}
	
    public String getID() {
        return id;
    }

	public void onModeChange(Connection connection, ConnectionMode oldMode) {
		// TODO Auto-generated method stub

	}

	public void onStateChange(Connection connection, ConnectionState oldState) {
		// TODO Auto-generated method stub

	}
	
    public Connection getMediaConnection() {
        return this.connection;
    }
	
    public void close() {
        try {
            session.removeConnection(id);
        } catch (Exception e) {
        }
    	connection.getEndpoint().deleteConnection(connection.getId());
        this.connection.removeListener(this);
        this.connection = null;
    }

}
