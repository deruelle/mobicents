package javax.megaco.message.descriptor;

import java.io.Serializable; //FIXME: this requires JSIP

import javax.sdp.SessionDescription;

/**
 * The SDPInfo object is a class that shall be used to set the Media
 * description, Session Description and Time description of the SDP. These would
 * be defined in javax.sdp package. This is an independent class derived from
 * java.util.Object and shall not have any derived classes.
 */
public class SDPInfo implements Serializable {

	private SessionDescription sessionDescription = null;
	private String sessionDescStr = null;

	/**
	 * Constructs an object of type megaco SDP info which shall be used within
	 * the media descriptor to set the local and remote descriptor.
	 */
	public SDPInfo() {
	}

	/**
	 * The method is used to get the session description of the SDP which would
	 * be set in local or remote descriptor. In an SDP this information
	 * necessarily needs to be set.
	 * 
	 * @return session - The reference to the object corresponding to the
	 *         session description. If the session description is not set then
	 *         it shall return a null value.
	 */
	public SessionDescription getSessionDescription() {
		return this.sessionDescription;
	}

	/**
	 * The method can be used to set the session description within an SDP for
	 * local/remote descriptor.
	 * 
	 * @param session
	 *            - The reference to the object corresponding to the session
	 *            description.
	 * @throws IllegalArgumentException
	 *             - Thrown if parameters set in the session description are
	 *             such that the object cannot be set for the local/remote
	 *             descriptor.
	 * @throws javax.megaco.ParamNotSupportedException
	 *             - Thrown if parameters set in the session description are
	 *             such that they are not supported.
	 */
	public void setSessionDescription(SessionDescription session) throws IllegalArgumentException, javax.megaco.ParamNotSupportedException {

		// FIXME: add checks
		if (session == null) {
			throw new IllegalArgumentException("SessionDescription must not be null.");
		}

		this.sessionDescription = session;

	}

	/**
	 * The method is used to get the session description of the SDP which would
	 * be set in local or remote descriptor. The SDP info returned is in the
	 * string format. The applications may use this method if it is not
	 * concerned with the parameters contained in the SDP info. The SDP string
	 * obtained may be processed later or may be tunnelled to the other layer
	 * for processing.
	 * 
	 * @return session - The reference to the object corresponding to the
	 *         session description string. If the session description string is
	 *         not set then it shall return a null value.
	 */
	public java.lang.String getSessionDescStr() {
		return this.sessionDescStr;
	}

	/**
	 * The method can be used to set the session description string within an
	 * SDP for local/remote descriptor. The applications may use this method in
	 * case it has pre-encoded SDP string with itself. The pre-encoded SDP
	 * string may be obtained either at the time of configuration of the
	 * application or may be obtained in the commands exchanged for the call.
	 * The SDP info string as set in this method must be in the format as
	 * defined by the RFC 2327 for IN SDP and RFC 3018 for ATM SDP.
	 * 
	 * @param session
	 *            - The reference to the object corresponding to the session
	 *            description string.
	 * @throws IllegalArgumentException
	 *             This exception is raised if the reference of session
	 *             description string passed to this method is NULL.
	 */
	public void setSessionDescStr(java.lang.String session)

	throws IllegalArgumentException {

		if (session == null) {
			throw new IllegalArgumentException("SessionDescription must not be null.");
		}
		this.sessionDescStr = session;

	}

	public java.lang.String toString() {
		return super.toString();
	}

}
