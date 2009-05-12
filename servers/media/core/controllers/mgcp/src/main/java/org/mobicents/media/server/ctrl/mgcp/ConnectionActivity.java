/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package org.mobicents.media.server.ctrl.mgcp;

import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionListener;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.ConnectionState;

/**
 * Used as a listener for an actual connection and sends events to the MGCP CA.
 * 
 * @author kulikov
 */
public class ConnectionActivity implements ConnectionListener {

    private static int GEN = 1;
    
    private Connection connection;
    private String id;
    
    protected ConnectionActivity(Call call, Connection connection) {
        this.id = Integer.toHexString(GEN++);
        if (GEN == Integer.MAX_VALUE) {
            GEN = 1;
        }
        this.connection = connection;
        connection.addListener(this);
    }
    
    public String getID() {
        return id;
    }
    
    public void onStateChange(Connection connection, ConnectionState oldState) {
    }

    public void onModeChange(Connection connection, ConnectionMode oldMode) {
    }
    
    public Connection getMediaConnection() {
        return connection;
    }
    
    public void close() {
    	connection.getEndpoint().deleteConnection(connection.getId());
        connection.removeListener(this);
    }

}
