package org.openxdm.xcap.common.uri;

/**
 * 
 * An element selector step is part of the element selector. This step extends
 * ElementSelectorStep by containing positional information, that is used to
 * select an element from a xml document, and it's defined in XCAP specs by the
 * regular expression:
 * 
 * by-pos = NameorAny "[" position "]" NameorAny = QName / "*" position =
 * 1*DIGIT
 * 
 * @author Eduardo Martins
 * 
 */
public class ElementSelectorStepByPos extends ElementSelectorStep {

	private int pos;

	/**
	 * Creates a new step from the specified element name and position. Besides
	 * possible limitations defined by ElementSelectorStep, this constructor
	 * throws IllegalArgumentException if the provided position is not > 0.
	 * 
	 * @param name
	 * @param pos
	 */
	public ElementSelectorStepByPos(String name, int pos) {
		super(name);

		if (pos < 0) {
			throw new IllegalArgumentException(
					"pos must be non negative number.");
		} else {
			this.pos = pos;
		}
	}

	/**
	 * Retreives the element position of this step.
	 * 
	 * @return
	 */
	public int getPos() {
		return pos;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName()).append('[').append(getPos()).append(']');
		return sb.toString();
	}
}
