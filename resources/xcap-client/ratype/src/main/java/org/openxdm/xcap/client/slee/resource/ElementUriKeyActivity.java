package org.openxdm.xcap.client.slee.resource;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.commons.httpclient.HttpException;
import org.openxdm.xcap.client.key.ElementUriKey;

public interface ElementUriKeyActivity extends XcapUriKeyActivity {

	
	public ElementUriKey getElementUriKey();
	
	
	public void getAndUnmarshall() throws HttpException, IOException, JAXBException;
	
	
	public void marshallAndPut(Object content);
	
	public void marshallAndPutIfMatch(String eTag,Object content);
	
	public void marshallAndPutIfNoneMatch(String eTag,Object content);
	
	
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
