package org.openxdm.xcap.server.slee;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.slee.ActivityContextInterface;
import javax.slee.ActivityEndEvent;
import javax.slee.ChildRelation;
import javax.slee.InitialEventSelector;
import javax.slee.RolledBackContext;
import javax.slee.SbbContext;
import javax.slee.serviceactivity.ServiceActivity;
import javax.slee.serviceactivity.ServiceActivityFactory;

import net.java.slee.resource.http.events.HttpServletRequestEvent;

import org.apache.log4j.Logger;
import org.openxdm.xcap.common.appusage.AppUsage;
import org.openxdm.xcap.common.appusage.AppUsageFactory;
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
import org.openxdm.xcap.server.slee.resource.appusagecache.AppUsageCacheResourceAdaptorSbbInterface;
import org.openxdm.xcap.server.slee.resource.datasource.DataSourceSbbInterface;

public abstract class AbstractAppUsageSbb implements javax.slee.Sbb {

	private SbbContext sbbContext = null; // This SBB's context

	private Context myEnv = null; // This SBB's environment

	private AppUsageCacheResourceAdaptorSbbInterface appUsageCache = null;

	private Logger logger = getLogger();

	protected DataSourceSbbInterface dataSource = null;
	
	protected DataSourceSbbInterface getDataSource() {
		return dataSource;
	}
	private void logAsInfo(String msg) {
		logger.info(msg);
	}

	private void logAsDebug(String msg) {		
		logger.debug(msg);	
	}

