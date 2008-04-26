package org.mobicents.slee.sipevent.server.publication;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipProvider;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.EventHeader;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.Header;
import javax.sip.header.HeaderFactory;
import javax.sip.header.SIPIfMatchHeader;
import javax.sip.header.UserAgentHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.slee.ActivityContextInterface;
import javax.slee.ActivityEndEvent;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.facilities.ActivityContextNamingFacility;
import javax.slee.facilities.TimerEvent;
import javax.slee.facilities.TimerFacility;
import javax.slee.facilities.TimerID;
import javax.slee.facilities.TimerOptions;
import javax.slee.facilities.TimerPreserveMissed;
import javax.slee.nullactivity.NullActivity;
import javax.slee.nullactivity.NullActivityContextInterfaceFactory;
import javax.slee.nullactivity.NullActivityFactory;
import javax.slee.serviceactivity.ServiceActivity;
import javax.slee.serviceactivity.ServiceActivityFactory;
import javax.slee.serviceactivity.ServiceStartedEvent;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.mobicents.slee.resource.persistence.ratype.PersistenceResourceAdaptorSbbInterface;
import org.mobicents.slee.resource.sip.SipActivityContextInterfaceFactory;
import org.mobicents.slee.resource.sip.SipResourceAdaptorSbbInterface;
import org.mobicents.slee.sipevent.server.publication.pojo.ComposedPublication;
import org.mobicents.slee.sipevent.server.publication.pojo.ComposedPublicationKey;
import org.mobicents.slee.sipevent.server.publication.pojo.Publication;
import org.mobicents.slee.sipevent.server.publication.pojo.PublicationKey;

/**
 * Sbb to control publication of sip events in a dialog
 * 
 * @author Eduardo Martins
 * 
 */
public abstract class PublicationControlSbb implements Sbb, PublicationControlSbbLocalObject {

	private static Logger logger = Logger.getLogger(PublicationControlSbb.class);	
	
	/**
	 * JAIN-SIP provider & factories
	 * 
	 * @return
	 */
	protected SipActivityContextInterfaceFactory sipActivityContextInterfaceFactory;
	protected SipProvider sipProvider;
	protected AddressFactory addressFactory;
	protected MessageFactory messageFactory;
	protected HeaderFactory headerFactory;
	
	/**
	 * Persistence RA sbb interface
	 */
	protected PersistenceResourceAdaptorSbbInterface persistenceResourceAdaptorSbbInterface;
	
	/**
	 * SLEE Facilities
	 */
	protected TimerFacility timerFacility;
	protected ActivityContextNamingFacility activityContextNamingfacility;
	protected NullActivityContextInterfaceFactory nullACIFactory;
	protected NullActivityFactory nullActivityFactory;
	
	/**
	 * SbbObject's sbb context
	 */
	protected SbbContext sbbContext;
	
	/**
	 * SbbObject's context setting
	 */
	public void setSbbContext(SbbContext sbbContext) {
		this.sbbContext=sbbContext;
		// retrieve factories, facilities & providers
		try {
			Context context = (Context) new InitialContext().lookup("java:comp/env");			
			timerFacility = (TimerFacility) context.lookup("slee/facilities/timer");
			nullACIFactory = (NullActivityContextInterfaceFactory) context
			.lookup("slee/nullactivity/activitycontextinterfacefactory");
			nullActivityFactory = (NullActivityFactory) context
			.lookup("slee/nullactivity/factory");
			sipActivityContextInterfaceFactory = (SipActivityContextInterfaceFactory)context.lookup("slee/resources/jainsip/1.2/acifactory");
			SipResourceAdaptorSbbInterface sipFactoryProvider = (SipResourceAdaptorSbbInterface)context.lookup("slee/resources/jainsip/1.2/provider");
			sipProvider = sipFactoryProvider.getSipProvider();            
			addressFactory = sipFactoryProvider.getAddressFactory();
			headerFactory = sipFactoryProvider.getHeaderFactory();
			messageFactory = sipFactoryProvider.getMessageFactory();
			persistenceResourceAdaptorSbbInterface = (PersistenceResourceAdaptorSbbInterface) context
			.lookup("slee/resources/pra/0.1/provider");
			activityContextNamingfacility = (ActivityContextNamingFacility) context.lookup("slee/facilities/activitycontextnaming");
		}
		catch (Exception e) {
			getLogger().error("Unable to retrieve factories, facilities & providers",e);			
		}
	}
	
