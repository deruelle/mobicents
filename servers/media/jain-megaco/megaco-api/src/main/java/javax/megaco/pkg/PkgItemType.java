package javax.megaco.pkg;

import java.io.Serializable;

/**
 * Constants used in package javax.megaco.pkg for defining item types.
 * 
 * 
 */
public final class PkgItemType implements Serializable {

	public static final int M_ALL = 1;
	public static final int M_EVENT = 2;
	public static final int M_SIGNAL = 3;
	public static final int M_STATISTICS = 4;
	public static final int M_PROPERTY = 5;

	/**
	 * Identifies a package item type object that constructs the class with the
	 * constant M_ALL. Since it is reference to static final object, it prevents
	 * further instantiation of the same object in the system.
	 */
	public static final PkgItemType ALL = new PkgItemType(M_ALL);

	/**
	 * Identifies a package item type object that constructs the class with the
	 * constant M_EVENT. Since it is reference to static final object, it
	 * prevents further instantiation of the same object in the system.
	 */
	public static final PkgItemType EVENT = new PkgItemType(M_EVENT);

	/**
	 * Identifies a package item type object that constructs the class with the
	 * constant M_SIGNAL. Since it is reference to static final object, it
	 * prevents further instantiation of the same object in the system.
	 */
	public static final PkgItemType SIGNAL = new PkgItemType(M_SIGNAL);

	/**
	 * Identifies a package item type object that constructs the class with the
	 * constant M_STATISTICS. Since it is reference to static final object, it
	 * prevents further instantiation of the same object in the system.
	 */
	public static final PkgItemType STATISTICS = new PkgItemType(M_STATISTICS);

	/**
	 * Identifies a package item type object that constructs the class with the
	 * constant M_PROPERTY. Since it is reference to static final object, it
	 * prevents further instantiation of the same object in the system.
	 */
	public static final PkgItemType PROPERTY = new PkgItemType(M_PROPERTY);

	private int type;

	private PkgItemType(int type) {
		this.type = type;
	}

	public int getPkgItemType() {
		return this.type;
	}

	public static final PkgItemType getObject(int value) throws IllegalArgumentException {
		PkgItemType p = null;
		switch (value) {
		case M_ALL:
			p = ALL;
			break;
		case M_EVENT:
			p = EVENT;
			break;
		case M_SIGNAL:
			p = SIGNAL;
			break;
		case M_STATISTICS:
			p = STATISTICS;
			break;
		case M_PROPERTY:
			p = PROPERTY;
			break;
		default:
			throw new IllegalArgumentException("There is no PkgItemType for passed value = " + value);
		}
		return p;
	}

	private Object readResolve() {
		return this.getObject(this.type);
	}

	@Override
	public String toString() {
		String p = null;
		switch (this.type) {
		case M_ALL:
			p = "PkgItemType[ALL]";
			break;
		case M_EVENT:
			p = "PkgItemType[EVENT]";
			break;
		case M_SIGNAL:
			p = "PkgItemType[SIGNAL]";
			break;
		case M_STATISTICS:
			p = "PkgItemType[STATISTICS]";
			break;
		case M_PROPERTY:
			p = "PkgItemType[PROPERTY]";
			break;
		default:
			p = "PkgItemType[" + this.type + "]";
		}
		return p;
	}

}
