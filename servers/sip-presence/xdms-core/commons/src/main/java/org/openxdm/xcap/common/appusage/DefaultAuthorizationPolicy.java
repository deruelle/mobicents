package org.openxdm.xcap.common.appusage;

import org.openxdm.xcap.common.uri.DocumentSelector;

/**
 * This XCAP Authorization Policy implements the Default
 * Authorization Policy.
 * 
 * By XCAP Specs:
 * 
 * "By default, each user is able to access (read, modify, and delete)
 * all of the documents below their home directory, and any user is able
 * to read documents within the global directory.  However, only trusted
 * users, explicitly provisioned into the server, can modify global
 * documents."
 *    
 * @author Eduardo Martins
 *
 */
public class DefaultAuthorizationPolicy extends AuthorizationPolicy {

	public boolean isAuthorized(String user, AuthorizationPolicy.Operation operation, DocumentSelector documentSelector) throws NullPointerException {
		
		// check args
		if (user == null) {
			throw new NullPointerException("user is null");
		}
		else if (operation == null) {
			throw new NullPointerException("operation is null");
		}		
		else if (documentSelector == null) {
			throw new NullPointerException("document selector is null");
		}
				
		try {
			
			// split document parent FIXME use getDocumentParent
			String[] documentParentParts = documentSelector.getCompleteDocumentParent().split("/");
			// part 0 is "" and part 1 is the auid
			// so the auid child directory is part 2 						
			if (documentParentParts[2].equalsIgnoreCase("global")) {
				// /auid/global dir, authorize operation only if is a get operation
				if(operation.equals(AuthorizationPolicy.Operation.GET)) {
					return true;
				}
				else {
					return false;
				}
			} else if (documentParentParts[2].equalsIgnoreCase("users")) {
				// /auid/users directory, get it's child, the user directory 
				String userDirectory = documentParentParts[3];
				// only the user is authorized to operate on it's directory 
				if (user.equalsIgnoreCase(userDirectory)) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
		catch (IndexOutOfBoundsException e) {
			throw new IllegalArgumentException("invalid document selector");
		}
		
	}
}
