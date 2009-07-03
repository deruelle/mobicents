package org.mobicents.slee.sipevent.server.subscription.eventlist.flatlist;

import java.io.IOException;
import java.io.InputStream;

import javax.slee.NoSuchObjectLocalException;
import javax.slee.SLEEException;
import javax.slee.SbbLocalObject;
import javax.slee.TransactionRequiredLocalException;

import org.mobicents.slee.xdm.server.XDMClientControlParentSbbLocalObject;
import org.mobicents.slee.xdm.server.XDMClientControlSbbLocalObject;
import org.openxdm.xcap.common.key.XcapUriKey;
import org.openxdm.xcap.common.uri.DocumentSelector;

public class XDMClientControl implements XDMClientControlSbbLocalObject {

	private XDMClientControlParentSbbLocalObject parentSbb;
	
	private int requestCounter = 0;
	
	public void delete(XcapUriKey key, String user) {
		// TODO Auto-generated method stub
		
	}

	public void deleteIfMatch(XcapUriKey key, String tag, String user) {
		// TODO Auto-generated method stub
		
	}

	public void deleteIfNoneMatch(XcapUriKey key, String tag, String user) {
		// TODO Auto-generated method stub
		
	}

	private String readFile(String fileName) {
		try {
			InputStream is = XDMClientControl.class
			.getResourceAsStream(fileName);
			byte[] b = new byte[is.available()];
			is.read(b);
			is.close();
			return new String(b);

		} catch (IOException e) {
			throw new RuntimeException("failed to read file "+fileName,e);
		}
	}
	
	public void get(XcapUriKey key, String user) {
		String content = null;
		if (requestCounter == 0) {
			requestCounter++;
			content = readFile("resource-list.xml");
		}
		else {
			content = readFile("entry-ref.xml");
		}
		
		parentSbb.getResponse(key, 200, "", content, "");
	}

	public void put(XcapUriKey key, String mimetype, byte[] content, String user) {
		// TODO Auto-generated method stub
		
	}

	public void putIfMatch(XcapUriKey key, String tag, String mimetype,
			byte[] content, String user) {
		// TODO Auto-generated method stub
		
	}

	public void putIfNoneMatch(XcapUriKey key, String tag, String mimetype,
			byte[] content, String user) {
		// TODO Auto-generated method stub
		
	}

	public void setParentSbb(XDMClientControlParentSbbLocalObject parentSbb) {
		this.parentSbb = parentSbb;
		
	}

	public void subscribeAppUsage(String auid) {
		// TODO Auto-generated method stub
		
	}

	public void subscribeDocument(DocumentSelector documentSelector) {
		// TODO Auto-generated method stub
		
	}

	public void unsubscribeAppUsage(String auid) {
		// TODO Auto-generated method stub
		
	}

	public void unsubscribeDocument(DocumentSelector documentSelector) {
		// TODO Auto-generated method stub
		
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
