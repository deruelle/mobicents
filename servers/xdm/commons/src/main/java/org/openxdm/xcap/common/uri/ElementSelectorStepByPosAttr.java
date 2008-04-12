package org.openxdm.xcap.common.uri;

import org.openxdm.xcap.common.xml.XMLValidator;

/**
 * An element selector step is part of the element selector. This step extends
 * ElementSelectorStepByAttr by containing positional information, that is used
 * to select an element from a xml document.
 * 
 * @author Eduardo Martins
 * 
 */
public class ElementSelectorStepByPosAttr extends ElementSelectorStepByPos {
	
	private String attrName;
	private String attrValue;

	/**
	 * Creates a new step from the specified element name and position. Besides
	 * possible limitations defined by ElementSelectorStepByAttr, this
	 * constructor throws IllegalArgumentException if the provided position is
	 * not > 0.
	 * 
	 * @param name
	 * @param pos
	 * @param attrName
	 * @param attrValue
	 */
	public ElementSelectorStepByPosAttr(String name, int pos, String attrName,
			String attrValue) {
		/*super(name, attrName, attrValue);
		
		if (pos < 0) {
			throw new IllegalArgumentException(
					"pos must be non negative number.");
		} else {
			this.pos = pos;
		}
		*/
		super(name,pos);
		if (XMLValidator.isQName(attrName)) {
			this.attrName = attrName;
		} else {
			throw new IllegalArgumentException(
					"attribute name must be a QName.");
		}
		this.attrValue = attrValue;
	}

	public String getAttrName() {
		return attrName;
	}
	
	public String getAttrValue() {
		return attrValue;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName()).append('[').append(getPos()).append("][@").append(getAttrName()).append("='").append(getAttrValue()).append("']");
		return sb.toString();
	}
}
