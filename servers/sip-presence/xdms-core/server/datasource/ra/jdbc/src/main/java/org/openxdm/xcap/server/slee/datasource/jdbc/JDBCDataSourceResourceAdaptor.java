package org.openxdm.xcap.server.slee.datasource.jdbc;

import javax.slee.resource.ResourceException;

import org.apache.log4j.Logger;
import org.openxdm.xcap.common.datasource.DataSource;
import org.openxdm.xcap.common.error.InternalServerErrorException;
import org.openxdm.xcap.server.slee.resource.datasource.AbstractDataSourceResourceAdaptor;

/**
 * This is the OPENXDM JDBC DataSource Resource Adaptor's Implementation.
 * 
 * @author Eduardo Martins
 * @version 1.0
 * 
 */

public class JDBCDataSourceResourceAdaptor extends AbstractDataSourceResourceAdaptor {
    
 	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static private transient Logger logger = Logger.getLogger(JDBCDataSourceResourceAdaptor.class);
        
    private String serverHost;
    private int serverPort; 
    private String serverXcapRoot; 
    
    private JDBCDataSource dataSource = null; 
           
    public JDBCDataSourceResourceAdaptor() { }

	public void entityActivated() throws ResourceException {
	   super.entityActivated();
	   logger.info("Configuration: serverhost="+serverHost+",serverPort="+serverPort+",serverXcapRoot="+serverXcapRoot);
	   // open datasource			
	   try {
			dataSource = new JDBCDataSource();
			dataSource.open();
			logger.info("jdbc database open");
		} catch (InternalServerErrorException e) {
			logger.error("Can't open data source",e);
		}
	}

	public void entityDeactivated() {
		super.entityDeactivated();
		// close datasource
		try {
        	dataSource.close();
        	logger.info("data source closed with sucess");
        }
        catch (Exception e) {
        	logger.error("Can't close data source",e);
        }
        dataSource = null;
	}

	public DataSource getDataSource() {
		return dataSource;
	}
	
	public Logger getLogger() {
		return logger;
	}

}

