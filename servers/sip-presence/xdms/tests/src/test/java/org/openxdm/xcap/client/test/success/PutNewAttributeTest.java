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
import org.openxdm.xcap.common.key.UserAttributeUriKey;
import org.openxdm.xcap.common.key.UserDocumentUriKey;
import org.openxdm.xcap.common.resource.AttributeResource;
import org.openxdm.xcap.common.uri.AttributeSelector;
import org.openxdm.xcap.common.uri.ElementSelector;
import org.openxdm.xcap.common.uri.ElementSelectorStep;
import org.openxdm.xcap.common.xml.XMLValidator;

public class PutNewAttributeTest extends AbstractXDMJunitTest {
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(PutNewAttributeTest.class);
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
		
		// create uri
		LinkedList<ElementSelectorStep> elementSelectorSteps = new LinkedList<ElementSelectorStep>();
		ElementSelectorStep step1 = new ElementSelectorStep("resource-lists");
		ElementSelectorStep step2 = new ElementSelectorStep("list");								
		elementSelectorSteps.add(step1);
		elementSelectorSteps.addLast(step2);
		UserAttributeUriKey attrKey = new UserAttributeUriKey(appUsage.getAUID(),user,documentName,new ElementSelector(elementSelectorSteps),new AttributeSelector("name"),null);
		
		// send put request and get response
		Response attrPutResponse = client.put(attrKey,AttributeResource.MIMETYPE,"friends",null);
		
		// check put response
		assertTrue("Put response must exists",attrPutResponse != null);
		assertTrue("Put response code should be 201",attrPutResponse.getCode() == 201);
				
		// send get request and get response
		Response attrGetResponse = client.get(attrKey,null);
		
		// check get response
		assertTrue("Get response must exists",attrGetResponse != null);
		assertTrue("Get response code should be 200 and the attr value must equals the one sent in put",attrGetResponse.getCode() == 200 && XMLValidator.weaklyEquals((String)attrGetResponse.getContent(),"friends"));
		
		// clean up
		client.delete(key,null);
	}
			
}
