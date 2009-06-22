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

public class NotUniqueListTest {
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(NotUniqueListTest.class);
	}
	
	private String user1 = "sip:joe1@example.com";
	private String user2 = "sip:joe2@example.com";
	private String documentName = "index";
	
	@Test
	public void test() throws HttpException, IOException, JAXBException, InterruptedException, TransformerException, NotWellFormedConflictException, NotUTF8ConflictException, InternalServerErrorException {
		
		XCAPClient client = new XCAPClientImpl("localhost",8080,"/mobicents");
		
		// create exception for return codes
		ConstraintFailureConflictException exception = new ConstraintFailureConflictException("junit test");
		
		// create uri		
		UserDocumentUriKey key1 = new UserDocumentUriKey(RLSServicesAppUsage.ID,user1,documentName);
		UserDocumentUriKey key2 = new UserDocumentUriKey(RLSServicesAppUsage.ID,user2,documentName);
		
		// read document xml
		InputStream is = this.getClass().getResourceAsStream("example_not_unique_list.xml");
        String content = TextWriter.toString(XMLValidator.getWellFormedDocument(XMLValidator.getUTF8Reader(is)));
		is.close();
		
		// send put request and get response
		Response response = client.put(key1,RLSServicesAppUsage.MIMETYPE,content,null);
		
		// check put response
		System.out.println("Response got:\n"+response);
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be 201",response.getCode() == 201);
		
		// now try to put same document for another user
		response = client.put(key2,RLSServicesAppUsage.MIMETYPE,content,null);
		
		// check put response
		System.out.println("Response got:\n"+response);
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// cleanup
		client.delete(key1,null);
		client.shutdown();
	}
		
}
