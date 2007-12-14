package org.mobicents.slee.services.sip.proxy;

import gov.nist.javax.sip.address.SipUri;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sip.ClientTransaction;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipProvider;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionState;
import javax.sip.address.AddressFactory;
import javax.sip.header.CallIdHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.slee.ActivityContextInterface;
import javax.slee.ActivityEndEvent;
import javax.slee.ChildRelation;
import javax.slee.CreateException;
import javax.slee.FactoryException;
import javax.slee.InitialEventSelector;
import javax.slee.RolledBackContext;
import javax.slee.SLEEException;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.SbbID;
import javax.slee.SbbLocalObject;
import javax.slee.TransactionRequiredLocalException;
import javax.slee.UnrecognizedActivityException;
import javax.slee.facilities.ActivityContextNamingFacility;
import javax.slee.facilities.AlarmFacility;
import javax.slee.facilities.FacilityException;
import javax.slee.facilities.Level;
import javax.slee.facilities.NameAlreadyBoundException;
import javax.slee.facilities.NameNotBoundException;
import javax.slee.facilities.TimerFacility;
import javax.slee.facilities.TimerID;
import javax.slee.facilities.TimerOptions;
import javax.slee.facilities.TimerPreserveMissed;
import javax.slee.facilities.TraceFacility;
import javax.slee.nullactivity.NullActivity;
import javax.slee.nullactivity.NullActivityContextInterfaceFactory;
import javax.slee.nullactivity.NullActivityFactory;
import javax.slee.profile.ProfileFacility;
import javax.slee.serviceactivity.ServiceActivity;
import javax.slee.serviceactivity.ServiceActivityFactory;

import org.mobicents.slee.container.SleeContainer;
import org.mobicents.slee.resource.sip.SipActivityContextInterfaceFactory;
import org.mobicents.slee.resource.sip.SipFactoryProvider;

import org.mobicents.slee.services.sip.common.ProxyConfiguration;
import org.mobicents.slee.services.sip.common.ProxyConfigurationProvider;
import org.mobicents.slee.services.sip.common.SipLoopDetectedException;
import org.mobicents.slee.services.sip.mbean.ProxyConfigurator;

public abstract class ProxySbb implements Sbb, ProxyCallBackInterface {

	// We will use java loggin?
	private static Logger logger = Logger.getLogger(ProxySbb.class.getName());

	// ************************************************* SLEE STUFF
	private SbbContext context;

	private TraceFacility traceFacility;

	private TimerFacility timerFacility;

	private AlarmFacility alarmFacility;

	private ProfileFacility profileFacility;

	private ActivityContextNamingFacility namingFacility;

	private SbbID id;

	private NullActivityFactory nullActivityFactory;

	private NullActivityContextInterfaceFactory nullACIFactory;

	private Context myEnv;

	private static TimerOptions defaultTimerOptions = new TimerOptions(true,
			500, TimerPreserveMissed.LAST);

	// **************************************************** STATICS - JNDI NAMES
	private static final String JNDI_SERVICEACTIVITY_FACTORY = "java:comp/env/slee/serviceactivity/factory";

	private static final String JNDI_SERVICEACTIVITYACI_FACTORY = "java:comp/env/slee/serviceactivity/activitycontextinterfacefactory";

	private static final String JNDI_NULL_ACTIVITY_FACTORY = "java:comp/env/slee/nullactivity/factory";

	private static final String JNDI_NULL_ACI_FACTORY = "java:comp/env/slee/nullactivity/activitycontextinterfacefactory";

	private static final String JNDI_ACTIVITY_CONTEXT_NAMING_FACILITY = "java:comp/env/slee/facilities/activitycontextnaming";

	private static final String JNDI_TRACE_FACILITY = "java:comp/env/slee/facilities/trace";

	private static final String JNDI_TIMER_FACILITY_NAME = "java:comp/env/slee/facilities/timer";

	private static final String JNDI_ALARM_FACILITY_NAME = "java:comp/env/slee/facilities/alarm";

	private static final String JNDI_PROFILE_FACILITY_NAME = "java:comp/env/slee/facilities/profile";

	private static final String JNDI_SIP_PROVIDER_NAME = "java:comp/env/slee/resources/jainsip/1.2/provider";

	private static final String JNDI_SIP_ACIF_NAME = "java:comp/env/slee/resources/jainsip/1.2/acifactory";

	// *************************************************** SIP RELATED
	private SipFactoryProvider fp;

	private SipProvider provider;

	private AddressFactory addressFactory;

	private HeaderFactory headerFactory;

	private MessageFactory messageFactory;

	private SipActivityContextInterfaceFactory acif;

	private String configurationName = null;

	public void sbbActivate() {

	}

	public void sbbCreate() throws CreateException {

	}

	public void sbbExceptionThrown(Exception arg0, Object arg1,
			ActivityContextInterface arg2) {

	}

	public void sbbLoad() {

	}

	public void sbbPassivate() {

	}

	public void sbbPostCreate() throws CreateException {

	}

	public void sbbRemove() {

	}

