package org.openxdm.xcap.common.key;

import org.openxdm.xcap.common.uri.DocumentSelector;
import org.openxdm.xcap.common.uri.ResourceSelector;

public class DocumentUriKey extends XcapUriKey {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DocumentSelector documentSelector;
		
	public DocumentUriKey(DocumentSelector documentSelector) {
		super(new ResourceSelector(documentSelector.toString(),null,null));
		this.documentSelector = documentSelector;		
	}	
	
	public DocumentSelector getDocumentSelector() {
		return documentSelector;
	}
	
}
