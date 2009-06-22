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

public class PutNewDocumentTest extends AbstractXDMJunitTest {
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(PutNewDocumentTest.class);
	}
	
	@Test
	public void test() throws HttpException, IOException, JAXBException, InterruptedException {
			
		// create uri		
		UserDocumentUriKey key = new UserDocumentUriKey(appUsage.getAUID(),user,documentName);
		
		String content =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<resource-lists xmlns=\"urn:ietf:params:xml:ns:resource-lists\">" +
				"<list/>" +
			"</resource-lists>";
		
		// send put request and get response
		Response putResponse = client.put(key,appUsage.getMimetype(),content,null);
		
		// check put response
		assertTrue("Put response must exists",putResponse != null);
		assertTrue("Put response code should be 201",putResponse.getCode() == 201);
				
		// send get request and get response
		Response getResponse = client.get(key,null);
		
		// check get response
		assertTrue("Get response must exists",getResponse != null);
		assertTrue("Get response code should be 200",getResponse.getCode() == 200); 
		assertTrue("Get response content must equals the one sent in put",XMLValidator.weaklyEquals(content,(String)getResponse.getContent()));
		
		// clean up
		client.delete(key,null);
	}
		
}
