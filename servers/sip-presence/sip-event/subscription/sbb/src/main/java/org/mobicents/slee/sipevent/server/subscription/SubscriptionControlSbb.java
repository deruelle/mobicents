package org.mobicents.slee.sipevent.server.subscription;

import gov.nist.javax.sip.Utils;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipProvider;
import javax.sip.TransactionDoesNotExistException;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.EventHeader;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.SubscriptionStateHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.UserAgentHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.slee.ActivityContextInterface;
import javax.slee.CreateException;
import javax.slee.InitialEventSelector;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.SbbLocalObject;
import javax.slee.facilities.ActivityContextNamingFacility;
import javax.slee.facilities.TimerEvent;
import javax.slee.facilities.TimerFacility;
import javax.slee.facilities.TimerID;
import javax.slee.facilities.TimerOptions;
import javax.slee.facilities.TimerPreserveMissed;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.mobicents.slee.resource.persistence.ratype.PersistenceResourceAdaptorSbbInterface;
import org.mobicents.slee.resource.sip.SipActivityContextInterfaceFactory;
import org.mobicents.slee.resource.sip.SipResourceAdaptorSbbInterface;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription;
import org.mobicents.slee.sipevent.server.subscription.pojo.SubscriptionKey;

/**
 * Sbb to control subscriptions of sip events in a dialog
 * 
 * @author Eduardo Martins
 * 
 */
public abstract class SubscriptionControlSbb implements Sbb, SubscriptionControlSbbLocalObject {

	private static Logger logger = Logger.getLogger(SubscriptionControlSbb.class);
	
	/**
	 * JAIN-SIP provider & factories
	 * 
	 * @return
	 */
	protected SipActivityContextInterfaceFactory sipActivityContextInterfaceFactory;
	protected SipProvider sipProvider;
	protected AddressFactory addressFactory;
	protected MessageFactory messageFactory;
	protected HeaderFactory headerFactory;
	
	/**
	 * Persistence RA sbb interface
	 */
	protected PersistenceResourceAdaptorSbbInterface persistenceResourceAdaptorSbbInterface;
	
	/**
	 * SLEE Facilities
	 */
	protected TimerFacility timerFacility;
	protected ActivityContextNamingFacility activityContextNamingfacility;
	
	/**
	 * SbbObject's sbb context
	 */
	protected SbbContext sbbContext;
	
	/**
	 * SbbObject's context setting
	 */
	public void setSbbContext(SbbContext sbbContext) {
		this.sbbContext=sbbContext;
		// retrieve factories, facilities & providers
		try {
			Context context = (Context) new InitialContext().lookup("java:comp/env");			
			timerFacility = (TimerFacility) context.lookup("slee/facilities/timer");
			sipActivityContextInterfaceFactory = (SipActivityContextInterfaceFactory)context.lookup("slee/resources/jainsip/1.2/acifactory");
			SipResourceAdaptorSbbInterface sipFactoryProvider = (SipResourceAdaptorSbbInterface)context.lookup("slee/resources/jainsip/1.2/provider");
			sipProvider = sipFactoryProvider.getSipProvider();            
			addressFactory = sipFactoryProvider.getAddressFactory();
			headerFactory = sipFactoryProvider.getHeaderFactory();
			messageFactory = sipFactoryProvider.getMessageFactory();
			persistenceResourceAdaptorSbbInterface = (PersistenceResourceAdaptorSbbInterface) context
			.lookup("slee/resources/pra/0.1/provider");
			activityContextNamingfacility = (ActivityContextNamingFacility) context.lookup("slee/facilities/activitycontextnaming");
		}
		catch (Exception e) {
			getLogger().error("Unable to retrieve factories, facilities & providers",e);			
		}
	}
	
	/**
	 * the impl class logger
	 * 
	 * @return
	 */
	protected Logger getLogger() {
		return logger;
	}

	/**
	 * the event packages supported
	 * 
	 * @return
	 */
	protected abstract String[] getEventPackages();
	

	/**
	 * get default subscription time
	 * 
	 * @return
	 */
	protected int getDefaultExpires() {
		return 3600;
	}

	/**
	 * get max subscription time
	 * 
	 * @return
	 */
	protected int getMaxExpires() {
		return 3600;
	}
	
	/**
	 * get min subscription time
	 * 
	 * @return
	 */
	protected int getMinExpires() {
		return getDefaultExpires();
	}
	
	/**
	 * get default expires value to keep a subscription in waiting value
	 * 
	 * @return
	 */
	protected int getDefaultWaitingExpires() {
		return  86400; //24h
	}
	
	/**
	 * get the string to be used as sip address in contact headers
	 * 
	 * @return
	 */
	protected abstract String getContactAddressString();
	
