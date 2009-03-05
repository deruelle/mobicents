package javax.megaco.message.descriptor;

import java.io.Serializable;


import javax.megaco.ParameterNotSetException;
import javax.megaco.pkg.PkgItemStr;
import javax.megaco.pkg.PkgSignalItem;

/**
 * The SignalRequest object is a class that shall be used to set the fields of
 * signal request within the signal descriptor. This is an independent class
 * derived from java.util.Object and shall not have any derived classes. If the
 * signal param type of the SignalParam class is M_SIGNAL_PARAM_LIST, then it
 * indicates that the SignalRequest is part of the signal list of the signal
 * descriptor. The class optionally provides interface to specify the package
 * name, item name and the associated parameters in the string format.
 */
public class SignalRequest implements Serializable {

	private PkgItemStr signalItemStr = null;
	private PkgSignalItem signalItem = null;

	private Integer streamId = null;
	private SignalType signalType = null;
	private boolean keepAlive = false;
	private Integer signalDuration = null;
	private SignalNotifyReason signalNotifyReason = null;

	/**
	 * Constructs Signal request parameter object with the PkgSignalItem object.
	 * 
	 * @param signalItem
	 * @throws IllegalArgumentException
	 *             - Thrown if an invalid signalItem object reference is set.
	 */
	public SignalRequest(PkgSignalItem signalItem) throws IllegalArgumentException {
		this.signalItem = signalItem;
	}

	/**
	 * Constructs Signal request object with the PkgItemStr object. This method
	 * would be used if the package parameters are to be conveyed in the string
	 * format.
	 * 
	 * @param signalItemStr
	 * @throws IllegalArgumentException
	 *             - Thrown if an invalid signalItemStr object reference is set.
	 */
	public SignalRequest(PkgItemStr signalItemStr) throws IllegalArgumentException {
		this.signalItemStr = signalItemStr;

	}

	/**
	 * The method can be used to get the package name in the signal descriptor.
	 * This method gives the package information, the attached signal
	 * information and the parameter name and value for the signal id.
	 * 
	 * @return package - The object reference for the Signal Item. This has the
	 *         object reference of corresponding megaco package and has the
	 *         reference of the parameter info associated with it.
	 */
	public PkgSignalItem getMegacoPkgSignalItem() {
		return this.signalItem;
	}

	/**
	 * The method can be used to get the pkdgName as set in the signal
	 * descriptor. This method gives the package information, the attached
	 * signal information and the parameter name and value. As comapres to the
	 * getMegacoPkgSignalItem() method, this method returnes the package
	 * parameters in the string format.
	 * 
	 * @return The object reference for the megaco package item. This class
	 *         holds information about package name, item name and the
	 *         parameters in the string format. If the parameter has not been
	 *         set, then this method shall return NULL.
	 */
	public PkgItemStr getMegacoPkgItemStr() {
		return this.signalItemStr;
	}

	/**
	 * This method specifies whether the stream id is present or not.
	 * 
	 * @return The TRUE if the stream id is present. Else it shall return FALSE.
	 */
	public boolean isStreamIdPresent() {
		return this.streamId != null;
	}

	/**
	 * The method is used to retrieve the stream id only if the
	 * isStreamIdPresent() returns TRUE.
	 * 
	 * @return The stream id set for this signal.
	 * @throws javax.megaco.ParameterNotSetException
	 *             if isStreamIdPresent() returns FALSE.
	 */
	public int getStreamId() throws javax.megaco.ParameterNotSetException {
		if (!isStreamIdPresent()) {
			throw new ParameterNotSetException("StremId has not been set.");
		}
		return this.streamId.intValue();
	}

	/**
	 * The method is sets the stream id and automatically sets a flag such that
	 * isStreamIdPresent() returns TRUE.
	 * 
	 * @param streamId
	 *            - Stream id that is to be set for this signal.
	 * @return
	 * @throws IllegalArgumentException
	 *             - If the stream Id specified is invalid, then this exception
	 *             is raised.
	 */
	public void setStreamId(int streamId) throws IllegalArgumentException {
		// FIXME: add errors?
		// FIXME: jdoc had return type
		this.streamId = new Integer(streamId);

	}

