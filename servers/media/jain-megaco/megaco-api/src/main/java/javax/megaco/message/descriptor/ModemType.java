package javax.megaco.message.descriptor;

import java.io.Serializable;

/**
 * Modem type constants used in package javax.megaco.message.descriptor. This
 * defines the Modem types for the megaco package.
 */
public class ModemType implements Serializable {

	/**
	 * Identifies Modem type to be V32bis. Its value shall be set to 0.
	 */
	public static final int M_MODEM_TYPE_V32BIS = 0;

	/**
	 * Identifies Modem type to be V22bis. Its value shall be set to 1.
	 */
	public static final int M_MODEM_TYPE_V22BIS = 1;

	/**
	 * Identifies Modem type to be V18. Its value shall be set to 2.
	 */
	public static final int M_MODEM_TYPE_V18 = 2;

	/**
	 * Identifies Modem type to be V22. Its value shall be set to 3.
	 */
	public static final int M_MODEM_TYPE_V22 = 3;

	/**
	 * Identifies Modem type to be V32. Its value shall be set to 4.
	 */
	public static final int M_MODEM_TYPE_V32 = 4;

	/**
	 * Identifies Modem type to be V34. Its value shall be set to 5.
	 */
	public static final int M_MODEM_TYPE_V34 = 5;

	/**
	 * Identifies Modem type to be V90. Its value shall be set to 6.
	 */
	public static final int M_MODEM_TYPE_V90 = 6;

	/**
	 * Identifies Modem type to be V91. Its value shall be set to 7.
	 */
	public static final int M_MODEM_TYPE_V91 = 7;

	/**
	 * Identifies Modem type to be Synch ISDN. Its value shall be set to 8.
	 */
	public static final int M_MODEM_TYPE_SYNCH_ISDN = 8;

	/**
	 * Identifies Modem type to be Extension Parameter. Its value shall be set
	 * to 9.
	 */
	public static final int M_MODEM_TYPE_EXT = 9;

	/**
	 * Identifies a Modem Type object that constructs the class with the
	 * constant M_MODEM_TYPE_V32BIS. Since it is reference to static final
	 * object, it prevents further instantiation of the same object in the
	 * system.
	 */
	public static final ModemType MODEM_TYPE_V32BIS

	= new ModemType(M_MODEM_TYPE_V32BIS);

	/**
	 * Identifies a Modem Type object that constructs the class with the
	 * constant M_MODEM_TYPE_V22BIS. Since it is reference to static final
	 * object, it prevents further instantiation of the same object in the
	 * system.
	 */
	public static final ModemType MODEM_TYPE_V22BIS

	= new ModemType(M_MODEM_TYPE_V22BIS);

	/**
	 * Identifies a Modem Type object that constructs the class with the
	 * constant M_MODEM_TYPE_V18. Since it is reference to static final object,
	 * it prevents further instantiation of the same object in the system.
	 */
	public static final ModemType MODEM_TYPE_V18

	= new ModemType(M_MODEM_TYPE_V18);

	/**
	 * Identifies a Modem Type object that constructs the class with the
	 * constant M_MODEM_TYPE_V22. Since it is reference to static final object,
	 * it prevents further instantiation of the same object in the system.
	 */
	public static final ModemType MODEM_TYPE_V22

	= new ModemType(M_MODEM_TYPE_V22);

	/**
	 * Identifies a Modem Type object that constructs the class with the
	 * constant M_MODEM_TYPE_V32. Since it is reference to static final object,
	 * it prevents further instantiation of the same object in the system.
	 */
	public static final ModemType MODEM_TYPE_V32

	= new ModemType(M_MODEM_TYPE_V32);

	/**
	 * Identifies a Modem Type object that constructs the class with the
	 * constant M_MODEM_TYPE_V34. Since it is reference to static final object,
	 * it prevents further instantiation of the same object in the system.
	 */
	public static final ModemType MODEM_TYPE_V34

	= new ModemType(M_MODEM_TYPE_V34);

	/**
	 * Identifies a Modem Type object that constructs the class with the
	 * constant M_MODEM_TYPE_V90. Since it is reference to static final object,
	 * it prevents further instantiation of the same object in the system.
	 */
	public static final ModemType MODEM_TYPE_V90

	= new ModemType(M_MODEM_TYPE_V90);

