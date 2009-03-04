package javax.megaco;

/**
 * The ExceptionInfoCode class defines the constants for the associated info
 * code whenever a exception is raised. The info code would assist the
 * application in debugging the cause of exception, whenever it is raised. Most
 * of the info codes specified in this class are for the
 * IllegalArgumentException raised when a invalid argument is passed to a
 * method.
 * 
 */
public class ExceptionInfoCode {
	public static final int M_INV_IP_ADDR_FORMAT = 1;
	public static final int M_INV_MTP3B_ADDR_FORMAT = 2;
	public static final int M_INV_AAL5_ADDR_FORMAT = 3;
	public static final int M_INV_LOCAL_ADDR = 4;
	public static final int M_INV_REMOTE_ADDR = 5;
	public static final int M_INV_ERROR_CODE = 6;
	public static final int M_INV_EVT_STATUS_VAL = 7;
	public static final int M_INV_REQ_ID = 8;
	public static final int M_MISSING_EVT_PARAM = 9;
	public static final int M_SYNTAX_ERR_IN_SVC_CHNG_PROFILE = 10;
	public static final int M_MISSING_PKG_NAME = 11;
	public static final int M_MISSING_ITEM_NAME = 12;
	public static final int M_MISSING_PKG_PARAM = 13;
	public static final int M_MISSING_INFO_IN_OBJ = 14;
	public static final int M_BOTH_KA_EMBEDSIG_PRESENT = 15;
	public static final int M_INV_STREAM_ID = 16;
	public static final int M_SYNTAX_ERR_IN_DMAP_NAME = 17;
	public static final int M_INV_TIMER_VAL = 18;
	public static final int M_INV_DIGIT_VAL = 19;
	public static final int M_NULL_OBJ_REF_PASSED = 20;
	public static final int M_INV_SIGNAL_LIST_ID = 21;
	public static final int M_MISSING_SIGNAL_LIST_ID = 22;
	public static final int M_INV_OPR_ON_SIGNAL_PARAM = 23;
	public static final int M_INV_SIGNAL_DURATION = 24;
	public static final int M_BOTH_STREAM_PARAM_DESC_PRESENT = 25;
	public static final int M_INV_SDP_PARAM = 26;
	public static final int M_MISSING_MEDIA_STREAM_PARAM = 27;
	public static final int M_MISSING_STREAM_ID = 28;
	public static final int M_MISSING_EXTENSION_PARAM = 29;
	public static final int M_SYNTAX_ERR_IN_TERM_NAME = 30;
	public static final int M_INV_PROTOCOL_VERSION = 31;
	public static final int M_INV_DATE_STRING = 32;
	public static final int M_INV_TIME_STRING = 33;
	public static final int M_INV_MEDIA_DESC = 34;
	public static final int M_INV_DIGITMAP_DESC = 35;
	public static final int M_INV_PKG_DESC = 36;
	public static final int M_INV_CNTX_TERM_AUDIT_DESC = 37;
	public static final int M_ERROR_DESC_ALRDY_PRESENT = 38;
	public static final int M_TERMID_LIST_ALRDY_PRESENT = 39;
	public static final int M_INV_YEAR_VAL = 40;
	public static final int M_INV_MONTH_VAL = 41;
	public static final int M_INV_DAY_VAL = 42;
	public static final int M_INV_HOUR_VAL = 43;
	public static final int M_INV_MINUTES_VAL = 44;
	public static final int M_INV_SECONDS_VAL = 45;
	public static final int M_INV_ASSOCIATED_PKD_ID = 46;
	public static final int M_INV_PKG_ITEM_PARAM = 47;
	public static final int M_INV_RETURN_STATUS_VALUE = 48;
	public static final int M_INV_ASSOC_EVENT_TYPE = 49;
	public static final int M_INV_ASSOC_IND_REASON = 50;
	public static final int M_INV_ASSOC_STATE = 51;
	public static final int M_INV_ENC_FORMAT = 52;
	public static final int M_INV_SVC_CHNG_METHOD = 53;
	public static final int M_INV_SVC_CHNG_REASON = 54;
	public static final int M_INV_TPT_TYPE = 55;
	public static final int M_INV_CMD_REQ_TYPE = 56;
	public static final int M_INV_CMD_RESP_TYPE = 57;
	public static final int M_INV_CMD_TYPE = 58;
	public static final int M_INV_DESC_TYPE = 59;
	public static final int M_INV_TERM_TYPE = 60;
	public static final int M_INV_EVT_BUFFER_CTRL = 61;
	public static final int M_INV_MODEM_TYPE = 62;
	public static final int M_INV_MUX_TYPE = 63;
	public static final int M_INV_SERVICE_STATE = 64;
	public static final int M_INV_SIGNAL_PARAM_TYPE = 65;
	public static final int M_INV_SIGNAL_TYPE = 66;
	public static final int M_INV_STREAM_MODE = 67;
	public static final int M_INV_TOPOLOGY_DIR = 68;
	public static final int M_INV_PARAM_VAL_TYPE = 69;
	public static final int M_INV_PARAM_RELATION = 70;
	public static final int M_INV_PKG_ITEM_TYPE = 71;
	public static final int M_INV_PRIORITY_VAL = 72;
	public static final int M_DUPLICATE_DESC_IN_CMD = 73;
	public static final int M_MISSING_DIGIT_WITH_QUALIFIER_DOT = 74;

