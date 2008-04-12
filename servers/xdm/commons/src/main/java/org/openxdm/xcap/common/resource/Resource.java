package org.openxdm.xcap.common.resource;

import java.io.Serializable;

public interface Resource extends Serializable {

	public String getMimetype();
	
	public String toXML();
	
}