	/**
	 * the Max-Forwards header value for generated NOTIFY requests
	 * @return
	 */
	protected int getMaxForwards() {
		return 70;
	}
	
	/**
	 * Retrieves SIP response code for a new subscription request SUBSCRIBE. This value defines the authorization. 
	 * @param subscriber
	 * @param notifier
	 * @param eventPackage
	 * @return
	 */
	protected abstract int isSubscriberAuthorized(String subscriber, String notifier, String eventPackage);
	
	/**
	 * Retrieves the content for the NOTIFY request of the specified Subscription
	 * @param subscription
	 * @return
	 */
	protected abstract NotifyContent getNotifyContent(Subscription subscription);
	
	/**
	 * Filters content per subscriber.
	 * @param subscriber
	 * @return
	 */
	protected abstract JAXBElement filterContentPerSubscriber(JAXBElement unmarshalledContent, String subscriber);
	
	/**
	 * Retrieves a JAXB Marshaller to convert a JAXBElement to a String. 
	 * @return
	 */
	protected abstract Marshaller getMarshaller();
	
	// ----------- EVENT HANDLERS
	
	/**
	 * event handler for initial subscribe, which is out of dialog
	 * 
	 * @param event
	 * @param aci
	 */
	public void onSubscribeOutOfDialog(RequestEvent event,
			ActivityContextInterface aci) {
		processSubscribe(event, aci);
	}

	/**
	 * event handler for in dialog subscribe
	 * 
	 * @param event
	 * @param aci
	 */
	public void onSubscribeInDialog(RequestEvent event,
			ActivityContextInterface aci) {
		processSubscribe(event, aci);
	}

	/**
	 * SUBSCRIBE event processing
	 * 
	 * @param event
	 * @param aci
	 */
	private void processSubscribe(RequestEvent event,
			ActivityContextInterface aci) {

		EntityManager entityManager = getEntityManager();
		
		// if exists remove UserAgent header
		if (event.getRequest().getHeader(UserAgentHeader.NAME) != null)
			event.getRequest().removeHeader(UserAgentHeader.NAME);

		getLogger().info("Processing SUBSCRIBE request...");

		// get event header
		EventHeader eventHeader = (EventHeader) event.getRequest().getHeader(
				EventHeader.NAME);
		if (eventHeader != null) {
			// check event package
			String eventPackage = eventHeader.getEventType();
			if (acceptsEventPackage(eventPackage)) {

				// process expires header
				ExpiresHeader expiresHeader = event.getRequest().getExpires();
				int expires;

				// if expires does not exist then set it's value to default
				// value
				if (expiresHeader == null) {
					expires = getDefaultExpires();
				} else {
					expires = expiresHeader.getExpires();
				}

				// check expires value
				if (expires > 0) {
					// check if expires is not less than the allowed min expires
					if (expires >= getMinExpires()) {
						// ensure expires is not bigger than max expires
						if (expires > getMaxExpires()) {
							expires = getMaxExpires();
						}
						// new subscription or subscription refresh ?
						Dialog dialog = event.getDialog();
						if (dialog == null) {
							// no dialog means it's a new subscription for sure
							newSubscription(event, aci, eventPackage,
									eventHeader.getEventId(), expires, entityManager);
						} else {
							String eventId = eventHeader.getEventId();							
							// trying to create or refresh a subscription
							Subscription subscription = getSubscription(entityManager, dialog.getDialogId(), eventPackage, eventId);
							if (subscription != null) {
								// subscription exists
								if (subscription.getStatus().equals(
										Subscription.Status.ACTIVE)
										|| subscription.getStatus().equals(
												Subscription.Status.PENDING)) {
									// subscription status permits refresh
									refreshSubscription(event, aci, eventPackage,
											eventId, expires, subscription, entityManager);
								} else {
									// subscription status does not permits
									// refresh
									sendResponse(
											Response.CONDITIONAL_REQUEST_FAILED,
											event.getRequest(), event
											.getServerTransaction());
								}
							} else {
								// subscription does not exists
								newSubscription(event, aci, eventPackage,
										eventId, expires, entityManager);
							}							
						}
					} else {
						// expires is > 0 but < min expires, respond (Interval
						// Too Brief) with Min-Expires = MINEXPIRES
						sendResponse(Response.INTERVAL_TOO_BRIEF, event
								.getRequest(), event.getServerTransaction());
					}
				}

				else if (expires == 0) {
					Dialog dialog = event.getDialog();
					if (dialog != null) {
						String eventId = eventHeader.getEventId();					
						// trying to remove a subscription
						Subscription subscription = getSubscription(entityManager, dialog.getDialogId(), eventPackage, eventId);
						if (subscription != null) {
							if (subscription.getStatus().equals(
									Subscription.Status.ACTIVE) || subscription
									.getStatus().equals(
											Subscription.Status.PENDING)) {
								// subscription exists and status permits remove
								try {				
									Response response = messageFactory.createResponse(Response.OK, event.getRequest());
									response = addContactHeader(response);
									response.addHeader(headerFactory.createExpiresHeader(expires));
									event.getServerTransaction().sendResponse(response);
									getLogger().info("Response sent:\n"+response.toString());					
								}
								catch (Exception e) {
									getLogger().error("Can't send RESPONSE",e);			
								}							
								// remove subscription
								removeSubscription(aci, eventPackage,
										eventId, subscription,entityManager);
							} else {
								// subscription does exists but status does
								// not permits removal
								sendResponse(
										Response.CONDITIONAL_REQUEST_FAILED,
										event.getRequest(), event
										.getServerTransaction());
							}
						}
						else {
							// subscription does not exists, one shot subscription request, not supported
							sendResponse(
									Response.CONDITIONAL_REQUEST_FAILED,
									event.getRequest(), event
									.getServerTransaction());
						}
					}
					else {
						// dialog does not exists, one shot subscription request, not supported
						sendResponse(
								Response.CONDITIONAL_REQUEST_FAILED,
								event.getRequest(), event
								.getServerTransaction());
					}
				} else {
					// expires can't be negative
					sendResponse(Response.BAD_REQUEST, event.getRequest(),
							event.getServerTransaction());
				}
			} else {
				// wrong event package, send bad event type error
				sendResponse(Response.BAD_EVENT, event.getRequest(), event
						.getServerTransaction());
			}
		} else {
			// subscribe does not have a event header
			sendResponse(Response.BAD_REQUEST, event.getRequest(), event
					.getServerTransaction());
		}
		
		entityManager.flush();
		entityManager.close();
	}

