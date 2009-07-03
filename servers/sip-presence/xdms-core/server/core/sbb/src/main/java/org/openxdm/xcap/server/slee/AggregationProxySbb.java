package org.openxdm.xcap.server.slee;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.slee.ActivityContextInterface;
import javax.slee.ChildRelation;
import javax.slee.RolledBackContext;
import javax.slee.SbbContext;

import net.java.slee.resource.http.events.HttpServletRequestEvent;
import org.apache.log4j.Logger;
import org.mobicents.slee.xdm.server.ServerConfiguration;
import org.openxdm.xcap.common.error.BadRequestException;
import org.openxdm.xcap.common.error.ConflictException;
import org.openxdm.xcap.common.error.InternalServerErrorException;
import org.openxdm.xcap.common.error.MethodNotAllowedException;
import org.openxdm.xcap.common.error.NoParentConflictException;
import org.openxdm.xcap.common.error.NotFoundException;
import org.openxdm.xcap.common.error.PreconditionFailedException;
import org.openxdm.xcap.common.error.UnsupportedMediaTypeException;
import org.openxdm.xcap.common.http.HttpConstant;
import org.openxdm.xcap.common.resource.Resource;
import org.openxdm.xcap.common.uri.ParseException;
import org.openxdm.xcap.common.uri.Parser;
import org.openxdm.xcap.common.uri.ResourceSelector;
import org.openxdm.xcap.server.etag.ETagValidator;
import org.openxdm.xcap.server.etag.IfMatchETagValidator;
import org.openxdm.xcap.server.etag.IfNoneMatchETagValidator;
import org.openxdm.xcap.server.result.ReadResult;
import org.openxdm.xcap.server.result.WriteResult;

/**
 * 
 * @author martins
 * @author aayush.bhatnagar
 * 
 */
public abstract class AggregationProxySbb implements javax.slee.Sbb {

	private SbbContext sbbContext = null;

	private static final Logger logger = Logger
			.getLogger(AggregationProxySbb.class);

	public void setSbbContext(SbbContext context) {
		this.sbbContext = context;
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
		if (logger.isDebugEnabled())
			logger.debug("sbbExceptionThrown(exception=" + exception.toString()
					+ ",event=" + event.toString() + ",activity="
					+ activity.toString() + ")");
	}

	public void sbbRolledBack(RolledBackContext sbbRolledBack) {
		if (logger.isDebugEnabled())
			logger.debug("sbbRolledBack(sbbRolledBack="
					+ sbbRolledBack.toString() + ")");
	}

	protected SbbContext getSbbContext() {
		return sbbContext;
	}

	// CHILD RELATIONS & RA ABSTRACTIONS
	// ################################################################

	public abstract ChildRelation getRequestProcessorChildRelation();

	protected RequestProcessorSbbLocalObject getRequestProcessor() {
		try {
			return (RequestProcessorSbbLocalObject) getRequestProcessorChildRelation()
			.create();
		} catch (Exception e) {
			logger.error("Failed to create child sbb", e);
			return null;
		}
	}

	// added by aayush here: Child relation and child sbb creation
	// for Authentication Proxy.
	public abstract ChildRelation getAuthenticationProxyChildRelation();

	protected AuthenticationProxySbbLocalObject getAuthenticationProxy() {
			try {
				return (AuthenticationProxySbbLocalObject) getAuthenticationProxyChildRelation()
						.create();
			} catch (Exception e) {
				logger.error("Failed to create child sbb", e);
				return null;
			}
	}

