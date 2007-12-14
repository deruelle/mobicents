package org.mobicents.slee.resource.sip;

//import javax.slee.resource.SleeEndpoint;

import javax.transaction.SystemException;

import org.apache.log4j.Logger;
import org.mobicents.slee.container.SleeContainer;
import org.mobicents.slee.container.component.ComponentKey; //import org.mobicents.slee.resource.EventLookup;
import org.mobicents.slee.resource.SleeActivityHandle;
import org.mobicents.slee.runtime.ActivityContext;
import org.mobicents.slee.runtime.ActivityContextFactory;
import org.mobicents.slee.runtime.transaction.SleeTransactionManager;

// TO REMOVE?

public class SipToSLEEUtility {

	protected static Logger log = Logger.getLogger(SipResourceAdaptor.class);

	public static void displayMessage(String nameOfModule, String message,
			ComponentKey key) {
		if (log.isDebugEnabled()) {
			log.debug("\n================================\n" + nameOfModule
					+ " " + message + ":\n" + "\nEvent:\nName:" + key.getName()
					+ "\nVendor: " + key.getVendor() + "\nVersion: "
					+ key.getVersion() + "\n================================");
		}

	}

	public static void displayDeliveryMessage(String module, int eventID,
			ComponentKey key, String activityID) {
		if (log.isDebugEnabled()) {
			log.debug("\n================================\n" + module
					+ " delivering event:\nEventID: " + eventID
					+ "\nEvent desc:" + key + "\nActivityID: " + activityID
					+ "\n================================");
		}

	}

}
