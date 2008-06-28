/*
 * Mobicents Media Gateway
 *
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */

package org.mobicents.media.server.impl.jmf.player;

import java.io.Serializable;

import org.mobicents.media.server.impl.common.events.PlayerEventType;

/**
 * 
 * @author Oleg Kulikov
 */
public class PlayerEvent implements Serializable {

	private PlayerEventType type = null;
	private AudioPlayer source;
	private String msg;

	public PlayerEvent(AudioPlayer source, PlayerEventType type, String msg) {
		this.source = source;
		this.type = type;
		this.msg = msg;
	}

	public AudioPlayer getSource() {
		return source;
	}

	public PlayerEventType getEventType() {
		return type;
	}

	public String getMessage() {
		return msg;
	}
}
