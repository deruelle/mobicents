package org.mobicents.mscontrol.events.pkg;

import org.mobicents.mscontrol.events.MsEventIdentifier;

public class MsPackageNotSupported {

	public final static MsEventIdentifier ANNOUNCEMENT = new MsEventID(MsAnnouncement.PACKAGE_NAME,
			"PACKAGE_NOT_SUPPORTED");
	public final static MsEventIdentifier AUDIO = new MsEventID(MsAudio.PACKAGE_NAME, "PACKAGE_NOT_SUPPORTED");
	public final static MsEventIdentifier DTMF = new MsEventID(org.mobicents.mscontrol.events.pkg.DTMF.PACKAGE_NAME,
			"PACKAGE_NOT_SUPPORTED");

}
