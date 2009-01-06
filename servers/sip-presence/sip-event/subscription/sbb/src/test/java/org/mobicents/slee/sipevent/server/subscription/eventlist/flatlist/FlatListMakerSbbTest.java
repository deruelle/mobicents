package org.mobicents.slee.sipevent.server.subscription.eventlist.flatlist;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.slee.NoSuchObjectLocalException;
import javax.slee.SLEEException;
import javax.slee.SbbLocalObject;
import javax.slee.TransactionRequiredLocalException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Assert;
import org.junit.Test;
import org.mobicents.slee.sipevent.server.subscription.FlatListMakerParentSbbLocalObject;
import org.mobicents.slee.sipevent.server.subscription.eventlist.FlatList;
import org.openxdm.xcap.client.appusage.resourcelists.jaxb.EntryType;
import org.openxdm.xcap.client.appusage.rlsservices.jaxb.RlsServices;

public class FlatListMakerSbbTest implements FlatListMakerParentSbbLocalObject {
	
	@Test
	public void test() throws JAXBException, IOException, InterruptedException {
		
		// read rls service xml
		InputStream is = FlatListMakerSbbTest.class
				.getResourceAsStream("rls-services1.xml");
		JAXBContext context = JAXBContext.newInstance("org.openxdm.xcap.client.appusage.rlsservices.jaxb");
		Unmarshaller unmarshaller = context.createUnmarshaller();
		RlsServices rlsServices = (RlsServices) unmarshaller.unmarshal(is);		
		is.close();
		// create flat maker and create list
		FlatListMaker flatListMaker = new FlatListMaker();
		flatListMaker.setSbbContext(new FlatMakerSbbContext(flatListMaker));
		flatListMaker.setParentSbbCMP(this);
		flatListMaker.makeFlatList(rlsServices.getService().get(0));
		
		is = FlatListMakerSbbTest.class
		.getResourceAsStream("rls-services2.xml");
		rlsServices = (RlsServices) unmarshaller.unmarshal(is);		
		is.close();
		flatListMaker.makeFlatList(rlsServices.getService().get(0));
		
	}

	public void flatListMade(FlatList flatList) {
		System.out.println("flatListMade(): status = "+flatList.getStatus());
		if (flatList.getStatus() != 200) {
			Assert.fail();	
		}
		for (Iterator<EntryType> it = flatList.getEntries().values().iterator();it.hasNext();) {
			System.out.println("Entry with uri "+it.next().getUri());
		}
	}

	public byte getSbbPriority() throws TransactionRequiredLocalException,
			NoSuchObjectLocalException, SLEEException {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isIdentical(SbbLocalObject arg0)
			throws TransactionRequiredLocalException, SLEEException {
		// TODO Auto-generated method stub
		return false;
	}

	public void remove() throws TransactionRequiredLocalException,
			NoSuchObjectLocalException, SLEEException {
		// TODO Auto-generated method stub
		
	}

	public void setSbbPriority(byte arg0)
			throws TransactionRequiredLocalException,
			NoSuchObjectLocalException, SLEEException {
		// TODO Auto-generated method stub
		
	}
}
