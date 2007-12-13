/*
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 *  Copyrights:
 *
 *  Copyright - 2005 Internet Technologies, Ltd. All rights reserved.
 *  Volgograd, Russia
 *
 *  This product and related documentation are protected by copyright and
 *  distributed under licenses restricting its use, copying, distribution, and
 *  decompilation. No part of this product or related documentation may be
 *  reproduced in any form by any means without prior written authorization of
 *  ITech and its licensors, if any.
 *
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 *  Author:
 *
 *  Internet Technologies, Ltd.
 *  Volgograd, Russia
 *
 *  Module Name   : GCT API
 *  File Name     : MediaConnection.java
 *  Version       : $Revision: 1.1 $
 *
 *  $Log: MediaConnection.java,v $
 *  Revision 1.1  2007/02/16 08:06:52  kulikoff
 *  Initial JCC for SIP  implementation
 *
 *  Revision 1.3  2006/05/22 13:44:44  pavel
 *  mixer
 *
 *  Revision 1.2  2006/05/22 05:06:30  oleg
 *  Please wait, commit template is loading...
 *
 *  Revision 1.1  2006/05/19 03:39:27  pavel
 *  working
 *
 *
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

package org.mobicents.jcc.sip;

import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;

import jain.protocol.ip.mgcp.message.CreateConnection;
import jain.protocol.ip.mgcp.message.ModifyConnection;

import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionMode;
import jain.protocol.ip.mgcp.message.parms.ConnectionDescriptor;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionDescriptor;

import jain.protocol.ip.mgcp.message.parms.ConflictingParameterException;

import EDU.oswego.cs.dl.util.concurrent.Semaphore;

/**
 *
 * @author $Author: kulikoff $
 * @version $Revision: 1.1 $
 */
public class MediaConnection {
    public static int GENERATOR = 1;
    
    private ConnectionIdentifier ci;
    private CallIdentifier callID;
    private EndpointIdentifier ei;
    
    private int id;
    private JccConnectionImpl connection;
    private Semaphore semaphore = new Semaphore(0);
    private String sdp;
    private String endpoint;
    
    /** Creates a new instance of MediaConnection */
    public MediaConnection(JccConnectionImpl connection) {
        this.connection = connection;
        id = GENERATOR++;
    }

    public MediaConnection(JccConnectionImpl connection, String endpoint) {
        this.connection = connection;
        this.endpoint = endpoint;
        id = GENERATOR++;
    }
    
    public int getID() {
        return id;
    }
    
    public String getSdp() {
        return sdp;
    }
    
    public void request() throws ConflictingParameterException, InterruptedException {
        JccCallImpl call = (JccCallImpl) connection.call;
        EndpointIdentifier ei = endpoint == null ?
                new EndpointIdentifier("rtp/$", "localhost:2427"):
                new EndpointIdentifier(endpoint, "localhost:2427");
        
        String id = call.callID.replaceAll("-", "");
        CallIdentifier callID = id.length() < 33 ?
            new CallIdentifier(id) : new CallIdentifier(id.substring(0, 31));
        
        
        CreateConnection cc = new CreateConnection(this, callID, ei, ConnectionMode.SendRecv);
        cc.setTransactionHandle(this.id);
        
        if (connection.remoteSdp != null) {
            cc.setRemoteConnectionDescriptor(new ConnectionDescriptor(connection.remoteSdp));
        }
        call.provider.mgcpProvider.sendMgcpEvents(new JainMgcpEvent[]{cc});
        
        semaphore.acquire();
    }
    
    public void apply(String remoteID, String sdp, String endpoint) {
        this.sdp = sdp;
        connection.id = remoteID;
        connection.call.endpoint = endpoint;
        semaphore.release();
    }
    
    public void modify(String sdp) throws ConflictingParameterException, InterruptedException {
        JccCallImpl call = (JccCallImpl) connection.call;        
        EndpointIdentifier ei = new EndpointIdentifier(endpoint, "localhost:2427");
        
        String id = call.callID.replaceAll("-", "");
        CallIdentifier callID = id.length() < 33 ?
            new CallIdentifier(id) : new CallIdentifier(id.substring(0, 31));
        
        ConnectionIdentifier ci = new ConnectionIdentifier(connection.toString());
        ModifyConnection mc = new ModifyConnection(this, callID, ei, ci);
        mc.setMode(ConnectionMode.SendRecv);
        mc.setTransactionHandle(this.id);        
        mc.setRemoteConnectionDescriptor(new ConnectionDescriptor(sdp));
        
        call.provider.mgcpProvider.sendMgcpEvents(new JainMgcpEvent[]{mc});
        
        semaphore.acquire();        
    }
    
    public void modifyResponse() {
        semaphore.release();
    }
}