	// ---- SUBSCRIPTION CREATION --------------------------------------------------------------	

	private void newSubscription(RequestEvent event,
			ActivityContextInterface aci, String eventPackage, String eventId,
			int expires, EntityManager entityManager) {

		getLogger().info("new subscription request for event package "+eventPackage);
		
		Dialog dialog = event.getDialog();
		ActivityContextInterface dialogAci = null;
		
		// get subscription data from request
		Address fromAddress = ((FromHeader)event.getRequest().getHeader(FromHeader.NAME)).getAddress();
		String subscriber = fromAddress.getURI().toString();
		String subscriberDisplayName = fromAddress.getDisplayName();
		ToHeader toHeader = ((ToHeader)event.getRequest().getHeader(ToHeader.NAME));
		String notifier = toHeader.getAddress().getURI().toString();

		// get authorization
		int responseCode = -1;
		if (eventPackage.endsWith(".winfo")) {
			// winfo package, only accept subscriptions when subscriber and notifier are the same
			responseCode = subscriber.equals(notifier) ? Response.OK : Response.FORBIDDEN;
		}
		else {
			responseCode = isSubscriberAuthorized(subscriber,notifier,eventPackage);
		}
		
		// send response
		try {				
			Response response = messageFactory.createResponse(responseCode, event.getRequest());				
			if(responseCode == Response.ACCEPTED || responseCode == Response.OK) {
				if (dialog == null) {
					dialog = sipProvider.getNewDialog(event.getServerTransaction());
					ToHeader responseToHeader = (ToHeader) response.getHeader(ToHeader.NAME);
					responseToHeader.setTag(Utils.generateTag());
				}
				// attach to dialog
				SbbLocalObject sbbLocalObject = sbbContext.getSbbLocalObject();
				dialogAci = sipActivityContextInterfaceFactory.getActivityContextInterface(dialog);
				dialogAci.attach(sbbLocalObject);
				// detach from server tx
				aci.detach(sbbLocalObject);
				// finish and send response
				response = addContactHeader(response);
				response.addHeader(headerFactory.createExpiresHeader(expires));
				event.getServerTransaction().sendResponse(response);
				getLogger().info("Response sent:\n"+response.toString());
			}
			else {
				response = addContactHeader(response);
				event.getServerTransaction().sendResponse(response);
				getLogger().info("Response sent:\n"+response.toString());
				return;
			}
		}
		catch (Exception e) {
			getLogger().error("Can't send RESPONSE",e);
			// cleanup
			try {				
				Response response = messageFactory.createResponse(Response.SERVER_INTERNAL_ERROR, event.getRequest());				
				response = addContactHeader(response);
				event.getServerTransaction().sendResponse(response);
				getLogger().info("Response sent:\n"+response.toString());					
			}
			catch (Exception f) {
				getLogger().error("Can't send RESPONSE",f);				
			}		
			return;		
		}						

		// create subscription, initial status depends on authorization
		Subscription.Status initialStatus = 
			responseCode == Response.ACCEPTED ? Subscription.Status.PENDING : Subscription.Status.ACTIVE;
		SubscriptionKey subscriptionKey = new SubscriptionKey(dialog.getDialogId(),eventPackage,eventId);
		// FIXME remove
		getLogger().info("Creating "+subscriptionKey);
		Subscription subscription = 
			new Subscription(subscriptionKey,subscriber,notifier,initialStatus,subscriberDisplayName,expires);

		// notify subscriber
		try {
			notifySubscriber(entityManager,subscription,dialog);
		} catch (Exception e) {
			getLogger().error("failed to notify subscriber",e);
		}

		// notify winfo subscribers
		notifyWinfoSubscriptions(entityManager, subscription);
		
		// bind name for dialog aci
		try {
			activityContextNamingfacility.bind(dialogAci, subscriptionKey.toString());
		} catch (Exception e) {
			getLogger().error("failed to bind a name to dialog's aci",e);
		}
		
		// set new timer
		setSubscriptionTimer(entityManager,subscription,expires+5,aci);
	}

