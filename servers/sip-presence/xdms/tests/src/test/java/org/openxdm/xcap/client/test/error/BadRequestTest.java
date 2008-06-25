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
import org.openxdm.xcap.common.error.BadRequestException;
import org.openxdm.xcap.common.key.UserAttributeUriKey;
import org.openxdm.xcap.common.key.UserDocumentUriKey;
import org.openxdm.xcap.common.resource.AttributeResource;
import org.openxdm.xcap.common.uri.AttributeSelector;
import org.openxdm.xcap.common.uri.ElementSelector;
import org.openxdm.xcap.common.uri.ElementSelectorStep;
import org.openxdm.xcap.common.uri.ElementSelectorStepByPos;
import org.openxdm.xcap.server.slee.appusage.resourcelists.ResourceListsAppUsage;

public class BadRequestTest {
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(BadRequestTest.class);
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
				"<list/>" +
				"<list name=\"enemies\"/>" +
			"</resource-lists>";
		
		// send put request and get response
		Response response = client.put(key,appUsage.getMimetype(),content);
		
		// check put response
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be 201",response.getCode() == 201);
				
		// create element selector		
		LinkedList<ElementSelectorStep> elementSelectorSteps = new LinkedList<ElementSelectorStep>();
		ElementSelectorStep step1 = new ElementSelectorStep("pre1:resource-lists");
		ElementSelectorStep step2 = new ElementSelectorStepByPos("pre2:list",1);
		elementSelectorSteps.add(step1);
		elementSelectorSteps.addLast(step2);
		// set namespace bindings 
		Map<String,String> namespaces = new HashMap<String,String>();
		namespaces.put("pre1",appUsage.getDefaultDocumentNamespace());
		
		// create attr put uri
		UserAttributeUriKey attrKey = new UserAttributeUriKey(appUsage.getAUID(),user,documentName,new ElementSelector(elementSelectorSteps),new AttributeSelector("name"),namespaces);
		// send put request and get response
		Response putResponse = client.put(attrKey,AttributeResource.MIMETYPE,"friends");
		// check put response, must be bad request
		assertTrue("Put response must exists",putResponse != null);
		assertTrue("Put response code should be "+BadRequestException.RESPONSE_STATUS,putResponse.getCode() == BadRequestException.RESPONSE_STATUS);

		// create attr delete & get uri
		elementSelectorSteps.removeLast();
		step2 = new ElementSelectorStepByPos("pre2:list",2);
		elementSelectorSteps.addLast(step2);
		UserAttributeUriKey anotherAttrKey = new UserAttributeUriKey(appUsage.getAUID(),user,documentName,new ElementSelector(elementSelectorSteps),new AttributeSelector("name"),null);
				
		// send get request and get response
		Response getResponse = client.get(anotherAttrKey);
		// check get response, must be bad request
		assertTrue("Delete response must exists",getResponse != null);
		assertTrue("Delete response code should be "+BadRequestException.RESPONSE_STATUS,getResponse.getCode() == BadRequestException.RESPONSE_STATUS);
		
		// send delete request and get response
		Response deleteResponse = client.delete(anotherAttrKey);
		// check put response, must be bad request
		assertTrue("Delete response must exists",deleteResponse != null);
		assertTrue("Delete response code should be "+BadRequestException.RESPONSE_STATUS,deleteResponse.getCode() == BadRequestException.RESPONSE_STATUS);		
	}	
	
	public static void main(String[] args) {
		System.out.println(ServerConfiguration.SERVER_HOST+ServerConfiguration.SERVER_PORT+ServerConfiguration.SERVER_XCAP_ROOT);
	}
}