	public static final ExceptionInfoCode INV_IP_ADDR_FORMAT = new ExceptionInfoCode(M_INV_IP_ADDR_FORMAT);

	public static final ExceptionInfoCode INV_MTP3B_ADDR_FORMAT = new ExceptionInfoCode(M_INV_MTP3B_ADDR_FORMAT);

	public static final ExceptionInfoCode INV_AAL5_ADDR_FORMAT = new ExceptionInfoCode(M_INV_AAL5_ADDR_FORMAT);

	public static final ExceptionInfoCode INV_LOCAL_ADDR = new ExceptionInfoCode(M_INV_LOCAL_ADDR);

	public static final ExceptionInfoCode INV_REMOTE_ADDR = new ExceptionInfoCode(M_INV_REMOTE_ADDR);

	public static final ExceptionInfoCode INV_ERROR_CODE = new ExceptionInfoCode(M_INV_ERROR_CODE);

	public static final ExceptionInfoCode INV_EVT_STATUS_VAL = new ExceptionInfoCode(M_INV_EVT_STATUS_VAL);

	public static final ExceptionInfoCode INV_REQ_ID = new ExceptionInfoCode(M_INV_REQ_ID);

	public static final ExceptionInfoCode MISSING_EVT_PARAM = new ExceptionInfoCode(M_MISSING_EVT_PARAM);

	public static final ExceptionInfoCode MISSING_PKG_NAME = new ExceptionInfoCode(M_MISSING_PKG_NAME);

	public static final ExceptionInfoCode MISSING_ITEM_NAME = new ExceptionInfoCode(M_MISSING_ITEM_NAME);

	public static final ExceptionInfoCode MISSING_PKG_PARAM = new ExceptionInfoCode(M_MISSING_PKG_PARAM);

	public static final ExceptionInfoCode MISSING_INFO_IN_OBJ = new ExceptionInfoCode(M_MISSING_INFO_IN_OBJ);

	public static final ExceptionInfoCode BOTH_KA_EMBEDSIG_PRESENT = new ExceptionInfoCode(M_BOTH_KA_EMBEDSIG_PRESENT);

	public static final ExceptionInfoCode INV_STREAM_ID = new ExceptionInfoCode(M_INV_STREAM_ID);

	public static final ExceptionInfoCode INV_TIMER_VAL = new ExceptionInfoCode(M_INV_TIMER_VAL);

	public static final ExceptionInfoCode INV_DIGIT_VAL = new ExceptionInfoCode(M_INV_DIGIT_VAL);

	public static final ExceptionInfoCode NULL_OBJ_REF_PASSED = new ExceptionInfoCode(M_NULL_OBJ_REF_PASSED);

	public static final ExceptionInfoCode INV_SIGNAL_LIST_ID = new ExceptionInfoCode(M_INV_SIGNAL_LIST_ID);

	public static final ExceptionInfoCode MISSING_SIGNAL_LIST_ID = new ExceptionInfoCode(M_MISSING_SIGNAL_LIST_ID);

	public static final ExceptionInfoCode INV_OPR_ON_SIGNAL_PARAM = new ExceptionInfoCode(M_INV_OPR_ON_SIGNAL_PARAM);

	public static final ExceptionInfoCode INV_SIGNAL_DURATION = new ExceptionInfoCode(M_INV_SIGNAL_DURATION);

	public static final ExceptionInfoCode BOTH_STREAM_PARAM_DESC_PRESENT = new ExceptionInfoCode(M_BOTH_STREAM_PARAM_DESC_PRESENT);

	public static final ExceptionInfoCode INV_SDP_PARAM = new ExceptionInfoCode(M_INV_SDP_PARAM);

