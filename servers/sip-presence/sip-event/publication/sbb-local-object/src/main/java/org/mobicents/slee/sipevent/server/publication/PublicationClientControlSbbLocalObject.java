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
	 * Used to set the call back sbb local object in the sbb implementing this
	 * interface. Must be used whenever a new object of this interface is
	 * created.
	 * 
	 * An example:
	 * 
	 * ChildRelation childRelation = getChildRelation();
	 * PublicationClientControlSbbLocalObject childSbb =
	 * (PublicationClientControlSbbLocalObject) childRelation.create();
	 * childSbb.setParentSbb(
	 * (PublicationClientControlParentSbbLocalObject)this.getSbbContext().getSbbLocalObject());
	 * 
	 * 
	 * @param parent
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
	 *            the time in seconds, which the publication is valid
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
	 *            the time in seconds, which the publication is valid
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
	 *            the time in seconds, which the publication is valid
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

}
