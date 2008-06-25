package org.openxdm.xcap.client.test.subscription;


public class SubscribeAppUsageTest extends SubscribeDocumentTest {
	
	@Override
	protected String getContent() {
		return 	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
		"<resource-lists xmlns=\"urn:ietf:params:xml:ns:resource-lists\">" +
			"<list>" +
				"<entry uri=\""+getDocumentUriKey().getDocumentSelector().getAUID()+"\"/>" +
			"</list>" +
		"</resource-lists>";
	}
	
	
}
