package org.openxdm.xcap.server.slee.appusage.rlsservices;

import org.openxdm.xcap.common.appusage.AuthorizationPolicy;
import org.openxdm.xcap.common.uri.DocumentSelector;

/**
 * This XCAP Authorization Policy implements the Default
 * Authorization Policy.
 * 
 * By XCAP Specs:
 * 
 * "This application usage does not modify the default XCAP authorization
 * policy, which is that only a user can read, write or modify their own
 * documents.  A server can allow privileged users to modify documents
 * that they don't own, but the establishment and indication of such
 * policies is outside the scope of this document.  It is anticipated
 * that a future application usage will define which users are allowed
 * to modify an RLS services document.
 * 
 * The index document maintained in the global tree represents sensitive
 * information, as it contains the union of all of the information for
 * all users on the server.  As such, its access MUST be restricted to
 * trusted elements within domain of the server.  Typically, this would
 * be limited to the RLSs that need access to this document."
 *    
 * @author Eduardo Martins
 *
 */

public class RLSServicesAuthorizationPolicy extends AuthorizationPolicy {

	public boolean isAuthorized(String user, AuthorizationPolicy.Operation operation, DocumentSelector documentSelector) {
		
		if (user == null) {
			// no authentication so no authorization
			return true;
		}
		
		// check args
		
		else if (operation == null) {
			throw new IllegalArgumentException("operation is null");
		}		
		else if (documentSelector == null) {
			throw new IllegalArgumentException("document selector is null");
		}
				
		try {
			// split document parent, FIXME use getDocumentPArent
			String[] documentParentParts = documentSelector.getCompleteDocumentParent().split("/");
			// check auid child directory
			if (documentParentParts[2].equalsIgnoreCase("global")) {
				// /auid/global dir, never authorize operation except pre-authorized users
				// which will not need to use the auth policy
				// FIXME should these users be controlled here too?
				return false;				
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