	public void sbbRolledBack(RolledBackContext arg0) {

	}

	public void sbbStore() {

	}

	protected SbbContext getSbbContext() {
		// TODO Auto-generated method stub
		return this.context;
	}

	public void setSbbContext(SbbContext context) {

		this.context = context;
		try {
			id = context.getSbb();

			myEnv = new InitialContext();

			traceFacility = (TraceFacility) myEnv.lookup(JNDI_TRACE_FACILITY);
			timerFacility = (TimerFacility) myEnv
					.lookup(JNDI_TIMER_FACILITY_NAME);
			alarmFacility = (AlarmFacility) myEnv
					.lookup(JNDI_ALARM_FACILITY_NAME);
			profileFacility = (ProfileFacility) myEnv
					.lookup(JNDI_PROFILE_FACILITY_NAME);
			namingFacility = (ActivityContextNamingFacility) myEnv
					.lookup(JNDI_ACTIVITY_CONTEXT_NAMING_FACILITY);
			nullACIFactory = (NullActivityContextInterfaceFactory) myEnv
					.lookup(JNDI_NULL_ACI_FACTORY);
			nullActivityFactory = (NullActivityFactory) myEnv
					.lookup(JNDI_NULL_ACTIVITY_FACTORY);

			fp = (SipFactoryProvider) myEnv.lookup(JNDI_SIP_PROVIDER_NAME);

			provider = fp.getSipProvider();
			messageFactory = fp.getMessageFactory();
			headerFactory = fp.getHeaderFactory();
			addressFactory = fp.getAddressFactory();

			acif = (SipActivityContextInterfaceFactory) myEnv
					.lookup(JNDI_SIP_ACIF_NAME);
		} catch (NamingException ne) {
			logger.log(java.util.logging.Level.WARNING,
					"Could not set SBB context: ", ne);
		}

	}

	public void unsetSbbContext() {

	}

	/**
	 * Generate a custom convergence name so that events with the same call
	 * identifier will go to the same root SBB entity.
	 */
	public InitialEventSelector callIDSelect(InitialEventSelector ies) {
		Object event = ies.getEvent();
		String callId = null;
		if (event instanceof ResponseEvent) {
			// If response event, the convergence name to callId
			Response response = ((ResponseEvent) event).getResponse();
			callId = ((CallIdHeader) response.getHeader(CallIdHeader.NAME))
					.getCallId();
		} else if (event instanceof RequestEvent) {
			// If request event, the convergence name to callId
			Request request = ((RequestEvent) event).getRequest();
			callId = ((CallIdHeader) request.getHeader(CallIdHeader.NAME))
					.getCallId();
		}
		// Set the convergence name

		trace(Level.FINE, "Setting convergence name to: " + callId);

		ies.setCustomName(callId);
		return ies;
	}

	// ******************************** LEFT FROM OLD SIPPROXY - USE SLEE
	// FACILITIES TO LOG ESSENTIALS
	protected final void trace(Level level, String message) {
		try {
			traceFacility.createTrace(id, level, getTraceMessageType(),
					message, System.currentTimeMillis());
		} catch (Exception e) {
		}
	}

	protected final void trace(Level level, String message, Throwable t) {
		try {
			traceFacility.createTrace(id, level, getTraceMessageType(),
					message, t, System.currentTimeMillis());
		} catch (Exception e) {
		}
	}

	protected final void alarm(Level level, String message) {
		try {
			alarmFacility.createAlarm(id, level, getTraceMessageType(),
					message, System.currentTimeMillis());
		} catch (Exception e) {
		}
	}

	protected final void alarm(Level level, String message, Throwable t) {
		try {
			alarmFacility.createAlarm(id, level, getTraceMessageType(),
					message, t, System.currentTimeMillis());
		} catch (Exception e) {
		}
	}

	protected String getTraceMessageType() {
		return "ProxySbb";
	}

	// ****************************** ABSTRACT PARTS ********************
	// **** SLEE
	public abstract ChildRelation getRegistrarSbbChild();

	public abstract ProxySbbActivityContextInterface asSbbActivityContextInterface(
			ActivityContextInterface ac);

	// ***** CMPs - to create response context, with conjuction
	// public abstract void setServerTX(ServerTransaction tx);

	// public abstract ServerTransaction getServerTX();

	// public abstract void setForkedTransactionList(List forkedCTX);

	// public abstract List getForkedTransactionList();

	// public abstract void setAccumulatedResponses(Map accumulatedResponses);

	// public abstract Map getAccumulatedResponses();

	public abstract Map getLocalCancelByeTxMap();

	public abstract void setLocalCancelByeTxMap(Map map);

	public abstract int getFinishedBranches();

	public abstract void setFinishedBranches(int active);

	public abstract void setConfiguration(Object pc);

	public abstract Object getConfiguration();

	public abstract void setForwardedInviteViaHeader(ViaHeader value);
	
	public abstract ViaHeader getForwardedInviteViaHeader();
	
	// ********** Experimantal - usage of native iteration over aci over storing
	// them in cmps

