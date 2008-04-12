package org.openxdm.xcap.client.slee.resource;

import org.openxdm.xcap.client.key.AttributeUriKey;

public interface AttributeUriKeyActivity extends XcapUriKeyActivity {

	
	public AttributeUriKey getAttributeUriKey();
	
	
	public void put(String content);
	
	
	public void putIfMatch(String eTag, String content);
	
	public void putIfNoneMatch(String eTag, String content);
	
	public void put(byte[] content);
	
	public void putIfMatch(String eTag, byte[] content);
	
	public void putIfNoneMatch(String eTag, byte[] content);
	
	
	public void delete();
	
	public void deleteIfMatch(String eTag);
	
	public void deleteIfNoneMatch(String eTag);
	
}
