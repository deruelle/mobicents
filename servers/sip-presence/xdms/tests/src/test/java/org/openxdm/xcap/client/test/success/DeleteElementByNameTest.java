package org.openxdm.xcap.client.test.success;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.LinkedList;

import javax.xml.bind.JAXBException;

import junit.framework.JUnit4TestAdapter;

import org.apache.commons.httpclient.HttpException;
import org.junit.Test;
import org.openxdm.xcap.client.Response;
import org.openxdm.xcap.client.test.AbstractXDMJunitTest;
import org.openxdm.xcap.client.test.ServerConfiguration;
import org.openxdm.xcap.common.key.UserDocumentUriKey;
import org.openxdm.xcap.common.key.UserElementUriKey;
import org.openxdm.xcap.common.uri.ElementSelector;
import org.openxdm.xcap.common.uri.ElementSelectorStep;

public class DeleteElementByNameTest extends AbstractXDMJunitTest {
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(DeleteElementByNameTest.class);
	}
	
	@Test
	public void test() throws HttpException, IOException, JAXBException, InterruptedException {
		
		// create uri		
		UserDocumentUriKey key = new UserDocumentUriKey(appUsage.getAUID(),user,documentName);
		
		// the doc to put
		String document =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<resource-lists xmlns=\"urn:ietf:params:xml:ns:resource-lists\">" +
				"<list name=\"friends\">" +
					"<display-name>" +
					"</display-name>" +
				"</list>" +
			"</resource-lists>";			
		
		// send put request and get response
		Response initialPutResponse = client.put(key,appUsage.getMimetype(),document,null);
		
		// check put response
		assertTrue("Put response must exists",initialPutResponse != null);
		assertTrue("Put response code should be 201",initialPutResponse.getCode() == 201);		
		
		// create uri
		LinkedList<ElementSelectorStep> elementSelectorSteps = new LinkedList<ElementSelectorStep>();
		ElementSelectorStep step1 = new ElementSelectorStep("resource-lists");
		ElementSelectorStep step2 = new ElementSelectorStep("list");
		ElementSelectorStep step3 = new ElementSelectorStep("display-name");
		elementSelectorSteps.add(step1);
		elementSelectorSteps.addLast(step2);
		elementSelectorSteps.addLast(step3);
		UserElementUriKey elemKey = new UserElementUriKey(appUsage.getAUID(),user,documentName,new ElementSelector(elementSelectorSteps),null);
		
		// send delete request and get response
		Response elemDeleteResponse = client.delete(elemKey,null);
		
		// check delete response
		assertTrue("Delete response must exists",elemDeleteResponse != null);
		assertTrue("Delete response code should be 200",elemDeleteResponse.getCode() == 200);
				
		// send get request and get response
		Response elemGetResponse = client.get(elemKey,null);
		
		// check get response
		assertTrue("Get response must exists",elemGetResponse != null);
		assertTrue("Get response code should be 404",elemGetResponse.getCode() == 404);
		
		// clean up
		client.delete(key,null);
	}
			
}
