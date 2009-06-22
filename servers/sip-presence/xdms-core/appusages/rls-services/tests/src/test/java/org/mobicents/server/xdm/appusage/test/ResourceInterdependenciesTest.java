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
import org.w3c.dom.Document;

public class ResourceInterdependenciesTest {
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(ResourceInterdependenciesTest.class);
	}
	
	private String user1 = "sip:john@example.com";
	private String user2 = "sip:doe@example.com";
	
	private String documentName = "index";
	
	@Test
	public void test() throws HttpException, IOException, JAXBException, InterruptedException, TransformerException, NotWellFormedConflictException, NotUTF8ConflictException, InternalServerErrorException {
		
		XCAPClient client = new XCAPClientImpl("localhost",8080,"/mobicents");
				
		// INSERT USER1 DOC
		// create uri		
		UserDocumentUriKey key1 = new UserDocumentUriKey(RLSServicesAppUsage.ID,user1,documentName);
		// read document xml
		InputStream is1 = this.getClass().getResourceAsStream("example_resource_interdependencies_1.xml");
		Document document1 = XMLValidator.getWellFormedDocument(XMLValidator.getUTF8Reader(is1));
        String content1 = TextWriter.toString(document1);
		is1.close();
		// send put request and get response
		Response putResponse1 = client.put(key1,RLSServicesAppUsage.MIMETYPE,content1,null);
		// check put response
		System.out.println("Response got:\n"+putResponse1);
		assertTrue("Put response must exists",putResponse1 != null);
		assertTrue("Put response code should be 201",putResponse1.getCode() == 201);
				
		// INSERT USER2 DOC
		// create uri		
		UserDocumentUriKey key2 = new UserDocumentUriKey(RLSServicesAppUsage.ID,user2,documentName);
		// read document xml
		InputStream is2 = this.getClass().getResourceAsStream("example_resource_interdependencies_2.xml");
		Document document2 = XMLValidator.getWellFormedDocument(XMLValidator.getUTF8Reader(is2));
        String content2 = TextWriter.toString(document2);
		is2.close();
		// send put request and get response
		Response putResponse2 = client.put(key2,RLSServicesAppUsage.MIMETYPE,content2,null);
		// check put response
		System.out.println("Response got:\n"+putResponse2);
		assertTrue("Put response must exists",putResponse2 != null);
		assertTrue("Put response code should be 201",putResponse2.getCode() == 201);
		
		// GET AND CHECK GLOBAL DOC
		GlobalDocumentUriKey key3 = new GlobalDocumentUriKey(RLSServicesAppUsage.ID,documentName);
		// read document xml
		InputStream is3 = this.getClass().getResourceAsStream("example_resource_interdependencies_3.xml");
		Document document3 = XMLValidator.getWellFormedDocument(XMLValidator.getUTF8Reader(is3));
        String content3 = TextWriter.toString(document3);
		is3.close();
		Response getResponse = client.get(key3,null);
		// check get response
		System.out.println("Response got:\n"+getResponse);
		assertTrue("Get response must exists",getResponse != null);
		assertTrue("Get response code should be 200",getResponse.getCode() == 200); 
		ByteArrayInputStream bais = new ByteArrayInputStream(getResponse.getContent().getBytes("UTF-8"));
		String getResponseContent = TextWriter.toString(XMLValidator.getWellFormedDocument(XMLValidator.getUTF8Reader(bais)));
		bais.close();
		assertTrue("Get response content must equals the one sent in put. \n-------- Content --------\n"+getResponseContent+"\n--------\n-------- Expected --------\n"+content3+"\n--------",XMLValidator.weaklyEquals(content3,getResponseContent));
		
		// REMOVE USER1 DOC AND CHECK GLOBAL AGAIN
		client.delete(key1,null);
		getResponse = client.get(key3,null);
		// check get response
		System.out.println("Response got:\n"+getResponse);
		assertTrue("Get response must exists",getResponse != null);
		assertTrue("Get response code should be 200",getResponse.getCode() == 200); 
		bais = new ByteArrayInputStream(getResponse.getContent().getBytes("UTF-8"));
		getResponseContent = TextWriter.toString(XMLValidator.getWellFormedDocument(XMLValidator.getUTF8Reader(bais)));
		bais.close();
		assertTrue("Get response content must equals the one sent in put. \n-------- Content --------\n"+getResponseContent+"\n--------\n-------- Expected --------\n"+content2+"\n--------",XMLValidator.weaklyEquals(content2,getResponseContent));
		
		// clean
		client.delete(key2,null);
		client.shutdown();
	}
		
}
