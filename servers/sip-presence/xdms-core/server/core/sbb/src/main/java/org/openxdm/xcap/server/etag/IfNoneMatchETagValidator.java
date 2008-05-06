package org.openxdm.xcap.server.etag;

import org.openxdm.xcap.common.error.PreconditionFailedException;

public class IfNoneMatchETagValidator implements ETagValidator {

	private String eTag;
	
	public IfNoneMatchETagValidator(String eTag) {		
		this.eTag = eTag;
	}
	
	public void validate(String documentETag) throws PreconditionFailedException {
		if(eTag != null) {
			if(eTag.compareTo("*") == 0) {
				// matches anything except null
				if (documentETag != null) {
					throw new PreconditionFailedException();
				}
			}
			else {
				// etags must not match
				if (documentETag != null && eTag.compareTo(documentETag) == 0) {
					throw new PreconditionFailedException();
				}
			}
		}
	}
	
}
