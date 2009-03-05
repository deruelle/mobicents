package javax.megaco.message.descriptor;

import java.io.Serializable;

/**
 * Mux type constants used in package javax.megaco.message.descriptor. This
 * defines the mux type for the megaco package.
 */
public class MuxType implements Serializable {

	/**
	 * Identifies mux type to be h221. Its value shall be set to 0.
	 */
	public static final int M_H221 = 0;

	/**
	 * Identifies mux type to be h223. Its value shall be set to 1.
	 */
	public static final int M_H223 = 1;

	/**
	 * Identifies mux type to be "h226". Its value shall be set to 2.
	 */
	public static final int M_H226 = 2;

	/**
	 * Identifies mux type to be "v76". Its value shall be set to 3.
	 */
	public static final int M_V76 = 3;

	/**
	 * Identifies mux type to be "extension". Its value shall be set to 4.
	 */
	public static final int M_EXT = 4;

	/**
	 * Identifies a Mux Type object that constructs the class with the constant
	 * M_H221. Since it is reference to static final object, it
	 * prevents further instantiation of the same object in the system.
	 */
	public static final MuxType H221

	= new MuxType(M_H221);

	/**
	 * Identifies a Mux Type object that constructs the class with the constant
	 * M_H223. Since it is reference to static final object, it
	 * prevents further instantiation of the same object in the system.
	 */
	public static final MuxType H223

	= new MuxType(M_H223);

	/**
	 * Identifies a Mux Type object that constructs the class with the constant
	 * M_H226. Since it is reference to static final object, it
	 * prevents further instantiation of the same object in the system.
	 */
	public static final MuxType H226

	= new MuxType(M_H226);

	/**
	 * Identifies a Mux Type object that constructs the class with the constant
	 * M_V76. Since it is reference to static final object, it prevents
	 * further instantiation of the same object in the system.
	 */
	public static final MuxType V76

	= new MuxType(M_V76);

	/**
	 * Identifies a Mux Type object that constructs the class with the constant
	 * M_EXT. Since it is reference to static final object, it prevents
	 * further instantiation of the same object in the system.
	 */
	public static final MuxType EXT

	= new MuxType(M_EXT);

	private int muxType = -1;

	/**
	 * Constructs an class that specifies the mux type in the Mux descriptor.
	 * 
	 * @param mux_type
	 */
	private MuxType(int mux_type) {
		this.muxType = mux_type;
	}

	/**
	 * This method returns one of the static field constants defined in this
	 * class.
	 * 
	 * @return Returns an integer value that identifies the mux type to be one
	 *         of h221 or h223 or h226 or v26 or extension.
	 */
	public int getMuxType() {
		return this.muxType;
	}

	/**
	 * Returns reference of the MuxType object that identifies the mux type as
	 * value passed to this method.
	 * 
	 * @param value
	 *            - It is one of the possible values of the static constant that
	 *            this class provides.
	 * @return Returns reference of the MuxType object.
	 * @throws IllegalArgumentException
	 *             - If the value passed to this method is invalid, then this
	 *             exception is raised.
	 */
	public static final MuxType getObject(int value) throws IllegalArgumentException {

		switch (value) {

		case M_EXT:
			return EXT;
		case M_H221:
			return H221;
		case M_H223:
			return H223;
		case M_H226:
			return H226;
		case M_V76:
			return V76;

		default:
			throw new IllegalArgumentException("Wrong mux type passed: " + value);

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
		return getObject(this.muxType);
	}

	@Override
	public String toString() {
		switch (this.muxType) {

		case M_EXT:
			return "MuxType[EXT]";
		case M_H221:
			return "MuxType[H221]";
		case M_H223:
			return "MuxType[H223]";
		case M_H226:
			return "MuxType[H226]";
		case M_V76:
			return "MuxType[V76]";

		default:
			return "MuxType[" + this.muxType + "]";

		}
	}

}