	/**
	 * the impl class logger
	 * 
	 * @return
	 */
	protected Logger getLogger() {
		return logger;
	}

	/**
	 * the impl class SIP event packages supported
	 * 
	 * @return
	 */
	protected abstract String[] getEventPackages();
	

	/**
	 * get default publication time
	 * 
	 * @return
	 */
	protected int getDefaultExpires() {		
		return 3600;
	}

	/**
	 * get max publication time
	 * 
	 * @return
	 */
	protected int getMaxExpires() {
		return getDefaultExpires();
	}
	
	/**
	 * get min publication time
	 * 
	 * @return
	 */
	protected int getMinExpires() {
		return getDefaultExpires();
	}

	
	/**
	 * get the string to be used as sip address in contact headers
	 * 
	 * @return
	 */
	protected abstract String getContactAddressString();
	
	/**
	 * the Max-Forwards header value for generated NOTIFY requests
	 * @return
	 */
	protected int getMaxForwards() {
		return 70;
	}
	
	/**
	 * Verifies if the specified content type header can be accepted for the specified event package.
	 * @param eventPackage
	 * @param contentTypeHeader
	 * @return
	 */
	protected abstract boolean acceptsContentType(String eventPackage, ContentTypeHeader contentTypeHeader);
	
	/**
	 * Retrieves the accepted content types for the specified event package.
	 * @param eventPackage
	 * @return
	 */
	protected abstract Header getAcceptsHeader(String eventPackage);

	/**
	 * Notifies subscribers about a publication update for the specified entity
	 * regarding the specified evtnt package.
	 * @param composedPublication
	 */
	protected abstract void notifySubscribers(ComposedPublication composedPublication);
	
	/**
	 * Retrieves a JAXB Unmarshaller to parse a publication content. 
	 * @return 
	 */
	protected abstract Unmarshaller getUnmarshaller();
	
	/**
	 * Retrieves a JAXB Marshaller to convert a JAXBElement to a String. 
	 * @return
	 */
	protected abstract Marshaller getMarshaller();
	
	/**
	 * Combines a new publication with the current composed publication.
	 * @return the updated composed publication
	 */
	protected abstract ComposedPublication combinePublication(Publication publication, ComposedPublication composedPublication);
	
	/**
	 * Checks if this server is responsible for the resource
	 * publishing state.
	 * 
	 */
	protected abstract boolean isResponsibleForResource(URI uri);
	
	// ----------- EVENT HANDLERS

