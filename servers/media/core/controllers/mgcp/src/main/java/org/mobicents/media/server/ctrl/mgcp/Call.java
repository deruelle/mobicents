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

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import org.mobicents.media.server.spi.Connection;

/**
 * Represents MGCP Call.
 * 
 * @author kulikov
 */
public class Call implements Serializable {

    private String id;
    private MgcpController controller;
    private ConcurrentHashMap<String, ConnectionActivity> connections = new ConcurrentHashMap<String, ConnectionActivity>();
    
    protected Call(String id, MgcpController controller) {
        this.controller = controller;
        this.id = id;
    }
    
    public String getID() {
        return id;
    }
    
    public ConnectionActivity addConnection(Connection connection) {
        ConnectionActivity l = new ConnectionActivity(this, connection);
        connections.put(l.getID(), l);
        return l;
    }
    
    public void removeConnection(String connectionID) throws UnknownConnectionException {
        ConnectionActivity l = connections.remove(connectionID);
        if (l == null) {
            throw new UnknownConnectionException(connectionID);
        }
        //l.close();
        if (connections.isEmpty()) {
            controller.removeCall(id);
        }
    }
    
    public Collection<ConnectionActivity> getActivities() {
        return connections.values();
    }
}