	public static final ExceptionInfoCode MISSING_MEDIA_STREAM_PARAM = new ExceptionInfoCode(M_MISSING_MEDIA_STREAM_PARAM);

	public static final ExceptionInfoCode MISSING_STREAM_ID = new ExceptionInfoCode(M_MISSING_STREAM_ID);

	public static final ExceptionInfoCode MISSING_EXTENSION_PARAM = new ExceptionInfoCode(M_MISSING_EXTENSION_PARAM);

	public static final ExceptionInfoCode INV_PROTOCOL_VERSION = new ExceptionInfoCode(M_INV_PROTOCOL_VERSION);

	public static final ExceptionInfoCode INV_DATE_STRING = new ExceptionInfoCode(M_INV_DATE_STRING);

	public static final ExceptionInfoCode INV_TIME_STRING = new ExceptionInfoCode(M_INV_TIME_STRING);

	public static final ExceptionInfoCode INV_MEDIA_DESC = new ExceptionInfoCode(M_INV_MEDIA_DESC);

	public static final ExceptionInfoCode INV_DIGITMAP_DESC = new ExceptionInfoCode(M_INV_DIGITMAP_DESC);

	public static final ExceptionInfoCode INV_PKG_DESC = new ExceptionInfoCode(M_INV_PKG_DESC);

	public static final ExceptionInfoCode INV_CNTX_TERM_AUDIT_DESC = new ExceptionInfoCode(M_INV_CNTX_TERM_AUDIT_DESC);

	public static final ExceptionInfoCode ERROR_DESC_ALRDY_PRESENT = new ExceptionInfoCode(M_ERROR_DESC_ALRDY_PRESENT);

	public static final ExceptionInfoCode TERMID_LIST_ALRDY_PRESENT = new ExceptionInfoCode(M_TERMID_LIST_ALRDY_PRESENT);

	public static final ExceptionInfoCode INV_YEAR_VAL = new ExceptionInfoCode(M_INV_YEAR_VAL);

	public static final ExceptionInfoCode INV_MONTH_VAL = new ExceptionInfoCode(M_INV_MONTH_VAL);

	public static final ExceptionInfoCode INV_DAY_VAL = new ExceptionInfoCode(M_INV_DAY_VAL);

	public static final ExceptionInfoCode INV_HOUR_VAL = new ExceptionInfoCode(M_INV_HOUR_VAL);

	public static final ExceptionInfoCode INV_MINUTES_VAL = new ExceptionInfoCode(M_INV_MINUTES_VAL);

	public static final ExceptionInfoCode INV_SECONDS_VAL = new ExceptionInfoCode(M_INV_SECONDS_VAL);

	public static final ExceptionInfoCode INV_ASSOCIATED_PKD_ID = new ExceptionInfoCode(M_INV_ASSOCIATED_PKD_ID);

	public static final ExceptionInfoCode INV_PKG_ITEM_PARAM = new ExceptionInfoCode(M_INV_PKG_ITEM_PARAM);

	public static final ExceptionInfoCode INV_RETURN_STATUS_VALUE = new ExceptionInfoCode(M_INV_RETURN_STATUS_VALUE);

	public static final ExceptionInfoCode INV_ASSOC_EVENT_TYPE = new ExceptionInfoCode(M_INV_ASSOC_EVENT_TYPE);

	public static final ExceptionInfoCode INV_ASSOC_IND_REASON = new ExceptionInfoCode(M_INV_ASSOC_IND_REASON);

	public static final ExceptionInfoCode INV_ASSOC_STATE = new ExceptionInfoCode(M_INV_ASSOC_STATE);

	public static final ExceptionInfoCode INV_ENC_FORMAT = new ExceptionInfoCode(M_INV_ENC_FORMAT);

	public static final ExceptionInfoCode INV_SVC_CHNG_METHOD = new ExceptionInfoCode(M_INV_SVC_CHNG_METHOD);

	public static final ExceptionInfoCode INV_SVC_CHNG_REASON = new ExceptionInfoCode(M_INV_SVC_CHNG_REASON);

	public static final ExceptionInfoCode INV_TPT_TYPE = new ExceptionInfoCode(M_INV_TPT_TYPE);

	public static final ExceptionInfoCode INV_CMD_REQ_TYPE = new ExceptionInfoCode(M_INV_CMD_REQ_TYPE);

	public static final ExceptionInfoCode INV_CMD_RESP_TYPE = new ExceptionInfoCode(M_INV_CMD_RESP_TYPE);

	public static final ExceptionInfoCode INV_CMD_TYPE = new ExceptionInfoCode(M_INV_CMD_TYPE);

