package javax.megaco.message;

import java.io.Serializable;

/**
 * Termination type constants for the termination type for the megaco package.
 * 
 * 
 */
public class TermType implements Serializable {
	public static final int M_NORMAL = 1;
	public static final int M_CHOOSE = 2;
	public static final int M_ROOT = 3;
	public static final int M_WILDCARD = 4;

	public static final TermType NORMAL = new TermType(M_NORMAL);
	public static final TermType CHOOSE = new TermType(M_CHOOSE);
	public static final TermType ROOT = new TermType(M_ROOT);
	public static final TermType WILDCARD = new TermType(M_WILDCARD);

	private int term_type;

	/**
	 * Constructs an abstract class that specifies the termination type in the
	 * command.
	 * 
	 * @param term_type
	 */
	private TermType(int term_type) {
		this.term_type = term_type;

	}

	/**
	 * This method returns one of the static field constants defined in this
	 * class. This method is overriden by the derived class, which shall return
	 * an hard coded value depending on the class.
	 * 
	 * @return Returns an integer value that identifies the termination type to
	 *         be one of normal, or choose or root or wildcarded.
	 */
	public int getTermType() {
		return this.term_type;
	}

	/**
	 * Returns reference of the TermType object that identifies the termination
	 * type as value passed to this method.
	 * 
	 * @param value
	 *            It is one of the possible values of the static constant that
	 *            this class provides.
	 * @return Returns reference of the TermType object.
	 * @throws IllegalArgumentException
	 */
	public static final TermType getObject(int value) throws IllegalArgumentException {
		TermType t = null;
		switch (value) {
		case M_NORMAL:
			t = NORMAL;
			break;
		case M_CHOOSE:
			t = CHOOSE;
			break;

		case M_ROOT:
			t = ROOT;
			break;

		case M_WILDCARD:
			t = WILDCARD;
			break;
		default:
			throw new IllegalArgumentException("TermType not found for value " + value);
		}
		return t;
	}

	private Object readResolve() {
		return this.getObject(this.term_type);
	}

	@Override
	public String toString() {
		String t = null;
		switch (this.term_type) {
		case M_NORMAL:
			t = "TermType[NORMAL]";
			break;
		case M_CHOOSE:
			t = "TermType[CHOOSE]";
			break;

		case M_ROOT:
			t = "TermType[ROOT]";
			break;

		case M_WILDCARD:
			t = "TermType[WILDCARD]";
			break;
		default:
			t = "TermType[" + this.term_type + "]";
		}
		return t;
	}

}
