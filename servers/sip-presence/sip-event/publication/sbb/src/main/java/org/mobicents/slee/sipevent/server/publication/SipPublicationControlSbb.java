package org.mobicents.slee.sipevent.server.publication;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sip.ListeningPoint;
import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.EventHeader;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.SIPIfMatchHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.slee.ActivityContextInterface;
import javax.slee.ActivityEndEvent;
import javax.slee.ChildRelation;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.SLEEException;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.SbbLocalObject;
import javax.slee.TransactionRequiredLocalException;
import javax.slee.serviceactivity.ServiceActivity;
import javax.slee.serviceactivity.ServiceActivityFactory;
import javax.slee.serviceactivity.ServiceStartedEvent;

import net.java.slee.resource.sip.SleeSipProvider;

import org.apache.log4j.Logger;
import org.mobicents.slee.sipevent.server.publication.jmx.PublicationControlManagement;
import org.mobicents.slee.sipevent.server.publication.jmx.PublicationControlManagementMBean;

/**
 * Sbb to control publication of sip events.
 * 
 * @author Eduardo Martins
 * 
 */
public abstract class SipPublicationControlSbb implements Sbb, PublicationClientControlParentSbbLocalObject {

	private static Logger logger = Logger.getLogger(SipPublicationControlSbb.class);	
	
	/**
	 * JAIN-SIP provider & factories
	 * 
	 * @return
	 */
	private SleeSipProvider sipProvider;
	private AddressFactory addressFactory;
	private MessageFactory messageFactory;
	private HeaderFactory headerFactory;
	
	/**
	 * SbbObject's sbb context
	 */
	protected SbbContext sbbContext;
	
	private Context jndiContext;
	
	/**
	 * SbbObject's context setting
	 */
	public void setSbbContext(SbbContext sbbContext) {
		this.sbbContext=sbbContext;
		// retrieve factories, facilities & providers
		try {
			jndiContext = (Context) new InitialContext().lookup("java:comp/env");			
			sipProvider = (SleeSipProvider)jndiContext.lookup("slee/resources/jainsip/1.2/provider");      
			addressFactory = sipProvider.getAddressFactory();
			headerFactory = sipProvider.getHeaderFactory();
			messageFactory = sipProvider.getMessageFactory();
		}
		catch (Exception e) {
			logger.error("Unable to retrieve factories, facilities & providers",e);			
		}
	}
		
	// --- INTERNAL CHILD SBB
	
	public abstract ChildRelation getPublicationControlChildRelation();
	public abstract PublicationControlSbbLocalObject getPublicationControlChildSbbCMP();
	public abstract void setPublicationControlChildSbbCMP(PublicationControlSbbLocalObject value);
	private PublicationControlSbbLocalObject getPublicationControlChildSbb() throws TransactionRequiredLocalException, SLEEException, CreateException {
		PublicationControlSbbLocalObject childSbb = getPublicationControlChildSbbCMP();
		if (childSbb == null) {
			childSbb = (PublicationControlSbbLocalObject) getPublicationControlChildRelation().create();
			setPublicationControlChildSbbCMP(childSbb);
			childSbb.setParentSbb((PublicationClientControlParentSbbLocalObject)this.sbbContext.getSbbLocalObject());
		}
		return childSbb;
	}
	
	// -- CONFIGURATION
	
	/**
	 * the Management MBean
	 */
	private static final PublicationControlManagement configuration = new PublicationControlManagement();
	
	/**
	 * Retrieves the current configuration for this component from an MBean
	 * @return
	 */
	public static PublicationControlManagementMBean getConfiguration() {
		return configuration;
	}
	
	// ----------- EVENT HANDLERS

