package org.openxdm.xcap.common.key;

import java.io.Serializable;

import org.openxdm.xcap.common.uri.ResourceSelector;

/**
 * This key selects a resource on a XCAP server. It's built from a resource selector, the lowest level selector for a XCAP resource.
 * 
 * Note that the resource selector provided must take care of percent enconding chars that are not
 * allowed in a valid XCAP URI.
 * 
 * 
 * @author Eduardo Martins
 *
 */

public class XcapUriKey implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ResourceSelector resourceSelector;
		
	public XcapUriKey(ResourceSelector resourceSelector) {
		this.resourceSelector = resourceSelector;
	}
	
	public ResourceSelector getResourceSelector() {
		return resourceSelector;
	}

	public String toString() {
		if(toString==null){
			toString = resourceSelector.toString();			
		}
		return toString;
	}
	
	private String toString = null;

	public boolean equals(Object obj) {
        if (obj instanceof XcapUriKey)
        	return toString().equals(((XcapUriKey)obj).toString());
        else
        	return false;
    }    
        
    public int hashCode() {
    	return toString().hashCode();
    }
    
}

