package org.openxdm.xcap.server.slee.appusage.presrules;

import javax.xml.validation.Validator;

import org.openxdm.xcap.common.appusage.AppUsage;


public class PresRulesAppUsage extends AppUsage {

	public static final String ID = "pres-rules";
	public static final String DEFAULT_DOC_NAMESPACE = "urn:ietf:params:xml:ns:pres-rules";
	public static final String MIMETYPE = "application/auth-policy+xml";
	
	public PresRulesAppUsage(Validator schemaValidator) {
		super(ID,DEFAULT_DOC_NAMESPACE,MIMETYPE,schemaValidator);
	}
		
}
