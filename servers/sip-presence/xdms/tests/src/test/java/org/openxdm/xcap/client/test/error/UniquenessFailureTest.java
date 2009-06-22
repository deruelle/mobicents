package org.openxdm.xcap.client.test.error;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import junit.framework.JUnit4TestAdapter;

import org.apache.commons.httpclient.HttpException;
import org.junit.Test;
import org.openxdm.xcap.client.Response;
import org.openxdm.xcap.client.test.AbstractXDMJunitTest;
import org.openxdm.xcap.common.error.UniquenessFailureConflictException;
import org.openxdm.xcap.common.key.UserDocumentUriKey;

public class UniquenessFailureTest extends AbstractXDMJunitTest {

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(UniquenessFailureTest.class);
	}
	
	@Test
	public void test() throws HttpException, IOException, JAXBException, InterruptedException {
				
		// create exception for return codes
		UniquenessFailureConflictException exception = new UniquenessFailureConflictException();
		
		// create uri		
		UserDocumentUriKey key = new UserDocumentUriKey(appUsage.getAUID(),user,documentName);
		
		String content =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<resource-lists xmlns=\"urn:ietf:params:xml:ns:resource-lists\">" +
				"<list name=\"friends\"/>" +
				"<list name=\"friends\"/>" +	
			"</resource-lists>";
		
		// send put request and get response
		Response response = client.put(key,appUsage.getMimetype(),content,null);
		
		// check put response
		assertTrue("Put response must exists",response != null);
		
		if (response.getCode() == 201) {
			client.delete(key,null);
		}
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus() && response.getContent().equals(exception.getResponseContent()));
		
		// TODO add dummy app usage with constraints also for DELETE
	}
		
}


