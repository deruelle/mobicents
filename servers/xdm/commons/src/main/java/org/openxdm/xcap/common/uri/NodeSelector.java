package org.openxdm.xcap.common.uri;

import java.util.Map;

/**
 * A node selector selects an element, attribute or namespace bindings in a
 * element. It's defined by XCAP specs with the regular expression:
 * 
 * node-selector = element-selector ["/" terminal-selector]
 * 
 * @author Eduardo Martins
 */
public class NodeSelector {

	private String elementSelector = null;

	private String elementParentSelector = null;

	private String elementSelectorWithEmptyPrefix = null;

	private String elementParentSelectorWithEmptyPrefix = null;

	private String terminalSelector = null;

	/**
	 * Creates a new NodeSelector instance pointing to an element.
	 * 
	 * @param elementSelector
	 *            selects the element.
	 */
	public NodeSelector(String elementSelector) {
		this(elementSelector, null);
	}

	/**
	 * Creates a new NodeSelector instance pointing to an attribute or namespace
	 * bindings, from the specified terminal selector, in a element, selected by
	 * an element selector.
	 * 
	 * @param elementSelector
	 *            selects the element.
	 * @param terminalSelector
	 *            an attribute or namespace selector.
	 */
	public NodeSelector(String elementSelector, String terminalSelector) {
		this.elementSelector = elementSelector;
		this.terminalSelector = terminalSelector;
	}

	/**
	 * Retreives the element selector of the node selector.
	 * 
	 * @return
	 */
	public String getElementSelector() {
		return elementSelector;
	}

	/**
	 * Retrieves the terminal selector of the node selector.
	 */
	public String getTerminalSelector() {
		return terminalSelector;
	}

	/**
	 * Checks if the element selector has steps with unbinded namespaces
	 * prefixes, considering the provided map of namespace bindings.
	 * 
	 * @param namespaceBindings
	 *            the namespace bindings map.
	 * @return
	 */
	public boolean elementSelectorHasUnbindedPrefixes(
			Map<String, String> namespaceBindings) {
		String[] elementSelectorParts = elementSelector.split("/");
		for (int i = 0; i < elementSelectorParts.length; i++) {
			// get index of :
			int index = elementSelectorParts[i].indexOf(':');
			if (index >= 0) {
				String prefix = elementSelectorParts[i].substring(0, index);
				if (!namespaceBindings.containsKey(prefix)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Retrieves the element selector with empty prefixes instead of no prefix
	 * at all. This is required for XPtah queries.
	 * 
	 * @return
	 */
	public String getElementSelectorWithEmptyPrefix() {
		if (elementSelectorWithEmptyPrefix == null) {
			StringBuilder sb = new StringBuilder();
			String[] elementSelectorParts = elementSelector.split("/");
			// ignore the first part because its ""
			for (int i = 1; i < elementSelectorParts.length; i++) {
				if (elementSelectorParts[i].charAt(0) == '*') {
					// wildcard, just copy
					sb.append('/').append(elementSelectorParts[i]);
				} else if (elementSelectorParts[i].indexOf(':') > -1) {
					// it has at least one :, check if it's not inside an attr
					// value
					int pos = elementSelectorParts[i].indexOf('[');
					if (pos > 0 && elementSelectorParts[i].indexOf(':') > pos) {
						// insert empty prefix
						sb.append("/:").append(elementSelectorParts[i]);
					} else {
						// already has a prefix
						sb.append('/').append(elementSelectorParts[i]);
					}
				} else {
					// insert empty prefix
					sb.append("/:").append(elementSelectorParts[i]);
				}
			}
			elementSelectorWithEmptyPrefix = sb.toString();
		}
		return elementSelectorWithEmptyPrefix;
	}

	/**
	 * Retreives the element parent selector, that is, the parent of the element
	 * selected by the element selector.
	 * 
	 * @return
	 */
	public String getElementParentSelector() {
		if (elementParentSelector == null) {
			int elementParentSelectorSeparator = getElementSelectorWithEmptyPrefix()
					.lastIndexOf('/');
			if (elementParentSelectorSeparator > 0) {
				// parent is not root
				elementParentSelector = elementSelectorWithEmptyPrefix
						.substring(0, elementParentSelectorSeparator);
			} else {
				// parent is root
				elementParentSelector = "/";
			}
		}
		return elementParentSelector;
	}

	/**
	 * Retrieves the element parent selector with empty prefixes instead of no
	 * prefix at all. This is required for XPtah queries.
	 * 
	 * @return
	 */
	public String getElementParentSelectorWithEmptyPrefix() {
		if (elementParentSelectorWithEmptyPrefix == null) {
			String elementSelectorWithEmptyPrefix = getElementSelectorWithEmptyPrefix();
			int elementParentSelectorSeparator = elementSelectorWithEmptyPrefix
					.lastIndexOf('/');
			if (elementParentSelectorSeparator > 0) {
				// parent is not root
				elementParentSelectorWithEmptyPrefix = elementSelectorWithEmptyPrefix
						.substring(0, elementParentSelectorSeparator);
			} else {
				// parent is not root
				elementParentSelectorWithEmptyPrefix = "/";
			}
		}
		return elementParentSelectorWithEmptyPrefix;
	}
}
