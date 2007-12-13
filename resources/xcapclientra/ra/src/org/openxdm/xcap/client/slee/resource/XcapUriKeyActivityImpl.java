package org.openxdm.xcap.client.slee.resource;

import org.openxdm.xcap.client.key.XcapUriKey;

public class XcapUriKeyActivityImpl implements XcapUriKeyActivity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private XcapUriKey key;
	
	private transient XCAPClientResourceAdaptor ra;
	
	public XcapUriKeyActivityImpl(XCAPClientResourceAdaptor ra, XcapUriKey key) {
		this.key = key;
		this.ra = ra;
	}
	
	public XCAPClientResourceAdaptor getRA() {
		return ra;
	}
	
	public XcapUriKey getXcapUriKey() {
		return key;
	}

	public void get() {
		// create handler and start it
		ra.getExecutorService().execute(ra.new AsyncGetHandler(key));		
	}

	public void endActivity() {		
		ra.endActivity(new XCAPResourceAdaptorActivityHandle(key));
	}
	
	public boolean equals(Object o) {
		if (o != null && o.getClass() == this.getClass()) {
			return ((XcapUriKeyActivityImpl)o).key.equals(this.key);
		}
		else {
			return false;
		}
	}
	
	public int hashCode() {
		return key.hashCode();
	}

}
