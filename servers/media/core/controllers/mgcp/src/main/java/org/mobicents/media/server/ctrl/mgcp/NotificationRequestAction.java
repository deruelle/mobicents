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
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.EventName;
import jain.protocol.ip.mgcp.message.parms.NotifiedEntity;
import jain.protocol.ip.mgcp.message.parms.RequestIdentifier;
import jain.protocol.ip.mgcp.message.parms.RequestedEvent;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;
import jain.protocol.ip.mgcp.pkg.PackageName;
import java.util.concurrent.Callable;
import org.mobicents.media.server.ctrl.mgcp.evt.EventDetector;
import org.mobicents.media.server.ctrl.mgcp.evt.MgcpPackage;
import org.mobicents.media.server.ctrl.mgcp.evt.SignalGenerator;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.Endpoint;

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
        
        RequestIdentifier reqID = req.getRequestIdentifier();
        EndpointIdentifier endpointID = req.getEndpointIdentifier();
        
        //request identifier and endpoint identifier are mandatory parameters
        if (reqID == null || endpointID == null) {
            return reject(ReturnCode.Protocol_Error);
        }
        
        //determine notified entity. 
        //use default if not specifie explicit
        NotifiedEntity notifiedEntity = req.getNotifiedEntity();
        if (notifiedEntity == null) {
            notifiedEntity = controller.getNotifiedEntity();
        }
        
        //notified entity is mandatory
        if (notifiedEntity == null) {
            return reject(ReturnCode.Transient_Error);
        }
        
        //obatin and check endpoint
        Endpoint endpoint = null;
        try {
            endpoint = controller.getNamingService().lookup(endpointID.getLocalEndpointName(), true);
        } catch (Exception e) {
            return reject(ReturnCode.Endpoint_Unknown);
        }
        
        Request request = new Request(controller, reqID, endpoint, notifiedEntity);
        //assign event detectors 
        RequestedEvent[] events = req.getRequestedEvents();        
        for (int i = 0; i < events.length; i++) {
            RequestedEvent event = events[i];
            EventName eventName = event.getEventName();
            
            PackageName packageName = eventName.getPackageName();
            
            MgcpPackage pkg = controller.getPackage(packageName.toString());            
            if (pkg == null) {
                return reject(ReturnCode.Unsupported_Or_Unknown_Package);
            }
            
            EventDetector det = pkg.getDetector(event.getEventName().getEventIdentifier(),
                    event.getRequestedActions());
            
            if (det == null) {
                return reject(ReturnCode.Gateway_Cannot_Detect_Requested_Event);
            }
            
            Connection connection = null;
            ConnectionIdentifier connectionID = eventName.getConnectionIdentifier();
            if (connectionID != null) {
                connection = endpoint.getConnection(connectionID.toString());
                if (connection == null) {
                    reject(ReturnCode.Connection_Was_Deleted);
                }
            }
            
            request.append(det, connection);
        }
        
        //queue signal
        EventName[] signals = req.getSignalRequests();
        for (int i = 0; i < signals.length; i++) {
            EventName eventName = signals[i];
            
            PackageName packageName = eventName.getPackageName();            
            MgcpPackage pkg = controller.getPackage(packageName.toString());            
            if (pkg == null) {
                return reject(ReturnCode.Unsupported_Or_Unknown_Package);
            }
            
            SignalGenerator signal = pkg.getGenerator(eventName.getEventIdentifier());
            if (signal == null) {
                return reject(ReturnCode.Gateway_Cannot_Generate_Requested_Signal);
            }
            
            Connection connection = null;
            ConnectionIdentifier connectionID = eventName.getConnectionIdentifier();
            if (connectionID != null) {
                connection = endpoint.getConnection(connectionID.toString());
                if (connection == null) {
                    reject(ReturnCode.Connection_Was_Deleted);
                }
            }
            
            request.append(signal, connection);
        }
        
        if (!request.verifyDetectors())  {
            return reject(ReturnCode.Gateway_Cannot_Detect_Requested_Event);
        }
        
        if (!request.verifyGenerators()) {
            return reject(ReturnCode.Gateway_Cannot_Generate_Requested_Signal);
        }
        
        //disable previous signal
        Request prev = controller.requests.remove(endpoint.getLocalName());
        prev.cancel();
        
        //enable current signal
        controller.requests.put(endpoint.getLocalName(), request);
        request.run();
        
        //send response
        response = new NotificationRequestResponse(this, ReturnCode.Transaction_Executed_Normally);
        response.setTransactionHandle(req.getTransactionHandle());
        
        return response;
    }

    private NotificationRequestResponse reject(ReturnCode code) {
        NotificationRequestResponse response = new NotificationRequestResponse(
                this, code);
        response.setTransactionHandle(req.getTransactionHandle());
        return response;
    }
}
