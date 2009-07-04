package org.mobicents.slee.sipevent.server.publication;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sip.address.URI;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.Header;
import javax.sip.message.Response;
import javax.slee.ActivityContextInterface;
import javax.slee.ChildRelation;
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
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.mobicents.slee.sipevent.server.publication.pojo.ComposedPublication;
import org.mobicents.slee.sipevent.server.publication.pojo.ComposedPublicationKey;
import org.mobicents.slee.sipevent.server.publication.pojo.Publication;
import org.mobicents.slee.sipevent.server.publication.pojo.PublicationKey;

/**
 * Sbb to control publication of sip events
 * 
 * @author Eduardo Martins
 * 
 */
public abstract class PublicationControlSbb implements Sbb,
		PublicationControlSbbLocalObject {

	private static Logger logger = Logger
			.getLogger(PublicationControlSbb.class);

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
		this.sbbContext = sbbContext;
		// retrieve factories, facilities & providers
		try {
			Context context = (Context) new InitialContext()
					.lookup("java:comp/env");
			timerFacility = (TimerFacility) context
					.lookup("slee/facilities/timer");
			nullACIFactory = (NullActivityContextInterfaceFactory) context
					.lookup("slee/nullactivity/activitycontextinterfacefactory");
			nullActivityFactory = (NullActivityFactory) context
					.lookup("slee/nullactivity/factory");
			activityContextNamingfacility = (ActivityContextNamingFacility) context
					.lookup("slee/facilities/activitycontextnaming");
		} catch (Exception e) {
			logger.error(
					"Unable to retrieve factories, facilities & providers", e);
		}
	}

	public static Logger getLogger() {
		return logger;
	}

	private static final TimerOptions timerOptions = createTimerOptions();

	private static TimerOptions createTimerOptions() {
		TimerOptions options = new TimerOptions();
		options.setPersistent(true);
		options.setPreserveMissed(TimerPreserveMissed.ALL);
		return options;
	}

	// -- STORAGE OF PARENT SBB

	public void setParentSbb(
			PublicationClientControlParentSbbLocalObject parentSbb) {
		setParentSbbCMP(parentSbb);
	}

	public abstract PublicationClientControlParentSbbLocalObject getParentSbbCMP();

	public abstract void setParentSbbCMP(
			PublicationClientControlParentSbbLocalObject value);

	// --- IMPL CHILD SBB

	public abstract ChildRelation getImplementedSbbChildRelation();

	public abstract ImplementedPublicationControlSbbLocalObject getImplementedChildSbbCMP();

	public abstract void setImplementedChildSbbCMP(
			ImplementedPublicationControlSbbLocalObject value);

	private ImplementedPublicationControlSbbLocalObject getImplementedChildSbb() {
		ImplementedPublicationControlSbbLocalObject childSbb = getImplementedChildSbbCMP();
		if (childSbb == null) {
			try {
				childSbb = (ImplementedPublicationControlSbbLocalObject) getImplementedSbbChildRelation()
						.create();
			} catch (Exception e) {
				logger.error("Failed to create child sbb", e);
				return null;
			}
			setImplementedChildSbbCMP(childSbb);
		}
		return childSbb;
	}

	// ----------------------

	public void newPublication(Object requestId, String entity,
			String eventPackage, String document, String contentType,
			String contentSubType, int expires) {

		if (getLogger().isDebugEnabled()) {
			getLogger().debug(
					"new publication: entity=" + entity + ",eventPackage="
							+ eventPackage);
		}

		EntityManager entityManager = getEntityManager();
		NullActivity nullActivity = null;
		try {
			// get child sbb
			ImplementedPublicationControlSbbLocalObject childSbb = getImplementedChildSbb();
			// unmarshall document
			StringReader publicationStringReader = new StringReader(document);
			JAXBElement unmarshalledContent = null;
			try {
				unmarshalledContent = (JAXBElement) childSbb.getUnmarshaller()
						.unmarshal(publicationStringReader);
			} catch (JAXBException e) {
				getLogger().error("failed to parse publication content", e);
				// If the content type of the request does
				// not match the event package, or is not understood by the ESC,
				// the
				// ESC MUST reject the request with an appropriate response,
				// such as
				// 415 (Unsupported Media Type)
				if (getLogger().isInfoEnabled()) {
					logger.info("publication for resource " + entity
							+ " on event package " + eventPackage
							+ " has unsupported media type");
				}
				getParentSbbCMP().newPublicationError(requestId,
						Response.UNSUPPORTED_MEDIA_TYPE);
				entityManager.close();
				return;
			}
			publicationStringReader.close();

			// authorize publication
			if (!childSbb.authorizePublication(entity, unmarshalledContent)) {
				getParentSbbCMP().newPublicationError(requestId,
						Response.FORBIDDEN);
				if (getLogger().isInfoEnabled()) {
					logger.info("publication for resource " + entity
							+ " on event package " + eventPackage
							+ " not authorized");
				}
			} else {
				// create SIP-ETag
				String eTag = ETagGenerator.generate(entity, eventPackage);
				// create publication pojo
				PublicationKey publicationKey = new PublicationKey(eTag,
						entity, eventPackage);
				Publication publication = new Publication(publicationKey,
						document, contentType, contentSubType);
				publication.setUnmarshalledContent(unmarshalledContent);
				// inform parent publication is valid
				getParentSbbCMP().newPublicationOk(requestId, eTag, expires);
				// create null aci
				nullActivity = nullActivityFactory.createNullActivity();
				ActivityContextInterface aci = nullACIFactory
						.getActivityContextInterface(nullActivity);
				// attach to aci
				aci.attach(sbbContext.getSbbLocalObject());
				// bind a name to the aci
				activityContextNamingfacility.bind(aci, publication
						.getPublicationKey().toString());
				if (expires != -1) {
					// set a timer for this publication and store it in the
					// publication pojo
					TimerID timerID = timerFacility.setTimer(aci, null, System
							.currentTimeMillis()
							+ ((expires + 1) * 1000), 1, 1, timerOptions);
					publication.setTimerID(timerID);
				}
				// compose document
				ComposedPublication composedPublication = getUpdatedComposedPublication(
						entityManager, publication, childSbb);
				// persist data
				entityManager.persist(publication);
				entityManager.persist(composedPublication);
				if (getLogger().isInfoEnabled()) {
					logger.info("Created " + publication);
				}
				// notify subscribers
				childSbb.notifySubscribers(composedPublication);
			}
		} catch (Exception e) {
			try {
				getLogger().error("failed to create publication", e);
				// try to send server error
				getParentSbbCMP().newPublicationError(requestId,
						Response.SERVER_INTERNAL_ERROR);
				// end null activity if needed
				if (nullActivity != null) {
					nullActivity.endActivity();
				}
			} catch (Exception f) {
				getLogger().error(f);
			}
		}
		if (entityManager != null) {
			entityManager.close();
		}
	}

	private ComposedPublication getUpdatedComposedPublication(
			EntityManager entityManager, Publication publication,
			ImplementedPublicationControlSbbLocalObject childSbb) {

		ComposedPublication composedPublication = getComposedPublication(
				entityManager, publication.getPublicationKey().getEntity(),
				publication.getPublicationKey().getEventPackage());
		if (composedPublication == null) {
			ComposedPublicationKey key = new ComposedPublicationKey(publication
					.getPublicationKey().getEntity(), publication
					.getPublicationKey().getEventPackage());
			composedPublication = new ComposedPublication(key, publication
					.getDocument(), publication.getContentType(), publication
					.getContentSubType());
			composedPublication.setUnmarshalledContent(publication
					.getUnmarshalledContent());
		} else {
			composedPublication = childSbb.combinePublication(publication,
					composedPublication);
		}
		return composedPublication;
	}

	public void refreshPublication(Object requestId, String entity,
			String eventPackage, String oldETag, int expires) {

		if (getLogger().isDebugEnabled()) {
			getLogger().debug(
					"refresh Publication: entity=" + entity + ",eventPackage="
							+ eventPackage + ",eTag=" + oldETag + ",expires="
							+ expires);
		}

		EntityManager entityManager = getEntityManager();

		try {
			// get publication
			Publication publication = getPublication(entityManager, oldETag,
					entity, eventPackage);
			if (publication == null) {
				if (getLogger().isInfoEnabled()) {
					logger.info("can't refresh publication for resource "
							+ entity + " on event package " + eventPackage
							+ " with eTag " + oldETag + ", it does not exist");
				}
				getParentSbbCMP().refreshPublicationError(requestId,
						Response.CONDITIONAL_REQUEST_FAILED);
				entityManager.close();
				return;
			}

			if (publication.getTimerID() != null) {
				// cancel current timer
				timerFacility.cancelTimer(publication.getTimerID());
			}
			// remove old publication
			entityManager.remove(publication);
			// create new SIP-ETag
			String eTag = ETagGenerator.generate(publication
					.getPublicationKey().getEntity(), publication
					.getPublicationKey().getEventPackage());
			// create new publication pojo
			PublicationKey newPublicationKey = new PublicationKey(eTag,
					publication.getPublicationKey().getEntity(), publication
							.getPublicationKey().getEventPackage());
			Publication newPublication = new Publication(newPublicationKey,
					publication.getDocument(), publication.getContentType(),
					publication.getContentSubType());
			// inform parent publication is valid
			getParentSbbCMP().refreshPublicationOk(requestId, eTag, expires);
			// get null aci
			ActivityContextInterface aci = activityContextNamingfacility
					.lookup(publication.getPublicationKey().toString());
			// change aci name
			activityContextNamingfacility.unbind(publication
					.getPublicationKey().toString());
			activityContextNamingfacility.bind(aci, newPublication
					.getPublicationKey().toString());
			if (expires != -1) {
				// set timer with 1 secs more
				TimerID newTimerID = timerFacility.setTimer(aci, null, System
						.currentTimeMillis()
						+ ((expires + 1) * 1000), 1, 1, timerOptions);
				newPublication.setTimerID(newTimerID);
			}
			// persist new publication
			entityManager.persist(newPublication);

			if (getLogger().isInfoEnabled()) {
				getLogger().info(
						"Refreshed " + publication + " for " + expires
								+ " seconds");
			}
		} catch (Exception e) {
			getLogger().error("failed to refresh publication", e);
			// try to send server error
			getParentSbbCMP().refreshPublicationError(requestId,
					Response.SERVER_INTERNAL_ERROR);
		}

		if (entityManager != null) {
			entityManager.close();
		}
	}

	public void removePublication(Object requestId, String entity,
			String eventPackage, String eTag) {

		if (getLogger().isDebugEnabled()) {
			getLogger().debug(
					"removePublication: entity=" + entity + ",eventPackage="
							+ eventPackage + ",eTag=" + eTag);
		}

		EntityManager entityManager = getEntityManager();
		try {
			// get publication
			Publication publication = getPublication(entityManager, eTag,
					entity, eventPackage);
			if (publication == null) {
				if (getLogger().isInfoEnabled()) {
					logger.info("can't remove publication for resource "
							+ entity + " on event package " + eventPackage
							+ " with eTag " + eTag + ", it does not exist");
				}
				getParentSbbCMP().removePublicationError(requestId,
						Response.CONDITIONAL_REQUEST_FAILED);
				entityManager.close();
				return;
			}

			if (publication.getTimerID() != null) {
				// cancel current timer
				timerFacility.cancelTimer(publication.getTimerID());
			}
			// lookup timer aci
			String aciName = publication.getPublicationKey().toString();
			ActivityContextInterface aci = activityContextNamingfacility
					.lookup(aciName);
			// explictly end the null activity
			((NullActivity) aci.getActivity()).endActivity();
			// remove old publication
			entityManager.remove(publication);
			// inform parent publication is removed
			getParentSbbCMP().removePublicationOk(requestId);
			if (getLogger().isInfoEnabled()) {
				getLogger().info("Removed " + publication);
			}
			// get child sbb
			ImplementedPublicationControlSbbLocalObject childSbb = getImplementedChildSbb();
			// we need to re-compose all publications except the one being
			// removed
			ComposedPublication composedPublication = removeFromComposedPublication(
					entityManager, publication, childSbb);
			entityManager.flush();
			entityManager.close();
			entityManager = null;
			if (composedPublication.getDocument() == null) {
				// give the event package implementation sbb a chance to define
				// an alternative publication value for the one removed,
				// this can allow a behavior such as defining offline status
				// in a presence resource
				Publication alternativePublication = childSbb.getAlternativeValueForExpiredPublication(publication);
				if (alternativePublication != null) {
					composedPublication.setContentSubType(alternativePublication.getContentSubType());
					composedPublication.setContentType(alternativePublication.getContentType());
					composedPublication.setDocument(alternativePublication.getDocument());
					composedPublication.setUnmarshalledContent(alternativePublication.getUnmarshalledContent());
				}
			}
			// notify subscribers
			childSbb.notifySubscribers(composedPublication);
		} catch (Exception e) {
			getLogger().error("failed to remove publication", e);
			// try to send server error
			getParentSbbCMP().removePublicationError(requestId,
					Response.SERVER_INTERNAL_ERROR);
		}

		if (entityManager != null) {
			entityManager.close();
		}
	}

	public void modifyPublication(Object requestId, String entity,
			String eventPackage, String oldETag, String document,
			String contentType, String contentSubType, int expires) {

		if (getLogger().isDebugEnabled()) {
			getLogger().debug(
					"modifyPublication: entity=" + entity + ",eventPackage="
							+ eventPackage + ",eTag=" + oldETag);
		}

		EntityManager entityManager = getEntityManager();
		try {
			// get child sbb
			ImplementedPublicationControlSbbLocalObject childSbb = getImplementedChildSbb();
			// get publication
			Publication publication = getPublication(entityManager, oldETag,
					entity, eventPackage);
			if (publication == null) {
				if (getLogger().isInfoEnabled()) {
					logger.info("can't modify publication for resource "
							+ entity + " on event package " + eventPackage
							+ " with eTag " + oldETag + ", it does not exist");
				}
				getParentSbbCMP().modifyPublicationError(requestId,
						Response.CONDITIONAL_REQUEST_FAILED);
				entityManager.close();
				return;
			}

			if (publication.getTimerID() != null) {
				// cancel current timer
				timerFacility.cancelTimer(publication.getTimerID());
			}

			// remove current publication
			entityManager.remove(publication);

			// unmarshall document
			StringReader publicationStringReader = new StringReader(document);
			JAXBElement unmarshalledContent = null;
			try {
				unmarshalledContent = (JAXBElement) childSbb.getUnmarshaller()
						.unmarshal(publicationStringReader);
			} catch (JAXBException e) {
				getLogger().error("failed to parse publication content", e);
				// If the content type of the request does
				// not match the event package, or is not understood by the ESC,
				// the
				// ESC MUST reject the request with an appropriate response,
				// such as
				// 415 (Unsupported Media Type)
				if (getLogger().isInfoEnabled()) {
					logger.info("publication for resource " + entity
							+ " on event package " + eventPackage
							+ " has unsupported media type");
				}
				getParentSbbCMP().modifyPublicationError(requestId,
						Response.UNSUPPORTED_MEDIA_TYPE);
				entityManager.close();
				return;
			}
			publicationStringReader.close();

			// authorize publication
			if (!childSbb.authorizePublication(entity, unmarshalledContent)) {
				getParentSbbCMP().modifyPublicationError(requestId,
						Response.FORBIDDEN);
				if (getLogger().isInfoEnabled()) {
					logger.info("publication for resource " + entity
							+ " on event package " + eventPackage
							+ " not authorized");
				}
			} else {
				// create new SIP-ETag
				String eTag = ETagGenerator.generate(publication
						.getPublicationKey().getEntity(), publication
						.getPublicationKey().getEventPackage());
				// create new publication pojo with new key and document
				PublicationKey newPublicationKey = new PublicationKey(eTag,
						publication.getPublicationKey().getEntity(),
						publication.getPublicationKey().getEventPackage());
				Publication newPublication = new Publication(newPublicationKey,
						document, contentType, contentSubType);
				newPublication.setUnmarshalledContent(unmarshalledContent);
				// inform parent publication is valid
				getParentSbbCMP().modifyPublicationOk(requestId, eTag, expires);
				// get null aci
				ActivityContextInterface aci = activityContextNamingfacility
						.lookup(publication.getPublicationKey().toString());
				// change aci name
				activityContextNamingfacility.unbind(publication
						.getPublicationKey().toString());
				activityContextNamingfacility.bind(aci, newPublication
						.getPublicationKey().toString());
				if (expires != -1) {
					// set timer with 1 sec more
					TimerID newTimerID = timerFacility
							.setTimer(aci, null, System.currentTimeMillis()
									+ ((expires + 1) * 1000), 1, 1,
									timerOptions);
					newPublication.setTimerID(newTimerID);
				}
				getLogger().info(publication + " modified.");
				// compose document
				ComposedPublication composedPublication = getUpdatedComposedPublication(
						entityManager, newPublication, childSbb);
				// persist data
				entityManager.persist(newPublication);
				entityManager.persist(composedPublication);
				// notify subscribers
				childSbb.notifySubscribers(composedPublication);
			}
		} catch (Exception e) {
			getLogger().error("failed to refresh publication", e);
			// try to send server error
			getParentSbbCMP().modifyPublicationError(requestId,
					Response.SERVER_INTERNAL_ERROR);
		}

		if (entityManager != null) {
			entityManager.close();
		}
	}

	/**
	 * a timer has occurred in a dialog regarding a publication
	 * 
	 * @param event
	 * @param aci
	 */
	public void onTimerEvent(TimerEvent event, ActivityContextInterface aci) {

		// cancel current timer
		timerFacility.cancelTimer(event.getTimerID());
		// detach from aci
		aci.detach(this.sbbContext.getSbbLocalObject());
		// end it
		((NullActivity) aci.getActivity()).endActivity();
		// create jpa entity manager
		EntityManager entityManager = getEntityManager();
		// get publication
		Publication publication = getPublication(entityManager, event
				.getTimerID());

		if (publication != null) {

			try {
				// get child sbb
				ImplementedPublicationControlSbbLocalObject childSbb = getImplementedChildSbb();
				// remove publication
				entityManager.remove(publication);
				if (getLogger().isInfoEnabled()) {
					getLogger().info(publication + " removed. Timer expired.");
				}
				// we need to re-compose all publications except the one being
				// removed
				ComposedPublication composedPublication = removeFromComposedPublication(
						entityManager, publication, childSbb);
				entityManager.flush();
				entityManager.close();
				entityManager = null;
				if (composedPublication.getDocument() == null) {
					// give the event package implementation sbb a chance to define
					// an alternative publication value for the one expired,
					// this can allow a behavior such as defining offline status
					// in a presence resource
					Publication alternativePublication = childSbb.getAlternativeValueForExpiredPublication(publication);
					if (alternativePublication != null) {
						composedPublication.setContentSubType(alternativePublication.getContentSubType());
						composedPublication.setContentType(alternativePublication.getContentType());
						composedPublication.setDocument(alternativePublication.getDocument());
						composedPublication.setUnmarshalledContent(alternativePublication.getUnmarshalledContent());
					}
				}
				// notify subscribers
				childSbb.notifySubscribers(composedPublication);
			} catch (Exception e) {
				getLogger().error("failed to remove publication that expired",
						e);
			}
		}

		if (entityManager != null) {
			entityManager.close();
		}
	}

	// ----------- SBB LOCAL OBJECT

	public void shutdown() {
		// close entity manager factory
		entityManagerFactory.close();
	}

	public ComposedPublication getComposedPublication(String entity,
			String eventPackage) {
		// create jpa entity manager
		EntityManager entityManager = getEntityManager();
		// get composed document
		ComposedPublication composedPublication = getComposedPublication(
				entityManager, entity, eventPackage);
		if (composedPublication != null
				&& composedPublication.getDocument() != null) {
			// unmarshalled content if needed
			// content needs to unmarshalled
			StringReader stringReader = new StringReader(composedPublication
					.getDocument());
			try {
				composedPublication
						.setUnmarshalledContent((JAXBElement) getImplementedChildSbb()
								.getUnmarshaller().unmarshal(stringReader));
			} catch (Exception e) {
				getLogger().error(
						"failed to unmarshall content of "
								+ composedPublication
										.getComposedPublicationKey(), e);
				composedPublication = null;
			}
			stringReader.close();
		}
		// close entity manager
		entityManager.close();
		return composedPublication;
	}

	public boolean acceptsContentType(String eventPackage,
			ContentTypeHeader contentTypeHeader) {
		ImplementedPublicationControlSbbLocalObject childSbb = getImplementedChildSbb();
		return childSbb != null ? childSbb.acceptsContentType(eventPackage,
				contentTypeHeader) : false;
	}

	public Header getAcceptsHeader(String eventPackage) {
		ImplementedPublicationControlSbbLocalObject childSbb = getImplementedChildSbb();
		return childSbb != null ? childSbb.getAcceptsHeader(eventPackage)
				: null;
	}

	private static final String[] emptyArray = new String[0];

	public String[] getEventPackages() {
		ImplementedPublicationControlSbbLocalObject childSbb = getImplementedChildSbb();
		return childSbb != null ? childSbb.getEventPackages() : emptyArray;
	}

	public boolean isResponsibleForResource(URI uri) {
		ImplementedPublicationControlSbbLocalObject childSbb = getImplementedChildSbb();
		return childSbb != null ? childSbb.isResponsibleForResource(uri)
				: false;
	}

	public boolean authorizePublication(String entity,
			JAXBElement unmarshalledContent) {
		ImplementedPublicationControlSbbLocalObject childSbb = getImplementedChildSbb();
		return childSbb != null ? childSbb.authorizePublication(entity,
				unmarshalledContent) : false;
	}

	// ----------- AUX METHODS

	private ComposedPublication removeFromComposedPublication(
			EntityManager entityManager, Publication publication,
			ImplementedPublicationControlSbbLocalObject childSbb) {
		// get composed publication
		ComposedPublication composedPublication = getComposedPublication(
				entityManager, publication.getPublicationKey().getEntity(),
				publication.getPublicationKey().getEventPackage());
		// reset content
		composedPublication.setDocument(null);
		composedPublication.setContentType(null);
		composedPublication.setContentSubType(null);
		// process existing publications
		List resultList = entityManager.createNamedQuery(
				Publication.JPA_NAMED_QUERY_SELECT_PUBLICATION_FROM_ENTITY_AND_EVENTPACKAGE).setParameter(
				"entity", publication.getPublicationKey().getEntity())
				.setParameter("eventPackage",
						publication.getPublicationKey().getEventPackage())
				.getResultList();
		for (Iterator i = resultList.iterator(); i.hasNext();) {
			Publication otherPublication = (Publication) i.next();
			if (!otherPublication.getPublicationKey().getETag().equals(
					publication.getPublicationKey().getETag())) {
				// it's not the publication being removed
				
				if (composedPublication.getDocument() == null) {
					composedPublication.setDocument(otherPublication
							.getDocument());
					composedPublication.setContentType(otherPublication
							.getContentType());
					composedPublication.setContentSubType(otherPublication
							.getContentSubType());
				} else {
					// combine
					composedPublication = childSbb.combinePublication(
							otherPublication, composedPublication);
				}
			}
		}
		if (composedPublication.getDocument() == null) {
			// no publications other than the one being removed, remove composed
			// publication too
			entityManager.remove(composedPublication);
		}
		return composedPublication;
	}

	private Publication getPublication(EntityManager entityManager,
			String eTag, String entity, String eventPackage) {
		return entityManager.find(Publication.class, new PublicationKey(eTag,
				entity, eventPackage));

	}

	private Publication getPublication(EntityManager entityManager,
			TimerID timerID) {
		List resultList = entityManager.createNamedQuery(
				Publication.JPA_NAMED_QUERY_SELECT_PUBLICATION_FROM_TIMER_ID).setParameter("timerID",
				timerID).getResultList();
		if (resultList.size() == 1) {
			return (Publication) resultList.get(0);
		}
		else {
			if (logger.isDebugEnabled()) {
				logger.debug("Failed to get publication for timer id "+timerID);
			}
			return null;
		}
	}

	private ComposedPublication getComposedPublication(
			EntityManager entityManager, String entity, String eventPackage) {
		
		List resultList = entityManager.createNamedQuery(
		ComposedPublication.JPA_NAMED_QUERY_SELECT_COMPOSEDPUBLICATION_FROM_ENTITY_AND_EVENTPACKAGE)
		.setParameter("entity", entity).setParameter(
				"eventPackage", eventPackage).getResultList();
		if (resultList.size() == 1) {
			return (ComposedPublication) resultList.get(0);
		}
		else {
			if (logger.isDebugEnabled()) {
				logger.debug("Failed to get single composed publication for entity "+entity+" and event package "+eventPackage);
			}
			return null;
		}
	}

	// --- JPA INITIALIZATION

	private static EntityManagerFactory initEntityManagerFactory() {
		try {
			TransactionManager txMgr = (TransactionManager) new InitialContext()
					.lookup("java:/TransactionManager");

			Transaction tx = null;
			try {
				if (txMgr.getTransaction() != null) {
					tx = txMgr.suspend();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			try {
				return Persistence
						.createEntityManagerFactory("sipevent-publication-pu");
			} finally {
				if (tx != null) {
					txMgr.resume(tx);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	private static EntityManagerFactory entityManagerFactory = initEntityManagerFactory();

	private EntityManager getEntityManager() {
		return entityManagerFactory.createEntityManager();
	}

	// ----------- SBB OBJECT's LIFE CYCLE

	public void sbbActivate() {
	}

	public void sbbCreate() throws CreateException {
	}

	public void sbbExceptionThrown(Exception arg0, Object arg1,
			ActivityContextInterface arg2) {
	}

	public void sbbLoad() {
	}

	public void sbbPassivate() {
	}

	public void sbbPostCreate() throws CreateException {
	}

	public void sbbRemove() {
	}

	public void sbbRolledBack(RolledBackContext arg0) {
	}

	public void sbbStore() {
	}

	public void unsetSbbContext() {
		this.sbbContext = null;
	}

}