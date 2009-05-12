/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.testsuite.general.ann;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.CreateConnection;
import jain.protocol.ip.mgcp.message.CreateConnectionResponse;
import jain.protocol.ip.mgcp.message.DeleteConnection;
import jain.protocol.ip.mgcp.message.DeleteConnectionResponse;
import jain.protocol.ip.mgcp.message.NotificationRequest;
import jain.protocol.ip.mgcp.message.NotificationRequestResponse;
import jain.protocol.ip.mgcp.message.Notify;
import jain.protocol.ip.mgcp.message.NotifyResponse;
import jain.protocol.ip.mgcp.message.parms.ConnectionDescriptor;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionMode;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.EventName;
import jain.protocol.ip.mgcp.message.parms.NotifiedEntity;
import jain.protocol.ip.mgcp.message.parms.RequestIdentifier;
import jain.protocol.ip.mgcp.message.parms.RequestedAction;
import jain.protocol.ip.mgcp.message.parms.RequestedEvent;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;
import jain.protocol.ip.mgcp.pkg.MgcpEvent;
import jain.protocol.ip.mgcp.pkg.PackageName;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.mobicents.media.server.testsuite.general.AbstractCall;
import org.mobicents.media.server.testsuite.general.AbstractTestCase;
import org.mobicents.media.server.testsuite.general.CallState;


/**
 *
 * @author baranowb
 */
public class AnnCall extends AbstractCall{

    private AnnCallState localFlowState = AnnCallState.INITIAL;
    private String HELLO_WORLD = "";
    private ConnectionIdentifier allocatedConnection = null;
    public AnnCall(AbstractTestCase testCase, String fileToPlay) throws IOException
    {
        super(testCase);
        super.endpointName = "/mobicents/media/aap/$";
        this.HELLO_WORLD = fileToPlay;
       
    }
    
    protected void setLocalFlowState(AnnCallState state)
    {
    
        if(this.localFlowState == state)
        {
            return;
        }
        
        //FIXME: add more
        
        this.localFlowState = state;
    
    
    }
    
    
    
    public void transactionRxTimedOut(JainMgcpCommandEvent arg0) {
        
    }

    public void transactionTxTimedOut(JainMgcpCommandEvent arg0) {
        switch(this.localFlowState)
        {
            case SENT_CRCX:
                    this.setLocalFlowState(AnnCallState.TERMINATED);
                    super.setState(CallState.IN_ERROR);
                break;
            case SENT_ANN:
                    //THis is really bad, ... wech
                    sendDelete();
                    //If it fails, we dont have means to recover do we?
                    this.setLocalFlowState(AnnCallState.TERMINATED);
                    super.setState(CallState.IN_ERROR);
                break;
            case SENT_DLCX:
                     this.setLocalFlowState(AnnCallState.TERMINATED);
                     super.setState(CallState.IN_ERROR);
                break;
                
        }
    }

    public void transactionEnded(int arg0) {
        
    }

    public void processMgcpCommandEvent(JainMgcpCommandEvent mgcpCommand) {

		switch (this.localFlowState) {
		case SENT_ANN:
			if(mgcpCommand instanceof Notify)
			{
				//here we could do something, instead we respond with 200 and remove
				Notify notify = (Notify) mgcpCommand;
				//lets remove us from call maps
                                super.testCase.removeCall(mgcpCommand);
                                
				ReturnCode rc= ReturnCode.Transaction_Executed_Normally;
				NotifyResponse notifyResponse = new NotifyResponse(this,rc);
				notifyResponse.setTransactionHandle(notify.getTransactionHandle());
				super.provider.sendMgcpEvents(new JainMgcpEvent[]{notifyResponse});
                                
                                //FIXME, should we term here?
			}
			break;
		default:
			
		}

	}

