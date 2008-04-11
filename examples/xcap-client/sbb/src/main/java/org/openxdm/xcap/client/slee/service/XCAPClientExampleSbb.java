package org.openxdm.xcap.client.slee.service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.slee.ActivityContextInterface;
import javax.slee.ActivityEndEvent;
import javax.slee.RolledBackContext;
import javax.slee.SLEEException;
import javax.slee.SbbContext;
import javax.slee.TransactionRequiredLocalException;
import javax.slee.TransactionRolledbackLocalException;
import javax.slee.UnrecognizedActivityException;
import javax.slee.resource.ActivityAlreadyExistsException;
import javax.slee.resource.CouldNotStartActivityException;
import javax.slee.serviceactivity.ServiceActivity;
import javax.slee.serviceactivity.ServiceActivityFactory;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Logger;
import org.openxdm.xcap.client.Response;
import org.openxdm.xcap.client.appusage.resourcelists.EntryType;
import org.openxdm.xcap.client.appusage.resourcelists.ListType;
import org.openxdm.xcap.client.appusage.resourcelists.ObjectFactory;
import org.openxdm.xcap.client.appusage.resourcelists.ResourceListsUserElementUriKey;
import org.openxdm.xcap.client.key.UserDocumentUriKey;
import org.openxdm.xcap.client.key.UserElementUriKey;
import org.openxdm.xcap.client.slee.resource.ElementUriKeyActivity;
import org.openxdm.xcap.client.slee.resource.ResponseEvent;
import org.openxdm.xcap.client.slee.resource.XCAPClientActivityContextInterfaceFactory;
import org.openxdm.xcap.client.slee.resource.XCAPClientResourceAdaptorSbbInterface;
import org.openxdm.xcap.common.uri.ElementSelector;
import org.openxdm.xcap.common.uri.ElementSelectorStep;
import org.openxdm.xcap.common.uri.ElementSelectorStepByAttr;
import org.openxdm.xcap.common.xml.XMLValidator;

public abstract class XCAPClientExampleSbb implements javax.slee.Sbb {

	private SbbContext sbbContext = null; // This SBB's SbbContext		
	
	private Context myEnv = null;
	private XCAPClientResourceAdaptorSbbInterface ra = null;
	private XCAPClientActivityContextInterfaceFactory acif = null;
	
	private String userName = null;
	private String documentName = null;
			
	/**
	 * Called when an sbb object is instantied and enters the pooled state.
	 */
	public void setSbbContext(SbbContext context) { 
		this.sbbContext = context;
		try {
		myEnv = (Context) new InitialContext().lookup("java:comp/env");           
        ra = (XCAPClientResourceAdaptorSbbInterface) myEnv.lookup("slee/resources/xcapclient/1.0/sbbrainterface");
        acif = (XCAPClientActivityContextInterfaceFactory) myEnv.lookup("slee/resources/xcapclient/1.0/acif");  
        userName = (String) myEnv.lookup("USERNAME");
	    documentName = (String) myEnv.lookup("DOCUMENTNAME");
		}
		catch (NamingException e) {
			log.error("unable to set sbb context",e);
		}
	}
	
    public void unsetSbbContext() { this.sbbContext = null; }
    
    public void sbbCreate() throws javax.slee.CreateException {}
    public void sbbPostCreate() throws javax.slee.CreateException {}
    public void sbbActivate() {}
    public void sbbPassivate() {}
    public void sbbRemove() {}
    public void sbbLoad() {}    
    public void sbbStore() {}
    public void sbbExceptionThrown(Exception exception, Object event, ActivityContextInterface activity) {}
    public void sbbRolledBack(RolledBackContext context) {}
			
	protected SbbContext getSbbContext() {
		return sbbContext;
	}	
	
