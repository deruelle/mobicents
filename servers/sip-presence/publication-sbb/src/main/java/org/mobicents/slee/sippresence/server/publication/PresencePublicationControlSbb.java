package org.mobicents.slee.sippresence.server.publication;

import java.io.StringReader;
import java.text.ParseException;

import javax.sip.address.URI;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.Header;
import javax.sip.message.Request;
import javax.slee.ChildRelation;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.mobicents.slee.sipevent.server.publication.pojo.ComposedPublication;
import org.mobicents.slee.sipevent.server.publication.pojo.Publication;
import org.mobicents.slee.sipevent.server.publication.PublicationControlSbb;
import org.mobicents.slee.sipevent.server.subscription.SubscriptionControlSbbLocalObject;
import org.mobicents.slee.sippresence.pojo.pidf.Presence;

/**
 * Publication control sbb for a SIP Presence Server.
 * @author eduardomartins
 *
 */
public abstract class PresencePublicationControlSbb extends PublicationControlSbb {
	 	
	private static Logger logger = Logger.getLogger(PresencePublicationControlSbb.class);
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
	
	protected void notifySubscribers(ComposedPublication composedPublication) {		
		try {
			SubscriptionControlSbbLocalObject childSbb = (SubscriptionControlSbbLocalObject) getChildRelation().create();
			ContentTypeHeader contentTypeHeader = 
				(composedPublication.getContentType() == null || composedPublication.getContentSubType() == null) ?
						null : headerFactory.createContentTypeHeader(composedPublication.getContentType(), composedPublication.getContentSubType());
			if (composedPublication.getDocument() != null && composedPublication.getUnmarshalledContent() == null) {
				// content needs to unmarshalled
				StringReader stringReader = new StringReader(composedPublication.getDocument());
				composedPublication.setUnmarshalledContent((JAXBElement)getUnmarshaller().unmarshal(stringReader));
				stringReader.close();
			}
			childSbb.notifySubscribers(composedPublication.getComposedPublicationKey().getEntity(), composedPublication.getComposedPublicationKey().getEventPackage(), composedPublication.getUnmarshalledContent(), contentTypeHeader);
		} catch (Exception e) {
			getLogger().error("failed to notify subscribers for "+composedPublication.getComposedPublicationKey(),e);
		}
	}
	
	@Override
	protected boolean authorizePublication(Request request,
			JAXBElement unmarshalledContent) {
		// returns true if request uri matches entity inside pidf doc
		return ((JAXBElement<Presence>)unmarshalledContent).getValue().getEntity().equals(request.getRequestURI().toString());
	}
	
	@Override
	protected boolean acceptsContentType(String eventPackage,
			ContentTypeHeader contentTypeHeader) {
		// FIXME 
		return true;
	}
	
	@Override
	protected Header getAcceptsHeader(String eventPackage) {
		// FIXME
		if (eventPackage.equals("presence")) {
			try {
				return headerFactory.createAcceptHeader("application","pidf+xml");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
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
	
	protected Unmarshaller getUnmarshaller() {
		try {
			return jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			getLogger().error("failed to create unmarshaller",e);
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
	
	protected ComposedPublication combinePublication(Publication publication,
			ComposedPublication composedPublication) {
		// for now don't compose, maintain only newest state
		composedPublication.setDocument(publication.getDocument());
		composedPublication.setUnmarshalledContent(publication.getUnmarshalledContent());
		composedPublication.setContentSubType(publication.getContentSubType());
		composedPublication.setContentType(publication.getContentType());
		return composedPublication;
	}
	
	protected boolean isResponsibleForResource(URI uri) {
		return true;
	}
}
