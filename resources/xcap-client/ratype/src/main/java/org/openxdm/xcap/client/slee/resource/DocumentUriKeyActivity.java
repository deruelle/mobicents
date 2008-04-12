package org.openxdm.xcap.client.slee.resource;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.commons.httpclient.HttpException;

import org.openxdm.xcap.client.key.DocumentUriKey;

public interface DocumentUriKeyActivity extends XcapUriKeyActivity {

	
	public DocumentUriKey getDocumentUriKey();
	
	
	public void getAndUnmarshall() throws HttpException, IOException, JAXBException;
	
	
	public void marshallAndPut(String mimetype, Object content);
	
	public void marshallAndPutIfMatch(String eTag, String mimetype, Object content);
	
	public void marshallAndPutIfNoneMatch(String eTag, String mimetype, Object content);
	
	
	public void put(String mimetype, String content);
	
	public void putIfMatch(String eTag, String mimetype, String content);
	
	public void putIfNoneMatch(String eTag, String mimetype, String content);
	
	public void put(String mimetype, byte[] content);
	
	public void putIfMatch(String eTag, String mimetype, byte[] content);
	
	public void putIfNoneMatch(String eTag, String mimetype, byte[] content);
	
	
	public void delete();
	
	public void deleteIfMatch(String eTag);
	
	public void deleteIfNoneMatch(String eTag);
	
}
