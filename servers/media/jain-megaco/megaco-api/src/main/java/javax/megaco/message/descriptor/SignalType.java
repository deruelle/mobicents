package javax.megaco.message.descriptor;

import java.io.Serializable;

/**
 * Signal type class defines all the static constants for the signal type for
 * the megaco package.
 */
public class SignalType implements Serializable {

	/**
	 * Identifies signal type to be brief. Its value shall be set to 1.
	 */
	public static final int M_SIGNAL_TYPE_BRIEF = 1;

	/**
	 * Identifies signal type to be on-off. Its value shall be set to 2.
	 */
	public static final int M_SIGNAL_TYPE_ON_OFF = 2;

	/**
	 * Identifies signal type to be "timeout". Its value shall be set to 3.
	 */
	public static final int M_SIGNAL_TYPE_TIMEOUT = 3;

	/**
	 * Identifies a SignalType object that constructs the class with the
	 * constant M_SIGNAL_TYPE_BRIEF. Since it is reference to static final
	 * object, it prevents further instantiation of the same object in the
	 * system.
	 */
	public static final SignalType SIGNAL_TYPE_BRIEF = new SignalType(M_SIGNAL_TYPE_BRIEF);

	/**
	 * Identifies a SignalType object that constructs the class with the
	 * constant M_SIGNAL_TYPE_ON_OFF. Since it is reference to static final
	 * object, it prevents further instantiation of the same object in the
	 * system.
	 */
	public static final SignalType SIGNAL_TYPE_ON_OFF = new SignalType(M_SIGNAL_TYPE_ON_OFF);

	/**
	 * Identifies a SignalType object that constructs the class with the
	 * constant M_SIGNAL_TYPE_TIMEOUT. Since it is reference to static final
	 * object, it prevents further instantiation of the same object in the
	 * system.
	 */
	public static final SignalType SIGNAL_TYPE_TIMEOUT = new SignalType(M_SIGNAL_TYPE_TIMEOUT);

	private int signalType = -1;

	/**
	 * Constructs an class that specifies the signal type in the signal request.
	 * 
	 * @param signal_type
	 */
	private SignalType(int signal_type) {
		this.signalType = signal_type;
	}

	/**
	 * This method returns one of the static field constants defined in this
	 * class.
	 * 
	 * @return Returns an integer value that identifies the signal type to be
	 *         one of brief or on-off or other.
	 */
	public int getSignalType() {
		return this.signalType;
	}

	/**
	 * Returns reference of the SignalType object that identifies the signal
	 * type as value passed to this method.
	 * 
	 * @param value
	 *            - It is one of the possible values of the static constant that
	 *            this class provides.
	 * @return Returns reference of the SignalType object.
	 * @throws IllegalArgumentException
	 *             - If the value passed to this method is invalid, then this
	 *             exception is raised.
	 */
	public static final SignalType getObject(int value) throws IllegalArgumentException {

		switch (value) {
		case M_SIGNAL_TYPE_BRIEF:
			return SIGNAL_TYPE_BRIEF;
		case M_SIGNAL_TYPE_ON_OFF:
			return SIGNAL_TYPE_ON_OFF;
		case M_SIGNAL_TYPE_TIMEOUT:
			return SIGNAL_TYPE_TIMEOUT;
		default:
			throw new IllegalArgumentException("Wrong signal type passed: " + value);
		}

	}

	private Object readResolve() {
		return this.getObject(this.signalType);
	}

}