	// ---- SUBSCRIPTION REFRESH --------------------------------------------------------------
	
	private void refreshSubscription(RequestEvent event,
			ActivityContextInterface aci, String eventPackage, String eventId,
			int expires, Subscription subscription, EntityManager entityManager) {
		
		getLogger().info("resfreshing "+subscription.getSubscriptionKey());
		
		// cancel actual timer
		timerFacility.cancelTimer(subscription.getTimerID());

		// refresh subscription
		subscription.refresh(expires);		

		// send OK response
		try {				
			Response response = messageFactory.createResponse(Response.OK, event.getRequest());
			response = addContactHeader(response);
			response.addHeader(headerFactory.createExpiresHeader(expires));
			event.getServerTransaction().sendResponse(response);
			getLogger().info("Response sent:\n"+response.toString());					
		}
		catch (Exception e) {
			getLogger().error("Can't send RESPONSE",e);			
		}

		// notify subscriber
		try {
			notifySubscriber(entityManager,subscription,(Dialog)aci.getActivity());
		} catch (Exception e) {
			getLogger().error("failed to notify subscriber",e);
		}
		
		// set new timer
		setSubscriptionTimer(entityManager,subscription,expires+5,aci);
	}
	
	// ---- SUBSCRIPTION REMOVAL -----------------------------------------------------------------
	
	private void removeSubscription(ActivityContextInterface aci, String eventPackage, String eventId,
			Subscription subscription, EntityManager entityManager) {
		
		getLogger().info("Removing "+subscription.getSubscriptionKey());
		
		// cancel timer
		timerFacility.cancelTimer(subscription.getTimerID());

		// change subscription state, simulate a timeout after a refresh of 0 secs 
		subscription.changeStatus(Subscription.Event.TIMEOUT);

		// get dialog from aci
		Dialog dialog = (Dialog)aci.getActivity();
		
		// notify subscriber
		try {
			notifySubscriber(entityManager,subscription,dialog);
		} catch (Exception e) {
			getLogger().error("failed to notify subscriber",e);
		}

		// notify winfo subscription(s)				
		notifyWinfoSubscriptions(entityManager,subscription);

		// check resulting subscription state
		if (subscription.getStatus().equals(Subscription.Status.TERMINATED)) {
			// remove subscription data
			removeSubscriptionData(entityManager,subscription,dialog,aci);
		}
		else if (subscription.getStatus().equals(Subscription.Status.WAITING)) {
			getLogger().info("Subscription status is now waiting.");
			// keep the subscription for default waiting time so notifier may know about this attemp to subscribe him
			// refresh subscription
			subscription.refresh(getDefaultWaitingExpires());
			// set waiting timer				
			setSubscriptionTimer(entityManager,subscription,getDefaultWaitingExpires()+5,aci);				
		}							
		
	}
	