    public void processMgcpResponseEvent(JainMgcpResponseEvent mgcpResponse) {
                
        
                System.err.println("PROCESS RESPONSE ON STATE : "+this.localFlowState+"\n"+mgcpResponse);
		int code = mgcpResponse.getReturnCode().getValue();
                super.testCase.removeCall(mgcpResponse);
		switch (this.localFlowState) {
		case SENT_CRCX:
			// here we wait for answer, we need to send 200 to invite
                     
			if (mgcpResponse instanceof CreateConnectionResponse) {
				CreateConnectionResponse ccr = (CreateConnectionResponse) mgcpResponse;
				if (99 < code && code < 200) {
					// its provisional
				} else if (199 < code && code < 300) {
					// its success
					super.endpointIdentifier = ccr.getSpecificEndpointIdentifier();
                                        this.allocatedConnection = ccr.getConnectionIdentifier();
					ConnectionDescriptor cd = ccr.getLocalConnectionDescriptor();
	
                                        
        				RequestIdentifier ri = ( provider.getUniqueRequestIdentifier());
					NotificationRequest notificationRequest = new NotificationRequest(this, super.endpointIdentifier, ri);
					EventName[] signalRequests = { new EventName(PackageName.Announcement, MgcpEvent.pa.withParm(HELLO_WORLD), ccr.getConnectionIdentifier()) };
					notificationRequest.setSignalRequests(signalRequests);
					RequestedAction[] actions = new RequestedAction[] { RequestedAction.NotifyImmediately };
     
					RequestedEvent[] requestedEvents = { new RequestedEvent(new EventName(PackageName.Announcement, MgcpEvent.oc, null /*ccr.getConnectionIdentifier()*/), actions),
						/*new RequestedEvent(new EventName(PackageName.Announcement, MgcpEvent.of, ccr.getConnectionIdentifier()), actions)*/ };
						notificationRequest.setRequestedEvents(requestedEvents);
						// notificationRequest.setTransactionHandle(mgcpProvider.getUniqueTransactionHandler());
					NotifiedEntity notifiedEntity = new NotifiedEntity(super.testCase.getClientTestNodeAddress().getHostAddress(), super.testCase.getClientTestNodeAddress().getHostAddress(),
					super.testCase.getCallDisplayInterface().getLocalPort());
					notificationRequest.setNotifiedEntity(notifiedEntity);

                                        
                                        notificationRequest.setTransactionHandle(provider.getUniqueTransactionHandler());
                                        //We dont care
					//super.testCase.addCall(ri, this);
					super.testCase.addCall(notificationRequest, this);
					super.provider.sendMgcpEvents(new JainMgcpCommandEvent[] { notificationRequest });

                                        //IS this wrong? should we wait unitl notification is played?
					super.setState(CallState.ESTABILISHED);
                                        this.setLocalFlowState( AnnCallState.SENT_ANN);

				} else {
					
                                    //ADD ERROR
                                    this.setLocalFlowState(AnnCallState.TERMINATED);
                                    super.setState(CallState.IN_ERROR);
                                    //FIXME: add error dump
                                    System.err.println("FAILED["+this.localFlowState+"] ON CRCS RESPONSE: "+mgcpResponse);
				}
			} else {

				 //ADD ERROR
                                    this.setLocalFlowState(AnnCallState.TERMINATED);
                                    super.setState(CallState.IN_ERROR);
                                    //FIXME: add error dump
                                    System.err.println("FAILED["+this.localFlowState+"] ON RESPONSE: "+mgcpResponse);
			}
			break;
			
		case SENT_ANN:
			if (mgcpResponse instanceof NotificationRequestResponse) {

				if (99 < code && code < 200) {
					// its provisional
				} else if (199 < code && code < 300) {
					// its success
					
				} else {
					// its error always?
					 //ADD ERROR
                                    this.setLocalFlowState(AnnCallState.TERMINATED);
                                    super.setState(CallState.IN_ERROR);
                                    //FIXME: add error dump
                                    System.err.println("FAILED["+this.localFlowState+"] ON RESPONSE: "+mgcpResponse);
				}
				
			} else {
                                    this.setLocalFlowState(AnnCallState.TERMINATED);
                                    super.setState(CallState.IN_ERROR);
                                    //FIXME: add error dump
                                    System.err.println("FAILED["+this.localFlowState+"] ON CRCS RESPONSE: "+mgcpResponse);
				
			}
			break;
			
			
		case SENT_DLCX:
                    
                    
                    if (mgcpResponse instanceof DeleteConnectionResponse) {
				if (99 < code && code < 200) {
					// its provisional
				} else if (199 < code && code < 300) {
					// its success
				    this.setLocalFlowState(AnnCallState.TERMINATED);
                                    super.setState(CallState.ENDED);
				} else {
					// its error always?
					 //ADD ERROR
                                   this.setLocalFlowState(AnnCallState.TERMINATED);
                                    super.setState(CallState.IN_ERROR);
                                    //FIXME: add error dump
                                    System.err.println("FAILED["+this.localFlowState+"] ON CRCS RESPONSE: "+mgcpResponse);
				}
			} else {
				this.setLocalFlowState(AnnCallState.TERMINATED);
                                    super.setState(CallState.IN_ERROR);
                                    //FIXME: add error dump
                                    System.err.println("FAILED["+this.localFlowState+"] ON CRCS RESPONSE: "+mgcpResponse);
			}

			
			break;
		default:
			System.err.println("GOT RESPONSE UNKONWN["+this.localFlowState+"] ON CRCS RESPONSE: "+mgcpResponse);
		}

	}