	/**
	 * PUBLISH event processing
	 * 
	 * @param event
	 * @param aci
	 */
	public void onPublish(RequestEvent event,
			ActivityContextInterface aci) {

		aci.detach(this.sbbContext.getSbbLocalObject());
		
		EntityManager entityManager = getEntityManager();
		
		// if exists remove UserAgent header
		if (event.getRequest().getHeader(UserAgentHeader.NAME) != null)
			event.getRequest().removeHeader(UserAgentHeader.NAME);

		getLogger().info("Processing PUBLISH request...");

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
			if (acceptsEventPackage(eventPackage)) {

				URI entityURI = event.getRequest().getRequestURI();
				// The ESC inspects the Request-URI to determine whether this request
			    // is targeted to a resource for which the ESC is responsible for
			    // maintaining event state.  If not, the ESC MUST return a 404 (Not
			    // Found) response and skip the remaining steps.
				if (isResponsibleForResource(entityURI)) {	
					
					// process expires header
					ExpiresHeader expiresHeader = event.getRequest().getExpires();
					int expires;

					// if expires does not exist then set it's value to default
					// value
					if (expiresHeader == null) {
						expires = getDefaultExpires();
					} else {
						expires = expiresHeader.getExpires();
					}

					// check expires value
					if (expires > 0) {
						// check if expires is not less than the allowed min expires
						if (expires >= getMinExpires()) {
							// ensure expires is not bigger than max expires
							if (expires > getMaxExpires()) {
								expires = getMaxExpires();
							}						
							String entity = entityURI.toString();
							// new publication or publication refresh ?	
							SIPIfMatchHeader sipIfMatchHeader = (SIPIfMatchHeader)event.getRequest().getHeader(SIPIfMatchHeader.NAME);
							if (sipIfMatchHeader != null) {
								// refresh or modification of publication
								// get publication
								Publication publication = getPublication(entityManager,sipIfMatchHeader.getETag(),entity,eventPackage);
								if (publication != null) {
									// publication exists
									if (event.getRequest().getContentLength().getContentLength() == 0) {
										// refreshing a publication
										refreshPublication(entityManager,event,publication,expires);
									}
									else {
										ContentTypeHeader contentTypeHeader = (ContentTypeHeader) event.getRequest().getHeader(ContentTypeHeader.NAME);
										if (acceptsContentType(eventPackage,contentTypeHeader)) {
											// modification	
											modifyPublication(entityManager,event,publication,expires,contentTypeHeader);
										}
										else {
											// unsupported media type, send the ones supported
											sendResponse(Response.UNSUPPORTED_MEDIA_TYPE,event.getRequest(),event.getServerTransaction(),eventPackage);
										}
									}
								}
								else {
									// there is no presence with that etag
									sendResponse(Response.CONDITIONAL_REQUEST_FAILED,event.getRequest(),event.getServerTransaction(),eventPackage);
								}
							}
							else {
								// new publication
								if (event.getRequest().getContentLength().getContentLength() != 0) {
									ContentTypeHeader contentTypeHeader = (ContentTypeHeader) event.getRequest().getHeader(ContentTypeHeader.NAME);
									if (acceptsContentType(eventPackage,contentTypeHeader)) {
										newPublication(entityManager,event,eventPackage,expires,entity,contentTypeHeader);
									}
									else {
										// unsupported media type, send the one supported
										sendResponse(Response.UNSUPPORTED_MEDIA_TYPE,event.getRequest(),event.getServerTransaction(),eventPackage);
									}
								}
								else {
									// send Bad Request since there is no content
									sendResponse(Response.BAD_REQUEST,event.getRequest(),event.getServerTransaction(),eventPackage);
								}
							}						
						} else {
							// expires is > 0 but < min expires, respond (Interval
							// Too Brief) with Min-Expires = MINEXPIRES
							sendResponse(Response.INTERVAL_TOO_BRIEF, event
									.getRequest(), event.getServerTransaction(),eventPackage);
						}
					}

					else if (expires == 0) {
						String presentity = event.getRequest().getRequestURI().toString();
						SIPIfMatchHeader sipIfMatchHeader = (SIPIfMatchHeader)event.getRequest().getHeader(SIPIfMatchHeader.NAME);
						if (sipIfMatchHeader != null) {
							// refresh or modification of publication
							// get publication
							Publication publication = getPublication(entityManager,sipIfMatchHeader.getETag(),presentity,eventPackage);
							if (publication != null) {
								// publication exists
								removePublication(publication,event,entityManager);							
							}
							else {
								// there is no presence with that etag
								sendResponse(Response.CONDITIONAL_REQUEST_FAILED,event.getRequest(),event.getServerTransaction(),eventPackage);
							}
						}
						else {
							// send Bad Request since removal requires etag
							sendResponse(Response.BAD_REQUEST,event.getRequest(),event.getServerTransaction(),eventPackage);						
						}							
					} else {
						// expires can't be negative
						sendResponse(Response.BAD_REQUEST, event.getRequest(),
								event.getServerTransaction(),eventPackage);
					}
				}
				else {
					// not responsible for this resource
					sendResponse(Response.NOT_FOUND, event.getRequest(), event.getServerTransaction(), eventPackage);
				}
			} else {
				// wrong event package, send bad event type error
				sendResponse(Response.BAD_EVENT, event.getRequest(), event
						.getServerTransaction(),eventPackage);
			}
		} else {
			// subscribe does not have a event header
			sendResponse(Response.BAD_REQUEST, event.getRequest(), event
					.getServerTransaction(),null);
		}

