package org.mobicents.media.msc.common;

/**
 * IDLE state indicates the Session has zero connections or links. ACTIVE state
 * indicates the Session has one or more connections or links. INVALID state
 * indicates the Session has lost all of its connections or links.
 */
public enum MsSessionState {

	/**
	 * IDLE state indicates the Session has zero connections or links. ACTIVE
	 * state indicates the Session has one or more connections or links. INVALID
	 * state indicates the Session has lost all of its connections or links.
	 */
	IDLE, ACTIVE, INVALID;

}
