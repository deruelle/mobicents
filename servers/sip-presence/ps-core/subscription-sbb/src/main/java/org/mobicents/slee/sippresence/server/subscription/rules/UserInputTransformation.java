package org.mobicents.slee.sippresence.server.subscription.rules;

/**
 * user-input values on pres-rules transformations.
 * @author emmartins
 *
 */
public enum UserInputTransformation {
	
	false_(0),
	bare(10),
	thresholds(20),
	full(30);
	
	private final int value;
	
	private UserInputTransformation(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		if (value == 0) {
			return "false";
		}
		else {
			return super.toString();
		}
	}
}
