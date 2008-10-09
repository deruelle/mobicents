package org.mobicents.slee.xdm.server.subscription;

import java.io.StringReader;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javax.sip.Dialog;
import javax.sip.RequestEvent;
import javax.sip.header.ContentTypeHeader;
import javax.sip.message.Response;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.mobicents.slee.sipevent.server.subscription.NotifyContent;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription;
import org.mobicents.slee.sipevent.server.subscription.pojo.SubscriptionKey;
import org.mobicents.slee.xdm.server.ServerConfiguration;
import org.mobicents.slee.xdm.server.XDMClientControlSbbLocalObject;
import org.openxdm.xcap.client.appusage.resourcelists.jaxb.EntryType;
import org.openxdm.xcap.client.appusage.resourcelists.jaxb.ListType;
import org.openxdm.xcap.client.appusage.resourcelists.jaxb.ResourceLists;
import org.openxdm.xcap.common.datasource.Document;
import org.openxdm.xcap.common.error.InternalServerErrorException;
import org.openxdm.xcap.common.uri.DocumentSelector;
import org.openxdm.xcap.common.uri.NodeSelector;
import org.openxdm.xcap.common.uri.Parser;
import org.openxdm.xcap.common.uri.ResourceSelector;
import org.openxdm.xcap.common.uri.TerminalSelector;
import org.openxdm.xcap.common.xcapdiff.DocumentType;
import org.openxdm.xcap.common.xcapdiff.ObjectFactory;
import org.openxdm.xcap.common.xcapdiff.XcapDiff;

/**
 * Logic for {@link XcapDiffSubscriptionControlSbb}
 * @author martins
 *
 */
public class XcapDiffSubscriptionControl {

	private static final String[] xcapDiffEventPackages = {"xcap-diff"};
	
	private XcapDiffSubscriptionControlSbbLocalObject sbb;
	
	public XcapDiffSubscriptionControl(XcapDiffSubscriptionControlSbbLocalObject sbb) {
		this.sbb = sbb;
	}
	
	public static String[] getEventPackages() {
		return xcapDiffEventPackages;
	}
	
	private ContentTypeHeader xcapDiffContentTypeHeader = null;
	
	public ContentTypeHeader getXcapDiffContentTypeHeader() {
		if (xcapDiffContentTypeHeader == null) {
			try {
				xcapDiffContentTypeHeader = sbb.getHeaderFactory().createContentTypeHeader("application", "xcap-diff+xml");
			} catch (ParseException e) {
				// ignore
				e.printStackTrace();
			}
		}
		return xcapDiffContentTypeHeader;
	}
	
