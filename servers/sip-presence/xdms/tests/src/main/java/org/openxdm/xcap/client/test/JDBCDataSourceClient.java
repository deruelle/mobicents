package org.openxdm.xcap.client.test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.openxdm.xcap.common.datasource.DataSource;
import org.openxdm.xcap.common.datasource.Document;
import org.openxdm.xcap.common.error.InternalServerErrorException;
import org.openxdm.xcap.common.uri.DocumentSelector;

/**
 * @author eduardomartins
 */
public class JDBCDataSourceClient implements DataSource {
	
	/**
	 * A register of database info for an app usage
	 * @author eduardomartins
	 *
	 */
	private class AppUsageRegister {
		
		private final String auid;
		private final String docs_table;
		private final String collections_table;
		
		public AppUsageRegister(String auid,String collections_table,String docs_table) {
			this.auid = auid;
			this.collections_table = collections_table;
			this.docs_table = docs_table;
		}
		
		public String getAuid() { return auid; }
		public String getCollectionsTable() { return collections_table; }
		public String getDocsTable() { return docs_table; }
		
		public int hashCode() {
			return auid.hashCode();
		}
		
		public boolean equals(Object obj) {
			if (obj != null && obj.getClass() == this.getClass()) {
				return this.auid.equals(((AppUsageRegister)obj).auid);
			}
			else {
				return false;
			}
		}
	}
	
	/**
	 * The map of app usage registers active
	 */
	private ConcurrentHashMap<String, AppUsageRegister> activeAppUsages = new ConcurrentHashMap<String, AppUsageRegister>();
	
	/**
	 * The connection to the database
	 */
    private Connection connection = null;
    
    /**
     * The datasource state
     */
    private AtomicBoolean open = new AtomicBoolean(false);

    public JDBCDataSourceClient(String connectionURL) throws InternalServerErrorException {
    	try {
    		this.connection = DriverManager.getConnection(connectionURL);
    	}
    	catch (Exception e) {
    		throw new InternalServerErrorException("Failed to connect to database. Exception: "+e.getCause()+" Msg:"+e.getMessage());
    	}
    }
    
    private void requiresDatasourceOpen() throws InternalServerErrorException {
    	if (!open.get()) {
    		throw new InternalServerErrorException("datasource is closed.");
    	}
    }
    
    private void requiresDatasourceClosed() throws InternalServerErrorException {
    	if (open.get()) {
    		throw new InternalServerErrorException("datasource is open.");
    	}
    }
    
    // TODO add support to more jdbc drivers other than hsqldb
    private String createAppUsagesTableQuery() {
    	return "CREATE TABLE app_usages (auid VARCHAR(128), collections_table VARCHAR(128), docs_table VARCHAR(128), PRIMARY KEY (auid));";
    }
    
    private String getAllAppUsagesQuery() {
    	return "SELECT auid,collections_table,docs_table FROM app_usages;";
    }

    public void open() throws InternalServerErrorException {

    	requiresDatasourceClosed();

    	// check app_usages table exists
    	ResultSet rs = null;
    	Statement stmt = null;
    	try {
    		DatabaseMetaData dbm = connection.getMetaData();
    		rs = dbm.getTables(null, null,"APP_USAGES", null);
    		if (!rs.next()) {
    			// table does not exist, create it
    			System.out.println("app_usages table do not exist, creating...");
    			rs.close();
    			stmt = connection.createStatement();
    			rs = stmt.executeQuery(createAppUsagesTableQuery());
    			// TODO find way to ensure table as utf8
    		}
    		else {
    			// table exists, get all app usages and create registers
    			System.out.println("app_usages table exists, creating registers of existent app usages data ...");
    			rs.close();
    			stmt = connection.createStatement();
    			rs = stmt.executeQuery(getAllAppUsagesQuery());
    			while(rs.next()) {
    				String auid = rs.getString(1);
    				String collections_table = rs.getString(2);
    				String docs_table = rs.getString(3);
    				if (auid != null && collections_table != null && docs_table != null) {
    					activeAppUsages.put(auid, new AppUsageRegister(auid,collections_table,docs_table));
    					System.out.println("created register for app usage "+auid+" collections_table="+collections_table+" docs_table="+docs_table);
    				}
    			}
    		}
    	} catch (SQLException e) {
    		System.out.println("SQLException: " + e.getMessage());
    	} finally {
    		if (rs != null) {
    			try {
    				rs.close();
    			} catch (SQLException sqlEx) { // ignore 
    				rs = null;
    			}
    		}
    		if (stmt != null) {
    			try {
    				stmt.close();
    			} catch (SQLException sqlEx) { // ignore 
    				stmt = null;
    			}
    		}
    	}

    	// change state
    	open.set(true);
    }

    public void close() throws InternalServerErrorException {
    	requiresDatasourceOpen();
        try {
            connection.close();
        } catch (SQLException e) {
            throw new InternalServerErrorException("unable to close data source");
        }
        // change state
        open.set(false);
    }
  
