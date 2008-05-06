package org.openxdm.xcap.common.key;

import java.util.Map;

import org.openxdm.xcap.common.uri.DocumentSelector;
import org.openxdm.xcap.common.uri.ElementSelector;

public class GlobalElementUriKey extends ElementUriKey {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GlobalElementUriKey(String auid, String documentName,ElementSelector elementSelector,Map<String,String> namespaces) {
		super(new DocumentSelector(auid,"global",documentName),elementSelector,namespaces);		
	}
}
