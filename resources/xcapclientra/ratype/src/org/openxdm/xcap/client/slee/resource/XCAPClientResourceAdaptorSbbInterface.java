package org.openxdm.xcap.client.slee.resource;

import java.io.IOException;

import javax.slee.resource.ActivityAlreadyExistsException;
import javax.slee.resource.CouldNotStartActivityException;
import javax.xml.bind.JAXBException;

import org.apache.commons.httpclient.HttpException;
import org.openxdm.xcap.client.Response;
import org.openxdm.xcap.client.key.AttributeUriKey;
import org.openxdm.xcap.client.key.DocumentUriKey;
import org.openxdm.xcap.client.key.ElementUriKey;
import org.openxdm.xcap.client.key.NamespaceBindingsUriKey;
import org.openxdm.xcap.client.key.XcapUriKey;

/**
 * This is the XCAP Client Resource Adaptor's Interface that Sbbs can use.
 * 
 * @author Eduardo Martins
 * @version 1.0
 * 
 */

public interface XCAPClientResourceAdaptorSbbInterface {
	
	public void setUnmarshallingResponseError(boolean value) throws JAXBException;
	
	public boolean isUnmarshallingResponseError();
	
	
	public Response get(XcapUriKey key) throws HttpException, IOException, JAXBException;
	
	public Response getAndUnmarshallDocument(DocumentUriKey key) throws HttpException, IOException, JAXBException;
	
	public Response getAndUnmarshallElement(ElementUriKey key) throws HttpException, IOException, JAXBException;
	
		
	public Response marshallAndPutDocument(DocumentUriKey key, String mimetype, Object content) throws JAXBException, HttpException, IOException;
	
	public Response marshallAndPutDocumentIfMatch(DocumentUriKey key, String eTag, String mimetype, Object content) throws JAXBException, HttpException, IOException;
	
	public Response marshallAndPutDocumentIfNoneMatch(DocumentUriKey key, String eTag, String mimetype, Object content) throws JAXBException, HttpException, IOException;
	
	public Response marshallAndPutElement(ElementUriKey key, Object content) throws JAXBException, HttpException, IOException;
	
	public Response marshallAndPutElementIfMatch(ElementUriKey key, String eTag, Object content) throws JAXBException, HttpException, IOException;
	
	public Response marshallAndPutElementIfNoneMatch(ElementUriKey key, String eTag, Object content) throws JAXBException, HttpException, IOException;
		
	
	public Response putDocument(DocumentUriKey key, String mimetype, String content) throws HttpException, IOException, JAXBException;
	
	public Response putDocumentIfMatch(DocumentUriKey key, String eTag, String mimetype, String content) throws HttpException, IOException, JAXBException;

	public Response putDocumentIfNoneMatch(DocumentUriKey key, String eTag, String mimetype, String content) throws HttpException, IOException, JAXBException;
	
	public Response putDocument(DocumentUriKey key, String mimetype, byte[] content) throws HttpException, IOException, JAXBException;
	
	public Response putDocumentIfMatch(DocumentUriKey key, String eTag, String mimetype, byte[] content) throws HttpException, IOException, JAXBException;
	
	public Response putDocumentIfNoneMatch(DocumentUriKey key, String eTag, String mimetype, byte[] content) throws HttpException, IOException, JAXBException;


	public Response putElement(ElementUriKey key, String content) throws HttpException, IOException, JAXBException;
	
	public Response putElementIfMatch(ElementUriKey key, String eTag, String content) throws HttpException, IOException, JAXBException;

	public Response putElementIfNoneMatch(ElementUriKey key, String eTag, String content) throws HttpException, IOException, JAXBException;
	
	public Response putElement(ElementUriKey key, byte[] content) throws HttpException, IOException, JAXBException;
	
	public Response putElementIfMatch(ElementUriKey key, String eTag, byte[] content) throws HttpException, IOException, JAXBException;
	
	public Response putElementIfNoneMatch(ElementUriKey key, String eTag, byte[] content) throws HttpException, IOException, JAXBException;
	
	
	public Response putAttribute(AttributeUriKey key, String content) throws HttpException, IOException, JAXBException;
	
	public Response putAttributeIfMatch(AttributeUriKey key, String eTag, String content) throws HttpException, IOException, JAXBException;

	public Response putAttributeIfNoneMatch(AttributeUriKey key, String eTag, String content) throws HttpException, IOException, JAXBException;
	
	public Response putAttribute(AttributeUriKey key, byte[] content) throws HttpException, IOException, JAXBException;
	
	public Response putAttributeIfMatch(AttributeUriKey key, String eTag, byte[] content) throws HttpException, IOException, JAXBException;
	
	public Response putAttributeIfNoneMatch(AttributeUriKey key, String eTag, byte[] content) throws HttpException, IOException, JAXBException;
	
	
	public Response deleteDocument(DocumentUriKey key) throws HttpException, IOException, JAXBException;

	public Response deleteDocumentIfMatch(DocumentUriKey key,String eTag) throws HttpException, IOException, JAXBException;
	
	public Response deleteDocumentIfNoneMatch(DocumentUriKey key,String eTag) throws HttpException, IOException, JAXBException;
	
	
	public Response deleteElement(ElementUriKey key) throws HttpException, IOException, JAXBException;

	public Response deleteElementIfMatch(ElementUriKey key,String eTag) throws HttpException, IOException, JAXBException;
	
	public Response deleteElementIfNoneMatch(ElementUriKey key,String eTag) throws HttpException, IOException, JAXBException;
	
	
	public Response deleteAttribute(AttributeUriKey key) throws HttpException, IOException, JAXBException;

	public Response deleteAttributeIfMatch(AttributeUriKey key,String eTag) throws HttpException, IOException, JAXBException;
	
	public Response deleteAttributeIfNoneMatch(AttributeUriKey key,String eTag) throws HttpException, IOException, JAXBException;
		
	
	public XcapUriKeyActivity createActivity(XcapUriKey key) throws ActivityAlreadyExistsException, CouldNotStartActivityException;
	
	public DocumentUriKeyActivity createActivity(DocumentUriKey key) throws ActivityAlreadyExistsException, CouldNotStartActivityException;
	
	public ElementUriKeyActivity createActivity(ElementUriKey key) throws ActivityAlreadyExistsException, CouldNotStartActivityException;
	
	public AttributeUriKeyActivity createActivity(AttributeUriKey key) throws ActivityAlreadyExistsException, CouldNotStartActivityException;
	
	public NamespaceBindingsUriKeyActivity createActivity(NamespaceBindingsUriKey key) throws ActivityAlreadyExistsException, CouldNotStartActivityException;
	
	
	public XcapUriKeyActivity getActivity(XcapUriKey key);
	
	public DocumentUriKeyActivity getActivity(DocumentUriKey key);
	
	public ElementUriKeyActivity getActivity(ElementUriKey key);
	
	public AttributeUriKeyActivity getActivity(AttributeUriKey key);
	
	public NamespaceBindingsUriKeyActivity getActivity(NamespaceBindingsUriKey key);
	
}
