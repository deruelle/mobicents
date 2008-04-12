package org.openxdm.xcap.client.slee.resource;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.commons.httpclient.HttpException;
import org.openxdm.xcap.client.key.NamespaceBindingsUriKey;

public class NamespaceBindingsUriKeyActivityImpl extends XcapUriKeyActivityImpl implements NamespaceBindingsUriKeyActivity {

	NamespaceBindingsUriKey key;
	
	public NamespaceBindingsUriKeyActivityImpl(XCAPClientResourceAdaptor ra, NamespaceBindingsUriKey key) {
		super(ra, key);
		this.key = key;
	}

	public NamespaceBindingsUriKey getNamespaceBindingsUriKey() {
		return key;
	}	

}
