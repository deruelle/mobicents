package org.openxdm.xcap.server.slee.appusage.omapresrules;

import javax.xml.validation.Validator;

import org.openxdm.xcap.common.appusage.AppUsage;

public class OMAPresRulesAppUsage extends AppUsage {

	public static final String ID = "org.openmobilealliance.pres-rules";
	public static final String DEFAULT_DOC_NAMESPACE = "urn:ietf:params:xml:ns:common-policy";
	public static final String SCHEMA_TARGETNAMESPACE = "urn:oma:xml:prs:pres-rules";
	public static final String MIMETYPE = "application/auth-policy+xml";
	
	public OMAPresRulesAppUsage(Validator schemaValidator) {
		super(ID,DEFAULT_DOC_NAMESPACE,MIMETYPE,schemaValidator);
	}
}
