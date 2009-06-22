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
import org.openxdm.xcap.common.xml.XMLValidator;

public class IfNoneMatchReplaceExistingDocumentTest extends AbstractXDMJunitTest  {
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(IfNoneMatchReplaceExistingDocumentTest.class);
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
		
		String replaceContent =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<resource-lists xmlns=\"urn:ietf:params:xml:ns:resource-lists\">" +
				"<list name=\"friends\"/>" +
			"</resource-lists>";		
		
		// send put request and get response
		Response initialPutResponse = client.put(key,appUsage.getMimetype(),newContent,null);
		
		// check put response
		assertTrue("Put response must exists",initialPutResponse != null);
		assertTrue("Put response code should be 201",initialPutResponse.getCode() == 201);		
		
		// send put request and get response
		Response replacePutResponse = client.putIfNoneMatch(key,"1",appUsage.getMimetype(),replaceContent,null);
		
		// check put response
		assertTrue("Put response must exists",replacePutResponse != null);
		assertTrue("Put response code should be 200",replacePutResponse.getCode() == 200);
				
		// send get request and get response
		Response replaceGetResponse = client.get(key,null);
		
		// check get response
		assertTrue("Get response must exists",replaceGetResponse != null);
		assertTrue("Get response code should be 200",replaceGetResponse.getCode() == 200); 
		assertTrue("Get response content must equals the one sent in put",XMLValidator.weaklyEquals(replaceContent,(String)replaceGetResponse.getContent()));
		
		// clean up
		client.delete(key,null);
	}
			
}
