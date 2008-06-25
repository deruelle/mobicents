package org.openxdm.xcap.client.test.error;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.LinkedList;

import javax.xml.bind.JAXBException;

import junit.framework.JUnit4TestAdapter;

import org.apache.commons.httpclient.HttpException;
import org.junit.After;
import org.junit.Test;
import org.openxdm.xcap.client.Response;
import org.openxdm.xcap.client.XCAPClient;
import org.openxdm.xcap.client.test.ServerConfiguration;
import org.openxdm.xcap.common.appusage.AppUsage;
import org.openxdm.xcap.common.datasource.DataSource;
import org.openxdm.xcap.common.error.NoParentConflictException;
import org.openxdm.xcap.common.key.UserAttributeUriKey;
import org.openxdm.xcap.common.key.UserDocumentUriKey;
import org.openxdm.xcap.common.key.UserElementUriKey;
import org.openxdm.xcap.common.resource.AttributeResource;
import org.openxdm.xcap.common.resource.ElementResource;
import org.openxdm.xcap.common.uri.AttributeSelector;
import org.openxdm.xcap.common.uri.ElementSelector;
import org.openxdm.xcap.common.uri.ElementSelectorStep;
import org.openxdm.xcap.common.uri.ElementSelectorStepByPos;
import org.openxdm.xcap.server.slee.appusage.resourcelists.ResourceListsAppUsage;

public class NoParentTest {
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(NoParentTest.class);
	}
		
	private XCAPClient client = null;
	private AppUsage appUsage = new ResourceListsAppUsage(null);
	private String user = "sip:eduardo@openxdm.org";
	private String documentName = "index";
			
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
		ElementSelectorStep step3 = new ElementSelectorStep("display-name");
		elementSelectorSteps.add(step1);
		elementSelectorSteps.addLast(step2);		
		elementSelectorSteps.addLast(step3);
		ElementSelector elementSelector = new ElementSelector(elementSelectorSteps);
		UserElementUriKey elementKey = new UserElementUriKey(appUsage.getAUID(),user,documentName,elementSelector,null);
		UserElementUriKey fakeAppUsageElementKey = new UserElementUriKey("eduardomartinsappusage",user,documentName,elementSelector,null);
		
		UserAttributeUriKey attrKey = new UserAttributeUriKey(appUsage.getAUID(),user,documentName,elementSelector,new AttributeSelector("name"),null);
		UserAttributeUriKey fakeAttrKey = new UserAttributeUriKey("eduardomartinsappusage",user,documentName,elementSelector,new AttributeSelector("name"),null);
	
		// DOCUMENT PARENT NOT FOUND

		NoParentConflictException exception = new NoParentConflictException(ServerConfiguration.SERVER_XCAP_ROOT);				
		exception.setSchemeAndAuthorityURI("http://"+ServerConfiguration.SERVER_HOST+":"+ServerConfiguration.SERVER_PORT);
		
		// 1. put new document and document parent not found
		
		// send put request and get response
		Response response = client.put(fakeAppUsageDocumentKey,appUsage.getMimetype(),documentContent);		
		// check response
		assertTrue("Response must exists",response != null);
		assertTrue("Response content must be the expected one and response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus() && response.getContent().equals(exception.getResponseContent()));

		// 2. put new element and document parent not found
		
		// send put request and get response
		response = client.put(fakeAppUsageElementKey,ElementResource.MIMETYPE,elementContent);		
		// check response
		assertTrue("Response must exists",response != null);
		assertTrue("Response content must be the expected one and response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus() && response.getContent().equals(exception.getResponseContent()));

		// 3. put new attribute and document parent not found
		
		// send put request and get response
		response = client.put(fakeAttrKey,AttributeResource.MIMETYPE,"");		
		// check response
		assertTrue("Response must exists",response != null);
		assertTrue("Response content must be the expected one and response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus() && response.getContent().equals(exception.getResponseContent()));

		// DOCUMENT NOT FOUND
		
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

		exception = new NoParentConflictException(ServerConfiguration.SERVER_XCAP_ROOT+'/'+appUsage.getAUID()+"/users/"+user);				
		exception.setSchemeAndAuthorityURI("http://"+ServerConfiguration.SERVER_HOST+":"+ServerConfiguration.SERVER_PORT);
		
		// 4. put new element and document not found

		// send put request and get response
		response = client.put(elementKey,ElementResource.MIMETYPE,elementContent);		
		// check response
		assertTrue("Response must exists",response != null);
		assertTrue("Response content must be the expected one and response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus() && response.getContent().equals(exception.getResponseContent()));
		
		// 5. put new attribute and document not found

		// send put request and get response
		response = client.put(attrKey,AttributeResource.MIMETYPE,"");		
		// check response
		assertTrue("Response must exists",response != null);
		assertTrue("Response content must be the expected one and response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus() && response.getContent().equals(exception.getResponseContent()));
		
		// ELEMENT PARENT NOT FOUND
		
		// send put request and get response
		response = client.put(documentKey,appUsage.getMimetype(),documentContent);		
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be 201",response.getCode() == 201);

		exception = new NoParentConflictException(ServerConfiguration.SERVER_XCAP_ROOT+'/'+appUsage.getAUID()+"/users/"+user+"/"+documentName+"/~~/resource-lists");				
		exception.setSchemeAndAuthorityURI("http://"+ServerConfiguration.SERVER_HOST+":"+ServerConfiguration.SERVER_PORT);
		
		// 6. put new element and element parent not found

		// send put request and get response
		response = client.put(elementKey,ElementResource.MIMETYPE,elementContent);		
		// check response
		assertTrue("Response must exists",response != null);
		assertTrue("Response content must be the expected one and response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus() && response.getContent().equals(exception.getResponseContent()));
		
		// 7. put new attribute and element parent not found

		// send put request and get response
		response = client.put(attrKey,AttributeResource.MIMETYPE,"");		
		// check response
		assertTrue("Response must exists",response != null);
		assertTrue("Response content must be the expected one and response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus() && response.getContent().equals(exception.getResponseContent()));
								
	}
	
}





