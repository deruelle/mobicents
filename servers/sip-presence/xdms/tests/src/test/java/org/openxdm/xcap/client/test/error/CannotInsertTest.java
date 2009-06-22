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
import org.openxdm.xcap.common.error.CannotInsertConflictException;
import org.openxdm.xcap.common.key.UserAttributeUriKey;
import org.openxdm.xcap.common.key.UserDocumentUriKey;
import org.openxdm.xcap.common.key.UserElementUriKey;
import org.openxdm.xcap.common.resource.AttributeResource;
import org.openxdm.xcap.common.resource.ElementResource;
import org.openxdm.xcap.common.uri.AttributeSelector;
import org.openxdm.xcap.common.uri.ElementSelector;
import org.openxdm.xcap.common.uri.ElementSelectorStep;
import org.openxdm.xcap.common.uri.ElementSelectorStepByAttr;
import org.openxdm.xcap.common.uri.ElementSelectorStepByPos;
import org.openxdm.xcap.common.uri.ElementSelectorStepByPosAttr;

public class CannotInsertTest extends AbstractXDMJunitTest {
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(CannotInsertTest.class);
	}
	
	@Test
	public void test() throws HttpException, IOException, JAXBException, InterruptedException {
			
		// create uri		
		UserDocumentUriKey key = new UserDocumentUriKey(appUsage.getAUID(),user,documentName);
		
		String content =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<resource-lists xmlns=\"urn:ietf:params:xml:ns:resource-lists\">" +
				"<list name=\"friends\" />" +					
			"</resource-lists>";
		
		// send put request and get response
		Response response = client.put(key,appUsage.getMimetype(),content,null);
		
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be 201",response.getCode() == 201);
				
		// create element selector		
		LinkedList<ElementSelectorStep> elementSelectorSteps = new LinkedList<ElementSelectorStep>();
		ElementSelectorStep step1 = new ElementSelectorStep("resource-lists");
		ElementSelectorStep step2 = new ElementSelectorStepByAttr("list","name","friends");
		elementSelectorSteps.add(step1);
		elementSelectorSteps.addLast(step2);		
		ElementSelector elementSelector = new ElementSelector(elementSelectorSteps);
		
		// create exception for return codes
		CannotInsertConflictException exception = new CannotInsertConflictException();
		
		// 1st test is for attr replace
		
		// create attr uri		
		AttributeSelector attributeSelector = new AttributeSelector("name"); 
		UserAttributeUriKey attrKey = new UserAttributeUriKey(appUsage.getAUID(),user,documentName,elementSelector,attributeSelector,null);
		// send put and get response
		Response putResponse = client.put(attrKey,AttributeResource.MIMETYPE,"enemies",null);
		assertTrue("Put response must exists",putResponse != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),putResponse.getCode() == exception.getResponseStatus() && putResponse.getContent().equals(exception.getResponseContent()));

		// 2nd is for elem replace
				
		// create elem uri
		UserElementUriKey elementKey = new UserElementUriKey(appUsage.getAUID(),user,documentName,elementSelector,null);
		
		// element content to put
		String elementContent =	"<list name=\"enemies\" />";								
		// send put request and get response
		putResponse = client.put(elementKey,ElementResource.MIMETYPE,elementContent,null);
		assertTrue("Put response must exists",putResponse != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),putResponse.getCode() == exception.getResponseStatus() && putResponse.getContent().equals(exception.getResponseContent()));
		
		// element content to put
		elementContent = "<cannot-insert name=\"friends\" />";								
		// send put request and get response
		putResponse = client.put(elementKey,ElementResource.MIMETYPE,elementContent,null);
		assertTrue("Put response must exists",putResponse != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),putResponse.getCode() == exception.getResponseStatus() && putResponse.getContent().equals(exception.getResponseContent()));

		// 3rd is for new elem put by pos and attr
		
		// create elem uri
		elementSelectorSteps.removeLast();
		step2 = new ElementSelectorStepByPosAttr("list",1,"name","3n3m13s");
		elementSelectorSteps.addLast(step2);
		elementKey = new UserElementUriKey(appUsage.getAUID(),user,documentName,elementSelector,null);
		
		// element content to put		
		elementContent = "<list name=\"enemies\" />";								
		// send put request and get response
		putResponse = client.put(elementKey,ElementResource.MIMETYPE,elementContent,null);
		assertTrue("Put response must exists",putResponse != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),putResponse.getCode() == exception.getResponseStatus() && putResponse.getContent().equals(exception.getResponseContent()));
		
		// element content to put
		elementContent =	"<cannot-insert name=\"3n3m13s\" />";								
		// send put request and get response
		putResponse = client.put(elementKey,ElementResource.MIMETYPE,elementContent,null);
		assertTrue("Put response must exists",putResponse != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),putResponse.getCode() == exception.getResponseStatus() && putResponse.getContent().equals(exception.getResponseContent()));
		
		// create elem uri
		elementSelectorSteps.removeLast();
		step2 = new ElementSelectorStepByPosAttr("list",3,"name","3n3m13s");
		elementSelectorSteps.addLast(step2);
		elementKey = new UserElementUriKey(appUsage.getAUID(),user,documentName,elementSelector,null);
		// element content to put		
		elementContent = "<list name=\"3n3m13s\" />";								
		// send put request and get response
		putResponse = client.put(elementKey,ElementResource.MIMETYPE,elementContent,null);
		assertTrue("Put response must exists",putResponse != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),putResponse.getCode() == exception.getResponseStatus() && putResponse.getContent().equals(exception.getResponseContent()));
		
		// 4th is for new elem put by pos
		
		// create elem uri
		elementSelectorSteps.removeLast();
		step2 = new ElementSelectorStepByPos("list",2);
		elementSelectorSteps.addLast(step2);
		elementKey = new UserElementUriKey(appUsage.getAUID(),user,documentName,elementSelector,null);
		
		// element content to put
		elementContent = "<cannot-insert name=\"3n3m13s\" />";								
		// send put request and get response
		putResponse = client.put(elementKey,ElementResource.MIMETYPE,elementContent,null);
		assertTrue("Put response must exists",putResponse != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),putResponse.getCode() == exception.getResponseStatus() && putResponse.getContent().equals(exception.getResponseContent()));
		
		// create elem uri
		elementSelectorSteps.removeLast();
		step2 = new ElementSelectorStepByPos("list",3);
		elementSelectorSteps.addLast(step2);
		elementKey = new UserElementUriKey(appUsage.getAUID(),user,documentName,elementSelector,null);
		// element content to put		
		elementContent = "<list name=\"3n3m13s\" />";								
		// send put request and get response
		putResponse = client.put(elementKey,ElementResource.MIMETYPE,elementContent,null);
		assertTrue("Put response must exists",putResponse != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),putResponse.getCode() == exception.getResponseStatus() && putResponse.getContent().equals(exception.getResponseContent()));
		
		// 5th is for new elem put by attr
		
		// create elem uri
		elementSelectorSteps.removeLast();
		step2 = new ElementSelectorStepByAttr("list","name","3n3m13s");
		elementSelectorSteps.addLast(step2);
		elementKey = new UserElementUriKey(appUsage.getAUID(),user,documentName,elementSelector,null);
		
		// element content to put		
		elementContent = "<list name=\"enemies\" />";								
		// send put request and get response
		putResponse = client.put(elementKey,ElementResource.MIMETYPE,elementContent,null);
		assertTrue("Put response must exists",putResponse != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),putResponse.getCode() == exception.getResponseStatus() && putResponse.getContent().equals(exception.getResponseContent()));
		
		// element content to put
		elementContent =	"<cannot-insert name=\"3n3m13s\" />";								
		// send put request and get response
		putResponse = client.put(elementKey,ElementResource.MIMETYPE,elementContent,null);
		assertTrue("Put response must exists",putResponse != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),putResponse.getCode() == exception.getResponseStatus() && putResponse.getContent().equals(exception.getResponseContent()));
		
		// 6th is for new elem put by name only
		
		// create elem uri
		elementSelectorSteps.removeLast();
		step2 = new ElementSelectorStep("list");
		elementSelectorSteps.addLast(step2);
		ElementSelectorStep step3 = new ElementSelectorStep("list");
		elementSelectorSteps.addLast(step3);
		elementKey = new UserElementUriKey(appUsage.getAUID(),user,documentName,elementSelector,null);
				
		// element content to put
		elementContent =	"<cannot-insert name=\"3n3m13s\" />";								
		// send put request and get response
		putResponse = client.put(elementKey,ElementResource.MIMETYPE,elementContent,null);
		assertTrue("Put response must exists",putResponse != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),putResponse.getCode() == exception.getResponseStatus() && putResponse.getContent().equals(exception.getResponseContent()));
		
		// clean up
		client.delete(key,null);
	}
		
}
