package org.mobicents.slee.sippresence.server.publication;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.mobicents.slee.sippresence.pojo.pidf.Presence;

public class PresenceCompositionPolicyTest {

	private static final JAXBContext jaxbContext = initJAXBContext();

	private static JAXBContext initJAXBContext() {
		try {
			return JAXBContext
					.newInstance("org.mobicents.slee.sippresence.pojo.pidf"
							+ ":org.mobicents.slee.sippresence.pojo.pidf.oma"
							+ ":org.mobicents.slee.sippresence.pojo.rpid"
							+ ":org.mobicents.slee.sippresence.pojo.datamodel"
							+ ":org.mobicents.slee.sippresence.pojo.commonschema");
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Test
	public void test() throws JAXBException, IOException {
		String pidf1 =   
			"<?xml version='1.0' encoding='UTF-8'?>" +
			"<presence xmlns='urn:ietf:params:xml:ns:pidf' xmlns:dm='urn:ietf:params:xml:ns:pidf:data-model' xmlns:rpid='urn:ietf:params:xml:ns:pidf:rpid' xmlns:c='urn:ietf:params:xml:ns:pidf:cipid' xmlns:op='urn:oma:xml:prs:pidf:oma-pres' entity='sip:user@example.com'>" +
				"<tuple id='t54bb0569'>" +
					"<status>" +
						"<basic>open</basic>" +
					"</status>" +
					"<contact>sip:my_name@example.com</contact>" +
					"<timestamp>2005-02-22T20:07:07Z</timestamp>" +
					"<op:service-description>" + 
						"<op:service-id>org.openmobilealliance:PoC-session</op:service-id>" + 
						"<op:version> 1.0 </op:version>" + 
						"<op:description>This is 1</op:description>"+
					"</op:service-description>"+
				"</tuple>" +
				"<dm:person id='p65f3307a'>" +
					"<rpid:activities><rpid:busy/></rpid:activities>" +
					"<dm:note>Busy</dm:note>" +
				"</dm:person>" +
				"<dm:device id='p65f3307b'>" +
					"<dm:deviceID>urn:uuid:d27459b7-8213-4395-aa77-ed859a3e5b3a</dm:deviceID>" +
					"<dm:timestamp>2005-02-22T20:07:07Z</dm:timestamp>" +
					"<dm:note>BlahBlah</dm:note>" +
				"</dm:device>" +
				"<dm:device id='p65f3307c'>" +
					"<dm:deviceID>urn:uuid:d27459b7-8213-4395-aa77-ed859a3e5b3b</dm:deviceID>" +
					"<dm:timestamp>2005-02-22T20:07:07Z</dm:timestamp>" +
					"<dm:note>BlahBlahBlah</dm:note>" +
					"<op:network-availability>" + 
					"<op:network id='IMS'>" + 
						"<op:active/>" + 
					"</op:network>" + 
				 "</op:network-availability>" +
				"</dm:device>" +
			"</presence>";
		
		String pidf2 = "<?xml version='1.0' encoding='UTF-8'?>" +
		"<presence xmlns='urn:ietf:params:xml:ns:pidf' xmlns:dm='urn:ietf:params:xml:ns:pidf:data-model' xmlns:rpid='urn:ietf:params:xml:ns:pidf:rpid' xmlns:c='urn:ietf:params:xml:ns:pidf:cipid' xmlns:op='urn:oma:xml:prs:pidf:oma-pres' entity='sip:user@example.com'>" +
			"<tuple id='t54bb05690'>" +
				"<status>" +
					"<basic>open</basic>" +
				"</status>" +
				"<contact>sip:my_name@example.com</contact>" +
				"<timestamp>2005-02-23T20:07:07Z</timestamp>" +
				"<op:service-description>" + 
					"<op:service-id>org.openmobilealliance:PoC-session</op:service-id>" + 
					"<op:version> 1.0 </op:version>" + 
					"<op:description>This is 2</op:description>"+
				"</op:service-description>"+
			"</tuple>" +
			"<dm:person id='p65f3307a'>" +
				"<rpid:activities><rpid:busy/></rpid:activities>" +
				"<dm:note>Busy</dm:note>" +
			"</dm:person>" +
			"<dm:device id='p65f3307b'>" +
				"<dm:deviceID>urn:uuid:d27459b7-8213-4395-aa77-ed859a3e5b3b</dm:deviceID>" +
				"<dm:timestamp>2005-02-23T20:07:07Z</dm:timestamp>" +
				"<dm:note>Zzz</dm:note>" +
				"<op:network-availability>" + 
					"<op:network id='IMS'>" + 
						"<op:terminated/>" + 
					"</op:network>" + 
				 "</op:network-availability>" +
			"</dm:device>" +
		"</presence>";
		
		StringReader sr = new StringReader(pidf1);
		Presence presence = ((JAXBElement<Presence>) jaxbContext.createUnmarshaller().unmarshal(sr)).getValue();
		sr.close();
		sr = new StringReader(pidf2);
		Presence otherPresence = ((JAXBElement<Presence>) jaxbContext.createUnmarshaller().unmarshal(sr)).getValue();
		sr.close();
		Presence compositionPresence = new PresenceCompositionPolicy().compose(presence, otherPresence);
		StringWriter sw = new StringWriter();
		jaxbContext.createMarshaller().marshal(compositionPresence, sw);
		System.out.println("Composed pidf:\n"+sw.toString());
		sw.close();
	}
}
