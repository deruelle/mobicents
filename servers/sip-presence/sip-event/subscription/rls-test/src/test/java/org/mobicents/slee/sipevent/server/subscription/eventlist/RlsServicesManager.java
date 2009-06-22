package org.mobicents.slee.sipevent.server.subscription.eventlist;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;

import org.openxdm.xcap.client.XCAPClient;
import org.openxdm.xcap.client.appusage.resourcelists.jaxb.EntryType;
import org.openxdm.xcap.client.appusage.resourcelists.jaxb.ListType;
import org.openxdm.xcap.client.appusage.resourcelists.jaxb.EntryType.DisplayName;
import org.openxdm.xcap.client.appusage.rlsservices.jaxb.ObjectFactory;
import org.openxdm.xcap.client.appusage.rlsservices.jaxb.PackagesType;
import org.openxdm.xcap.client.appusage.rlsservices.jaxb.RlsServices;
import org.openxdm.xcap.client.appusage.rlsservices.jaxb.ServiceType;
import org.openxdm.xcap.common.key.UserDocumentUriKey;
import org.openxdm.xcap.server.slee.appusage.rlsservices.RLSServicesAppUsage;

public class RlsServicesManager {

	private XCAPClient xCAPClient;
	private final ResourceListServerSipTest test;
	private final String serviceUri;
	private final String[] entryURIs;
	
	public RlsServicesManager(String serviceUri, String[] entryURIs, ResourceListServerSipTest test) {
		this.serviceUri = serviceUri;
		this.entryURIs = entryURIs;
		this.test = test;
		try {
			xCAPClient = ServerConfiguration.getXCAPClientInstance();
		} catch (InterruptedException e) {
			e.printStackTrace();
			test.failTest(e.getMessage());
		}
	}
	
	public void putRlsServices() {
		try {
			xCAPClient.put(new UserDocumentUriKey(RLSServicesAppUsage.ID,serviceUri,"index"), RLSServicesAppUsage.MIMETYPE, getRlsServices(entryURIs).getBytes("UTF-8"),null);
		} catch (Exception e) {
			e.printStackTrace();
			test.failTest(e.getMessage());
		}
	}
	
	public void deleteRlsServices() {
		try {
			xCAPClient.delete(new UserDocumentUriKey(RLSServicesAppUsage.ID,serviceUri,"index"),null);
		} catch (Exception e) {
			e.printStackTrace();
			test.failTest(e.getMessage());
		}
	}
	
	private EntryType createEntryType(String uri) {
		EntryType entryType = new EntryType();
		entryType.setUri(uri);
		DisplayName displayName = new EntryType.DisplayName();
		displayName.setValue(uri);
		entryType.setDisplayName(displayName);
		return entryType;
	}
	
	private String getRlsServices(String[] entryURIs) {
		StringWriter stringWriter = new StringWriter();
		try {			
			JAXBContext context = JAXBContext.newInstance("org.openxdm.xcap.client.appusage.rlsservices.jaxb");
			ListType listType = new ListType();
			for (String entryURI : entryURIs) {
				listType.getListOrExternalOrEntry().add(createEntryType(entryURI));
			}
			ServiceType serviceType = new ServiceType();
			serviceType.setList(listType);
			PackagesType packagesType = new PackagesType();
			packagesType.getPackageAndAny().add(new ObjectFactory().createPackagesTypePackage("presence"));
			serviceType.setPackages(packagesType);
			serviceType.setUri(serviceUri);
			RlsServices rlsServices = new RlsServices();
			rlsServices.getService().add(serviceType);
			context.createMarshaller().marshal(rlsServices, stringWriter);
			return stringWriter.toString();			
		} catch (Exception e) {
			e.printStackTrace();
			test.failTest(e.getMessage());
		}
		finally {		
			try {
				stringWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
				test.failTest(e.getMessage());
			}
		}
		return null;
	}
}