	/**
	 * Identifies a Modem Type object that constructs the class with the
	 * constant M_MODEM_TYPE_V91. Since it is reference to static final object,
	 * it prevents further instantiation of the same object in the system.
	 */
	public static final ModemType MODEM_TYPE_V91

	= new ModemType(M_MODEM_TYPE_V91);

	/**
	 * Identifies a Modem Type object that constructs the class with the
	 * constant M_MODEM_TYPE_SYNCH_ISDN. Since it is reference to static final
	 * object, it prevents further instantiation of the same object in the
	 * system.
	 */
	public static final ModemType MODEM_TYPE_SYNCH_ISDN

	= new ModemType(M_MODEM_TYPE_SYNCH_ISDN);

	/**
	 * Identifies a Modem Type object that constructs the class with the
	 * constant M_MODEM_TYPE_EXT. Since it is reference to static final object,
	 * it prevents further instantiation of the same object in the system.
	 */
	public static final ModemType MODEM_TYPE_EXT

	= new ModemType(M_MODEM_TYPE_EXT);

	private int modemType = -1;

	/**
	 * Constructs an class that specifies the Modem type in the Modem
	 * descriptor.
	 * 
	 * @param Modem_type
	 */
	private ModemType(int modem_type) {
		this.modemType = modem_type;
	}

	/**
	 * This method returns one of the static field constants defined in this
	 * class.
	 * 
	 * @return Returns an integer value that identifies the Modem type to be one
	 *         of V32bis, V22bis, V32, V22, V18, V34, V90, V91, Synch ISDN or
	 *         extension.
	 */
	public int getModemType() {
		return this.modemType;
	}

	/**
	 * Returns reference of the ModemType object that identifies the modem type
	 * as value passed to this method.
	 * 
	 * @param value
	 *            - It is one of the possible values of the static constant that
	 *            this class provides.
	 * @return Returns reference of the ModemType object.
	 * @throws IllegalArgumentException
	 *             - If the value passed to this method is invalid, then this
	 *             exception is raised.
	 */
	public static final ModemType getObject(int value) throws IllegalArgumentException {
		switch (value) {

		case M_MODEM_TYPE_EXT:
			return MODEM_TYPE_EXT;
		case M_MODEM_TYPE_SYNCH_ISDN:
			return MODEM_TYPE_SYNCH_ISDN;
		case M_MODEM_TYPE_V18:
			return MODEM_TYPE_V18;
		case M_MODEM_TYPE_V22:
			return MODEM_TYPE_V22;
		case M_MODEM_TYPE_V22BIS:
			return MODEM_TYPE_V22BIS;
		case M_MODEM_TYPE_V32:
			return MODEM_TYPE_V32;
		case M_MODEM_TYPE_V32BIS:
			return MODEM_TYPE_V32BIS;
		case M_MODEM_TYPE_V34:
			return MODEM_TYPE_V34;
		case M_MODEM_TYPE_V90:
			return MODEM_TYPE_V90;
		case M_MODEM_TYPE_V91:
			return MODEM_TYPE_V91;

		default:
			throw new IllegalArgumentException("Wrong modem type passed: " + value);

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
		return getObject(this.modemType);
	}

	@Override
	public String toString() {

		switch (this.modemType) {
		case M_MODEM_TYPE_EXT:
			return "ModemType[MODEM_TYPE_EXT]";
		case M_MODEM_TYPE_SYNCH_ISDN:
			return "ModemType[MODEM_TYPE_SYNCH_ISDN]";
		case M_MODEM_TYPE_V18:
			return "ModemType[MODEM_TYPE_V18]";
		case M_MODEM_TYPE_V22:
			return "ModemType[MODEM_TYPE_V22]";
		case M_MODEM_TYPE_V22BIS:
			return "ModemType[MODEM_TYPE_V22BIS]";
		case M_MODEM_TYPE_V32:
			return "ModemType[MODEM_TYPE_V32]";
		case M_MODEM_TYPE_V32BIS:
			return "ModemType[MODEM_TYPE_V32BIS]";
		case M_MODEM_TYPE_V34:
			return "ModemType[MODEM_TYPE_V34]";
		case M_MODEM_TYPE_V90:
			return "ModemType[MODEM_TYPE_V90]";
		case M_MODEM_TYPE_V91:
			return "ModemType[MODEM_TYPE_V91]";

		default:
			return "ModemType[" + this.modemType + "]";

		}
	}

}
