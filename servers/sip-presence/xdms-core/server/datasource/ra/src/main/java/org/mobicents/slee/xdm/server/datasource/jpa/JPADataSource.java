package org.mobicents.slee.xdm.server.datasource.jpa;

import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
import org.mobicents.slee.xdm.server.ServerConfiguration;
import org.openxdm.xcap.client.XCAPClient;
import org.openxdm.xcap.client.XCAPClientImpl;
import org.openxdm.xcap.common.datasource.DataSource;
import org.openxdm.xcap.common.error.InternalServerErrorException;
import org.openxdm.xcap.common.key.DocumentUriKey;
import org.openxdm.xcap.common.uri.DocumentSelector;

/**
 * @author eduardomartins
 */
public class JPADataSource implements DataSource {

	private static Logger logger = Logger.getLogger(JPADataSource.class);

	private static final String[] EMPTY_STRING_ARRAY = new String[0];
	
	private EntityManagerFactory entityManagerFactory = null;
	
	public void open() throws InternalServerErrorException {
		entityManagerFactory = Persistence.createEntityManagerFactory("mobicents-xdm-core-datasource-pu");
	}

	public void close() throws InternalServerErrorException {
		entityManagerFactory.close();
	}

	public String getExistingCollection(String auid, String startingCollection)
			throws InternalServerErrorException {
		
		EntityManager entityManager = entityManagerFactory
				.createEntityManager();
		while (true) {
			if (!entityManager
					.createNamedQuery("selectCollectionFromKey")
					.setParameter("collectionName", startingCollection)
					.setParameter("auid", auid).getResultList().isEmpty()) {
				entityManager.close();
				return startingCollection;
			} else {
				int index = startingCollection.lastIndexOf('/');
				if (index > 0) {
					startingCollection = startingCollection.substring(0, index);
				} else {
					break;
				}
			}
		}
		entityManager.close();
		return "";
		
	}

	public org.openxdm.xcap.common.datasource.Document getDocument(DocumentSelector documentSelector)
			throws InternalServerErrorException {
		
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Document document = null;
		List resultList = entityManager
		.createNamedQuery("selectDocumentFromKey")
		.setParameter("auid", documentSelector.getAUID())
		.setParameter("collectionName", documentSelector.getDocumentParent())
		.setParameter("documentName", documentSelector.getDocumentName())
		.getResultList();
		if (!resultList.isEmpty()) {
			document = (Document) resultList.get(0);
		}
		entityManager.close();
		return document;
	}

	public void createDocument(DocumentSelector documentSelector, String eTag,
			String xml, org.w3c.dom.Document domDocument) throws InternalServerErrorException {

		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Document document = new Document(documentSelector.getAUID(), documentSelector.getDocumentParent(), documentSelector.getDocumentName());
		document.setETag(eTag);
		document.setXml(xml);
		entityManager.persist(document);
		entityManager.flush();
		entityManager.close();
	}

	public void updateDocument(DocumentSelector documentSelector,
			String oldETag, String newETag, String documentAsString,
			org.w3c.dom.Document document)
			throws InternalServerErrorException {

		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.createNamedQuery("updateDocumentFromKey")
		.setParameter("auid", documentSelector.getAUID())
		.setParameter("collectionName", documentSelector.getDocumentParent())
		.setParameter("documentName", documentSelector.getDocumentName())
		.setParameter("eTag", newETag)
		.setParameter("xml", documentAsString)
		.executeUpdate();
		entityManager.flush();
		entityManager.close();	
	}

	public void deleteDocument(DocumentSelector documentSelector, String oldETag)
			throws InternalServerErrorException {
		
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.createNamedQuery("deleteDocumentFromKey")
		.setParameter("auid", documentSelector.getAUID())
		.setParameter("collectionName", documentSelector.getDocumentParent())
		.setParameter("documentName", documentSelector.getDocumentName())
		.executeUpdate();
		entityManager.flush();
		entityManager.close();
	}

	public void addAppUsage(String auid)
			throws InternalServerErrorException {
		
		if (!containsAppUsage(auid)) {
			EntityManager entityManager = entityManagerFactory
					.createEntityManager();
			AppUsage appUsage = new AppUsage(auid);
			entityManager.persist(appUsage);
			entityManager.flush();
			entityManager.close();
		}
	}

	public String[] getAppUsages() throws InternalServerErrorException {

		String[] result = null;
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		List resultList = entityManager.createNamedQuery("selectAppUsages").getResultList();
		int resultListSize = resultList.size();
		if (resultListSize > 0) {
			result = new String[resultListSize];
			for(int i=0;i<resultListSize;i++) {
				result[i] = ((AppUsage)resultList.get(i)).getId();
			}
		}
		else {
			result = EMPTY_STRING_ARRAY;
		}
		entityManager.close();
		return result;
	}

