package org.mobicents.javax.media.mscontrol.networkconnection;

import javax.media.mscontrol.EventType;
import javax.media.mscontrol.MediaErr;
import javax.media.mscontrol.Qualifier;
import javax.media.mscontrol.networkconnection.SdpPortManager;
import javax.media.mscontrol.networkconnection.SdpPortManagerEvent;
import javax.media.mscontrol.resource.Trigger;

/**
 * 
 * @author amit.bhayani
 * 
 */
public class SdpPortManagerEventImpl implements SdpPortManagerEvent {

	private SdpPortManager source = null;
	private MediaErr error = MediaErr.NO_ERROR;
	private String errorText = null;
	private EventType eventType = null;
	private byte[] mediaServerSdp = null;
	private boolean isSuccessful = false;

	public SdpPortManagerEventImpl(SdpPortManager source, EventType eventType, byte[] mediaServerSdp,
			boolean isSuccessful) {
		this.source = source;
		this.eventType = eventType;
		this.mediaServerSdp = mediaServerSdp;
		this.isSuccessful = isSuccessful;
	}

	public SdpPortManagerEventImpl(SdpPortManager source, EventType eventType, byte[] mediaServerSdp,
			boolean isSuccessful, MediaErr error, String errorText) {
		this(source, eventType, mediaServerSdp, isSuccessful);
		this.error = error;
		this.errorText = errorText;
	}

	public byte[] getMediaServerSdp() {
		return this.mediaServerSdp;
	}

	public Qualifier getQualifier() {
		// TODO Auto-generated method stub
		return null;
	}

	public Trigger getRTCTrigger() {
		// TODO Auto-generated method stub
		return null;
	}

	public MediaErr getError() {
		return this.error;
	}

	public String getErrorText() {
		return this.errorText;
	}

	public EventType getEventType() {
		return this.eventType;
	}

	public SdpPortManager getSource() {
		return this.source;
	}

	public boolean isSuccessful() {
		return this.isSuccessful;
	}

}
