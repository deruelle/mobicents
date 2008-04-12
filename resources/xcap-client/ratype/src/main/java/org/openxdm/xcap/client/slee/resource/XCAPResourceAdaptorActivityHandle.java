package org.openxdm.xcap.client.slee.resource;

import javax.slee.resource.ActivityHandle;

import org.openxdm.xcap.client.key.XcapUriKey;

/**
 * @author Eduardo Martins
 * @version 1.0
 * 
 */

public class XCAPResourceAdaptorActivityHandle implements ActivityHandle {
	
	private XcapUriKey key;
	
    public XCAPResourceAdaptorActivityHandle(XcapUriKey key){
        this.key = key;
    }
    
    public XcapUriKey getKey() {
		return key;
	}
    
    public boolean equals(Object o) {
    	if (o != null && o.getClass() == this.getClass()) {
			return ((XCAPResourceAdaptorActivityHandle)o).key.equals(this.key);
		}
		else {
			return false;
		}
    }    
    
    public String toString() {
        return new StringBuilder("Xcap Uri Key[").append(key.toString()).append("]").toString();
    }
    
    public int hashCode() {
    	return key.hashCode();
    }
}
