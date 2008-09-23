package org.mobicents.slee.xdm.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.slee.ActivityContextInterface;
import javax.slee.ChildRelation;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;

import org.apache.log4j.Logger;
import org.openxdm.xcap.common.error.InternalServerErrorException;
import org.openxdm.xcap.common.error.RequestException;
import org.openxdm.xcap.common.key.XcapUriKey;
import org.openxdm.xcap.common.uri.DocumentSelector;
import org.openxdm.xcap.server.etag.ETagValidator;
import org.openxdm.xcap.server.etag.IfMatchETagValidator;
import org.openxdm.xcap.server.etag.IfNoneMatchETagValidator;
import org.openxdm.xcap.server.result.ReadResult;
import org.openxdm.xcap.server.result.WriteResult;
import org.openxdm.xcap.server.slee.RequestProcessorSbbLocalObject;
import org.openxdm.xcap.server.slee.resource.datasource.AppUsageActivity;
import org.openxdm.xcap.server.slee.resource.datasource.AttributeUpdatedEvent;
import org.openxdm.xcap.server.slee.resource.datasource.DataSourceActivityContextInterfaceFactory;
import org.openxdm.xcap.server.slee.resource.datasource.DataSourceSbbInterface;
import org.openxdm.xcap.server.slee.resource.datasource.DocumentActivity;
import org.openxdm.xcap.server.slee.resource.datasource.DocumentUpdatedEvent;
import org.openxdm.xcap.server.slee.resource.datasource.ElementUpdatedEvent;

public abstract class InternalXDMClientControlSbb implements Sbb, XDMClientControlSbbLocalObject {

	
	private static Logger logger = Logger.getLogger(InternalXDMClientControlSbb.class);
	private SbbContext sbbContext = null; // This SBB's context
	private DataSourceSbbInterface dataSourceSbbInterface = null;
	private DataSourceActivityContextInterfaceFactory dataSourceACIF = null;
	
	/**
	 * Called when an sbb object is created and enters the pooled state.
	 */
	public void setSbbContext(SbbContext sbbContext) {
		this.sbbContext = sbbContext;
		try {
			Context context = (Context) new InitialContext().lookup("java:comp/env");
			dataSourceSbbInterface = (DataSourceSbbInterface) context.lookup("slee/resources/xdm/datasource/sbbrainterface");
			dataSourceACIF = (DataSourceActivityContextInterfaceFactory) context.lookup("slee/resources/xdm/datasource/1.0/acif");
		} catch (NamingException e) {
			logger.error("Can't set sbb context.", e);
		}
	}
	
	public abstract ChildRelation getRequestProcessorChildRelation();

	protected RequestProcessorSbbLocalObject getRequestProcessor()
			throws InternalServerErrorException {
		// get the child relation
		ChildRelation childRelation = getRequestProcessorChildRelation();
		// creates the child sbb if does not exist
		if (childRelation.isEmpty()) {
			try {
				return (RequestProcessorSbbLocalObject) childRelation.create();
			} catch (Exception e) {
				logger.error("unable to create the child sbb.", e);
				throw new InternalServerErrorException("");
			}
		}
		else {
			// return the child sbb
			return (RequestProcessorSbbLocalObject) childRelation.iterator().next();
		}
	}
	
	// -- SBB LOCAL OBJECT METHODS
	
	private void delete(XcapUriKey key, ETagValidator eTagValidator) {
		int responseCode = -1;
		String eTag = null;
		try {
			WriteResult writeResult = getRequestProcessor().delete(key.getResourceSelector(), null, ServerConfiguration.XCAP_ROOT);
			responseCode =  writeResult.getResponseStatus();
			eTag = writeResult.getResponseEntityTag();
		} catch (RequestException e) {
			responseCode = e.getResponseStatus();
		} 
		getParentSbbCMP().deleteResponse(key,responseCode,eTag);		
	}
	
	public void delete(XcapUriKey key) {
		delete(key,null);		
	}	
	
	public void deleteIfMatch(XcapUriKey key, String tag) {
		delete(key,new IfMatchETagValidator(tag));					
	}
	
	public void deleteIfNoneMatch(XcapUriKey key, String tag) {
		delete(key,new IfNoneMatchETagValidator(tag));	
	}
	
	public void get(XcapUriKey key) {
		logger.info("Retrieveing "+key);
		int responseCode = -1;
		String mimetype = null;
		String content = null;
		String eTag = null;
		try {
			ReadResult readResult = getRequestProcessor().get(key.getResourceSelector());
			responseCode = 200;
			mimetype = readResult.getResponseDataObject().getMimetype();
			content = readResult.getResponseDataObject().toXML();
			eTag = readResult.getResponseEntityTag();			
		} catch (RequestException e) {
			logger.error(e);
			responseCode = e.getResponseStatus();
		} 
		getParentSbbCMP().getResponse(key, responseCode, mimetype, content, eTag);	
	}
	