	public static final ExceptionInfoCode INV_DESC_TYPE = new ExceptionInfoCode(M_INV_DESC_TYPE);

	public static final ExceptionInfoCode INV_TERM_TYPE = new ExceptionInfoCode(M_INV_TERM_TYPE);

	public static final ExceptionInfoCode INV_EVT_BUFFER_CTRL = new ExceptionInfoCode(M_INV_EVT_BUFFER_CTRL);

	public static final ExceptionInfoCode INV_MODEM_TYPE = new ExceptionInfoCode(M_INV_MODEM_TYPE);

	public static final ExceptionInfoCode INV_MUX_TYPE = new ExceptionInfoCode(M_INV_MUX_TYPE);

	public static final ExceptionInfoCode INV_SERVICE_STATE = new ExceptionInfoCode(M_INV_SERVICE_STATE);

	public static final ExceptionInfoCode INV_SIGNAL_PARAM_TYPE = new ExceptionInfoCode(M_INV_SIGNAL_PARAM_TYPE);

	public static final ExceptionInfoCode INV_SIGNAL_TYPE = new ExceptionInfoCode(M_INV_SIGNAL_TYPE);

	public static final ExceptionInfoCode INV_STREAM_MODE = new ExceptionInfoCode(M_INV_STREAM_MODE);

	public static final ExceptionInfoCode INV_TOPOLOGY_DIR = new ExceptionInfoCode(M_INV_TOPOLOGY_DIR);

	public static final ExceptionInfoCode INV_PARAM_VAL_TYPE = new ExceptionInfoCode(M_INV_PARAM_VAL_TYPE);

	public static final ExceptionInfoCode INV_PARAM_RELATION = new ExceptionInfoCode(M_INV_PARAM_RELATION);

	public static final ExceptionInfoCode INV_PKG_ITEM_TYPE = new ExceptionInfoCode(M_INV_PKG_ITEM_TYPE);

	public static final ExceptionInfoCode INV_PRIORITY_VAL = new ExceptionInfoCode(M_INV_PRIORITY_VAL);

	public static final ExceptionInfoCode DUPLICATE_DESC_IN_CMD = new ExceptionInfoCode(M_DUPLICATE_DESC_IN_CMD);

	public static final ExceptionInfoCode MISSING_DIGIT_WITH_QUALIFIER_DOT = new ExceptionInfoCode(M_MISSING_DIGIT_WITH_QUALIFIER_DOT);

	public static final ExceptionInfoCode SYNTAX_ERR_IN_TERM_NAME = new ExceptionInfoCode(M_SYNTAX_ERR_IN_TERM_NAME);

	public static final ExceptionInfoCode SYNTAX_ERR_IN_DMAP_NAME = new ExceptionInfoCode(M_SYNTAX_ERR_IN_DMAP_NAME);

	public static final ExceptionInfoCode SYNTAX_ERR_IN_SVC_CHNG_PROFILE = new ExceptionInfoCode(M_SYNTAX_ERR_IN_SVC_CHNG_PROFILE);

	private int info_code;

	private ExceptionInfoCode(int info_code) {
		this.info_code = info_code;
	}

	public int getExceptionInfoCode() {
		return this.info_code;
	}