	public boolean containsAppUsage(String auid)
			throws InternalServerErrorException {
		
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		boolean result = false;
		if(!
			entityManager.createNamedQuery("selectAppUsageFromKey")
					.setParameter("id", auid)
					.getResultList()
					.isEmpty()) {
			result = true;
		} 
		entityManager.close();
		return result;
	}

	public void removeAppUsage(String auid)
			throws InternalServerErrorException {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		// remove all docs
		entityManager.createNamedQuery("deleteDocumentsFromAppUsage").setParameter("auid", auid).executeUpdate();
		// remove all collections
		entityManager.createNamedQuery("deleteCollectionsFromAppUsage").setParameter("auid", auid).executeUpdate();
		// remove the app usage
		entityManager.createNamedQuery("deleteAppUsageFromKey").setParameter("auid", auid).executeUpdate();
		entityManager.flush();
		entityManager.close();
	}

	public void addCollection(String appUsage, String collectionName)
			throws InternalServerErrorException {

		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Collection collection = new Collection(appUsage,collectionName);
		entityManager.persist(collection);
		entityManager.flush();
		entityManager.close();
	}

	public void addUser(String appUsage, String user)
			throws InternalServerErrorException {
		addCollection(appUsage, "users/" + user);
	}
	
	public String[] getDocuments(String auid, String collection)
			throws InternalServerErrorException {
	
		String[] result = null;
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		List resultList = entityManager.createNamedQuery("selectDocumentsFromCollection")
		.setParameter("auid", auid)
		.setParameter("collectionName", collection)
		.getResultList();
		int resultListSize = resultList.size();
		if (resultListSize > 0) {
			result = new String[resultListSize];
			for(int i=0;i<resultListSize;i++) {
				result[i] = ((Document)resultList.get(i)).getKey().getDocumentName();
			}
		}
		else {
			result = EMPTY_STRING_ARRAY;
		}
		entityManager.close();
		return result;
	}
	
	public String[] getCollections(String auid)
			throws InternalServerErrorException {
		return getCollections(auid, null);
	}

	private String[] getCollections(String auid, String expression)
			throws InternalServerErrorException {
		
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		List resultList = null;
		if (expression != null) {
			resultList = entityManager.createNamedQuery("selectCollectionsFromAppUsageAndExpression")
			.setParameter("auid", auid)
			.setParameter("expression", expression)
			.getResultList();
		}
		else {
			resultList = entityManager.createNamedQuery("selectCollectionsFromAppUsage")
			.setParameter("auid", auid)
			.getResultList();
		}
		
		String[] result = null;
		int resultListSize = resultList.size();
		if (resultListSize > 0) {
			result = new String[resultListSize];
			for(int i=0;i<resultListSize;i++) {
				result[i] = ((Collection)resultList.get(i)).getKey().getCollectionName();
			}
		}
		else {
			result = EMPTY_STRING_ARRAY;
		}
		entityManager.close();
		return result;
	}

	public String[] getUsers(String auid)
			throws InternalServerErrorException {
		
		HashSet<String> resultSet = new HashSet<String>();
		for(String collectionName: getCollections(auid, "users/%")) {
			collectionName = collectionName.substring("users/".length());
			int slashIndex = collectionName.indexOf('/');
			if (slashIndex > 0) {
				resultSet.add(collectionName.substring(0,slashIndex));
			}
			else {
				resultSet.add(collectionName);
			}
		}
	
		int resultSetSize = resultSet.size();
		if (resultSetSize > 0) {
			return resultSet.toArray(new String[resultSetSize]);
		}
		else {
			return EMPTY_STRING_ARRAY;
		}
		
	}

	public void removeUser(String auid, String user)
			throws InternalServerErrorException {
		try {
			EntityManager entityManager = entityManagerFactory.createEntityManager();
			XCAPClient client = new XCAPClientImpl(
					ServerConfiguration.SERVER_HOST,
					ServerConfiguration.SERVER_PORT,
					ServerConfiguration.XCAP_ROOT);
			// get user collections
			String[] collectionNames = getCollections(auid, "users/" + user
					+ '%');
			for (String collectionName : collectionNames) {
				for (String documentName : getDocuments(auid, collectionName)) {
					// remove docs through xcap so app usages process
					// interdependencies
					client.delete(new DocumentUriKey(new DocumentSelector(auid,
							collectionName, documentName)),null);
				}
				// remove collection
				entityManager.createNamedQuery("deleteCollectionFromKey")
					.setParameter("auid", auid)
					.setParameter("collectionName", collectionName)
					.executeUpdate();
			}
			client.shutdown();
			entityManager.flush();
			entityManager.close();
		} catch (Exception e) {
			logger.error(e);
			throw new InternalServerErrorException(e.getStackTrace().toString());
		}
	}

}