	public void onDelete(HttpServletRequestEvent event,
			ActivityContextInterface aci) {

		// detach from the activity
		aci.detach(sbbContext.getSbbLocalObject());

		HttpServletRequest request = event.getRequest();
		HttpServletResponse response = event.getResponse();

		try {

			PrintWriter responseWriter = response.getWriter();

			try {

				// get xcap root from config
				String xcapRoot = ServerConfiguration.XCAP_ROOT;

				// create resource selector from request's uri & query
				// string
				ResourceSelector resourceSelector = Parser
						.parseResourceSelector(xcapRoot, request
								.getRequestURI(), request.getQueryString());

				// user authentication
				String user = null;
				if (ServerConfiguration.DO_AUTHENTICATION) {
					user = getAuthenticationProxy().authenticate(request, response);
					if (user == null) {
						return;
					}
				}
				
				// check conditional request headers
				// get ifMatch eTag
				ETagValidator eTagValidator = null;
				String eTag = request.getHeader(HttpConstant.HEADER_IF_MATCH);
				if (eTag != null) {
					eTagValidator = new IfMatchETagValidator(eTag);
				} else {
					eTag = request.getHeader(HttpConstant.HEADER_IF_NONE_MATCH);
					if (eTag != null) {
						eTagValidator = new IfNoneMatchETagValidator(eTag);
					}
				}

				// delete in data source
				if (logger.isInfoEnabled()) {
					logger.info("delete(resourceSelector=" + resourceSelector
							+ ",eTagValidator=" + eTagValidator + ",xcapRoot="
							+ xcapRoot + ")");
				}
				WriteResult result = getRequestProcessor().delete(
						resourceSelector, eTagValidator, xcapRoot,user);
				// set response status
				response.setStatus(result.getResponseStatus());
				// set response entity tag if provided
				if (result.getResponseEntityTag() != null) {
					response.setHeader(HttpConstant.HEADER_ETAG, result
							.getResponseEntityTag());
				}

			} catch (ParseException e) {
				NotFoundException ne = new NotFoundException();
				if (logger.isDebugEnabled())
					logger.debug("invalid xcap uri, replying , replying "
							+ ne.getResponseStatus());
				response.setStatus(ne.getResponseStatus());

			} catch (NotFoundException e) {
				if (logger.isDebugEnabled())
					logger.debug("doc/elem/attrib not found, replying "
							+ e.getResponseStatus());
				response.setStatus(e.getResponseStatus());

			} catch (ConflictException e) {
				if (logger.isDebugEnabled())
					logger.debug("conflict exception, replying "
							+ e.getResponseStatus());
				response.setStatus(e.getResponseStatus());
				responseWriter.print(e.getResponseContent());

			} catch (MethodNotAllowedException e) {
				if (logger.isDebugEnabled())
					logger.debug("method not allowed, replying "
							+ e.getResponseStatus());
				response.setStatus(e.getResponseStatus());
				// add all exception headers
				Map<String, String> exceptionHeaders = e.getResponseHeaders();
				for (Iterator<String> i = exceptionHeaders.keySet().iterator(); i
						.hasNext();) {
					String headerName = i.next();
					String headerValue = exceptionHeaders.get(headerName);
					response.setHeader(headerName, headerValue);
				}

			} catch (PreconditionFailedException e) {
				if (logger.isDebugEnabled())
					logger.debug("precondition failed on etags, replying "
							+ e.getResponseStatus());
				response.setStatus(e.getResponseStatus());

			} catch (InternalServerErrorException e) {
				logger.warn("internal server error: " + e.getMessage()
						+ ", replying " + e.getResponseStatus());
				response.setStatus(e.getResponseStatus());

			} catch (BadRequestException e) {
				if (logger.isDebugEnabled())
					logger.debug("bad request, replying "
							+ e.getResponseStatus());
				response.setStatus(e.getResponseStatus());
			}
			// send to client
			responseWriter.close();

		} catch (Exception e) {
			logger.error("Error processing onDelete()", e);
		}

	}

