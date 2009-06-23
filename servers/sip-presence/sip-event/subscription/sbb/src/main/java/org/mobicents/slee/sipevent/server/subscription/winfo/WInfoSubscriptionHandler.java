package org.mobicents.slee.sipevent.server.subscription.winfo;

import java.io.StringWriter;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.sip.Dialog;
import javax.sip.header.ContentTypeHeader;
import javax.sip.message.Request;
import javax.slee.ActivityContextInterface;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.mobicents.slee.sipevent.server.subscription.ImplementedSubscriptionControlSbbLocalObject;
import org.mobicents.slee.sipevent.server.subscription.SubscriptionControlSbb;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription;
import org.mobicents.slee.sipevent.server.subscription.winfo.pojo.Watcher;
import org.mobicents.slee.sipevent.server.subscription.winfo.pojo.WatcherList;
import org.mobicents.slee.sipevent.server.subscription.winfo.pojo.Watcherinfo;

/**
 * Service logic regarding winfo subscriptions
 * 
 * @author martins
 * 
 */
public class WInfoSubscriptionHandler {

	private static Logger logger = Logger
			.getLogger(SubscriptionControlSbb.class);

	private SubscriptionControlSbb sbb;

	public WInfoSubscriptionHandler(SubscriptionControlSbb sbb) {
		this.sbb = sbb;
	}

	public void notifyWinfoSubscriptions(EntityManager entityManager,
			Subscription subscription,
			ImplementedSubscriptionControlSbbLocalObject childSbb) {

		if (!subscription.getKey().getEventPackage().endsWith(".winfo")) {
			// lookup persistent data fr subscriptions
			List winfoSubscriptions = entityManager.createNamedQuery(Subscription.JPA_NAMED_QUERY_SELECT_SUBSCRIPTIONS_FROM_NOTIFIER_AND_EVENTPACKAGE)
					.setParameter("notifier", subscription.getNotifier())
					.setParameter("eventPackage",
							subscription.getKey().getEventPackage() + ".winfo")
					.getResultList();
			// process result
			if (!winfoSubscriptions.isEmpty()) {
				for (Iterator it = winfoSubscriptions.iterator(); it.hasNext();) {
					Subscription winfoSubscription = (Subscription) it.next();
					if (winfoSubscription.getStatus().equals(
							Subscription.Status.active)) {

						try {
							// get subscription aci
							ActivityContextInterface winfoAci = sbb
									.getActivityContextNamingfacility().lookup(
											winfoSubscription.getKey()
													.toString());
							if (winfoAci != null) {
								// increment subscription version
								winfoSubscription.incrementVersion();
								// get winfo notify content and content type
								// header
								String partialWInfoContent = getPartialWatcherInfoContent(
										winfoSubscription, subscription);
								ContentTypeHeader winfoContentHeader = getWatcherInfoContentHeader();
								if (winfoSubscription.getKey()
										.isInternalSubscription()) {
									// internal subscription
									sbb
											.getInternalSubscriptionHandler()
											.getInternalSubscriberNotificationHandler()
											.notifyInternalSubscriber(entityManager,
													subscription,
													partialWInfoContent,
													winfoContentHeader,
													winfoAci);
								} else {
									// sip subscription
									Dialog winfoDialog = (Dialog) winfoAci
											.getActivity();
									// create notify
									Request notify = sbb
											.getSipSubscribeHandler()
											.getSipSubscriberNotificationHandler()
											.createNotify(winfoDialog,
													winfoSubscription);
									// add content
									notify.setContent(partialWInfoContent,
											winfoContentHeader);
									// send notify in dialog related with
									// subscription
									winfoDialog.sendRequest(sbb
											.getSipProvider()
											.getNewClientTransaction(notify));
									if (logger.isDebugEnabled()) {
										logger.debug("Request sent:\n"
												+ notify.toString());
									}
								}
								// persist subscription
								entityManager.persist(winfoSubscription);
							} else {
								// aci is gone, cleanup subscription
								logger
										.warn("Unable to find subscription aci to notify subscription "
												+ winfoSubscription.getKey()
												+ ". Removing subscription data");
								sbb
										.removeSubscriptionData(
												entityManager,
												winfoSubscription, null, null,
												childSbb);
							}
						} catch (Exception e) {
							logger
									.error("failed to notify winfo subscriber",
											e);
						}
					}

				}
			}
			entityManager.flush();
		}
	}

	private static final JAXBContext winfoJAXBContext = initWInfoJAXBContext();