	/*
	 * Init the xmpp component connection when the service is activated by SLEE
	 */
	public void onServiceStartedEvent(javax.slee.serviceactivity.ServiceStartedEvent event, ActivityContextInterface aci) {		           
		try {
			//check if it's my service that is starting
            ServiceActivity sa = ((ServiceActivityFactory) myEnv.lookup("slee/serviceactivity/factory")).getActivity();                       
	    	if (sa.equals(aci.getActivity())) {	    						
	    		// do sync test
        		if (log.isDebugEnabled()) {
					log.debug("service started...");
				}
        		try {
        			syncTest();
        			asyncTest();
        		}
        		catch (Exception f) {
        			log.error("sync test failed...",f);
        		}
        		// start async test
        	}
		}
        catch (Exception e) {
        	log.error("unable to handle service started event...",e);        	
        }					
	}	
	
	public void syncTest() throws HttpException, IOException, JAXBException {
					
		// create uri		
		UserDocumentUriKey docKey = new UserDocumentUriKey("resource-lists",userName,documentName);
		
		// the doc to put
		String initialDocument =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<resource-lists xmlns=\"urn:ietf:params:xml:ns:resource-lists\">" +
				"<list name=\"friends\"/>" +
			"</resource-lists>";			
		
		String element = "<entry uri=\"sip:alice@example.com\"/>";
		
		String finalDocument =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<resource-lists xmlns=\"urn:ietf:params:xml:ns:resource-lists\">" +
				"<list name=\"friends\">" +
				element +
				"</list>" +
			"</resource-lists>";	
		
		// put the document and get sync response
		Response response = ra.putDocument(docKey,"application/resource-lists+xml",initialDocument);
		
		// check put response
		if (response != null) {
			if(response.getCode() == 200 || response.getCode() == 201) {
				log.info("document created in xcap server...");
			} else {
				log.error("bad response from xcap server: "+response.toString());
			}
		} else {
			log.error("unable to create document in xcap server...");
		}
					
		// let's create an uri selecting an element
		LinkedList<ElementSelectorStep> elementSelectorSteps = new LinkedList<ElementSelectorStep>();
		ElementSelectorStep step1 = new ElementSelectorStep("resource-lists");
		ElementSelectorStep step2 = new ElementSelectorStepByAttr("list","name","friends");
		ElementSelectorStep step3 = new ElementSelectorStepByAttr("entry","uri","sip:alice@example.com");
		elementSelectorSteps.add(step1);
		elementSelectorSteps.addLast(step2);
		elementSelectorSteps.addLast(step3);
		UserElementUriKey elemKey = new UserElementUriKey("resource-lists",userName,documentName,new ElementSelector(elementSelectorSteps),null);		
		
		// put the element and get sync response
		response = ra.putElement(elemKey,element);
		
		// check put response
		if (response != null) {
			if(response.getCode() == 201) {
				log.info("element created in xcap server...");
			} else {
				log.error("bad response from xcap server: "+response.toString());
			}
		} else {
			log.error("unable to create element in xcap server...");
		}
				
		// get the document and check content is ok
		response = ra.get(docKey);
		
		// check get response		
		if (response != null) {
			if(response.getCode() == 200 && XMLValidator.weaklyEquals((String)response.getContent(),finalDocument)) {
				log.info("document retreived in xcap server and content is the expected...");
				log.info("sync test suceed :)");
			} else {
				log.error("bad response from xcap server: "+response.toString());
			}
		} else {
			log.error("unable to retreive document in xcap server...");
		}	
							
	}
	
