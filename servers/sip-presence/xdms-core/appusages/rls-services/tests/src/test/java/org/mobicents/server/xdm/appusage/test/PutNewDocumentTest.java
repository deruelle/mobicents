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
import org.openxdm.xcap.common.error.InternalServerErrorException;
import org.openxdm.xcap.common.error.NotUTF8ConflictException;
import org.openxdm.xcap.common.error.NotWellFormedConflictException;
import org.openxdm.xcap.common.key.GlobalDocumentUriKey;
import org.openxdm.xcap.common.key.UserDocumentUriKey;
import org.openxdm.xcap.common.xml.TextWriter;
import org.openxdm.xcap.common.xml.XMLValidator;
import org.openxdm.xcap.server.slee.appusage.rlsservices.RLSServicesAppUsage;

public class PutNewDocumentTest {
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(PutNewDocumentTest.class);
	}
	
	private String user = "sip:joe@example.com";
	private String documentName = "index";
	
	@Test
	public void test() throws HttpException, IOException, JAXBException, InterruptedException, TransformerException, NotWellFormedConflictException, NotUTF8ConflictException, InternalServerErrorException {
		
		XCAPClient client = new XCAPClientImpl("localhost",8080,"/mobicents");
		
		// create uri		
		UserDocumentUriKey key = new UserDocumentUriKey(RLSServicesAppUsage.ID,user,documentName);
		
		// read document xml
		InputStream is = this.getClass().getResourceAsStream("example.xml");
        String content = TextWriter.toString(XMLValidator.getWellFormedDocument(XMLValidator.getUTF8Reader(is)));
		is.close();
		
		// send put request and get response
		Response putResponse = client.put(key,RLSServicesAppUsage.MIMETYPE,content,null);
		
		// check put response
		System.out.println("Response got:\n"+putResponse);
		assertTrue("Put response must exists",putResponse != null);
		assertTrue("Put response code should be 201",putResponse.getCode() == 201);
				
		// send get request and get response
		Response getResponse = client.get(key,null);
		
		// check get response
		assertTrue("Get response must exists",getResponse != null);
		assertTrue("Get response code should be 200",getResponse.getCode() == 200); 
		ByteArrayInputStream bais = new ByteArrayInputStream(getResponse.getContent().getBytes("UTF-8"));
		String getResponseContent = TextWriter.toString(XMLValidator.getWellFormedDocument(XMLValidator.getUTF8Reader(bais)));
		bais.close();
		assertTrue("Get response content must equals the one sent in put",content.equals(getResponseContent));
		
		System.out.println("Global document after put:\n"+client.get(new GlobalDocumentUriKey(RLSServicesAppUsage.ID,documentName),null).getContent());
		// clean
		client.delete(key,null);
		System.out.println("Global document after delete:\n"+client.get(new GlobalDocumentUriKey(RLSServicesAppUsage.ID,documentName),null).getContent());
		client.shutdown();
	}
		
}
