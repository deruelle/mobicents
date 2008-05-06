package org.openxdm.xcap.common.error;

public class ConstraintFailureConflictException extends ConflictException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String phrase;
	
	public ConstraintFailureConflictException(String phrase) {
		this.phrase = phrase;
	}
	
	protected String getConflictError() {
		StringBuilder sb = new StringBuilder("<constraint-failure phrase='").append(phrase).append("' />");
		return sb.toString();
	}

}
