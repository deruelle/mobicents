package javax.megaco.pkg;

import java.io.Serializable;

/**
 * Constants used in package javax.megaco.pkg for defining item types.
 * 
 * 
 */
public final class PkgItemType implements Serializable {

	public static final int M_ITEM_ALL = 1;
	public static final int M_ITEM_EVENT = 2;
	public static final int M_ITEM_SIGNAL = 3;
	public static final int M_ITEM_STATISTICS = 4;
	public static final int M_ITEM_PROPERTY = 5;

	/**
	 * Identifies a package item type object that constructs the class with the
	 * constant M_ITEM_ALL. Since it is reference to static final object, it
	 * prevents further instantiation of the same object in the system.
	 */
	public static final PkgItemType ITEM_ALL = new PkgItemType(M_ITEM_ALL);

	/**
	 * Identifies a package item type object that constructs the class with the
	 * constant M_ITEM_EVENT. Since it is reference to static final object, it
	 * prevents further instantiation of the same object in the system.
	 */
	public static final PkgItemType ITEM_EVENT = new PkgItemType(M_ITEM_EVENT);

	/**
	 * Identifies a package item type object that constructs the class with the
	 * constant M_ITEM_SIGNAL. Since it is reference to static final object, it
	 * prevents further instantiation of the same object in the system.
	 */
	public static final PkgItemType ITEM_SIGNAL = new PkgItemType(M_ITEM_SIGNAL);

	/**
	 * Identifies a package item type object that constructs the class with the
	 * constant M_ITEM_STATISTICS. Since it is reference to static final object,
	 * it prevents further instantiation of the same object in the system.
	 */
	public static final PkgItemType ITEM_STATISTICS = new PkgItemType(M_ITEM_STATISTICS);

	/**
	 * Identifies a package item type object that constructs the class with the
	 * constant M_ITEM_PROPERTY. Since it is reference to static final object,
	 * it prevents further instantiation of the same object in the system.
	 */
	public static final PkgItemType ITEM_PROPERTY = new PkgItemType(M_ITEM_PROPERTY);

	private int item_type;

	private PkgItemType(int item_type) {
		this.item_type = item_type;
	}

	public int getPkgItemType() {
		return this.item_type;
	}

	public static final PkgItemType getObject(int value) throws IllegalArgumentException {
		PkgItemType p = null;
		switch (value) {
		case M_ITEM_ALL:
			p = ITEM_ALL;
			break;
		case M_ITEM_EVENT:
			p = ITEM_EVENT;
			break;
		case M_ITEM_SIGNAL:
			p = ITEM_SIGNAL;
			break;
		case M_ITEM_STATISTICS:
			p = ITEM_STATISTICS;
			break;
		case M_ITEM_PROPERTY:
			p = ITEM_PROPERTY;
			break;
		default:
			throw new IllegalArgumentException("There is no PkgItemType for passed value = " + value);
		}
		return p;
	}

	private Object readResolve() {
		return this.getObject(this.item_type);
	}

	@Override
	public String toString() {
		String p = null;
		switch (this.item_type) {
		case M_ITEM_ALL:
			p = "PkgItemType[ALL]";
			break;
		case M_ITEM_EVENT:
			p = "PkgItemType[EVENT]";
			break;
		case M_ITEM_SIGNAL:
			p = "PkgItemType[SIGNAL]";
			break;
		case M_ITEM_STATISTICS:
			p = "PkgItemType[STATISTICS]";
			break;
		case M_ITEM_PROPERTY:
			p = "PkgItemType[PROPERTY]";
			break;
		default:
			p = "PkgItemType[" + this.item_type + "]";
		}
		return p;
	}

}
