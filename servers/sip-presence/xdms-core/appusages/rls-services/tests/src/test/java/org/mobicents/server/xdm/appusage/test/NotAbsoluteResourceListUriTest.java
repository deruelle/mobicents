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
import org.openxdm.xcap.common.error.UniquenessFailureConflictException;
import org.openxdm.xcap.common.key.UserDocumentUriKey;
import org.openxdm.xcap.common.xml.TextWriter;
import org.openxdm.xcap.common.xml.XMLValidator;
import org.openxdm.xcap.server.slee.appusage.rlsservices.RLSServicesAppUsage;

public class NotAbsoluteResourceListUriTest {
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(NotAbsoluteResourceListUriTest.class);
	}
	
	private String user = "sip:joe@example.com";
	private String documentName = "index";
	
	@Test
	public void test() throws HttpException, IOException, JAXBException, InterruptedException, TransformerException, NotWellFormedConflictException, NotUTF8ConflictException, InternalServerErrorException {
		
		XCAPClient client = new XCAPClientImpl("localhost",8080,"/mobicents");
		
		// create exception for return codes
		ConstraintFailureConflictException exception = new ConstraintFailureConflictException("junit test");
		
		// create uri		
		UserDocumentUriKey key = new UserDocumentUriKey(RLSServicesAppUsage.ID,user,documentName);
		
		// read document xml
		InputStream is = this.getClass().getResourceAsStream("example_bad_resource-list_uri_not_absolute.xml");
        String content = TextWriter.toString(XMLValidator.getWellFormedDocument(XMLValidator.getUTF8Reader(is)));
		is.close();
		
		// send put request and get response
		Response response = client.put(key,RLSServicesAppUsage.MIMETYPE,content,null);
		
		// check put response
		System.out.println("Response got:\n"+response);
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		client.shutdown();
	}
		
}
