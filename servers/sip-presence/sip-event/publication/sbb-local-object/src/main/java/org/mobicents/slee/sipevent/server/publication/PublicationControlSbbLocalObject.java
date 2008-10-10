package org.mobicents.slee.sipevent.server.publication;

import javax.sip.address.URI;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.Header;

import org.mobicents.slee.sipevent.server.publication.pojo.ComposedPublication;

public interface PublicationControlSbbLocalObject extends
		PublicationClientControlSbbLocalObject {

	/**
	 * Retrieves the composed publication for the specified entity and event
	 * package.
	 * 
	 * @param entity
	 * @param eventPackage
	 * @return
	 */
	public ComposedPublication getComposedPublication(String entity,
			String eventPackage);

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
	 * Checks if this server is responsible for the resource publishing state.
	 * 
	 */
	public boolean isResponsibleForResource(URI uri);
}
