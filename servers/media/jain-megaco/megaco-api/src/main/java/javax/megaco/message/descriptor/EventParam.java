package javax.megaco.message.descriptor;


import javax.megaco.MethodInvocationException;

/**
 * The EventParam object is a class that shall be used to set the signal
 * descriptor, digitMap descriptor, stream id, whether keep alive is active or
 * not, package name, event id and the associated parameter names and values.
 * The class optionally provides interface to specify the package name, item
 * name and the associated parameters in the string format. This is an
 * independent class derived from java.util.Object and shall not have any
 * derived classes.
 */
public class EventParam {

	private SignalDescriptor signalDescriptor = null;
	private Integer streamId = null;
	private Boolean keepActive = new Boolean(false);
	private DigitMapDescriptor digitMapDescriptor = new DigitMapDescriptor();

	/**
	 * Constructs an object of type event parameter which shall be used within
	 * the event descriptor.
	 */
	public EventParam() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * The method can be used to get the signal descriptor.
	 * 
	 * @return signalDesc - The reference to the object corresponding to the
	 *         signal descriptor. This shall be returned only if the signal
	 *         descriptor is present in the event parameter of the event
	 *         descriptor.
	 */
	public SignalDescriptor getSignalDescriptor() {
		return this.signalDescriptor;
	}

	/**
	 * The method can be used to get the embedded digit map name in the event
	 * descriptor. Only one of digit map name or digit map value shall be set.
	 * 
	 * @return The reference to the embedded digitMap name. If this value is not
	 *         set, then this method would return NULL.
	 */
	public java.lang.String getDigitMapName() {
		return this.digitMapDescriptor.getDigitMapName();
	}

	/**
	 * The method can be used to get the embedded digit map value in the event
	 * descriptor. Only one of digit map name or digit map value shall be set.
	 * 
	 * @return The reference to the embedded digitMap value. If this value is
	 *         not set, then this method would return NULL.
	 */
	public DigitMapValue getDigitMapValue() {
		return this.digitMapDescriptor.getDigitMapValue();
	}

	/**
	 * The method can be used to get the embedded digit map value in the event
	 * descriptor. Only one of digit map name or digit map value shall be set.
	 * 
	 * @return The reference to the embedded digitMap value. If this value is
	 *         not set, then this method would return NULL.
	 */
	public java.lang.String getDigitMapValueStr() {
		return this.digitMapDescriptor.getDigitMapValueStr();
	}

	/**
	 * The method can be used to get stream id in the event descriptor.
	 * 
	 * @return streamId - The integer value of the stream id shall be returned.
	 *         This shall be returned only if the streamId is present in the
	 *         event parameter of the event descriptor.
	 * @throws IllegalStateException
	 *             - Thrown if streamId has not been set. Thus this method
	 *             should be called only after verifying that the streamId is
	 *             set using isStreamIdPresent()
	 */
	public int getStreamId() throws IllegalStateException {
		if (streamId == null) {
			throw new IllegalStateException("StreamId must be set.");
		}

		return this.streamId.intValue();
	}

	/**
	 * The method can be used to set the signal descriptor within the event
	 * descriptor.
	 * 
	 * @param signalDesc
	 *            - The reference to the object corresponding to the signal
	 *            descriptor.
	 * @throws IllegalArgumentException
	 *             - Thrown if parameters set in the signal descriptor are such
	 *             that the object cannot be set for the event descriptor.
	 */
	public void setSignalDescriptor(SignalDescriptor signalDesc) throws IllegalArgumentException {
		// FIXME: add checks
		this.signalDescriptor = signalDesc;

	}

	/**
	 * The method can be used to set the digit map name. Only one of digit map
	 * name or digit map value shall be set. This automatically sets the value
	 * of digitMap descriptor present in the event parameter object to true. If
	 * digitmap value is already set, then this method would raise an exception.
	 * 
	 * @param digitMapName
	 *            - The reference to the object corresponding to the digitMap
	 *            name.
	 * @throws IllegalArgumentException
	 *             - Thrown if digit Map value has already been set. The
	 *             protocol allows only either of them but not both.
	 */
	public void setDigitMapName(java.lang.String digitMapName) throws IllegalArgumentException {
		if (this.digitMapDescriptor.getDigitMapValue() != null) {
			throw new IllegalArgumentException("Digit map value must not be present when name is set.");
		}
		this.digitMapDescriptor.setDigitMapName(digitMapName);
	}

	/**
	 * The method can be used to set the digit map value. Only one of digit map
	 * name or digit map value shall be set. This automatically sets the value
	 * of digitMap descriptor present in the event parameter object to true. If
	 * digitmap name is already set, then this method would raise an exception.
	 * 
	 * @param digitMapValue
	 *            - The reference to the object corresponding to the digitMap
	 *            value.
	 * @throws IllegalArgumentException
	 *             - Thrown if digit Map name has already been set. The protocol
	 *             allows only either of them but not both.
	 */
	public void setDigitMapValue(DigitMapValue digitMapValue) throws IllegalArgumentException {
		if (this.digitMapDescriptor.getDigitMapName() != null) {
			throw new IllegalArgumentException("Digit map name must not be present when value is set.");
		}
		this.digitMapDescriptor.setDigitMapValue(digitMapValue);
	}

	/**
	 * The method can be used to set the digit map value. Only one of digit map
	 * name or digit map value shall be set. This automatically sets the value
	 * of digitMap descriptor present in the event parameter object to true. If
	 * digitmap name is already set, then this method would raise an exception.
	 * 
	 * @param digitMapValue
	 *            - The reference to the object corresponding to the digitMap
	 *            value.
	 * @throws IllegalArgumentException
	 *             - Thrown if digit Map name has already been set. The protocol
	 *             allows only either of them but not both.
	 */
	public void setDigitMapValueStr(java.lang.String digitMapValue) throws IllegalArgumentException {
		if (this.digitMapDescriptor.getDigitMapName() != null) {
			throw new IllegalArgumentException("Digit map name must not be present when digit map string is set.");
		}
		this.digitMapDescriptor.setDigitMapValueStr(digitMapValue);
	}

	/**
	 * The method can be used to set stream id in the event descriptor. This
	 * automatically sets the value of stream id present in the event parameter
	 * object to true.
	 * 
	 * @param streamId
	 *            - The integer value of the stream id shall be set.
	 * @throws IllegalArgumentException
	 *             - Thrown if streamId is set with an invalid value.
	 */
	public void setStreamId(int streamId) throws IllegalArgumentException {
		// FIXME: what invalid value?
		this.streamId = new Integer(streamId);
	}

	/**
	 * The method can be used to find if the stream id is present in the current
	 * object.
	 * 
	 * @return Returns true if the stream id is present. This needs to be
	 *         checked before getting the stream id from this object.
	 */
	public boolean isStreamIdPresent() {
		return this.streamId != null;
	}

	/**
	 * The method can be used to set Keep Active flag in the event descriptor.
	 */
	public void setKeepActive() {
		this.keepActive = new Boolean(true);
	}

	/**
	 * The method can be used to find if the Keep Active is present in the
	 * current object.
	 * 
	 * @return Returns true if the Keep Active is present.
	 */
	public boolean isKeepActivePresent() {
		return this.keepActive.booleanValue();
	}

	@Override
	public java.lang.String toString() {
		return this.getClass().getSimpleName() + " : StreamId = " + this.streamId + " : KeepActive = " + this.keepActive + " : SignalDesc[ " + this.signalDescriptor + " ] : DigitMapDescriptor[ "
				+ this.digitMapDescriptor + " ]";
	}

}
