package org.mobicents.slee.sipevent.server.publication;

import javax.sip.address.URI;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.Header;
import javax.slee.SbbLocalObject;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.mobicents.slee.sipevent.server.publication.pojo.ComposedPublication;
import org.mobicents.slee.sipevent.server.publication.pojo.Publication;

/**
 * Child sbb that handles the sip event publication control implementation
 * details
 * 
 * @author martins
 * 
 */
public interface ImplementedPublicationControlSbbLocalObject extends
		SbbLocalObject {

	/**
	 * the impl class SIP event packages supported
	 * 
	 * @return
	 */
	public String[] getEventPackages();

	/**
	 * Verifies if the specified content type header can be accepted for the
	 * specified event package.
	 * 
	 * @param eventPackage
	 * @param contentTypeHeader
	 * @return
	 */
	public boolean acceptsContentType(String eventPackage,
			ContentTypeHeader contentTypeHeader);

	/**
	 * Retrieves the accepted content types for the specified event package.
	 * 
	 * @param eventPackage
	 * @return
	 */
	public Header getAcceptsHeader(String eventPackage);

	/**
	 * Notifies subscribers about a publication update for the specified entity
	 * regarding the specified evtnt package.
	 * 
	 * @param composedPublication
	 */
	public void notifySubscribers(ComposedPublication composedPublication);

	/**
	 * Retrieves a JAXB Unmarshaller to parse a publication content.
	 * 
	 * @return
	 */
	public Unmarshaller getUnmarshaller();

	/**
	 * Retrieves a JAXB Marshaller to convert a JAXBElement to a String.
	 * 
	 * @return
	 */
	public Marshaller getMarshaller();

	/**
	 * Combines a new publication with the current composed publication.
	 * 
	 * @return the updated composed publication
	 */
	public ComposedPublication combinePublication(Publication publication,
			ComposedPublication composedPublication);

	/**
	 * Checks if this server is responsible for the resource publishing state.
	 * 
	 */
	public boolean isResponsibleForResource(URI uri);

	/**
	 * verifies if entity is authorized to publish the content
	 * 
	 * @param entity
	 * @param unmarshalledContent
	 * @return
	 */
	public boolean authorizePublication(String entity,
			JAXBElement unmarshalledContent);

	/**
	 * 
	 * Through this method the event package implementation sbb has a chance to
	 * define an alternative publication value for the one expired, this can
	 * allow a behavior such as defining offline status in a presence resource.
	 * 
	 * @param publication
	 * @return
	 */
	public Publication getAlternativeValueForExpiredPublication(
			Publication publication);

}
