package javax.megaco.message.descriptor;

import java.io.Serializable;

/**
 * Event buffer control constants used in package
 * javax.megaco.message.descriptor. This forms the base class for all derived
 * classes for the event buffer control for the megaco package.
 */
public class EventBufferCtrl implements Serializable {

	/**
	 * Identifies event buffer control to be off. Its value shall be set to 0.
	 */
	public static final int M_OFF = 0;

	/**
	 * Identifies event buffer control to be lock step. Its value shall be set
	 * to 1.
	 */
	public static final int M_LOCK_STEP = 1;

	/**
	 * Identifies a EventBufferCtrl object that constructs the class with the
	 * constant M_OFF. Since it is reference to static
	 * final object, it prevents further instantiation of the same object in the
	 * system.
	 */
	public static final EventBufferCtrl OFF = new EventBufferCtrl(M_OFF);

	/**
	 * Identifies a EventBufferCtrl object that constructs the class with the
	 * constant M_LOCK_STEP. Since it is reference to
	 * static final object, it prevents further instantiation of the same object
	 * in the system.
	 */
	public static final EventBufferCtrl LOCK_STEP = new EventBufferCtrl(M_LOCK_STEP);

	private int eventBufferCtrl = -1;

	/**
	 * Constructs an class that specifies the event buffer control in the
	 * termination state descriptor.
	 * 
	 * @param evt_buffer_ctrl
	 */
	private EventBufferCtrl(int evt_buffer_ctrl) {
		this.eventBufferCtrl = evt_buffer_ctrl;
	}

	/**
	 * This method returns one of the static field constants defined in this
	 * class.
	 * 
	 * @return Returns an integer value that identifies the event buffer control
	 *         to be one of off or lock step.
	 */
	public int getEvtBufferControl() {
		return this.eventBufferCtrl;
	}

	/**
	 * Returns reference of the EventBufferCtrl object that identifies the
	 * command type as value passed to this method.
	 * 
	 * @param value
	 *            - It is one of the possible values of the static constant that
	 *            this class provides.
	 * @return Returns reference of the EventBufferCtrl object.
	 * @throws IllegalArgumentException
	 *             - If the value passed to this method is invalid, then this
	 *             exception is raised.
	 */
	public static final EventBufferCtrl getObject(int value) throws IllegalArgumentException {
		switch (value) {
		case M_LOCK_STEP:
			return LOCK_STEP;
		case M_OFF:
			return OFF;

		default:
			throw new IllegalArgumentException("Wrong value of EventBufferCtrl: " + value);

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
		return getObject(this.eventBufferCtrl);
	}

	@Override
	public String toString() {
		switch (this.eventBufferCtrl) {
		case M_LOCK_STEP:
			return "EventBufferCtrl[LOCK_STEP]";
		case M_OFF:
			return "EventBufferCtrl[OFF]";

		default:
			return "EventBufferCtrl[" + this.eventBufferCtrl + "]";

		}
	}

}
