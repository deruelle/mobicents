package org.openxdm.xcap.common.key;

import java.util.Map;

import org.openxdm.xcap.common.uri.AttributeSelector;
import org.openxdm.xcap.common.uri.DocumentSelector;
import org.openxdm.xcap.common.uri.ElementSelector;
import org.openxdm.xcap.common.uri.ResourceSelector;

public class AttributeUriKey extends XcapUriKey {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DocumentSelector documentSelector;
	private ElementSelector elementSelector;
	private AttributeSelector attributeSelector;
	private Map<String,String> namespaces;
	
	public AttributeUriKey(DocumentSelector documentSelector,ElementSelector elementSelector,AttributeSelector attributeSelector,Map<String,String> namespaces) {
		super(new ResourceSelector(documentSelector.toString(),getNodeSelector(elementSelector,attributeSelector),namespaces));
		this.documentSelector = documentSelector;
		this.elementSelector = elementSelector;
		this.namespaces = namespaces;
	}

	private static String getNodeSelector(ElementSelector elementSelector,AttributeSelector attributeSelector) {
		return new StringBuilder(KeyUtils.getPercentEncondedElementSelector(elementSelector)).append('/').append(KeyUtils.getPercentEncodedString(attributeSelector.toString())).toString();
	}

	public AttributeSelector getAttributeSelector() {
		return attributeSelector;
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
