package org.mobicents.media.server.impl.common.dtmf;

public enum DTMFType {
    
	RFC2833("RFC2833"), INBAND("INBAND"), AUTO("AUTO");	

	private DTMFType(String type) {
            this.type = type;
	}
        
        private String type;
}