	public void isSubscriberAuthorized(RequestEvent event,
			String subscriber, String notifier, String eventPackage,
			String eventId, int expires) {
		
		StringReader stringReader = null;
		
		try {
			stringReader = new StringReader(new String(event.getRequest().getRawContent()));
			ResourceLists object = (ResourceLists) sbb.getUnmarshaller().unmarshal(stringReader);
			stringReader.close();
			stringReader = null;
			// ok, resource-lists parsed, let's process it's lists elements
			HashSet<String> appUsagesToSubscribe = new HashSet<String>();
			HashSet<DocumentSelector> documentsToSubscribe = new HashSet<DocumentSelector>();
			for (ListType listType : object.getList()) {
				for (Object listOrExternalOrEntry : listType.getListOrExternalOrEntry()) {
					if (listOrExternalOrEntry instanceof JAXBElement) {
						JAXBElement jAXBElement = (JAXBElement) listOrExternalOrEntry;
						if (jAXBElement.getValue() instanceof EntryType) {
							EntryType entryType = (EntryType) jAXBElement.getValue();
							// process it
							ResourceSelector resourceSelector = null;
							try {
								int queryComponentSeparator = entryType.getUri().indexOf('?');
								if (queryComponentSeparator > 0) {
									resourceSelector = Parser
									.parseResourceSelector(ServerConfiguration.XCAP_ROOT, entryType.getUri().substring(0,queryComponentSeparator), entryType.getUri().substring(queryComponentSeparator+1));
								}
								else {
									resourceSelector = Parser
									.parseResourceSelector(ServerConfiguration.XCAP_ROOT, entryType.getUri(), null);
								}
								if (resourceSelector.getDocumentSelector().indexOf('/')<0) {
									// trying to subscribe app usage
									String auid = resourceSelector.getDocumentSelector();
									logger.info("subscribing auid "+auid);
									appUsagesToSubscribe.add(auid);
								}
								else {
									// trying to subscribe a document or part of it
									DocumentSelector documentSelector = Parser.parseDocumentSelector(resourceSelector.getDocumentSelector());
									NodeSelector nodeSelector = null;
									TerminalSelector terminalSelector = null;
									if (resourceSelector.getNodeSelector() != null) {
										nodeSelector = Parser.parseNodeSelector(resourceSelector
												.getNodeSelector());
										if (nodeSelector.getTerminalSelector() != null) {
											// parse terminal selector
											terminalSelector = Parser
													.parseTerminalSelector(nodeSelector
															.getTerminalSelector());
										}
									}
									logger.info("subscribing document (or part of it) "+documentSelector);
									documentsToSubscribe.add(documentSelector);
								}								
							}
							catch (Exception e) {
								logger.error("failed to parse entry uri to subscribe",e);
							}
						}							
					}
				}
			}
			
			// create subscriptions object 
			Dialog dialog = event.getServerTransaction().getDialog();
			SubscriptionKey key = new SubscriptionKey(dialog.getCallId().getCallId(),dialog.getRemoteTag(),"xcap-diff",eventId);
			Subscriptions subscriptions = new Subscriptions(key,appUsagesToSubscribe,documentsToSubscribe);
			// get subscriptions map cmp
			Map subscriptionsMap = sbb.getSubscriptionsMap();
			if (subscriptionsMap == null) {
				subscriptionsMap = new HashMap();
			}
			// build set of other documents and app usages already subscribed by this entity
			HashSet<DocumentSelector> documentSelectorsAlreadySubscribed = new HashSet<DocumentSelector>();
			HashSet<String> appUsagesAlreadySubscribed = new HashSet<String>();
			for(Iterator i=subscriptionsMap.values().iterator();i.hasNext();) {
				Subscriptions s = (Subscriptions) i.next();
				for (DocumentSelector ds:s.getDocumentSelectors()) {
					documentSelectorsAlreadySubscribed.add(ds);
				}
				for (String auid:s.getAppUsages()) {
					appUsagesAlreadySubscribed.add(auid);
				}
			}
			// save subscriptions object on cmp
			subscriptionsMap.put(key, subscriptions);
			sbb.setSubscriptionsMap(subscriptionsMap);			
			// let's subscribe all documents and/or app usages
			XDMClientControlSbbLocalObject xdm = null;
			try {
				xdm = sbb.getXDMClientControlSbb();
			} catch (Exception e) {
				logger.error("failed to get xdm client sbb",e);
			}
			for(DocumentSelector documentSelector : documentsToSubscribe) {
				if (!documentSelectorsAlreadySubscribed.contains(documentSelector) && !appUsagesAlreadySubscribed.contains(documentSelector.getAUID())) {
					// app usages already subscribed does not match this document selector's app usage,
					// and this document selector is not subscribed already due to another 
					// subscription in the same entity, so subscribe the doc
					xdm.subscribeDocument(documentSelector);
				}				
			}
			for(String auid:appUsagesToSubscribe) {
				if (!appUsagesAlreadySubscribed.contains(auid)) {
					// app usages already subscribed does not match this app usage,
					// so subscribe it
					xdm.subscribeAppUsage(auid);
				}
			}
			
			// continue new subscription process
			sbb.getParentSbbCMP().newSipSubscriptionAuthorization(event, subscriber, notifier, eventPackage, eventId, expires, Response.OK);
		} catch (JAXBException e) {
			logger.error("failed to parse resource-lists in initial subscribe",e);
			if (stringReader != null) {
				stringReader.close();
			}
			sbb.getParentSbbCMP().newSipSubscriptionAuthorization(event, subscriber, notifier, eventPackage, eventId, expires, Response.FORBIDDEN);
		}
		
	}

