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

public class XCAPClientResourceAdaptorSbbInterfaceImpl implements XCAPClientResourceAdaptorSbbInterface {
    
	private XCAPClientResourceAdaptor ra;
	
	public XCAPClientResourceAdaptorSbbInterfaceImpl(XCAPClientResourceAdaptor ra) {
		this.ra = ra;
	}
	
	public void setUnmarshallingResponseError(boolean value) throws JAXBException {
		ra.getClient().setUnmarshallingResponseError(value);		
	}
	
	public boolean isUnmarshallingResponseError() {
		return ra.getClient().isUnmarshallingResponseError();
	}
	
	
	public Response get(XcapUriKey key) throws HttpException, IOException, JAXBException {
		return ra.getClient().get(key);
	}
	
	public Response getAndUnmarshallDocument(DocumentUriKey key) throws HttpException, IOException, JAXBException {
		return ra.getClient().getAndUnmarshallDocument(key);
	}
	
	public Response getAndUnmarshallElement(ElementUriKey key) throws HttpException, IOException, JAXBException {
		return ra.getClient().getAndUnmarshallElement(key);
	}
		
	public Response marshallAndPutDocument(DocumentUriKey key, String mimetype, Object content) throws JAXBException, HttpException, IOException {
		return ra.getClient().marshallAndPutDocument(key,mimetype,content);
	}
	
	public Response marshallAndPutDocumentIfMatch(DocumentUriKey key, String eTag, String mimetype, Object content) throws JAXBException, HttpException, IOException {
		return ra.getClient().marshallAndPutDocumentIfMatch(key,eTag,mimetype,content);
	}
	
	public Response marshallAndPutDocumentIfNoneMatch(DocumentUriKey key, String eTag, String mimetype, Object content) throws JAXBException, HttpException, IOException {
		return ra.getClient().marshallAndPutDocumentIfNoneMatch(key,eTag,mimetype,content);
	}
	
	public Response marshallAndPutElement(ElementUriKey key, Object content) throws JAXBException, HttpException, IOException {
		return ra.getClient().marshallAndPutElement(key,content);
	}
	
	public Response marshallAndPutElementIfMatch(ElementUriKey key, String eTag, Object content) throws JAXBException, HttpException, IOException {
		return ra.getClient().marshallAndPutElementIfMatch(key,eTag,content);
	}
	
	public Response marshallAndPutElementIfNoneMatch(ElementUriKey key, String eTag, Object content) throws JAXBException, HttpException, IOException {
		return ra.getClient().marshallAndPutElementIfNoneMatch(key,eTag,content);
	}
	
	// PUT DOCUMENT
	
	public Response putDocument(DocumentUriKey key, String mimetype, String content) throws HttpException, IOException, JAXBException {
		return ra.getClient().putDocument(key,mimetype,content);
	}
	
	public Response putDocumentIfMatch(DocumentUriKey key, String eTag, String mimetype, String content) throws HttpException, IOException, JAXBException {
		return ra.getClient().putDocumentIfMatch(key,eTag,mimetype,content);
	}
	
	public Response putDocumentIfNoneMatch(DocumentUriKey key, String eTag, String mimetype, String content) throws HttpException, IOException, JAXBException {
		return ra.getClient().putDocumentIfNoneMatch(key,eTag,mimetype,content);		
	}
	
	public Response putDocument(DocumentUriKey key, String mimetype, byte[] content) throws HttpException, IOException, JAXBException {
		return ra.getClient().putDocument(key,mimetype,content);
	}
	
	public Response putDocumentIfMatch(DocumentUriKey key, String eTag, String mimetype, byte[] content) throws HttpException, IOException, JAXBException {
		return ra.getClient().putDocumentIfMatch(key,eTag,mimetype,content);
	}
	
	public Response putDocumentIfNoneMatch(DocumentUriKey key, String eTag, String mimetype, byte[] content) throws HttpException, IOException, JAXBException {
		return ra.getClient().putDocumentIfNoneMatch(key,eTag,mimetype,content);
	}
	
	// PUT ELEMENT
	
	public Response putElement(ElementUriKey key, String content) throws HttpException, IOException, JAXBException {
		return ra.getClient().putElement(key,content);
	}
	
	public Response putElementIfMatch(ElementUriKey key, String eTag, String content) throws HttpException, IOException, JAXBException {
		return ra.getClient().putElementIfMatch(key,eTag,content);
	}
	
