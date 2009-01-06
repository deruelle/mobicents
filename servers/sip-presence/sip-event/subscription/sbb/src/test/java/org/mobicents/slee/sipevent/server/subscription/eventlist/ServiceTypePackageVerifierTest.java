package org.mobicents.slee.sipevent.server.subscription.eventlist;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Assert;
import org.junit.Test;
import org.openxdm.xcap.client.appusage.rlsservices.jaxb.RlsServices;

public class ServiceTypePackageVerifierTest {

	/**
	 * tests the building of a {@link RlsServicesCache}
	 * @throws JAXBException
	 * @throws IOException
	 */
	@Test
	public void test() throws JAXBException, IOException {

		// read rls service xml
		InputStream is = ServiceTypePackageVerifierTest.class
				.getResourceAsStream("rls-services-example.xml");
		JAXBContext context = JAXBContext.newInstance("org.openxdm.xcap.client.appusage.rlsservices.jaxb");
		Unmarshaller unmarshaller = context.createUnmarshaller();
		RlsServices rlsServices = (RlsServices) unmarshaller.unmarshal(is);		
		is.close();
		Assert
				.assertTrue(new ServiceTypePackageVerifier().hasPackage(rlsServices.getService().get(0), "presence"));
		
	}
}
