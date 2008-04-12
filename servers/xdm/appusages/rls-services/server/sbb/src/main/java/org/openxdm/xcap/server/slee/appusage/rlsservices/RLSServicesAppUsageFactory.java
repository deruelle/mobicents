package org.openxdm.xcap.server.slee.appusage.rlsservices;

import javax.xml.validation.Schema;

import org.openxdm.xcap.common.appusage.AppUsage;
import org.openxdm.xcap.common.appusage.AppUsageFactory;

public class RLSServicesAppUsageFactory implements AppUsageFactory {

	private Schema schema = null;
	
	public RLSServicesAppUsageFactory(Schema schema) {
		this.schema = schema;
	}
	
	public AppUsage getAppUsageInstance() {
		return new RLSServicesAppUsage(schema.newValidator());
	}
	
	public String getAppUsageId() {
		return RLSServicesAppUsage.ID;
	}
}
