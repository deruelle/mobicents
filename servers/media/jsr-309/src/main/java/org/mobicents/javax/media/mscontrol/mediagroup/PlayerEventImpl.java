package org.mobicents.javax.media.mscontrol.mediagroup;

import javax.media.mscontrol.mediagroup.Player;
import javax.media.mscontrol.mediagroup.PlayerEvent;
import javax.media.mscontrol.resource.Action;
import javax.media.mscontrol.MediaErr;
import javax.media.mscontrol.EventType;
import javax.media.mscontrol.Qualifier;
import javax.media.mscontrol.resource.Trigger;

/**
 * 
 * @author amit bhayani
 * 
 */
public class PlayerEventImpl implements PlayerEvent {
	private Player player = null;
	private EventType eventType = null;
	private Qualifier qualifier = null;
	private Trigger rtcTrigger = null;
	private boolean isSuccessful = false;

	private String errorText = null;
	private MediaErr error = MediaErr.NO_ERROR;

	public PlayerEventImpl(Player player, EventType eventType, boolean isSuccessful) {
		this.player = player;
		this.eventType = eventType;
		this.isSuccessful = isSuccessful;
	}

	public PlayerEventImpl(Player player, EventType eventType, boolean isSuccessful, Qualifier qualifier,
			Trigger rtcTrigger) {
		this(player, eventType, isSuccessful);
		this.qualifier = qualifier;
		this.rtcTrigger = rtcTrigger;
	}

	public PlayerEventImpl(Player player, EventType eventType, boolean isSuccessful, MediaErr error, String errorText) {
		this(player, eventType, isSuccessful);
		this.error = error;
		this.errorText = errorText;
	}

	public PlayerEventImpl(Player player, EventType eventType, boolean isSuccessful, Qualifier qualifier,
			Trigger rtcTrigger, MediaErr error, String errorText) {
		this(player, eventType, isSuccessful, qualifier, rtcTrigger);

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

	public MediaErr getError() {
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

	public boolean isSuccessful() {
		return this.isSuccessful;
	}

}
