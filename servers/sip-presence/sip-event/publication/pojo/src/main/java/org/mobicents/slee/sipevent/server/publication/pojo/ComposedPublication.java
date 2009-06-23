package org.mobicents.slee.sipevent.server.publication.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.JAXBElement;

/**
 *  
 *     This class is JPA pojo for a composition of sip event publications.
 *     
 * @author eduardomartins
 *
 */
@Entity
@Table(name = "MOBICENTS_SIPEVENT_COMPOSEDPUBLICATIONS")
@NamedQueries({
	@NamedQuery(name=ComposedPublication.JPA_NAMED_QUERY_SELECT_COMPOSEDPUBLICATION_FROM_ENTITY_AND_EVENTPACKAGE,query="SELECT p FROM ComposedPublication p WHERE p.composedPublicationKey.entity = :entity AND  p.composedPublicationKey.eventPackage = :eventPackage")
	})
public class ComposedPublication implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8020033417766370446L;

	private static final String JPA_NAMED_QUERY_PREFIX = "MSPS_NQUERY_";
	public static final String JPA_NAMED_QUERY_SELECT_COMPOSEDPUBLICATION_FROM_ENTITY_AND_EVENTPACKAGE = JPA_NAMED_QUERY_PREFIX + "selectComposedPublicationFromEntityAndEventPackage";
	
	/**
	 * the publication key
	 */   
	@EmbeddedId
	protected ComposedPublicationKey composedPublicationKey;
	
	/**
	 * the document published
	 */
	@Column(name = "DOCUMENT", nullable = true)
	private String document;
	
	/**
	 * the type of document published
	 */
	@Column(name = "CONTENT_TYPE", nullable = true)
	private String contentType;
	@Column(name = "CONTENT_SUBTYPE", nullable = true)
	private String contentSubType;
	
	/**
	 * unmarshalled version of the document
	 */
	private transient JAXBElement unmarshalledContent;
	
	public ComposedPublication() {
		// TODO Auto-generated constructor stub
	}
	
	public ComposedPublication(ComposedPublicationKey composedPublicationKey, String document, String contentType, String contentSubType) {
		this.composedPublicationKey = composedPublicationKey;
		this.document = document;
		this.contentType = contentType;
		this.contentSubType = contentSubType;
	}

	@Override
	public int hashCode() {
		return composedPublicationKey.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass() == this.getClass()) {
			return ((ComposedPublication)obj).composedPublicationKey.equals(this.composedPublicationKey);
		}
		else {
			return false;
		}
	}

	// -- GETTERS AND SETTERS
	
	public ComposedPublicationKey getComposedPublicationKey() {
		return composedPublicationKey;
	}

	public void setComposedPublicationKey(ComposedPublicationKey composedPublicationKey) {
		this.composedPublicationKey = composedPublicationKey;
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
	
	/**
	 * Retrieves cached unmarshalled version of the content, if available.
	 * @return
	 */
	public JAXBElement getUnmarshalledContent() {
		return unmarshalledContent;
	}
	
	/**
	 * Sets unmarshalled version of the content for caching, this is not persisted.
	 * @return
	 */
	public void setUnmarshalledContent(JAXBElement unmarshalledContent) {
		this.unmarshalledContent = unmarshalledContent;
	}
}
