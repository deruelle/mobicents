package javax.megaco.message;

import java.io.Serializable;

/**
 * Constants used in package javax.megaco for defining different descriptors
 * that can occur in the commands/actions.
 * 
 * 
 * 
 */
public class DescriptorType implements Serializable {

	public static final int M_EVENT_DESC = 1;
	public static final int M_DIGIT_MAP_DESC = 2;
	public static final int M_SIGNAL_DESC = 3;
	public static final int M_MEDIA_DESC = 4;
	public static final int M_STREAM_DESC = 5;
	public static final int M_LOCAL_CONTROL_DESC = 6;
	public static final int M_TERMINATION_STATE_DESC = 7;
	public static final int M_SERVICE_CHANGE_DESC = 8;
	public static final int M_SERVICE_CHANGE_RESP_DESC = 9;
	public static final int M_AUDIT_CAP_DESC = 10;
	public static final int M_AUDIT_VAL_DESC = 11;

	public static final int M_AUDIT_CAP_REPLY_DESC = 12;
	public static final int M_AUDIT_VAL_REPLY_DESC = 13;
	public static final int M_EVENT_BUF_DESC = 14;
	public static final int M_STATISTICS_DESC = 15;
	public static final int M_PACKAGE_DESC = 16;
	public static final int M_ERROR_DESC = 17;

	public static final int M_MODEM_DESC = 18;
	public static final int M_MUX_DESC = 19;
	public static final int M_TOPOLOGY_DESC = 20;
	public static final int M_CTX_TERM_AUDIT_DESC = 21;
	public static final int M_OBSERVED_EVENT_DESC = 22;

	public static final DescriptorType EVENT_DESC = new DescriptorType(M_EVENT_DESC);
	public static final DescriptorType DIGIT_MAP_DESC = new DescriptorType(M_DIGIT_MAP_DESC);

	public static final DescriptorType SIGNAL_DESC = new DescriptorType(M_SIGNAL_DESC);

	public static final DescriptorType MEDIA_DESC = new DescriptorType(M_MEDIA_DESC);

	public static final DescriptorType STREAM_DESC = new DescriptorType(M_STREAM_DESC);

	public static final DescriptorType LOCAL_CONTROL_DESC = new DescriptorType(M_LOCAL_CONTROL_DESC);

	public static final DescriptorType TERMINATION_STATE_DESC = new DescriptorType(M_TERMINATION_STATE_DESC);

	public static final DescriptorType SERVICE_CHANGE_DESC = new DescriptorType(M_SERVICE_CHANGE_DESC);

	public static final DescriptorType SERVICE_CHANGE_RESP_DESC = new DescriptorType(M_SERVICE_CHANGE_RESP_DESC);

	public static final DescriptorType AUDIT_CAP_DESC = new DescriptorType(M_AUDIT_CAP_DESC);

	public static final DescriptorType AUDIT_VAL_DESC = new DescriptorType(M_AUDIT_VAL_DESC);

	public static final DescriptorType AUDIT_CAP_REPLY_DESC = new DescriptorType(M_AUDIT_CAP_REPLY_DESC);

	public static final DescriptorType AUDIT_VAL_REPLY_DESC = new DescriptorType(M_AUDIT_VAL_REPLY_DESC);

	public static final DescriptorType EVENT_BUF_DESC = new DescriptorType(M_EVENT_BUF_DESC);

	public static final DescriptorType STATISTICS_DESC = new DescriptorType(M_STATISTICS_DESC);

	public static final DescriptorType PACKAGE_DESC = new DescriptorType(M_PACKAGE_DESC);

	public static final DescriptorType ERROR_DESC = new DescriptorType(M_ERROR_DESC);

	public static final DescriptorType MODEM_DESC = new DescriptorType(M_MODEM_DESC);

	public static final DescriptorType MUX_DESC = new DescriptorType(M_MUX_DESC);

	public static final DescriptorType TOPOLOGY_DESC = new DescriptorType(M_TOPOLOGY_DESC);

	public static final DescriptorType CTX_TERM_AUDIT_DESC = new DescriptorType(M_CTX_TERM_AUDIT_DESC);

	public static final DescriptorType OBSERVED_EVENT_DESC = new DescriptorType(M_OBSERVED_EVENT_DESC);

	private int descriptor;

	private DescriptorType(int descriptor) {
		this.descriptor = descriptor;
	}

	/**
	 * This method returns one of the static field constants defined in this
	 * class.
	 * 
	 * @return Returns an integer value that identifies the desc type of the
	 *         descriptor, which could to be one of possible values of constants
	 *         defined by this class.
	 */
	public int getDescType() {
		return this.descriptor;
	}