	public void onGet(HttpServletRequestEvent event,
			ActivityContextInterface aci) {

		// detach from the activity
		aci.detach(sbbContext.getSbbLocalObject());

		HttpServletRequest request = event.getRequest();
		HttpServletResponse response = event.getResponse();

		try {

			PrintWriter responseWriter = response.getWriter();

			try {

				// create jxcap resource selector from request's uri & query
				// string
				ResourceSelector resourceSelector = Parser
						.parseResourceSelector(ServerConfiguration.XCAP_ROOT,
								request.getRequestURI(), request
										.getQueryString());
				// read result from data source
				if (logger.isInfoEnabled()) {
					logger.info("get(resourceSelector=" + resourceSelector
							+ ")");
				}
				
				// user authentication
				String user = null;
				if (ServerConfiguration.DO_AUTHENTICATION) {
					user = getAuthenticationProxy().authenticate(request, response);
					if (user == null) {
						return;
					}
				}
				
				ReadResult result = getRequestProcessor().get(resourceSelector,user);
				// get data object from result
				Resource dataObject = result.getResponseDataObject();
				// set response content type
				response.setContentType(dataObject.getMimetype());
				// set response entity tag
				response.setHeader(HttpConstant.HEADER_ETAG, result
						.getResponseEntityTag());
				// add response content
				responseWriter.println(dataObject.toXML());

			} catch (ParseException e) {
				NotFoundException ne = new NotFoundException();
				logger.warn("invalid xcap uri, replying "
						+ ne.getResponseStatus());
				response.setStatus(ne.getResponseStatus());

			} catch (NotFoundException e) {

				if (logger.isDebugEnabled())
					logger.debug("doc/elem/attrib not found, replying "
							+ e.getResponseStatus());
				response.setStatus(e.getResponseStatus());

			} catch (InternalServerErrorException e) {
				logger.warn("internal server error: ");
				response.setStatus(e.getResponseStatus());

			} catch (BadRequestException e) {
				if (logger.isDebugEnabled())
					logger.debug("bad request, replying "
							+ e.getResponseStatus());
				response.setStatus(e.getResponseStatus());
			}
			// send to client
			responseWriter.close();

		} catch (Exception e) {
			logger.error("Error processing onGet()", e);
		}

	}

