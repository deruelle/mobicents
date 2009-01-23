package org.mobicents.mscontrol.impl.events.line;

import org.mobicents.mscontrol.events.MsEventIdentifier;
import org.mobicents.mscontrol.events.MsRequestedEvent;
import org.mobicents.mscontrol.events.MsRequestedSignal;
import org.mobicents.mscontrol.events.pkg.MsLine;
import org.mobicents.mscontrol.impl.events.MsPackage;

/**
 * 
 * @author amit bhayani
 *
 */
public class MsLinePackage implements MsPackage {

	public MsRequestedEvent createRequestedEvent(MsEventIdentifier eventID) {
		if (eventID.getPackageName().equals(MsLine.PACKAGE_NAME)) {
			LineRequestedEventImpl lineRequestedEventImpl = new LineRequestedEventImpl();
			lineRequestedEventImpl.setID(eventID);
			
			return lineRequestedEventImpl;

		}
		return null;
	}

	public MsRequestedSignal createRequestedSignal(MsEventIdentifier eventID) {
		if (eventID.getPackageName().equals(MsLine.PACKAGE_NAME)) {
			LineRequestedSignalImpl lineRequestedSignalImpl = new LineRequestedSignalImpl();
			lineRequestedSignalImpl.setID(eventID);

			return lineRequestedSignalImpl;

		}
		return null;
	}
}