	/**
	 * The method is used to get the type of signal which could be one of brief
	 * or on-off or other.
	 * 
	 * @return The integer value which identifies the SignalType. If SignalType
	 *         is not specified, then this method would return value null.
	 */
	public SignalType getSignalType() {

		return this.signalType;
	}

	public boolean isSignalTypePresent() {
		// FIXME: jdoc does not have this.
		return this.signalType != null;
	}

	/**
	 * The method is used to set the type of signal.
	 * 
	 * @param signalType
	 *            - The object reference to a derived class of SignalType.
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the reference of Signal Type
	 *             passed to this method is NULL.
	 */
	public void setSignalType(SignalType signalType) throws IllegalArgumentException {
		if (signalType == null) {
			throw new IllegalArgumentException("SignalType must not be null");
		}
		this.signalType = signalType;
	}

	/**
	 * The method is used to get the notification reason which could be one of
	 * timeout or interrupted due to an event received or interrupted due to
	 * another signal overriding the current signal or other.
	 * 
	 * @return The integer value which identifies the SignalNotifyReason. If the
	 *         signal notify reason is not specified, then this method would
	 *         return value 0. The possible values are defined in the class
	 *         SignalNotifyReason.
	 * @throws ParameterNotSetException
	 *             - isSignalNotifyReasonPresent
	 */
	public int getSignalNotifyReason() throws ParameterNotSetException {
		if (!isSignalNotifyReasonPresent()) {
			throw new ParameterNotSetException("SignalNotifyReason must be set.");
		}

		return this.signalNotifyReason.getNotifyReason();
	}

	public boolean isSignalNotifyReasonPresent() {
		return this.signalNotifyReason != null;
	}

	/**
	 * The method is used to set the signal notification reason.
	 * 
	 * @param notifyReason
	 *            - The object reference to a derived class of
	 *            SignalNotifyReason.
	 * @throws IllegalArgumentException
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the reference of Signal Notify
	 *             Reason passed to this method is NULL.
	 */
	public void setSignalNotifyReason(SignalNotifyReason notifyReason) throws IllegalArgumentException {
		if (notifyReason == null) {
			throw new IllegalArgumentException("SignalNotifyReason must not be null");
		}
		this.signalNotifyReason = notifyReason;
	}

	/**
	 * This method specifies whether the keep alive token is set or not.
	 * 
	 * @return Returns TRUE if the keep alive token has been set.
	 */
	public boolean isKeepAliveSet() {
		return this.keepAlive;
	}

	/**
	 * This method is used to set the keep alive flag. Following this the method
	 * isKeepAliveSet() method would return value TRUE, indicating that the keep
	 * alive flag is set.
	 */
	public void setKeepAlive() {
		this.keepAlive = true;
	}

	/**
	 * This method specifies whether the duration field is present or not.
	 * 
	 * @return Returns TRUE if the duration field is present.
	 */
	public boolean isDurationPresent() {
		return this.signalDuration != null;
	}

	/**
	 * This method is used to set the signal duration in the signal parameters.
	 * 
	 * @param signal_duration
	 *            - specifies the signal duration parameter in the signal
	 *            descriptor.
	 * @return
	 * @throws IllegalArgumentException
	 *             - If the signal duration specified is invalid, then this
	 *             exception is raised.
	 */
	public void setSignalDuration(int signal_duration) throws IllegalArgumentException {
		// FIXME: jdoc has return type
		if (signal_duration <= 0) {
			throw new IllegalArgumentException("Duration must not be less or equal to zero.");
		}

		this.signalDuration = new Integer(signal_duration);
	}

	/**
	 * This method returns the signal duration if it is specified in the signal
	 * parameters. This method must be invoked after checking for the presence
	 * of the signal duration field in the signal parameters.
	 * 
	 * @return Returns the value of the signal duration that has been set.
	 * @throws javax.megaco.ParameterNotSetException
	 *             : This is raised if no value of signal duration is set. This
	 *             method must be invoked after invoking isDurationPresent()
	 *             method.
	 */
	public int getSignalDuration() throws javax.megaco.ParameterNotSetException {
		if (!isDurationPresent()) {
			throw new ParameterNotSetException("Signal duration must be present.");
		}
		return this.signalDuration.intValue();
	}

	public java.lang.String toString() {
		return super.toString();
	}

}
