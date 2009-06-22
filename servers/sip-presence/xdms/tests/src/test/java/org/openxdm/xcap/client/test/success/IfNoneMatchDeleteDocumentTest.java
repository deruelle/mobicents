package org.openxdm.xcap.client.test.success;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import junit.framework.JUnit4TestAdapter;

import org.apache.commons.httpclient.HttpException;
import org.junit.Test;
import org.openxdm.xcap.client.Response;
import org.openxdm.xcap.client.test.AbstractXDMJunitTest;
import org.openxdm.xcap.common.key.UserDocumentUriKey;

public class IfNoneMatchDeleteDocumentTest extends AbstractXDMJunitTest  {
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(IfNoneMatchDeleteDocumentTest.class);
	}
	
	@Test
	public void test() throws HttpException, IOException, JAXBException, InterruptedException {
		
		// create uri		
		UserDocumentUriKey key = new UserDocumentUriKey(appUsage.getAUID(),user,documentName);
		
		String newContent =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<resource-lists xmlns=\"urn:ietf:params:xml:ns:resource-lists\">" +
				"<list/>" +
			"</resource-lists>";
		
		// send put request and get response
		Response initialPutResponse = client.put(key,appUsage.getMimetype(),newContent,null);
		
		// check put response
		assertTrue("Put response must exists",initialPutResponse != null);
		assertTrue("Put response code should be 201",initialPutResponse.getCode() == 201);
				
		// send get request and get response
		Response initialGetResponse = client.get(key,null);
		
		// check get response
		assertTrue("Get response must exists",initialGetResponse != null);
		assertTrue("Get response code should be 200",initialGetResponse.getCode() == 200);
		
		// send delete request and get response
		Response deleteResponse = client.deleteIfNoneMatch(key,"1",null);
		
		// check delete response
		assertTrue("Delete response must exists",deleteResponse != null);
		assertTrue("Delete response code should be 200",deleteResponse.getCode() == 200);
				
		// send get request and get response
		Response deleteGetResponse = client.get(key,null);
		
		// check get response
		assertTrue("Delete response must exists",deleteGetResponse != null);
		assertTrue("Delete response code should be 404",deleteGetResponse.getCode() == 404);
	
		// clean up
		client.delete(key,null);
	}
			
}
