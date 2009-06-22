package org.openxdm.xcap.client.test.error;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.LinkedList;

import javax.xml.bind.JAXBException;

import junit.framework.JUnit4TestAdapter;

import org.apache.commons.httpclient.HttpException;
import org.junit.Test;
import org.openxdm.xcap.client.Response;
import org.openxdm.xcap.client.test.AbstractXDMJunitTest;
import org.openxdm.xcap.common.error.NotValidXMLFragmentConflictException;
import org.openxdm.xcap.common.key.UserDocumentUriKey;
import org.openxdm.xcap.common.key.UserElementUriKey;
import org.openxdm.xcap.common.resource.ElementResource;
import org.openxdm.xcap.common.uri.ElementSelector;
import org.openxdm.xcap.common.uri.ElementSelectorStep;
import org.openxdm.xcap.common.uri.ElementSelectorStepByPos;

public class NotValidXMLFragmentTest extends AbstractXDMJunitTest {
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(NotValidXMLFragmentTest.class);
	}
	
	@Test
	public void test() throws HttpException, IOException, JAXBException, InterruptedException {
		
		// exception for response codes
		NotValidXMLFragmentConflictException exception = new NotValidXMLFragmentConflictException();
		
		// create uri		
		UserDocumentUriKey key = new UserDocumentUriKey(appUsage.getAUID(),user,documentName);
		
		String documentContent =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<resource-lists xmlns=\"urn:ietf:params:xml:ns:resource-lists\">" +
				"<list/>" +
			"</resource-lists>";
		
		String goodElementContent = "<list xmlns=\"urn:ietf:params:xml:ns:resource-lists\" name=\"friends\"/>";
		String badElementContent = "not valid xml fragment";
			
		// send put request and get response
		Response response = client.put(key,appUsage.getMimetype(),documentContent,null);
		
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be 201",response.getCode() == 201);
		
		// create uri
		LinkedList<ElementSelectorStep> elementSelectorSteps = new LinkedList<ElementSelectorStep>();
		ElementSelectorStep step1 = new ElementSelectorStep("resource-lists");
		ElementSelectorStep step2 = new ElementSelectorStepByPos("list",2);								
		elementSelectorSteps.add(step1);
		elementSelectorSteps.addLast(step2);
		ElementSelector elementSelector = new ElementSelector(elementSelectorSteps);
		UserElementUriKey elementKey = new UserElementUriKey(appUsage.getAUID(),user,documentName,elementSelector,null);
		
		// send put request and get response
		response = client.put(elementKey,ElementResource.MIMETYPE,badElementContent,null);
				
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus() && response.getContent().equals(exception.getResponseContent()));
		
		// send put request and get response
		response = client.put(elementKey,ElementResource.MIMETYPE,goodElementContent,null);
				
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be 201",response.getCode() == 201);
		
		// send put request and get response
		response = client.put(elementKey,ElementResource.MIMETYPE,badElementContent,null);
				
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus() && response.getContent().equals(exception.getResponseContent()));
		
		// clean up
		client.delete(key,null);
	}
			
}
