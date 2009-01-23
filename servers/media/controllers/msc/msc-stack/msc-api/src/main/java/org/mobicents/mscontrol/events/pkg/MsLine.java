package org.mobicents.mscontrol.events.pkg;

import org.mobicents.mscontrol.events.MsEventIdentifier;

/**
 * 
 * @author amit bhayani
 * 
 */
public class MsLine {

	public final static String PACKAGE_NAME = "org.mobicents.media.events.line";

	/*
	 * RFC 2833 3.12 Line Events
	 */

	// Dial tone: The exchange is ready to receive address information.
	public final static MsEventIdentifier DIAL_TONE = new MsEventID(PACKAGE_NAME, "DIAL");

	// PABX internal dial tone: The PABX is ready to receive address
	// information.
	public final static MsEventIdentifier PABX_INTERNATIONAL_DIAL_TONE = new MsEventID(PACKAGE_NAME, "PABX_INTERNATIONAL_DIAL");

	// Special dial tone: Same as dial tone, but the caller's line is
	// subject to a specific condition, such as call diversion or a
	// voice mail is available (e.g., "stutter dial tone").
	public final static MsEventIdentifier SPECIAL_DIAL_TONE = new MsEventID(PACKAGE_NAME, "SPECIAL_DIAL");

	// Ring: This named signal event causes the recipient to generate an
	// alerting signal ("ring"). The actual tone or other indication
	// used to render this named event is left up to the receiver.
	// (This differs from the ringing tone, below, heard by the
	// caller
	public final static MsEventIdentifier RING = new MsEventID(PACKAGE_NAME, "RING");

	// Ringing tone: The call has been placed to the callee and a calling
	// signal (ringing) is being transmitted to the callee. This
	// tone is also called "ringback".
	public final static MsEventIdentifier RINGING_TONE = new MsEventID(PACKAGE_NAME, "RINGING");

	// Special ringing tone: A special service, such as call forwarding
	// or call waiting, is active at the called number.
	public final static MsEventIdentifier SPECIAL_RINGING_TONE = new MsEventID(PACKAGE_NAME, "SPECIAL_RINGING");

	// Busy tone: The called telephone number is busy.
	public final static MsEventIdentifier BUSY_TONE = new MsEventID(PACKAGE_NAME, "BUSY");

	// Congestion tone: Facilities necessary for the call are temporarily
	// unavailable.
	public final static MsEventIdentifier CONGESTION_TONE = new MsEventID(PACKAGE_NAME, "CONGESTION");

	// Calling card service tone: The calling card service tone consists
	// of 60 ms of the sum of 941 Hz and 1477 Hz tones (DTMF '#'),
	// followed by 940 ms of 350 Hz and 440 Hz (U.S. dial tone),
	// decaying exponentially with a time constant of 200 ms.
	public final static MsEventIdentifier CALLING_CARD_SERVICE_TONE = new MsEventID(PACKAGE_NAME,
			"CALLING_CARD_SERVICE");

	// Special information tone: The callee cannot be reached, but the
	// reason is neither "busy" nor "congestion". This tone should
	// be used before all call failure announcements, for the
	// benefit of automatic equipment.
	public final static MsEventIdentifier SPECIAL_INFORMATION_TONE = new MsEventID(PACKAGE_NAME, "SPECIAL_INFORMATION");

	// Comfort tone: The call is being processed. This tone may be used
	// during long post-dial delays, e.g., in international
	// connections.
	public final static MsEventIdentifier COMFORT_TONE = new MsEventID(PACKAGE_NAME, "COMFORT");

	// Hold tone: The caller has been placed on hold.
	public final static MsEventIdentifier HOLD_TONE = new MsEventID(PACKAGE_NAME, "HOLD");

	// Record tone: The caller has been connected to an automatic
	// answering device and is requested to begin speaking.
	public final static MsEventIdentifier RECORD_TONE = new MsEventID(PACKAGE_NAME, "RECORD");

	// Caller waiting tone: The called station is busy, but has call
	// waiting service.
	public final static MsEventIdentifier CALLER_WAITING_TONE = new MsEventID(PACKAGE_NAME, "CALLER_WAITING");

	// Pay tone: The caller, at a payphone, is reminded to deposit
	// additional coins.
	public final static MsEventIdentifier PAY_TONE = new MsEventID(PACKAGE_NAME, "PAY");

	// Positive indication tone: The supplementary service has been
	// activated.
	public final static MsEventIdentifier POSITIVE_INDICATION_TONE = new MsEventID(PACKAGE_NAME, "POSITIVE_INDICATION");

	// Negative indication tone: The supplementary service could not be
	// activated.
	public final static MsEventIdentifier NEGATIVE_INDICATION_TONE = new MsEventID(PACKAGE_NAME, "NEGATIVE_INDICATION");

	// Off-hook warning tone: The caller has left the instrument off-hook
	// for an extended period of time.
	public final static MsEventIdentifier OFF_HOOK_WARNING_TONE = new MsEventID(PACKAGE_NAME, "OFF_HOOK_WARNING");

	/*
	 * The following tones can be heard by either calling or called party during
	 * a conversation:
	 */

	// Call waiting tone: Another party wants to reach the subscriber.
	public final static MsEventIdentifier CALL_WAITING_TONE = new MsEventID(PACKAGE_NAME, "CALL_WAITING");

	// Warning tone: The call is being recorded. This tone is not
	// required in all jurisdictions.
	public final static MsEventIdentifier WARNING_TONE = new MsEventID(PACKAGE_NAME, "WARNING");

	// Intrusion tone: The call is being monitored, e.g., by an operator.
	public final static MsEventIdentifier INTRUSION_TONE = new MsEventID(PACKAGE_NAME, "INTRUSION");

	// CPE alerting signal: A tone used to alert a device to an arriving
	// in-band FSK data transmission. A CPE alerting signal is a
	// combined 2130 and 2750 Hz tone, both with tolerances of 0.5%
	// and a duration of 80 to. 80 ms. The CPE alerting signal is
	// used with ADSI services and Call Waiting ID services [14].
	public final static MsEventIdentifier CPE_ALERTING_SIGNAL_TONE = new MsEventID(PACKAGE_NAME, "CPE_ALERTING_SIGNAL");

	/*
	 * The following tones are heard by operators:
	 */

	// Payphone recognition tone: The person making the call or being
	// called is using a payphone (and thus it is ill-advised to
	// allow collect calls to such a person).
	public final static MsEventIdentifier PAYPHONE_RECOGNITION_TONE = new MsEventID(PACKAGE_NAME,
			"PAYPHONE_RECOGNITION");

}
