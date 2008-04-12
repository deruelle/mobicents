package org.openxdm.xcap.server.slee.resource.datasource;

import org.openxdm.xcap.common.datasource.Document;
import org.openxdm.xcap.common.error.InternalServerErrorException;
import org.openxdm.xcap.common.uri.DocumentSelector;
import org.openxdm.xcap.common.uri.NodeSelector;

/**
 * Sbb Interface implementation for a DataSource Resource Adaptor.
 * TODO complete javadoc 
 * @author Eduardo Martins
 *
 */
public class DataSourceSbbInterfaceInterceptor implements DataSourceSbbInterface {

	private DataSourceResourceAdaptor ra;
	
	public DataSourceSbbInterfaceInterceptor(DataSourceResourceAdaptor ra) {
		this.ra = ra;
	}

	public String[] getAppUsages() throws InternalServerErrorException {
		return ra.getDataSourceSbbInterface().getAppUsages();
	}

	public void addAppUsage(String appUsage) throws InternalServerErrorException {
		ra.getDataSourceSbbInterface().addAppUsage(appUsage);		
	}

	public void removeAppUsage(String appUsage) throws InternalServerErrorException {
		ra.getDataSourceSbbInterface().removeAppUsage(appUsage);		
	}

	public String[] getUsers(String appUsage) throws InternalServerErrorException {		
		return ra.getDataSourceSbbInterface().getUsers(appUsage);
	}

	public void addUser(String appUsage, String user) throws InternalServerErrorException {
		ra.getDataSourceSbbInterface().addUser(appUsage,user);		
	}

	public void removeUser(String appUsage, String user) throws InternalServerErrorException {
		ra.getDataSourceSbbInterface().removeUser(appUsage,user);
	}

	public void open() throws InternalServerErrorException {
		throw new InternalServerErrorException("forbidden to open datasource from sbb");		
	}

	public void close() throws InternalServerErrorException {
		throw new InternalServerErrorException("forbidden to close datasource from sbb");		
	}	
	
	public DocumentActivity createDocumentActivity(DocumentSelector documentSelector) {
		return ra.createDocumentActivity(documentSelector);
	}
	
	public DocumentActivity getDocumentActivity(DocumentSelector documentSelector) {
		return ra.getDocumentActivity(documentSelector);
	}
	
	public void removeDocumentActivity(DocumentSelector documentSelector) {
		ra.endDocumentActivity(documentSelector);
	}

	public void addCollection(String appUsage, String collection) throws InternalServerErrorException {
		ra.getDataSourceSbbInterface().addCollection(appUsage, collection);
	}

	public void createDocument(DocumentSelector documentSelector, String eTag, String xml) throws InternalServerErrorException {
		ra.getDataSourceSbbInterface().createDocument(documentSelector, eTag, xml);
	}

	public void deleteDocument(DocumentSelector documentSelector, Document oldDocument, NodeSelector nodeSelector) throws InternalServerErrorException {
		ra.getDataSourceSbbInterface().deleteDocument(documentSelector,oldDocument,nodeSelector);
	}

	public String[] getCollections(String appUsage) throws InternalServerErrorException {
		return ra.getDataSourceSbbInterface().getCollections(appUsage);
	}

	public Document getDocument(DocumentSelector documentSelector) throws InternalServerErrorException {
		return ra.getDataSourceSbbInterface().getDocument(documentSelector);
	}

	public String getExistingCollection(String auid, String startingCollection) throws InternalServerErrorException {
		return ra.getDataSourceSbbInterface().getExistingCollection(auid, startingCollection);
	}

	public void updateDocument(DocumentSelector documentSelector, String eTag, String xml, Document oldDocument, NodeSelector nodeSelector) throws InternalServerErrorException {
		ra.getDataSourceSbbInterface().updateDocument(documentSelector, eTag, xml, oldDocument, nodeSelector);
	}

	public boolean containsAppUsage(String appUsage) throws InternalServerErrorException {
		return ra.getDataSourceSbbInterface().containsAppUsage(appUsage);
	}
	
}
