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
import org.openxdm.xcap.common.error.UnsupportedMediaTypeException;
import org.openxdm.xcap.common.key.UserAttributeUriKey;
import org.openxdm.xcap.common.key.UserDocumentUriKey;
import org.openxdm.xcap.common.key.UserElementUriKey;
import org.openxdm.xcap.common.resource.AttributeResource;
import org.openxdm.xcap.common.uri.AttributeSelector;
import org.openxdm.xcap.common.uri.ElementSelector;
import org.openxdm.xcap.common.uri.ElementSelectorStep;
import org.openxdm.xcap.common.uri.ElementSelectorStepByAttr;

public class UnsupportedMediaTypeTest extends AbstractXDMJunitTest {

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(UnsupportedMediaTypeTest.class);
	}
	
	@Test
	public void test() throws HttpException, IOException, JAXBException, InterruptedException {
				
		// create exception for return codes
		UnsupportedMediaTypeException exception = new UnsupportedMediaTypeException();
			
		String documentContent =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<resource-lists xmlns=\"urn:ietf:params:xml:ns:resource-lists\">" +
				"<list/>" +					
			"</resource-lists>";
		
		// create uri key		
		UserDocumentUriKey documentKey = new UserDocumentUriKey(appUsage.getAUID(),user,documentName);
		
		// CREATES
		
		// send put request and get response
		Response response = client.put(documentKey,"badmimetype",documentContent,null);
				
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// send put request and get response
		response = client.put(documentKey,appUsage.getMimetype(),documentContent,null);
				
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be 201",response.getCode() == 201);
		
		String elementContent = "<list name=\"friends\"/>";					
			
		// create element selector		
		LinkedList<ElementSelectorStep> elementSelectorSteps = new LinkedList<ElementSelectorStep>();
		ElementSelectorStep step1 = new ElementSelectorStep("resource-lists");
		ElementSelectorStep step2 = new ElementSelectorStepByAttr("list","name","friends");
		elementSelectorSteps.add(step1);
		elementSelectorSteps.addLast(step2);		
		ElementSelector elementSelector = new ElementSelector(elementSelectorSteps);
		
		// create element uri key as a fake document uri key, no other way to send a bad mimetype		
		UserElementUriKey elementKey = new UserElementUriKey(appUsage.getAUID(),user,documentName,elementSelector,null);
		UnsupportedMediaTypeFakeDocumentUriKey evilKey = new UnsupportedMediaTypeFakeDocumentUriKey(elementKey);
		
		// send put request and get response		
		response = client.put(evilKey,"badmimetype",elementContent,null);
				
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		elementSelectorSteps.removeLast();
		ElementSelectorStep step3 = new ElementSelectorStep("list");
		elementSelectorSteps.add(step3);
		elementSelector = new ElementSelector(elementSelectorSteps);
		
		AttributeSelector attributeSelector = new AttributeSelector("name");
		
		// create uri key		
		UserAttributeUriKey attributeKey = new UserAttributeUriKey(appUsage.getAUID(),user,documentName,elementSelector,attributeSelector,null);
		AnotherUnsupportedMediaTypeFakeDocumentUriKey anotherEvilKey = new AnotherUnsupportedMediaTypeFakeDocumentUriKey(attributeKey);
		// send put request and get response
		response = client.put(anotherEvilKey,"badmimetype","friends",null);
				
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// REPLACES
		
		// send put request and get response
		response = client.put(documentKey,"badmimetype",documentContent,null);
				
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// create uri key		
		elementKey = new UserElementUriKey(appUsage.getAUID(),user,documentName,elementSelector,null);
		
		// send put request and get response
		response = client.put(evilKey,"badmimetype",elementContent,null);
				
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// send put request and get response
		response = client.put(attributeKey,AttributeResource.MIMETYPE,"friends",null);
		
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be 201",response.getCode() == 201);
		
		// send put request and get response
		response = client.put(anotherEvilKey,"badmimetype","friends",null);
				
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// clean up
		client.delete(documentKey,null);
	}
		
}