	public Response putElementIfNoneMatch(ElementUriKey key, String eTag, String content) throws HttpException, IOException, JAXBException {
		return ra.getClient().putElementIfNoneMatch(key,eTag,content);		
	}
	
	public Response putElement(ElementUriKey key, byte[] content) throws HttpException, IOException, JAXBException {
		return ra.getClient().putElement(key,content);
	}
	
	public Response putElementIfMatch(ElementUriKey key, String eTag, byte[] content) throws HttpException, IOException, JAXBException {
		return ra.getClient().putElementIfMatch(key,eTag,content);
	}
	
	public Response putElementIfNoneMatch(ElementUriKey key, String eTag, byte[] content) throws HttpException, IOException, JAXBException {
		return ra.getClient().putElementIfNoneMatch(key,eTag,content);
	}
	
	// PUT ELEMENT
	
	public Response putAttribute(AttributeUriKey key, String content) throws HttpException, IOException, JAXBException {
		return ra.getClient().putAttribute(key,content);
	}
	
	public Response putAttributeIfMatch(AttributeUriKey key, String eTag, String content) throws HttpException, IOException, JAXBException {
		return ra.getClient().putAttributeIfMatch(key,eTag,content);
	}
	
	public Response putAttributeIfNoneMatch(AttributeUriKey key, String eTag, String content) throws HttpException, IOException, JAXBException {
		return ra.getClient().putAttributeIfNoneMatch(key,eTag,content);		
	}
	
	public Response putAttribute(AttributeUriKey key, byte[] content) throws HttpException, IOException, JAXBException {
		return ra.getClient().putAttribute(key,content);
	}
	
	public Response putAttributeIfMatch(AttributeUriKey key, String eTag, byte[] content) throws HttpException, IOException, JAXBException {
		return ra.getClient().putAttributeIfMatch(key,eTag,content);
	}
	
	public Response putAttributeIfNoneMatch(AttributeUriKey key, String eTag, byte[] content) throws HttpException, IOException, JAXBException {
		return ra.getClient().putAttributeIfNoneMatch(key,eTag,content);
	}
	
	// DELETE DOCUMENT
	
	public Response deleteDocument(DocumentUriKey key) throws HttpException, IOException, JAXBException {
		return ra.getClient().deleteDocument(key);
	}
	
	public Response deleteDocumentIfMatch(DocumentUriKey key, String eTag) throws HttpException, IOException, JAXBException {
		return ra.getClient().deleteDocumentIfMatch(key,eTag);
	}
	
	public Response deleteDocumentIfNoneMatch(DocumentUriKey key, String eTag) throws HttpException, IOException, JAXBException {
		return ra.getClient().deleteDocumentIfNoneMatch(key,eTag);
	}
	
	// DELETE ELEMENT
	
	public Response deleteElement(ElementUriKey key) throws HttpException, IOException, JAXBException {
		return ra.getClient().deleteElement(key);
	}
	
	public Response deleteElementIfMatch(ElementUriKey key, String eTag) throws HttpException, IOException, JAXBException {
		return ra.getClient().deleteElementIfMatch(key,eTag);
	}
	
	public Response deleteElementIfNoneMatch(ElementUriKey key, String eTag) throws HttpException, IOException, JAXBException {
		return ra.getClient().deleteElementIfNoneMatch(key,eTag);
	}
	
	// DELETE ATTRIBUTE
	
	public Response deleteAttribute(AttributeUriKey key) throws HttpException, IOException, JAXBException {
		return ra.getClient().deleteAttribute(key);
	}
	
	public Response deleteAttributeIfMatch(AttributeUriKey key, String eTag) throws HttpException, IOException, JAXBException {
		return ra.getClient().deleteAttributeIfMatch(key,eTag);
	}
	
	public Response deleteAttributeIfNoneMatch(AttributeUriKey key, String eTag) throws HttpException, IOException, JAXBException {
		return ra.getClient().deleteAttributeIfNoneMatch(key,eTag);
	}	

	public XcapUriKeyActivity createActivity(XcapUriKey key) throws ActivityAlreadyExistsException, CouldNotStartActivityException {
		if (key != null) {
			// create activity
			XcapUriKeyActivity activity = new XcapUriKeyActivityImpl(ra,key);
			// create handle
			XCAPResourceAdaptorActivityHandle handle = new XCAPResourceAdaptorActivityHandle(key);    		
			// start activity
			ra.getSleeEndpoint().activityStarted(handle);
			// save handle in ra
			ra.getHandles().add(handle);
			return activity;
		}
		else {
			throw new IllegalArgumentException("null key");
		}
	}

