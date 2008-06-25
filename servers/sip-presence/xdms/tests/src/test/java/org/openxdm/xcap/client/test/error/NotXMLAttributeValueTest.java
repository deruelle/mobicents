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
import org.openxdm.xcap.common.error.NotXMLAttributeValueConflictException;
import org.openxdm.xcap.common.key.UserAttributeUriKey;
import org.openxdm.xcap.common.key.UserDocumentUriKey;
import org.openxdm.xcap.common.resource.AttributeResource;
import org.openxdm.xcap.common.uri.AttributeSelector;
import org.openxdm.xcap.common.uri.ElementSelector;
import org.openxdm.xcap.common.uri.ElementSelectorStep;
import org.openxdm.xcap.server.slee.appusage.resourcelists.ResourceListsAppUsage;

public class NotXMLAttributeValueTest {
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(NotXMLAttributeValueTest.class);
	}
	
	private XCAPClient client = null;
	private AppUsage appUsage = new ResourceListsAppUsage(null);
	private String user = "sip:eduardo@openxdm.org";
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
		// create uri		
		UserDocumentUriKey key = new UserDocumentUriKey(appUsage.getAUID(),user,documentName);
		
		String newContent =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<resource-lists xmlns=\"urn:ietf:params:xml:ns:resource-lists\">" +
				"<list/>" +
			"</resource-lists>";			
		
		// send put request and get response
		Response initialPutResponse = client.put(key,appUsage.getMimetype(),newContent);
		
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
		
		// create exception for return codes
		NotXMLAttributeValueConflictException exception = new NotXMLAttributeValueConflictException();
		
		// 2. put new attr
		
		// send put request and get response
		Response attrPutResponse = client.put(attrKey,AttributeResource.MIMETYPE,"<badattvalue");				
		// check put response
		assertTrue("Put response must exists",attrPutResponse != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),attrPutResponse.getCode() == exception.getResponseStatus() && attrPutResponse.getContent().equals(exception.getResponseContent()));
		
		// send put request and get response
		attrPutResponse = client.put(attrKey,AttributeResource.MIMETYPE,"'badattvalue");				
		// check put response
		assertTrue("Put response must exists",attrPutResponse != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),attrPutResponse.getCode() == exception.getResponseStatus() && attrPutResponse.getContent().equals(exception.getResponseContent()));
		
		// send put request and get response
		attrPutResponse = client.put(attrKey,AttributeResource.MIMETYPE,"\"badattvalue");				
		// check put response
		assertTrue("Put response must exists",attrPutResponse != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),attrPutResponse.getCode() == exception.getResponseStatus() && attrPutResponse.getContent().equals(exception.getResponseContent()));
		
		// 2. replace attr
		
		// send put request and get response
		attrPutResponse = client.put(attrKey,AttributeResource.MIMETYPE,"enemies");				
		
		// check put response
		assertTrue("Put response must exists",attrPutResponse != null);
		assertTrue("Put response code should be 201",attrPutResponse.getCode() == 201);
		
		// send put request and get response
		attrPutResponse = client.put(attrKey,AttributeResource.MIMETYPE,"<badattvalue");				
		// check put response
		assertTrue("Put response must exists",attrPutResponse != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),attrPutResponse.getCode() == exception.getResponseStatus() && attrPutResponse.getContent().equals(exception.getResponseContent()));
		
		// send put request and get response
		attrPutResponse = client.put(attrKey,AttributeResource.MIMETYPE,"'badattvalue");				
		// check put response
		assertTrue("Put response must exists",attrPutResponse != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),attrPutResponse.getCode() == exception.getResponseStatus() && attrPutResponse.getContent().equals(exception.getResponseContent()));
		
		// send put request and get response
		attrPutResponse = client.put(attrKey,AttributeResource.MIMETYPE,"\"badattvalue");				
		// check put response
		assertTrue("Put response must exists",attrPutResponse != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),attrPutResponse.getCode() == exception.getResponseStatus() && attrPutResponse.getContent().equals(exception.getResponseContent()));
		
		//TODO bad char and entity refs puts
		
	}
			
}
