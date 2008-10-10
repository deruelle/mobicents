package org.mobicents.slee.sipevent.server.publication;

import javax.slee.SbbLocalObject;

/**
 * Callback Interface for an SBB that uses the
 * {@link PublicationClientControlSbbLocalObject} as a child.
 * 
 * @author martins
 * 
 */
public interface PublicationClientControlParentSbbLocalObject extends
		SbbLocalObject {

	/**
	 * Ok Response about a new publication request.
	 * 
	 * @param requestId
	 * @param eTag
	 * @param expires
	 */
	public void newPublicationOk(Object requestId, String eTag, int expires)
			throws Exception;

	/**
	 * Ok Response about a refresh publication request.
	 * 
	 * @param requestId
	 * @param eTag
	 * @param expires
	 */
	public void refreshPublicationOk(Object requestId, String eTag, int expires)
			throws Exception;

	/**
	 * Ok Response about a modify publication request.
	 * 
	 * @param requestId
	 * @param eTag
	 * @param expires
	 */
	public void modifyPublicationOk(Object requestId, String eTag, int expires)
			throws Exception;

	/**
	 * Ok Response about a remove publication request.
	 * 
	 * @param requestId
	 */
	public void removePublicationOk(Object requestId) throws Exception;

	/**
	 * Error Response about a new publication request.
	 * 
	 * @param requestId
	 * @param error
	 *            sip matching error status code
	 */
	public void newPublicationError(Object requestId, int error);

	/**
	 * Error about a refresh publication request.
	 * 
	 * @param requestId
	 * @param error
	 *            sip matching error status code
	 */
	public void refreshPublicationError(Object requestId, int error);

	/**
	 * Error about a modify publication request.
	 * 
	 * @param requestId
	 * @param error
	 *            sip matching error status code
	 */
	public void modifyPublicationError(Object requestId, int error);

	/**
	 * Error about a remove publication request.
	 * 
	 * @param requestId
	 * @param error
	 *            sip matching error status code
	 */
	public void removePublicationError(Object requestId, int error);
}
