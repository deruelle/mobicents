package org.openxdm.xcap.server.slee.appusage.xcapcaps;

import javax.xml.validation.Schema;

import org.openxdm.xcap.common.appusage.AppUsage;
import org.openxdm.xcap.common.appusage.AppUsageFactory;

public class XCAPCapsAppUsageFactory implements AppUsageFactory {

	private Schema schema = null;
	
	public XCAPCapsAppUsageFactory(Schema schema) {
		this.schema = schema;
	}
	
	public AppUsage getAppUsageInstance() {
		return new XCAPCapsAppUsage(schema.newValidator());
	}

	public String getAppUsageId() {
		return XCAPCapsAppUsage.ID;
	}
	
}
