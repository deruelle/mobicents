package org.openxdm.xcap.common.error;

public class NoParentConflictException extends ConflictException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String conflictError = null;
	private String existingAncestor = null;
	private String queryComponent = null;
	private String schemeAndAuthorityURI = null;
			
	public NoParentConflictException(String existingAncestor) {
		if (existingAncestor == null) {
			throw new IllegalArgumentException("existing ancestor must not be null");
		}
		this.existingAncestor = existingAncestor;
	}
	
	public void setQueryComponent(String queryComponent) {
		this.queryComponent = queryComponent;
	}
	
	public void setSchemeAndAuthorityURI(String schemeAndAuthorityURI) {		
		this.schemeAndAuthorityURI = schemeAndAuthorityURI;
	}
	
	protected String getConflictError() {
		if (conflictError == null) {
			if (schemeAndAuthorityURI != null) {
				StringBuilder sb = new StringBuilder("<no-parent><ancestor>").append(schemeAndAuthorityURI);
				if (existingAncestor != "") {
					sb.append(existingAncestor);
				}
				if (queryComponent != null) {
					sb.append('?').append(queryComponent);
				}
				sb.append("</ancestor></no-parent>");
				conflictError = sb.toString();				
			}
			else{
				return "<parent />";
			}			
		}
		return conflictError;
	}

}
