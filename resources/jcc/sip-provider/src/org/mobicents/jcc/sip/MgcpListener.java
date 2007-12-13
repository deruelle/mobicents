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
 *  File Name     : MgcpListener.java
 *  Version       : $Revision: 1.1 $
 *
 *  $Log: MgcpListener.java,v $
 *  Revision 1.1  2007/02/16 08:06:53  kulikoff
 *  Initial JCC for SIP  implementation
 *
 *  Revision 1.2  2006/05/22 13:44:44  pavel
 *  mixer
 *
 *  Revision 1.1  2006/05/19 03:39:27  pavel
 *  working
 *
 *
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

package org.mobicents.jcc.sip;

import jain.protocol.ip.mgcp.JainMgcpListener;
import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.Constants;

import jain.protocol.ip.mgcp.message.CreateConnectionResponse;
/**
 *
 * @author $Author: kulikoff $
 * @version $Revision: 1.1 $
 */
public class MgcpListener implements JainMgcpListener {
    
    private JccProviderImpl provider;
    
    /** Creates a new instance of MgcpListener */
    public MgcpListener(JccProviderImpl provider) {
        this.provider = provider;
    }
    
    public void processMgcpCommandEvent(JainMgcpCommandEvent event) {
    }
    
    public void processMgcpResponseEvent(JainMgcpResponseEvent event) {
        System.out.println("Receive event " + event);
        switch (event.getObjectIdentifier()) {
            case Constants.RESP_CREATE_CONNECTION : {
                CreateConnectionResponse response = (CreateConnectionResponse) event;
                //response.ge
                
                int tid = event.getTransactionHandle();
                
                MediaConnection mc = (MediaConnection)
                provider.mgcpTransactions.get(new Integer(tid));
                
                String sdp = response.getLocalConnectionDescriptor().toString();
                String remoteID = response.getConnectionIdentifier().toString();
                String ep = response.getSpecificEndpointIdentifier().toString();
                mc.apply(remoteID, sdp, ep);
                
                break;
            }
            
            case Constants.RESP_MODIFY_CONNECTION : {
                int tid = event.getTransactionHandle();
                
                MediaConnection mc = (MediaConnection)
                provider.mgcpTransactions.get(new Integer(tid));
                mc.modifyResponse();                
                break;
            }
            
            case Constants.RESP_NOTIFICATION_REQUEST : {
                break;
            }
        }
    }
    
}
