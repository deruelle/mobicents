package org.openxdm.xcap.server.slee;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

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
import org.mobicents.slee.enabler.userprofile.UserProfile;
import org.mobicents.slee.enabler.userprofile.UserProfileControlSbbLocalObject;
import org.mobicents.slee.xdm.server.ServerConfiguration;
import org.openxdm.xcap.common.error.InternalServerErrorException;
import org.openxdm.xcap.common.http.HttpConstant;
import org.openxdm.xcap.server.slee.auth.RFC2617AuthQopDigest;
import org.openxdm.xcap.server.slee.auth.RFC2617ChallengeParamGenerator;

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

	private static final RFC2617ChallengeParamGenerator challengeParamGenerator = new RFC2617ChallengeParamGenerator();
	
	private Context myEnv = null;
	public String authenticationRealm = null;

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
	 * @throws InternalServerErrorException 
	 */
	private void challengeRequest(HttpServletRequest request,
			HttpServletResponse response) throws IOException,
			NoSuchAlgorithmException, InternalServerErrorException {

		if (logger.isDebugEnabled())
			logger
					.debug("Authorization header is missing...challenging the request");

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		/**
		 * If a qop directive is sent by the server in the challenge, then the
		 * challenge response MUST contain the nonce-count and cnonce
		 * parameters. This will be checked later on.
		 */
		String opaque = challengeParamGenerator.generateOpaque();
		final String challengeParams = "Digest nonce=\"" + challengeParamGenerator.getNonce(opaque)
				+ "\", realm=\"" + getRealm()
				+ "\", opaque=\"" + opaque
				+ "\", qop=auth";

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
		String opaque = null;

		for(String param : authHeaderParams.split(",")) {
			int i = param.indexOf('=');
			if (i > 0 && i < (param.length()-1)) {
				String paramName = param.substring(0,i).trim();
				String paramValue = param.substring(i+1).trim();
				if (paramName.equals("username")) {
					if (paramValue.length()>2) {
						username = paramValue.substring(1, paramValue.length()-1);
						if (logger.isDebugEnabled()) {
							logger.debug("Username param with value "+username);
						}
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
						if (logger.isDebugEnabled()) {
							logger.debug("Nonce param with value "+nonce);
						}
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
						if (logger.isDebugEnabled()) {
							logger.debug("CNonce param with value "+cnonce);
						}
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
						if (logger.isDebugEnabled()) {
							logger.debug("Realm param with value "+realm);
						}
					}
					else {
						if (logger.isDebugEnabled()) {
							logger.debug("Ignoring invalid param "+paramName+" value "+paramValue);
						}
					}
				}
				else if (paramName.equals("nc")) {
					nc = paramValue;
					if (logger.isDebugEnabled()) {
						logger.debug("Nonce-count param with value "+nc);
					}
				}
				else if (paramName.equals("response")) {
					if (paramValue.length()>2) {
						resp = paramValue.substring(1, paramValue.length()-1);
						if (logger.isDebugEnabled()) {
							logger.debug("Response param with value "+resp);
						}
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
						if (logger.isDebugEnabled()) {
							logger.debug("Digest uri param with value "+uri);
						}
					}
					else {
						if (logger.isDebugEnabled()) {
							logger.debug("Ignoring invalid param "+paramName+" value "+paramValue);
						}
					}
				}
				else if (paramName.equals("opaque")) {
					if (paramValue.length()>2) {
						opaque = paramValue.substring(1, paramValue.length()-1);
						if (logger.isDebugEnabled()) {
							logger.debug("Opaque param with value "+opaque);
						}
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
					if (logger.isDebugEnabled()) {
						logger.debug("Qop param with value "+qop);
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
				|| uri == null || resp == null || opaque == null) {
			logger
					.error("A required parameter is missing in the challenge response");
			// FIXME should be replied with BAD REQUEST 400
			return null;
		}
		
		// verify opaque vs nonce
		if (challengeParamGenerator.getNonce(opaque).equals(nonce)) {
			if (logger.isDebugEnabled())
				logger.debug("Nonce provided matches the one generated using opaque as seed");
			
		}
		else {
			if (logger.isDebugEnabled())
				logger.debug("Authentication failed, nonce provided doesn't match the one generated using opaque as seed");
			return null;
		}
		
		if (!qop.equals("auth")) {
			if (logger.isDebugEnabled())
				logger.debug("Authentication failed, qop value "+qop+" unsupported");
			return null;
		}
		
		// get user password
		UserProfile userProfile = getUserProfileControlSbb().find(username);
		if (userProfile == null) {
			if (logger.isDebugEnabled())
				logger.debug("Authentication failed, profile not found for user "+username);
			return null;
		}
		else {
			password = userProfile.getPassword();
		}
		
		final String digest = new RFC2617AuthQopDigest(username, realm, password, nonce, nc, cnonce, request.getMethod().toUpperCase(), uri).digest();
		
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
