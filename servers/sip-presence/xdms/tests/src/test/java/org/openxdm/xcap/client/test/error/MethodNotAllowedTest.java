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
import org.openxdm.xcap.common.error.MethodNotAllowedException;
import org.openxdm.xcap.common.key.UserDocumentUriKey;
import org.openxdm.xcap.common.uri.ElementSelector;
import org.openxdm.xcap.common.uri.ElementSelectorStep;
import org.openxdm.xcap.server.slee.appusage.resourcelists.ResourceListsAppUsage;

public class MethodNotAllowedTest {
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(MethodNotAllowedTest.class);
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

		// create uri		
		UserDocumentUriKey key = new UserDocumentUriKey(appUsage.getAUID(),user,documentName);
		
		String content =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<resource-lists xmlns=\"urn:ietf:params:xml:ns:resource-lists\">" +
				"<list/>" +
			"</resource-lists>";
		
		// send put request and get response
		Response putResponse = client.put(key,appUsage.getMimetype(),content);
		
		// check put response
		assertTrue("Put response must exists",putResponse != null);
		assertTrue("Put response code should be 201",putResponse.getCode() == 201);
				
		// create element selector		
		LinkedList<ElementSelectorStep> elementSelectorSteps = new LinkedList<ElementSelectorStep>();
		ElementSelectorStep step1 = new ElementSelectorStep("pre:resource-lists");
		ElementSelectorStep step2 = new ElementSelectorStep("pre:list");
		elementSelectorSteps.add(step1);
		elementSelectorSteps.addLast(step2);
		// set namespace bindings to ask 
		Map<String,String> namespaces = new HashMap<String,String>();
		namespaces.put("pre",appUsage.getDefaultDocumentNamespace());
		// create namespace bindings uri in a fake document uri key
		MethodNotAllowedTestFakeUserDocumentUriKey nsKey = new MethodNotAllowedTestFakeUserDocumentUriKey(appUsage.getAUID(),user,documentName,new ElementSelector(elementSelectorSteps),namespaces);
				
		// send put request and get response
		Response nsPutResponse = client.put(nsKey,appUsage.getMimetype(),content);
		
		// check put response, must be method not allowed
		assertTrue("Put response must exists",nsPutResponse != null);
		assertTrue("Put response code should be "+MethodNotAllowedException.RESPONSE_STATUS,nsPutResponse.getCode() == MethodNotAllowedException.RESPONSE_STATUS);

		// send delete request and get response
		Response nsDeleteResponse = client.delete(nsKey);
		
		// check delete response, must be method not allowed
		assertTrue("Put response must exists",nsDeleteResponse != null);
		assertTrue("Put response code should be "+MethodNotAllowedException.RESPONSE_STATUS,nsDeleteResponse.getCode() == MethodNotAllowedException.RESPONSE_STATUS);
	}
		
}
