package org.openxdm.xcap.server.slee;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.slee.ActivityContextInterface;
import javax.slee.ChildRelation;
import javax.slee.RolledBackContext;
import javax.slee.SbbContext;

import org.apache.log4j.Logger;
import org.mobicents.slee.enabler.userprofile.UserProfileControlSbbLocalObject;
import org.mobicents.slee.xdm.server.ServerConfiguration;
import org.openxdm.xcap.common.error.InternalServerErrorException;
import org.openxdm.xcap.common.http.HttpConstant;
import org.openxdm.xcap.server.slee.auth.RFC2617AuthQopDigest;

/**
 * 
 * @author aayush.bhatnagar
 * @author martins
 * 
 *         From the OMA-TS-XDM-core specification:
 * 
 *         The Aggregation Proxy SHALL act as an HTTP Proxy defined in [RFC2616]
 *         with the following clarifications. The Aggregation Proxy:
 * 
 *         1. SHALL be configured as an HTTP reverse proxy (see [RFC3040]);
 * 
 *         2. SHALL support authenticating the XDM Client; in case the GAA is
 *         used according to [3GPP TS 33.222], the mutual authentication SHALL
 *         be supported; or SHALL assert the XDM Client identity by inserting
 *         the X-XCAPAsserted- Identity extension header to the HTTP requests
 *         after a successful HTTP Digest Authentication as defined in Section
 *         6.3.2, in case the GAA is not used.
 * 
 *         3. SHALL forward the XCAP requests to the corresponding XDM Server,
 *         and forward the response back to the XDM Client;
 * 
 *         4. SHALL protect the XCAP traffic by enabling TLS transport security
 *         mechanism. The TLS resumption procedure SHALL be used as specified in
 *         [RFC2818].
 * 
 *         When realized with 3GPP IMS or 3GPP2 MMD networks, the Aggregation
 *         Proxy SHALL act as an Authentication Proxy defined in [3GPP TS
 *         33.222] with the following clarifications. The Aggregation Proxy:
 *         SHALL check whether an XDM Client identity has been inserted in
 *         X-3GPP-Intended-Identity header of HTTP request.
 * 
 *         • If the X-3GPP-Intended-Identity is included , the Aggregation Proxy
 *         SHALL check the value in the header is allowed to be used by the
 *         authenticated identity.
 * 
 *         • If the X-3GPP-Intended-Identity is not included, the Aggregation
 *         Proxy SHALL insert the authenticated identity in the
 *         X-3GPP-Asserted-Identity header of the HTTP request.
 * 
 *         TODO: GAA is not supported as of now. It is FFS on how we go about
 *         GAA support. TODO: TLS is not supported as of now.
 */
