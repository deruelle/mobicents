package org.openxdm.xcap.server.slee.datasource.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.openxdm.xcap.common.datasource.DataSource;
import org.openxdm.xcap.common.datasource.Document;
import org.openxdm.xcap.common.error.InternalServerErrorException;
import org.openxdm.xcap.common.uri.DocumentSelector;

/**
 * @author Luis Barreiro
 * @author eduardomartins
 */
public class JDBCDataSource implements DataSource {

	private static Logger logger = Logger.getLogger(JDBCDataSource.class);

	/**
	 * A register of database info for an app usage
	 * @author eduardomartins
	 *
	 */
	private class AppUsageRegister {

		private final String auid;

		private final String docs_table;

		private final String collections_table;

		public AppUsageRegister(String auid, String collections_table,
				String docs_table) {
			this.auid = auid;
			this.collections_table = collections_table;
			this.docs_table = docs_table;
		}

		public String getAuid() {
			return auid;
		}

		public String getCollectionsTable() {
			return collections_table;
		}

		public String getDocsTable() {
			return docs_table;
		}

		public int hashCode() {
			return auid.hashCode();
		}

		public boolean equals(Object obj) {
			if (obj != null && obj.getClass() == this.getClass()) {
				return this.auid.equals(((AppUsageRegister) obj).auid);
			} else {
				return false;
			}
		}
	}

	/**
	 * The map of app usage registers active
	 */
	private ConcurrentHashMap<String, AppUsageRegister> activeAppUsages = new ConcurrentHashMap<String, AppUsageRegister>();

	/**
	 * The datasource for the database
	 */
	private javax.sql.DataSource jdbcDataSource = null;

	/**
	 * The datasource state
	 */
	private AtomicBoolean open = new AtomicBoolean(false);