	/**
	 * a timer has ocurred in a dialog regarding a subscription
	 * @param event
	 * @param aci
	 */
	public void onTimerEvent(TimerEvent event, ActivityContextInterface aci) {
		
		// get subscription dialog
		Dialog dialog = (Dialog) aci.getActivity();
		
		// create jpa entity manager
		EntityManager entityManager = getEntityManager();
				
		// get subscription
		Subscription subscription = (Subscription) entityManager.createQuery(
		"SELECT s FROM Subscription s WHERE s.timerID = :timerID")
		.setParameter("timerID",event.getTimerID())
		.getSingleResult();
		
		if (subscription != null) {
			
			getLogger().info("Timer expired for "+subscription.getSubscriptionKey());
			
			// check subscription status
			if (subscription.getStatus().equals(Subscription.Status.WAITING)) {
				// change subscription status
				subscription.changeStatus(Subscription.Event.GIVEUP);
				// notify winfo subscription(s)				
				notifyWinfoSubscriptions(entityManager,subscription);
				// remove subscription data
				removeSubscriptionData(entityManager,subscription,dialog,aci);
				// close entity manager
				entityManager.close();
			}
			else {
				// remove subscription
				removeSubscription(aci, subscription.getSubscriptionKey().getEventPackage(),
						subscription.getSubscriptionKey().getRealEventId(),subscription,entityManager);
				// flush and close entity manager
				entityManager.flush();
				entityManager.close();
			}
		}
		
		
	}
	
	// ----------- SBB LOCAL OBJECT
	
	public void notifySubscribers(String notifier, String eventPackage,
			JAXBElement content, ContentTypeHeader contentTypeHeader) {
		
		// create jpa entity manager
		EntityManager entityManager = getEntityManager();
		
		// get subscriptions
		List resultList = entityManager.createQuery(
		"SELECT s FROM Subscription s WHERE s.notifier = :notifier AND s.subscriptionKey.eventPackage = :eventPackage")
		.setParameter("notifier",notifier)
		.setParameter("eventPackage",eventPackage)
		.getResultList();
		
		for(Iterator it=resultList.iterator();it.hasNext();) {
			Subscription subscription = (Subscription) it.next();
			if (subscription.getStatus().equals(Subscription.Status.ACTIVE)) {
				try {
					// get subscription dialog
					ActivityContextInterface dialogACI = activityContextNamingfacility.lookup(subscription.getSubscriptionKey().toString());
					Dialog dialog = (Dialog) dialogACI.getActivity();						
					// create notify
					Request notify = createNotify(dialog,subscription);						
					// add content
					if (content != null) {
						notify = setNotifyContent(subscription,notify,content,contentTypeHeader);
					}
					// send notify in dialog related with subscription
					ClientTransaction clientTransaction = sipProvider.getNewClientTransaction(notify);
					dialog.sendRequest(clientTransaction);
					getLogger().info("Request sent:\n"+notify.toString());

				} catch (Exception e) {
					getLogger().error("failed to notify subscriber",e);
				}
			}
		}
				
		// close entity manager
		entityManager.close();
	}
	
	// ----------- AUX METHODS

	private Request setNotifyContent(Subscription subscription, Request notify, JAXBElement content,
			ContentTypeHeader contentTypeHeader) throws JAXBException, ParseException, IOException {

		// filter content per subscriber (notifier rules)
		JAXBElement filteredContent = filterContentPerSubscriber(content, subscription.getSubscriber());
		// filter content per notifier (subscriber rules)
		// TODO
		// marshall content to string
		StringWriter stringWriter = new StringWriter();
		getMarshaller().marshal(filteredContent, stringWriter);
		notify.setContent(stringWriter.toString(),contentTypeHeader);
		stringWriter.close();

		return notify;
	}

	private String getPartialWatcherInfoContent(Subscription winfoSubscription,
			Subscription subscription) {
		// FIXME use dom or jaxb
		// create xml element
		String xml =	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
						"<watcherinfo xmlns=\"urn:ietf:params:xml:ns:watcherinfo\" version=\""+winfoSubscription.getVersion()+"\" state=\"partial\">\n" +
						"\t<watcher-list resource=\""+winfoSubscription.getNotifier()+"\" package=\""+subscription.getSubscriptionKey().getEventPackage()+"\">";		
		// add watcher 
		xml +=			"\n\t\t<watcher status=\""+subscription.getStatus().toString()+"\"\n"
				+		"\t\tid=\""+subscription.hashCode()+"\"\n"
				+		((subscription.getLastEvent() != null) ?
						"\t\tevent=\""+subscription.getLastEvent().toString()+"\"\n" : "")
				+		((subscription.getSubscriberDisplayName() != null) ?
						"\t\tdisplay-name=\""+subscription.getSubscriberDisplayName()+"\"\n" : "")
				+		(!subscription.getStatus().equals(Subscription.Status.TERMINATED) ?
						"\t\texpiration=\""+subscription.getRemainingExpires()+"\"\n" :"")
				+		"\t\tduration-subscribed=\""+subscription.getSubscriptionDuration()+"\">"+subscription.getSubscriber()+"</watcher>";	    				
		// finalize xml element
		xml += 	"\n\t</watcher-list>" +
				"\n</watcherinfo>";
		return xml;
	}

