package org.openxdm.xcap.client;

import java.io.IOException;

import org.apache.commons.httpclient.HttpException;
import org.openxdm.xcap.common.key.XcapUriKey;
import org.openxdm.xcap.common.resource.AttributeResource;
import org.openxdm.xcap.common.resource.ElementResource;

public interface XCAPClient {

	// CLIENT MANAGEMENT

	public void shutdown();

	// CLIENT OPERATIONS

	/**
	 * Retrieves the XML resource from the XCAP server, for the specified key.
	 */
	public Response get(XcapUriKey key) throws HttpException, IOException;

	/**
	 * Puts the specified content in the XCAP Server, in the XCAP URI pointed by
	 * the key.
	 * 
	 * @param key
	 * @param mimetype
	 *            the mimetype of the content to put, for document each XCAP App
	 *            Usage defines their own mimetype, but for elements and attributes
	 *            you can use {@link ElementResource} and
	 *            {@link AttributeResource} static MIMETYPE fields.
	 * @param content
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public Response put(XcapUriKey key, String mimetype, String content)
			throws HttpException, IOException;

	/**
	 * Puts the specified content in the XCAP Server, in the XCAP URI pointed by
	 * the key.
	 * 
	 * @param key
	 * @param mimetype
	 *            the mimetype of the content to put, for document each XCAP App
	 *            Usage defines their own mimetype, but for elements and attributes
	 *            you can use {@link ElementResource} and
	 *            {@link AttributeResource} static MIMETYPE fields.
	 * @param content
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public Response put(XcapUriKey key, String mimetype, byte[] content)
			throws HttpException, IOException;

	/**
	 * Puts the specified content in the XCAP Server, in the XCAP URI pointed by
	 * the key, if the specified ETag matches the current one on the server.
	 * 
	 * @param key
	 * @param eTag
	 * @param mimetype
	 *            the mimetype of the content to put, for document each XCAP App
	 *            Usage defines their own mimetype, but for elements and attributes
	 *            you can use {@link ElementResource} and
	 *            {@link AttributeResource} static MIMETYPE fields.
	 * @param content
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public Response putIfMatch(XcapUriKey key, String eTag, String mimetype,
			String content) throws HttpException, IOException;

	/**
	 * Puts the specified content in the XCAP Server, in the XCAP URI pointed by
	 * the key, if the specified ETag matches the current one on the server.
	 * 
	 * @param key
	 * @param eTag
	 * @param mimetype
	 *            the mimetype of the content to put, for document each XCAP App
	 *            Usage defines their own mimetype, but for elements and attributes
	 *            you can use {@link ElementResource} and
	 *            {@link AttributeResource} static MIMETYPE fields.
	 * @param content
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public Response putIfMatch(XcapUriKey key, String eTag, String mimetype,
			byte[] content) throws HttpException, IOException;

	/**
	 * Puts the specified content in the XCAP Server, in the XCAP URI pointed by
	 * the key, if the specified ETag does not matches the current one on the
	 * server.
	 * 
	 * @param key
	 * @param eTag
	 * @param mimetype
	 *            the mimetype of the content to put, for document each XCAP App
	 *            Usage defines their own mimetype, but for elements and attributes
	 *            you can use {@link ElementResource} and
	 *            {@link AttributeResource} static MIMETYPE fields.
	 * @param content
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public Response putIfNoneMatch(XcapUriKey key, String eTag,
			String mimetype, String content) throws HttpException, IOException;

	/**
	 * Puts the specified content in the XCAP Server, in the XCAP URI pointed by
	 * the key, if the specified ETag does not matches the current one on the
	 * server.
	 * 
	 * @param key
	 * @param eTag
	 * @param mimetype
	 *            the mimetype of the content to put, for document each XCAP App
	 *            Usage defines their own mimetype, but for elements and attributes
	 *            you can use {@link ElementResource} and
	 *            {@link AttributeResource} static MIMETYPE fields.
	 * @param content
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public Response putIfNoneMatch(XcapUriKey key, String eTag,
			String mimetype, byte[] content) throws HttpException, IOException;

	/**
	 * Deletes the content related the specified XCAP URI key.
	 * 
	 * @param key
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public Response delete(XcapUriKey key) throws HttpException, IOException;

	/**
	 * Deletes the content related the specified XCAP URI key, if the specified
	 * ETag matches the current one on the server.
	 * 
	 * @param key
	 * @param eTag
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public Response deleteIfMatch(XcapUriKey key, String eTag)
			throws HttpException, IOException;

	/**
	 * Deletes the content related the specified XCAP URI key, if the specified
	 * ETag does not matches the current one on the server.
	 * 
	 * @param key
	 * @param eTag
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public Response deleteIfNoneMatch(XcapUriKey key, String eTag)
			throws HttpException, IOException;

}