	public DocumentUriKeyActivity createActivity(DocumentUriKey key) throws ActivityAlreadyExistsException, CouldNotStartActivityException {
		if (key != null) {
			// create activity
			DocumentUriKeyActivity activity = new DocumentUriKeyActivityImpl(ra,key);
			// create handle
			XCAPResourceAdaptorActivityHandle handle = new XCAPResourceAdaptorActivityHandle(key);    		
			// start activity
			ra.getSleeEndpoint().activityStarted(handle);
			// save handle in ra
			ra.getHandles().add(handle);
			return activity;
		}
		else {
			throw new IllegalArgumentException("null key");
		}
	}

	public ElementUriKeyActivity createActivity(ElementUriKey key) throws ActivityAlreadyExistsException, CouldNotStartActivityException {
		if (key != null) {
			// create activity
			ElementUriKeyActivity activity = new ElementUriKeyActivityImpl(ra,key);
			// create handle
			XCAPResourceAdaptorActivityHandle handle = new XCAPResourceAdaptorActivityHandle(key);    		
			// start activity
			ra.getSleeEndpoint().activityStarted(handle);
			// save handle in ra
			ra.getHandles().add(handle);
			return activity;
		}
		else {
			throw new IllegalArgumentException("null key");
		}
	}

	public AttributeUriKeyActivity createActivity(AttributeUriKey key) throws ActivityAlreadyExistsException, CouldNotStartActivityException {
		if (key != null) {
			// create activity
			AttributeUriKeyActivity activity = new AttributeUriKeyActivityImpl(ra,key);
			// create handle
			XCAPResourceAdaptorActivityHandle handle = new XCAPResourceAdaptorActivityHandle(key);    		
			// start activity
			ra.getSleeEndpoint().activityStarted(handle);
			// save handle in ra
			ra.getHandles().add(handle);
			return activity;
		}
		else {
			throw new IllegalArgumentException("null key");
		}
	}

	public NamespaceBindingsUriKeyActivity createActivity(NamespaceBindingsUriKey key) throws ActivityAlreadyExistsException, CouldNotStartActivityException {
		if (key != null) {
			// create activity
			NamespaceBindingsUriKeyActivity activity = new NamespaceBindingsUriKeyActivityImpl(ra,key);
			// create handle
			XCAPResourceAdaptorActivityHandle handle = new XCAPResourceAdaptorActivityHandle(key);    		
			// start activity
			ra.getSleeEndpoint().activityStarted(handle);
			// save handle in ra
			ra.getHandles().add(handle);
			return activity;
		}
		else {
			throw new IllegalArgumentException("null key");
		}
	}

	public XcapUriKeyActivity getActivity(XcapUriKey key) {
		if (ra.getHandles().contains(new XCAPResourceAdaptorActivityHandle(key))) {
			// handle exists, recreate activity 
			return new XcapUriKeyActivityImpl(ra,key);
		}
		else {
			// handle does not exist
			return null;
		}
	}

	public DocumentUriKeyActivity getActivity(DocumentUriKey key) {
		if (ra.getHandles().contains(new XCAPResourceAdaptorActivityHandle(key))) {
			// handle exists, recreate activity 
			return new DocumentUriKeyActivityImpl(ra,key);
		}
		else {
			// handle does not exist
			return null;
		}
	}

	public ElementUriKeyActivity getActivity(ElementUriKey key) {
		if (ra.getHandles().contains(new XCAPResourceAdaptorActivityHandle(key))) {
			// handle exists, recreate activity 
			return new ElementUriKeyActivityImpl(ra,key);
		}
		else {
			// handle does not exist
			return null;
		}
	}

	public AttributeUriKeyActivity getActivity(AttributeUriKey key) {
		if (ra.getHandles().contains(new XCAPResourceAdaptorActivityHandle(key))) {
			// handle exists, recreate activity 
			return new AttributeUriKeyActivityImpl(ra,key);
		}
		else {
			// handle does not exist
			return null;
		}
	}

	public NamespaceBindingsUriKeyActivity getActivity(NamespaceBindingsUriKey key) {
		if (ra.getHandles().contains(new XCAPResourceAdaptorActivityHandle(key))) {
			// handle exists, recreate activity 
			return new NamespaceBindingsUriKeyActivityImpl(ra,key);
		}
		else {
			// handle does not exist
			return null;
		}
	}	
}