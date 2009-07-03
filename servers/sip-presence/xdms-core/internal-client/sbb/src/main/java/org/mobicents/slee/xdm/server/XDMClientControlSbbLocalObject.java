package org.mobicents.slee.xdm.server;

import javax.slee.SbbLocalObject;

import org.openxdm.xcap.common.key.XcapUriKey;
import org.openxdm.xcap.common.resource.AttributeResource;
import org.openxdm.xcap.common.resource.ElementResource;
import org.openxdm.xcap.common.uri.DocumentSelector;

/**
 * Client interface to interact with an XDM Server. IF used by an sbb in a child
 * relation, then that sbb's local object must implement
 * {@link XDMClientControlParentSbbLocalObject}.
 * 
 * @author martins
 * 
 */
public interface XDMClientControlSbbLocalObject extends SbbLocalObject {

	/**
	 * Used to set the call back sbb local object in the sbb implementing this
	 * interface. Must be used whenever a new object of this interface is
	 * created.
	 * 
	 * An example:
	 * 
	 * ChildRelation childRelation = getChildRelation();
	 * XDMClientControlSbbLocalObject childSbb =
	 * (XDMClientControlSbbLocalObject) childRelation.create();
	 * childSbb.setParentSbb(
	 * (XDMClientControlParentSbbLocalObject)this.getSbbContext().getSbbLocalObject());
	 * 
	 * 
	 * @param parent
	 */
	public void setParentSbb(XDMClientControlParentSbbLocalObject parentSbb);

	// --- get/put/delete interface methods

	/**
	 * Retrieves the XML resource from the XCAP server, for the specified key.
	 * Response is async.
	 */
	public void get(XcapUriKey key, String user);

	/**
	 * Puts the specified content in the XCAP Server, in the XCAP URI pointed by
	 * the key. Response is async.
	 * 
	 * @param key
	 * @param mimetype
	 *            the mimetype of the content to put, for document each XCAP App
	 *            Usage defines their own mimetype, but for elements and
	 *            attributes you can use {@link ElementResource} and
	 *            {@link AttributeResource} static MIMETYPE fields.
	 * @param content
	 */
	public void put(XcapUriKey key, String mimetype, byte[] content, String user);

	/**
	 * Puts the specified content in the XCAP Server, in the XCAP URI pointed by
	 * the key, if the specified ETag matches the current one on the server.
	 * 
	 * @param key
	 * @param eTag
	 * @param mimetype
	 *            the mimetype of the content to put, for document each XCAP App
	 *            Usage defines their own mimetype, but for elements and
	 *            attributes you can use {@link ElementResource} and
	 *            {@link AttributeResource} static MIMETYPE fields.
	 * @param content
	 */
	public void putIfMatch(XcapUriKey key, String eTag, String mimetype,
			byte[] content, String user);

	/**
	 * Puts the specified content in the XCAP Server, in the XCAP URI pointed by
	 * the key, if the specified ETag does not matches the current one on the
	 * server.
	 * 
	 * @param key
	 * @param eTag
	 * @param mimetype
	 *            the mimetype of the content to put, for document each XCAP App
	 *            Usage defines their own mimetype, but for elements and
	 *            attributes you can use {@link ElementResource} and
	 *            {@link AttributeResource} static MIMETYPE fields.
	 * @param content
	 */
	public void putIfNoneMatch(XcapUriKey key, String eTag, String mimetype,
			byte[] content, String user);

	/**
	 * Deletes the content related the specified XCAP URI key.
	 * 
	 * @param key
	 */
	public void delete(XcapUriKey key, String user);

	/**
	 * Deletes the content related the specified XCAP URI key, if the specified
	 * ETag matches the current one on the server.
	 * 
	 * @param key
	 * @param eTag
	 */
	public void deleteIfMatch(XcapUriKey key, String eTag, String user);

	/**
	 * Deletes the content related the specified XCAP URI key, if the specified
	 * ETag does not matches the current one on the server.
	 * 
	 * @param key
	 * @param eTag
	 */
	public void deleteIfNoneMatch(XcapUriKey key, String eTag, String user);

	// --- subscribe/unsubscribe interface methods

	/**
	 * Subscribes changes on a XML document, stored on the XDM.
	 * 
	 * @param key
	 */
	public void subscribeDocument(DocumentSelector documentSelector);

	/**
	 * Unsubscribes changes on a XML document, stored on the XDM.
	 * 
	 * @param key
	 */
	public void unsubscribeDocument(DocumentSelector documentSelector);

	/**
	 * Subscribes changes on XML documents of the specified app usage, stored on
	 * the XDM.
	 * 
	 * @param auid
	 */
	public void subscribeAppUsage(String auid);

	/**
	 * Unsubscribes changes on XML documents of the specified app usage, stored
	 * on the XDM.
	 * 
	 * @param auid
	 */
	public void unsubscribeAppUsage(String auid);

}
