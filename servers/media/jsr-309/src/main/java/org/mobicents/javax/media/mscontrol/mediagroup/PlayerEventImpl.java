package org.mobicents.javax.media.mscontrol.mediagroup;

import javax.media.mscontrol.mediagroup.Player;
import javax.media.mscontrol.mediagroup.PlayerEvent;
import javax.media.mscontrol.resource.Action;
import javax.media.mscontrol.resource.Error;
import javax.media.mscontrol.resource.EventType;
import javax.media.mscontrol.resource.Qualifier;
import javax.media.mscontrol.resource.Trigger;

public class PlayerEventImpl implements PlayerEvent {
	private Player player = null;
	private EventType eventType = null;
	private Qualifier qualifier = null;
	private Trigger rtcTrigger = null;

	private String errorText = null;
	private Error error = Error.e_OK;

	public PlayerEventImpl(Player player, EventType eventType) {
		this.player = player;
		this.eventType = eventType;
	}

	public PlayerEventImpl(Player player, EventType eventType, Qualifier qualifier, Trigger rtcTrigger) {
		this(player, eventType);
		this.qualifier = qualifier;
		this.rtcTrigger = rtcTrigger;
	}

	public PlayerEventImpl(Player player, EventType eventType, Error error, String errorText) {
		this(player, eventType);
		this.error = error;
		this.errorText = errorText;
	}

	public PlayerEventImpl(Player player, EventType eventType, Qualifier qualifier, Trigger rtcTrigger, Error error,
			String errorText) {
		this(player, eventType, qualifier, rtcTrigger);

		this.error = error;
		this.errorText = errorText;
	}

	public Action getChangeType() {
		return null;
	}

	public int getIndex() {
		return 0;
	}

	public int getOffset() {
		return 0;
	}

	public Qualifier getQualifier() {
		return this.qualifier;
	}

	public Trigger getRTCTrigger() {
		return this.rtcTrigger;
	}

	public Error getError() {
		return this.error;
	}

	public String getErrorText() {
		return this.errorText;
	}

	public EventType getEventType() {
		return this.eventType;
	}

	public Player getSource() {
		return this.player;
	}

}
