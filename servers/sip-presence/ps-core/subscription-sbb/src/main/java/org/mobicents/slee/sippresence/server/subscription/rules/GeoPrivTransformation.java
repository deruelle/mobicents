package org.mobicents.slee.sippresence.server.subscription.rules;

/**
 * provideGeopriv values on OMA pres-rules transformations.
 * @author emmartins
 *
 */
public enum GeoPrivTransformation {
	
	false_(0),
	full(10);
	
	private final int value;
	
	private GeoPrivTransformation(int value) {
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