	/**
	 * PUBLISH event processing
	 * 
	 * @param event
	 * @param aci
	 */
	public void onPublish(RequestEvent event,
			ActivityContextInterface aci) {

		// detach from aci, we don't want ot handle the activity end event
		SbbLocalObject sbbLocalObject = this.sbbContext.getSbbLocalObject();
		aci.detach(sbbLocalObject);
		
		if (logger.isDebugEnabled()) {
			logger.debug("Processing PUBLISH request...");
		}
		
		// get child sbb that handles all the publication logic
		PublicationControlSbbLocalObject childSbb = null;
		try	{
			childSbb = getPublicationControlChildSbb();						
		}
		catch (Exception e) {
			logger.error("Failed to get child sbb",e);
			try {
				// create response
				Response response = messageFactory.createResponse(Response.SERVER_INTERNAL_ERROR,event.getRequest());
				event.getServerTransaction().sendResponse(response);
				if (logger.isDebugEnabled()) {
					logger.debug("Response sent:\n"+response.toString());
				}
			}
			catch (Exception f) {
				logger.error("Can't send error response!",f);
			}
			return;
		}
		
		/*
		 * The presence of a body and the SIP-If-Match header field
		 * determine the specific operation that the request is performing,
		 * as described in Table 1.
		 * +-----------+-------+---------------+---------------+
		 * | Operation | Body? | SIP-If-Match? | Expires Value |
		 * +-----------+-------+---------------+---------------+
		 * | Initial   | yes   | no            | > 0           |
		 * | Refresh   | no    | yes           | > 0           |
		 * | Modify    | yes   | yes           | > 0           |
		 * | Remove    | no    | yes           | 0             |
		 * +-----------+-------+---------------+---------------+
		 *            Table 1: Publication Operations
		 *         
		 *  If expires does not exist then the service must choose it's value          
		 */
		
		// get event header
		EventHeader eventHeader = (EventHeader) event.getRequest().getHeader(
				EventHeader.NAME);
		
		if (eventHeader != null) {
			// check event package
			String eventPackage = eventHeader.getEventType();
			if (acceptsEventPackage(eventPackage,childSbb)) {

				URI entityURI = event.getRequest().getRequestURI();
				// The ESC inspects the Request-URI to determine whether this request
			    // is targeted to a resource for which the ESC is responsible for
			    // maintaining event state.  If not, the ESC MUST return a 404 (Not
			    // Found) response and skip the remaining steps.
				if (childSbb.isResponsibleForResource(entityURI)) {	
					
					// process expires header
					ExpiresHeader expiresHeader = event.getRequest().getExpires();
					int expires;

					// if expires does not exist then set it's value to default
					// value
					if (expiresHeader == null) {
						expires = getConfiguration().getDefaultExpires();
					} else {
						expires = expiresHeader.getExpires();
					}

					// check expires value
					if (expires > 0) {
						// check if expires is not less than the allowed min expires
						if (expires >= getConfiguration().getMinExpires()) {
							// ensure expires is not bigger than max expires
							if (expires > getConfiguration().getMaxExpires()) {
								expires = getConfiguration().getMaxExpires();
							}						
							String entity = entityURI.toString();
							// new publication or publication refresh ?	
							SIPIfMatchHeader sipIfMatchHeader = (SIPIfMatchHeader)event.getRequest().getHeader(SIPIfMatchHeader.NAME);
							if (sipIfMatchHeader != null) {
								// refresh or modification of publication
								if (event.getRequest().getContentLength().getContentLength() == 0) {
									// refreshing a publication
									childSbb.refreshPublication(event, entity, eventPackage, sipIfMatchHeader.getETag(), expires);
								}
								else {
									ContentTypeHeader contentTypeHeader = (ContentTypeHeader) event.getRequest().getHeader(ContentTypeHeader.NAME);
									if (childSbb.acceptsContentType(eventPackage,contentTypeHeader)) {
										// modification	
										childSbb.modifyPublication(event, entity, eventPackage, sipIfMatchHeader.getETag(), new String(event.getRequest().getRawContent()), contentTypeHeader.getContentType(), contentTypeHeader.getContentSubType(), expires);
									}
									else {
										// unsupported media type, send the ones supported
										sendErrorResponse(Response.UNSUPPORTED_MEDIA_TYPE,event.getRequest(),event.getServerTransaction(),eventPackage,childSbb);
									}
								}
							}
							else {
								// new publication
								if (event.getRequest().getContentLength().getContentLength() != 0) {
									ContentTypeHeader contentTypeHeader = (ContentTypeHeader) event.getRequest().getHeader(ContentTypeHeader.NAME);
									if (childSbb.acceptsContentType(eventPackage,contentTypeHeader)) {
										childSbb.newPublication(event, entityURI.toString(), eventPackage, new String(event.getRequest().getRawContent()), contentTypeHeader.getContentType(), contentTypeHeader.getContentSubType(), expires);
									}
									else {
										// unsupported media type, send the one supported
										sendErrorResponse(Response.UNSUPPORTED_MEDIA_TYPE,event.getRequest(),event.getServerTransaction(),eventPackage,childSbb);
									}
								}
								else {
									// send Bad Request since there is no content
									sendErrorResponse(Response.BAD_REQUEST,event.getRequest(),event.getServerTransaction(),eventPackage,childSbb);
								}
							}						
						} else {
							// expires is > 0 but < min expires, respond (Interval
							// Too Brief) with Min-Expires = MINEXPIRES
							sendErrorResponse(Response.INTERVAL_TOO_BRIEF, event
									.getRequest(), event.getServerTransaction(),eventPackage,childSbb);
						}
					}

					else if (expires == 0) {
						String entity = event.getRequest().getRequestURI().toString();
						SIPIfMatchHeader sipIfMatchHeader = (SIPIfMatchHeader)event.getRequest().getHeader(SIPIfMatchHeader.NAME);
						if (sipIfMatchHeader != null) {
							// remove publication
							childSbb.removePublication(event, entity, eventPackage, sipIfMatchHeader.getETag());														
						}
						else {
							// send Bad Request since removal requires etag
							sendErrorResponse(Response.BAD_REQUEST,event.getRequest(),event.getServerTransaction(),eventPackage,childSbb);						
						}							
					} else {
						// expires can't be negative
						sendErrorResponse(Response.BAD_REQUEST, event.getRequest(),
								event.getServerTransaction(),eventPackage,childSbb);
					}
				}
				else {
					// not responsible for this resource
					sendErrorResponse(Response.NOT_FOUND, event.getRequest(), event.getServerTransaction(), eventPackage,childSbb);
				}
			} else {
				// wrong event package, send bad event type error
				sendErrorResponse(Response.BAD_EVENT, event.getRequest(), event
						.getServerTransaction(),eventPackage,childSbb);
			}
		} else {
			// subscribe does not have a event header
			sendErrorResponse(Response.BAD_REQUEST, event.getRequest(), event
					.getServerTransaction(),null,childSbb);
		}

	}

