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
import org.openxdm.xcap.common.error.NotFoundException;
import org.openxdm.xcap.common.key.UserAttributeUriKey;
import org.openxdm.xcap.common.key.UserDocumentUriKey;
import org.openxdm.xcap.common.key.UserElementUriKey;
import org.openxdm.xcap.common.resource.ElementResource;
import org.openxdm.xcap.common.uri.AttributeSelector;
import org.openxdm.xcap.common.uri.ElementSelector;
import org.openxdm.xcap.common.uri.ElementSelectorStep;
import org.openxdm.xcap.common.uri.ElementSelectorStepByPos;

public class NotFoundTest extends AbstractXDMJunitTest {
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(NotFoundTest.class);
	}
	
	@Test
	public void test() throws HttpException, IOException, JAXBException, InterruptedException {
		
		// exception for response codes

		NotFoundException exception = new NotFoundException();				
	
		// create content for tests		
		
		String documentContent =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<resource-lists xmlns=\"urn:ietf:params:xml:ns:resource-lists\">" +
				"<list/>" +
			"</resource-lists>";						
		
		String elementContent = "<list xmlns=\"urn:ietf:params:xml:ns:resource-lists\"/>";				

		// create uri keys
		UserDocumentUriKey documentKey = new UserDocumentUriKey(appUsage.getAUID(),user,documentName);		
		UserDocumentUriKey fakeAppUsageDocumentKey = new UserDocumentUriKey("eduardomartinsappusage",user,documentName);

		LinkedList<ElementSelectorStep> elementSelectorSteps = new LinkedList<ElementSelectorStep>();
		ElementSelectorStep step1 = new ElementSelectorStep("resource-lists");
		ElementSelectorStep step2 = new ElementSelectorStepByPos("list",2);								
		elementSelectorSteps.add(step1);
		elementSelectorSteps.addLast(step2);
		ElementSelector elementSelector = new ElementSelector(elementSelectorSteps);
		
		UserElementUriKey elementParentKey = new UserElementUriKey(appUsage.getAUID(),user,documentName,elementSelector,null);
		
		UserAttributeUriKey attrWithElementKey = new UserAttributeUriKey(appUsage.getAUID(),user,documentName,elementSelector,new AttributeSelector("name"),null);
		UserAttributeUriKey fakeAppUsageAttrWithElementKey = new UserAttributeUriKey("eduardomartinsappusage",user,documentName,elementSelector,new AttributeSelector("name"),null);
		
		ElementSelectorStep step3 = new ElementSelectorStep("display-name");
		elementSelectorSteps.addLast(step3);
		elementSelector = new ElementSelector(elementSelectorSteps);
		
		UserElementUriKey elementKey = new UserElementUriKey(appUsage.getAUID(),user,documentName,elementSelector,null);
		UserElementUriKey fakeAppUsageElementKey = new UserElementUriKey("eduardomartinsappusage",user,documentName,elementSelector,null);
		
		UserAttributeUriKey attrWithoutElementKey = new UserAttributeUriKey(appUsage.getAUID(),user,documentName,elementSelector,new AttributeSelector("name"),null);
		
		// DOCUMENT PARENT NOT FOUND
		
		client.put(documentKey, appUsage.getMimetype(),documentContent,null);
		client.delete(documentKey,null);
		
		// 1. get document and document parent not found
		
		// send get request and get response
		Response response = client.get(fakeAppUsageDocumentKey,null);		
		// check get response
		assertTrue("Get response must exists",response != null);
		assertTrue("Get response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// 2. delete document and document parent not found
		
		// send delete request and get response
		response = client.delete(fakeAppUsageDocumentKey,null);		
		// check delete response
		assertTrue("Delete response must exists",response != null);
		assertTrue("Delete response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// 3. get element and document parent not found
		
		// send get request and get response
		response = client.get(fakeAppUsageElementKey,null);		
		// check get response
		assertTrue("Get response must exists",response != null);
		assertTrue("Get response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// 4. delete element and document parent not found
		
		// send delete request and get response
		response = client.delete(fakeAppUsageElementKey,null);		
		// check delete response
		assertTrue("Delete response must exists",response != null);
		assertTrue("Delete response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// 5. get attribute and document parent not found

		// send get request and get response
		response = client.get(fakeAppUsageAttrWithElementKey,null);		
		// check get response
		assertTrue("Get response must exists",response != null);
		assertTrue("Get response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// 6. delete attribute and document parent not found
		
		// send delete request and get response
		response = client.delete(fakeAppUsageAttrWithElementKey,null);		
		// check delete response
		assertTrue("Delete response must exists",response != null);
		assertTrue("Delete response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// DOCUMENT NOT FOUND
		
		// 7. get document and document not found
		
		// send get request and get response
		response = client.get(documentKey,null);		
		// check get response
		assertTrue("Get response must exists",response != null);
		assertTrue("Get response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// 8. delete document and document not found
		
		// send delete request and get response
		response = client.delete(documentKey,null);		
		// check delete response
		assertTrue("Delete response must exists",response != null);
		assertTrue("Delete response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// 9. get element and document not found

		// send get request and get response
		response = client.get(elementKey,null);		
		// check get response
		assertTrue("Get response must exists",response != null);
		assertTrue("Get response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// 10. delete element and document not found
		
		// send delete request and get response
		response = client.delete(elementKey,null);		
		// check delete response
		assertTrue("Delete response must exists",response != null);
		assertTrue("Delete response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// 11. get attribute and document not found

		// send get request and get response
		response = client.get(attrWithElementKey,null);		
		// check get response
		assertTrue("Get response must exists",response != null);
		assertTrue("Get response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// 12. delete attribute and document not found

		// send delete request and get response
		response = client.delete(attrWithElementKey,null);		
		// check delete response
		assertTrue("Delete response must exists",response != null);
		assertTrue("Delete response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// ELEMENT PARENT NOT FOUND
		
		// send put request and get response
		response = client.put(documentKey,appUsage.getMimetype(),documentContent,null);		
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be 201",response.getCode() == 201);
		
		// 13. get element and element parent not found

		// send get request and get response
		response = client.get(elementParentKey,null);		
		// check get response
		assertTrue("Get response must exists",response != null);
		assertTrue("Get response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// 14. get attribute and element parent not found
		
		// send get request and get response
		response = client.get(attrWithoutElementKey,null);		
		// check get response
		assertTrue("Get response must exists",response != null);
		assertTrue("Get response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// 15. delete element and element parent not found
		
		// send delete request and get response
		response = client.delete(elementParentKey,null);		
		// check delete response
		assertTrue("Delete response must exists",response != null);
		assertTrue("Delete response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// 16. delete attribute and element parent not found
		
		// send delete request and get response
		response = client.delete(attrWithoutElementKey,null);		
		// check delete response
		assertTrue("Delete response must exists",response != null);
		assertTrue("Delete response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// ELEMENT NOT FOUND
		
		// send put request and get response
		response = client.put(elementParentKey,ElementResource.MIMETYPE,elementContent,null);				
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be 201",response.getCode() == 201);
		
		// 17. get element and element not found
		
		// send get request and get response
		response = client.get(elementKey,null);		
		// check get response
		assertTrue("Get response must exists",response != null);
		assertTrue("Get response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// 18. delete element and element not found
		
		// send delete request and get response
		response = client.delete(elementKey,null);		
		// check delete response
		assertTrue("Delete response must exists",response != null);
		assertTrue("Delete response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// 19. get attribute and element not found
		
		// send get request and get response
		response = client.get(attrWithoutElementKey,null);		
		// check get response
		assertTrue("Get response must exists",response != null);
		assertTrue("Get response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// 20. delete attribute and element not found
		
		// send delete request and get response
		response = client.delete(attrWithoutElementKey,null);		
		// check delete response
		assertTrue("Delete response must exists",response != null);
		assertTrue("Delete response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// ATTRIBUTE NOT FOUND
				
		// 21. get attribute and attribute not found

		// send get request and get response
		response = client.get(attrWithElementKey,null);		
		// check get response
		assertTrue("Get response must exists",response != null);
		assertTrue("Get response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// 22. delete attribute and attribute not found

		// send delete request and get response
		response = client.delete(attrWithElementKey,null);		
		// check delete response
		assertTrue("Delete response must exists",response != null);
		assertTrue("Delete response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// clean up
		client.delete(documentKey,null);
	}
	
}

