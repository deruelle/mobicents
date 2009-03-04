package javax.megaco.message;

import java.io.Serializable;

public class CmdRequestType implements Serializable {

	public static final int M_ADD_REQ = 1;
	public static final int M_MODIFY_REQ = 2;
	public static final int M_MOVE_REQ = 3;
	public static final int M_SERVICE_CHANGE_REQ = 4;
	public static final int M_NOTIFY_REQ = 5;
	public static final int M_AUDIT_VAL_REQ = 6;
	public static final int M_AUDIT_CAP_REQ = 7;
	public static final int M_SUBTRACT_REQ = 8;

	public static final CmdRequestType ADD_REQ = new CmdRequestType(M_ADD_REQ);
	public static final CmdRequestType MODIFY_REQ = new CmdRequestType(M_MODIFY_REQ);
	public static final CmdRequestType MOVE_REQ = new CmdRequestType(M_MOVE_REQ);
	public static final CmdRequestType SERVICE_CHANGE_REQ = new CmdRequestType(M_SERVICE_CHANGE_REQ);
	public static final CmdRequestType NOTIFY_REQ = new CmdRequestType(M_NOTIFY_REQ);
	public static final CmdRequestType AUDIT_VAL_REQ = new CmdRequestType(M_AUDIT_VAL_REQ);
	public static final CmdRequestType AUDIT_CAP_REQ = new CmdRequestType(M_AUDIT_CAP_REQ);
	public static final CmdRequestType SUBTRACT_REQ = new CmdRequestType(M_SUBTRACT_REQ);

	private int cmd_type;

	private CmdRequestType(int cmd_type) {
		this.cmd_type = cmd_type;

	}

	public int getCmdRequestType() {
		return this.cmd_type;
	}

	public static final CmdRequestType getObject(int value) throws IllegalArgumentException {
		CmdRequestType c = null;
		switch (value) {
		case M_ADD_REQ:
			c = ADD_REQ;
			break;

		case M_MODIFY_REQ:
			c = MODIFY_REQ;
			break;

		case M_MOVE_REQ:
			c = MOVE_REQ;
			break;

		case M_SERVICE_CHANGE_REQ:
			c = SERVICE_CHANGE_REQ;
			break;

		case M_NOTIFY_REQ:
			c = NOTIFY_REQ;
			break;

		case M_AUDIT_VAL_REQ:
			c = AUDIT_VAL_REQ;
			break;

		case M_AUDIT_CAP_REQ:
			c = AUDIT_CAP_REQ;
			break;

		case M_SUBTRACT_REQ:
			c = SUBTRACT_REQ;
			break;

		default:
			IllegalArgumentException illegalArgumentException = new IllegalArgumentException("No CmdRequestType defined for value = " + value);
			throw illegalArgumentException;
		}
		return c;
	}

	private Object readResolve() {
		return this.getObject(this.cmd_type);
	}

	@Override
	public String toString() {
		String c = null;
		switch (this.cmd_type) {
		case M_ADD_REQ:
			c = "CmdType[ADD_REQ]";
			break;

		case M_MODIFY_REQ:
			c = "CmdType[MODIFY_REQ]";
			break;

		case M_MOVE_REQ:
			c = "CmdType[MOVE_REQ]";
			break;

		case M_SERVICE_CHANGE_REQ:
			c = "CmdType[SERVICE_CHANGE_REQ]";
			break;

		case M_NOTIFY_REQ:
			c = "CmdType[NOTIFY_REQ]";
			break;

		case M_AUDIT_VAL_REQ:
			c = "CmdType[AUDIT_VAL_REQ]";
			break;

		case M_AUDIT_CAP_REQ:
			c = "CmdType[AUDIT_CAP_REQ]";
			break;

		case M_SUBTRACT_REQ:
			c = "CmdType[SUBTRACT_REQ]";
			break;

		default:
			c = "CmdType[" + this.cmd_type + "]";
		}
		return c;
	}

}