	public static final ExceptionInfoCode getObject(int value) throws IllegalArgumentException {
		ExceptionInfoCode e = null;
		switch (value) {
		case M_INV_IP_ADDR_FORMAT:
			e = INV_IP_ADDR_FORMAT;
			break;

		case M_INV_MTP3B_ADDR_FORMAT:
			e = INV_MTP3B_ADDR_FORMAT;
			break;

		case M_INV_AAL5_ADDR_FORMAT:
			e = INV_AAL5_ADDR_FORMAT;
			break;

		case M_INV_LOCAL_ADDR:
			e = INV_LOCAL_ADDR;
			break;

		case M_INV_REMOTE_ADDR:
			e = INV_REMOTE_ADDR;
			break;

		case M_INV_ERROR_CODE:
			e = INV_ERROR_CODE;
			break;

		case M_INV_EVT_STATUS_VAL:
			e = INV_EVT_STATUS_VAL;
			break;

		case M_INV_REQ_ID:
			e = INV_REQ_ID;
			break;

		case M_MISSING_EVT_PARAM:
			e = MISSING_EVT_PARAM;
			break;

		case M_SYNTAX_ERR_IN_SVC_CHNG_PROFILE:
			e = SYNTAX_ERR_IN_SVC_CHNG_PROFILE;
			break;

		case M_MISSING_PKG_NAME:
			e = MISSING_PKG_NAME;
			break;

		case M_MISSING_ITEM_NAME:
			e = MISSING_ITEM_NAME;
			break;

		case M_MISSING_PKG_PARAM:
			e = MISSING_PKG_PARAM;
			break;

		case M_MISSING_INFO_IN_OBJ:
			e = MISSING_INFO_IN_OBJ;
			break;

		case M_BOTH_KA_EMBEDSIG_PRESENT:
			e = BOTH_KA_EMBEDSIG_PRESENT;
			break;

		case M_INV_STREAM_ID:
			e = INV_STREAM_ID;
			break;

		case M_SYNTAX_ERR_IN_DMAP_NAME:
			e = SYNTAX_ERR_IN_DMAP_NAME;
			break;

		case M_INV_TIMER_VAL:
			e = INV_TIMER_VAL;
			break;

		case M_INV_DIGIT_VAL:
			e = INV_DIGIT_VAL;
			break;

		case M_NULL_OBJ_REF_PASSED:
			e = NULL_OBJ_REF_PASSED;
			break;

		case M_INV_SIGNAL_LIST_ID:
			e = INV_SIGNAL_LIST_ID;
			break;

		case M_MISSING_SIGNAL_LIST_ID:
			e = MISSING_SIGNAL_LIST_ID;
			break;

		case M_INV_OPR_ON_SIGNAL_PARAM:
			e = INV_OPR_ON_SIGNAL_PARAM;
			break;

		case M_INV_SIGNAL_DURATION:
			e = INV_SIGNAL_DURATION;
			break;

		case M_BOTH_STREAM_PARAM_DESC_PRESENT:
			e = BOTH_STREAM_PARAM_DESC_PRESENT;
			break;

		case M_INV_SDP_PARAM:
			e = INV_SDP_PARAM;
			break;

		case M_MISSING_MEDIA_STREAM_PARAM:
			e = MISSING_MEDIA_STREAM_PARAM;
			break;

		case M_MISSING_STREAM_ID:
			e = MISSING_STREAM_ID;
			break;

		case M_MISSING_EXTENSION_PARAM:
			e = MISSING_EXTENSION_PARAM;
			break;

		case M_SYNTAX_ERR_IN_TERM_NAME:
			e = SYNTAX_ERR_IN_TERM_NAME;
			break;

		case M_INV_PROTOCOL_VERSION:
			e = INV_PROTOCOL_VERSION;
			break;

		case M_INV_DATE_STRING:
			e = INV_DATE_STRING;
			break;

		case M_INV_TIME_STRING:
			e = INV_TIME_STRING;
			break;

		case M_INV_MEDIA_DESC:
			e = INV_MEDIA_DESC;
			break;

		case M_INV_DIGITMAP_DESC:
			e = INV_DIGITMAP_DESC;
			break;

		case M_INV_PKG_DESC:
			e = INV_PKG_DESC;
			break;

		case M_INV_CNTX_TERM_AUDIT_DESC:
			e = INV_CNTX_TERM_AUDIT_DESC;
			break;

		case M_ERROR_DESC_ALRDY_PRESENT:
			e = ERROR_DESC_ALRDY_PRESENT;
			break;

		case M_TERMID_LIST_ALRDY_PRESENT:
			e = TERMID_LIST_ALRDY_PRESENT;
			break;

		case M_INV_YEAR_VAL:
			e = INV_YEAR_VAL;
			break;

		case M_INV_MONTH_VAL:
			e = INV_MONTH_VAL;
			break;

		case M_INV_DAY_VAL:
			e = INV_DAY_VAL;
			break;

		case M_INV_HOUR_VAL:
			e = INV_HOUR_VAL;
			break;

		case M_INV_MINUTES_VAL:
			e = INV_MINUTES_VAL;
			break;

		case M_INV_SECONDS_VAL:
			e = INV_SECONDS_VAL;
			break;

		case M_INV_ASSOCIATED_PKD_ID:
			e = INV_ASSOCIATED_PKD_ID;
			break;

		case M_INV_PKG_ITEM_PARAM:
			e = INV_PKG_ITEM_PARAM;
			break;

		case M_INV_RETURN_STATUS_VALUE:
			e = INV_RETURN_STATUS_VALUE;
			break;

		case M_INV_ASSOC_EVENT_TYPE:
			e = INV_ASSOC_EVENT_TYPE;
			break;

		case M_INV_ASSOC_IND_REASON:
			e = INV_ASSOC_IND_REASON;
			break;

		case M_INV_ASSOC_STATE:
			e = INV_ASSOC_STATE;
			break;

		case M_INV_ENC_FORMAT:
			e = INV_ENC_FORMAT;
			break;

		case M_INV_SVC_CHNG_METHOD:
			e = INV_SVC_CHNG_METHOD;
			break;

		case M_INV_SVC_CHNG_REASON:
			e = INV_SVC_CHNG_REASON;
			break;

		case M_INV_TPT_TYPE:
			e = INV_TPT_TYPE;
			break;

		case M_INV_CMD_REQ_TYPE:
			e = INV_CMD_REQ_TYPE;
			break;

		case M_INV_CMD_RESP_TYPE:
			e = INV_CMD_RESP_TYPE;
			break;

		case M_INV_CMD_TYPE:
			e = INV_CMD_TYPE;
			break;

		case M_INV_DESC_TYPE:
			e = INV_DESC_TYPE;
			break;

		case M_INV_TERM_TYPE:
			e = INV_TERM_TYPE;
			break;

		case M_INV_EVT_BUFFER_CTRL:
			e = INV_EVT_BUFFER_CTRL;
			break;

		case M_INV_MODEM_TYPE:
			e = INV_MODEM_TYPE;
			break;

		case M_INV_MUX_TYPE:
			e = INV_MUX_TYPE;
			break;

		case M_INV_SERVICE_STATE:
			e = INV_SERVICE_STATE;
			break;

		case M_INV_SIGNAL_PARAM_TYPE:
			e = INV_SIGNAL_PARAM_TYPE;
			break;

		case M_INV_SIGNAL_TYPE:
			e = INV_SIGNAL_TYPE;
			break;

		case M_INV_STREAM_MODE:
			e = INV_STREAM_MODE;
			break;

		case M_INV_TOPOLOGY_DIR:
			e = INV_TOPOLOGY_DIR;
			break;

		case M_INV_PARAM_VAL_TYPE:
			e = INV_PARAM_VAL_TYPE;
			break;

		case M_INV_PARAM_RELATION:
			e = INV_PARAM_RELATION;
			break;

		case M_INV_PKG_ITEM_TYPE:
			e = INV_PKG_ITEM_TYPE;
			break;

		case M_INV_PRIORITY_VAL:
			e = INV_PRIORITY_VAL;
			break;

		case M_DUPLICATE_DESC_IN_CMD:
			e = DUPLICATE_DESC_IN_CMD;
			break;

		case M_MISSING_DIGIT_WITH_QUALIFIER_DOT:
			e = MISSING_DIGIT_WITH_QUALIFIER_DOT;
			break;

		default:
			throw new IllegalArgumentException("There is no ExceptionInfoCode for value " + value);
		}

		return e;
	}