	public JDBCDataSource() throws InternalServerErrorException {
		try {
			this.jdbcDataSource = ((javax.sql.DataSource) new InitialContext()
					.lookup("java:/DefaultDS"));
		} catch (Exception e) {
			throw new InternalServerErrorException(
					"Failed to create sql data source. Exception: "
							+ e.getCause() + " Message: " + e.getMessage());
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
		Connection connection = null;
		try {
			connection = jdbcDataSource.getConnection();
			DatabaseMetaData dbm = connection.getMetaData();
			rs = dbm.getTables(null, null, "APP_USAGES", null);
			if (!rs.next()) {
				// table does not exist, create it
				logger.info("app_usages table do not exist, creating...");
				rs.close();
				stmt = connection.createStatement();
				rs = stmt.executeQuery(createAppUsagesTableQuery());
				// TODO find way to ensure table as utf8
			} else {
				// table exists, get all app usages and create registers
				logger
						.info("app_usages table exists, creating registers of existent app usages data ...");
				rs.close();
				stmt = connection.createStatement();
				rs = stmt.executeQuery(getAllAppUsagesQuery());
				while (rs.next()) {
					String auid = rs.getString(1);
					String collections_table = rs.getString(2);
					String docs_table = rs.getString(3);
					if (auid != null && collections_table != null
							&& docs_table != null) {
						activeAppUsages.put(auid, new AppUsageRegister(auid,
								collections_table, docs_table));
						logger.info("created register for app usage " + auid
								+ " collections_table=" + collections_table
								+ " docs_table=" + docs_table);
					}
				}
			}
		} catch (SQLException e) {
			logger.error("SQLException: " + e.getMessage());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { // ignore
					logger.error(sqlEx);
					rs = null;
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { // ignore
					logger.error(sqlEx);
					stmt = null;
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException sqlEx) { // ignore
					logger.error(sqlEx);
					stmt = null;
				}
			}
		}

		// change state
		open.set(true);
	}

	public void close() throws InternalServerErrorException {
		requiresDatasourceOpen();
		// do nothing besides state change
		open.set(false);
	}

	private String selectAppUsageCollectionQuery(String auid, String collection) {
		return "SELECT collection FROM "
				+ activeAppUsages.get(auid).getCollectionsTable()
				+ " WHERE collection ='" + collection + "';";
	}

	public String getExistingCollection(String auid, String startingCollection)
			throws InternalServerErrorException {

		requiresDatasourceOpen();

		ResultSet rs = null;
		Statement stmt = null;
		Connection connection = null;
		try {
			while(true) {
				connection = jdbcDataSource.getConnection();
				stmt = connection.createStatement();
				rs = stmt.executeQuery(selectAppUsageCollectionQuery(auid,
						startingCollection));
				if (rs.next()) {
					return startingCollection;
				} else {
					int index = startingCollection.lastIndexOf('/');
					if(index > 0) {
						startingCollection = startingCollection.substring(0, index);
					}
					else {
						break;
					}
				}
				stmt.close();
				rs.close();
			}
			return "";
		} catch (SQLException e) {
			logger.error("SQLException: " + e.getMessage());
			throw new InternalServerErrorException(
					"Failed to retreive existing collection. Exception: SQLException Message: "
							+ e.getMessage());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { // ignore
					logger.error(sqlEx);
					rs = null;
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { // ignore
					logger.error(sqlEx);
					stmt = null;
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException sqlEx) { // ignore
					logger.error(sqlEx);
					stmt = null;
				}
			}
		}

	}

	private String selectDocumentQuery(DocumentSelector documentSelector) {
		return "SELECT doc_etag,doc_xml FROM "
				+ activeAppUsages.get(documentSelector.getAUID())
						.getDocsTable() + " WHERE doc_name = '"
				+ documentSelector.getDocumentName() + "' AND doc_parent = '"
				+ documentSelector.getDocumentParent() + "';";
	}

	public Document getDocument(DocumentSelector documentSelector)
			throws InternalServerErrorException {

		requiresDatasourceOpen();

		ResultSet rs = null;
		Statement stmt = null;
		Connection connection = null;
		try {
			connection = jdbcDataSource.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(selectDocumentQuery(documentSelector));
			if (rs.next()) {
				String eTag = rs.getString(1);
				String xml = rs.getString(2);
				if (eTag != null && xml != null) {
					return new JDBCDocument(eTag, xml);
				} else {
					throw new InternalServerErrorException(
							"Failed to retreive document. Invalid values in database");
				}
			} else {
				return null;
			}
		} catch (SQLException e) {
			logger.error("SQLException: " + e.getMessage());
			throw new InternalServerErrorException(
					"Failed to retreive document. Exception: SQLException Message: "
							+ e.getMessage());
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException sqlEx) { // ignore
					logger.error(sqlEx);
					stmt = null;
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { // ignore
					logger.error(sqlEx);
					rs = null;
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { // ignore
					logger.error(sqlEx);
					stmt = null;
				}
			}
		}

	}

	private String insertDocumentQuery(DocumentSelector documentSelector,
			String eTag, String xml) {
		return "INSERT INTO "
				+ activeAppUsages.get(documentSelector.getAUID())
						.getDocsTable()
				+ " (doc_name,doc_parent,doc_etag,doc_xml) VALUES ('"
				+ documentSelector.getDocumentName() + "','"
				+ documentSelector.getDocumentParent() + "','" + eTag + "','"
				+ xml + "');";
	}

	public void createDocument(DocumentSelector documentSelector, String eTag,
			String xml) throws InternalServerErrorException {

		requiresDatasourceOpen();

		ResultSet rs = null;
		Statement stmt = null;
		Connection connection = null;
		try {
			connection = jdbcDataSource.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(insertDocumentQuery(documentSelector, eTag,
					xml));
		} catch (SQLException e) {
			logger.error("SQLException: " + e.getMessage());
			throw new InternalServerErrorException(
					"Failed to insert document. Exception: SQLException Message: "
							+ e.getMessage());
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException sqlEx) { // ignore
					logger.error(sqlEx);
					stmt = null;
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { // ignore
					logger.error(sqlEx);
					rs = null;
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { // ignore
					logger.error(sqlEx);
					stmt = null;
				}
			}
		}
	}

	private String updateDocumentQuery(DocumentSelector documentSelector,
			String eTag, String xml) {
		return "UPDATE "
				+ activeAppUsages.get(documentSelector.getAUID())
						.getDocsTable() + " SET doc_etag = '" + eTag
				+ "', doc_xml = '" + xml + "' WHERE doc_name = '"
				+ documentSelector.getDocumentName() + "' AND doc_parent = '"
				+ documentSelector.getDocumentParent() + "';";
	}

	public void updateDocument(DocumentSelector documentSelector, String eTag,
			String xml)
			throws InternalServerErrorException {

		requiresDatasourceOpen();

		ResultSet rs = null;
		Statement stmt = null;
		Connection connection = null;
		try {
			connection = jdbcDataSource.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(updateDocumentQuery(documentSelector, eTag,
					xml));
		} catch (SQLException e) {
			logger.error("SQLException: " + e.getMessage());
			throw new InternalServerErrorException(
					"Failed to update document. Exception: SQLException Message: "
							+ e.getMessage());
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException sqlEx) { // ignore
					logger.error(sqlEx);
					stmt = null;
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { // ignore
					logger.error(sqlEx);
					rs = null;
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { // ignore
					logger.error(sqlEx);
					stmt = null;
				}
			}
		}
	}

	private String deleteDocumentQuery(DocumentSelector documentSelector) {
		return "DELETE FROM "
				+ activeAppUsages.get(documentSelector.getAUID())
						.getDocsTable() + " WHERE doc_name = '"
				+ documentSelector.getDocumentName() + "' AND doc_parent = '"
				+ documentSelector.getDocumentParent() + "';";
	}

	public void deleteDocument(DocumentSelector documentSelector)
			throws InternalServerErrorException {

		requiresDatasourceOpen();

		ResultSet rs = null;
		Statement stmt = null;
		Connection connection = null;
		try {
			connection = jdbcDataSource.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(deleteDocumentQuery(documentSelector));
		} catch (SQLException e) {
			logger.error("SQLException: " + e.getMessage());
			throw new InternalServerErrorException(
					"Failed to delete document. Exception: SQLException Message: "
							+ e.getMessage());
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException sqlEx) { // ignore
					logger.error(sqlEx);
					stmt = null;
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { // ignore
					logger.error(sqlEx);
					rs = null;
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { // ignore
					logger.error(sqlEx);
					stmt = null;
				}
			}
		}
	}

	private String getAppUsageIdInDatabase(String auid) {
		return auid.replace('-', '_').replace('.', '_');
	}
	
	private String getAppUsageColsTableName(String auid) {
		 return getAppUsageIdInDatabase(auid) + "_cols";
	}

	private String getAppUsageDocsTableName(String auid) {
		return getAppUsageIdInDatabase(auid) + "_docs";
	}

	private String insertAppUsageQuery(String appUsage) {
		String appUsage_Cols = getAppUsageColsTableName(appUsage);
		String appUsage_Docs = getAppUsageDocsTableName(appUsage);
		// insert app usage, create tables and insert base collections
		return "INSERT INTO app_usages "
				+ " (auid,collections_table,docs_table) VALUES ('"+ appUsage+ "','"
				+ appUsage_Cols+ "','"
				+ appUsage_Docs+ "');"
				+ " CREATE TABLE "+ appUsage_Docs
				+ " (doc_name VARCHAR(64), doc_parent VARCHAR(64), doc_etag VARCHAR(16), doc_xml LONGVARCHAR, PRIMARY KEY (doc_name,doc_parent));"
				+ " CREATE TABLE " + appUsage_Cols
				+ " (collection VARCHAR(64), PRIMARY KEY (collection));"
				+ " INSERT INTO " + appUsage_Cols
				+ " (collection) VALUES ('global');" + " INSERT INTO "
				+ appUsage_Cols + " (collection) VALUES ('users');";
	}

	public void addAppUsage(String appUsage)
			throws InternalServerErrorException {

		requiresDatasourceOpen();

		if (!activeAppUsages.containsKey(appUsage)) {
			ResultSet rs = null;
			Statement stmt = null;
			Connection connection = null;
			try {				
				connection = jdbcDataSource.getConnection();
				stmt = connection.createStatement();
				rs = stmt.executeQuery(insertAppUsageQuery(appUsage));
				activeAppUsages.put(appUsage, new AppUsageRegister(appUsage,
						getAppUsageColsTableName(appUsage),
						getAppUsageDocsTableName(appUsage)));
			} catch (SQLException e) {
				logger.error("SQLException: " + e.getMessage());
				throw new InternalServerErrorException(
						"Failed to add app usage. Exception: SQLException Message: "
								+ e.getMessage());
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException sqlEx) { // ignore
						logger.error(sqlEx);
						rs = null;
					}
				}
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException sqlEx) { // ignore
						logger.error(sqlEx);
						stmt = null;
					}
				}
				if (connection != null) {
					try {
						connection.close();
					} catch (SQLException sqlEx) { // ignore
						logger.error(sqlEx);
						stmt = null;
					}
				}
				
			}
		}
	}

	public String[] getAppUsages() throws InternalServerErrorException {

		requiresDatasourceOpen();

		Set<String> s = activeAppUsages.keySet();
		return s.toArray(new String[s.size()]);
	}

	public boolean containsAppUsage(String appUsage)
			throws InternalServerErrorException {
		requiresDatasourceOpen();
		return activeAppUsages.containsKey(appUsage);
	}

	private String removeAppUsageQuery(AppUsageRegister aur) {
		return "DELETE FROM app_usages WHERE auid = '" + aur.getAuid() + "';"
				+ "DROP TABLE " + aur.getCollectionsTable() + "; "
				+ "DROP TABLE " + aur.getDocsTable() + ";";
	}

	public void removeAppUsage(String appUsage)
			throws InternalServerErrorException {

		requiresDatasourceOpen();

		AppUsageRegister aur = activeAppUsages.get(appUsage);
		if (aur != null) {
			ResultSet rs = null;
			Statement stmt = null;
			Connection connection = null;
			try {
				connection = jdbcDataSource.getConnection();
				stmt = connection.createStatement();
				rs = stmt.executeQuery(removeAppUsageQuery(aur));
			} catch (SQLException e) {
				logger.error("SQLException: " + e.getMessage());
				throw new InternalServerErrorException(
						"Failed to remove app usage. Exception: SQLException Message: "
								+ e.getMessage());
			} finally {
				if (connection != null) {
					try {
						connection.close();
					} catch (SQLException sqlEx) { // ignore
						logger.error(sqlEx);
						stmt = null;
					}
				}
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException sqlEx) { // ignore
						logger.error(sqlEx);
						rs = null;
					}
				}
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException sqlEx) { // ignore
						logger.error(sqlEx);
						stmt = null;
					}
				}
			}
			activeAppUsages.remove(appUsage);
		}
	}

	private String insertCollectionQuery(AppUsageRegister aur, String collection) {
		return "INSERT INTO " + aur.getCollectionsTable()
				+ " (collection) VALUES ('" + collection + "');";
	}

	public void addCollection(String appUsage, String collection)
			throws InternalServerErrorException {

		requiresDatasourceOpen();

		AppUsageRegister aur = activeAppUsages.get(appUsage);
		if (aur != null) {
			ResultSet rs = null;
			Statement stmt = null;
			Connection connection = null;
			try {
				connection = jdbcDataSource.getConnection();
				stmt = connection.createStatement();
				rs = stmt.executeQuery(insertCollectionQuery(aur, collection));
			} catch (SQLException e) {
				logger.error("SQLException: " + e.getMessage());
				throw new InternalServerErrorException(
						"Failed to add collection " + collection
								+ " to app usage " + appUsage
								+ ". Exception: SQLException Message: "
								+ e.getMessage());
			} finally {
				if (connection != null) {
					try {
						connection.close();
					} catch (SQLException sqlEx) { // ignore
						logger.error(sqlEx);
						stmt = null;
					}
				}
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException sqlEx) { // ignore
						logger.error(sqlEx);
						rs = null;
					}
				}
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException sqlEx) { // ignore
						logger.error(sqlEx);
						stmt = null;
					}
				}
			}
		} else {
			throw new InternalServerErrorException("unknown appUsage \""
					+ appUsage + "\"");
		}
	}

	public void addUser(String appUsage, String user)
			throws InternalServerErrorException {
		addCollection(appUsage, "users/" + user);
	}

	private String selectCollectionsQuery(AppUsageRegister aur,
			String expression) {
		return "SELECT collection FROM " + aur.getCollectionsTable()
				+(expression != null ? " WHERE collection LIKE '" + expression+ "';" : ";");
	}

	public String[] getCollections(String auid)
			throws InternalServerErrorException {
		return getCollections(auid, null);
	}

	private String[] getCollections(String auid, String expression)
			throws InternalServerErrorException {

		requiresDatasourceOpen();

		AppUsageRegister aur = activeAppUsages.get(auid);
		if (aur != null) {
			ResultSet rs = null;
			Statement stmt = null;
			Connection connection = null;
			try {
				connection = jdbcDataSource.getConnection();
				stmt = connection.createStatement();
				rs = stmt.executeQuery(selectCollectionsQuery(aur, expression));
				ArrayList<String> list = new ArrayList<String>();
				while (rs.next()) {
					list.add(rs.getString(1));
				}
				return list.toArray(new String[list.size()]);
			} catch (SQLException e) {
				logger.error("SQLException: " + e.getMessage());
				throw new InternalServerErrorException(
						"Failed to get collections for app usage " + auid
								+ ". Exception: SQLException Message: "
								+ e.getMessage());
			} finally {
				if (connection != null) {
					try {
						connection.close();
					} catch (SQLException sqlEx) { // ignore
						logger.error(sqlEx);
						stmt = null;
					}
				}
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException sqlEx) { // ignore
						logger.error(sqlEx);
						rs = null;
					}
				}
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException sqlEx) { // ignore
						logger.error(sqlEx);
						stmt = null;
					}
				}
			}
		} else {
			throw new InternalServerErrorException("unknown appUsage \""
					+ auid + "\"");
		}
	}

	public String[] getUsers(String auid)
			throws InternalServerErrorException {
		// TODO use getCollections with expression
		return null;
	}

	private String removeUserQuery(AppUsageRegister aur, String user) {
		return "DELETE FROM " + aur.getCollectionsTable()
				+ " WHERE collection LIKE 'users/" + user + "%';"
				+ "DELETE FROM " + aur.getDocsTable()
				+ " WHERE doc_parent LIKE 'users/" + user + "%';";
	}

	// FIXME the usage of this method may broke composition docs, add function to let the appusage refresh those kind of docs
	public void removeUser(String appUsage, String user)
			throws InternalServerErrorException {

		requiresDatasourceOpen();

		AppUsageRegister aur = activeAppUsages.get(appUsage);
		if (aur != null) {
			ResultSet rs = null;
			Statement stmt = null;
			Connection connection = null;
			try {
				connection = jdbcDataSource.getConnection();
				stmt = connection.createStatement();
				rs = stmt.executeQuery(removeUserQuery(aur, user));
			} catch (SQLException e) {
				logger.error("SQLException: " + e.getMessage());
				throw new InternalServerErrorException("Failed to remove user "
						+ user + "from app usage " + appUsage
						+ ". Exception: SQLException Message: "
						+ e.getMessage());
			} finally {
				if (connection != null) {
					try {
						connection.close();
					} catch (SQLException sqlEx) { // ignore
						logger.error(sqlEx);
						stmt = null;
					}
				}
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException sqlEx) { // ignore
						logger.error(sqlEx);
						rs = null;
					}
				}
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException sqlEx) { // ignore
						logger.error(sqlEx);
						stmt = null;
					}
				}
			}
		}
	}

}
