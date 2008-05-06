package org.openxdm.xcap.client.test.error;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.LinkedList;

import javax.xml.bind.JAXBException;

import junit.framework.JUnit4TestAdapter;

import org.apache.commons.httpclient.HttpException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openxdm.xcap.client.Response;
import org.openxdm.xcap.client.XCAPClient;
import org.openxdm.xcap.client.test.ServerConfiguration;
import org.openxdm.xcap.common.appusage.AppUsage;
import org.openxdm.xcap.common.datasource.DataSource;
import org.openxdm.xcap.common.error.UnsupportedMediaTypeException;
import org.openxdm.xcap.common.key.UserAttributeUriKey;
import org.openxdm.xcap.common.key.UserDocumentUriKey;
import org.openxdm.xcap.common.key.UserElementUriKey;
import org.openxdm.xcap.common.resource.AttributeResource;
import org.openxdm.xcap.common.uri.AttributeSelector;
import org.openxdm.xcap.common.uri.ElementSelector;
import org.openxdm.xcap.common.uri.ElementSelectorStep;
import org.openxdm.xcap.common.uri.ElementSelectorStepByAttr;
import org.openxdm.xcap.server.slee.appusage.resourcelists.ResourceListsAppUsage;

public class UnsupportedMediaTypeTest {

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(UnsupportedMediaTypeTest.class);
	}
	
	private XCAPClient client = null;
	private AppUsage appUsage = new ResourceListsAppUsage(null);
	private String user = "sip:bob@example.com";
	private String documentName = "index";
	
	@Before
	public void runBefore() throws IOException {
			
		// data source app usage & user provisioning
		try {
			// get data source
			DataSource dataSource = ServerConfiguration.getDataSourceInstance();
			dataSource.open();
			// ensure a new user in the app usage
			try {				
				dataSource.removeUser(appUsage.getAUID(),user);				
			}
			catch (Exception e) {
				System.out.println("Unable to remove user, maybe does not exist yet. Exception msg: "+e.getMessage());
			}
			dataSource.addUser(appUsage.getAUID(),user);
			dataSource.close();
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to complete test data source provisioning. Exception msg: "+e.getMessage());
		}
	}
	
	@After
	public void runAfter() throws IOException {
		if (client != null) {
			client.shutdown();
			client = null;
		}
		appUsage = null;
		user = null;
		documentName = null;
	}
	
	@Test
	public void test() throws HttpException, IOException, JAXBException, InterruptedException {
		
		client = ServerConfiguration.getXCAPClientInstance();

		
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
		Response response = client.put(documentKey,"badmimetype",documentContent);
				
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// send put request and get response
		response = client.put(documentKey,appUsage.getMimetype(),documentContent);
				
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
		response = client.put(evilKey,"badmimetype",elementContent);
				
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
		response = client.put(anotherEvilKey,"badmimetype","friends");
				
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// REPLACES
		
		// send put request and get response
		response = client.put(documentKey,"badmimetype",documentContent);
				
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// create uri key		
		elementKey = new UserElementUriKey(appUsage.getAUID(),user,documentName,elementSelector,null);
		
		// send put request and get response
		response = client.put(evilKey,"badmimetype",elementContent);
				
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// send put request and get response
		response = client.put(attributeKey,AttributeResource.MIMETYPE,"friends");
		
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be 201",response.getCode() == 201);
		
		// send put request and get response
		response = client.put(anotherEvilKey,"badmimetype","friends");
				
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
	}
		
}