    @Override
    public void start() {
        
        try{
            super.initSocket();
            EndpointIdentifier ei = new EndpointIdentifier(super.endpointName, super.testCase.getServerJbossBindAddress().getHostAddress() + ":" + super.testCase.getCallDisplayInterface().getRemotePort());

            CreateConnection crcx = new CreateConnection(this, this.callIdentifier, ei, ConnectionMode.SendRecv);
//            int localPort = this.datagramChannel.socket().getLocalPort();
            int localPort = super.socket.getLocalPort();
            crcx.setRemoteConnectionDescriptor(new ConnectionDescriptor(super.getLocalDescriptor(localPort)));
            crcx.setTransactionHandle(this.provider.getUniqueTransactionHandler());
            super.provider.sendMgcpEvents(new JainMgcpEvent[] { crcx });
            
            
            super.testCase.addCall(crcx, this);
            
            
            this.receiveRTP=true;
            //for now we do that like that this will go away with rtp socket
            this.readerThread.schedule(this,super._READ_PERIOD,TimeUnit.MILLISECONDS);
            
            
            
            this.setLocalFlowState(AnnCallState.SENT_CRCX);
            super.setState(CallState.INITIAL);
            
        }catch(Exception e)
        {
            e.printStackTrace();
            this.setLocalFlowState(AnnCallState.TERMINATED);
            super.setState(CallState.IN_ERROR);
        }
    }

    @Override
    public void stop() {
      
            this.receiveRTP=false;
            if (this.readerTask != null) {
                this.readerTask.cancel(true);
            }
            if(this.state == CallState.IN_ERROR)
            {
                
            }else
            {
                
                   //FIXME: Should in case of forced stop this indicate error?
                   super.setState(CallState.ENDED);
                   this.setLocalFlowState(AnnCallState.TERMINATED);
            }
       
    }

    @Override
    public void timeOut() {
       //sometimes its error, for us, we consider this and end of test
        sendDelete();
    }

    private void sendDelete() {
       DeleteConnection dlcx = new DeleteConnection(this,super.endpointIdentifier);
       dlcx.setCallIdentifier(this.callIdentifier);
       dlcx.setConnectionIdentifier(this.allocatedConnection);
       dlcx.setTransactionHandle(provider.getUniqueTransactionHandler());
       
       super.testCase.addCall(dlcx, this);
       super.provider.sendMgcpEvents(new JainMgcpCommandEvent[] { dlcx });

                                        //IS this wrong? should we wait unitl notification is played?
        
        this.setLocalFlowState( AnnCallState.SENT_DLCX);
    }

}
