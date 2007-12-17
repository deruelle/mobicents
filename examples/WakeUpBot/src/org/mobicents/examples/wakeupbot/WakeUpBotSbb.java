package org.mobicents.examples.wakeupbot;

import javax.slee.*;
import org.mobicents.slee.resource.xmpp.XmppResourceAdaptor.XmppRASbbInterfaceImpl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Properties;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.slee.ActivityContextInterface;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.SLEEException;
import javax.slee.SbbContext;
import javax.slee.TransactionRequiredLocalException;
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
import org.mobicents.examples.wakeupbot.events.WakeUpRequestEvent;
import org.mobicents.slee.resource.xmpp.*;

public abstract class WakeUpBotSbb implements javax.slee.Sbb {

	private Class[] packetsToListen = { Message.class, Presence.class };

	private String connectionID = "org.mobicents.examples.wakeupbot.WakeUpBotSbb";

	// PUT RELEVANT DATA HERE.
	private String serviceName = "gmail.com";

	private String resource = "MobicentsGoogleWakeUpBot";

	private String username = null;

	private String password = null;

	private String serviceHost = "talk.google.com";

	private int servicePort = 5222;
	/*
	 * private String serviceName = BotConf.getServiceName(); private String
	 * resource = BotConf.getResource(); private String username =
	 * BotConf.getUsername(); private String password = BotConf.getPassword();
	 * 
	 * private String serviceHost = BotConf.getServiceHost(); private int
	 * servicePort = BotConf.getServicePort()
	 */;

	private XmppRASbbInterfaceImpl xmppSbbInterface;

	private SbbContext sbbContext; // This SBB's SbbContext

	private ActivityContextInterface aci; // This activity context interface.

	static private transient Logger logger;
	static {
		logger = Logger.getLogger(XmppResourceAdaptor.class.toString());
	}

	// TODO: Perform further operations if required in these methods.
	public void setSbbContext(SbbContext context) {
		this.sbbContext = context;

		try {
			logger.info("Called setSbbContext PtinAudioConf!!!");
			Context myEnv = (Context) new InitialContext()
					.lookup("java:comp/env");
			xmppSbbInterface = (XmppRASbbInterfaceImpl) myEnv
					.lookup("slee/resources/xmpp/2.0/xmppinterface");

		} catch (NamingException ne) {
			logger.warning("Could not set SBB context:" + ne.getMessage());
		}
	}

	public void unsetSbbContext() {
		this.sbbContext = null;
	}

	/**
	 * Convenience method to retrieve the SbbContext object stored in
	 * setSbbContext.
	 * 
	 * TODO: If your SBB doesn't require the SbbContext object you may remove
	 * this method, the sbbContext variable and the variable assignment in
	 * setSbbContext().
	 * 
	 * @return this SBB's SbbContext object
	 */

	protected SbbContext getSbbContext() {
		return sbbContext;
	}

	/**
	 * Init the xmpp connection to GOOGLE TALK when the service is activated by
	 * SLEE.
	 * 
	 * @param event
	 *            javax.slee.serviceactivity.ServiceStartedEvent
	 * @param aci
	 *            ActivityContextInterface - Service ACI
	 */
	public void onStartServiceEvent(
			javax.slee.serviceactivity.ServiceStartedEvent event,
			ActivityContextInterface aci) {
		Context myEnv;

		// PROPERTIES FOR JNDI LOOKUP... IF NEEDED

		//Properties properties = new Properties();
		//properties.put("java.naming.factory.initial",
		//		"org.jnp.interfaces.NamingContextFactory");
		//properties.put("java.naming.factory.url.pkgs",
		//		"org.jboss.naming:org.jnp.interfaces");
		//properties.put("java.naming.provider.url", "jnp://192.168.1.100:1099");

		try {
			logger
					.info("START SERVICE EVENT!!!!! Trying to start WakeupBot Service");
			myEnv = (Context) new InitialContext()
					.lookup("java:comp/env");
			// check if it's my service that is starting
			ServiceActivity sa = ((ServiceActivityFactory) myEnv
					.lookup("slee/serviceactivity/factory")).getActivity();
			if (sa.equals(aci.getActivity())) {

				BotConf conf = BotConf.getBonConf();
				username = conf.getUsername();
				password = conf.getPassword();
				serviceHost = conf.getServiceHost();
				serviceName = conf.getServiceName();
				servicePort = conf.getServicePort();
				connectionID = conf.getConnectionID();
				// OTHER PROPS HAVE DEFAULTS
				if (username == null || password == null)
					throw new Exception("USERNAME OR PASSWORD IS NULL!!!");
				// connect to google talk xmpp server
				xmppSbbInterface.connectClient(connectionID, serviceHost,
						servicePort, serviceName, username, password, resource,
						Arrays.asList(packetsToListen));

			}
		} catch (XMPPException e) {
			System.out.println("Connection to server failed! Error:");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("FATAL ERROR: " + e.toString());
		}

	}

