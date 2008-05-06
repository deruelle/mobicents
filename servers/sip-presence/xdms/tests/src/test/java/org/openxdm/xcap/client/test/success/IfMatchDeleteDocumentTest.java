package org.openxdm.xcap.client.test.success;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

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
import org.openxdm.xcap.common.key.UserDocumentUriKey;
import org.openxdm.xcap.server.slee.appusage.resourcelists.ResourceListsAppUsage;

public class IfMatchDeleteDocumentTest {
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(IfMatchDeleteDocumentTest.class);
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
				
		// send get request and get response
		Response initialGetResponse = client.get(key);
		
		// check get response
		assertTrue("Get response must exists",initialGetResponse != null);
		assertTrue("Get response code should be 200",initialGetResponse.getCode() == 200); 
				
		// send delete request and get response
		Response deleteResponse = client.deleteIfMatch(key,initialGetResponse.getETag());
		
		// check delete response
		assertTrue("Delete response must exists",deleteResponse != null);
		assertTrue("Delete response code should be 200",deleteResponse.getCode() == 200);
				
		// send get request and get response
		Response deleteGetResponse = client.get(key);
		
		// check get response
		assertTrue("Delete response must exists",deleteGetResponse != null);
		assertTrue("Delete response code should be 404",deleteGetResponse.getCode() == 404);
							
	}
			
}
