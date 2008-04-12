package org.openxdm.xcap.client.slee.resource;

import org.openxdm.xcap.client.key.AttributeUriKey;

public class AttributeUriKeyActivityImpl extends XcapUriKeyActivityImpl implements AttributeUriKeyActivity {

	private AttributeUriKey key;
	
	public AttributeUriKeyActivityImpl(XCAPClientResourceAdaptor ra, AttributeUriKey key) {
		super(ra, key);
		this.key = key;
	}

	public AttributeUriKey getAttributeUriKey() {
		return key;
	}

	public void put(String content) {
		// create handler and start it
		getRA().getExecutorService().execute(getRA().new AsyncPutAttributeStringHandler(key,content));
	}
	
	public void putIfMatch(String eTag, String content) {
		// create handler and start it		
		getRA().getExecutorService().execute(getRA().new AsyncPutAttributeStringIfMatchHandler(key,eTag,content));
	}
	
	public void putIfNoneMatch(String eTag, String content) {
		// create handler and start it
		getRA().getExecutorService().execute(getRA().new AsyncPutAttributeStringIfNoneMatchHandler(key,eTag,content));
	}
	
	public void put(byte[] content) {
		// create handler and start it
		getRA().getExecutorService().execute(getRA().new AsyncPutAttributeByteArrayHandler(key,content));
	}
	
	public void putIfMatch(String eTag, byte[] content) {
		// create handler and start it
		getRA().getExecutorService().execute(getRA().new AsyncPutAttributeByteArrayIfMatchHandler(key,eTag,content));
	}
	
	public void putIfNoneMatch(String eTag, byte[] content) {
		// create handler and start it
		getRA().getExecutorService().execute(getRA().new AsyncPutAttributeByteArrayIfNoneMatchHandler(key,eTag,content));
	}

	public void delete() {
		// create handler and start it
		getRA().getExecutorService().execute(getRA().new AsyncDeleteAttributeHandler(key));
	}
	
	public void deleteIfMatch(String eTag) {
		// create handler and start it
		getRA().getExecutorService().execute(getRA().new AsyncDeleteAttributeIfMatchHandler(key,eTag));
		
	}
	
	public void deleteIfNoneMatch(String eTag)  {
		// create handler and start it
		getRA().getExecutorService().execute(getRA().new AsyncDeleteAttributeIfNoneMatchHandler(key,eTag));
	}

}