	private static JAXBContext initWInfoJAXBContext() {
		try {
			return JAXBContext
					.newInstance("org.mobicents.slee.sipevent.server.subscription.winfo.pojo");
		} catch (JAXBException e) {
			logger.error("failed to create winfo jaxb context");
			return null;
		}
	}

	private Marshaller getWInfoMarshaller() {
		try {
			return winfoJAXBContext.createMarshaller();
		} catch (JAXBException e) {
			logger.error("failed to create winfo unmarshaller", e);
			return null;
		}
	}

	/*
	 * creates watcher jaxb object for a subscription
	 */
	private Watcher createWInfoWatcher(Subscription subscription) {
		// create watcher
		Watcher watcher = new Watcher();
		watcher.setId(String.valueOf(subscription.hashCode()));
		watcher.setStatus(subscription.getStatus().toString());
		watcher.setDurationSubscribed(BigInteger.valueOf(subscription
				.getSubscriptionDuration()));
		if (subscription.getLastEvent() != null) {
			watcher.setEvent(subscription.getLastEvent().toString());
		}
		if (subscription.getSubscriberDisplayName() != null) {
			watcher.setDisplayName(subscription.getSubscriberDisplayName());
		}
		if (!subscription.getStatus().equals(Subscription.Status.terminated)) {
			watcher.setExpiration(BigInteger.valueOf(subscription
					.getRemainingExpires()));
		}
		watcher.setValue(subscription.getSubscriber());
		return watcher;
	}

	/*
	 * marshals a jaxb watcherinfo object to string
	 */
	private String marshallWInfo(Watcherinfo watcherinfo) {
		// marshall to string
		String result = null;
		StringWriter stringWriter = new StringWriter();
		try {
			Marshaller marshaller = getWInfoMarshaller();
			marshaller.marshal(watcherinfo, stringWriter);
			result = stringWriter.toString();
			stringWriter.close();
		} catch (Exception e) {
			logger.error("failed to marshall winfo", e);
			try {
				stringWriter.close();
			} catch (Exception f) {
				logger.error("failed to close winfo string writer", f);
			}
		}

		return result;
	}

	/*
	 * creates partial watcher info doc
	 */
	private String getPartialWatcherInfoContent(Subscription winfoSubscription,
			Subscription subscription) {
		// create watcher info
		Watcherinfo watcherinfo = new Watcherinfo();
		watcherinfo.setVersion(BigInteger.valueOf(winfoSubscription
				.getVersion()));
		watcherinfo.setState("partial");
		// create watcher list
		WatcherList watcherList = new WatcherList();
		watcherList.setResource(winfoSubscription.getNotifier());
		watcherList.setPackage(subscription.getKey().getEventPackage());
		// create and add watcher to watcher info list
		watcherList.getWatcher().add(createWInfoWatcher(subscription));
		// add watcher list to watcher info
		watcherinfo.getWatcherList().add(watcherList);
		// marshall and return
		return marshallWInfo(watcherinfo);
	}

	/*
	 * generates full watcher info doc
	 */
	public String getFullWatcherInfoContent(EntityManager entityManager,
			Subscription winfoSubscription) {

		// create watcher info
		Watcherinfo watcherinfo = new Watcherinfo();
		watcherinfo.setVersion(BigInteger.valueOf(winfoSubscription
				.getVersion()));
		watcherinfo.setState("full");
		// create watcher list
		WatcherList watcherList = new WatcherList();
		watcherList.setResource(winfoSubscription.getNotifier());
		String winfoEventPackage = winfoSubscription.getKey().getEventPackage();
		String eventPackage = winfoEventPackage.substring(0, winfoEventPackage
				.indexOf(".winfo"));
		watcherList.setPackage(eventPackage);
		// get watcher subscriptions
		List resultList = entityManager.createNamedQuery(Subscription.JPA_NAMED_QUERY_SELECT_SUBSCRIPTIONS_FROM_NOTIFIER_AND_EVENTPACKAGE).setParameter(
				"eventPackage", eventPackage).setParameter("notifier",
				winfoSubscription.getNotifier()).getResultList();
		// add a watcher element for each
		List<Watcher> watchers = watcherList.getWatcher();
		for (Iterator i = resultList.iterator(); i.hasNext();) {
			Subscription subscription = (Subscription) i.next();
			// create and add watcher to watcher info list
			watchers.add(createWInfoWatcher(subscription));
		}
		// add watcher list to watcher info
		watcherinfo.getWatcherList().add(watcherList);
		// marshall and return
		return marshallWInfo(watcherinfo);
	}

	public ContentTypeHeader getWatcherInfoContentHeader()
			throws ParseException {
		return sbb.getHeaderFactory().createContentTypeHeader("application",
				"watcherinfo+xml");
	}
}
