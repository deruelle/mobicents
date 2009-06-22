package org.openxdm.xcap.client.test.error;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import junit.framework.JUnit4TestAdapter;

import org.apache.commons.httpclient.HttpException;
import org.junit.Test;
import org.openxdm.xcap.client.Response;
import org.openxdm.xcap.client.test.AbstractXDMJunitTest;
import org.openxdm.xcap.common.error.NotWellFormedConflictException;
import org.openxdm.xcap.common.key.UserDocumentUriKey;

public class NotWellFormedTest extends AbstractXDMJunitTest {
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(NotWellFormedTest.class);
	}
	
	@Test
	public void test() throws HttpException, IOException, JAXBException, InterruptedException {
		
		// exception for response codes
		NotWellFormedConflictException exception = new NotWellFormedConflictException();
		
		// create uri		
		UserDocumentUriKey key = new UserDocumentUriKey(appUsage.getAUID(),user,documentName);
		
		String goodDocumentContent =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<resource-lists xmlns=\"urn:ietf:params:xml:ns:resource-lists\">" +
				"<list/>" +
			"</resource-lists>";
		
		String badDocumentContent =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"not well formed";			
			
		// send put request and get response
		Response response = client.put(key,appUsage.getMimetype(),badDocumentContent,null);
		
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus() && response.getContent().equals(exception.getResponseContent()));		
		
		// send put request and get response
		response = client.put(key,appUsage.getMimetype(),goodDocumentContent,null);
		
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be 201",response.getCode() == 201);
		
		// send put request and get response
		response = client.put(key,appUsage.getMimetype(),badDocumentContent,null);
		
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus() && response.getContent().equals(exception.getResponseContent()));
		
		// clean up
		client.delete(key,null);
	}
			
}