	private Object readResolve() {
		return this.getObject(this.info_code);
	}

	@Override
	public String toString() {
		String e = null;
		switch (this.info_code) {
		case M_INV_IP_ADDR_FORMAT:
			e = "ExceptionInfoCode[INV_IP_ADDR_FORMAT]";
			break;

		case M_INV_MTP3B_ADDR_FORMAT:
			e = "ExceptionInfoCode[INV_MTP3B_ADDR_FORMAT]";
			break;

		case M_INV_AAL5_ADDR_FORMAT:
			e = "ExceptionInfoCode[INV_AAL5_ADDR_FORMAT]";
			break;

		case M_INV_LOCAL_ADDR:
			e = "ExceptionInfoCode[INV_LOCAL_ADDR]";
			break;

		case M_INV_REMOTE_ADDR:
			e = "ExceptionInfoCode[INV_REMOTE_ADDR]";
			break;

		case M_INV_ERROR_CODE:
			e = "ExceptionInfoCode[INV_ERROR_CODE]";
			break;

		case M_INV_EVT_STATUS_VAL:
			e = "ExceptionInfoCode[INV_EVT_STATUS_VAL]";
			break;

		case M_INV_REQ_ID:
			e = "ExceptionInfoCode[INV_REQ_ID]";
			break;

		case M_MISSING_EVT_PARAM:
			e = "ExceptionInfoCode[MISSING_EVT_PARAM]";
			break;

		case M_SYNTAX_ERR_IN_SVC_CHNG_PROFILE:
			e = "ExceptionInfoCode[SYNTAX_ERR_IN_SVC_CHNG_PROFILE]";
			break;

		case M_MISSING_PKG_NAME:
			e = "ExceptionInfoCode[MISSING_PKG_NAME]";
			break;

		case M_MISSING_ITEM_NAME:
			e = "ExceptionInfoCode[MISSING_ITEM_NAME]";
			break;

		case M_MISSING_PKG_PARAM:
			e = "ExceptionInfoCode[MISSING_PKG_PARAM]";
			break;

		case M_MISSING_INFO_IN_OBJ:
			e = "ExceptionInfoCode[MISSING_INFO_IN_OBJ]";
			break;

		case M_BOTH_KA_EMBEDSIG_PRESENT:
			e = "ExceptionInfoCode[BOTH_KA_EMBEDSIG_PRESENT]";
			break;

		case M_INV_STREAM_ID:
			e = "ExceptionInfoCode[INV_STREAM_ID]";
			break;

		case M_SYNTAX_ERR_IN_DMAP_NAME:
			e = "ExceptionInfoCode[SYNTAX_ERR_IN_DMAP_NAME]";
			break;

		case M_INV_TIMER_VAL:
			e = "ExceptionInfoCode[INV_TIMER_VAL]";
			break;

		case M_INV_DIGIT_VAL:
			e = "ExceptionInfoCode[INV_DIGIT_VAL]";
			break;

		case M_NULL_OBJ_REF_PASSED:
			e = "ExceptionInfoCode[NULL_OBJ_REF_PASSED]";
			break;

		case M_INV_SIGNAL_LIST_ID:
			e = "ExceptionInfoCode[INV_SIGNAL_LIST_ID]";
			break;

		case M_MISSING_SIGNAL_LIST_ID:
			e = "ExceptionInfoCode[MISSING_SIGNAL_LIST_ID]";
			break;

		case M_INV_OPR_ON_SIGNAL_PARAM:
			e = "ExceptionInfoCode[INV_OPR_ON_SIGNAL_PARAM]";
			break;

		case M_INV_SIGNAL_DURATION:
			e = "ExceptionInfoCode[INV_SIGNAL_DURATION]";
			break;

		case M_BOTH_STREAM_PARAM_DESC_PRESENT:
			e = "ExceptionInfoCode[BOTH_STREAM_PARAM_DESC_PRESENT]";
			break;

		case M_INV_SDP_PARAM:
			e = "ExceptionInfoCode[INV_SDP_PARAM]";
			break;

		case M_MISSING_MEDIA_STREAM_PARAM:
			e = "ExceptionInfoCode[MISSING_MEDIA_STREAM_PARAM]";
			break;

		case M_MISSING_STREAM_ID:
			e = "ExceptionInfoCode[MISSING_STREAM_ID]";
			break;

		case M_MISSING_EXTENSION_PARAM:
			e = "ExceptionInfoCode[MISSING_EXTENSION_PARAM]";
			break;

		case M_SYNTAX_ERR_IN_TERM_NAME:
			e = "ExceptionInfoCode[SYNTAX_ERR_IN_TERM_NAME]";
			break;

		case M_INV_PROTOCOL_VERSION:
			e = "ExceptionInfoCode[INV_PROTOCOL_VERSION]";
			break;

		case M_INV_DATE_STRING:
			e = "ExceptionInfoCode[INV_DATE_STRING]";
			break;

		case M_INV_TIME_STRING:
			e = "ExceptionInfoCode[INV_TIME_STRING]";
			break;

		case M_INV_MEDIA_DESC:
			e = "ExceptionInfoCode[INV_MEDIA_DESC]";
			break;

		case M_INV_DIGITMAP_DESC:
			e = "ExceptionInfoCode[INV_DIGITMAP_DESC]";
			break;

		case M_INV_PKG_DESC:
			e = "ExceptionInfoCode[INV_PKG_DESC]";
			break;

		case M_INV_CNTX_TERM_AUDIT_DESC:
			e = "ExceptionInfoCode[INV_CNTX_TERM_AUDIT_DESC]";
			break;

		case M_ERROR_DESC_ALRDY_PRESENT:
			e = "ExceptionInfoCode[ERROR_DESC_ALRDY_PRESENT]";
			break;

		case M_TERMID_LIST_ALRDY_PRESENT:
			e = "ExceptionInfoCode[TERMID_LIST_ALRDY_PRESENT]";
			break;

		case M_INV_YEAR_VAL:
			e = "ExceptionInfoCode[INV_YEAR_VAL]";
			break;

		case M_INV_MONTH_VAL:
			e = "ExceptionInfoCode[INV_MONTH_VAL]";
			break;

		case M_INV_DAY_VAL:
			e = "ExceptionInfoCode[INV_DAY_VAL]";
			break;

		case M_INV_HOUR_VAL:
			e = "ExceptionInfoCode[INV_HOUR_VAL]";
			break;

		case M_INV_MINUTES_VAL:
			e = "ExceptionInfoCode[INV_MINUTES_VAL]";
			break;

		case M_INV_SECONDS_VAL:
			e = "ExceptionInfoCode[INV_SECONDS_VAL]";
			break;

		case M_INV_ASSOCIATED_PKD_ID:
			e = "ExceptionInfoCode[INV_ASSOCIATED_PKD_ID]";
			break;

		case M_INV_PKG_ITEM_PARAM:
			e = "ExceptionInfoCode[INV_PKG_ITEM_PARAM]";
			break;

		case M_INV_RETURN_STATUS_VALUE:
			e = "ExceptionInfoCode[INV_RETURN_STATUS_VALUE]";
			break;

		case M_INV_ASSOC_EVENT_TYPE:
			e = "ExceptionInfoCode[INV_ASSOC_EVENT_TYPE]";
			break;

		case M_INV_ASSOC_IND_REASON:
			e = "ExceptionInfoCode[INV_ASSOC_IND_REASON]";
			break;

		case M_INV_ASSOC_STATE:
			e = "ExceptionInfoCode[INV_ASSOC_STATE]";
			break;

		case M_INV_ENC_FORMAT:
			e = "ExceptionInfoCode[INV_ENC_FORMAT]";
			break;

		case M_INV_SVC_CHNG_METHOD:
			e = "ExceptionInfoCode[INV_SVC_CHNG_METHOD]";
			break;

		case M_INV_SVC_CHNG_REASON:
			e = "ExceptionInfoCode[INV_SVC_CHNG_REASON]";
			break;

		case M_INV_TPT_TYPE:
			e = "ExceptionInfoCode[INV_TPT_TYPE]";
			break;

		case M_INV_CMD_REQ_TYPE:
			e = "ExceptionInfoCode[INV_CMD_REQ_TYPE]";
			break;

		case M_INV_CMD_RESP_TYPE:
			e = "ExceptionInfoCode[INV_CMD_RESP_TYPE]";
			break;

		case M_INV_CMD_TYPE:
			e = "ExceptionInfoCode[INV_CMD_TYPE]";
			break;

		case M_INV_DESC_TYPE:
			e = "ExceptionInfoCode[INV_DESC_TYPE]";
			break;

		case M_INV_TERM_TYPE:
			e = "ExceptionInfoCode[INV_TERM_TYPE]";
			break;

		case M_INV_EVT_BUFFER_CTRL:
			e = "ExceptionInfoCode[INV_EVT_BUFFER_CTRL]";
			break;

		case M_INV_MODEM_TYPE:
			e = "ExceptionInfoCode[INV_MODEM_TYPE]";
			break;

		case M_INV_MUX_TYPE:
			e = "ExceptionInfoCode[INV_MUX_TYPE]";
			break;

		case M_INV_SERVICE_STATE:
			e = "ExceptionInfoCode[INV_SERVICE_STATE]";
			break;

		case M_INV_SIGNAL_PARAM_TYPE:
			e = "ExceptionInfoCode[INV_SIGNAL_PARAM_TYPE]";
			break;

		case M_INV_SIGNAL_TYPE:
			e = "ExceptionInfoCode[INV_SIGNAL_TYPE]";
			break;

		case M_INV_STREAM_MODE:
			e = "ExceptionInfoCode[INV_STREAM_MODE]";
			break;

		case M_INV_TOPOLOGY_DIR:
			e = "ExceptionInfoCode[INV_TOPOLOGY_DIR]";
			break;

		case M_INV_PARAM_VAL_TYPE:
			e = "ExceptionInfoCode[INV_PARAM_VAL_TYPE]";
			break;

		case M_INV_PARAM_RELATION:
			e = "ExceptionInfoCode[INV_PARAM_RELATION]";
			break;

		case M_INV_PKG_ITEM_TYPE:
			e = "ExceptionInfoCode[INV_PKG_ITEM_TYPE]";
			break;

		case M_INV_PRIORITY_VAL:
			e = "ExceptionInfoCode[INV_PRIORITY_VAL]";
			break;

		case M_DUPLICATE_DESC_IN_CMD:
			e = "ExceptionInfoCode[DUPLICATE_DESC_IN_CMD]";
			break;

		case M_MISSING_DIGIT_WITH_QUALIFIER_DOT:
			e = "ExceptionInfoCode[MISSING_DIGIT_WITH_QUALIFIER_DOT]";
			break;

		default:
			e = "ExceptionInfoCode[" + this.info_code + "]";
		}

		return e;
	}

}