    public String[] getAppUsages() throws InternalServerErrorException {
    	
    	requiresDatasourceOpen();
    	
    	Set<String> s = activeAppUsages.keySet();
    	return s.toArray(new String[s.size()]);
    }

    
    private String insertCollectionQuery(AppUsageRegister aur, String collection) {
    	return new StringBuilder("INSERT INTO ").append(aur.getCollectionsTable())
    		.append(" (collection) VALUES ('")
    		.append(collection).append("');")
    		.toString();
    }
    
    public void addCollection(String appUsage, String collection) throws InternalServerErrorException {

    	requiresDatasourceOpen();
    	
    	AppUsageRegister aur = activeAppUsages.get(appUsage);
    	if (aur != null) {
    		ResultSet rs = null;
    		Statement stmt = null;
    		try {
    			stmt = connection.createStatement();
    			rs = stmt.executeQuery(insertCollectionQuery(aur,collection));
    		} catch (SQLException e) {
    			System.out.println("SQLException: " + e.getMessage());
    			throw new InternalServerErrorException("Failed to add collection "+collection+" to app usage "+appUsage+". Exception: SQLException Message: "+e.getMessage());
    		} finally {
    			if (rs != null) {
    				try {
    					rs.close();
    				} catch (SQLException sqlEx) { // ignore 
    					rs = null;
    				}
    			}
    			if (stmt != null) {
    				try {
    					stmt.close();
    				} catch (SQLException sqlEx) { // ignore 
    					stmt = null;
    				}
    			}
    		}
    	}
    	else {
    		throw new InternalServerErrorException("unknown appUsage \"" + appUsage + "\"");
    	}
    }

    public void addUser(String appUsage, String user) throws InternalServerErrorException {
    	addCollection(appUsage, "users/"+user);
    }    
   
    private String removeUserQuery(AppUsageRegister aur, String user) {
    	return new StringBuilder("DELETE FROM ").append(aur.getCollectionsTable()).append(" WHERE collection LIKE 'users/").append(user).append("%';")
    		.append("DELETE FROM ").append(aur.getDocsTable()).append(" WHERE doc_parent LIKE 'users/").append(user).append("%';")
    		.toString();
    }
    
    // FIXME the usage of this method may broke composition docs, add function to let the appusage refresh those kind of docs
    public void removeUser(String appUsage, String user) throws InternalServerErrorException {
       
    	requiresDatasourceOpen();
    	
    	AppUsageRegister aur = activeAppUsages.get(appUsage);
    	if (aur != null) {
    		ResultSet rs = null;
    		Statement stmt = null;
    		try {
    			stmt = connection.createStatement();
    			rs = stmt.executeQuery(removeUserQuery(aur,user));
    		} catch (SQLException e) {
    			System.out.println("SQLException: " + e.getMessage());
    			throw new InternalServerErrorException("Failed to remove user "+user+ "from app usage "+appUsage+". Exception: SQLException Message: "+e.getMessage());
    		} finally {
    			if (rs != null) {
    				try {
    					rs.close();
    				} catch (SQLException sqlEx) { // ignore 
    					rs = null;
    				}
    			}
    			if (stmt != null) {
    				try {
    					stmt.close();
    				} catch (SQLException sqlEx) { // ignore 
    					stmt = null;
    				}
    			}
    		}
    	}
    }

    // -- UNSUPPORTED METHODS
    
	public void addAppUsage(String appUsage)
			throws InternalServerErrorException {
		throw new UnsupportedOperationException();		
	}

	public boolean containsAppUsage(String appUsage)
			throws InternalServerErrorException {
		throw new UnsupportedOperationException();
	}

	public void createDocument(DocumentSelector documentSelector, String tag,
			String xml) throws InternalServerErrorException {
		throw new UnsupportedOperationException();
	}

	public void deleteDocument(DocumentSelector documentSelector)
			throws InternalServerErrorException {
		throw new UnsupportedOperationException();
	}

	public String[] getCollections(String appUsage)
			throws InternalServerErrorException {
		throw new UnsupportedOperationException();
	}

	public Document getDocument(DocumentSelector documentSelector)
			throws InternalServerErrorException {
		throw new UnsupportedOperationException();
	}

	public String getExistingCollection(String auid, String startingCollection)
			throws InternalServerErrorException {
		throw new UnsupportedOperationException();
	}

	public String[] getUsers(String appUsage)
			throws InternalServerErrorException {
		throw new UnsupportedOperationException();
	}

	public void removeAppUsage(String appUsage)
			throws InternalServerErrorException {
		throw new UnsupportedOperationException();
	}

	public void updateDocument(DocumentSelector documentSelector,
			String newETag, String newXml) throws InternalServerErrorException {
		throw new UnsupportedOperationException();
	}

	public String[] getDocuments(String auid, String collection)
			throws InternalServerErrorException {
		throw new UnsupportedOperationException();
	}
}
