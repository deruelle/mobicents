package org.openxdm.xcap.common.uri;

import org.openxdm.xcap.common.xml.XMLValidator;

/**
 * 
 *  attribute-selector     = "@" att-name
 *  att-name               = QName
 * @author Eduardo Martins
 *
 */

public class AttributeSelector implements TerminalSelector {

	private String attName;
	
	public AttributeSelector(String attName) {
		if (XMLValidator.isQName(attName)) {
			this.attName = attName;
		}
		else {
			throw new IllegalArgumentException();
		}
	}
	
	public String getAttName() {
		return attName;
	}
	
	private String toString = null;
	
	public String toString() {
		if (toString == null) {
			toString = new StringBuilder("@").append(attName).toString();
		}
		return toString;
	}
}
