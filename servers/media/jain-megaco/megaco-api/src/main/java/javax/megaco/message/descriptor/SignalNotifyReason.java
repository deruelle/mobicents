package javax.megaco.message.descriptor;

/**
 * Signal Notification reason constants used in package
 * javax.megaco.message.descriptor. This defines the notification reasons for
 * the megaco package.
 */
public class SignalNotifyReason {

	/**
	 * Identifies notification reason to be timeout. Its value shall be set to
	 * 1.
	 */
	public static final int M_NOTIFY_REASON_TIMEOUT = 1;

	/**
	 * Identifies notification reason to be interrupted by event. Its value
	 * shall be set to 2.
	 */
	public static final int M_NOTIFY_REASON_INT_EVENT = 2;

	/**
	 * Identifies notification reason to be interrupted by application of
	 * another signal. Its value shall be set to 3.
	 */
	public static final int M_NOTIFY_REASON_INT_SIGNAL = 3;

	/**
	 * Identifies notification reason to be "other". Its value shall be set to
	 * 4.
	 */
	public static final int M_NOTIFY_REASON_OTHER = 4;

	/**
	 * Identifies a SignalNotifyReason object that constructs the class with the
	 * constant M_NOTIFY_REASON_TIMEOUT. Since it is reference to static final
	 * object, it prevents further instantiation of the same object in the
	 * system.
	 */
	public static final SignalNotifyReason NOTIFY_REASON_TIMEOUT = new SignalNotifyReason(M_NOTIFY_REASON_TIMEOUT);

	/**
	 * Identifies a SignalNotifyReason object that constructs the class with the
	 * constant M_NOTIFY_REASON_INT_EVENT. Since it is reference to static final
	 * object, it prevents further instantiation of the same object in the
	 * system.
	 */
	public static final SignalNotifyReason NOTIFY_REASON_INT_EVENT

	= new SignalNotifyReason(M_NOTIFY_REASON_INT_EVENT);

	/**
	 * Identifies a SignalNotifyReason object that constructs the class with the
	 * constant M_NOTIFY_REASON_INT_SIGNAL. Since it is reference to static
	 * final object, it prevents further instantiation of the same object in the
	 * system.
	 */
	public static final SignalNotifyReason NOTIFY_REASON_INT_SIGNAL

	= new SignalNotifyReason(M_NOTIFY_REASON_INT_SIGNAL);

	/**
	 * Identifies a SignalNotifyReason object that constructs the class with the
	 * constant M_NOTIFY_REASON_OTHER. Since it is reference to static final
	 * object, it prevents further instantiation of the same object in the
	 * system.
	 */
	public static final SignalNotifyReason NOTIFY_REASON_OTHER

	= new SignalNotifyReason(M_NOTIFY_REASON_OTHER);

	private int notifyReason = -1;

	/**
	 * 
	 * Constructs an class that specifies the notification reason in the signal
	 * request.
	 * 
	 * @param signal_notify_reason
	 */
	private SignalNotifyReason(int signal_notify_reason) {
		this.notifyReason = signal_notify_reason;
	}

	/**
	 * This method returns one of the static field constants defined in this
	 * class.
	 * 
	 * @return Returns an integer value that identifies the notification reason
	 *         to be one of timeout or interrupted by event or interrupted by
	 *         application of another signal or other.
	 */
	public int getNotifyReason() {
		return notifyReason;
	}

	/**
	 * Returns reference of the SignalNotifyReason object that identifies the
	 * signal notify reason as value passed to this method.
	 * 
	 * @param value
	 *            - It is one of the possible values of the static constant that
	 *            this class provides.
	 * @return Returns reference of the SignalNotifyReason object.
	 * @throws IllegalArgumentException
	 *             - If the value passed to this method is invalid, then this
	 *             exception is raised.
	 */
	public static final SignalNotifyReason getObject(int value) throws IllegalArgumentException {

		switch (value) {
		case M_NOTIFY_REASON_INT_EVENT:
			return NOTIFY_REASON_INT_EVENT;
		case M_NOTIFY_REASON_INT_SIGNAL:
			return NOTIFY_REASON_INT_SIGNAL;
		case M_NOTIFY_REASON_OTHER:
			return NOTIFY_REASON_OTHER;
		case M_NOTIFY_REASON_TIMEOUT:
			return NOTIFY_REASON_TIMEOUT;

		default:
			throw new IllegalArgumentException("Wrong signal notify reason passed: " + value);
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
		return getObject(this.notifyReason);
	}

}
