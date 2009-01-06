package org.mobicents.slee.sipevent.server.subscription.eventlist;

import javax.xml.bind.JAXBElement;

import org.openxdm.xcap.client.appusage.rlsservices.jaxb.ServiceType;

public class ServiceTypePackageVerifier {

	public boolean hasPackage(ServiceType serviceType, String eventPackage) {		
		for (Object obj : serviceType.getPackages().getPackageAndAny()) {
			JAXBElement element = (JAXBElement) obj;
			if (element.getName().getLocalPart().equals("package") && element.getValue().equals(eventPackage)) {
				return true;
			}
		}
		return false;
	}
	
}
