package org.mobicents.mscontrol;

/**
 * Whenever the state of {@link MsLink} changes <code>MsLinkEvent</code> is
 * fired and change in state is represented by <code>MsLinkEventID</code>
 * 
 * 
 * <ul>
 * <li><code>LINK_CREATED</code> As soon as new MsLink is created
 * LINK_CREATED is fired</li>
 * <br/>
 * 
 * <li><code>LINK_JOINED</code> Fired as soon as join operation of MsLink is
 * successful </li>
 * <br/>
 * 
 * <li><code>LINK_DROPPED</code> Fired as soon as release operation of MsLink
 * is successful</li>
 * <br/>
 * 
 * <li><code>LINK_FAILED</code> Fired as soon as join operation of MsLink
 * fails</li>
 * <br/>
 * 
 * </ul>
 */
public enum MsLinkEventID {
	LINK_CREATED, 
        LINK_CONNECTED, 
        LINK_DISCONNECTED, 
        LINK_FAILED,
        MODE_FULL_DUPLEX,
        MODE_HALF_DUPLEX
}