	public void removingSubscription(Subscription subscription) {
		
		// get subscriptions map and remove subscription terminating
		Map subscriptionsMap = sbb.getSubscriptionsMap();
		if (subscriptionsMap != null) {
		Subscriptions subscriptions = (Subscriptions)subscriptionsMap.remove(subscription.getKey());
		
		// build set of other documents and app usages already subscribed by this entity
		HashSet<DocumentSelector> documentSelectorsSubscribedByOthers = new HashSet<DocumentSelector>();
		HashSet<String> appUsagesSubscribedByOthers = new HashSet<String>();
		for(Iterator i=subscriptionsMap.values().iterator();i.hasNext();) {
			Subscriptions s = (Subscriptions) i.next();
			for (DocumentSelector ds:s.getDocumentSelectors()) {
				documentSelectorsSubscribedByOthers.add(ds);
			}
			for (String auid:s.getAppUsages()) {
				appUsagesSubscribedByOthers.add(auid);
			}
		}
		
		// now unsubscribe each that was subscribed only by the subscription terminating
		XDMClientControlSbbLocalObject xdm = null;
		try {
			xdm = sbb.getXDMClientControlSbb();
		} catch (Exception e) {
			logger.error("failed to get xdm client sbb",e);
		}
		for(DocumentSelector ds : subscriptions.getDocumentSelectors()) {
			if (!documentSelectorsSubscribedByOthers.contains(ds)) {
				// safe to unsubscribe this document 
				xdm.unsubscribeDocument(ds);
			}				
		}
		for(String auid:subscriptions.getAppUsages()) {
			if (!appUsagesSubscribedByOthers.contains(auid)) {
				// safe to unsubscribe this app usage
				xdm.unsubscribeAppUsage(auid);
			}
		}
		}
		else {
			logger.warn("Removing subscription but map of subscriptions is null");
		}
	}

	public NotifyContent getNotifyContent(Subscription subscription) {
		// let's gather all content this subscription
		Map subscriptionsMap = sbb.getSubscriptionsMap();
		Subscriptions subscriptions = (Subscriptions)subscriptionsMap.get(subscription.getKey());
		if (subscriptions != null) {
			HashMap<DocumentSelector,String> documentEtags = new HashMap<DocumentSelector, String>();
			// let's process first app usages
			for (String auid:subscriptions.getAppUsages()) {				
				// get collections that exist in this app usage
				try {
					String[] appUsageCollections = sbb.getDataSourceSbbInterface().getCollections(auid);
					// for each one gather all documents names
					for (String collection : appUsageCollections) {
						String[] documentNames = sbb.getDataSourceSbbInterface().getDocuments(auid, collection);
						// grab each document
						for(String documentName : documentNames) {
							DocumentSelector documentSelector = new DocumentSelector(auid,collection,documentName);
							Document document = sbb.getDataSourceSbbInterface().getDocument(documentSelector);
							if (document != null) {
								// TODO authorize inclusion of the document
								documentEtags.put(documentSelector, document.getETag());
							}
						}
					}
				}
				catch (Exception e) {
					logger.error(e);
				}
			}
			for (DocumentSelector documentSelector: subscriptions.getDocumentSelectors()) {
				Document document = null;
				try {
					document = sbb.getDataSourceSbbInterface().getDocument(documentSelector);
				} catch (InternalServerErrorException e) {
					logger.error(e);
				}
				if (document != null) {
					// borrow app usage object from cache
					//TODO AppUsage appUsage = appUsageCache.borrow(auid);
					// get auth policy
					//TODO AuthorizationPolicy authorizationPolicy = appUsage.getAuthorizationPolicy();
					// TODO authorize inclusion of the document
					documentEtags.put(documentSelector, document.getETag());
				}
			}
			// build notify content
			XcapDiff xcapDiff = new XcapDiff();
			xcapDiff.setXcapRoot(ServerConfiguration.SCHEME_AND_AUTHORITY_URI+ServerConfiguration.XCAP_ROOT+"/");
			ObjectFactory objectFactory = new ObjectFactory();
			for (DocumentSelector documentSelector : documentEtags.keySet()) {
				DocumentType documentType = objectFactory.createDocumentType();
				documentType.setSel(documentSelector.toString());
				documentType.setNewEtag(documentEtags.get(documentSelector));
				xcapDiff.getDocumentOrElementOrAttribute().add(documentType);
			}
			
			return new NotifyContent(xcapDiff,getXcapDiffContentTypeHeader());
			
		}
		else {
			
			return null;
		}
	}
	
