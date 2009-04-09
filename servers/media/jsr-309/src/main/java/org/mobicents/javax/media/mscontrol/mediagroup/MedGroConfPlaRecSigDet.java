package org.mobicents.javax.media.mscontrol.mediagroup;

import java.util.HashSet;
import java.util.Set;

import javax.media.mscontrol.MediaConfig;
import javax.media.mscontrol.MediaConfigException;
import javax.media.mscontrol.JoinableStream.StreamType;
import javax.media.mscontrol.mediagroup.MediaGroupConfig;
import javax.media.mscontrol.resource.Parameters;
import javax.media.mscontrol.resource.Symbol;

public class MedGroConfPlaRecSigDet implements MediaGroupConfig {

	private static MediaGroupConfig medGroConfPlayer = new MedGroConfPlaRecSigDet();

	public static Set<Symbol> suppSymbols = new HashSet<Symbol>();

	static {
		suppSymbols.add(MediaGroupParameterEnum.PLAYER_SUPPORTED);
		suppSymbols.add(MediaGroupParameterEnum.RECORDER_SUPPORTED);
		suppSymbols.add(MediaGroupParameterEnum.SD_SUPPORTED);
	}

	private MedGroConfPlaRecSigDet() {
	}

	public static MediaGroupConfig getInstance() {
		return medGroConfPlayer;
	}

	public MediaConfig createCustomizedClone(Parameters params) throws MediaConfigException {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Symbol> getSupportedSymbols() {
		return this.suppSymbols;
	}

	public boolean hasStream(StreamType type) {
		return StreamType.audio == type ? true : false;
	}

	public String marshall() {
		// TODO Auto-generated method stub
		return null;
	}

}
