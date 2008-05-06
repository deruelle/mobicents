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
import org.openxdm.xcap.common.error.PreconditionFailedException;
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

public class PreconditionFailedTest {
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(PreconditionFailedTest.class);
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

		
		// exception for response codes
		PreconditionFailedException exception = new PreconditionFailedException();				
	
		// create content for tests
		
		String documentContent =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<resource-lists xmlns=\"urn:ietf:params:xml:ns:resource-lists\">" +
				"<list/>" +
			"</resource-lists>";
				
		String attrContent = "enemies";		
				
		String elementContent = "<list xmlns=\"urn:ietf:params:xml:ns:resource-lists\" name='friends'/>";				
				
		// 1. replace document if match

		// create uri		
		UserDocumentUriKey documentKey = new UserDocumentUriKey(appUsage.getAUID(),user,documentName);		
		
		// send put request and get response
		Response response = client.put(documentKey,appUsage.getMimetype(),documentContent);		
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be 201",response.getCode() == 201);
				
		String eTag = response.getETag();
		
		// send put request and get response
		response = client.putIfMatch(documentKey,eTag+"z",appUsage.getMimetype(),documentContent);		
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// 2. replace document if none match
		
		// send put request and get response
		response = client.putIfNoneMatch(documentKey,eTag,appUsage.getMimetype(),documentContent);		
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());

		// 3. delete document if match

		// send delete request and get response
		response = client.deleteIfMatch(documentKey,eTag+"z");		
		// check delete response
		assertTrue("Delete response must exists",response != null);
		assertTrue("Delete response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// 4. delete document if none match

		// send delete request and get response
		response = client.deleteIfNoneMatch(documentKey,eTag);		
		// check delete response
		assertTrue("Delete response must exists",response != null);
		assertTrue("Delete response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// 5. put new element if match

		// create uri
		LinkedList<ElementSelectorStep> elementSelectorSteps = new LinkedList<ElementSelectorStep>();
		ElementSelectorStep step1 = new ElementSelectorStep("resource-lists");
		ElementSelectorStep step2 = new ElementSelectorStepByPos("list",2);								
		elementSelectorSteps.add(step1);
		elementSelectorSteps.addLast(step2);
		ElementSelector elementSelector = new ElementSelector(elementSelectorSteps);
		UserElementUriKey elementKey = new UserElementUriKey(appUsage.getAUID(),user,documentName,elementSelector,null);
		
		// send put request and get response
		response = client.putIfMatch(elementKey,ElementResource.MIMETYPE,eTag+"z",elementContent);				
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// 6. put new element if none match

		// send put request and get response
		response = client.putIfNoneMatch(elementKey,ElementResource.MIMETYPE,eTag,elementContent);				
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// 7. replace element if match

		// send put request and get response
		response = client.put(elementKey,ElementResource.MIMETYPE,elementContent);				
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be 201",response.getCode() == 201);
		
		eTag = response.getETag();
		
		// send put request and get response
		response = client.putIfMatch(elementKey,ElementResource.MIMETYPE,eTag+"z",elementContent);				
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// 8. replace element if none match

		// send put request and get response
		response = client.putIfNoneMatch(elementKey,ElementResource.MIMETYPE,eTag,elementContent);				
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// 9. delete element if match

		// send delete request and get response
		response = client.deleteIfMatch(elementKey,eTag+"z");		
		// check delete response
		assertTrue("Delete response must exists",response != null);
		assertTrue("Delete response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// 10. delete element if none match

		// send delete request and get response
		response = client.deleteIfNoneMatch(elementKey,eTag);		
		// check delete response
		assertTrue("Delete response must exists",response != null);
		assertTrue("Delete response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// 11. put new attr if match

		UserAttributeUriKey attrKey = new UserAttributeUriKey(appUsage.getAUID(),user,documentName,new ElementSelector(elementSelectorSteps),new AttributeSelector("name"),null);
				
		// send put request and get response
		response = client.putIfMatch(attrKey,AttributeResource.MIMETYPE,eTag+"z",attrContent);				
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
				
		// 12. put new attr if none match

		// send put request and get response
		response = client.putIfNoneMatch(attrKey,AttributeResource.MIMETYPE,eTag,attrContent);				
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// 13. replace attr if match

		// send put request and get response
		response = client.put(attrKey,AttributeResource.MIMETYPE,attrContent);				
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be 200",response.getCode() == 200);

		eTag = response.getETag();
		
		// send put request and get response
		response = client.putIfMatch(attrKey,AttributeResource.MIMETYPE,eTag+"z",attrContent);				
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
						
		// 14. replace attr if none match

		// send put request and get response
		response = client.putIfNoneMatch(attrKey,AttributeResource.MIMETYPE,eTag,attrContent);				
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());

		// 15. delete attr if match

		// send delete request and get response
		response = client.deleteIfMatch(attrKey,eTag+"z");		
		// check delete response
		assertTrue("Delete response must exists",response != null);
		assertTrue("Delete response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// 16. delete attr if none match
		
		// send delete request and get response
		response = client.deleteIfNoneMatch(attrKey,eTag);		
		// check delete response
		assertTrue("Delete response must exists",response != null);
		assertTrue("Delete response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
				
	}
	
}

