package org.openxdm.xcap.common.xml;

import java.util.Iterator;
import java.util.Map;

import javax.xml.*;

public class NamespaceContext implements javax.xml.namespace.NamespaceContext {

	private Map<String,String> namespaces;
	
	public NamespaceContext(Map<String,String> namespaces) {
		this.namespaces = namespaces;
	}
	
    public String getNamespaceURI(String prefix) {
        if (prefix == null) {
        	throw new IllegalArgumentException("Null prefix");
        }
        else {        	
        	String namespace = namespaces.get(prefix);        	
        	if (namespace == null) {
        		return XMLConstants.NULL_NS_URI;
        	} else {
        		return namespace;
        	}
        }        
    }

    public String getPrefix(String uri) {
        for(Iterator<String> i=namespaces.keySet().iterator();i.hasNext();) {
        	String prefix = i.next();
        	if ((namespaces.get(prefix)).equals(uri)) {
        		return prefix;
        	}
        }
        return null;
    }

    public Iterator<String> getPrefixes(String uri) {
        return namespaces.keySet().iterator();
    }

}