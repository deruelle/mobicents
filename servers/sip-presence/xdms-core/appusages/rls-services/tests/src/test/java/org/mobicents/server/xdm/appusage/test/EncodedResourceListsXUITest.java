package org.mobicents.server.xdm.appusage.test;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;

import junit.framework.JUnit4TestAdapter;

import org.apache.commons.httpclient.HttpException;
import org.junit.Test;
import org.openxdm.xcap.client.Response;
import org.openxdm.xcap.client.XCAPClient;
import org.openxdm.xcap.client.XCAPClientImpl;
import org.openxdm.xcap.common.error.ConstraintFailureConflictException;
import org.openxdm.xcap.common.error.InternalServerErrorException;
import org.openxdm.xcap.common.error.NotUTF8ConflictException;
import org.openxdm.xcap.common.error.NotWellFormedConflictException;
import org.openxdm.xcap.common.key.UserDocumentUriKey;
import org.openxdm.xcap.common.xml.TextWriter;
import org.openxdm.xcap.common.xml.XMLValidator;
import org.openxdm.xcap.server.slee.appusage.rlsservices.RLSServicesAppUsage;

public class EncodedResourceListsXUITest {
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(EncodedResourceListsXUITest.class);
	}
	
	private String user = "sip:vasily@ericsson.com";
	private String documentName = "index";
	
	@Test
	public void test() throws HttpException, IOException, JAXBException, InterruptedException, TransformerException, NotWellFormedConflictException, NotUTF8ConflictException, InternalServerErrorException {
		
		XCAPClient client = new XCAPClientImpl("127.0.0.1",8080,"/mobicents");
		
		// create uri		
		UserDocumentUriKey key = new UserDocumentUriKey(RLSServicesAppUsage.ID,user,documentName);
		
		// read document xml
		InputStream is = this.getClass().getResourceAsStream("example_encodedResourceListsXUI.xml");
        String content = TextWriter.toString(XMLValidator.getWellFormedDocument(XMLValidator.getUTF8Reader(is)));
		is.close();
		
		// send put request and get response
		Response response = client.put(key,RLSServicesAppUsage.MIMETYPE,content,null);
		
		// check put response
		System.out.println("Response got:\n"+response);
		assertTrue("Put response must exists and status code must be 200 or 201",response != null && (response.getCode() == 200 || response.getCode() == 201));
		
		client.delete(key,null);
		client.shutdown();
	}
		
}
