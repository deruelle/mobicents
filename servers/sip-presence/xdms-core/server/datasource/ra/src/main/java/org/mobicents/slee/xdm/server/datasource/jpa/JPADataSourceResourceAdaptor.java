package org.mobicents.slee.xdm.server.datasource.jpa;

import javax.slee.resource.ResourceException;

import org.apache.log4j.Logger;
import org.openxdm.xcap.common.datasource.DataSource;
import org.openxdm.xcap.common.error.InternalServerErrorException;
import org.openxdm.xcap.server.slee.resource.datasource.AbstractDataSourceResourceAdaptor;

/**
 * JPA DataSource Resource Adaptor's Implementation.
 * 
 * @author Eduardo Martins
 * @version 1.0
 * 
 */

public class JPADataSourceResourceAdaptor extends AbstractDataSourceResourceAdaptor {
    
 	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static private transient Logger logger = Logger.getLogger(JPADataSourceResourceAdaptor.class);
        
    private String serverHost;
    private int serverPort; 
    private String serverXcapRoot; 
    
    private JPADataSource dataSource = null; 
           
    public JPADataSourceResourceAdaptor() { }

	public void entityActivated() throws ResourceException {
	   super.entityActivated();
	   logger.info("Configuration: serverhost="+serverHost+",serverPort="+serverPort+",serverXcapRoot="+serverXcapRoot);
	   dataSource = new JPADataSource();
	   try {
		   dataSource.open();
	   } catch (Exception e) {
		   throw new ResourceException("Failed to open datasource",e);
	   }
	}

	public void entityDeactivated() {
		super.entityDeactivated();
		try {
			dataSource.close();
		} catch (Exception e) {
			logger.error("Failed to close datasource", e);
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

