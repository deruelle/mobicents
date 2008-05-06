package org.openxdm.xcap.common.key;

import org.openxdm.xcap.common.uri.DocumentSelector;

public class GlobalDocumentUriKey extends DocumentUriKey {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GlobalDocumentUriKey(String auid, String documentName) {
		super(new DocumentSelector(auid,"global",documentName));
	}
}
