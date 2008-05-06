package org.openxdm.xcap.common.uri;

import org.openxdm.xcap.common.xml.XMLValidator;

/**
 * 
 * An element selector step is part of the element selector. The simplest form of a step doesn't have element position or attribute information, and it's defined in XCAP specs by the regular expression:
 * 
 * step = by-name/by-pos/by-attr/by-pos-attr/extension-selector
 * by-name = NameorAny
 * NameorAny = QName / "*"
 * 
 * 
 * @author Eduardo Martins
 *
 */
public class ElementSelectorStep {

	private String name;

	/**
	 * Creates a new step by the element's name. If the provided name is not '*' or a valid QName, an IllegalArgumentException is thrown.
	 * @param name the element's name.
	 */
	public ElementSelectorStep(String name) {
		if (name.equals("*") || XMLValidator.isQName(name) ) {
			this.name = name;
		} 
		else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Retrieves the element's name of this step.
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves the prefix of this step element's name, if any.
	 * @return the prefix of this step element's name; null if it doesn't have a prefix.
	 */
	public String getPrefix() {
		// get index of :
		int i = name.indexOf(':');
		if (i < 0) {
			return null;
		} else {
			return name.substring(0, i);
		}
	}

	public String getNameWithoutPrefix() {
		// get index of :
		int i = name.indexOf(':');
		if (i < 0) {
			return name;
		} else {
			return name.substring(i+1);
		}
	}
	
	public String toString() {
		return name;
	}
}
