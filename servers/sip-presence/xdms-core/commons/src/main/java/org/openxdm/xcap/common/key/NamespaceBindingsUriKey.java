package org.openxdm.xcap.common.key;

import java.util.Map;

import org.openxdm.xcap.common.uri.DocumentSelector;
import org.openxdm.xcap.common.uri.ElementSelector;
import org.openxdm.xcap.common.uri.ResourceSelector;

public class NamespaceBindingsUriKey extends XcapUriKey {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DocumentSelector documentSelector;
	private ElementSelector elementSelector;
	private Map<String,String> namespaces;
	
	public NamespaceBindingsUriKey(DocumentSelector documentSelector,ElementSelector elementSelector,Map<String,String> namespaces) {
		super(new ResourceSelector(documentSelector.toString(),getNodeSelector(elementSelector),namespaces));
		this.documentSelector = documentSelector;
		this.elementSelector = elementSelector;
		this.namespaces = namespaces;
	}
	
	public NamespaceBindingsUriKey(DocumentSelector documentSelector,ElementSelector elementSelector) {
		this(documentSelector,elementSelector,null);
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
	
	private static String getNodeSelector(ElementSelector elementSelector) {
		return new StringBuilder(KeyUtils.getPercentEncondedElementSelector(elementSelector)).append("/namespace%3A%3A%2A").toString();
	}
		
}
