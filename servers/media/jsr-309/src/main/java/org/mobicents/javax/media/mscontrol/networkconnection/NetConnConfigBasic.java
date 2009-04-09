package org.mobicents.javax.media.mscontrol.networkconnection;

import java.util.HashSet;
import java.util.Set;

import javax.media.mscontrol.MediaConfig;
import javax.media.mscontrol.MediaConfigException;
import javax.media.mscontrol.JoinableStream.StreamType;
import javax.media.mscontrol.networkconnection.NetworkConnectionConfig;
import javax.media.mscontrol.resource.Parameters;
import javax.media.mscontrol.resource.Symbol;

public class NetConnConfigBasic implements NetworkConnectionConfig {

	private static final NetworkConnectionConfig config = new NetConnConfigBasic();
	public Set<Symbol> suppSymbols = new HashSet<Symbol>();

	static {
		// TODO : Add all the supported Parameter to suppSymbols here
	}

	private NetConnConfigBasic() {
	}

	public static NetworkConnectionConfig getInstance() {
		return config;
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