	public void onPut(HttpServletRequestEvent event,
			ActivityContextInterface aci) {

		// detach from the activity
		aci.detach(sbbContext.getSbbLocalObject());

		HttpServletRequest request = event.getRequest();
		HttpServletResponse response = event.getResponse();

		try {

			PrintWriter responseWriter = response.getWriter();

			try {

				// create resource selector from request's uri & query
				// string
				ResourceSelector resourceSelector = Parser
						.parseResourceSelector(ServerConfiguration.XCAP_ROOT,
								request.getRequestURI(), request
										.getQueryString());

				// user authentication
				String user = null;
				if (ServerConfiguration.DO_AUTHENTICATION) {
					user = getAuthenticationProxy().authenticate(request, response);
					if (user == null) {
						return;
					}
				}
				
				// check conditional request headers
				// get ifMatch eTag
				ETagValidator eTagValidator = null;
				String eTag = request.getHeader(HttpConstant.HEADER_IF_MATCH);
				if (eTag != null) {
					eTagValidator = new IfMatchETagValidator(eTag);
				} else {
					eTag = request.getHeader(HttpConstant.HEADER_IF_NONE_MATCH);
					if (eTag != null) {
						eTagValidator = new IfNoneMatchETagValidator(eTag);
					}
				}
				// get content mimetype
				String mimetype = request.getContentType();
				if (logger.isInfoEnabled()) {
					logger.info("put(resourceSelector=" + resourceSelector
							+ ",mimetype=" + mimetype + ",eTagValidator="
							+ eTagValidator + ",xcapRoot="
							+ ServerConfiguration.XCAP_ROOT + ")");
				}
				// put object in data source
				WriteResult result = getRequestProcessor().put(
						resourceSelector, mimetype, request.getInputStream(),
						eTagValidator, ServerConfiguration.XCAP_ROOT,user);
				// set response status
				response.setStatus(result.getResponseStatus());
				// set response entity tag with new one on result
				response.setHeader(HttpConstant.HEADER_ETAG, result
						.getResponseEntityTag());

			} catch (ParseException e) {
				// invalid resource selector
				BadRequestException bre = new BadRequestException();
				if (logger.isDebugEnabled())
					logger.debug("invalid xcap uri, replying "
							+ bre.getResponseStatus());
				response.setStatus(bre.getResponseStatus());

			} catch (IOException e) {
				InternalServerErrorException ie = new InternalServerErrorException(
						e.getMessage());
				logger.warn("internal server error: " + e.getMessage()
						+ ", replying " + ie.getResponseStatus());
				response.setStatus(ie.getResponseStatus());

			} catch (NoParentConflictException e) {
				// add base uri
				e
						.setSchemeAndAuthorityURI(ServerConfiguration.SCHEME_AND_AUTHORITY_URI);
				// add query string if exists
				if (request.getQueryString() != null) {
					e.setQueryComponent(request.getQueryString());
				}
				if (logger.isDebugEnabled())
					logger.debug("no parent conflict exception, replying "
							+ e.getResponseStatus());
				response.setStatus(e.getResponseStatus());
				responseWriter.print(e.getResponseContent());

			} catch (ConflictException e) {
				if (logger.isDebugEnabled())
					logger.debug("conflict exception, replying "
							+ e.getResponseStatus());
				response.setStatus(e.getResponseStatus());
				responseWriter.print(e.getResponseContent());

			} catch (MethodNotAllowedException e) {
				if (logger.isDebugEnabled())
					logger.debug("method not allowed, replying "
							+ e.getResponseStatus());
				response.setStatus(e.getResponseStatus());
				// add all exception headers
				Map<String, String> exceptionHeaders = e.getResponseHeaders();
				for (Iterator<String> i = exceptionHeaders.keySet().iterator(); i
						.hasNext();) {
					String headerName = i.next();
					String headerValue = exceptionHeaders.get(headerName);
					response.setHeader(headerName, headerValue);
				}

			} catch (UnsupportedMediaTypeException e) {
				if (logger.isDebugEnabled())
					logger.debug("unsupported media exception, replying "
							+ e.getResponseStatus());
				response.setStatus(e.getResponseStatus());

			} catch (InternalServerErrorException e) {
				logger.warn("internal server error: " + e.getMessage()
						+ ", replying " + e.getResponseStatus());
				response.setStatus(e.getResponseStatus());

			} catch (PreconditionFailedException e) {
				if (logger.isDebugEnabled())
					logger.debug("precondition failed on etags, replying "
							+ e.getResponseStatus());
				response.setStatus(e.getResponseStatus());

			} catch (BadRequestException e) {
				if (logger.isDebugEnabled())
					logger.debug("invalid xcap uri, replying "
							+ e.getResponseStatus());
				response.setStatus(e.getResponseStatus());
			}
			// send to client
			responseWriter.close();

		} catch (Exception e) {
			logger.error("Error processing onPut()", e);
		}

	}

	// ######################################## NOT SUPPORTED METHODS

	public void onPost(HttpServletRequestEvent event,
			ActivityContextInterface aci) {

		sendUnsupportedRequestErrorResponse(event, aci);
	}

	public void onHead(HttpServletRequestEvent event,
			ActivityContextInterface aci) {

		sendUnsupportedRequestErrorResponse(event, aci);
	}

	public void onOptions(HttpServletRequestEvent event,
			ActivityContextInterface aci) {

		sendUnsupportedRequestErrorResponse(event, aci);
	}

	public void onTrace(HttpServletRequestEvent event,
			ActivityContextInterface aci) {
		sendUnsupportedRequestErrorResponse(event, aci);
	}

	private void sendUnsupportedRequestErrorResponse(
			HttpServletRequestEvent event, ActivityContextInterface aci) {

		// detach from the activity
		aci.detach(sbbContext.getSbbLocalObject());

		// method not allowed, set right sc and allow header then send response
		try {
			HttpServletResponse response = event.getResponse();
			response.setStatus(MethodNotAllowedException.RESPONSE_STATUS);
			response.setHeader(HttpConstant.HEADER_ALLOW, "GET, PUT, DELETE");
			response.flushBuffer();
		} catch (Exception e) {
			logger.error("unable to send response", e);
		}
	}

}