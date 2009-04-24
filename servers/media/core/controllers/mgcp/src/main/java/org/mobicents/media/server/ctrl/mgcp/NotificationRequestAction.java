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

import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.NotificationRequest;
import jain.protocol.ip.mgcp.message.NotificationRequestResponse;
import jain.protocol.ip.mgcp.message.parms.EventName;
import jain.protocol.ip.mgcp.message.parms.RequestedEvent;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;
import jain.protocol.ip.mgcp.pkg.PackageName;
import java.util.concurrent.Callable;
import org.mobicents.media.server.ctrl.mgcp.evt.MgcpPackage;
import org.mobicents.media.server.ctrl.mgcp.evt.MgcpSignal;

/**
 *
 * @author kulikov
 */
public class NotificationRequestAction implements Callable<JainMgcpResponseEvent> {

    private MgcpController controller;
    private NotificationRequest req;
    
    public NotificationRequestAction(MgcpController controller, NotificationRequest req) {
        this.controller = controller;
        this.req = req;
    }
    
    public JainMgcpResponseEvent call() throws Exception {
        NotificationRequestResponse response = null;
        
        RequestedEvent[] events = req.getRequestedEvents();
        for (RequestedEvent event: events) {
        }
        
        EventName[] signals = req.getSignalRequests();
        for (EventName eventName: signals) {
            PackageName packageName = eventName.getPackageName();
            
            MgcpPackage pkg = controller.getPackage(packageName.intValue());
            if (pkg == null) {
                response = new NotificationRequestResponse(this, 
                        ReturnCode.Unsupported_Or_Unknown_Package);
                response.setTransactionHandle(req.getTransactionHandle());
                return response;
            }
            
            MgcpSignal signal = pkg.getSignal(eventName.getEventIdentifier());
            if (signal == null) {
                response = new NotificationRequestResponse(this, 
                        ReturnCode.Gateway_Cannot_Generate_Requested_Signal);
                response.setTransactionHandle(req.getTransactionHandle());
                return response;
            }
            
            signal.queue(req.getEndpointIdentifier(), eventName.getConnectionIdentifier());
        }
        
        response = new NotificationRequestResponse(this, ReturnCode.Transaction_Executed_Normally);
        response.setTransactionHandle(req.getTransactionHandle());
        return response;
    }

}
