package org.mobicents.slee.sipevent.server.publication.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.slee.facilities.TimerID;
import javax.xml.bind.JAXBElement;

/**
 *  
 *     This class is JPA pojo for a publication of sip events.
 *     
 * @author eduardomartins
 *
 */
@Entity
@Table(name = "MOBICENTS_SIPEVENT_PUBLICATIONS")
@NamedQueries({
	@NamedQuery(name=Publication.JPA_NAMED_QUERY_SELECT_PUBLICATION_FROM_ENTITY_AND_EVENTPACKAGE,query="SELECT p FROM Publication p WHERE p.publicationKey.entity = :entity AND p.publicationKey.eventPackage = :eventPackage"),
	@NamedQuery(name=Publication.JPA_NAMED_QUERY_SELECT_PUBLICATION_FROM_TIMER_ID,query="SELECT p FROM Publication p WHERE p.timerID = :timerID")
	})
public class Publication implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8020033417766370446L;

	private static final String JPA_NAMED_QUERY_PREFIX = "MSPS_NQUERY_";
	public static final String JPA_NAMED_QUERY_SELECT_PUBLICATION_FROM_ENTITY_AND_EVENTPACKAGE = JPA_NAMED_QUERY_PREFIX + "selectPublicationFromEntityAndEventPackage";
	public static final String JPA_NAMED_QUERY_SELECT_PUBLICATION_FROM_TIMER_ID = JPA_NAMED_QUERY_PREFIX + "selectPublicationFromTimerID";
	
	/**
	 * the publication key
	 */   
	@EmbeddedId
	protected PublicationKey publicationKey;
	
	/**
	 * the id of the SLEE timer associated with this subscription
	 */
	@Column(name = "TIMER_ID", nullable = true)
	private TimerID timerID;
	
	/**
	 * the document published
	 */
	@Column(name = "DOCUMENT", nullable = false)
	private String document;
	
	/**
	 * the type of document published
	 */
	@Column(name = "CONTENT_TYPE", nullable = false)
	private String contentType;
	@Column(name = "CONTENT_SUBTYPE", nullable = false)
	private String contentSubType;
	
	/**
	 * unmarshalled version of the document
	 */
	private transient JAXBElement unmarshalledContent;
	
	public Publication() {
		// TODO Auto-generated constructor stub
	}
	
	public Publication(PublicationKey publicationKey, String document, String contentType, String contentSubType) {
		this.publicationKey = publicationKey;
		this.document = document;
		this.contentType = contentType;
		this.contentSubType = contentSubType;
	}

	@Override
	public int hashCode() {
		return publicationKey.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass() == this.getClass()) {
			return ((Publication)obj).publicationKey.equals(this.publicationKey);
		}
		else {
			return false;
		}
	}

	// -- GETTERS AND SETTERS
	
	public PublicationKey getPublicationKey() {
		return publicationKey;
	}

	public void setPublicationKey(PublicationKey publicationKey) {
		this.publicationKey = publicationKey;
	}

	public String getContentSubType() {
		return contentSubType;
	}
	
	public void setContentSubType(String contentSubType) {
		this.contentSubType = contentSubType;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public TimerID getTimerID() {
		return timerID;
	}

	public void setTimerID(TimerID timerID) {
		this.timerID = timerID;
	}
	
	public JAXBElement getUnmarshalledContent() {
		return unmarshalledContent;
	}
	
	public void setUnmarshalledContent(JAXBElement unmarshalledContent) {
		this.unmarshalledContent = unmarshalledContent;
	}
	
	@Override
	public String toString() {
		return "publication: entity="+publicationKey.getEntity()+",eventPackage="+publicationKey.getEventPackage()+",eTag="+publicationKey.getETag();
	}
}
