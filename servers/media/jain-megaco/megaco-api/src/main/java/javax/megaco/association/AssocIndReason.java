package javax.megaco.association;

import java.io.Serializable;

/**
 * Constants used in package javax.megaco.association. This forms the class for
 * the association state change indication reason. The reasons indicate why the
 * association has undergone the state change.
 */
public class AssocIndReason implements Serializable {

	/**
	 * Identifies association state change indication reason to be indication
	 * received from peer. Its value shall be set to 1.
	 */
	public static final int M_IND_RCVD_FRM_PEER = 1;
	/**
	 * Identifies association state change indication reason to be
	 * retransmissions expiry. Its value shall be set to 2.
	 */
	public static final int M_RETRIES_XPIRED = 2;
	/**
	 * Identifies association state change indication reason to be timer expiry.
	 * Its value shall be set to 3.
	 */
	public static final int M_TIMER_EXP_IND = 3;
	/**
	 * Identifies association state change indication reason to be operation
	 * completion. Its value shall be set to 4.
	 */
	public static final int M_OPER_COMPL_IND = 4;
	/**
	 * Identifies association state change indication reason to be network
	 * failure. Its value shall be set to 5.
	 */
	public static final int M_NW_FAILURE_IND = 5;
	/**
	 * Identifies association state change indication reason to be network
	 * linkup. Its value shall be set to 6.
	 */
	public static final int M_NW_LINKUP_IND = 6;

	/**
	 * Identifies a AssocIndReason object that constructs the class with the
	 * constant M_IND_RCVD_FRM_PEER.
	 */
	public static final AssocIndReason IND_RCVD_FRM_PEER = new AssocIndReason(M_IND_RCVD_FRM_PEER);
	/**
	 * Identifies a AssocIndReason object that constructs the class with the
	 * field constant M_RETRIES_XPIRED.
	 */
	public static final AssocIndReason RETRIES_XPIRED = new AssocIndReason(M_RETRIES_XPIRED);
	/**
	 * Identifies a AssocIndReason object that constructs the class with the
	 * field constant M_TIMER_EXP_IND.
	 */
	public static final AssocIndReason TIMER_EXP_IND = new AssocIndReason(M_TIMER_EXP_IND);
	/**
	 * Identifies a AssocIndReason object that constructs the class with the
	 * field constant M_OPER_COMPL_IND.
	 */
	public static final AssocIndReason OPER_COMPL_IND = new AssocIndReason(M_OPER_COMPL_IND);
	/**
	 * Identifies a AssocIndReason object that constructs the class with the
	 * field constant M_NW_FAILURE_IND.
	 */
	public static final AssocIndReason NW_FAILURE_IND = new AssocIndReason(M_NW_FAILURE_IND);
	/**
	 * Identifies a AssocIndReason object that constructs the class with the
	 * field constant M_NW_LINKUP_IND.
	 */
	public static final AssocIndReason NW_LINKUP_IND = new AssocIndReason(M_NW_LINKUP_IND);

	private int nwLinkupInd = -1;

	private AssocIndReason(int nwLinkupInd) {
		this.nwLinkupInd = nwLinkupInd;
	}

	public int getAssocIndReason() {
		return nwLinkupInd;
	}

	public static final AssocIndReason getObject(int value) throws IllegalArgumentException {

		switch (value) {

		case M_IND_RCVD_FRM_PEER:
			return IND_RCVD_FRM_PEER;
		case M_RETRIES_XPIRED:
			return RETRIES_XPIRED;
		case M_TIMER_EXP_IND:
			return TIMER_EXP_IND;
		case M_OPER_COMPL_IND:
			return OPER_COMPL_IND;
		case M_NW_FAILURE_IND:
			return NW_FAILURE_IND;
		case M_NW_LINKUP_IND:
			return NW_LINKUP_IND;

		default:
			throw new IllegalArgumentException("Wrogn value passed, there is no AssocIndReason with code: " + value);
		}
	}

	private Object readResolve() {
		return this.getObject(this.nwLinkupInd);
	}

	@Override
	public String toString() {
		switch (this.nwLinkupInd) {

		case M_IND_RCVD_FRM_PEER:
			return "AssocIndReason[IND_RCVD_FRM_PEER]";
		case M_RETRIES_XPIRED:
			return "AssocIndReason[RETRIES_XPIRED]";

		case M_TIMER_EXP_IND:
			return "AssocIndReason[TIMER_EXP_IND]";

		case M_OPER_COMPL_IND:
			return "AssocIndReason[OPER_COMPL_IND]";

		case M_NW_FAILURE_IND:
			return "AssocIndReason[NW_FAILURE_IND]";

		case M_NW_LINKUP_IND:
			return "AssocIndReason[NW_LINKUP_IND]";

		default:
			return "AssocIndReason[" + this.nwLinkupInd + "]";
		}
	}

}
