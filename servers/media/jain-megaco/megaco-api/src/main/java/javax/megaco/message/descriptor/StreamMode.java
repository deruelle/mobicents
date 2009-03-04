package javax.megaco.message.descriptor;

import java.io.Serializable;

/**
 * Stream mode constants used in package javax.megaco.message.descriptor. This
 * defines the stream modes for the megaco package.
 */
public class StreamMode implements Serializable {

	/**
	 * Identifies stream mode to be send only. Its value shall be set to 0.
	 */
	public static final int M_STREAM_MODE_SEND_ONLY = 0;

	/**
	 * Identifies stream mode to be receive only. Its value shall be set to 1.
	 */
	public static final int M_STREAM_MODE_RECV_ONLY = 1;

	/**
	 * Identifies stream mode to be "send receive". Its value shall be set to 2.
	 */
	public static final int M_STREAM_MODE_SEND_RECV = 2;

	/**
	 * Identifies stream mode to be "inactive". Its value shall be set to 3.
	 */
	public static final int M_STREAM_MODE_INACTIVE = 3;

	/**
	 * Identifies stream mode to be "loopback". Its value shall be set to 4.
	 */
	public static final int M_STREAM_MODE_LOOPBACK = 4;

	/**
	 * Identifies a StreamMode object that constructs the class with the
	 * constant M_STREAM_MODE_SEND_ONLY. Since it is reference to static final
	 * object, it prevents further instantiation of the same object in the
	 * system.
	 */
	public static final StreamMode STREAM_MODE_SEND_ONLY = new StreamMode(M_STREAM_MODE_SEND_ONLY);

	/**
	 * Identifies a StreamMode object that constructs the class with the
	 * constant M_STREAM_MODE_SEND_RECV. Since it is reference to static final
	 * object, it prevents further instantiation of the same object in the
	 * system.
	 */
	public static final StreamMode STREAM_MODE_SEND_RECV = new StreamMode(M_STREAM_MODE_SEND_RECV);

	/**
	 * Identifies a StreamMode object that constructs the class with the
	 * constant M_STREAM_MODE_RECV_ONLY. Since it is reference to static final
	 * object, it prevents further instantiation of the same object in the
	 * system.
	 */
	public static final StreamMode STREAM_MODE_RECV_ONLY = new StreamMode(M_STREAM_MODE_RECV_ONLY);

	/**
	 * Identifies a StreamMode object that constructs the class with the
	 * constant M_STREAM_MODE_INACTIVE. Since it is reference to static final
	 * object, it prevents further instantiation of the same object in the
	 * system.
	 */
	public static final StreamMode STREAM_MODE_INACTIVE = new StreamMode(M_STREAM_MODE_INACTIVE);

	/**
	 * Identifies a StreamMode object that constructs the class with the
	 * constant M_STREAM_MODE_LOOPBACK. Since it is reference to static final
	 * object, it prevents further instantiation of the same object in the
	 * system.
	 */
	public static final StreamMode STREAM_MODE_LOOPBACK = new StreamMode(M_STREAM_MODE_LOOPBACK);

	private int streamMode = -1;

	/**
	 * 
	 * Constructs an class that specifies the stream mode in the local control
	 * descriptor.
	 * 
	 * @param stream_mode
	 */
	private StreamMode(int stream_mode) {
		this.streamMode = stream_mode;
	}

	/**
	 * This method returns one of the static field constants defined in this
	 * class.
	 * 
	 * @return Returns an integer value that identifies the stream mode to be
	 *         one of send only or receive only or send receive or inactive or
	 *         loopback.
	 */
	public int getStreamMode() {
		return this.streamMode;
	}

	/**
	 * Returns reference of the StreamMode object that identifies the stream
	 * mode as value passed to this method.
	 * 
	 * @param value
	 *            - It is one of the possible values of the static constant that
	 *            this class provides.
	 * @return Returns reference of the StreamMode object.
	 * @throws IllegalArgumentException
	 *             - If the value passed to this method is invalid, then this
	 *             exception is raised.
	 */
	public static final StreamMode getObject(int value) throws IllegalArgumentException {

		switch (value) {
		case M_STREAM_MODE_INACTIVE:
			return STREAM_MODE_INACTIVE;
		case M_STREAM_MODE_LOOPBACK:
			return STREAM_MODE_LOOPBACK;
		case M_STREAM_MODE_RECV_ONLY:
			return STREAM_MODE_RECV_ONLY;
		case M_STREAM_MODE_SEND_ONLY:
			return STREAM_MODE_SEND_ONLY;
		case M_STREAM_MODE_SEND_RECV:
			return STREAM_MODE_SEND_RECV;

		default:
			throw new IllegalArgumentException("Wrong value passed as StreamMode: " + value);
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
		return getObject(this.streamMode);
	}

}