	public void onOptions(RequestEvent requestEvent, ActivityContextInterface aci) {
		
		logger.info("options event received but server does not supports it");
		aci.detach(this.sbbContext.getSbbLocalObject());
		/*
		 * A client may probe the ESC for the support of PUBLISH using the
		 * OPTIONS request defined in SIP [4]. The ESC processes OPTIONS
		 * requests as defined in Section 11.2 of RFC 3261 [4]. In the response
		 * to an OPTIONS request, the ESC SHOULD include "PUBLISH" to the list
		 * of allowed methods in the Allow header field. Also, it SHOULD list
		 * the supported event packages in an Allow-Events header field.
		 * 
		 * The Allow header field may also be used to specifically announce
		 * support for PUBLISH messages when registering. (See SIP Capabilities
		 * [12] for details).
		 */
		
		// TODO 
		
	}
	
	public void onServiceStartedEvent(ServiceStartedEvent event, ActivityContextInterface aci) {
		// we want to stay attached to this service activity, to receive the activity end event on service deactivation
		try {                     
			//get this service activity
			ServiceActivity sa = ((ServiceActivityFactory) jndiContext.lookup("slee/serviceactivity/factory")).getActivity();                       
			if (!sa.equals(aci.getActivity())) {
				aci.detach(this.sbbContext.getSbbLocalObject());
			}
			else {
				// starts the mbean
				configuration.startService();
			}
		}
		catch (Exception e) {
			logger.error("failed to process service started event",e);
		}				
	}
	
	public void onActivityEndEvent(ActivityEndEvent event, ActivityContextInterface aci) {
		// shutdown internal interface
		try {
			getPublicationControlChildSbb().shutdown();
			// stop mbean
			configuration.stopService();
		} catch (Exception e) {
			logger.error("Faield to shutdown publication control",e);
		}
	}
	
	// ----------- CALL BACK METHODS FOR CHILD SBB
	
	public void modifyPublicationError(Object requestId, int error) {
		sendErrorResponse(requestId,error);
	}
	
	public void modifyPublicationOk(Object requestId, String tag, int expires) throws Exception {
		sendOkResponse((RequestEvent)requestId, tag, expires);
	}
	
	public void newPublicationError(Object requestId, int error) {
		sendErrorResponse(requestId,error);
	}
	
