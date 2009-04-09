package org.mobicents.javax.media.mscontrol;

import javax.media.mscontrol.MediaConfig;
import javax.media.mscontrol.MediaConfigException;
import javax.media.mscontrol.mediagroup.MediaGroupConfig;
import javax.media.mscontrol.networkconnection.NetworkConnectionConfig;
import javax.media.mscontrol.resource.Configuration;

import org.mobicents.javax.media.mscontrol.mediagroup.MedGroConfPlaRecSigDet;
import org.mobicents.javax.media.mscontrol.mediagroup.MedGroConfPlaRecSigDetGen;
import org.mobicents.javax.media.mscontrol.mediagroup.MedGroConfPlaSigDet;
import org.mobicents.javax.media.mscontrol.mediagroup.MedGroConfPlayer;
import org.mobicents.javax.media.mscontrol.mediagroup.MedGroConfSigDet;
import org.mobicents.javax.media.mscontrol.networkconnection.NetConnConfigBasic;

public class MediaConfigFactory {
	
	private MediaConfigFactory() {

	}

	public static <C extends MediaConfig> C getMediaConfig(Configuration<C> configSymbol) throws MediaConfigException {
		if (configSymbol.equals(NetworkConnectionConfig.c_Basic)) {
			return (C) (NetConnConfigBasic.getInstance());
		} else if (configSymbol.equals(MediaGroupConfig.c_Player)) {
			return (C) (MedGroConfPlayer.getInstance());
		} else if (configSymbol.equals(MediaGroupConfig.c_SignalDetector)) {
			return (C) (MedGroConfSigDet.getInstance());
		} else if (configSymbol.equals(MediaGroupConfig.c_PlayerSignalDetector)) {
			return (C) (MedGroConfPlaSigDet.getInstance());
		} else if (configSymbol.equals(MediaGroupConfig.c_PlayerRecorderSignalDetector)) {
			return (C) (MedGroConfPlaRecSigDet.getInstance());
		} else if (configSymbol.equals(MediaGroupConfig.c_PlayerRecorderSignalDetectorSignalGenerator)) {
			return (C) (MedGroConfPlaRecSigDetGen.getInstance());
		} else {
			throw new MediaConfigException("The Configuartion " + configSymbol + " is not supported yet");
		}
	}
}