	private String getFullWatcherInfoContent(EntityManager entityManager,Subscription winfoSubscription) {
		
		String winfoEventPackage = winfoSubscription.getSubscriptionKey().getEventPackage();
		String eventPackage = winfoEventPackage.substring(0,winfoEventPackage.indexOf(".winfo"));
		// FIXME use dom or jaxb
		// create xml element
		String xml =	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		"<watcherinfo xmlns=\"urn:ietf:params:xml:ns:watcherinfo\" version=\""+winfoSubscription.getVersion()+"\" state=\"full\">\n" +
		" <watcher-list resource=\""+winfoSubscription.getNotifier()+"\" package=\""+eventPackage+"\">";
		
		// get all subscriptions to specific event package
		List resultList = entityManager
		.createQuery("SELECT s FROM Subscription s WHERE s.subscriptionKey.eventPackage=:eventPackage AND s.notifier=:notifier")
		.setParameter("eventPackage", eventPackage)
		.setParameter("notifier", winfoSubscription.getNotifier()).getResultList();
		// add a watcher element for each
		for(Iterator i=resultList.iterator();i.hasNext();) {
			Subscription subscription = (Subscription) i.next();
			xml +=		"\n" 
				+		"  <watcher status=\""+subscription.getStatus().toString()+"\"\n"
				+		"   id=\""+subscription.hashCode()+"\"\n"				
				+		((subscription.getSubscriberDisplayName() != null) ?
						"   display-name=\""+subscription.getSubscriberDisplayName()+"\"\n" : "")
				+		(!subscription.getStatus().equals(Subscription.Status.TERMINATED) ?
						"   expiration=\""+subscription.getRemainingExpires()+"\"\n" :"")
				+		"   duration-subscribed=\""+subscription.getSubscriptionDuration()+"\">"+subscription.getSubscriber()+"</watcher>";
		}
	
		// finalize xml element
		xml += 	"\n\t</watcher-list>" +
				"\n</watcherinfo>";
		return xml;
	}
	
	
	private ContentTypeHeader getWatcherInfoContentHeader() throws ParseException {		
		return headerFactory.createContentTypeHeader("application","watcherinfo+xml");
	}	
	
	private void setSubscriptionTimer(EntityManager entityManager, Subscription subscription, long delay, ActivityContextInterface aci) {
		TimerOptions options = new TimerOptions();			
		options.setPersistent(true);
		options.setPreserveMissed(TimerPreserveMissed.ALL);		
		// 	set timer
		TimerID timerId = timerFacility.setTimer(aci, null, System.currentTimeMillis() + (delay * 1000), 1, 1, options);
		subscription.setTimerID(timerId);
		// update subscription
		entityManager.persist(subscription);
	}

	private void notifySubscriber(EntityManager entityManager,
			Subscription subscription, Dialog dialog) throws TransactionDoesNotExistException, SipException, ParseException {
			
		// create notify
		Request notify = createNotify(dialog,subscription);			
		// add content if subscription is active
		if (subscription.getStatus().equals(Subscription.Status.ACTIVE)) {
			if (subscription.getSubscriptionKey().getEventPackage().endsWith(".winfo")) {
				// winfo content, increment version before adding the content
				subscription.incrementVersion();
				entityManager.persist(subscription);
				entityManager.flush();
				notify.setContent(getFullWatcherInfoContent(entityManager,subscription),getWatcherInfoContentHeader());
			}
			else {
				// specific event package content
				NotifyContent notifyContent = getNotifyContent(subscription);
				// add content
				if (notifyContent != null) {
					try {
						notify = setNotifyContent(subscription,notify,notifyContent.getContent(),notifyContent.getContentTypeHeader());
					}
					catch (Exception e) {
						getLogger().error("failed to set notify content",e);
					}
				}
			}
		}
		// send notify
		ClientTransaction clientTransaction = sipProvider.getNewClientTransaction(notify);
		dialog.sendRequest(clientTransaction);
		getLogger().info("Request sent:\n"+notify.toString());	
		
	}

	private void removeSubscriptionData(EntityManager entityManager, Subscription subscription, Dialog dialog, ActivityContextInterface aci) {
		// remove subscription
		entityManager.remove(subscription);
		// remove aci name binding
		try {
			activityContextNamingfacility.unbind(subscription.getSubscriptionKey().toString());
		} catch (Exception e) {
			getLogger().error("failed to unbind subscription dialog aci name");
		}
		// verify if dialog is not needed anymore (and remove if that's the case)
		verifyDialogSubscriptions(entityManager,subscription,dialog,aci);		
		// FIXME check one flush is enough 
		entityManager.flush();
	}

