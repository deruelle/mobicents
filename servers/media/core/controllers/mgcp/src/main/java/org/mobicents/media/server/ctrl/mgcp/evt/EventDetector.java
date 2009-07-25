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

package org.mobicents.media.server.ctrl.mgcp.evt;

import jain.protocol.ip.mgcp.message.parms.EventName;
import jain.protocol.ip.mgcp.message.parms.RequestedAction;
import jain.protocol.ip.mgcp.pkg.MgcpEvent;
import jain.protocol.ip.mgcp.pkg.PackageName;
import org.mobicents.media.Component;
import org.mobicents.media.server.ctrl.mgcp.Request;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 *
 * @author kulikov
 */
public abstract class EventDetector implements NotificationListener {
    
    private String pkgName;
    private String eventName;
    
    private String resourceName;
    private int eventID;
    
    private Endpoint endpoint;
    private Connection connection;    
    private RequestedAction[] actions;
    private String params;
    private Component component;
    
    private Request request;
    
    public EventDetector(String pkgName, String eventName, String resourceName, int eventID, 
            String params, RequestedAction[] actions) {
        this.pkgName = pkgName;
        this.eventName = eventName;
        this.resourceName = resourceName;
        this.eventID = eventID;
        this.params = params;
        this.actions = actions;
    }

    
    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }
    
    public RequestedAction[] getActions() {
        return actions;
    }

    public Connection getConnection() {
        return connection;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }
    
    public void setPackageName(String pkgName) {
        this.pkgName = pkgName;
    }
    
    public EventName getEventName() {
        return new EventName(PackageName.factory(pkgName), MgcpEvent.factory(eventName));
    }
    
    public String getResourceName() {
        return this.resourceName;
    }
    
    public void setRequest(Request request) {
        this.request = request;
    }
    
    public Request getRequest() {
        return request;
    }
    
    public boolean verify(Connection connection) {
        this.connection = connection;
        return this.doVerify(connection);
    }
    
    public boolean verify(Endpoint endpoint) {
        this.endpoint = endpoint;
        return this.doVerify(endpoint);
    }

    protected boolean doVerify(Connection connection) {
        component = connection.getComponent(resourceName, Connection.CHANNEL_RX);
        return component != null;
    }
    
    protected boolean doVerify(Endpoint endpoint) {
        component = endpoint.getComponent(resourceName);
        return component != null;
    }
    
    public void start() {
        component.addListener(this);
        component.start();
    }
    
    public void stop() {
        if (component != null) {
            component.stop();
            component.removeListener(this);
            component = null;
        }
    }
    
    public void update(NotifyEvent event) {
        System.out.println("Receive event :" + event.getEventID() + ", expected=" + this.eventID + ", actions=" + actions.length);
        if (event.getEventID() == this.eventID) {
            for (RequestedAction action: actions) {
                System.out.println("Perform action " + event + "," + action);
                performAction(event, action);
            }
        }
    }
    
    public abstract void performAction(NotifyEvent event, RequestedAction action);
}
