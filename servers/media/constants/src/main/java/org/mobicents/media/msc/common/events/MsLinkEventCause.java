package org.mobicents.media.msc.common.events;

/**
 * When the state of Mslink is changed MsLinkEvent is fired which has
 * MsLinkEventCause representing the cause of event
 * 
 * <ul>
 * <li><code>NORMAL</code> Represents the MsLinkEvent is fired because the
 * operation is successful. For example join or release of MsLink is successful</li>
 * 
 * <li><code>FACILITY_FAILURE</code> Represents the MsLinkEvent is fired
 * because join operation failed. For example Endpoint already has reached its
 * upper limit on number of Connections it can handle</li>
 * 
 * <li><code>ENDPOINT_UNKNOWN</code> Represents the MsLinkEvent is fired
 * because join operation failed. For example lookup of Endpoint failed and
 * endpoint name passed is not correct</li>
 * 
 * <li><code>RESOURCE_UNAVAILABLE</code> Represents the MsLinkEvent is fired
 * because join operation failed. For example creationg of Scoket failed while
 * creating the Connection</li>
 * 
 * <li><code>REMOTE_SDP_INVALID</code> Represents the MsLinkEvent is fired
 * because join operation failed. For example SDP is invalid</li>
 * 
 * <li><code>REMOTE_SDP_MISSING</code> Represents the MsLinkEvent is fired
 * because join operation failed. For example SDP is null</li>
 * 
 * @author Oleg Kulikov
 * 
 */
public enum MsLinkEventCause {

	NORMAL, FACILITY_FAILURE, ENDPOINT_UNKNOWN, RESOURCE_UNAVAILABLE, REMOTE_SDP_INVALID, REMOTE_SDP_MISSING;

}
