package org.openxdm.xcap.server.slee;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.slee.SbbLocalObject;

import org.openxdm.xcap.common.error.InternalServerErrorException;

/**
 * 
 * @author aayush.bhatnagar
 * @author martins
 * 
 *         This SBB is used to handle all the authentication related logic for
 *         incoming XCAP requests and validate the challenge responses.
 * 
 */

public interface AuthenticationProxySbbLocalObject extends SbbLocalObject {

	/**
	 * Handles authentication of the XCAP request.
	 * 
	 * @param request
	 * @param response
	 * @return the authenticated user, null if authentication failed or is not
	 *         complete, the request processing should be canceled since a
	 *         response was already sent
	 * @throws InternalServerErrorException
	 */
	public String authenticate(HttpServletRequest request,
			HttpServletResponse response) throws InternalServerErrorException;

}