	public Object filterContentPerSubscriber(String subscriber, String notifier, String eventPackage, Object unmarshalledContent) {
		return unmarshalledContent;
	}

	public void documentUpdated(DocumentSelector documentSelector,
			String oldETag, String newETag, String documentAsString) {
		XcapDiff xcapDiff = null;
		// for all subscriptions in this entity that have interest on the document
		Map subscriptionsMap = sbb.getSubscriptionsMap();
		if (subscriptionsMap != null) {
		
		for(Iterator i=subscriptionsMap.values().iterator();i.hasNext();) {
			Subscriptions s = (Subscriptions) i.next();
			boolean doNotify = false;
			// document selector matches?
			for (DocumentSelector ds:s.getDocumentSelectors()) {
				if (ds.equals(documentSelector)) {
					doNotify = true;
					break;
				}
			}
			if (!doNotify) {
				// perhaps document selector has the same auid as one subscribed?
				for (String auid:s.getAppUsages()) {
					if (auid.equals(documentSelector.getAUID())) {
						doNotify = true;
						break;
					}
				}
			}
			if (doNotify) {
				// TODO check if subscriber has authorization to read document
				if (xcapDiff == null) {
					// lazy build of xcap diff
					xcapDiff = buildDocumentXcapDiff(documentSelector, newETag, oldETag);
				}
				// tell underlying sip event framework to notify subscriber
				sbb.getParentSbbCMP().notifySubscriber(s.getKey(), xcapDiff, getXcapDiffContentTypeHeader());
			}	
		}
			
		}
	}
	
	private XcapDiff buildDocumentXcapDiff(DocumentSelector documentSelector, String newETag, String oldETag) {
		// build notify content
		XcapDiff xcapDiff = new XcapDiff();
		xcapDiff.setXcapRoot(ServerConfiguration.SCHEME_AND_AUTHORITY_URI+ServerConfiguration.XCAP_ROOT+"/");
		ObjectFactory objectFactory = new ObjectFactory();
		DocumentType documentType = objectFactory.createDocumentType();
		documentType.setSel(documentSelector.toString());
		if (oldETag == null && newETag != null) {
			// document created
			documentType.setNewEtag(newETag);
		}
		else if (oldETag != null && newETag == null) {
			// document deleted
			documentType.setPreviousEtag(oldETag);
		} 
		else if (oldETag != null && newETag != null) {
			// document replaced
			documentType.setNewEtag(newETag);
			documentType.setPreviousEtag(oldETag);
			// FIXME provide patch ops content
		}
		xcapDiff.getDocumentOrElementOrAttribute().add(documentType);
		return xcapDiff;
	}
	
	private static Logger logger = Logger.getLogger(XcapDiffSubscriptionControl.class);
	
}