	public void newPublicationOk(Object requestId, String tag,int expires) throws Exception {
		sendOkResponse((RequestEvent)requestId, tag, expires);
	}
	
	public void refreshPublicationError(Object requestId, int error) {
		sendErrorResponse(requestId,error);
	}
	
	public void refreshPublicationOk(Object requestId, String tag, int expires) throws Exception {
		sendOkResponse((RequestEvent)requestId, tag, expires);
	}
	
	public void removePublicationError(Object requestId, int error) {
		sendErrorResponse(requestId,error);
	}
	
	public void removePublicationOk(Object requestId) throws Exception {
		sendOkResponse((RequestEvent)requestId, null, -1);
	}
	
	
	// ----------- AUX METHODS
	
	private void sendOkResponse(RequestEvent event,String eTag,int expires) throws Exception {
		// send 200 ok response	with expires and sipEtag				
		Response response = messageFactory.createResponse(Response.OK, event.getRequest());
		if (eTag != null) response.addHeader(headerFactory.createSIPETagHeader(eTag));
		if (expires != -1) response.addHeader(headerFactory.createExpiresHeader(expires));
		// Both 2xx response to SUBSCRIBE and NOTIFY need a Contact
		if (response.getHeader(ContactHeader.NAME) != null) {
			response.removeHeader(ContactHeader.NAME);
		}
		try {
			ListeningPoint listeningPoint = sipProvider.getListeningPoint("udp");
			Address address = addressFactory.createAddress(
					getConfiguration().getContactAddressDisplayName()+ " <sip:"
					+ listeningPoint.getIPAddress() + ">");
			((SipURI) address.getURI()).setPort(listeningPoint.getPort());
			response.addHeader(headerFactory.createContactHeader(address));
		} catch (Exception e) {
			logger.error("Can't add contact header", e);
		}
		event.getServerTransaction().sendResponse(response);
		if (logger.isDebugEnabled()) {
			logger.debug("Response sent:\n"+response.toString());		
		}
	}
	
	private void sendErrorResponse(Object requestId, int error) {
		try {
			RequestEvent event = (RequestEvent) requestId;
			sendErrorResponse(error,event.getRequest(), event.getServerTransaction(), ((EventHeader) event.getRequest().getHeader(
					EventHeader.NAME)).getEventType(), getPublicationControlChildSbb());
		} catch (Exception e) {
			logger.error("Failed to send error response",e);
		}
	}
	
	/*
	 * Sends an error response with the specified status code, adding additional
	 * headers if needed
	 */
	private void sendErrorResponse(int responseCode, Request request,
			ServerTransaction serverTransaction, String eventPackage, PublicationControlSbbLocalObject childSbb) {
		
		try {
			// create response
			Response response = messageFactory.createResponse(responseCode,request);
			// add headers if needed
			if (responseCode == Response.BAD_EVENT) {
				String allowEventsHeader = "";
				boolean first = true;
				for (String acceptedEventPackage : childSbb.getEventPackages()) {
					if (first) {
						allowEventsHeader += acceptedEventPackage;
					}
					else {
						allowEventsHeader += ","+acceptedEventPackage;
					}					
				}
				response
						.addHeader(headerFactory.createAllowEventsHeader(allowEventsHeader));
			}
			else if (responseCode == Response.INTERVAL_TOO_BRIEF)
				response.addHeader(headerFactory.createMinExpiresHeader(getConfiguration().getMinExpires()));
			else if (responseCode == Response.UNSUPPORTED_MEDIA_TYPE) 
				response.addHeader(childSbb.getAcceptsHeader(eventPackage));		

			serverTransaction.sendResponse(response);
			if (logger.isDebugEnabled()) {
				logger.debug("Response sent:\n"+response.toString());
			}
		}
		catch (Exception e) {
			logger.error("Can't send response!",e);
		}
	}	

	/**
	 * verifies if the specified event packaged is accepted
	 */
	private boolean acceptsEventPackage(String eventPackage,PublicationControlSbbLocalObject childSbb) {
		if (eventPackage != null) {			
			for(String acceptedEventPackage : childSbb.getEventPackages()) {
				if (eventPackage.equals(acceptedEventPackage)) {
					return true;
				}
			}
		}
		return false;
	}
	
	// ----------- SBB OBJECT's LIFE CYCLE
	
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