package org.openxdm.xcap.common.key;

import java.util.Map;

import org.openxdm.xcap.common.uri.DocumentSelector;
import org.openxdm.xcap.common.uri.ElementSelector;

public class GlobalNamespaceBindingsUriKey extends NamespaceBindingsUriKey {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GlobalNamespaceBindingsUriKey(String auid,String documentName,ElementSelector elementSelector,Map<String,String> namespaces) {
		super(new DocumentSelector(auid,"global",documentName),elementSelector,namespaces);		
	}
}
