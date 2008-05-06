package org.mobicents.slee.xdm.server;

import java.io.StringReader;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.slee.ActivityContextInterface;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.openxdm.xcap.common.datasource.Document;
import org.openxdm.xcap.common.uri.DocumentSelector;
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
	
	/*
	 * JAXB context is thread safe
	 */
	private static final JAXBContext jaxbContext = initJAXBContext();
	private static JAXBContext initJAXBContext() {
		try {
			return JAXBContext.newInstance(
					"org.openxdm.xcap.client.appusage.resourcelists.jaxb" +
					":org.openxdm.xcap.client.appusage.rlsservices.jaxb" +
					":org.openxdm.xcap.client.appusage.presrules.jaxb.commonpolicy" +
					":org.openxdm.xcap.client.appusage.presrules.jaxb" +
					":org.openxdm.xcap.client.appusage.omapresrules.jaxb" +
					":org.openxdm.xcap.client.appusage.xcapcaps.jaxb");
		} catch (JAXBException e) {
			logger.error("failed to create jaxb context",e);
			return null;
		}
	}
	
	// -- SBB LOCAL OBJECT METHODS
	
	public Object getDocument(DocumentSelector documentSelector) {		
		
		logger.info("Retrieveing "+documentSelector);
		
		try {
			Document document = dataSourceSbbInterface.getDocument(documentSelector);
			if (document != null) {
				return unmarshallDocument(document.getAsString());
			}			
		} catch (Exception e) {
			logger.error("failed to get document from xdm datasource", e);
		}
		return null;
	}
	
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
		logger.info("Didn't unsubscribe, not found subscription for "+documentSelector);
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
		logger.info("Didn't unsubscribe, not found subscription for "+auid);
	}
	
	// EVENT HANDLER METHODS
	
	public void onAttributeUpdatedEvent(AttributeUpdatedEvent event, ActivityContextInterface aci) {
		logger.info("attribute updated on "+event.getDocumentSelector());
		warnParentSbbAboutDocumentUpdate(event.getDocumentSelector(), event.getDocumentAsString());		
	}
	
	public void onDocumentUpdatedEvent(DocumentUpdatedEvent event, ActivityContextInterface aci) {
		logger.info("document updated on "+event.getDocumentSelector());
		warnParentSbbAboutDocumentUpdate(event.getDocumentSelector(), event.getDocumentAsString());
	}

	public void onElementUpdatedEvent(ElementUpdatedEvent event, ActivityContextInterface aci) {
		logger.info("element updated on "+event.getDocumentSelector());
		warnParentSbbAboutDocumentUpdate(event.getDocumentSelector(), event.getDocumentAsString());
	}
	
	// CMP FIELDs
	
	public abstract void setParentSbbCMP(XDMClientControlParentSbbLocalObject parentSbb);
	public abstract XDMClientControlParentSbbLocalObject getParentSbbCMP();
	
	// AUX METHODS
	
	private Object unmarshallDocument(String document) throws JAXBException {
		// unmarshal doc
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		StringReader stringReader = new StringReader(document);
		Object documentUnmarshalled = unmarshaller.unmarshal(stringReader);
		stringReader.close();
		return documentUnmarshalled;
	}
	
	private void warnParentSbbAboutDocumentUpdate(DocumentSelector documentSelector, String document) {
		try {
			getParentSbbCMP().documentUpdated(documentSelector,unmarshallDocument(document));
		} catch (Exception e) {
			logger.error("failed to unmarshall document received from xdm datasource", e);
		}
	}
	
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
