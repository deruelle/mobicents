package javax.megaco.message.descriptor;

import java.io.Serializable;

/**
 * Service state constants used in package javax.megaco.message.descriptor. This
 * defines the service state for the megaco package.
 */
public class ServiceState implements Serializable {

	/**
	 * Identifies service state to be out of service. Its value shall be set to
	 * 0.
	 */
	public static final int M_OOS = 0;

	/**
	 * Identifies service state to be in-service. Its value shall be set to 1.
	 */
	public static final int M_INS = 1;

	/**
	 * Identifies service state to be "test". Its value shall be set to 2.
	 */
	public static final int M_TEST = 2;

	/**
	 * Identifies a ServiceState object that constructs the class with the
	 * constant M_OOS. Since it is reference to static final
	 * object, it prevents further instantiation of the same object in the
	 * system.
	 */
	public static final ServiceState OOS = new ServiceState(M_OOS);

	/**
	 * Identifies a ServiceState object that constructs the class with the
	 * constant M_INS. Since it is reference to static final
	 * object, it prevents further instantiation of the same object in the
	 * system.
	 */
	public static final ServiceState INS = new ServiceState(M_INS);

	/**
	 * Identifies a ServiceState object that constructs the class with the
	 * constant M_TEST. Since it is reference to static final
	 * object, it prevents further instantiation of the same object in the
	 * system.
	 */
	public static final ServiceState TEST = new ServiceState(M_TEST);

	private int serviceState = -1;

	/**
	 * Constructs an class that specifies the service state in the termination
	 * state descriptor.
	 * 
	 * @param service_state
	 */
	private ServiceState(int service_state) {
		this.serviceState = service_state;
	}

	/**
	 * This method returns one of the static field constants defined in this
	 * class.
	 * 
	 * @return Returns an integer value that identifies the service state to be
	 *         one of out of service or in-service or test.
	 */
	public int getServiceState() {
		return this.serviceState;
	}

	/**
	 * Returns reference of the ServiceState object that identifies the service
	 * state as value passed to this method.
	 * 
	 * @param value
	 *            - It is one of the possible values of the static constant that
	 *            this class provides.
	 * @return Returns reference of the ServiceState object.
	 * @throws IllegalArgumentException
	 *             - If the value passed to this method is invalid, then this
	 *             exception is raised.
	 */
	public static final ServiceState getObject(int value) throws IllegalArgumentException {

		switch (value) {
		case M_INS:
			return INS;
		case M_OOS:
			return OOS;
		case M_TEST:
			return TEST;
		default:
			throw new IllegalArgumentException("Wrong value of service state: " + value);

		}

	}

	/**
	 * This method must be implemented to perform instance substitution during
	 * serialization. This method is required for reference comparison. This
	 * method if not implimented will simply fail each time we compare an
	 * Enumeration value against a de-serialized Enumeration instance.
	 * 
	 * @return
	 */
	private Object readResolve() {
		return getObject(this.serviceState);
	}

	@Override
	public String toString() {
		switch (this.serviceState) {
		case M_INS:
			return "ServiceState[INS]";
		case M_OOS:
			return "ServiceState[OOS]";
		case M_TEST:
			return "ServiceState[TEST]";
		default:
			return "ServiceState["+this.serviceState+"]";

		}

	}

}