	public void asyncTest() throws ActivityAlreadyExistsException, CouldNotStartActivityException, NullPointerException, UnrecognizedActivityException, TransactionRequiredLocalException, TransactionRolledbackLocalException, HttpException, SLEEException, IllegalStateException, JAXBException, IOException {
		
		// now we will use marshalling and unmarshalling
						
		// let's create a list containing  someone
		ObjectFactory of = new ObjectFactory();
		ListType list = of.createListType();
		list.setName("enemies");
		EntryType entry = of.createEntryType();
		entry.setUri("sip:winniethepooh@disney.com");
		list.getListOrExternalOrEntry().add(entry);
		
		// create the key selecting the new element
		LinkedList<ElementSelectorStep> elementSelectorSteps = new LinkedList<ElementSelectorStep>();
		ElementSelectorStep step1 = new ElementSelectorStep("resource-lists");
		ElementSelectorStep step2 = new ElementSelectorStepByAttr("list","name","enemies");
		elementSelectorSteps.add(step1);
		elementSelectorSteps.addLast(step2);
		ResourceListsUserElementUriKey key = new ResourceListsUserElementUriKey(userName,documentName,new ElementSelector(elementSelectorSteps),null);		
		
		// first lets put the element using the sync interface
		Response response = ra.marshallAndPutElement(key,list);
		// check put response
		if (response != null) {
			if(response.getCode() == 201) {
				log.info("list element created in xcap server...");
			} else {
				log.error("bad response from xcap server: "+response.toString());
			}
		} else {
			log.error("unable to create list element in xcap server...");
		}
		
		// now lets get it using the async interface
		
		// get a request activity from the xcap client ra
		ElementUriKeyActivity activity = ra.createActivity(key);
		
		// attach this sbb entity to the activity's related aci 
		ActivityContextInterface aci = acif.getActivityContextInterface(activity);
		aci.attach(sbbContext.getSbbLocalObject());
		
		// send request
		activity.getAndUnmarshall();
		
		// the response will be asyncronous
	}
	
	/*
	 * ResponseEvent handler
	 */
	public void onResponseEvent(ResponseEvent event, ActivityContextInterface aci) {
		
		if (log.isDebugEnabled()) {
			log.debug("onResponseEvent(event="+event+",aci="+aci+")");
		}
		
		// check put response
		Response response = event.getResponse();
		if (response != null) {
			if(response.getCode() == 200) {
				log.info("list element retreived from xcap server...");
				// get content unmarshalled
				ListType list = (ListType) response.getContent();
				if(list.getName().equals("enemies")) {
					// check if it's winnie inside
					List l = list.getListOrExternalOrEntry();
					
					if(l.size() == 1) {
						EntryType entry = (EntryType)((JAXBElement)l.get(0)).getValue();
						if(entry.getUri().equals("sip:winniethepooh@disney.com")) {
							log.info("async test suceed :)");
						}
						else {
							log.error("list element retreived is not the expected one");
						}
					}
					else {
						log.error("list element retreived is not the expected one");
					}
				}				
				else {
					log.error("list element retreived is not the expected one");
				}
			} else {
				log.error("bad response from xcap server: "+response.toString());
			}
		} else {
			log.error("unable to create list element in xcap server...");
		}
		
		try {
			// delete the document
			ra.deleteDocument(new UserDocumentUriKey("resource-lists",userName,documentName));	
		}
		catch (Exception e) {
			log.error("failed to delete document",e);
		}
						
		// cleanup, end the activity
		ElementUriKeyActivity activity = (ElementUriKeyActivity)aci.getActivity();
		if (activity != null) {
			activity.endActivity();
		}
		
	}	
		
	/*
	 * Service deactivated.
	 */
	
	public void onActivityEndEvent(ActivityEndEvent event, ActivityContextInterface aci) {
		try {			
			//check if it's my service aci that is ending, in that case the connection is terminated
			ServiceActivity sa = ((ServiceActivityFactory) myEnv.lookup("slee/serviceactivity/factory")).getActivity();			
			if (sa.equals(aci.getActivity())) {
				if (log.isDebugEnabled()) {
					log.debug("service deactivated...");
				}				
			}						
		}
		catch (Exception e) {
			log.error("unable to handle activity end event...",e);			
		}
	}	
		
	private static Logger log = Logger.getLogger(XCAPClientExampleSbb.class);
	
}