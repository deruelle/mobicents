package com.ptin.xmpp.test;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.slee.*;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Packet;
import org.mobicents.slee.resource.xmpp.XmppActivityContextInterfaceFactory;
import org.mobicents.slee.resource.xmpp.XmppResourceAdaptor.XmppRASbbInterfaceImpl;

/**
 * @author Neutel
 * @version 1.0
 * 
 *	PTInovacao 2005
 * 
 */

public abstract class XmppTestSbb implements javax.slee.Sbb {
	
	private final String myComponent = "CHANGETHIS";
	
	private final String myUser = "testsbb";
	private final String myJID = myUser+"@"+myComponent;
	private String connectionID;						// The xmpp component connection id    	 
    private XmppRASbbInterfaceImpl xmppSbbInterface;	// The ra sbb interface
    private SbbContext sbbContext; 						// This SBB's SbbContext	
    
    /*
     * count the chars in the message recieved and send the result back
     */
	public void onMessage(org.jivesoftware.smack.packet.Message event, ActivityContextInterface aci) {
	    if (event.getTo().equals(myJID)) {
	    	System.out.println("!!!! XMPP Message event type !!!!");
	    	Message msg = new Message(event.getFrom(),event.getType());
	    	msg.setFrom(event.getTo());
	    	msg.setBody("The message <"+event.getBody()+"> as "+event.getBody().length()+" chars!!");
	    	xmppSbbInterface.sendPacket(connectionID, msg);
	    }
	}

	/*
	 * accepts subscriptions requests and informs the basic service in a message
	 */
	public void onPresence(org.jivesoftware.smack.packet.Presence packet, ActivityContextInterface aci) {
		if (packet.getTo().equals(myJID)) {
			System.out.println("!!!! XMPP Presence event type "+packet.getType()+"!!!!");
			Presence reply = null;	    
			if (packet.getType()==Presence.Type.SUBSCRIBE){
				System.out.println("!!!! SUBSCRIBE !!!!");	        
				reply = new Presence(Presence.Type.SUBSCRIBED);           
				reply.setFrom(packet.getTo());
				reply.setTo(packet.getFrom());
				xmppSbbInterface.sendPacket(connectionID, reply);
			}
			else if (packet.getType()==Presence.Type.AVAILABLE){
				System.out.println("!!!! AVAILABLE !!!!");
				reply = new Presence(Presence.Type.AVAILABLE);
				reply.setFrom(packet.getTo());
				reply.setTo(packet.getFrom());	        
				Message msg = new Message(packet.getFrom(),Message.Type.CHAT);
				msg.setFrom(packet.getTo());
				msg.setBody(packet.getFrom()+", welcome to the XMPP Test Service, send a message and I will count your chars!!");
				xmppSbbInterface.sendPacket(connectionID, reply);
				xmppSbbInterface.sendPacket(connectionID, msg);
			}
		}
	}	
	
	public void setSbbContext(SbbContext context) {
	    this.sbbContext = context;
	    //initRoom();
	    try {           
            Context myEnv = (Context) new InitialContext().lookup("java:comp/env");
            //get the ra sbb interface from the java env
            xmppSbbInterface = (XmppRASbbInterfaceImpl) myEnv.lookup("slee/resources/xmpp/2.0/xmppinterface");
            //get the component connection id from system properties
            if ((connectionID = System.getProperty("org.mobicents.slee.service.xmppcomponent.CONNECTIONID")) == null)
            		throw new NullPointerException("Component not connected or this sevrice does not have a valid configuration");            
	    } catch (NamingException ne) {
            System.out.println("Could not set SBB context:" + ne.getMessage());
        }    
	}
    public void unsetSbbContext() { this.sbbContext = null; }
    
    // TODO: Implement the lifecycle methods if required
    public void sbbCreate() throws javax.slee.CreateException {}
    public void sbbPostCreate() throws javax.slee.CreateException {}
    public void sbbActivate() {}
    public void sbbPassivate() {}
    public void sbbRemove() {}
    public void sbbLoad() {}
    public void sbbStore() {}
    public void sbbExceptionThrown(Exception exception, Object event, ActivityContextInterface activity) {}
    public void sbbRolledBack(RolledBackContext context) {}
	
	/**
	 * Convenience method to retrieve the SbbContext object stored in setSbbContext.
	 * 
	 * TODO: If your SBB doesn't require the SbbContext object you may remove this 
	 * method, the sbbContext variable and the variable assignment in setSbbContext().
	 *
	 * @return this SBB's SbbContext object
	 */
	
	protected SbbContext getSbbContext() {
		return sbbContext;
	}	

}