	/*
	 * Handler to disconnect from Google when service is deactivateds when the
	 * service is being deactivated
	 */

	public void onActivityEndEvent(ActivityEndEvent event,
			ActivityContextInterface aci) {
		try {
			Context myEnv = (Context) new InitialContext()
					.lookup("java:comp/env");
			// check if it's my service aci that is ending, it may be a client
			// or translator
			ServiceActivity sa = ((ServiceActivityFactory) myEnv
					.lookup("slee/serviceactivity/factory")).getActivity();
			if (sa.equals(aci.getActivity())) {
				xmppSbbInterface.disconnect(connectionID);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * We got TimerEvent, have to lookup WakeUpQueues stored in CMP fields to
	 * send first request in queues. Queues are TreeMaps with java.util.Date as
	 * keys, so Dates are sorted in ascending order.
	 * 
	 * @param event
	 *            Wake up made by SLEE TimerFacility.
	 * @param aci
	 *            ActivityContextInterface object of this activity.
	 */
	public void onTimeEvent(javax.slee.facilities.TimerEvent event,
			ActivityContextInterface aci) {
		// We have to send first request ;]
		TreeMap localQueue;
		localQueue = getWakeUpQueue();
		if (!(localQueue.size() > 0)) // just to be sure
			return;
		// LETS GET FIRST DATE, its EARLIEST DATE IN KEYS
		Date date = (Date) localQueue.firstKey();
		// NOW WE NEED UID AND DATE WHICH WE WILL SHOW TO USER IN MESSAGE
		RequestHolder RH=(RequestHolder) localQueue.remove(date);
		String UID = RH.getUID();
		Date userDate =RH.getDate();
		// LETS CREATE BODY OF OUR MESSAGE
		String body = "Hi. I am wakeup Bot. You have requested wakeup call.\n\nINFO:\nUser:"
				+ UID + "\nRequest date:" + userDate;
		logger.info("SENDING WAKE UP REQUEST: " + body);

		// LETS SEND IT TO SOMEONE WHO NOWS WHAT TO DO WITH IT.
		forwardRequest(body, UID);
		// MESSAGE SENT, LETS ATTACH NEW CLOCK IF WE HAVE REQUEST
		if (localQueue.size() > 0) {
			
			date = (Date) localQueue.firstKey();
			UID = ((RequestHolder) localQueue.get(date)).getUID();
			Address address = new Address(AddressPlan.SMTP, UID);
			attachTimer(date, address);
		} else
			setLastTimerID(null); // WE DONT HAVE ANY TIMER TICKING, DONT NEED
									// GARBAGE HERE
	}

	/**
	 * Already have chat session, someone is trying to speak with us, let's
	 * advertise our service or respond to command -- "/list".
	 * 
	 * @param message
	 *            Message we got from user at the other end of connection.
	 * @param aci
	 *            ActivityContextInterface object of this activity.
	 */

	public void onMessage(org.jivesoftware.smack.packet.Message message,
			ActivityContextInterface aci) {
		// check if this event is "to" me
		if (message.getTo().equals(username + "@" + serviceName)|| message.getTo().equals(username + "@" + serviceName + "/" + resource)) {

			logger.info("!!!! XMPP Message event type !!!!");
			logger.info("XMPP Message body: " + message.getBody());
			logger.info("XMPP Message To: " + message.getTo());
			logger.info("XMPP Message From: " + message.getFrom());
			if (message.getType() == Message.Type.ERROR) {
				return;
			}
			String body;
			if (message.getBody().indexOf("/list") == -1)
				body = "Hi. I'm wakeup bot. I'm up and ready to wake users.\nAvaible commands are:\n\"/list\" - will list wakeup request for current user.";
			else {
				// WE NEED TO LIST THOSE REQUESTS ;]
				StringBuffer sb = new StringBuffer(
						"Wakeups are schduled as follows:\n");
				// TREE MAPS ITERATORS ARE SORTED IN ASCENDING ORDER
				Iterator it = getWakeUpQueue().values().iterator();
				while (it.hasNext()) {
					
					RequestHolder RH=(RequestHolder) it.next();
					sb.append(RH.getDate() + "\n");
					
				}
				
				body = sb.toString();
				
			}
			// LETS SEND IT TO SOMEONE WHO KNOWS WHAT TO DO WITH IT.
			forwardRequest(body, message.getFrom());
		}
	}

	/**
	 * Simple presence advertisment. If someones status changed to AVAILABLE,
	 * lets advertise
	 * 
	 * @param packet
	 * @param aci
	 */
	public void onPresence(org.jivesoftware.smack.packet.Presence packet,
			ActivityContextInterface aci) {
		// check if this event is "to" me
		if (packet.getTo().equals(username + "@" + serviceName)
				|| packet.getTo().equals(
						username + "@" + serviceName + "/" + resource)) {

			logger.info("!!!! XMPP Presence event type " + packet.getType()
					+ ". Sent by " + packet.getFrom() + "Sent to:"
					+ packet.getTo() + "!!!!");
			// reply hello msg if receives notification of available presence
			// state
			if (packet.getType() == Presence.Type.AVAILABLE) {
				String body = "Hi. I'm wakeup bot. I'm up and ready to wake users.\nAvaible commands are:\n\"/list\" - will list wakeup request for current user.";

				forwardRequest(body, packet.getFrom());
			}
		}
	}

	/**
	 * We got new reques. We have to store it in queues and create timer. For
	 * each request timer is created immediately. Requests are stored within
	 * TreeMap's with requests Date as key, so the "youngest" request is first
	 * in queue.
	 * 
	 * 
	 * @param event
	 * @param aci
	 */
	public void onRequest(
			org.mobicents.examples.wakeupbot.events.WakeUpRequestEvent event,
			ActivityContextInterface aci) {
		// WE GOT WAKEUP REQUEST FROM OUTSIDE
		logger.info("CREATING NEW REQUEST FOR:" + event.getUID()
				+ ".User time Milis:" + event.getTimeMillisDifference()
				+ ".user Time:" + event.getDate());

		// LETS STORE REQUEST IN QUEUES
		TreeMap localQueue, foreignQueue;
		localQueue = getWakeUpQueue();
		Date date = Calendar.getInstance().getTime();
		// We should send request when currentDate > date so:
		// LETS CREATE LOCAL DATE WHICH WILL INDICATE EXACT MOMENT IN TIME WHEN
		// REQUEST SHOULD BE SENT
		// IT HAS TO POINT SERVER LOCAL TIME
		date.setTime(date.getTime() + event.getTimeMillisDifference());
		RequestHolder RH=new RequestHolder();
		RH.setDate(event.getDate());
		RH.setUID(event.getUID());
		localQueue.put(date, RH);
		logger.info("REQUEST LIST: " + localQueue);

		// WE NEED A CLOCK TICKING FOR US
		if (localQueue.size() > 1) {

			if (localQueue.firstKey().equals(date)) {

				// TRUE?-IF WE GOT HERE IT MEANS THAT CURRENT REQUEST SHOULD BE
				// SENT AS FIRST
				// WE HAVE TO CANCEL CURRENTLY TICKING TIMER AND CREATE NEW ONE
				// FOR CURRENT REQUEST
				getTimerFacility().cancelTimer(getLastTimerID());
				setLastTimerID(null);
				Address address = new Address(AddressPlan.SMTP, event.getUID());
				attachTimer(date, address);
			}
			// FALSE?? WE DONT NEED ANY MAGIC WTH TIMERS, ONE THAT'S TICKING IS FINE
			return;
		}
		// QUEUE SHOULD HAVE EXACTLY 1 REQUEST, LETS CREATE TIMER FOR IT.

		Address address = new Address(AddressPlan.SMTP, event.getUID());
		attachTimer(date, address);

	}

	public abstract void firePacketRequest(PacketSendRequestEvent request,
			ActivityContextInterface aci, Address address);

	/**
	 * Uses
	 * {@link #firePacketRequest(PacketSendRequestEvent request,ActivityContextInterface aci, Address address)}
	 * method to forward request to other SBB.
	 * 
	 * @param body
	 *            Body of message that will be sent.
	 * @param UID
	 *            User ID to which message should be sent. xxxx@gmail.com
	 */
	protected void forwardRequest(String body, String UID) {
		PacketSendRequestEvent event = new PacketSendRequestEvent();
		event.setMessageBody(body);
		event.setUID(UID);
		ActivityContextInterface nullACI = getPacketSenderNullActivityContext();
		logger.info("FORWARDING REQUEST UID:" + UID + ".");
		firePacketRequest(event, nullACI, null);

	}

	/**
	 * 
	 * @return First sbbs child local object. Child is
	 *         "WakeUpBotPacketSenderSbb". It return first child from child
	 *         Collection.
	 */
	private SbbLocalObject getPacketSenderLocalObject() {
		Iterator it = getChildRelationPacketSender().iterator();
		SbbLocalObject packetLO = null;
		if (it.hasNext()) {
			packetLO = (SbbLocalObject) it.next();
		}
		return packetLO;
	}

	/**
	 * Initializes this sbb. Creates requests queues and nullACs used to firing
	 * and receiving requests
	 */
	protected void initialize() {
		logger.info("INITIALIZING WAKE UP SBB!!!");
		// LETS CREATE PACKET SENDER --> Child Sbb
		initializeChildPacketSender();
		// It would be strange when one of those variables wil be null and other
		// not,but... we have to check
		if (getWakeUpQueue() == null)
			setWakeUpQueue(new TreeMap());
		if (getTimerNullActivityContext() == null) {
			ActivityContextInterface localACI = retrieveNullActivityContext();
			localACI.attach(sbbContext.getSbbLocalObject());
			setTimerNullActivityContext(localACI);

		}
		if (getPacketSenderNullActivityContext() == null) {
			ActivityContextInterface localACI = retrieveNullActivityContext();
			setPacketSenderNullActivityContext(localACI);
			localACI.attach(getPacketSenderLocalObject());
		}

	}

	/**
	 * Attaches new Timer to nullAC stored in CMP field
	 * timerNullActivityContext. This sbb is attached to this nullAC.
	 * 
	 * @param dateToFire -
	 *            Date upon which the TimerEvent should be fired to this SBB.
	 * @param address -
	 *            Address which specifies this sbb. Addres.AddressType==SNMP.
	 *            xxxx@gmail.com
	 */
	protected void attachTimer(Date dateToFire, Address address) {
		logger.info("ATTACHING NEW TIMER!!");
		TimerFacility tf;
		if ((tf = getTimerFacility()) == null) {
			tf = retrieveTimerFacility();
			setTimerFacility(tf);
		}

		TimerOptions tOptions = new TimerOptions(true, 5000,
				TimerPreserveMissed.LAST); // our Timer will be persistent,
											// Timer Events after 5 seconds will
											// be considered late, only last
											// "late" event will be resent.
		TimerID lastTimerID = tf.setTimer(getTimerNullActivityContext(),
				address, dateToFire.getTime(), tOptions);
		setLastTimerID(lastTimerID);

	}

	/**
	 * Encapsulates JNDI lookups for creation of nullActivityContextInterface.
	 * 
	 * @return New NullActivityContextInterface.
	 */
	protected ActivityContextInterface retrieveNullActivityContext() {
		ActivityContextInterface localACI = null;
		NullActivityFactory nullACFactory = null;
		NullActivityContextInterfaceFactory nullActivityContextFactory = null;
		try {
			logger.info("Creating nullActivity!!!");
			Context myEnv = (Context) new InitialContext()
					.lookup("java:comp/env");
			nullACFactory = (NullActivityFactory) myEnv
					.lookup("slee/nullactivity/factory");
			NullActivity na = nullACFactory.createNullActivity();
			nullActivityContextFactory = (NullActivityContextInterfaceFactory) myEnv
					.lookup("slee/nullactivity/activitycontextinterfacefactory");
			localACI = nullActivityContextFactory
					.getActivityContextInterface(na);

		} catch (NamingException ne) {
			logger.warning("Could not create nullActivityFactory: "
					+ ne.getMessage());
		} catch (UnrecognizedActivityException UAE) {
			logger
					.warning("Could not create nullActivityContextInterfaceFactory: "
							+ UAE.getMessage());
		}
		return localACI;
	}

	/**
	 * Does JNDI lookup to create new reference to TimerFacility. If its
	 * successful it stores it in CMP field "timerFacility" and returns this
	 * reference.
	 * 
	 * @return TimerFacility object reference
	 */
	protected TimerFacility retrieveTimerFacility() {
		try {

			Context myEnv = (Context) new InitialContext()
					.lookup("java:comp/env");
			TimerFacility tf = (TimerFacility) myEnv
					.lookup("slee/facilities/timer");
			setTimerFacility(tf);
			return tf;
		} catch (NamingException NE) {
			logger.info("COULDNT GET TIMERFACILITY: " + NE.getMessage());
		}
		return null;
	}

	/**
	 * CMP FIELDS
	 */
	public abstract void setWakeUpQueue(TreeMap lst);

	public abstract TreeMap getWakeUpQueue();

	public abstract void setTimerNullActivityContext(
			ActivityContextInterface timerNull);

	public abstract ActivityContextInterface getTimerNullActivityContext();

	public abstract void setPacketSenderNullActivityContext(
			ActivityContextInterface timerNull);

	public abstract ActivityContextInterface getPacketSenderNullActivityContext();

	public abstract void setTimerFacility(TimerFacility tf);

	public abstract TimerFacility getTimerFacility();

	public abstract TimerID getLastTimerID();

	public abstract void setLastTimerID(TimerID tid);

	/**
	 * Initial event selector method for XMPP events method.
	 */
	public InitialEventSelector initialEventCheckMethod(InitialEventSelector ies) {
		// WE will receive either Message or Presence packetrs -> subclasses of
		// Packet
		logger.info("INITIAL EVENT SELECTOR METHOD CALLED!!! for: "
				+ ((Packet) ies.getEvent()).getFrom());
		org.jivesoftware.smack.packet.Packet event = (Packet) ies.getEvent();

		// UID will propably look like xxxx@wp.pl/resourceString
		//we dont need resource string, lets loose it
		String UID = event.getFrom().split("/")[0];
		Address address = new Address(AddressPlan.SMTP, UID);
		//now use address as initial event selector
		ies.setAddress(address);
		ies.setAddressSelected(true);
		return ies;

	}

	/**
	 * CHILD RELATION METHOD
	 */
	public abstract ChildRelation getChildRelationPacketSender();

	/**
	 * CHILD INITIALIZATION METHOD
	 */
	private void initializeChildPacketSender() {
		ChildRelation children = getChildRelationPacketSender();
		if (children.size() > 1) {

			return;
		}
		try {
			children.create();
			logger.info("CREATED PACKET SENDER CHILD SBB");

		} catch (TransactionRequiredLocalException e) {
			logger.info("FAILED TO CREATE PACKET SENDER:" + e.getMessage());

		} catch (SLEEException e) {
			logger.info("FAILED TO CREATE PACKET SENDER:" + e.getMessage());

		} catch (CreateException e) {
			logger.info("FAILED TO CREATE PACKET SENDER:" + e.getMessage());

		}

	}

	// TODO: Implement the lifecycle methods if required
	public void sbbCreate() throws javax.slee.CreateException {
	}

	public void sbbPostCreate() throws javax.slee.CreateException {
		initialize();
	}

	public void sbbActivate() {
	}

	public void sbbPassivate() {
	}

	public void sbbRemove() {
		// SbbLocalObject localOB =getPacketSenderLocalObject();
		// getPacketSenderNullActivityContext().detach(localOB);
		// localOB.remove(); // IT SHOULD BE DONE AUTOMATICLY

	}

	public void sbbLoad() {
	}

	public void sbbStore() {
	}

	public void sbbExceptionThrown(Exception exception, Object event,
			ActivityContextInterface activity) {
	}

	public void sbbRolledBack(RolledBackContext context) {
	}

	public abstract WakeUpBotSbbActivityContextInterface asSbbActivityContextInterface(
			ActivityContextInterface aci);
}
