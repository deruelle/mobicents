package org.mobicents.javax.media.mscontrol.mediagroup;

import javax.media.mscontrol.mediagroup.Player;
import javax.media.mscontrol.mediagroup.PlayerEvent;
import javax.media.mscontrol.resource.Error;
import javax.media.mscontrol.resource.Symbol;

public class PlayerEventImpl implements PlayerEvent {
	private Player player = null;
	private Symbol eventId = null;
	private Symbol qualifier = null;
	private Symbol rtcTrigger = null;

	private String errorText = null;
	private Symbol error = Error.e_OK;

	public PlayerEventImpl(Player player, Symbol eventId) {
		this.player = player;
		this.eventId = eventId;
	}

	public PlayerEventImpl(Player player, Symbol eventId, Symbol qualifier, Symbol rtcTrigger) {
		this(player, eventId);
		this.qualifier = qualifier;
		this.rtcTrigger = rtcTrigger;
	}

	public PlayerEventImpl(Player player, Symbol eventId, Symbol error, String errorText) {
		this(player, eventId);
		this.error = error;
		this.errorText = errorText;
	}

	public PlayerEventImpl(Player player, Symbol eventId, Symbol qualifier, Symbol rtcTrigger, Symbol error,
			String errorText) {
		this(player, eventId, qualifier, rtcTrigger);

		this.error = error;
		this.errorText = errorText;
	}

	public Symbol getChangeType() {
		return null;
	}

	public int getIndex() {
		return 0;
	}

	public int getOffset() {
		return 0;
	}

	public Symbol getQualifier() {
		return this.qualifier;
	}

	public Symbol getRTCTrigger() {
		return this.rtcTrigger;
	}

	public Symbol getError() {
		return this.error;
	}

	public String getErrorText() {
		return this.errorText;
	}

	public Symbol getEventID() {
		return this.eventId;
	}

	public Player getSource() {
		return this.player;
	}

}
