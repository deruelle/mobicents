package org.mobicents.media.server.impl.events.dtmf;

import org.mobicents.media.server.impl.AbstractSignal;
import org.mobicents.media.server.impl.BaseConnection;
import org.mobicents.media.server.impl.BaseEndpoint;

public class DTMFSignal extends AbstractSignal {
	
	private String tone = null;
	
	   public DTMFSignal(String tone) {
	        this.tone = tone;
	    }

	@Override
	public void apply(BaseConnection connection) {
		throw new UnsupportedOperationException("Not supported yet.");

	}

	@Override
	public void apply(BaseEndpoint connection) {
		throw new UnsupportedOperationException("Not supported yet.");

	}

}
