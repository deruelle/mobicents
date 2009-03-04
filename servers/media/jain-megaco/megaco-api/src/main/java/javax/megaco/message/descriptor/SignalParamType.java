package javax.megaco.message.descriptor;

import java.io.Serializable;

/**
 * Signal Param type class defines all the static constants for the signal param
 * type for the megaco package.
 */
public class SignalParamType implements Serializable {

	/**
	 * Identifies signal param type to be list. Its value shall be set to 1.
	 */
	public static final int M_SIGNAL_PARAM_LIST = 1;

	/**
	 * Identifies signal param type to be request. Its value shall be set to 2.
	 */
	public static final int M_SIGNAL_PARAM_REQUEST = 2;

	/**
	 * Identifies a MegacoSignalParamType object that constructs the class with
	 * the constant M_SIGNAL_PARAM_LIST. Since it is reference to static final
	 * object, it prevents further instantiation of the same object in the
	 * system.
	 */
	public static final SignalParamType SIGNAL_PARAM_LIST = new SignalParamType(M_SIGNAL_PARAM_LIST);

	/**
	 * Identifies a MegacoSignalParamType object that constructs the class with
	 * the constant M_SIGNAL_PARAM_REQUEST. Since it is reference to static
	 * final object, it prevents further instantiation of the same object in the
	 * system.
	 */
	public static final SignalParamType SIGNAL_PARAM_REQUEST = new SignalParamType(M_SIGNAL_PARAM_REQUEST);

	private int signalParamType = -1;

	/**
	 * Constructs an class that specifies the signal param type in the signal
	 * descriptor.
	 * 
	 * @param signal_type
	 */
	private SignalParamType(int signal_param_type) {
		this.signalParamType = signal_param_type;
	}

	/**
	 * This method returns one of the static field constants defined in this
	 * class.
	 * 
	 * @return Returns an integer value that identifies the signal type to be
	 *         one of brief or on-off or other.
	 */
	public int getsignalParamType() {
		return this.signalParamType;
	}

	/**
	 * Returns reference of the SignalParamType object that identifies the
	 * signal type as value passed to this method.
	 * 
	 * @param value
	 *            - It is one of the possible values of the static constant that
	 *            this class provides.
	 * @return Returns reference of the signalParamType object.
	 * @throws IllegalArgumentException
	 *             - If the value passed to this method is invalid, then this
	 *             exception is raised.
	 */
	public static final SignalParamType getObject(int value) throws IllegalArgumentException {

		switch (value) {
		case M_SIGNAL_PARAM_LIST:
			return SIGNAL_PARAM_LIST;
		case M_SIGNAL_PARAM_REQUEST:
			return SIGNAL_PARAM_REQUEST;

		default:
			throw new IllegalArgumentException("Wrong signal param type passed: " + value);
		}

	}

	private Object readResolve() {
		return this.getObject(this.signalParamType);
	}

	@Override
	public String toString() {
		switch (this.signalParamType) {
		case M_SIGNAL_PARAM_LIST:
			return "SignalParamType[LIST]";
		case M_SIGNAL_PARAM_REQUEST:
			return "SignalParamType[REQUEST]";

		default:
			return "SignalParamType[" + this.signalParamType + "]";
		}
	}

}