		entityManager.flush();
		entityManager.close();
	}

	// --- NEW PUBLICATION
	
	private void newPublication(EntityManager entityManager,
			RequestEvent event, String eventPackage, int expires,
			String entity, ContentTypeHeader contentTypeHeader) {
		
		getLogger().info("newPublication(entity="+entity+",eventPackage="+eventPackage+",expires="+expires+")");
		
		NullActivity nullActivity = null;
		try {			
			// get document
			String rawContent = new String(event.getRequest().getRawContent());
			
			// unmarshall document
			StringReader publicationStringReader = new StringReader(rawContent);
			JAXBElement unmarshalledContent = null;
			try {
				unmarshalledContent = (JAXBElement) getUnmarshaller().unmarshal(publicationStringReader);
			}
			catch (JAXBException e) {
				getLogger().error("failed to parse publication content",e);
				// If the content type of the request does
			    //  not match the event package, or is not understood by the ESC, the
			    //  ESC MUST reject the request with an appropriate response, such as
			    //  415 (Unsupported Media Type)
				sendResponse(Response.UNSUPPORTED_MEDIA_TYPE, event.getRequest(), event.getServerTransaction(), eventPackage);
				return;
			}
			publicationStringReader.close();
			
			// authorize publication
			// TODO

			// create SIP-ETag
			String eTag = ETagGenerator.generate(entity, eventPackage);

			// create publication pojo
			PublicationKey publicationKey = new PublicationKey(eTag,entity,eventPackage);
			Publication publication = new Publication(publicationKey,rawContent,contentTypeHeader.getContentType(),contentTypeHeader.getContentSubType());
			publication.setUnmarshalledContent(unmarshalledContent);

			// send 200 ok response	with expires and sipEtag				
			Response response = messageFactory.createResponse(Response.OK, event.getRequest());
			response.addHeader(headerFactory.createSIPETagHeader(eTag));
			response.addHeader(headerFactory.createExpiresHeader(expires));
			event.getServerTransaction().sendResponse(response);
			getLogger().info("Response sent:\n"+response.toString());		
			
			// set a timer for this publication and store it in the publication pojo
			// create null aci
			nullActivity = nullActivityFactory.createNullActivity();
			ActivityContextInterface aci = nullACIFactory.getActivityContextInterface(nullActivity);
			// attach to aci
			aci.attach(sbbContext.getSbbLocalObject());
			// set timer with 5 secs more
			TimerOptions options = new TimerOptions();			
			options.setPersistent(true);
			options.setPreserveMissed(TimerPreserveMissed.ALL);		
			TimerID timerID = timerFacility.setTimer(aci, null, System.currentTimeMillis() + ((expires+5) * 1000), 1, 1, options);
			// bind a name to the aci
			activityContextNamingfacility.bind(aci,publication.getPublicationKey().toString());
			publication.setTimerID(timerID);		
			
			// compose document			
			ComposedPublication composedPublication = getUpdatedComposedPublication(entityManager,publication);
			
			// persist data
			entityManager.persist(publication);	
			entityManager.persist(composedPublication);
			
			// notify subscribers			
			notifySubscribers(composedPublication);			
		}		
		catch (Exception e) {
			getLogger().error("failed to create publication",e);
			// try to send server error
			try {
				Response response = messageFactory.createResponse(Response.SERVER_INTERNAL_ERROR, event.getRequest());
				event.getServerTransaction().sendResponse(response);
				getLogger().info("Response sent:\n"+response.toString());
			}
			catch (Exception f) {
				getLogger().info("Can't send response!",f);				
			}
			// end null activity if needed
			if (nullActivity != null) {
				nullActivity.endActivity();
			}
		}
	}
	
	private ComposedPublication getUpdatedComposedPublication(
			EntityManager entityManager, Publication publication) throws JAXBException, IOException {

		ComposedPublication composedPublication = getComposedPublication(entityManager, publication.getPublicationKey().getEntity(), publication.getPublicationKey().getEventPackage());
		if (composedPublication == null) {
			ComposedPublicationKey key = new ComposedPublicationKey(publication.getPublicationKey().getEntity(), publication.getPublicationKey().getEventPackage());
			composedPublication = new ComposedPublication(key,publication.getDocument(),publication.getContentType(),publication.getContentSubType());
			composedPublication.setUnmarshalledContent(publication.getUnmarshalledContent());
		}
		else {
			composedPublication = combinePublication(publication, composedPublication);
		}
		return composedPublication;
	}

	private void refreshPublication(EntityManager entityManager,RequestEvent event,Publication publication,int expires) {

		getLogger().info("refreshPublication(publication="+publication.getPublicationKey()+",expires="+expires+")");
		
		try {
			// cancel current timer
			timerFacility.cancelTimer(publication.getTimerID());
			// remove old publication
			entityManager.remove(publication);
			// create new SIP-ETag
			String eTag = ETagGenerator.generate(publication.getPublicationKey().getEntity(),publication.getPublicationKey().getEventPackage());
			// create new publication pojo
			PublicationKey newPublicationKey = new PublicationKey(eTag,publication.getPublicationKey().getEntity(),publication.getPublicationKey().getEventPackage());
			Publication newPublication = new Publication(newPublicationKey,publication.getDocument(),publication.getContentType(),publication.getContentSubType());
			// send 200 ok response	with expires and sipEtag				
			Response response = messageFactory.createResponse(Response.OK, event.getRequest());
			response.addHeader(headerFactory.createSIPETagHeader(eTag));
			response.addHeader(headerFactory.createExpiresHeader(expires));
			event.getServerTransaction().sendResponse(response);
			getLogger().info("Response sent:\n"+response.toString());		
			// set a timer for this publication and store it in the new publication pojo
			// get timer aci
			ActivityContextInterface aci = activityContextNamingfacility.lookup(publication.getPublicationKey().toString());
			// change aci name
			activityContextNamingfacility.unbind(publication.getPublicationKey().toString());
			activityContextNamingfacility.bind(aci,newPublication.getPublicationKey().toString());
			// set timer with 5 secs more
			TimerOptions options = new TimerOptions();			
			options.setPersistent(true);
			options.setPreserveMissed(TimerPreserveMissed.ALL);		
			TimerID newTimerID = timerFacility.setTimer(aci, null, System.currentTimeMillis() + ((expires+5) * 1000), 1, 1, options);
			newPublication.setTimerID(newTimerID);						
			// persist new publication
			entityManager.persist(newPublication);						
		}
		catch (Exception e) {
			getLogger().error("failed to refresh publication",e);
			// try to send server error
			try {
				Response response = messageFactory.createResponse(Response.SERVER_INTERNAL_ERROR, event.getRequest());
				event.getServerTransaction().sendResponse(response);
				getLogger().info("Response sent:\n"+response.toString());
			}
			catch (Exception f) {
				getLogger().info("Can't send response!",f);				
			}
		}
	}
	
	private void removePublication(Publication publication, RequestEvent event,
			EntityManager entityManager) {
		
		getLogger().info("removePublication(publication="+publication.getPublicationKey()+")");
		
		try {
			// cancel current timer
			timerFacility.cancelTimer(publication.getTimerID());
			// lookup timer aci
			String aciName = publication.getPublicationKey().toString();
			ActivityContextInterface aci = activityContextNamingfacility.lookup(aciName);
			// unbind name
			activityContextNamingfacility.unbind(aciName);
			// detach from it, impliclt the aci ends
			aci.detach(sbbContext.getSbbLocalObject());
			// remove old publication
			entityManager.remove(publication);
			// send 200 ok response	with expires and sipEtag				
			Response response = messageFactory.createResponse(Response.OK, event.getRequest());
			response.addHeader(headerFactory.createSIPETagHeader(publication.getPublicationKey().getETag()));
			response.addHeader(headerFactory.createExpiresHeader(0));
			event.getServerTransaction().sendResponse(response);
			getLogger().info("Response sent:\n"+response.toString());
			// we need to re-compose all publications except the one being removed
			ComposedPublication composedPublication = removeFromComposedPublication(entityManager, publication);			
			entityManager.flush();
			// notify subscribers
			notifySubscribers(composedPublication);
		}
		catch (Exception e) {
			getLogger().error("failed to remove publication",e);
			// try to send server error
			try {
				Response response = messageFactory.createResponse(Response.SERVER_INTERNAL_ERROR, event.getRequest());
				event.getServerTransaction().sendResponse(response);
				getLogger().info("Response sent:\n"+response.toString());
			}
			catch (Exception f) {
				getLogger().info("Can't send response!",f);				
			}
		}		
	}

	private void modifyPublication(EntityManager entityManager,RequestEvent event, Publication publication, int expires, ContentTypeHeader contentTypeHeader) {
		
		getLogger().info("modifyPublication(publication="+publication.getPublicationKey()+",expires="+expires+")");
		
		try {
			// cancel current timer
			timerFacility.cancelTimer(publication.getTimerID());
			
			// remove current publication
			entityManager.remove(publication);
			
			// get document
			String rawContent = new String(event.getRequest().getRawContent());
			
			// unmarshall document
			StringReader publicationStringReader = new StringReader(rawContent);
			JAXBElement unmarshalledContent = null;
			try {
				unmarshalledContent = (JAXBElement) getUnmarshaller().unmarshal(publicationStringReader);				
			}
			catch (JAXBException e) {
				getLogger().error("failed to parse publication content",e);
				// If the content type of the request does
			    //  not match the event package, or is not understood by the ESC, the
			    //  ESC MUST reject the request with an appropriate response, such as
			    //  415 (Unsupported Media Type)
				sendResponse(Response.UNSUPPORTED_MEDIA_TYPE, event.getRequest(), event.getServerTransaction(), publication.getPublicationKey().getEventPackage());
				return;
			}
			publicationStringReader.close();
			
			// authorize publication
			// TODO

			// create new SIP-ETag
			String eTag = ETagGenerator.generate(publication.getPublicationKey().getEntity(),publication.getPublicationKey().getEventPackage());
			// create new publication pojo with new key and document
			PublicationKey newPublicationKey = new PublicationKey(eTag,publication.getPublicationKey().getEntity(),publication.getPublicationKey().getEventPackage());
			Publication newPublication = new Publication(newPublicationKey,rawContent,contentTypeHeader.getContentType(),contentTypeHeader.getContentSubType());
			newPublication.setUnmarshalledContent(unmarshalledContent);
			
			// send 200 ok response	with expires and sipEtag				
			Response response = messageFactory.createResponse(Response.OK, event.getRequest());
			response.addHeader(headerFactory.createSIPETagHeader(eTag));
			response.addHeader(headerFactory.createExpiresHeader(expires));
			event.getServerTransaction().sendResponse(response);
			getLogger().info("Response sent:\n"+response.toString());		
			// set a timer for this publication and store it in the new publication pojo
			// get timer aci
			ActivityContextInterface aci = activityContextNamingfacility.lookup(publication.getPublicationKey().toString());
			// change aci name
			activityContextNamingfacility.unbind(publication.getPublicationKey().toString());
			activityContextNamingfacility.bind(aci,newPublication.getPublicationKey().toString());
			// set timer with 5 secs more
			TimerOptions options = new TimerOptions();			
			options.setPersistent(true);
			options.setPreserveMissed(TimerPreserveMissed.ALL);		
			TimerID newTimerID = timerFacility.setTimer(aci, null, System.currentTimeMillis() + ((expires+5) * 1000), 1, 1, options);
			newPublication.setTimerID(newTimerID);				
			
			// compose document			
			ComposedPublication composedPublication = getUpdatedComposedPublication(entityManager,newPublication);
						
			// persist data			
			entityManager.persist(newPublication);	
			entityManager.persist(composedPublication);			
			// notify subscribers
			notifySubscribers(composedPublication);	
		}
		catch (Exception e) {
			getLogger().error("failed to refresh publication",e);
			// try to send server error
			try {
				Response response = messageFactory.createResponse(Response.SERVER_INTERNAL_ERROR, event.getRequest());
				event.getServerTransaction().sendResponse(response);
				getLogger().info("Response sent:\n"+response.toString());
			}
			catch (Exception f) {
				getLogger().info("Can't send response!",f);				
			}
		}
		
	}
	
	/**
	 * a timer has occurred in a dialog regarding a publication
	 * @param event
	 * @param aci
	 */
	public void onTimerEvent(TimerEvent event, ActivityContextInterface aci) {
		
		// cancel current timer
		timerFacility.cancelTimer(event.getTimerID());
		// detach from aci
		aci.detach(this.sbbContext.getSbbLocalObject());
		// end it
		((NullActivity)aci.getActivity()).endActivity();
		// create jpa entity manager
		EntityManager entityManager = getEntityManager();
		// get publication
		Publication publication = getPublication(entityManager, event.getTimerID());
		
		if (publication != null) {
			
			getLogger().info("Timer expired for "+publication.getPublicationKey());
			try {								
				// remove publication
				entityManager.remove(publication);
				// we need to re-compose all publications except the one being removed
				ComposedPublication composedPublication = removeFromComposedPublication(entityManager, publication);
				entityManager.flush();
				// notify subscribers
				notifySubscribers(composedPublication);	
			}
			catch (Exception e) {
				getLogger().error("failed to remove publication that expired",e);
			}		
		}
		
		entityManager.close();		
	}

	public void onOptions(RequestEvent requestEvent, ActivityContextInterface aci) {
		
		getLogger().info("processing options event");
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
			Context myEnv = (Context) new InitialContext().lookup("java:comp/env");                     
			//get this service activity
			ServiceActivity sa = ((ServiceActivityFactory) myEnv.lookup("slee/serviceactivity/factory")).getActivity();                       
			if (!sa.equals(aci.getActivity())) {
				aci.detach(this.sbbContext.getSbbLocalObject());
			}
		}
		catch (Exception e) {
			getLogger().error("failed to process service started event",e);
		}				
	}
	
	public void onActivityEndEvent(ActivityEndEvent event, ActivityContextInterface aci) {
		// close entity manager factory on service deactivation
		Object activity = aci.getActivity();		
		if (activity instanceof ServiceActivity) {
			entityManagerFactory.close();
		}
	}
	
	// ----------- SBB LOCAL OBJECT
	
	public ComposedPublication getComposedPublication(String entity, String eventPackage) {		
		// create jpa entity manager
		EntityManager entityManager = getEntityManager();
		// get composed document
		ComposedPublication composedPublication = getComposedPublication(entityManager, entity, eventPackage);
		if (composedPublication != null && composedPublication.getDocument() != null) {
			// unmarshalled content if needed
			// content needs to unmarshalled
			StringReader stringReader = new StringReader(composedPublication.getDocument());
			try {
				composedPublication.setUnmarshalledContent((JAXBElement)getUnmarshaller().unmarshal(stringReader));
			} catch (JAXBException e) {
				getLogger().error("failed to unmarshall content of "+composedPublication.getComposedPublicationKey(),e);
				composedPublication = null;
			}
			stringReader.close();
		}
		// close entity manager
		entityManager.close();
		return composedPublication;
	}
	
	// ----------- AUX METHODS

	private ComposedPublication removeFromComposedPublication(
			EntityManager entityManager, Publication publication) {
		// get composed publication
		ComposedPublication composedPublication = getComposedPublication(entityManager,publication.getPublicationKey().getEntity(), publication.getPublicationKey().getEventPackage());
		// reset content
		composedPublication.setDocument(null);
		composedPublication.setContentType(null);
		composedPublication.setContentSubType(null);
		// process existing publications
		List resultList = entityManager
		.createQuery("SELECT p FROM Publication p WHERE p.publicationKey.entity = :entity AND p.publicationKey.eventPackage = :eventPackage")
		.setParameter("entity",publication.getPublicationKey().getEntity())
		.setParameter("eventPackage",publication.getPublicationKey().getEventPackage())
		.getResultList();
		for (Iterator i = resultList.iterator();i.hasNext();) {
			Publication otherPublication = (Publication)i.next();
			if (otherPublication.getPublicationKey().getETag().equals(publication.getPublicationKey().getETag())) {
				// it's not the publication being removed
				if (composedPublication.getDocument() == null) {
					composedPublication.setDocument(otherPublication.getDocument());
					composedPublication.setContentType(otherPublication.getContentType());
					composedPublication.setContentSubType(otherPublication.getContentSubType());
				}
				else {
					// combine
					composedPublication = combinePublication(otherPublication, composedPublication);
				}
			}
		}
		if (composedPublication.getDocument() == null) {
			// no publications other than the one being removed, remove composed publication too
			entityManager.remove(composedPublication);
		}
		return composedPublication;
	}
	
	private Publication getPublication(EntityManager entityManager, String eTag, String entity,
			String eventPackage) {
		try {
			return (Publication) entityManager
			.createQuery("SELECT p FROM Publication p WHERE p.publicationKey.eTag = :eTag AND p.publicationKey.entity = :entity AND p.publicationKey.eventPackage = :eventPackage")
			.setParameter("eTag",eTag)
			.setParameter("entity",entity)
			.setParameter("eventPackage",eventPackage)
			.getSingleResult();
		}
		catch (Exception e) {
			return null;
		}
	}
	
	private Publication getPublication(EntityManager entityManager, TimerID timerID) {
		try {
			return (Publication) entityManager.createQuery(
			"SELECT p FROM Publication p WHERE p.timerID = :timerID")
			.setParameter("timerID",timerID)
			.getSingleResult();
		}
		catch (Exception e) {
			return null;
		}
	}
	
	private ComposedPublication getComposedPublication(EntityManager entityManager, String entity, String eventPackage) {
		try {
			return (ComposedPublication) entityManager.createQuery(
			"SELECT p FROM ComposedPublication p WHERE p.composedPublicationKey.entity = :entity AND  p.composedPublicationKey.eventPackage = :eventPackage")
			.setParameter("entity",entity)
			.setParameter("eventPackage",eventPackage)
			.getSingleResult();
		}
		catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * verifies if the specified event packaged is accepted
	 */
	private boolean acceptsEventPackage(String eventPackage) {
		if (eventPackage != null) {			
			for(String acceptedEventPackage : getEventPackages()) {
				if (eventPackage.equals(acceptedEventPackage)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/*
	 * Sends a response with the specified status code, adding additional
	 * headers if needed
	 */
	private void sendResponse(int responseCode, Request request,
			ServerTransaction serverTransaction, String eventPackage) {
		
		try {
			// create response
			Response response = messageFactory.createResponse(responseCode,request);
			// add headers if needed
			if (responseCode == Response.BAD_EVENT) {
				String allowEventsHeader = "";
				boolean first = true;
				for (String acceptedEventPackage : getEventPackages()) {
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
				response.addHeader(headerFactory.createMinExpiresHeader(getMinExpires()));
			else if (responseCode == Response.UNSUPPORTED_MEDIA_TYPE)
				response.addHeader(getAcceptsHeader(eventPackage));						

			// Both 2xx response to SUBSCRIBE and NOTIFY need a Contact
			response = addContactHeader(response);
			serverTransaction.sendResponse(response);
			getLogger().info("Response sent:\n"+response.toString());
		}
		catch (Exception e) {
			getLogger().error("Can't send response!",e);
		}
	}	

	/*
	 * Adds subscription agent contact header to SIP response
	 */
	private Response addContactHeader(Response response) {
		if (response.getHeader(ContactHeader.NAME) != null) {
			response.removeHeader(ContactHeader.NAME);
		}
		try {
			Address address = addressFactory.createAddress(
					getContactAddressString());
			((SipURI) address.getURI()).setPort(sipProvider
					.getListeningPoint("udp").getPort());
			response.addHeader(headerFactory.createContactHeader(address));
		} catch (Exception e) {
			getLogger().error("Can't add contact header", e);
		}
		return response;
	}
	
	private static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("sipevent-publication-pu");
	private EntityManager getEntityManager() {
		//return this.persistenceResourceAdaptorSbbInterface.createEntityManager(new HashMap(), "sipevent-publication-pu");
		return entityManagerFactory.createEntityManager();
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
	
	public void unsetSbbContext() {}
	
}