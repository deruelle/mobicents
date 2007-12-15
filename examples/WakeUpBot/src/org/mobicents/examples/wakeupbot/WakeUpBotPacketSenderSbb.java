package org.mobicents.examples.wakeupbot;
import org.mobicents.slee.resource.xmpp.XmppResourceAdaptor.XmppRASbbInterfaceImpl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;
import java.util.Properties;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.slee.ActivityContextInterface;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.SbbContext;
import javax.slee.facilities.TimerFacility;
import javax.slee.facilities.TimerID;
import javax.slee.facilities.TimerOptions;
import javax.slee.facilities.TimerPreserveMissed;
import javax.slee.nullactivity.NullActivity;
import javax.slee.nullactivity.NullActivityContextInterfaceFactory;
import javax.slee.nullactivity.NullActivityFactory;
import javax.slee.serviceactivity.ServiceActivity;
import javax.slee.serviceactivity.ServiceActivityFactory;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.XMPPException;

import org.mobicents.examples.wakeupbot.events.PacketSendRequestEvent;
import org.mobicents.slee.resource.xmpp.*;
public abstract class WakeUpBotPacketSenderSbb implements javax.slee.Sbb {

	private String connectionID = "org.mobicents.examples.wakeupbot.WakeUpBotSbb";

	// PUT RELEVANT DATA HERE.
	
	private String username = "ezgar.pl";
	private String resource = "MobicentsWakeUpBot";
	private String serviceName = "gmail.com";
	private XmppRASbbInterfaceImpl xmppSbbInterface;
	private SbbContext sbbContext; // This SBB's SbbContext
	 // This activity context interface.
	// static Strings passed to XMPP arent propablly allowed
	/*private String username = BotConf.getUsername();
	private String resource = BotConf.getResource();
	private String serviceName = BotConf.getServiceName(); 
	*/
	static private transient Logger logger;
	 
	static {
		logger = Logger.getLogger(XmppResourceAdaptor.class.toString());
	}
	
	
	public void onPacketRequest(org.mobicents.examples.wakeupbot.events.PacketSendRequestEvent request,ActivityContextInterface aci)
	{
		logger.info("SENDING MESSAGE");
		Message msg=new Message();
		msg.setBody(request.getMessageBody());
		msg.setType(Message.Type.CHAT);
		msg.setTo(request.getUID());
		//msg.setFrom(username+"@"+serviceName+"/"+resource);
		xmppSbbInterface.sendPacket(connectionID,msg);
	}
	
	public void setSbbContext(SbbContext context) {
		this.sbbContext = context;

		try {
			logger.info("Called setSbbContext PtinAudioConf!!!");
			Context myEnv = (Context) new InitialContext().lookup("java:comp/env");
			xmppSbbInterface = (XmppRASbbInterfaceImpl) myEnv.lookup("slee/resources/xmpp/2.0/xmppinterface");

		} catch (NamingException ne) {
			logger.warning("Could not set SBB context:" + ne.getMessage());
		}
		//we need this info.
		BotConf conf=BotConf.getBonConf();
		username=conf.getUsername();
		serviceName=conf.getServiceName();
		connectionID=conf.getConnectionID();
		
	}

	public void unsetSbbContext() {
		// TODO Auto-generated method stub
		
	}

	public void sbbCreate() throws CreateException {
		// TODO Auto-generated method stub
		
	}

	public void sbbPostCreate() throws CreateException {
		// TODO Auto-generated method stub
		
	}

	public void sbbActivate() {
		// TODO Auto-generated method stub
		
	}

	public void sbbPassivate() {
		// TODO Auto-generated method stub
		
	}

	public void sbbLoad() {
		// TODO Auto-generated method stub
		
	}

	public void sbbStore() {
		// TODO Auto-generated method stub
		
	}

	public void sbbRemove() {
		// TODO Auto-generated method stub
		
	}

	public void sbbExceptionThrown(Exception arg0, Object arg1, ActivityContextInterface arg2) {
		// TODO Auto-generated method stub
		
	}

	public void sbbRolledBack(RolledBackContext arg0) {
		// TODO Auto-generated method stub
		
	}

}
