package org.openxdm.xcap.server.etag;

import org.openxdm.xcap.common.error.PreconditionFailedException;

public interface ETagValidator {

	public void validate(String documentETag) throws PreconditionFailedException;
}
