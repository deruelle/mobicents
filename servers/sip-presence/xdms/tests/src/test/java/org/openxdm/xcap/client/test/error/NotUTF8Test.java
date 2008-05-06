package org.openxdm.xcap.client.test.error;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
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
import org.openxdm.xcap.common.error.NotUTF8ConflictException;
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

public class NotUTF8Test {
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(NotUTF8Test.class);
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
		NotUTF8ConflictException exception = new NotUTF8ConflictException();
				
	
		// create content for tests
		
		String goodDocumentContent =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<resource-lists xmlns=\"urn:ietf:params:xml:ns:resource-lists\">" +
				"<list/>" +
			"</resource-lists>";
				
		String badDocumentContent1 =
			"<?xml version=\"1.0\" encoding=\"UTF-16\"?>" +
			"<resource-lists xmlns=\"urn:ietf:params:xml:ns:resource-lists\">" +
				"<list name=\"friends\"/>" +
			"</resource-lists>";
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		char notUTF8Char = 0xA9;
		
		String goodAttrContent = "enemies";		
		baos.write(goodAttrContent.getBytes("UTF8"));
		baos.write(notUTF8Char);
		byte[] badAttrContent = baos.toByteArray();
		
		String goodElementContent = "<list xmlns=\"urn:ietf:params:xml:ns:resource-lists\"/>";
		baos.reset();
		baos.write("<list/>".getBytes("UTF8"));
		baos.write(notUTF8Char);
		baos.write("/>".getBytes("UTF8"));
		byte[] badElementContent = baos.toByteArray();
		
		String badDocumentContent2 =
			"<?xml version=\"1.0\" encoding=\"UTF-16\"?>" +
			"<resource-lists xmlns=\"urn:ietf:params:xml:ns:resource-lists\">" +
				badElementContent +
			"</resource-lists>";
		
		baos.reset();
		baos.write(0xFE);
		baos.write(0xFF);
		String someDocumentContent3 = "<?xml version=\"1.0\" ?>" +
		"<resource-lists xmlns=\"urn:ietf:params:xml:ns:resource-lists\">" +
			"<list name=\"friends\"/>" +
		"</resource-lists>";
		baos.write(someDocumentContent3.getBytes("UTF8"));
		byte[] badDocumentContent3Bytes = baos.toByteArray();
		
		// 1. put new document
		
		// create uri		
		UserDocumentUriKey documentKey = new UserDocumentUriKey(appUsage.getAUID(),user,documentName);		
		
		// send put request and get response
		Response response = client.put(documentKey,appUsage.getMimetype(),badDocumentContent1);		
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus() && response.getContent().equals(exception.getResponseContent()));
		
		// send put request and get response
		response = client.put(documentKey,appUsage.getMimetype(),badDocumentContent2);		
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus() && response.getContent().equals(exception.getResponseContent()));
		
		// send put request and get response
		response = client.put(documentKey,appUsage.getMimetype(),badDocumentContent3Bytes);		
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus() && response.getContent().equals(exception.getResponseContent()));
		
		// 2. replace document
		
		// send put request and get response
		response = client.put(documentKey,appUsage.getMimetype(),goodDocumentContent);		
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be 201",response.getCode() == 201);
		
		// send put request and get response
		response = client.put(documentKey,appUsage.getMimetype(),badDocumentContent1);		
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus() && response.getContent().equals(exception.getResponseContent()));
		
		// send put request and get response
		response = client.put(documentKey,appUsage.getMimetype(),badDocumentContent2);		
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus() && response.getContent().equals(exception.getResponseContent()));
		
		// send put request and get response
		response = client.put(documentKey,appUsage.getMimetype(),badDocumentContent3Bytes);		
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus() && response.getContent().equals(exception.getResponseContent()));
				
		// 3. put new element
		
		// create uri
		LinkedList<ElementSelectorStep> elementSelectorSteps = new LinkedList<ElementSelectorStep>();
		ElementSelectorStep step1 = new ElementSelectorStep("resource-lists");
		ElementSelectorStep step2 = new ElementSelectorStepByPos("list",2);								
		elementSelectorSteps.add(step1);
		elementSelectorSteps.addLast(step2);
		ElementSelector elementSelector = new ElementSelector(elementSelectorSteps);
		UserElementUriKey elementKey = new UserElementUriKey(appUsage.getAUID(),user,documentName,elementSelector,null);
		
		// send put request and get response
		response = client.put(elementKey,ElementResource.MIMETYPE,badElementContent);				
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus() && response.getContent().equals(exception.getResponseContent()));
				
		// 4. replace element
		
		// send put request and get response
		response = client.put(elementKey,ElementResource.MIMETYPE,goodElementContent);				
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be 201",response.getCode() == 201);
		
		// send put request and get response
		response = client.put(elementKey,ElementResource.MIMETYPE,badElementContent);				
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus() && response.getContent().equals(exception.getResponseContent()));
		
		// 5. put new attr
		
		UserAttributeUriKey attrKey = new UserAttributeUriKey(appUsage.getAUID(),user,documentName,new ElementSelector(elementSelectorSteps),new AttributeSelector("name"),null);
				
		// send put request and get response
		response = client.put(attrKey,AttributeResource.MIMETYPE,badAttrContent);				
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus() && response.getContent().equals(exception.getResponseContent()));
				
		// 6. replace attr

		// send put request and get response
		response = client.put(attrKey,AttributeResource.MIMETYPE,goodAttrContent);				
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be 201",response.getCode() == 201);
		
		// send put request and get response
		response = client.put(attrKey,AttributeResource.MIMETYPE,badAttrContent);				
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus() && response.getContent().equals(exception.getResponseContent()));
				
	}
	
}

