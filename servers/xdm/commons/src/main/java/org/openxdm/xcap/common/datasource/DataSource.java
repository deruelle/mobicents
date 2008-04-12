package org.openxdm.xcap.common.datasource;

import org.openxdm.xcap.common.error.InternalServerErrorException;
import org.openxdm.xcap.common.uri.DocumentSelector;
import org.openxdm.xcap.common.uri.NodeSelector;

/**
 * TODO
 * Important: Implementations must be thread safe!
 * @author Eduardo Martins
 *
 */
public interface DataSource {

	/**
	 * Open the datasource, enables all required resources. 
	 * @throws InternalServerErrorException
	 */
	public void open() throws InternalServerErrorException;
	
	/**
	 * Closes the datasource, disables all required resources. 
	 * @throws InternalServerErrorException
	 */
	public void close() throws InternalServerErrorException;
	
	/**
	 * Retrieves a document from the data source. 
	 * @param documentSelector the {@linkDocumentSelector} that points to the document
	 * @return the {@linkDocument} selected
	 * @throws InternalServerErrorException
	 */
	public Document getDocument(DocumentSelector documentSelector) throws InternalServerErrorException;

	/**
	 * Creates a document in the data source.
	 * @param documentSelector the {@linkDocumentSelector} that points to the document
	 * @param eTag a entity tag string to be associated with the document
	 * @param xml the document content
	 * @throws InternalServerErrorException
	 */
	public void createDocument(DocumentSelector documentSelector,String eTag, String xml) throws InternalServerErrorException;

	/**
	 * Updates a document
	 * @param documentSelector
	 * @param newETag
	 * @param newXml
	 * @param oldDocument
	 * @param nodeSelector
	 * @throws InternalServerErrorException
	 */
	public void updateDocument(DocumentSelector documentSelector,String newETag, String newXml, Document oldDocument, NodeSelector nodeSelector) throws InternalServerErrorException;
	
	public void deleteDocument(DocumentSelector documentSelector, Document oldDocument, NodeSelector nodeSelector) throws InternalServerErrorException;
	
	public String[] getAppUsages() throws InternalServerErrorException;
	
	public boolean containsAppUsage(String appUsage)  throws InternalServerErrorException;
	
	public void addCollection(String appUsage, String collection) throws InternalServerErrorException;
	
	public void addUser(String appUsage, String user) throws InternalServerErrorException;
	
	public String[] getCollections(String appUsage) throws InternalServerErrorException;
	
	public String[] getUsers(String appUsage) throws InternalServerErrorException;
	
	public void removeUser(String appUsage, String user) throws InternalServerErrorException;
	
	public String getExistingCollection(String auid, String startingCollection) throws InternalServerErrorException;

}
