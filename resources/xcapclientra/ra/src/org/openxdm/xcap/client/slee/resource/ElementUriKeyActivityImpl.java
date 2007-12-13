package org.openxdm.xcap.client.slee.resource;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.commons.httpclient.HttpException;
import org.openxdm.xcap.client.key.ElementUriKey;

public class ElementUriKeyActivityImpl extends XcapUriKeyActivityImpl implements ElementUriKeyActivity {

	private ElementUriKey key;
	
	public ElementUriKeyActivityImpl(XCAPClientResourceAdaptor ra, ElementUriKey key) {
		super(ra, key);
		this.key = key;
	}

	public ElementUriKey getElementUriKey() {
		return key;
	}

	public void getAndUnmarshall() throws HttpException, IOException, JAXBException {
		// create handler and start it
		getRA().getExecutorService().execute(getRA().new AsyncGetAndUnmarshallElementHandler(key));
	}

	public void marshallAndPut(Object content) {		
		// create handler and start it
		getRA().getExecutorService().execute(getRA().new AsyncMarshallAndPutElementHandler(key,content));	
	}
	
	public void marshallAndPutIfMatch(String eTag, Object content) {
		// create handler and start it
		getRA().getExecutorService().execute(getRA().new AsyncMarshallAndPutElementIfMatchHandler(key,eTag,content));
	}
	
	public void marshallAndPutIfNoneMatch(String eTag, Object content) {
		// create handler and start it
		getRA().getExecutorService().execute(getRA().new AsyncMarshallAndPutElementIfNoneMatchHandler(key,eTag,content));
	}

	public void put(String content) {
		// create handler and start it
		getRA().getExecutorService().execute(getRA().new AsyncPutElementStringHandler(key,content));
	}
	
	public void putIfMatch(String eTag, String content) {
		// create handler and start it		
		getRA().getExecutorService().execute(getRA().new AsyncPutElementStringIfMatchHandler(key,eTag,content));
	}
	
	public void putIfNoneMatch(String eTag, String content) {
		// create handler and start it
		getRA().getExecutorService().execute(getRA().new AsyncPutElementStringIfNoneMatchHandler(key,eTag,content));
	}
	
	public void put(byte[] content) {
		// create handler and start it
		getRA().getExecutorService().execute(getRA().new AsyncPutElementByteArrayHandler(key,content));
	}
	
	public void putIfMatch(String eTag, byte[] content) {
		// create handler and start it
		getRA().getExecutorService().execute(getRA().new AsyncPutElementByteArrayIfMatchHandler(key,eTag,content));
	}
	
	public void putIfNoneMatch(String eTag, byte[] content) {
		// create handler and start it
		getRA().getExecutorService().execute(getRA().new AsyncPutElementByteArrayIfNoneMatchHandler(key,eTag,content));
	}

	public void delete() {
		// create handler and start it
		getRA().getExecutorService().execute(getRA().new AsyncDeleteElementHandler(key));
	}
	
	public void deleteIfMatch(String eTag) {
		// create handler and start it
		getRA().getExecutorService().execute(getRA().new AsyncDeleteElementIfMatchHandler(key,eTag));
		
	}
	
	public void deleteIfNoneMatch(String eTag)  {
		// create handler and start it
		getRA().getExecutorService().execute(getRA().new AsyncDeleteElementIfNoneMatchHandler(key,eTag));
	}

}
