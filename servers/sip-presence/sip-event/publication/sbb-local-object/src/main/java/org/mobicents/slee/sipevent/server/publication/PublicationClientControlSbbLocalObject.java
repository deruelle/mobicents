package org.mobicents.slee.sipevent.server.publication;

import javax.slee.SbbLocalObject;

/**
 * Sbb local obejct interface for a client of the SIP Event Publication Control
 * framework.
 * 
 * @author martins
 * 
 */
public interface PublicationClientControlSbbLocalObject extends SbbLocalObject {

	/**
	 * Stores the parent sbb local object used for callbacks.
	 * 
	 * @param parentSbb
	 */
	public void setParentSbb(
			PublicationClientControlParentSbbLocalObject parentSbb);

	/**
	 * Creates a new publication for the specified Entity and SIP Event Package.
	 * 
	 * @param requestId
	 *            an object that identifies the request, the child sbb will
	 *            return it when providing the response
	 * @param entity
	 * @param eventPackage
	 * @param document
	 * @param contentType
	 * @param contentSubType
	 * @param expires
	 *            the time in seconds, which the publication is valid, use -1 if
	 *            publication should not expire
	 */
	public void newPublication(Object requestId, String entity,
			String eventPackage, String document, String contentType,
			String contentSubType, int expires);

	/**
	 * Refreshes the publication identified by the specified Entity, SIP Event
	 * Package and ETag.
	 * 
	 * @param requestId
	 *            an object that identifies the request, the child sbb will
	 *            return it when providing the response
	 * @param entity
	 * @param eventPackage
	 * @param eTag
	 * @param expires
	 *            the time in seconds, which the publication is valid, use -1 if
	 *            publication should not expire
	 */
	public void refreshPublication(Object requestId, String entity,
			String eventPackage, String eTag, int expires);

	/**
	 * Modifies the publication identified by the specified Entity, SIP Event
	 * Package and ETag.
	 * 
	 * @param requestId
	 *            an object that identifies the request, the child sbb will
	 *            return it when providing the response
	 * @param entity
	 * @param eventPackage
	 * @param eTag
	 * @param document
	 * @param contentType
	 * @param contentSubType
	 * @param expires
	 *            the time in seconds, which the publication is valid, use -1 if
	 *            publication should not expire
	 */
	public void modifyPublication(Object requestId, String entity,
			String eventPackage, String eTag, String document,
			String contentType, String contentSubType, int expires);

	/**
	 * Removes the publication identified by the specified Entity, SIP Event
	 * Package and ETag.
	 * 
	 * @param requestId
	 *            an object that identifies the request, the child sbb will
	 *            return it when providing the response
	 * @param entity
	 * @param eventPackage
	 * @param eTag
	 */
	public void removePublication(Object requestId, String entity,
			String eventPackage, String eTag);

	/**
	 * Shutdown the interface to the sip event server
	 */
	public void shutdown();
}
