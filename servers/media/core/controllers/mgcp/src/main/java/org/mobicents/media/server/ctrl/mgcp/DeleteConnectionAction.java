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
import jain.protocol.ip.mgcp.message.DeleteConnection;
import jain.protocol.ip.mgcp.message.DeleteConnectionResponse;
import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;
import java.util.Collection;
import java.util.concurrent.Callable;
import org.apache.log4j.Logger;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.EndpointQuery;

/**
 *
 * @author kulikov
 */
public class DeleteConnectionAction implements Callable {

    private static Logger logger = Logger.getLogger(DeleteConnectionAction.class);
    
    private DeleteConnection req;
    private MgcpController controller;
    private MgcpUtils utils = new MgcpUtils();
    private EndpointQuery endpointQuery = EndpointQuery.getInstance();
    
    protected DeleteConnectionAction(MgcpController controller, DeleteConnection req) {
        this.controller = controller;
        this.req = req;
    }
    
    private JainMgcpResponseEvent endpointDeleteConnections(String localName) {
        Endpoint endpoint = null;
        try {
            endpoint = endpointQuery.find(localName);
        } catch (Exception e) {
            return new DeleteConnectionResponse(controller, ReturnCode.Endpoint_Unknown);
        }
        
        Collection<ConnectionActivity> activities = controller.getActivities(localName);
        for (ConnectionActivity activity : activities) {
            activity.close();
        }
        
        endpoint.deleteAllConnections();
        return new DeleteConnectionResponse(controller, ReturnCode.Transaction_Executed_Normally);
    }
    
    private JainMgcpResponseEvent callDeleteConnections(String callID) {
        Call call = controller.getCall(callID);     
        if (call == null) {
            return new DeleteConnectionResponse(controller, ReturnCode.Unknown_Call_ID);
        }
        
        Collection<ConnectionActivity> activities = call.getActivities();
        for (ConnectionActivity activity : activities) {
            activity.close();
            Connection connection = activity.getMediaConnection();
            connection.getEndpoint().deleteConnection(connection.getId());
        }
        
        return new DeleteConnectionResponse(controller, ReturnCode.Transaction_Executed_Normally);
    }
    
    private JainMgcpResponseEvent deleteConnection(String localName, String connectionID) {
        Endpoint endpoint = null;
        try {
            endpoint = endpointQuery.find(localName);
        } catch (Exception e) {
            return new DeleteConnectionResponse(controller, ReturnCode.Endpoint_Unknown);
        }

        ConnectionActivity activity = controller.getActivity(localName, connectionID);
        activity.close();
        
        endpoint.deleteConnection(connectionID);
        return new DeleteConnectionResponse(controller, ReturnCode.Transaction_Executed_Normally);
    }
    
    public JainMgcpResponseEvent call() throws Exception {
        int txID = req.getTransactionHandle();
        CallIdentifier callID = req.getCallIdentifier();
        EndpointIdentifier endpointID = req.getEndpointIdentifier();
        ConnectionIdentifier connectionID = req.getConnectionIdentifier();
        
        logger.info("Request TX= " + txID + 
                ", CallID = " +  callID + 
                ", Endpoint = " + endpointID + 
                ", Connection = " + connectionID);
        

        JainMgcpResponseEvent response = null;
        if (endpointID != null && callID == null && connectionID == null) {
            response = this.endpointDeleteConnections(endpointID.getLocalEndpointName());
        } else if (endpointID != null && callID != null && connectionID != null) {
            response = this.deleteConnection(endpointID.getLocalEndpointName(), connectionID.toString());
        } else {
            response = this.callDeleteConnections(callID.toString());
        }
        
        logger.info("Response TX=" + txID + ", response=" + response.getReturnCode());
        return response;
    }
}