package org.openxdm.xcap.client.test;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.openxdm.xcap.client.XCAPClient;
import org.openxdm.xcap.common.appusage.AppUsage;
import org.openxdm.xcap.server.slee.appusage.resourcelists.ResourceListsAppUsage;

public abstract class AbstractXDMJunitTest {

	protected XCAPClient client = null;
	protected AppUsage appUsage = new ResourceListsAppUsage(null);
	protected String user = "sip:eduardo@openxdm.org";
	protected String documentName = "index";
	
	@Before
	public void runBefore() throws IOException, InterruptedException {
		client = ServerConfiguration.getXCAPClientInstance();	
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
}
