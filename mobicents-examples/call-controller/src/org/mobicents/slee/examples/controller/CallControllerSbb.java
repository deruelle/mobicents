/***************************************************
 *                                                 *
 *  Mobicents: The Open Source VoIP Platform       *
 *                                                 *
 *  Distributable under LGPL license.              *
 *  See terms of license at gnu.org.               *
 *                                                 *
 ***************************************************/
package org.mobicents.slee.examples.controller;

import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.address.Address;
import javax.sip.header.CallIdHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.slee.ActivityContextInterface;
import javax.slee.ChildRelation;
import javax.slee.CreateException;
import javax.slee.InitialEventSelector;
import javax.slee.RolledBackContext;
import javax.slee.SLEEException;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.SbbLocalObject;
import javax.slee.TransactionRequiredLocalException;

import org.mobicents.slee.examples.blocking.CallBlockingSbbLocalObject;
import org.mobicents.slee.examples.forwarding.CallForwardingSbbLocalObject;
import org.mobicents.slee.resource.sip.SipFactoryProvider;

/**
 * This SBB is the root of the service.<BR>
 * The job of the CallControllerSbb is to receive INVITE, BYE,
 * and ACK events and communicate with its children. <BR>
 * The Controller will know whether or not: <BR>
 * 1. The INVITE is blocked;
 * 2. The called user is available;
 * 3. The call has to be redirected;
 * 4. The called user has the Voice Mail enabled.
 * @author Victor
 *
 */
public abstract class CallControllerSbb implements Sbb {
	Logger logger = Logger.getLogger(org.mobicents.slee.examples.controller.CallControllerSbb.class.getName());

	public void onInviteEvent(javax.sip.RequestEvent event, ActivityContextInterface aci) {
		
		try {    	
			// Child relation between Controller and Call Blocking SBB
    		ChildRelation blockingRelation = getCallBlockingSbb();
    		CallBlockingSbbLocalObject blockingChild = (CallBlockingSbbLocalObject) blockingRelation.create();   		
    		      
			if (blockingChild.accept(event, aci)) {
    			logger.info("##### INVITE BLOCKED #####\n");	
    		}
			else {
				// Child relation between Controller and Call Forwarding SBB 
				ChildRelation forwardingRelation = getCallForwardingSbb();
				CallForwardingSbbLocalObject forwardingChild = (CallForwardingSbbLocalObject) forwardingRelation.create();
				     
				if (!forwardingChild.accept(event)) {
					logger.info("##### CALLED USER NOT AVAILABLE #####\n");
					// Cheking if there is any backup address for the called user
					Address toAddress = forwardingChild.forwarding(event, aci);
				
					if (toAddress == null) {
//						Here could be voice mail sbb, or something else that does something smart
						//It should "do" something in similar way that forward child does
						//WE WILL SEND SOME ERROR RESP, USER_TMP_UNAVAILABLE
						
//						 Responding to the client
						ServerTransaction st = (ServerTransaction) aci.getActivity();
						Response response;
						
						try {
							response = getMessageFactory().createResponse(Response.TEMPORARILY_UNAVAILABLE, event.getRequest());
							st.sendResponse(response);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SipException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvalidArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}				
						
					}
					else {
						logger.info("##### USING A BACKUP ADDRESS #####\n");
					}	
				}
				else {
					logger.info("##### CALLED USER AVAILABLE #####\n");
					//USER IS AVAILABLE, LET PROXY HADLE IT
						// Create proxy child SBB
						ChildRelation proxyRelation = getJainSipProxySbb();
						SbbLocalObject proxyChild = proxyRelation.create();
						// Attach Proxy Child to the activity
						aci.attach(proxyChild);
						// Detach ourselves
						aci.detach(this.getSbbContext().getSbbLocalObject());
						// Event router will pass this event to child SBB
								
				}					
			}
			
		}catch (TransactionRequiredLocalException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}catch (IllegalStateException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}catch (SLEEException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}catch (CreateException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}catch (NullPointerException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		} 
	}	
	
