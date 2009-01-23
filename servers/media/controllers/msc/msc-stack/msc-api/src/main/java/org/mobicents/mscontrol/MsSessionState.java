package org.mobicents.mscontrol;

/**
 * <ul>
 * <li><code>IDLE</code> state indicates the Session has zero connections or
 * links.</li>
 * <br/>
 * <li> <code>ACTIVE</code> state indicates the Session has one or more
 * connections or links.</li>
 * <br/>
 * <li> <code>INVALID</code> state indicates the Session has lost all of its
 * connections or links.</li>
 * <br/>
 * </ul>
 */
public enum MsSessionState {

	IDLE, ACTIVE, INVALID;

}
