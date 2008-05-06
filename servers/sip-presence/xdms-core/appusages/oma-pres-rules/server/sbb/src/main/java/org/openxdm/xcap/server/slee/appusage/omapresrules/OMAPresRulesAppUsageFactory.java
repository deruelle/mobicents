package org.openxdm.xcap.server.slee.appusage.omapresrules;

import javax.xml.validation.Schema;

import org.openxdm.xcap.common.appusage.AppUsage;
import org.openxdm.xcap.common.appusage.AppUsageFactory;

public class OMAPresRulesAppUsageFactory implements AppUsageFactory {

	private Schema schema = null;
	
	public OMAPresRulesAppUsageFactory(Schema schema) {
		this.schema = schema;
	}
	
	public AppUsage getAppUsageInstance() {
		return new OMAPresRulesAppUsage(schema.newValidator());
	}
	
	public String getAppUsageId() {
		return OMAPresRulesAppUsage.ID;
	}
}
