package org.openxdm.xcap.client.test.error;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

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
import org.openxdm.xcap.common.error.CannotDeleteConflictException;
import org.openxdm.xcap.common.key.UserDocumentUriKey;
import org.openxdm.xcap.common.key.UserElementUriKey;
import org.openxdm.xcap.common.uri.ElementSelector;
import org.openxdm.xcap.common.uri.ElementSelectorStep;
import org.openxdm.xcap.common.uri.ElementSelectorStepByPos;
import org.openxdm.xcap.common.uri.ElementSelectorStepByPosAttr;
import org.openxdm.xcap.server.slee.appusage.resourcelists.ResourceListsAppUsage;

public class CannotDeleteTest {
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(CannotDeleteTest.class);
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
		
		String content =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<resource-lists xmlns=\"urn:ietf:params:xml:ns:resource-lists\">" +
				"<list name=\"friends\">" +
					"<entry uri=\"sip:alice@example.com\"/>" +
					//"<entry-ref ref=\"resource-lists/users/sip%3Ababel%40example.com/index/~~/resource-lists/list\"/>" +
					"<list name=\"enemies\"/>" +
					"<external anchor=\"http://localhost:8888/xcap-root/resource-lists/users/sip%3Abill%40example.com/index/~~/resource-lists/list\"/>" +
					"<entry-ref ref=\"resource-lists/users/sip%3Ababel%40example.com/index/~~/resource-lists/list\"/>" +
					"<cannot-delete name=\"fake\" xmlns=\"extension\"/>" +
					"<cannot-delete name=\"fake\" xmlns=\"extension\"/>" +
					//"<entry uri=\"sip:alice@example.com\"/>" +
				"</list>" +
			"</resource-lists>";
		
		// send put request and get response
		Response response = client.put(key,appUsage.getMimetype(),content);
		
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be 201",response.getCode() == 201);
				
		// create element selector		
		LinkedList<ElementSelectorStep> elementSelectorSteps = new LinkedList<ElementSelectorStep>();
		ElementSelectorStep step1 = new ElementSelectorStep("resource-lists");
		ElementSelectorStep step2 = new ElementSelectorStep("list");
		elementSelectorSteps.add(step1);
		elementSelectorSteps.addLast(step2);		
		ElementSelector elementSelector = new ElementSelector(elementSelectorSteps);
		
		// create namespace bindings to be used in this test
		Map<String,String> nsBindings = new HashMap<String,String>();
		nsBindings.put("pre","extension");
				
		// create exception for return codes
		CannotDeleteConflictException exception = new CannotDeleteConflictException();
		
		// create elem uri		
		ElementSelectorStep step3 = new ElementSelectorStepByPos("*",5);
		elementSelectorSteps.addLast(step3);				
		UserElementUriKey elementKey = new UserElementUriKey(appUsage.getAUID(),user,documentName,elementSelector,nsBindings);
		// send delete and get response
		Response deleteResponse = client.delete(elementKey);
		assertTrue("Delete response must exists",deleteResponse != null);
		assertTrue("Delete response content must be the expected and the response code should be "+exception.getResponseStatus(),deleteResponse.getCode() == exception.getResponseStatus() && deleteResponse.getContent().equals(exception.getResponseContent()));

		// create elem uri		
		step3 = new ElementSelectorStepByPos("pre:cannot-delete",1);
		elementSelectorSteps.removeLast();
		elementSelectorSteps.addLast(step3);
		elementKey = new UserElementUriKey(appUsage.getAUID(),user,documentName,elementSelector,nsBindings);
		// send delete request and get response
		deleteResponse = client.delete(elementKey);
		assertTrue("Delete response must exists",deleteResponse != null);
		assertTrue("Delete response content must be the expected and the response code should be "+exception.getResponseStatus(),deleteResponse.getCode() == exception.getResponseStatus() && deleteResponse.getContent().equals(exception.getResponseContent()));

		//create elem uri		
		step3 = new ElementSelectorStepByPosAttr("*",5,"name","fake");
		elementSelectorSteps.removeLast();
		elementSelectorSteps.addLast(step3);		
		elementKey = new UserElementUriKey(appUsage.getAUID(),user,documentName,elementSelector,nsBindings);
		// send delete request and get response
		deleteResponse = client.delete(elementKey);
		assertTrue("Delete response must exists",deleteResponse != null);
		assertTrue("Delete response content must be the expected and the response code should be "+exception.getResponseStatus(),deleteResponse.getCode() == exception.getResponseStatus() && deleteResponse.getContent().equals(exception.getResponseContent()));		
		
		// send delete request and get response
		step3 = new ElementSelectorStepByPosAttr("pre:cannot-delete",1,"name","fake");
		elementSelectorSteps.removeLast();
		elementSelectorSteps.addLast(step3);
		elementKey = new UserElementUriKey(appUsage.getAUID(),user,documentName,elementSelector,nsBindings);
		deleteResponse = client.delete(elementKey);
		assertTrue("Delete response must exists",deleteResponse != null);
		assertTrue("Delete response content must be the expected and the response code should be "+exception.getResponseStatus(),deleteResponse.getCode() == exception.getResponseStatus() && deleteResponse.getContent().equals(exception.getResponseContent()));		
				
	}
		
}
