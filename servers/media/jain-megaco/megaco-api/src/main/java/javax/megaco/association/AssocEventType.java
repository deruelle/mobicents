package javax.megaco.association;

/**
 * Constants used in package javax.megaco.association. This defines the type of
 * the association event. It qualifies each association event.
 * 
 * 
 */
public final class AssocEventType {

	public static final int M_CREATE_ASSOC_REQ = 1;
	public static final int M_DELETE_ASSOC_REQ = 2;
	public static final int M_MODIFY_ASSOC_REQ = 3;
	public static final int M_ASSOC_STATE_IND = 4;
	public static final int M_CREATE_ASSOC_RESP = 5;
	public static final int M_DELETE_ASSOC_RESP = 6;
	public static final int M_MODIFY_ASSOC_RESP = 7;
	public static final int M_ASSOC_CONFIG_REQ = 8;
	public static final int M_ASSOC_CONFIG_RESP = 9;
	public static final int M_DELETE_TXN_REQ = 10;
	public static final int M_DELETE_TXN_RESP = 11;
	public static final int M_CREATE_TXN_REQ = 12;
	public static final int M_CREATE_TXN_RESP = 13;
	public static final int M_TXN_ERR_IND = 14;

	public static final AssocEventType CREATE_ASSOC_REQ = new AssocEventType(M_CREATE_ASSOC_REQ);

	public static final AssocEventType DELETE_ASSOC_REQ = new AssocEventType(M_DELETE_ASSOC_REQ);

	public static final AssocEventType MODIFY_ASSOC_REQ = new AssocEventType(M_MODIFY_ASSOC_REQ);

	public static final AssocEventType ASSOC_STATE_IND = new AssocEventType(M_ASSOC_STATE_IND);

	public static final AssocEventType CREATE_ASSOC_RESP = new AssocEventType(M_CREATE_ASSOC_RESP);

	public static final AssocEventType DELETE_ASSOC_RESP = new AssocEventType(M_DELETE_ASSOC_RESP);

	public static final AssocEventType MODIFY_ASSOC_RESP = new AssocEventType(M_MODIFY_ASSOC_RESP);

	public static final AssocEventType ASSOC_CONFIG_REQ = new AssocEventType(M_ASSOC_CONFIG_REQ);

	public static final AssocEventType ASSOC_CONFIG_RESP = new AssocEventType(M_ASSOC_CONFIG_RESP);

	public static final AssocEventType DELETE_TXN_REQ = new AssocEventType(M_DELETE_TXN_REQ);

	public static final AssocEventType DELETE_TXN_RESP = new AssocEventType(M_DELETE_TXN_RESP);

	public static final AssocEventType CREATE_TXN_REQ = new AssocEventType(M_CREATE_TXN_REQ);

	public static final AssocEventType CREATE_TXN_RESP = new AssocEventType(M_CREATE_TXN_RESP);

	public static final AssocEventType TXN_ERR_IND = new AssocEventType(M_TXN_ERR_IND);

	private int event_type;

	private AssocEventType(int event_type) {
		this.event_type = event_type;
	}

	public int getAssocEventType() {
		return this.event_type;
	}

	public static final AssocEventType getObject(int value) throws IllegalArgumentException {
		AssocEventType a = null;
		switch (value) {
		case M_CREATE_ASSOC_REQ:
			a = CREATE_ASSOC_REQ;
			break;

		case M_DELETE_ASSOC_REQ:
			a = DELETE_ASSOC_REQ;
			break;

		case M_MODIFY_ASSOC_REQ:
			a = MODIFY_ASSOC_REQ;
			break;

		case M_ASSOC_STATE_IND:
			a = ASSOC_STATE_IND;
			break;

		case M_CREATE_ASSOC_RESP:
			a = CREATE_ASSOC_RESP;
			break;

		case M_DELETE_ASSOC_RESP:
			a = DELETE_ASSOC_RESP;
			break;

		case M_MODIFY_ASSOC_RESP:
			a = MODIFY_ASSOC_RESP;
			break;

		case M_ASSOC_CONFIG_REQ:
			a = ASSOC_CONFIG_REQ;
			break;

		case M_ASSOC_CONFIG_RESP:
			a = ASSOC_CONFIG_RESP;
			break;

		case M_DELETE_TXN_REQ:
			a = DELETE_TXN_REQ;
			break;

		case M_DELETE_TXN_RESP:
			a = DELETE_TXN_RESP;
			break;

		case M_CREATE_TXN_REQ:
			a = CREATE_TXN_REQ;
			break;

		case M_CREATE_TXN_RESP:
			a = CREATE_TXN_RESP;
			break;

		case M_TXN_ERR_IND:
			a = TXN_ERR_IND;
			break;

		default:
			throw new IllegalArgumentException("There is no AsscoEventType for passed value = " + value);

		}
		return a;
	}

	private Object readResolve() {
		return this.getObject(this.event_type);
	}

	@Override
	public String toString() {
		switch (this.event_type) {
		case M_CREATE_ASSOC_REQ:

			return "AssocEventType[CREATE_ASSOC_REQ]";

		case M_DELETE_ASSOC_REQ:

			return "AssocEventType[DELETE_ASSOC_REQ]";

		case M_MODIFY_ASSOC_REQ:

			return "AssocEventType[MODIFY_ASSOC_REQ]";

		case M_ASSOC_STATE_IND:

			return "AssocEventType[ASSOC_STATE_IND]";

		case M_CREATE_ASSOC_RESP:

			return "AssocEventType[CREATE_ASSOC_RESP]";

		case M_DELETE_ASSOC_RESP:

			return "AssocEventType[DELETE_ASSOC_RESP]";

		case M_MODIFY_ASSOC_RESP:

			return "AssocEventType[MODIFY_ASSOC_RESP]";

		case M_ASSOC_CONFIG_REQ:

			return "AssocEventType[ASSOC_CONFIG_REQ]";

		case M_ASSOC_CONFIG_RESP:

			return "AssocEventType[ASSOC_CONFIG_RESP]";

		case M_DELETE_TXN_REQ:

			return "AssocEventType[DELETE_TXN_REQ]";

		case M_DELETE_TXN_RESP:

			return "AssocEventType[DELETE_TXN_RESP]";

		case M_CREATE_TXN_REQ:

			return "AssocEventType[CREATE_TXN_REQ]";

		case M_CREATE_TXN_RESP:

			return "AssocEventType[CREATE_TXN_RESP]";

		case M_TXN_ERR_IND:

			return "AssocEventType[TXN_ERR_IND]";

		default:

			return "AssocEventType[" + this.event_type + "]";

		}
	}

}
