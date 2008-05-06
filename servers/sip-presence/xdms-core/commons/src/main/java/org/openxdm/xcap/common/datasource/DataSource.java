package org.openxdm.xcap.common.datasource;

import org.openxdm.xcap.common.error.InternalServerErrorException;
import org.openxdm.xcap.common.uri.DocumentSelector;

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
	
	public Document getDocument(DocumentSelector documentSelector) throws InternalServerErrorException;

	public void createDocument(DocumentSelector documentSelector,String eTag, String xml) throws InternalServerErrorException;

	public void updateDocument(DocumentSelector documentSelector,String newETag, String newXml) throws InternalServerErrorException;
	
	public void deleteDocument(DocumentSelector documentSelector) throws InternalServerErrorException;
	
	public String[] getAppUsages() throws InternalServerErrorException;
	
	public boolean containsAppUsage(String appUsage)  throws InternalServerErrorException;
	
	public void addAppUsage(String appUsage) throws InternalServerErrorException;
	
	public void removeAppUsage(String appUsage) throws InternalServerErrorException;
	
	public void addCollection(String appUsage, String collection) throws InternalServerErrorException;
	
	public void addUser(String appUsage, String user) throws InternalServerErrorException;
	
	public String[] getCollections(String appUsage) throws InternalServerErrorException;
	
	public String[] getUsers(String appUsage) throws InternalServerErrorException;
	
	public void removeUser(String appUsage, String user) throws InternalServerErrorException;
	
	public String getExistingCollection(String auid, String startingCollection) throws InternalServerErrorException;

}