public abstract class AuthenticationProxySbb implements javax.slee.Sbb,
		AuthenticationProxySbbLocalObject {

	private static final Logger logger = Logger
			.getLogger(AuthenticationProxySbb.class);

	private Context myEnv = null;
	public String authenticationRealm = null;

	private static final SecureRandom nonceGenerator = new SecureRandom();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openxdm.xcap.server.slee.AuthenticationProxySbbLocalObject#authenticate
	 * (javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	public String authenticate(HttpServletRequest request,
			HttpServletResponse response) throws InternalServerErrorException {

		if (logger.isDebugEnabled()) {
			logger.debug("Authenticating request");
		}

		/**
		 * On receiving an HTTP request that does not contain the Authorization
		 * header field, the AP shall: a) challenge the user by generating a 401
		 * Unauthorized response according to the procedures specified in TS 133
		 * 222 [6] and RFC 2617 [3]; and b) forward the 401 Unauthorized
		 * response to the sender of the HTTP request.
		 */
		try {
			if (request.getHeader(HttpConstant.HEADER_AUTHORIZATION) == null) {
				challengeRequest(request, response);
				return null;
			} else {
				String user = checkAuthenticatedCredentials(request, response); 
				if (user != null) {
					if (logger.isDebugEnabled()) {
						logger.debug("Authentication suceed");
					}
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("Authentication failed");
					}
					response.setStatus(HttpServletResponse.SC_FORBIDDEN);
					response.getWriter().close();
				}
				return user;

			}
		} catch (Throwable e) {
			throw new InternalServerErrorException(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	private void challengeRequest(HttpServletRequest request,
			HttpServletResponse response) throws IOException,
			NoSuchAlgorithmException {

		if (logger.isDebugEnabled())
			logger
					.debug("Authorization header is missing...challenging the request");

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		/**
		 * If a qop directive is sent by the server in the challenge, then the
		 * challenge response MUST contain the nonce-count and cnonce
		 * parameters. This will be checked later on.
		 */
		// FIXME we are not storing the nonce & relation with request/user, 
		// so this is very weak against replay attacks
		final String challengeParams = "Digest nonce=\"" + generateNonce()
				+ "\", realm=\"" + getRealm()
				+ "\", qop=\"auth\"";

		response.setHeader(HttpConstant.HEADER_WWW_AUTHENTICATE,
				challengeParams);

		if (logger.isDebugEnabled()) {
			logger.debug("Sending response with header "+HttpConstant.HEADER_WWW_AUTHENTICATE+" challenge params: "+challengeParams);
		}
		
		// send to client
		response.getWriter().close();
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return null if authentication failed, authenticated user@domain otherwise
	 * @throws InternalServerErrorException 
	 */
	private String checkAuthenticatedCredentials(HttpServletRequest request,
			HttpServletResponse response) throws InternalServerErrorException {

		/**
		 * On receiving an HTTP request that contains the Authorization header
		 * field, the AP shall:
		 * 
		 * a)use the value of that username parameter of the Authorization
		 * header field to authenticate the user;
		 * 
		 * b)apply the procedures specified in RFC 2617 [3] for authentication;
		 * 
		 * c)if the HTTP request contains an X 3GPP Intended Identity header
		 * field (TS 124 109 [5]), then the AP may verify that the user identity
		 * belongs to the subscriber. This verification of the user identity
		 * shall be performed dependant on the subscriber's application specific
		 * or AP specific user security settings;
		 * 
		 * d)if authentication is successful, remove the Authorization header
		 * field from the HTTP request;
		 * 
		 * e)insert an HTTP X 3GPP Asserted Identity header field (TS 124 109
		 * [5]) that contains the asserted identity or a list of identities;
		 * 
		 * We wont be implementing points d and e, as they are applicable only
		 * if the Authentication Proxy had to forward the request to the XDM
		 * over the network. Here it is co-located with the XDM server.
		 */
		String authHeaderParams = request
				.getHeader(HttpConstant.HEADER_AUTHORIZATION);
		
		if (logger.isDebugEnabled()) {
			logger.debug("Authorization header included with value: "+authHeaderParams);
		}
		
		// 6 is "Digest".length(), lets skip the header value till that index
		final int digestParamsStart = 6;
		if (authHeaderParams.length() > digestParamsStart) {
			authHeaderParams = authHeaderParams.substring(digestParamsStart);
		}
		
		String username = null;
		String password = null;
		String realm = null;
		String nonce = null;
		String uri = null;
		String cnonce = null;
		String nc = null;
		String qop = null;
		String resp = null;

		for(String param : authHeaderParams.split(",")) {
			String[] paramParts = param.split("=");
			if (paramParts.length == 2) {
				String paramName = paramParts[0].trim();
				String paramValue = paramParts[1].trim();
				if (logger.isDebugEnabled()) {
					logger.debug("Found param "+paramName+" with value "+paramValue);
				}
				if (paramName.equals("username")) {
					if (paramValue.length()>2) {
						username = paramValue.substring(1, paramValue.length()-1);						
					}
					else {
						if (logger.isDebugEnabled()) {
							logger.debug("Ignoring invalid param "+paramName+" value "+paramValue);
						}
					}					
				}
				else if (paramName.equals("nonce")) {
					if (paramValue.length()>2) {
						nonce = paramValue.substring(1, paramValue.length()-1);
					}
					else {
						if (logger.isDebugEnabled()) {
							logger.debug("Ignoring invalid param "+paramName+" value "+paramValue);
						}
					}
				}
				else if (paramName.equals("cnonce")) {
					if (paramValue.length()>2) {
						cnonce = paramValue.substring(1, paramValue.length()-1);
					}
					else {
						if (logger.isDebugEnabled()) {
							logger.debug("Ignoring invalid param "+paramName+" value "+paramValue);
						}
					}
				}
				else if (paramName.equals("realm")) {
					if (paramValue.length()>2) {
						realm = paramValue.substring(1, paramValue.length()-1);
					}
					else {
						if (logger.isDebugEnabled()) {
							logger.debug("Ignoring invalid param "+paramName+" value "+paramValue);
						}
					}
				}
				else if (paramName.equals("nc")) {
					nc = paramValue;
				}
				else if (paramName.equals("response")) {
					if (paramValue.length()>2) {
						resp = paramValue.substring(1, paramValue.length()-1);
					}
					else {
						if (logger.isDebugEnabled()) {
							logger.debug("Ignoring invalid param "+paramName+" value "+paramValue);
						}
					}
				}
				else if (paramName.equals("uri")) {
					if (paramValue.length()>2) {
						uri = paramValue.substring(1, paramValue.length()-1);
					}
					else {
						if (logger.isDebugEnabled()) {
							logger.debug("Ignoring invalid param "+paramName+" value "+paramValue);
						}
					}
				}
				else if (paramName.equals("qop")) {
					if (paramValue.charAt(0) == '"') {
						if (paramValue.length()>2) {
							qop = paramValue.substring(1, paramValue.length()-1);
						}
						else {
							if (logger.isDebugEnabled()) {
								logger.debug("Ignoring invalid param "+paramName+" value "+paramValue);
							}
						}
					}
					else {
						qop = paramValue;
					}
				}
			}
			else {
				if (logger.isDebugEnabled()) {
					logger.debug("Ignoring invalid param "+param);
				}
			}
		}
		/**
		 * The client response to a WWW-Authenticate challenge for a protection
		 * space starts an authentication session with that protection space.
		 * The authentication session lasts until the client receives another
		 * WWW-Authenticate challenge from any server in the protection space. A
		 * client should remember the username, password, nonce, nonce count and
		 * opaque values associated with an authentication session to use to
		 * construct the Authorization header in future requests within that
		 * protection space.
		 */
		if (username == null || realm == null || nonce == null || cnonce == null || nc == null
				|| uri == null || resp == null) {
			logger
					.error("A required parameter is missing in the challenge response");
			// FIXME should be replied with BAD REQUEST 400
			return null;
		}
		
		final String digest = new RFC2617AuthQopDigest(username, realm, password, nonce, nc, cnonce, qop, request.getMethod().toUpperCase(), uri).digest();
		
		if (digest != null && digest.equals(resp)) {
			if (logger.isDebugEnabled())
				logger.debug("authentication response is matching");

			/**
			 * Add the cnonce,nc and qop as received in the Authorization header
			 * of the request. We need to add the Authentication-Info header and
			 * set these values.
			 * 
			 * Authentication-Info: qop=auth-int,
			 * rspauth="6629fae49394a05397450978507c4ef1",
			 * cnonce="6629fae49393a05397450978507c4ef1", nc=00000001
			 */
			String params = "cnonce=\"" + cnonce + "\", nc=" + nc + ", qop="
					+ qop + ", rspauth=\"" + digest+"\"";

			response.addHeader("Authentication-Info", params);
			return username;
		} else {
			if (logger.isDebugEnabled())
				logger.debug("authentication response digest received ("+resp+") didn't match the one calculated ("+digest+")");

			return null;
		}
	}

	/**
	 * Get the authentication scheme
	 * 
	 * @return the scheme name
	 */
	public String getScheme() {
		return "Digest";
	}

	/**
	 * get the authentication realm
	 * 
	 * @return the realm name
	 */
	public String getRealm() {
		return this.authenticationRealm;
	}

	/**
	 * Get the authentication Algorithm
	 * 
	 * @return the alogrithm name (i.e. Digest).
	 */
	public String getAlgorithm() {
		return "MD5";
	}

	/**
	 * Generate the challenge string.
	 * 
	 * @return a generated nonce.
	 * @throws NoSuchAlgorithmException
	 */
	public String generateNonce() {
		synchronized (nonceGenerator) {
			return Integer.toHexString(nonceGenerator.nextInt());
		}
	}

	// -- user profile enabler child relation
	
	public abstract ChildRelation getUserProfileControlChildRelation();

	protected UserProfileControlSbbLocalObject getUserProfileControlSbb() {
			try {
				return (UserProfileControlSbbLocalObject) getUserProfileControlChildRelation()
						.create();
			} catch (Exception e) {
				logger.error("Failed to create child sbb", e);
				return null;
			}
	}
	
	// -- sbb object lifecycle
	
	public void setSbbContext(SbbContext context) {
		if (logger.isDebugEnabled())
			logger.debug("Set sbb context for AuthenticationProxy Sbb");
		this.sbbContext = context;
		try {
			myEnv = (Context) new InitialContext().lookup("java:comp/env");
			this.authenticationRealm = (String) myEnv
					.lookup("authenticationRealm");
			if (authenticationRealm.equals("")) {
				authenticationRealm = ServerConfiguration.SERVER_HOST;
			}

		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void unsetSbbContext() {
		this.sbbContext = null;
	}

	public void sbbCreate() throws javax.slee.CreateException {
	}

	public void sbbPostCreate() throws javax.slee.CreateException {
	}

	public void sbbActivate() {
	}

	public void sbbPassivate() {
	}

	public void sbbRemove() {
	}

	public void sbbLoad() {
	}

	public void sbbStore() {
	}

	public void sbbExceptionThrown(Exception exception, Object event,
			ActivityContextInterface activity) {
	}

	public void sbbRolledBack(RolledBackContext context) {
	}

	protected SbbContext getSbbContext() {
		return sbbContext;
	}

	private SbbContext sbbContext; // This SBB's SbbContext

}