	public List getForkedTransactionList() {
		ArrayList ret = new ArrayList();

		ActivityContextInterface myacis[] = getSbbContext().getActivities();

		for (int i = 0; i < myacis.length; i++) {

			Object activity = myacis[i].getActivity();
			if (activity instanceof ClientTransaction) {
				ret.add(activity);
			}

		}

		return ret;
	}

	public Map getAccumulatedResponses() {

		HashMap ret = new HashMap();

		ActivityContextInterface myacis[] = getSbbContext().getActivities();

		for (int i = 0; i < myacis.length; i++) {

			Object activity = myacis[i].getActivity();
			if (activity instanceof ClientTransaction) {
				ClientTransaction ctx = (ClientTransaction) activity;
				Object d = ctx.getApplicationData();
				if (d != null && d instanceof Response)
					ret.put(ctx, d);

			}

		}

		return ret;

	}

	public ServerTransaction getServerTX(String method) {

		ActivityContextInterface myacis[] = getSbbContext().getActivities();

		for (int i = 0; i < myacis.length; i++) {

			Object activity = myacis[i].getActivity();
			if (activity instanceof ServerTransaction) {
				ServerTransaction stx = (ServerTransaction) activity;
				Request req = stx.getRequest();

				if (!req.getMethod().equals(Request.CANCEL) && req.getMethod().equals(method))
					return stx;
			}

		}

		return null;
	}

	// ****************************** Helper methods
	protected void prepareENV() {
		try {
			if (getConfiguration() == null)
				try {
					logger
							.info("[SipProxy][^^^][No conf present, obtainign one]");
					Context myEnv = (Context) new InitialContext()
							.lookup("java:comp/env");

					// env-entries
					configurationName = (String) myEnv
							.lookup("configuration-MBEAN");
					ProxyConfiguration conf = ProxyConfigurationProvider
							.getCopy(configurationName);

					setConfiguration(conf);

				} catch (NamingException ne) {
					logger.warning("Could not set SBB context:"
							+ ne.getMessage());
				}
		} catch (Exception e) {
			// e.printStackTrace();
			// This will happen if event is ServiceStart ????
		}

		// if (getAccumulatedResponses() == null)
		// setAccumulatedResponses(new HashMap());
		// if (getForkedTransactionList() == null)
		// setForkedTransactionList(new ArrayList());
		if (getLocalCancelByeTxMap() == null)
			setLocalCancelByeTxMap(new HashMap());

	}

	protected void clearENV() {

		try {
			// if (getConfiguration() != null)
			// try {
			logger.info("Clearing environment, removing mbean");
			Context myEnv = (Context) new InitialContext()
					.lookup("java:comp/env");

			// env-entries
			configurationName = (String) myEnv.lookup("configuration-MBEAN");

			MBeanServer mbs = SleeContainer.lookupFromJndi().getMBeanServer();
			ObjectName on = new ObjectName(ProxyConfiguration.MBEAN_NAME_PREFIX
					+ configurationName);
			mbs.unregisterMBean(on);
			// } catch (NamingException ne) {
			// logger.warning("Could not set SBB context:"
			// + ne.getMessage());
			// }
		} catch (Exception e) {
			e.printStackTrace();
			// This will happen if event is ServiceStart ????
		}
	}

	/**
	 * Checks if proxys ancestor has processed call, if so it sets attribute
	 * aliased with <b>"handledByProxy"</b> to true and retuns <b>false</b>.
	 * <br>
	 * If proxys ancestor hasnt processed this call it sets attribute aliased
	 * with <b>"handledByProxy"</b> to true and retuns <b>true</b> <br>
	 * <b> Variable alliased by "handledByProxy" is always set to true!!!
	 * 
	 * @param aci -
	 *            proxys aci.
	 * @return
	 *            <li><b>true</b> - if proxy should process this message.
	 *            <li><b>false</b> - otherwise.
	 */
	protected boolean proxyProcess(ProxySbbActivityContextInterface proxyACI) {

		// LETS CHECK IF OUR ANCESTOR HAS PROCESSED THIS CALL.
		if (proxyACI.getHandledByAncestor()) {
			// OUR ANCESTOR HAS PROCESSED THIS CALL, WE SHOULD INFORM OUR
			// DESCENDANTS IN CHAIN
			proxyACI.setHandledByMe(true);
			return false;
		} else {
			proxyACI.setHandledByMe(true);
			return true;
		}

	}

