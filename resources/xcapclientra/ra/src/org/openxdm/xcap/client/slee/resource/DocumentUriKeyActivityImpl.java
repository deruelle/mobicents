package org.openxdm.xcap.client.slee.resource;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.commons.httpclient.HttpException;
import org.openxdm.xcap.client.key.DocumentUriKey;

public class DocumentUriKeyActivityImpl extends XcapUriKeyActivityImpl implements DocumentUriKeyActivity {

	private DocumentUriKey key;
	
	public DocumentUriKeyActivityImpl(XCAPClientResourceAdaptor ra, DocumentUriKey key) {
		super(ra, key);
		this.key = key;		
	}

	public DocumentUriKey getDocumentUriKey() {
		return key;
	}

	public void getAndUnmarshall() throws HttpException, IOException, JAXBException {
		// create handler and start it
		getRA().getExecutorService().execute(getRA().new AsyncGetAndUnmarshallDocumentHandler(key));
	}

	public void marshallAndPut(String mimetype, Object content) {		
		// create handler and start it
		getRA().getExecutorService().execute(getRA().new AsyncMarshallAndPutDocumentHandler(key,mimetype,content));	
	}
	
	public void marshallAndPutIfMatch(String eTag, String mimetype, Object content) {
		// create handler and start it
		getRA().getExecutorService().execute(getRA().new AsyncMarshallAndPutDocumentIfMatchHandler(key,eTag,mimetype,content));
	}
	
	public void marshallAndPutIfNoneMatch(String eTag, String mimetype, Object content) {
		// create handler and start it
		getRA().getExecutorService().execute(getRA().new AsyncMarshallAndPutDocumentIfNoneMatchHandler(key,eTag,mimetype,content));
	}

	public void put(String mimetype, String content) {
		// create handler and start it
		getRA().getExecutorService().execute(getRA().new AsyncPutDocumentStringHandler(key,mimetype,content));
	}
	
	public void putIfMatch(String eTag, String mimetype, String content) {
		// create handler and start it		
		getRA().getExecutorService().execute(getRA().new AsyncPutDocumentStringIfMatchHandler(key,eTag,mimetype,content));
	}
	
	public void putIfNoneMatch(String eTag, String mimetype, String content) {
		// create handler and start it
		getRA().getExecutorService().execute(getRA().new AsyncPutDocumentStringIfNoneMatchHandler(key,eTag,mimetype,content));
	}
	
	public void put(String mimetype, byte[] content) {
		// create handler and start it
		getRA().getExecutorService().execute(getRA().new AsyncPutDocumentByteArrayHandler(key,mimetype,content));
	}
	
	public void putIfMatch(String eTag, String mimetype, byte[] content) {
		// create handler and start it
		getRA().getExecutorService().execute(getRA().new AsyncPutDocumentByteArrayIfMatchHandler(key,eTag,mimetype,content));
	}
	
	public void putIfNoneMatch(String eTag, String mimetype, byte[] content) {
		// create handler and start it
		getRA().getExecutorService().execute(getRA().new AsyncPutDocumentByteArrayIfNoneMatchHandler(key,eTag,mimetype,content));
	}	
	
	public void delete() {
		// create handler and start it
		getRA().getExecutorService().execute(getRA().new AsyncDeleteDocumentHandler(key));			
	}

	public void deleteIfMatch(String eTag) {
		// create handler and start it
		getRA().getExecutorService().execute(getRA().new AsyncDeleteDocumentIfMatchHandler(key,eTag));		
	}

	public void deleteIfNoneMatch(String eTag) {
		// create handler and start it
		getRA().getExecutorService().execute(getRA().new AsyncDeleteDocumentIfNoneMatchHandler(key,eTag));		
	}

}
