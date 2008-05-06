package org.openxdm.xcap.common.uri;

import java.util.Iterator;
import java.util.Map;

/**
 * 
 * A resource selector is the lowest level selector that points to a resource on
 * a XCAP server. It's built from a document selector string, relative to the
 * XCAP root on the server, an optional node selector string, and an opcional
 * map of namespace bindings.
 * 
 * Usage Examples:
 * 
 * 1) Create a resource selector pointing to 'resource-lists' document named
 * 'index', for user 'sip:eduardo@openxdm.org'
 * 
 * ResourceSelector resourceSelector = new ResourceSelector(
 * "resource-lists/users/user/sip:eduardo@openxdm.org/index");
 * 
 * 2) Create a resource selector pointing to the 'list' element, with the name
 * 'friends', for the 'resource-lists' document named 'index' of user
 * 'sip:eduardo@openxdm.org'
 * 
 * ResourceSelector resourceSelector = new ResourceSelector(
 * "resource-lists/users/user/sip:eduardo@openxdm.org/index",
 * "/resource-lists/list[name='friends']", null);
 * 
 * 3) Create a resource selector pointing to the 'pre:list' element, with the
 * name 'friends' and the element prefix 'pre' binded to namespace
 * 'urn:ietf:params:xml:ns:resource-lists' for the 'resource-lists' document
 * named 'index', for user 'sip:eduardo@openxdm.org'
 * 
 * Map<String,String> namespaceBindings = new HashMap<String,String>();
 * namespaceBindings.put("pre","urn:ietf:params:xml:ns:resource-lists");
 * 
 * ResourceSelector resourceSelector = new ResourceSelector(
 * "resource-lists/users/user/sip:eduardo@openxdm.org/index",
 * "/resource-lists/pre:list[name='friends']", namespaceBindings);
 * 
 * 
 * @author Eduardo Martins
 * 
 */

public class ResourceSelector {

	private String documentSelector;

	private String nodeSelector;

	private Map<String, String> namespaces;

	/**
	 * Creates a new instance of a resource selector, pointing to document
	 * resource.
	 * 
	 * @param documentSelector
	 *            selects the document resource.
	 */
	public ResourceSelector(String documentSelector) {
		this(documentSelector, null, null);
	}

	/**
	 * Creates a new instance of a resource selector, pointing to an element,
	 * attribute or namespace resource.
	 * 
	 * @param documentSelector
	 *            selects the document resource.
	 * @param nodeSelector
	 *            selects the element, attribute or namespace.
	 */
	public ResourceSelector(String documentSelector, String nodeSelector) {
		this(documentSelector, nodeSelector, null);
	}

	/**
	 * Creates a new instance of a resource selector, pointing to an element,
	 * attribute or namespace resource. At least one step in the node selector
	 * uses a namespace prefix, so a map of namespace bindings is provided.
	 * 
	 * @param documentSelector
	 *            selects the document resource.
	 * @param nodeSelector
	 *            selects the element, attribute or namespace.
	 * @param namespaces
	 *            defines namespace bindings for prefixe(s) found in the node
	 *            selector.
	 */
	public ResourceSelector(String documentSelector, String nodeSelector,
			Map<String, String> namespaces) {
		this.documentSelector = documentSelector;
		this.nodeSelector = nodeSelector;
		this.namespaces = namespaces;
	}

	/**
	 * Retreives the selector that points to the document, on the resource
	 * selector.
	 * 
	 * @return
	 */
	public String getDocumentSelector() {
		return documentSelector;
	}

	/**
	 * Retreives the namespace bindings map.
	 * 
	 * @return
	 */
	public Map<String, String> getNamespaces() {
		return namespaces;
	}

	/**
	 * Retreives the selector that points to the element, attribute or
	 * namespace, on the resource selector.
	 * 
	 * @return
	 */
	public String getNodeSelector() {
		return nodeSelector;
	}

	public String toString() {
		if (toString == null) {
			StringBuilder sb = new StringBuilder(documentSelector);
			if (nodeSelector != null) {
				sb.append("/~~").append(nodeSelector);
				if (namespaces != null && !namespaces.isEmpty()) {
					sb.append('?');
					for (Iterator<String> i = namespaces.keySet().iterator(); i
							.hasNext();) {
						String prefix = i.next();
						String namespace = namespaces.get(prefix);
						sb.append("xmlns(").append(prefix).append('=').append(
								namespace).append(')');
					}
				}
			}
			toString = sb.toString();
		}
		return toString;
	}

	private String toString = null;

}
