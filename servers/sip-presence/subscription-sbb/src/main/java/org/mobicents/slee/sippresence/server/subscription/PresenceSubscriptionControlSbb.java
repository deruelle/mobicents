package org.mobicents.slee.sippresence.server.subscription;

import javax.sip.message.Response;
import javax.slee.ChildRelation;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.mobicents.slee.sipevent.server.publication.PublicationControlSbbLocalObject;
import org.mobicents.slee.sipevent.server.publication.pojo.ComposedPublication;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription;
import org.mobicents.slee.sipevent.server.subscription.NotifyContent;
import org.mobicents.slee.sipevent.server.subscription.SubscriptionControlSbb;

/**
 * Subscription control sbb for a SIP Presence Server.
 * @author eduardomartins
 *
 */
public abstract class PresenceSubscriptionControlSbb extends SubscriptionControlSbb {

	private static Logger logger = Logger.getLogger(PresenceSubscriptionControlSbb.class);
	private final String[] eventPackages = {"presence"};
	
	public abstract ChildRelation getChildRelation();
	
	protected Logger getLogger() {
		return logger;
	}
	
	protected String getContactAddressString() {
		return "Presence Agent <sip:127.0.0.1:5060>";
	}
	
	protected String[] getEventPackages() {
		return eventPackages;
	}
	
	protected int isSubscriberAuthorized(String subscriber, String notifier, String eventPackage) {
		return Response.OK;		
	}
	
	protected NotifyContent getNotifyContent(Subscription subscription) {
		try {	
			ComposedPublication composedPublication = ((PublicationControlSbbLocalObject)getChildRelation().create()).getComposedPublication(subscription.getNotifier(), subscription.getSubscriptionKey().getEventPackage());
			if (composedPublication != null) {
				return new NotifyContent(composedPublication.getUnmarshalledContent(),headerFactory.createContentTypeHeader(composedPublication.getContentType(),composedPublication.getContentSubType()));
			}
		}
		catch (Exception e) {
			getLogger().error("failed to get notify content",e);			
		}
		return null;
	}
	
	protected JAXBElement filterContentPerSubscriber(
			JAXBElement unmarshalledContent, String subscriber) {
		return unmarshalledContent;
	}
	
	/*
	 * JAXB context is thread safe
	 */
	private static final JAXBContext jaxbContext = initJAXBContext();
	private static JAXBContext initJAXBContext() {
		try {
			return JAXBContext.newInstance(
					"org.mobicents.slee.sippresence.pojo.pidf" +
					":org.mobicents.slee.sippresence.pojo.rpid" +
					":org.mobicents.slee.sippresence.pojo.datamodel" +
					":org.mobicents.slee.sippresence.pojo.commonschema");
		} catch (JAXBException e) {
			logger.error("failed to create jaxb context");
			return null;
		}
	}
	
	protected Marshaller getMarshaller() {
		try {
			return jaxbContext.createMarshaller();
		} catch (JAXBException e) {
			getLogger().error("failed to create unmarshaller",e);
			return null;
		}
	}
	
}