	/**
	 * Returns reference of the DescriptorType object that identifies the
	 * descriptor type as value passed to this method.
	 * 
	 * @param value
	 *            It is one of the possible values of the static constant that
	 *            this class provides.
	 * @return Returns reference of the DescriptorType object.
	 */
	public static DescriptorType getObject(int value) throws IllegalArgumentException {
		DescriptorType d = null;
		switch (value) {
		case M_EVENT_DESC:
			d = EVENT_DESC;
			break;
		case M_DIGIT_MAP_DESC:
			d = DIGIT_MAP_DESC;
			break;
		case M_SIGNAL_DESC:
			d = SIGNAL_DESC;
			break;
		case M_MEDIA_DESC:
			d = MEDIA_DESC;
			break;
		case M_STREAM_DESC:
			d = STREAM_DESC;
			break;
		case M_LOCAL_CONTROL_DESC:
			d = LOCAL_CONTROL_DESC;
			break;
		case M_TERMINATION_STATE_DESC:
			d = TERMINATION_STATE_DESC;
			break;
		case M_SERVICE_CHANGE_DESC:
			d = SERVICE_CHANGE_DESC;
			break;
		case M_SERVICE_CHANGE_RESP_DESC:
			d = SERVICE_CHANGE_RESP_DESC;
			break;
		case M_AUDIT_CAP_DESC:
			d = AUDIT_CAP_DESC;
			break;
		case M_AUDIT_VAL_DESC:
			d = AUDIT_VAL_DESC;
			break;
		case M_AUDIT_CAP_REPLY_DESC:
			d = AUDIT_CAP_REPLY_DESC;
			break;
		case M_AUDIT_VAL_REPLY_DESC:
			d = AUDIT_VAL_REPLY_DESC;
			break;
		case M_EVENT_BUF_DESC:
			d = EVENT_BUF_DESC;
			break;
		case M_STATISTICS_DESC:
			d = STATISTICS_DESC;
			break;
		case M_PACKAGE_DESC:
			d = PACKAGE_DESC;
			break;
		case M_ERROR_DESC:
			d = ERROR_DESC;
			break;
		case M_MODEM_DESC:
			d = MODEM_DESC;
			break;
		case M_MUX_DESC:
			d = MUX_DESC;
			break;
		case M_TOPOLOGY_DESC:
			d = TOPOLOGY_DESC;
			break;
		case M_CTX_TERM_AUDIT_DESC:
			d = CTX_TERM_AUDIT_DESC;
			break;
		case M_OBSERVED_EVENT_DESC:
			d = OBSERVED_EVENT_DESC;
			break;
		default:
			throw new IllegalArgumentException("There is no DescriptorType defined for value " + value);
		}
		return d;
	}

	private Object readResolve() {
		return this.getObject(this.descriptor);
	}

	public String toString() {

		String d = "";
		switch (this.descriptor) {
		case M_EVENT_DESC:
			d = "DescriptorType[EVENT_DESC]";
			break;
		case M_DIGIT_MAP_DESC:
			d = "DescriptorType[DIGIT_MAP_DESC]";
			break;
		case M_SIGNAL_DESC:
			d = "DescriptorType[SIGNAL_DESC]";
			break;
		case M_MEDIA_DESC:
			d = "DescriptorType[MEDIA_DESC]";
			break;
		case M_STREAM_DESC:
			d = "DescriptorType[STREAM_DESC]";
			break;
		case M_LOCAL_CONTROL_DESC:
			d = "DescriptorType[LOCAL_CONTROL_DESC]";
			break;
		case M_TERMINATION_STATE_DESC:
			d = "DescriptorType[TERMINATION_STATE_DESC]";
			break;
		case M_SERVICE_CHANGE_DESC:
			d = "DescriptorType[SERVICE_CHANGE_DESC]";
			break;
		case M_SERVICE_CHANGE_RESP_DESC:
			d = "DescriptorType[SERVICE_CHANGE_RESP_DESC]";
			break;
		case M_AUDIT_CAP_DESC:
			d = "DescriptorType[AUDIT_CAP_DESC]";
			break;
		case M_AUDIT_VAL_DESC:
			d = "DescriptorType[AUDIT_VAL_DESC]";
			break;
		case M_AUDIT_CAP_REPLY_DESC:
			d = "DescriptorType[AUDIT_CAP_REPLY_DESC]";
			break;
		case M_AUDIT_VAL_REPLY_DESC:
			d = "DescriptorType[AUDIT_VAL_REPLY_DESC]";
			break;
		case M_EVENT_BUF_DESC:
			d = "DescriptorType[EVENT_BUF_DESC]";
			break;
		case M_STATISTICS_DESC:
			d = "DescriptorType[STATISTICS_DESC]";
			break;
		case M_PACKAGE_DESC:
			d = "DescriptorType[PACKAGE_DESC]";
			break;
		case M_ERROR_DESC:
			d = "DescriptorType[ERROR_DESC]";
			break;
		case M_MODEM_DESC:
			d = "DescriptorType[MODEM_DESC]";
			break;
		case M_MUX_DESC:
			d = "DescriptorType[MUX_DESC]";
			break;
		case M_TOPOLOGY_DESC:
			d = "DescriptorType[TOPOLOGY_DESC]";
			break;
		case M_CTX_TERM_AUDIT_DESC:
			d = "DescriptorType[CTX_TERM_AUDIT_DESC]";
			break;
		case M_OBSERVED_EVENT_DESC:
			d = "DescriptorType[OBSERVED_EVENT_DESC]";
			break;
		default:

			d = "DescriptorType[" + this.descriptor + "]";
		}

		return d;
	}

}