	private void put(XcapUriKey key, String mimetype, byte[] content, ETagValidator eTagValidator) {
		ByteArrayInputStream bais = new ByteArrayInputStream(content);
		int responseCode = -1;
		String eTag = null;
		try {
			WriteResult writeResult = getRequestProcessor().put(key.getResourceSelector(), mimetype, bais, eTagValidator, ServerConfiguration.XCAP_ROOT);
			responseCode =  writeResult.getResponseStatus();
			eTag = writeResult.getResponseEntityTag();
		} catch (RequestException e) {
			responseCode = e.getResponseStatus();
		} 
		finally {
			try {
				bais.close();
			} catch (IOException e) {
				// ignore
				logger.error(e);
			}
		}
		getParentSbbCMP().putResponse(key, responseCode, eTag);
	}
	
	public void put(XcapUriKey key, String mimetype, byte[] content) {
		put(key, mimetype, content,null);		
	}
	
	public void putIfMatch(XcapUriKey key, String tag, String mimetype,
			byte[] content) {
		put(key, mimetype, content, new IfMatchETagValidator(tag));
	}
	
	public void putIfNoneMatch(XcapUriKey key, String tag, String mimetype,
			byte[] content) {
		put(key, mimetype, content, new IfNoneMatchETagValidator(tag));
	}
	
	// --- subscribe/unsubscribe interface methods
	
	public void setParentSbb(XDMClientControlParentSbbLocalObject parentSbb) {
		setParentSbbCMP(parentSbb);
	}
	
	public void subscribeDocument(DocumentSelector documentSelector) {
		try {
			DocumentActivity activity = dataSourceSbbInterface.createDocumentActivity(documentSelector);
			ActivityContextInterface aci = dataSourceACIF.getActivityContextInterface(activity);
			aci.attach(this.sbbContext.getSbbLocalObject());
			logger.info("Subscribed "+documentSelector);
		} catch (Exception e) {
			logger.error("failed to subscribe document resource", e);
		}
	}
	
	public void unsubscribeDocument(DocumentSelector documentSelector) {
		for(ActivityContextInterface aci : sbbContext.getActivities()){
			Object object = aci.getActivity();
			if (object instanceof DocumentActivity) {
				DocumentActivity activity = (DocumentActivity) object;
				if (activity.getDocumentSelector().equals(documentSelector.toString())) {
					aci.detach(sbbContext.getSbbLocalObject());
					activity.remove();
					logger.info("Unsubscribed "+documentSelector);
					return;
				}
			}
		}
		logger.info("Didn't unsubscribe, did not found subscription for "+documentSelector);
	}
	
	public void subscribeAppUsage(String auid) {
		try {
			AppUsageActivity activity = dataSourceSbbInterface.createAppUsageActivity(auid);
			ActivityContextInterface aci = dataSourceACIF.getActivityContextInterface(activity);
			aci.attach(this.sbbContext.getSbbLocalObject());
			logger.info("Subscribed "+auid);
		} catch (Exception e) {
			logger.error("failed to subscribe document resource", e);
		}
	}
	
	public void unsubscribeAppUsage(String auid) {
		for(ActivityContextInterface aci : sbbContext.getActivities()){
			Object object = aci.getActivity();
			if (object instanceof AppUsageActivity) {
				AppUsageActivity activity = (AppUsageActivity) object;
				if (activity.getAUID().equals(auid)) {
					aci.detach(sbbContext.getSbbLocalObject());
					activity.remove();
					logger.info("Unsubscribed "+auid);
					return;
				}
			}
		}
		logger.info("Didn't unsubscribe, did not found subscription for "+auid);
	}
	
	// EVENT HANDLER METHODS
	
	public void onAttributeUpdatedEvent(AttributeUpdatedEvent event, ActivityContextInterface aci) {
		logger.info("attribute updated on "+event.getDocumentSelector());
		getParentSbbCMP().attributeUpdated(event.getDocumentSelector(),event.getNodeSelector(),event.getAttributeSelector(),event.getNamespaces(),event.getOldETag(),event.getNewETag(),event.getDocumentAsString(),event.getAttributeValue());	
	}
	
	public void onDocumentUpdatedEvent(DocumentUpdatedEvent event, ActivityContextInterface aci) {
		logger.info("document updated on "+event.getDocumentSelector());
		getParentSbbCMP().documentUpdated(event.getDocumentSelector(),event.getOldETag(),event.getNewETag(),event.getDocumentAsString());
	}

	public void onElementUpdatedEvent(ElementUpdatedEvent event, ActivityContextInterface aci) {
		logger.info("element updated on "+event.getDocumentSelector());
		getParentSbbCMP().elementUpdated(event.getDocumentSelector(),event.getNodeSelector(),event.getNamespaces(),event.getOldETag(),event.getNewETag(),event.getDocumentAsString(),event.getElementAsString());
	}
	
	// CMP FIELDs
	
	public abstract void setParentSbbCMP(XDMClientControlParentSbbLocalObject parentSbb);
	public abstract XDMClientControlParentSbbLocalObject getParentSbbCMP();
	
	// SBB OBJECT LIFECYCLE METHODS
	
	public void sbbActivate() {}
	
	public void sbbCreate() throws CreateException {}
	
	public void sbbExceptionThrown(Exception arg0, Object arg1,
			ActivityContextInterface arg2) {}
	
	public void sbbLoad() {}
	
	public void sbbPassivate() {}
	
	public void sbbPostCreate() throws CreateException {}
	
	public void sbbRemove() {}
	
	public void sbbRolledBack(RolledBackContext arg0) {}
	
	public void sbbStore() {}
	
	public void unsetSbbContext() { this.sbbContext = null; }
	
}