	protected void startMBeanConfigurator() {

		ProxyConfigurator proxy = new ProxyConfigurator();
		// proxy.setSipHostName(provider.getListeningPoints()[0].getIPAddress());
		// proxy.setSipPort(provider.getListeningPoints()[0].getPort());
		// proxy.setSipTransport(provider.getListeningPoints()[0].getTransport());

		proxy.setSipHostName(fp.getHostAddress());
		proxy.setSipPort(fp.getHostPort());
		proxy.setSipTransports(fp.getTransports());

		String confValue = null;
		Context myEnv = null;
		try {
			logger.info("Building Configuration from ENV Entries");
			myEnv = (Context) new InitialContext().lookup("java:comp/env");

		} catch (NamingException ne) {
			logger.warning("Could not set SBB context:" + ne.getMessage());
		}

		// env-entries
		try {
			confValue = (String) myEnv.lookup("configuration-URI-SCHEMES");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (confValue == null) {
			proxy.addSupportedURIScheme("sip");
			proxy.addSupportedURIScheme("sips");
		} else {
			String[] tmp = confValue.split(";");
			for (int i = 0; i < tmp.length; i++)
				proxy.addSupportedURIScheme(tmp[i]);
		}

		confValue = null;

		try {

			confValue = (String) myEnv.lookup("configuration-LOCAL-DOMAINS");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (confValue == null) {
			// Domains should be present in conf file, it shouldnt do much harm
			// to add those
			proxy.addLocalDomain("nist.gov");
			proxy.addLocalDomain("mobicents.org");
		} else {
			String[] tmp = confValue.split(";");
			for (int i = 0; i < tmp.length; i++)
				proxy.addLocalDomain(tmp[i]);
		}

		confValue = null;

		try {

			confValue = (String) myEnv.lookup("configuration-MUST-PASS");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

		if (confValue == null) {
			// WE DONT KNOW WHAT TO DO.... kernel panic
		} else {
			String[] tmp = confValue.split(";");
			Object[] o = proxy.getPassThroughList();
			int initialSize = o.length;
			for (int i = 0; i < tmp.length; i++)
				proxy.addMustPassThrough(initialSize++, tmp[i]);
		}

		confValue = null;

		try {

			confValue = (String) myEnv.lookup("configuration-C-TIMEOUT");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (confValue == null) {
			// WE DONT KNOW WHAT TO DO.... kernel panic
			// LETS ALLOW DEFUALT VALUE
			proxy.setCTimerTimeOut(0);
		} else {
			double d = Double.parseDouble(confValue);
			proxy.setCTimerTimeOut(d);
		}

		confValue = null;

		try {

			confValue = (String) myEnv.lookup("configuration-MAX-EXPIRES");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (confValue == null) {
			// WE DONT KNOW WHAT TO DO.... kernel panic
			// LETS ALLOW DEFUALT VALUE
			proxy.setSipRegistrationMaxExpires(0);
		} else {
			int i = Integer.parseInt(confValue);
			proxy.setSipRegistrationMaxExpires(i);
		}

		confValue = null;

		try {

			confValue = (String) myEnv.lookup("configuration-MIN-EXPIRES");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (confValue == null) {
			// WE DONT KNOW WHAT TO DO.... kernel panic
			// LETS ALLOW DEFUALT VALUE
			proxy.setSipRegistrationMinExpires(0);
		} else {
			int i = Integer.parseInt(confValue);
			proxy.setSipRegistrationMinExpires(i);
		}

		// GO ;] start service
		proxy.startService();

	}

	// ***************************** EVENT HANLDERS

	public void onServiceStarted(
			javax.slee.serviceactivity.ServiceStartedEvent serviceEvent,
			ActivityContextInterface aci) {
		// This is called when ANY service is started.
		logger.fine("Got a Service Started Event!");
		logger.fine("CREATING CONFIGURRATION");

		startMBeanConfigurator();

	}

	/*
	 * Handler to disconnect from Google when the service is being deactivated
	 */

	public void onActivityEndEvent(ActivityEndEvent event,
			ActivityContextInterface aci) {
		try {
			Object activity = aci.getActivity();
			if (activity instanceof ServiceActivity) {
				Context myEnv = (Context) new InitialContext()
				.lookup("java:comp/env");
				// check if it's my service aci that is ending
				ServiceActivity sa = ((ServiceActivityFactory) myEnv
						.lookup("slee/serviceactivity/factory")).getActivity();
				if (sa.equals(activity)) {
					logger.info("Service aci ending, removing mbean");
					// lets remove our mbean
					clearENV();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processRequest(ServerTransaction serverTransaction,
			Request request, ActivityContextInterface ac) {
		trace(Level.FINEST, "processRequest: request = \n" + request.toString());
		try {

			ProxySbbActivityContextInterface sipaci = asSbbActivityContextInterface(ac);

			if (sipaci.getTransactionTerminated()) {
				return; // nothing to do
			}

			// LETS CHECK IF THIS CALL SHOULD BE PROCESSED BY PROXY
			if (!proxyProcess(sipaci)) {
				trace(Level.FINE,
						"\n===============\nLEAVEING CALL:|\n===============\n"
								+ ((CallIdHeader) request
										.getHeader(CallIdHeader.NAME))
										.getCallId()
								+ "\n==============================");
				// WE SHOULD NOT PROCESS THIS REQUEST
				return;
			}
			trace(Level.FINE,
					"\n================+\nPROCESSING CALL:|\n================\n"
							+ ((CallIdHeader) request
									.getHeader(CallIdHeader.NAME)).getCallId()
							+ "\n================================");
			// Create a worker to process this event
			// MySipProxy proxy = new MySipProxy(this, sipaci);

			SbbLocalObject o = null;
			if (getRegistrarSbbChild().size() == 0)
				o = getRegistrarSbbChild().create();
			else
				o = (SbbLocalObject) getRegistrarSbbChild().iterator().next();

			prepareENV();
			ProxyMachine proxy = new ProxyMachine(
					(ProxyConfiguration) getConfiguration(),
					(RegistrationInformationAccess) o, addressFactory,
					headerFactory, messageFactory, provider, this,
					(ProxyConfiguration) getConfiguration());

			// if (getServerTX() == null)
			// setServerTX(serverTransaction);
			// Go - if it is invite here, serverTransaction can be CANCEL
			// transaction!!!! so we dont want to overwrite it above
			proxy.processRequest(serverTransaction, request,
					getForkedTransactionList(),
					(RegistrationInformationAccess) o);

		} catch (Exception e) {
			// Send error response so client can deal with it
			trace(Level.WARNING, "Exception during processRequest", e);
			try {
				serverTransaction.sendResponse(messageFactory.createResponse(
						Response.SERVER_INTERNAL_ERROR, request));
			} catch (Exception ex) {
				trace(Level.WARNING, "Exception during processRequest", e);
			}
		}

	}

	private void processResponse(ClientTransaction clientTransaction,
			Response response, ActivityContextInterface ac) {
		trace(Level.FINEST, "processResponse: response = \n"
				+ response.toString());
		try {

			ProxySbbActivityContextInterface sipaci = asSbbActivityContextInterface(ac);

			// LETS CHECK IF THIS CALL SHOULD BE PROCESSED BY PROXY
			if (!proxyProcess(sipaci)) {
				trace(Level.FINE,
						"\n===============\nLEAVEING CALL:|\n===============\n"
								+ ((CallIdHeader) response
										.getHeader(CallIdHeader.NAME))
										.getCallId()
								+ "\n==============================");
				// WE SHOULD NOT PROCESS THIS RESPONSE
				return;
			}
			trace(Level.FINE,
					"\n================+\nPROCESSING CALL:|\n================\n"
							+ ((CallIdHeader) response
									.getHeader(CallIdHeader.NAME)).getCallId()
							+ "\n================================");
			// Create a worker to process this event
			// MySipProxy proxy = new MySipProxy(this, sipaci);
			clientTransaction.setApplicationData(response);
			SbbLocalObject o = null;
			if (getRegistrarSbbChild().size() == 0)
				o = getRegistrarSbbChild().create();
			else
				o = (SbbLocalObject) getRegistrarSbbChild().iterator().next();
			prepareENV();
			ProxyMachine proxy = new ProxyMachine(
					(ProxyConfiguration) getConfiguration(),
					(RegistrationInformationAccess) o, addressFactory,
					headerFactory, messageFactory, provider, this,
					(ProxyConfiguration) getConfiguration());

			// Go
			ServerTransaction serverTransaction = getServerTransaction(clientTransaction);
			if (serverTransaction != null) {
				proxy.processResponse(serverTransaction,
					clientTransaction, response, getForkedTransactionList(),
					getAccumulatedResponses(), getLocalCancelByeTxMap());
			}

		} catch (Exception e) {
			// Send error response so client can deal with it
			trace(Level.WARNING, "Exception during processResponse", e);
		}

	}

	public void onRegisterEvent(RequestEvent event, ActivityContextInterface ac) {
		// getDefaultSbbUsageParameterSet().incrementNumberOfRegister(1);
		trace(Level.INFO, "Received REGISTER request, class="
				+ event.getClass());
		try {

			// is local domain?

			// LETS CHECK IF THIS CALL SHOULD BE PROCESSED BY PROXY
			ProxySbbActivityContextInterface sipaci = asSbbActivityContextInterface(ac);
			if (!proxyProcess(sipaci)) {
				trace(Level.FINE,
						"\n===============\nLEAVEING CALL:|\n===============\n"
								+ ((CallIdHeader) event.getRequest().getHeader(
										CallIdHeader.NAME)).getCallId()
								+ "\n==============================");
				// WE SHOULD NOT PROCESS THIS RESPONSE
				return;
			}
			trace(Level.FINE,
					"\n================+\nPROCESSING CALL:|\n================\n"
							+ ((CallIdHeader) event.getRequest().getHeader(
									CallIdHeader.NAME)).getCallId()
							+ "\n================================");
			// create registrar child SBB
			ChildRelation relation = getRegistrarSbbChild();
			SbbLocalObject child = null;
			if (relation.size() == 0)
				child = relation.create();
			else
				child = (SbbLocalObject) relation.iterator().next();

			// attach child to this activity
			ac.attach(child);

			// detach myself
			ac.detach(getSbbContext().getSbbLocalObject());

			// Event router will pass this event to child SBB

		} catch (Exception e) {
			trace(Level.WARNING, "Exception during onRegisterEvent", e);
		}
	}

	public void onInviteEvent(RequestEvent event, ActivityContextInterface ac) {
		// getDefaultSbbUsageParameterSet().incrementNumberOfInvite(1);
		trace(Level.INFO, "Received INVITE request");
		try {
			Request request = event.getRequest();

			ServerTransaction serverTransaction = (ServerTransaction) ac
					.getActivity();
			trace(Level.FINE, "Server transacton is " + serverTransaction);
			processRequest(serverTransaction, request, ac);
		} catch (Exception e) {
			trace(Level.WARNING, "Exception during onInviteEvent", e);
		}
	}

	public void onByeEvent(RequestEvent event, ActivityContextInterface ac) {
		if (getForwardedInviteViaHeader() != null) {
			// getDefaultSbbUsageParameterSet().incrementNumberOfBye(1);
			trace(Level.INFO, "Received BYE request");
			try {

				Request request = event.getRequest();
				ServerTransaction serverTransaction = (ServerTransaction) ac
				.getActivity();
				// ServerTransaction serverTransaction =
				// event.getServerTransaction();
				processRequest(serverTransaction, request, ac);
				// long start = this.getTimeStarted();
				// long callLength = System.currentTimeMillis() - start;
				// trace(Level.INFO, "Call Length = " + callLength / 1000 + "
				// seconds");

			} catch (Exception e) {
				trace(Level.WARNING, "Exception during onByeEvent", e);
			}
		}
		else {
			ac.detach(context.getSbbLocalObject());
		}
	}

	public void onCancelEvent(RequestEvent event, ActivityContextInterface ac) {
		if (getForwardedInviteViaHeader() != null) {
			trace(Level.INFO, "Received CANCEL request");
			try {

				Request request = event.getRequest();
				ServerTransaction serverTransaction = event.getServerTransaction();

				// CANCELs are hop-by-hop, so here we respond immediately on the
				// server txn, if the RA didn't do it
				if ((serverTransaction.getState() != TransactionState.TERMINATED)
						&& (serverTransaction.getState() != TransactionState.COMPLETED)
						&& (serverTransaction.getState() != TransactionState.CONFIRMED)) {
					serverTransaction.sendResponse(messageFactory.createResponse(
						Response.OK, request));
				}

				// This will generate a new CANCEL request that originates from the
				// proxy
				processRequest(serverTransaction, request, ac);

			} catch (Exception e) {
				trace(Level.WARNING, "Exception during onCancelEvent", e);
			}
		}
		else {
			ac.detach(context.getSbbLocalObject());
		}
	}

	public void onAckEvent(RequestEvent event, ActivityContextInterface ac) {
		// getDefaultSbbUsageParameterSet().incrementNumberOfAck(1);
		if (getForwardedInviteViaHeader() != null) {
			trace(Level.INFO, "Received ACK request");
			try {

				Request request = event.getRequest();
				ServerTransaction serverTransaction = event.getServerTransaction();
				processRequest(serverTransaction, request, ac);
				// this.setTimeStarted(System.currentTimeMillis());
			} catch (Exception e) {
				trace(Level.WARNING, "Exception during onAckEvent", e);
			}
		}
		else {
			ac.detach(context.getSbbLocalObject());
		}
	}

	public void onMessageEvent(RequestEvent event, ActivityContextInterface ac) {
		// getDefaultSbbUsageParameterSet().incrementNumberOfMessage(1);
		trace(Level.INFO, "Received MESSAGE request");
		try {
			Request request = event.getRequest();
			ServerTransaction serverTransaction = event.getServerTransaction();
			processRequest(serverTransaction, request, ac);
			// this.setTimeStarted(System.currentTimeMillis());
		} catch (Exception e) {
			trace(Level.WARNING, "Exception during onMessageEvent", e);
		}
	}

	public void onOptionsEvent(RequestEvent event, ActivityContextInterface ac) {
		trace(Level.INFO, "Received OPTIONS request");
		try {

			Request request = event.getRequest();
			ServerTransaction serverTransaction = event.getServerTransaction();
			ProxyConfiguration proxyConfiguration = (ProxyConfiguration) getConfiguration();
			// check if it's for me, in that case reply 501
			SipUri localNodeURI=new SipUri();
			localNodeURI.setHost(proxyConfiguration.getSipHostname());
			localNodeURI.setPort(proxyConfiguration.getSipPort());
			if(request.getRequestURI().equals(localNodeURI))
			{
				if (request.getHeader(MaxForwardsHeader.NAME) == null) {
					request.addHeader(headerFactory.createMaxForwardsHeader(69));
				}
				serverTransaction.sendResponse(messageFactory.createResponse(Response.NOT_IMPLEMENTED, request));
			}
			else {
				processRequest(serverTransaction, request, ac);
			}
		} catch (Exception e) {
			trace(Level.WARNING, "Exception during onOptionsEvent", e);
		}
	}

	public void onInfoRespEvent(ResponseEvent event, ActivityContextInterface ac) {
		trace(Level.INFO, "Received 1xx (INFO) response");
		try {

			Response response = event.getResponse();
			ClientTransaction clientTransaction = event.getClientTransaction();
			processResponse(clientTransaction, response, ac);

		} catch (Exception e) {
			trace(Level.WARNING, "Exception during onInfoRespEvent", e);
		}
	}

	public void onSuccessRespEvent(ResponseEvent event,
			ActivityContextInterface ac) {
		trace(Level.INFO, "Received 2xx (SUCCESS) response");
		try {

			Response response = event.getResponse();
			ClientTransaction clientTransaction = event.getClientTransaction();
			processResponse(clientTransaction, response, ac);

		} catch (Exception e) {
			trace(Level.WARNING, "Exception during onSuccessRespEvent", e);
		}
	}

	public void onRedirRespEvent(ResponseEvent event,
			ActivityContextInterface ac) {
		trace(Level.INFO, "Received 3xx (REDIRECT) response");
		try {

			Response response = event.getResponse();
			ClientTransaction clientTransaction = event.getClientTransaction();
			processResponse(clientTransaction, response, ac);

		} catch (Exception e) {
			trace(Level.WARNING, "Exception during onRedirRespEvent", e);
		}
	}

	public void onClientErrorRespEvent(ResponseEvent event,
			ActivityContextInterface ac) {
		trace(Level.INFO, "Received 4xx (CLIENT ERROR) response");
		try {

			Response response = event.getResponse();
			ClientTransaction clientTransaction = event.getClientTransaction();
			processResponse(clientTransaction, response, ac);

		} catch (Exception e) {
			trace(Level.WARNING, "Exception during onClientErrorRespEvent", e);
		}
	}

	public void onServerErrorRespEvent(ResponseEvent event,
			ActivityContextInterface ac) {
		trace(Level.INFO, "Received 5xx (SERVER ERROR) response");
		try {

			Response response = event.getResponse();
			ClientTransaction clientTransaction = event.getClientTransaction();
			processResponse(clientTransaction, response, ac);

		} catch (Exception e) {
			trace(Level.WARNING, "Exception during onServerErrorRespEvent", e);
		}
	}

	public void onGlobalFailureRespEvent(ResponseEvent event,
			ActivityContextInterface ac) {
		trace(Level.INFO, "Received 6xx (GLOBAL FAILURE) response");
		try {

			Response response = event.getResponse();
			ClientTransaction clientTransaction = event.getClientTransaction();
			processResponse(clientTransaction, response, ac);

		} catch (Exception e) {
			trace(Level.WARNING, "Exception during onGlobalFailureRespEvent", e);
		}
	}

	/*
	 * Timeouts
	 */

	public void onTransactionTimeoutEvent(TimeoutEvent event,
			ActivityContextInterface ac) {
		ClientTransaction clientTransaction = event.getClientTransaction();
		ServerTransaction serverTransaction = event.getServerTransaction();

		trace(Level.INFO, "Received transaction timeout event, tid="
				+ clientTransaction);
		if (serverTransaction != null) {

			try {
				asSbbActivityContextInterface(
						acif.getActivityContextInterface(serverTransaction))
						.setTransactionTerminated(true);
				serverTransaction.sendResponse(messageFactory.createResponse(
						Response.REQUEST_TIMEOUT, serverTransaction
								.getRequest()));
			} catch (FactoryException e) {

				e.printStackTrace();
			} catch (NullPointerException e) {

				e.printStackTrace();
			} catch (UnrecognizedActivityException e) {

				e.printStackTrace();
			} catch (SipException e) {

				e.printStackTrace();
			} catch (InvalidArgumentException e) {

				e.printStackTrace();
			} catch (ParseException e) {

				e.printStackTrace();
			}

		} else {
			if (getForkedTransactionList().contains(clientTransaction)) {
				getForkedTransactionList().remove(clientTransaction);

			}

			getAccumulatedResponses().remove(clientTransaction);

			if (getLocalCancelByeTxMap().containsValue(clientTransaction)) {

				Set s = getLocalCancelByeTxMap().entrySet();
				Iterator it = s.iterator();
				int remove = 0;
				while (it.hasNext()) {

					Map.Entry me = (Entry) it.next();
					if (me.getValue().equals(clientTransaction)) {
						it.remove();
						remove++;
					}

					if (remove == 2)
						break;
				}

			}
		}
	}

	public void onTransactionExpiredEvent(TimeoutEvent event,
			ActivityContextInterface ac) {
		trace(Level.INFO, "Received transaction expired event");
	}

	public ServerTransaction getServerTransaction(
			ClientTransaction clientTransaction) {

		return getServerTX(clientTransaction.getRequest().getMethod());

	}

	// ******************* CALL BACK METHODS
	// We relly on external timers - inside of ProxyMachine
	public void setCTimer(ClientTransaction ctx, double seconds) {
		// Branch should be unique - lets use it to name aci

		cancelCTimer(ctx);

		String branch = ctx.getBranchId();
		ActivityContextInterface aci = null;
		ProxySbbActivityContextInterface localACI = null;

		try {
			aci = nullACIFactory
					.getActivityContextInterface(nullActivityFactory
							.createNullActivity());
			namingFacility.bind(aci, branch);
			aci.attach(getSbbContext().getSbbLocalObject());
			localACI = asSbbActivityContextInterface(aci);
			localACI.setCTimedTransaction(ctx);
			logger.info("Setting CTimer for branch " + branch + " - " + seconds 
					+ (seconds * 1000) + "seconds");
			// TimerID tid=timerFacility.setTimer(aci, null,
			// System.currentTimeMillis()+(long) (seconds*1000),
			// defaultTimerOptions);
			// Damn if timer ac is removed all data within is lost - this way we
			// ensure its not removed....
			TimerID tid = timerFacility.setTimer(aci, null, System
					.currentTimeMillis()
					+ (long) (seconds * 1000), (long) (seconds * 1000), 2,
					defaultTimerOptions);
			localACI.setCTimerTID(tid);
		} catch (TransactionRequiredLocalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FacilityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NameAlreadyBoundException e) {

			e.printStackTrace();
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecognizedActivityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void updateCTimer(ClientTransaction ctx, double seconds) {
		setCTimer(ctx, seconds);

	}

	public void cancelCTimer(ClientTransaction ctx) {

		String branch = ctx.getBranchId();
		ActivityContextInterface aci = null;
		ProxySbbActivityContextInterface localACI = null;
		logger.info("Canceling CTimer for branch " + branch + ", client tx " + ctx);
		if (namingFacility.lookup(branch) != null) {

			// We must unbind and cancel timer if exists?
			// it shouldnt happen but...
			aci = namingFacility.lookup(branch);
			localACI = asSbbActivityContextInterface(aci);
			if (localACI.getCTimerTID() != null) {
				timerFacility.cancelTimer(localACI.getCTimerTID());

				// We must unbind and cancel timer if exists?
			}
			try {
				namingFacility.unbind(branch);
			} catch (TransactionRequiredLocalException e) {

				e.printStackTrace();
			} catch (FacilityException e) {

				e.printStackTrace();
			} catch (NullPointerException e) {

				e.printStackTrace();
			} catch (NameNotBoundException e) {

				e.printStackTrace();
			}

			// Lets kill it
			aci.detach(getSbbContext().getSbbLocalObject());
		}
	}

	public void onTimeEvent(javax.slee.facilities.TimerEvent event,
			ActivityContextInterface aci) {
		logger.info("CTimer expired");

		ProxySbbActivityContextInterface localACI = asSbbActivityContextInterface(aci);
		localACI.setCTimerTID(null);
		cancelCTimer(localACI.getCTimedTransaction());

		SbbLocalObject o = null;
		if (getRegistrarSbbChild().size() == 0)
			try {
				o = getRegistrarSbbChild().create();
			} catch (TransactionRequiredLocalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SLEEException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CreateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else
			o = (SbbLocalObject) getRegistrarSbbChild().iterator().next();

		ProxyMachine proxy;
		try {
			proxy = new ProxyMachine(
					(ProxyConfiguration) getConfiguration(),
					(RegistrationInformationAccess) o, addressFactory,
					headerFactory, messageFactory, provider, this,
					(ProxyConfiguration) getConfiguration());
			proxy.onCTimer(localACI.getCTimedTransaction(),
					getServerTX(Request.INVITE), getForkedTransactionList(),
					getAccumulatedResponses(), getLocalCancelByeTxMap());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	public void endCallProcessing() {
		try {
			
			
			ActivityContextInterface myacis[] = getSbbContext().getActivities();

			for (int i = 0; i < myacis.length; i++) {

				Object activity = myacis[i].getActivity();
				if (activity instanceof ServerTransaction) {
					ServerTransaction stx=(ServerTransaction) activity;
					Request req=stx.getRequest();
					
					
					if(!req.getMethod().equals(Request.CANCEL))
					{
						asSbbActivityContextInterface(
								acif.getActivityContextInterface(stx))
								.setTransactionTerminated(true);
					}
			
				}
			}
	
		} catch (FactoryException e) {

			e.printStackTrace();
		} catch (NullPointerException e) {

			e.printStackTrace();
		} catch (UnrecognizedActivityException e) {

			e.printStackTrace();
		}
	}

	// ***** SENDER METHODS

	public ClientTransaction sendRequest(Request request, boolean attach)
			throws SipException {
		if (request.getHeader(MaxForwardsHeader.NAME) == null) {
			try {
				request.addHeader(headerFactory.createMaxForwardsHeader(69));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		ClientTransaction ct = provider.getNewClientTransaction(request);
		if (attach) {
			try {
				ActivityContextInterface aci = acif
						.getActivityContextInterface(ct);
				aci.attach(getSbbContext().getSbbLocalObject());
			} catch (UnrecognizedActivityException e) {
				trace(Level.WARNING, "unable to attach to client transaction",
						e);
			}
		}

		ct.sendRequest();
		return ct;
	}

	public void sendStatelessRequest(Request request) throws SipException {
		provider.sendRequest(request);
	}

	public void sendStatelessResponse(Response response) throws SipException {
		provider.sendResponse(response);
	}

}
