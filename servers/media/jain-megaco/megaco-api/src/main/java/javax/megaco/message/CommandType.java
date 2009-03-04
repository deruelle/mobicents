package javax.megaco.message;

import java.io.Serializable;

/**
 * Constants used in package javax.megaco.message for defining the type of the
 * command, i.e whether a command request or a command response.
 * 
 * 
 */
public class CommandType implements Serializable {
	public static final int M_COMMAND_REQ = 1;
	public static final int M_COMMAND_RESP = 2;
	public static final int M_ACTION_REQ = 3;
	public static final int M_ACTION_RESP = 4;

	public static final CommandType COMMAND_REQ = new CommandType(M_COMMAND_REQ);

	public static final CommandType COMMAND_RESP = new CommandType(M_COMMAND_RESP);

	public static final CommandType ACTION_REQ = new CommandType(M_ACTION_REQ);

	public static final CommandType ACTION_RESP = new CommandType(M_ACTION_RESP);

	private int cmd_type;

	private CommandType(int cmd_type) {
		this.cmd_type = cmd_type;

	}

	public int getCommandType() {
		return this.cmd_type;
	}

	public static final CommandType getObject(int value) throws IllegalArgumentException {
		CommandType c = null;
		switch (value) {
		case M_COMMAND_REQ:
			c = COMMAND_REQ;
			break;

		case M_COMMAND_RESP:
			c = COMMAND_RESP;
			break;

		case M_ACTION_REQ:
			c = ACTION_REQ;
			break;

		case M_ACTION_RESP:
			c = ACTION_RESP;
			break;

		default:
			IllegalArgumentException illegalArgumentException = new IllegalArgumentException("No CommandType for value  = " + value);
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
		case M_COMMAND_REQ:
			c = "CommandType[COMMAND_REQ]";
			break;

		case M_COMMAND_RESP:
			c = "CommandType[COMMAND_RESP]";
			break;

		case M_ACTION_REQ:
			c = "CommandType[ACTION_REQ]";
			break;

		case M_ACTION_RESP:
			c = "CommandType[ACTION_RESP]";
			break;

		default:
			c = "CommandType[" + this.cmd_type + "]";
		}
		return c;
	}

}
