package org.openxdm.xcap.server.slee.appusage.omapresrules;

import java.net.URL;

import javax.xml.validation.Schema;

import org.apache.log4j.Logger;
import org.openxdm.xcap.common.appusage.AppUsageFactory;
import org.openxdm.xcap.common.error.InternalServerErrorException;
import org.openxdm.xcap.common.xml.SchemaContext;
import org.openxdm.xcap.server.slee.AbstractAppUsageSbb;

/**
 * JAIN SLEE Root Sbb for oma-pres-rules Xcap application usage.  
 * @author Eduardo Martins
 *
 */
public abstract class OMAPresRulesAppUsageSbb extends AbstractAppUsageSbb {

	private static Logger logger = Logger
			.getLogger(OMAPresRulesAppUsageSbb.class);

	// MANDATORY ABSTRACT METHODS IMPL FOR A APP USAGE ROOT SBB, AbstractAppUsageSbb will invoke them

	public Logger getLogger() {
		return logger;
	}

	public AppUsageFactory getAppUsageFactory() throws InternalServerErrorException {

		AppUsageFactory appUsageFactory = null;

		try {
			// load schema files to dom documents
			logger.info("Loading schemas from file system...");
			URL schemaDirURL = this.getClass().getResource("xsd");
			if (schemaDirURL != null) {
				// create schema context
				SchemaContext schemaContext = SchemaContext
						.fromDir(schemaDirURL.toURI());
				// get schema from context
				Schema schema = schemaContext
						.getCombinedSchema(OMAPresRulesAppUsage.DEFAULT_DOC_NAMESPACE);
				logger.info("Schemas loaded.");
				// create and return factory
				appUsageFactory = new OMAPresRulesAppUsageFactory(schema);
				
			} else {
				logger.warn("Schemas dir resource not found!");
			}
		} catch (Exception e) {
			logger.error("Unable to load app usage schemas from file system", e);
		}

		if (appUsageFactory == null) {
			throw new InternalServerErrorException(
					"Unable to get app usage factory");
		} else {
			return appUsageFactory;
		}
	}	

	public String getAUID() {
		return OMAPresRulesAppUsage.ID;
	}

}