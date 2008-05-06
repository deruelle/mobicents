package org.openxdm.xcap.server.slee.appusage.xcapcaps;

import java.io.PrintWriter;
import java.net.URL;

import javax.slee.InitialEventSelector;
import javax.xml.validation.Schema;

import net.java.slee.resource.http.events.HttpServletRequestEvent;

import org.apache.log4j.Logger;
import org.openxdm.xcap.common.appusage.AppUsageFactory;
import org.openxdm.xcap.common.error.InternalServerErrorException;
import org.openxdm.xcap.common.error.NoParentConflictException;
import org.openxdm.xcap.common.xml.SchemaContext;
import org.openxdm.xcap.server.slee.AbstractAppUsageSbb;
import org.openxdm.xcap.server.slee.ServerConfiguration;

/**
 * JAIN SLEE Root Sbb for xcap-caps Xcap application usage.  
 * @author Eduardo Martins
 *
 */
public abstract class XCAPCapsAppUsageSbb extends AbstractAppUsageSbb {

	private static Logger logger = Logger
			.getLogger(XCAPCapsAppUsageSbb.class);

	// MANDATORY ABSTRACT METHODS IMPL FOR A APP USAGE ROOT SBB, AbstractAppUsageSbb will invoke them

	public Logger getLogger() {
		return logger;
	}

	private void replyToEventNotHandled(HttpServletRequestEvent event) {
		try {
		if(event.getRequest().getMethod().equalsIgnoreCase("DELETE") || event.getRequest().getMethod().equalsIgnoreCase("GET")) {
				event.getResponse().sendError(404);
			}
			else if(event.getRequest().getMethod().equalsIgnoreCase("PUT")) {
				NoParentConflictException e = new NoParentConflictException(ServerConfiguration.XCAP_ROOT);
				e.setSchemeAndAuthorityURI(ServerConfiguration.SCHEME_AND_AUTHORITY_URI);
					// add query string if exists
					if (event.getRequest().getQueryString() != null) {
						e.setQueryComponent(event.getRequest().getQueryString());
					}
					if (logger.isDebugEnabled()) logger.debug("no parent conflict exception, replying "+e.getResponseStatus());
					event.getResponse().setStatus(e.getResponseStatus());
					PrintWriter writer = event.getResponse().getWriter();
					writer.print(e.getResponseContent());
					writer.close();
			}
		}
		catch (Exception e) {
			logger.error("unable to reply to event that no service will handle",e);
		}
	}
	
	/**
	 * This is a special app usage, since it will also handle requests here for non existent app usages
	 */
	public InitialEventSelector initialEventSelector(InitialEventSelector ies) {
		if (ies.getEvent() instanceof HttpServletRequestEvent) {
			// check auid from address
			String addressString = ies.getAddress().getAddressString();
			// strip sugar piece from http servlet ra
			int auidOffset = "/mobicents/".length();
			if (addressString.length() > auidOffset) {
				addressString = addressString.substring(auidOffset);
				int nextSlash = addressString.indexOf('/');
				if(nextSlash < 1) {
					replyToEventNotHandled((HttpServletRequestEvent) ies.getEvent());
				}
				else {
					addressString = addressString.substring(0,nextSlash);
					if (addressString.equals(getAUID())) {
						return ies;
					} else {
						try {
							if (!getDataSource().containsAppUsage(addressString)) {
								replyToEventNotHandled((HttpServletRequestEvent) ies.getEvent());
							}
						} catch (InternalServerErrorException e) {
							logger.error("failed to check if app usage exists",e);
						}
					}
				}
			}
			else {
				replyToEventNotHandled((HttpServletRequestEvent) ies.getEvent());
			}
		}
		else {
			logger.warn("unexpected event class in initial event selector");
		}
		ies.setInitialEvent(false);
		return ies;
		
	}
	
	public AppUsageFactory getAppUsageFactory() throws InternalServerErrorException {

		getLogger().info("getAppUsageFactory()");

		AppUsageFactory appUsageFactory = null;

		try {
			// load schema files to dom documents
			logger.info("Loading schemas from file system...");
			URL schemaDirURL = this.getClass().getResource("xsd");
			if (schemaDirURL != null) {
				// create schema context
				SchemaContext schemaContext = SchemaContext
						.fromDir(schemaDirURL.toURI());
				// get schema from context
				Schema schema = schemaContext
						.getCombinedSchema(XCAPCapsAppUsage.DEFAULT_DOC_NAMESPACE);
				logger.info("Schemas loaded.");
				// create and return factory
				appUsageFactory = new XCAPCapsAppUsageFactory(schema);
				
			} else {
				logger.warn("Schemas dir resource not found!");
			}
		} catch (Exception e) {
			logger.error("Unable to load app usage schemas from file system", e);
		}

		if (appUsageFactory == null) {
			throw new InternalServerErrorException(
					"Unable to get app usage factory");
		} else {
			return appUsageFactory;
		}
	}

	public String getAUID() {
		return XCAPCapsAppUsage.ID;
	}

}