	/**
	 * Removes the specified dialog if no more subscriptions exists 
	 * @param entityManager
	 * @param removedSubscription
	 * @param dialog
	 * @param dialogAci
	 */
	private void verifyDialogSubscriptions(EntityManager entityManager, Subscription removedSubscription, Dialog dialog, ActivityContextInterface dialogAci) {
		// get subscriptions of dialog from persistence 
		List subscriptionsInDialog = entityManager.createQuery("SELECT s FROM Subscription s WHERE s.subscriptionKey.dialogId = :dialogId")
		.setParameter("dialogId",dialog.getDialogId()).getResultList();
		if (subscriptionsInDialog.size() == 1 && ((Subscription)subscriptionsInDialog.get(0)).equals(removedSubscription)) {
			getLogger().info("Deleting dialog, no more subscriptions.");
			// no more subscriptions in dialog, detach and delete the dialog
			dialogAci.detach(sbbContext.getSbbLocalObject());
			dialog.delete();
		}
	}

	private void notifyWinfoSubscriptions(EntityManager entityManager,Subscription subscription) {
		if (!subscription.getSubscriptionKey().getEventPackage().endsWith(".winfo")) {
			// lookup persistent data fr subscriptions
			List winfoSubscriptions = entityManager.createQuery(
			"SELECT s FROM Subscription s WHERE s.notifier = :notifier AND s.subscriptionKey.eventPackage = :eventPackage")
			.setParameter("notifier",subscription.getNotifier())
			.setParameter("eventPackage",subscription.getSubscriptionKey().getEventPackage()+".winfo").getResultList();
			// process result
			if (!winfoSubscriptions.isEmpty()) {
				for(Iterator it=winfoSubscriptions.iterator();it.hasNext();) {
					Subscription winfoSubscription = (Subscription) it.next();
					if (winfoSubscription.getStatus().equals(Subscription.Status.ACTIVE)) {
						try {
							// get subscription dialog
							ActivityContextInterface winfoDialogAci = activityContextNamingfacility.lookup(winfoSubscription.getSubscriptionKey().toString());
							Dialog winfoDialog = (Dialog) winfoDialogAci.getActivity();
							// increment subscription version
							winfoSubscription.incrementVersion();
							// create notify
							Request notify = createNotify(winfoDialog,winfoSubscription);			
							// add content
							notify.setContent(getPartialWatcherInfoContent(winfoSubscription,subscription),getWatcherInfoContentHeader());																		
							// send notify in dialog related with subscription
							ClientTransaction clientTransaction = sipProvider.getNewClientTransaction(notify);
							winfoDialog.sendRequest(clientTransaction);
							getLogger().info("Request sent:\n"+notify.toString());
							// persist subscription
							entityManager.persist(winfoSubscription);
						} catch (Exception e) {
							getLogger().error("failed to notify winfo subscriber",e);
						}
					}					
				}
				entityManager.flush();
			}
		}
	}
	
	
	/*
	 * loads subscription pojo from persistence
	 */ 
	private Subscription getSubscription(EntityManager entityManager, String dialogId, String eventPackage, String eventId) {
		// FIXME remove
		getLogger().info("Searching for "+new SubscriptionKey(dialogId,eventPackage,eventId));
		try {
			return (Subscription) entityManager.createQuery(
			"SELECT s FROM Subscription s WHERE s.subscriptionKey.dialogId = :dialogId AND s.subscriptionKey.eventPackage = :eventPackage AND s.subscriptionKey.eventId = :eventId")
			.setParameter("dialogId",dialogId)
			.setParameter("eventPackage",eventPackage)
			.setParameter("eventId",SubscriptionKey.getEventIdPersisted(eventId))
			.getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}

	/*
	 * Sends a response with the specified status code, adding additional
	 * headers if needed
	 */
	private void sendResponse(int responseCode, Request request,
			ServerTransaction serverTransaction) {
		try {
			// create response
			Response response = messageFactory.createResponse(
					responseCode, request);
			// add headers if needed
			if (responseCode == Response.BAD_EVENT) {
				String allowEventsHeader = "";
				boolean first = true;
				for (String acceptedEventPackage : getEventPackages()) {
					if (first) {
						allowEventsHeader += acceptedEventPackage + "," + acceptedEventPackage + ".winfo";
					}
					else {
						allowEventsHeader += ","+acceptedEventPackage + "," + acceptedEventPackage + ".winfo";
					}					
				}
				response
						.addHeader(headerFactory.createAllowEventsHeader(allowEventsHeader));
			} else if (responseCode == Response.INTERVAL_TOO_BRIEF) {
				response.addHeader(headerFactory.createMinExpiresHeader(
						getMinExpires()));
			}
			// 2xx response to SUBSCRIBE need a Contact
			response = addContactHeader(response);
			serverTransaction.sendResponse(response);
			getLogger().info("Response sent:\n" + response.toString());
		} catch (Exception e) {
			getLogger().error("Can't send response!", e);
		}
	}

	/*
	 * Adds subscription agent contact header to SIP response
	 */
	private Response addContactHeader(Response response) {
		if (response.getHeader(ContactHeader.NAME) != null) {
			response.removeHeader(ContactHeader.NAME);
		}
		try {
			Address address = addressFactory.createAddress(
					getContactAddressString());
			((SipURI) address.getURI()).setPort(sipProvider
					.getListeningPoint("udp").getPort());
			response.addHeader(headerFactory.createContactHeader(address));
		} catch (Exception e) {
			getLogger().error("Can't add contact header", e);
		}
		return response;
	}
	

	// creates a notify request and fills headers
	private Request createNotify(Dialog dialog, Subscription subscription) {

		Request notify = null;
		try {	
			notify = dialog.createRequest(Request.NOTIFY);
			// add event header
			EventHeader eventHeader = headerFactory.createEventHeader(subscription.getSubscriptionKey().getEventPackage());
			if (subscription.getSubscriptionKey().getRealEventId() != null) eventHeader.setEventId(subscription.getSubscriptionKey().getRealEventId());
			notify.setHeader(eventHeader);
			// add max forwards header
			notify.setHeader(headerFactory.createMaxForwardsHeader(getMaxForwards()));		
			/*
			 NOTIFY requests MUST contain a "Subscription-State" header with a
			 value of "active", "pending", or "terminated".  The "active" value
			 indicates that the subscription has been accepted and has been
			 authorized (in most cases; see section 5.2.).  The "pending" value
			 indicates that the subscription has been received, but that policy
			 information is insufficient to accept or deny the subscription at
			 this time.  The "terminated" value indicates that the subscription is
			 not active.
			 */
			SubscriptionStateHeader ssh = null;
			if (subscription.getStatus().equals(Subscription.Status.ACTIVE) || subscription.getStatus().equals(Subscription.Status.PENDING)) {							
				ssh = headerFactory.createSubscriptionStateHeader(subscription.getStatus().toString());
				/*
				 If the value of the "Subscription-State" header is "active" or
				 "pending", the notifier SHOULD also include in the "Subscription-
				 State" header an "expires" parameter which indicates the time
				 remaining on the subscription.
				 */
				ssh.setExpires(subscription.getRemainingExpires());
			}
			else if (subscription.getStatus().equals(Subscription.Status.WAITING) || subscription.getStatus().equals(Subscription.Status.TERMINATED)) {
				ssh = headerFactory.createSubscriptionStateHeader("terminated");
				/*
				 If the value of the "Subscription-State" header is "terminated", the
				 notifier SHOULD also include a "reason" parameter.
				 */
				ssh.setReasonCode(subscription.getLastEvent().toString());
			}
			notify.addHeader(ssh);					
		}
		catch (Exception e) {
			getLogger().error("unable to fill notify headers",e);
		}
		return notify;		
	}

	
	/**
	 * verifies if the specified event packaged is accepted
	 */
	private boolean acceptsEventPackage(String eventPackage) {
		if (eventPackage != null) {			
			for(String acceptedEventPackage : getEventPackages()) {
				if (eventPackage.equals(acceptedEventPackage)
					|| eventPackage.equals(acceptedEventPackage + ".winfo")) {
					return true;
				}
			}
		}
		return false;
	}
	
	private static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("sipevent-subscription-pu");
	private EntityManager getEntityManager() {
		//return this.persistenceResourceAdaptorSbbInterface.createEntityManager(new HashMap(), "sipevent-subscription-pu");
		return entityManagerFactory.createEntityManager();
	}
	
	/**
	 * Workaround to the prob of having 2 subscribes fired in different activities.
	 * @param ies
	 * @return
	 */
	public InitialEventSelector ies(InitialEventSelector ies) {
		if (ies.getActivity() instanceof ServerTransaction && ((RequestEvent) ies.getEvent()).getDialog() != null) {
			getLogger().warn("Filtering request event on server tx activity with dialog not null"); 
			ies.setInitialEvent(false);
		}
		else {
			ies.setActivityContextSelected(true);
		}
		return ies;
	}
	
	// ----------- SBB OBJECT's LIFE CYCLE
	
	public void sbbActivate() {}
	
	public void sbbCreate() throws CreateException {}
	
	public void sbbExceptionThrown(Exception arg0, Object arg1,
			ActivityContextInterface arg2) {}
	
	public void sbbLoad() {}
	
	public void sbbPassivate() {}
	
	public void sbbPostCreate() throws CreateException {}
	
	public void sbbRemove() {}
	
	public void sbbRolledBack(RolledBackContext arg0) {}
	
	public void sbbStore() {}
	
	public void unsetSbbContext() {}
	
}