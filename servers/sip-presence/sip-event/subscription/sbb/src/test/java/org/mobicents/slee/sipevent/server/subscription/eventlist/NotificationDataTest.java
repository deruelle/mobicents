package org.mobicents.slee.sipevent.server.subscription.eventlist;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.mobicents.slee.sipevent.server.subscription.eventlist.FlatList;
import org.mobicents.slee.sipevent.server.subscription.eventlist.MultiPart;
import org.mobicents.slee.sipevent.server.subscription.eventlist.NotificationData;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription.Status;
import org.openxdm.xcap.client.appusage.resourcelists.jaxb.EntryType;
import org.openxdm.xcap.client.appusage.resourcelists.jaxb.EntryType.DisplayName;

public class NotificationDataTest {

	private EntryType createEntryType(String uri, String displayNameValue) {
		DisplayName displayName = new EntryType.DisplayName();
		displayName.setValue(displayNameValue);
		EntryType entryType = new EntryType();
		entryType.setDisplayName(displayName);
		entryType.setUri(uri);
		return entryType;
	}
	
	/**
	 * Tests if the string resulting from building an {@link MultiPart} has the expected value.
	 * @throws JAXBException 
	 * @throws IOException 
	 */
	@Test
	public void test() throws JAXBException, IOException {
		
		FlatList flatList = new FlatList(null);
		for (int i=0;i<2;i++) {
			flatList.putEntry(createEntryType("sip:entry"+i+"@example.com", "entry"+i));
		}
		
		String notifier = "sip:notifier@example.com";
		Subscription subscription = new Subscription();
		subscription.setNotifier(notifier);
		subscription.setStatus(Status.active);
		
		NotificationData notificationData = new NotificationData(subscription.getNotifierWithParams(),subscription.getVersion(),flatList,"rlmiCid","50UBfW7LSCVLtggUPe5z");
		MultiPart multiPart = null;
		for (int i=0;i<2;i++) {
			multiPart = notificationData.addNotificationData("sip:entry"+i+"@example.com", "cid"+i, "id"+i, "body"+i, "contentType"+i, "contentSubtype"+i, "active", null);			
		}
		
		String rlmi = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
		"<list fullState=\"true\" version=\"0\" uri=\""+notifier+"\" xmlns=\"urn:ietf:params:xml:ns:rlmi\">" +
		"<resource uri=\"sip:entry0@example.com\">"+
		"<name>entry0</name>"+
		"<instance id=\"id0\" state=\"active\" cid=\"cid0\"/>"+
		"</resource>"+
		"<resource uri=\"sip:entry1@example.com\">"+
		"<name>entry1</name>"+
		"<instance id=\"id1\" state=\"active\" cid=\"cid1\"/>"+
		"</resource>"+
		"</list>";
		StringReader stringReader = new StringReader(rlmi);
		Unmarshaller unmarshaller = NotificationData.rlmiJaxbContext.createUnmarshaller();
		Object rlmiUnmarshalled = unmarshaller.unmarshal(stringReader);
		stringReader.close();
		StringWriter stringWriter = new StringWriter();
		Marshaller marshaller = NotificationData.rlmiJaxbContext.createMarshaller();
		marshaller.marshal(rlmiUnmarshalled, stringWriter);
		rlmi = stringWriter.toString();
		stringWriter.close();
		
		String mp = 
			"--50UBfW7LSCVLtggUPe5z"
			+ "\n" + "Content-Transfer-Encoding: binary"
			+ "\n" + "Content-ID: <rlmiCid>"
			+ "\n" + "Content-Type: application/rlmi+xml;charset=\"UTF-8\""
			+ "\n" 
			+ "\n" + rlmi
			+ "\n" 
			+ "\n" + "--50UBfW7LSCVLtggUPe5z"
			+ "\n" + "Content-Transfer-Encoding: binary"
			+ "\n" + "Content-ID: <cid0>"
			+ "\n" + "Content-Type: contentType0/contentSubtype0;charset=\"UTF-8\""
			+ "\n" 
			+ "\n" + "body0"
			+ "\n" 
			+ "\n" + "--50UBfW7LSCVLtggUPe5z"
			+ "\n" + "Content-Transfer-Encoding: binary"
			+ "\n" + "Content-ID: <cid1>"
			+ "\n" + "Content-Type: contentType1/contentSubtype1;charset=\"UTF-8\""
			+ "\n"
			+ "\n" + "body1"
			+ "\n"
			+ "\n" + "--50UBfW7LSCVLtggUPe5z--";
		
		System.out.println("### Multipart object:\n"+multiPart);
		System.out.println("### Multipart string:\n"+mp);		
		assertEquals("multiPart.toString() is not the expected!", mp, multiPart.toString());
			
	}
}