	private boolean isLogDebugEnabled() {
		if (logger.isDebugEnabled()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private void logAsError(String msg, Exception e) {
		logger.error(msg, e);
	}

	/**
	 * Called when an sbb object is instantied and enters the pooled state.
	 */
	public void setSbbContext(SbbContext context) {
		if (isLogDebugEnabled()) logAsDebug("setSbbContext(context=" + context.toString() + ")");
		this.sbbContext = context;
		try {
			myEnv = (Context) new InitialContext().lookup("java:comp/env");
			appUsageCache = (AppUsageCacheResourceAdaptorSbbInterface) myEnv
					.lookup("slee/resources/openxdm/appusagecache/sbbrainterface");
			dataSource = (DataSourceSbbInterface) myEnv.lookup("slee/resources/openxdm/datasource/sbbrainterface");

		} catch (NamingException e) {
			logAsError("Can't set sbb context.", e);
		}
	}

	public void unsetSbbContext() {
		if (isLogDebugEnabled()) logAsDebug("unsetSbbContext()");
		this.sbbContext = null;
	}

	public void sbbCreate() throws javax.slee.CreateException {
		if (isLogDebugEnabled()) logAsDebug("sbbCreate()");
	}

	public void sbbPostCreate() throws javax.slee.CreateException {
		if (isLogDebugEnabled()) logAsDebug("sbbPostCreate()");
	}

	public void sbbActivate() {
		if (isLogDebugEnabled()) logAsDebug("sbbActivate()");
	}

	public void sbbPassivate() {
		if (isLogDebugEnabled()) logAsDebug("sbbPassivate()");
	}

	public void sbbRemove() {
		if (isLogDebugEnabled()) logAsDebug("sbbRemove()");
	}

	public void sbbLoad() {
		if (isLogDebugEnabled()) logAsDebug("sbbLoad()");
	}

	public void sbbStore() {
		if (isLogDebugEnabled()) logAsDebug("sbbStore()");
	}

	public void sbbExceptionThrown(Exception exception, Object event,
			ActivityContextInterface activity) {
		if (isLogDebugEnabled()) logAsDebug("sbbExceptionThrown(exception=" + exception.toString()
				+ ",event=" + event.toString() + ",activity="
				+ activity.toString() + ")");
	}

	public void sbbRolledBack(RolledBackContext sbbRolledBack) {
		if (isLogDebugEnabled()) logAsDebug("sbbRolledBack(sbbRolledBack=" + sbbRolledBack.toString()
				+ ")");
	}

	protected SbbContext getSbbContext() {
		return sbbContext;
	}

	// INITIAL EVENT SELECTOR

	public InitialEventSelector initialEventSelector(InitialEventSelector ies) {
		// check auid from address
		int auidOffset = "/mobicents/".length();
		int expectedEnd = auidOffset + getAUID().length();
		String addressString = ies.getAddress().getAddressString();
		if (addressString.length() < expectedEnd || !addressString.substring(auidOffset,expectedEnd).equals(getAUID())) {
			// does not matches, cancel initial delivering
			ies.setInitialEvent(false);
		}
		return ies;
	}

	// CHILD RELATIONS & RA ABSTRACTIONS
	// ################################################################

	public abstract ChildRelation getRequestProcessorChildRelation();

	protected RequestProcessorSbbLocalObject getRequestProcessor()
			throws InternalServerErrorException {
		// get the child relation
		ChildRelation childRelation = getRequestProcessorChildRelation();
		// creates the child sbb if does not exist
		if (childRelation.isEmpty()) {
			try {
				childRelation.create();
			} catch (Exception e) {
				logAsError("unable to create the child sbb.", e);
				throw new InternalServerErrorException("");
			}
		}
		// return the child sbb
		return (RequestProcessorSbbLocalObject) childRelation.iterator().next();
	}

	private void putAppUsageInCache() {		
		try {
			appUsageCache.put(getAppUsageFactory());
		} catch (InternalServerErrorException e) {
			logAsError("Unable to put app usage in cache", e);
		}		
	}

	private AppUsage borrowAppUsageFromCache(String auid)
			throws InternalServerErrorException {		
		try {
			// borrow app usage from cache
			return appUsageCache.borrow(auid);						
		} catch (Exception e) {
			logAsError("Unable to borrow app usage instance from cache", e);
			throw new InternalServerErrorException(
					"Unable to borrow app usage instance from cache");
		}		
	}

	private void releaseAppUsageToCache(AppUsage appUsage) {
		appUsageCache.release(appUsage);
	}

	private void removeAppUsageFromCache() {
		appUsageCache.remove(getAUID());
	}

	// ABSTRACT METHODS TO BE IMPLEMENTED
	// #########################################################

	public abstract Logger getLogger();

	public abstract String getAUID();

	public abstract AppUsageFactory getAppUsageFactory()
			throws InternalServerErrorException;

    // XCAP CAPS UPDATE
	
	private void updateXCAPCapsGlobalDoc() throws InternalServerErrorException {
		
		// we can't use the xcap caps app usage class, may not be loaded
		final String xcapCapsAUID = "xcap-caps";
		final String xcapCapsMimetype = "application/xcap-caps+xml";

		if (dataSource.containsAppUsage(xcapCapsAUID)) {
			// create xcap-caps global/index doc
			StringBuilder sb1 = new StringBuilder("<?xml version='1.0' encoding='UTF-8'?><xcap-caps xmlns='urn:ietf:params:xml:ns:xcap-caps'><auids>");
			StringBuilder sb2 = new StringBuilder("</auids><extensions/><namespaces>");
			AppUsage xcapCapsAppUsage = null;
			for(String auid:dataSource.getAppUsages()) {
				// borrow one app usage object from cache 
				AppUsage appUsage = borrowAppUsageFromCache(auid);
				// add auid and namespace
				if(appUsage != null) {
					sb1.append("<auid>").append(appUsage.getAUID()).append("</auid>");
					sb2.append("<namespace>").append(appUsage.getDefaultDocumentNamespace()).append("</namespace>");
					if (auid.equals(xcapCapsAUID)) {
						xcapCapsAppUsage = appUsage;
					}
					else {
						// release app usage object
						releaseAppUsageToCache(appUsage);
					}
				}
			}
			sb1.append(sb2).append("</namespaces></xcap-caps>");

			if (xcapCapsAppUsage != null) {
				try {
					getRequestProcessor().put(new ResourceSelector("/"+xcapCapsAUID+"/global/index",null), xcapCapsMimetype, new ByteArrayInputStream(sb1.toString().getBytes("utf-8")), null, xcapCapsAppUsage, ServerConfiguration.XCAP_ROOT);
				}
				catch (Exception e) {
					e.printStackTrace();
					throw new InternalServerErrorException("Failed to put xcap-caps global document. Cause: "+e.getCause()+" Message:"+e.getMessage());
				}
				// release app usage object
				releaseAppUsageToCache(xcapCapsAppUsage);
			}	
		}
		
	}
	
	// EVENT HANDLERS

	public void onServiceStartedEvent(
			javax.slee.serviceactivity.ServiceStartedEvent event,
			ActivityContextInterface aci) {

		try {
			// check if it's my service that is starting
			ServiceActivity sa = ((ServiceActivityFactory) myEnv
					.lookup("slee/serviceactivity/factory")).getActivity();
			if (sa.equals(aci.getActivity())) {
				logAsInfo("Application usage activated");
				// put app usage in cache
				putAppUsageInCache();
				if (isLogDebugEnabled()) logAsDebug("AppUsage cached");
				try {
					dataSource.addAppUsage(getAUID());
				}
				catch (Exception e) {
					logAsInfo("Failed to create the app usage in datasource, it may already exist. Error: "+e.getMessage());
				}
				// update xcap caps global doc
				try {
					updateXCAPCapsGlobalDoc();
				} catch (InternalServerErrorException e) {
					logger.error("failed to update xcap caps global doc", e);
				}
			} else {
				if (isLogDebugEnabled()) logAsDebug("Another service activated...");
				// we don't want to receive further events on this activity
				aci.detach(getSbbContext().getSbbLocalObject());
			}
		} catch (NamingException e) {
			logAsError("Can't handle service started event.", e);
		}
	}

	public void onActivityEndEvent(ActivityEndEvent event,
			ActivityContextInterface aci) {

		if (aci.getActivity() instanceof ServiceActivity) {
			// service activity ending
			if (isLogDebugEnabled()) logAsDebug("Service being deactivated...\n");
			// remove app usage from cache
			removeAppUsageFromCache();
			if (isLogDebugEnabled()) logAsDebug("AppUsage removed from cache...");
			// update xcap caps global doc
				try {
					updateXCAPCapsGlobalDoc();
				} catch (InternalServerErrorException e) {
					logger.error("failed to update xcap caps global doc", e);
				}
		}

	}

	public void onDelete(HttpServletRequestEvent event,
			ActivityContextInterface aci) {

		// detach from the activity
		aci.detach(sbbContext.getSbbLocalObject());

		HttpServletRequest request = event.getRequest();
		HttpServletResponse response = event.getResponse();
		
		AppUsage appUsage = null;
		boolean appUsageBorrowed = false;

		try {

			PrintWriter responseWriter = response.getWriter();
			try {

				// get xcap root from config
				String xcapRoot = ServerConfiguration.XCAP_ROOT;

				// create jxcap resource selector from request's uri & query
				// string
				ResourceSelector resourceSelector = Parser
						.parseResourceSelector(xcapRoot, request
								.getRequestURI(), request.getQueryString());

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

				// get app usage from cache
				appUsage = borrowAppUsageFromCache(getAUID());
				appUsageBorrowed = true;
				// delete in data source
				WriteResult result = getRequestProcessor().delete(
						resourceSelector, eTagValidator, appUsage, xcapRoot);
				// return app usage to cache
				releaseAppUsageToCache(appUsage);
				appUsageBorrowed = false;
				// set response status
				response.setStatus(result.getResponseStatus());
				// set response entity tag if provided
				if (result.getResponseEntityTag() != null) {
					response.setHeader(HttpConstant.HEADER_ETAG, result
							.getResponseEntityTag());
				}

			} catch (ParseException e) {
				NotFoundException ne = new NotFoundException();
				if (isLogDebugEnabled()) logAsDebug("invalid xcap uri, replying , replying "+ne.getResponseStatus());
				response.setStatus(ne.getResponseStatus());

			} catch (NotFoundException e) {
				if (isLogDebugEnabled()) logAsDebug("doc/elem/attrib not found, replying "+e.getResponseStatus());
				response.setStatus(e.getResponseStatus());

			} catch (ConflictException e) {
				if (isLogDebugEnabled()) logAsDebug("conflict exception, replying "+e.getResponseStatus());
				response.setStatus(e.getResponseStatus());
				responseWriter.print(e.getResponseContent());

			} catch (MethodNotAllowedException e) {
				if (isLogDebugEnabled()) logAsDebug("method not allowed, replying "+e.getResponseStatus());
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
				if (isLogDebugEnabled()) logAsDebug("precondition failed on etags, replying "+e.getResponseStatus());
				response.setStatus(e.getResponseStatus());

			} catch (InternalServerErrorException e) {
				getLogger().warn("internal server error: "+e.getMessage()+", replying "+e.getResponseStatus());
				response.setStatus(e.getResponseStatus());

			} catch (BadRequestException e) {
				if (isLogDebugEnabled()) logAsDebug("bad request, replying "+e.getResponseStatus());
				response.setStatus(e.getResponseStatus());
			}

			if(appUsageBorrowed) {
				// the app usage still borrowed, release it
				releaseAppUsageToCache(appUsage);
			}
			// send to client
			responseWriter.close();
			
		} catch (Exception e) {
			logAsError("Error processing onDelete()", e);
		}

	}

	public void onGet(HttpServletRequestEvent event,
			ActivityContextInterface aci) {

		// detach from the activity
		aci.detach(sbbContext.getSbbLocalObject());

		HttpServletRequest request = event.getRequest();
		HttpServletResponse response = event.getResponse();

		AppUsage appUsage = null;
		boolean appUsageBorrowed = false;
		
		try {

			PrintWriter responseWriter = response.getWriter();

			try {

				// create jxcap resource selector from request's uri & query
				// string
				ResourceSelector resourceSelector = Parser
						.parseResourceSelector(ServerConfiguration.XCAP_ROOT, request.getRequestURI(),
								request.getQueryString());
				// get app usage from cache
				appUsage = borrowAppUsageFromCache(getAUID());
				appUsageBorrowed = true;
				// read result from data source
				ReadResult result = getRequestProcessor().get(resourceSelector,
						appUsage);
				// return app usage to cache
				releaseAppUsageToCache(appUsage);
				appUsageBorrowed = false;
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
				getLogger().warn("invalid xcap uri, replying "+ne.getResponseStatus());
				response.setStatus(ne.getResponseStatus());

			} catch (NotFoundException e) {
			
				if (isLogDebugEnabled()) logAsDebug("doc/elem/attrib not found, replying "+e.getResponseStatus());
				response.setStatus(e.getResponseStatus());

			} catch (InternalServerErrorException e) {
				getLogger().warn("internal server error: ");
				response.setStatus(e.getResponseStatus());

			} catch (BadRequestException e) {
				if (isLogDebugEnabled()) logAsDebug("bad request, replying "+e.getResponseStatus());
				response.setStatus(e.getResponseStatus());
			}

			if(appUsageBorrowed) {
				// the app usage still borrowed, release it
				releaseAppUsageToCache(appUsage);
			}
			// send to client
			responseWriter.close();

		} catch (Exception e) {
			logAsError("Error processing onGet()", e);
		}

	}

	public void onPut(HttpServletRequestEvent event,
			ActivityContextInterface aci) {

		// detach from the activity
		aci.detach(sbbContext.getSbbLocalObject());

		HttpServletRequest request = event.getRequest();
		HttpServletResponse response = event.getResponse();
		
		AppUsage appUsage = null;
		boolean appUsageBorrowed = false;
		
		try {

			PrintWriter responseWriter = response.getWriter();

			try {

				// create resource selector from request's uri & query
				// string
				ResourceSelector resourceSelector = Parser
						.parseResourceSelector(ServerConfiguration.XCAP_ROOT, request
								.getRequestURI(), request.getQueryString());

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
				// get app usage from cache
				appUsage = borrowAppUsageFromCache(getAUID());
				appUsageBorrowed = true;
				// put object in data source
				WriteResult result = getRequestProcessor().put(
						resourceSelector, mimetype, request.getInputStream(),
						eTagValidator, appUsage, ServerConfiguration.XCAP_ROOT);
				// return app usage to cache
				releaseAppUsageToCache(appUsage);
				appUsageBorrowed = false;
				// set response status
				response.setStatus(result.getResponseStatus());
				// set response entity tag with new one on result
				response.setHeader(HttpConstant.HEADER_ETAG, result
						.getResponseEntityTag());

			} catch (ParseException e) {
				// invalid resource selector
				BadRequestException bre = new BadRequestException();
				if (isLogDebugEnabled()) logAsDebug("invalid xcap uri, replying "+bre.getResponseStatus());
				response.setStatus(bre.getResponseStatus());

			} catch (IOException e) {
				InternalServerErrorException ie = new InternalServerErrorException(
						e.getMessage());
				getLogger().warn("internal server error: "+e.getMessage()+", replying "+ie.getResponseStatus());
				response.setStatus(ie.getResponseStatus());

			} catch (NoParentConflictException e) {
					// add base uri
					e.setSchemeAndAuthorityURI(ServerConfiguration.SCHEME_AND_AUTHORITY_URI);
					// add query string if exists
					if (request.getQueryString() != null) {
						e.setQueryComponent(request.getQueryString());
					}
					if (isLogDebugEnabled()) logAsDebug("no parent conflict exception, replying "+e.getResponseStatus());
					response.setStatus(e.getResponseStatus());
					responseWriter.print(e.getResponseContent());
					
			} catch (ConflictException e) {
				if (isLogDebugEnabled()) logAsDebug("conflict exception, replying "+e.getResponseStatus());
				response.setStatus(e.getResponseStatus());
				responseWriter.print(e.getResponseContent());

			} catch (MethodNotAllowedException e) {
				if (isLogDebugEnabled()) logAsDebug("method not allowed, replying "+e.getResponseStatus());
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
				if (isLogDebugEnabled()) logAsDebug("unsupported media exception, replying "+e.getResponseStatus());
				response.setStatus(e.getResponseStatus());

			} catch (InternalServerErrorException e) {
				getLogger().warn("internal server error: "+e.getMessage()+", replying "+e.getResponseStatus());
				response.setStatus(e.getResponseStatus());

			} catch (PreconditionFailedException e) {
				if (isLogDebugEnabled()) logAsDebug("precondition failed on etags, replying "+e.getResponseStatus());
				response.setStatus(e.getResponseStatus());

			} catch (BadRequestException e) {
				if (isLogDebugEnabled()) logAsDebug("invalid xcap uri, replying "+e.getResponseStatus());
				response.setStatus(e.getResponseStatus());
			}

			if(appUsageBorrowed) {
				// the app usage still borrowed, release it
				releaseAppUsageToCache(appUsage);
			}			
			// send to client
			responseWriter.close();

		} catch (Exception e) {
			logAsError("Error processing onPut()", e);
		}

	}

	// ######################################## NOT SUPPORTED METHODS

	public void onPost(HttpServletRequestEvent event,
			ActivityContextInterface aci) {

		// detach from the activity
		aci.detach(sbbContext.getSbbLocalObject());

		// method not allowed, set right sc and allow header then send response
		try {
			HttpServletResponse response = event.getResponse();
			response.setStatus(MethodNotAllowedException.RESPONSE_STATUS);
			response.setHeader(HttpConstant.HEADER_ALLOW, "GET, PUT, DELETE");
			response.flushBuffer();
		} catch (Exception e) {
			logAsError("unable to send response", e);
		}

	}

	public void onHead(HttpServletRequestEvent event,
			ActivityContextInterface aci) {

		// detach from the activity
		aci.detach(sbbContext.getSbbLocalObject());

		// method not allowed, set right sc and allow header then send response
		try {
			HttpServletResponse response = event.getResponse();
			response.setStatus(MethodNotAllowedException.RESPONSE_STATUS);
			response.setHeader(HttpConstant.HEADER_ALLOW, "GET, PUT, DELETE");
			response.flushBuffer();
		} catch (Exception e) {
			logAsError("unable to send response", e);
		}

	}

	public void onOptions(HttpServletRequestEvent event,
			ActivityContextInterface aci) {

		// detach from the activity
		aci.detach(sbbContext.getSbbLocalObject());

		// method not allowed, set right sc and allow header then send response
		try {
			HttpServletResponse response = event.getResponse();
			response.setStatus(MethodNotAllowedException.RESPONSE_STATUS);
			response.setHeader(HttpConstant.HEADER_ALLOW, "GET, PUT, DELETE");
			response.flushBuffer();
		} catch (Exception e) {
			logAsError("unable to send response", e);
		}

	}

	public void onTrace(HttpServletRequestEvent event,
			ActivityContextInterface aci) {

		// detach from the activity
		aci.detach(sbbContext.getSbbLocalObject());

		// method not allowed, set right sc and allow header then send response
		try {
			HttpServletResponse response = event.getResponse();
			response.setStatus(MethodNotAllowedException.RESPONSE_STATUS);
			response.setHeader(HttpConstant.HEADER_ALLOW, "GET, PUT, DELETE");
			response.flushBuffer();
		} catch (Exception e) {
			logAsError("unable to send response", e);
		}

	}

}