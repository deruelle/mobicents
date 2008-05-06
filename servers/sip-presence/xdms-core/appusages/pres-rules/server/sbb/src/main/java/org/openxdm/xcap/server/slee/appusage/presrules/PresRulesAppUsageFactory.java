package org.openxdm.xcap.server.slee.appusage.presrules;

import javax.xml.validation.Schema;

import org.openxdm.xcap.common.appusage.AppUsage;
import org.openxdm.xcap.common.appusage.AppUsageFactory;

public class PresRulesAppUsageFactory implements AppUsageFactory {

	private Schema schema = null;
	
	public PresRulesAppUsageFactory(Schema schema) {
		this.schema = schema;
	}
	
	public AppUsage getAppUsageInstance() {
		return new PresRulesAppUsage(schema.newValidator());
	}
	
	public String getAppUsageId() {
		return PresRulesAppUsage.ID;
	}
}