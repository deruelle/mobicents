package org.openxdm.xcap.server.slee.resource.datasource;


/**
 * Activity Object for the DataSource Resource Adaptor. Represents the events
 * that update documents in a specific application usage.
 * 
 * @author Eduardo Martins
 * 
 */
public class AppUsageActivity extends ActivityObject {

	protected AppUsageActivity(String auid, DataSourceResourceAdaptor ra) {
		super(auid,ra);		
	}

	public String getAUID() {
		return id;
	}

	public String toString() {
		return new StringBuilder("AppUsageActivity[auid="
				+ getAUID()+ "]").toString();
	}
	
}
