package org.openxdm.xcap.server.slee.resource.datasource;

import org.openxdm.xcap.common.datasource.DataSource;
import org.openxdm.xcap.common.error.InternalServerErrorException;

public interface DataSourceSbbInterface extends DataSource {

	public void addAppUsage(String appUsage) throws InternalServerErrorException;
	
	public void removeAppUsage(String appUsage) throws InternalServerErrorException;
	
}