	/**
	 * If Voice Mail is used, VoicemailSbb will probably send
	 * an OK response to the caller. The caller will respond
	 * sending and ACK Request and ACK will be forwarded to
	 * the Voice Mail SBB.
	 * If Voice Mail is not used, ACK will be forwarded to 
	 * the Proxy SBB.
	 * 
	 * onAckEvent method is precisely to determine the child
	 * SBB which will handle the ACK event.
	 *  
	 * @param event
	 * @param aci
	 */
	public void onAckEvent(javax.sip.RequestEvent event, ActivityContextInterface aci) {
		try {
			
				// Create proxy child SBB
				ChildRelation proxyRelation = getJainSipProxySbb();
				SbbLocalObject proxyChild = proxyRelation.create();
				// Attach Proxy Child to the activity
				aci.attach(proxyChild);
				// Detach ourselves
				aci.detach(this.getSbbContext().getSbbLocalObject());
				// Event router will pass this event to child SBB
			
			
		} catch (TransactionRequiredLocalException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		} catch (SLEEException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		} catch (CreateException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		} catch (NullPointerException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}		
	}
	
	/**
	 * At any time a SIP Client can send a BYE Request.
	 * If the Voice Mail is being used it will be the
	 * VoicemailSbb the one that will send OK Response.
	 * On the other hand, if the Voice Mail is not being
	 * used, BYE will be forwarded to the Proxy SBB.
	 * 
	 * @param event
	 * @param aci
	 */
	public void onByeEvent(javax.sip.RequestEvent event, ActivityContextInterface aci) {
		try {
		
				// Create proxy child SBB
				ChildRelation proxyRelation = getJainSipProxySbb();
				SbbLocalObject proxyChild;
				proxyChild = proxyRelation.create();
				// Attach Proxy Child to the activity
				aci.attach(proxyChild);
				// Detach ourselves
				aci.detach(this.getSbbContext().getSbbLocalObject());
				// Event router will pass this event to child SBB
		
			
		} catch (TransactionRequiredLocalException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		} catch (SLEEException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		} catch (CreateException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}
	}

	public InitialEventSelector callIDSelect(InitialEventSelector ies) {
		Object event = ies.getEvent();
		String callID = null;
		
		if (event instanceof RequestEvent) {
			// If request event, the convergence name to callId
			Request request = ((RequestEvent) event).getRequest();
			callID = ((CallIdHeader) request.getHeader(CallIdHeader.NAME))
					.getCallId();
		}

		ies.setCustomName(callID);
        return ies;
    }
		
	/**
	 *  Initialize the component
	 */
	public void setSbbContext(SbbContext context) {
		this.sbbContext = context;
		try {
            Context myEnv = (Context) new InitialContext().lookup("java:comp/env");
            // Getting JAIN SIP Resource Adaptor interfaces            
            fp = (SipFactoryProvider) myEnv.lookup("slee/resources/jainsip/1.2/provider");
            
	        // To create Request and Response messages from a particular implementation of JAIN SIP    	                       
            messageFactory = fp.getMessageFactory();
            
            
        } catch (NamingException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
        }		      
	
	}	
    public void unsetSbbContext() { this.sbbContext = null; }
	    
    // TODO: Implement the lifecycle methods if required
    public void sbbCreate() throws javax.slee.CreateException {}
    public void sbbPostCreate() throws javax.slee.CreateException {}
    public void sbbActivate() {}
    public void sbbPassivate() {}
    public void sbbRemove() {}
    public void sbbLoad() {  }
    public void sbbStore() {}
    public void sbbExceptionThrown(Exception exception, Object event, ActivityContextInterface activity) {}
    public void sbbRolledBack(RolledBackContext context) {}
			
	/**
	 * Convenience method to retrieve the SbbContext object stored in setSbbContext.
	 * @return this SBB's SbbContext object
	 */		 
	protected SbbContext getSbbContext() {	
		return sbbContext;
	}
	
	public abstract ChildRelation getJainSipProxySbb();
	public abstract ChildRelation getCallBlockingSbb();
	public abstract ChildRelation getCallForwardingSbb();


	private SbbContext sbbContext;
	private SipFactoryProvider fp;
	private MessageFactory messageFactory;
	
	 protected final MessageFactory getMessageFactory() { return messageFactory; }
	
	/**
	 * *****************************************
	 * ************** CMP Fields ***************
	 * *****************************************
	 */
	
}