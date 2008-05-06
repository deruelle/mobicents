package org.openxdm.xcap.common.datasource;

import org.openxdm.xcap.common.error.InternalServerErrorException;

public interface Document {

	public String getAsString() throws InternalServerErrorException;
	public org.w3c.dom.Document getAsDOMDocument() throws InternalServerErrorException; 
	
	public String getETag();
	
}
