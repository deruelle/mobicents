package org.openxdm.xcap.common.key;

import java.util.Map;

import org.openxdm.xcap.common.uri.DocumentSelector;
import org.openxdm.xcap.common.uri.ElementSelector;
import org.openxdm.xcap.common.uri.ResourceSelector;

public class ElementUriKey extends XcapUriKey {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DocumentSelector documentSelector;
	private ElementSelector elementSelector;
	private Map<String,String> namespaces;
	
	public ElementUriKey(DocumentSelector documentSelector,ElementSelector elementSelector,Map<String,String> namespaces) {
		super(new ResourceSelector(documentSelector.toString(),KeyUtils.getPercentEncondedElementSelector(elementSelector),namespaces));
		this.documentSelector = documentSelector;
		this.elementSelector = elementSelector;
		this.namespaces = namespaces;
	}
	
	public DocumentSelector getDocumentSelector() {
		return documentSelector;
	}
	
	public ElementSelector getElementSelector() {
		return elementSelector;
	}
	
	public Map<String, String> getNamespaces() {
		return namespaces;
	}
	
}
