package org.openxdm.xcap.common.key;

import java.util.Map;

import org.openxdm.xcap.common.uri.AttributeSelector;
import org.openxdm.xcap.common.uri.DocumentSelector;
import org.openxdm.xcap.common.uri.ElementSelector;

public class GlobalAttributeUriKey extends AttributeUriKey {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GlobalAttributeUriKey(String auid, String documentName,ElementSelector elementSelector,AttributeSelector attributeSelector,Map<String,String> namespaces) {
		super(new DocumentSelector(auid,"global",documentName),elementSelector,attributeSelector,namespaces);
	}
}
