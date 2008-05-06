package org.openxdm.xcap.server.slee.appusage.resourcelists;

import javax.xml.validation.Schema;

import org.openxdm.xcap.common.appusage.AppUsage;
import org.openxdm.xcap.common.appusage.AppUsageFactory;

public class ResourceListsAppUsageFactory implements AppUsageFactory {

	private Schema schema = null;
	
	public ResourceListsAppUsageFactory(Schema schema) {
		this.schema = schema;
	}
	
	public AppUsage getAppUsageInstance() {
		return new ResourceListsAppUsage(schema.newValidator());
	}
	
	public String getAppUsageId() {
		return ResourceListsAppUsage.ID;
	}
}