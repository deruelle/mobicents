package javax.megaco.association;

import java.io.Serializable;

/**
 * Constants used in package javax.megaco.association. This forms the class for
 * the association state of the Association package. The association here refers
 * to the association w.r.t the peer i.e., MG-MGC association. The state here
 * refers to the state of the control association between MG-MGC. These states
 * would assist the application in making decisions regarding when and what
 * commands to initiate towards the peer. For eg. untill the association is in
 * state M_ASSOC_STATE_REG_COMPL, the application should not send any commands
 * to the stack. Similarly, if the association is in
 * M_ASSOC_STATE_GRACEFUL_SHUTDOWN_IN_PRGS state and if the application is MGC,
 * then it should refrain from establishing new calls, therefore it should not
 * send new ADD commands.
 * 
 */
public class AssocState implements Serializable {

	/**
	 * Identifies association is in IDLE state. Its value shall be set to 1.
	 */
	public final static int M_ASSOC_STATE_IDLE = 1;
	/**
	 * Identifies association is in Registration state. Its value shall be set
	 * to 2.
	 */
	public final static int M_ASSOC_STATE_REG_IN_PRGS = 2;
	/**
	 * Identifies association is in Registration completed state. Its value
	 * shall be set to 3.
	 */
	public final static int M_ASSOC_STATE_REG_COMPL = 3;
	/**
	 * Identifies association is in Disconnected state. Its value shall be set
	 * to 4.
	 */
	public final static int M_ASSOC_STATE_DISCONNECTED = 4;
	/**
	 * Identifies association is in Handoff state. Its value shall be set to 5.
	 */
	public final static int M_ASSOC_STATE_HANDOFF_IN_PRGS = 5;
	/**
	 * Identifies association is in Handoff state. Its value shall be set to 6.
	 */
	public final static int M_ASSOC_STATE_FAILOVER_IN_PRGS = 6;
	/**
	 * Identifies association is in Forced shutdown state. Its value shall be
	 * set to 7.
	 */
	public final static int M_ASSOC_STATE_FORCED_SHUTDOWN_IN_PRGS = 7;
	/**
	 * Identifies association is in Registration state. Its value shall be set
	 * to 8.
	 */
	public final static int M_ASSOC_STATE_GRACEFUL_SHUTDOWN_IN_PRGS = 8;
	/**
	 * Identifies a AssocState object that constructs the class with the
	 * constant M_ASSOC_STATE_IDLE.
	 */
	public final static AssocState ASSOC_STATE_IDLE = new AssocState(
			M_ASSOC_STATE_IDLE);
	/**
	 * Identifies a AssocState object that constructs the class with the field
	 * constant M_ASSOC_STATE_REG_IN_PRGS.
	 */
	public final static AssocState ASSOC_STATE_REG_IN_PRGS = new AssocState(
			M_ASSOC_STATE_REG_IN_PRGS);
	/**
	 * Identifies a AssocState object that constructs the class with the field
	 * constant M_ASSOC_STATE_REG_COMPL.
	 */
	public final static AssocState ASSOC_STATE_REG_COMPL = new AssocState(
			M_ASSOC_STATE_REG_COMPL);
	/**
	 * Identifies a AssocState object that constructs the class with the field
	 * constant M_ASSOC_STATE_DISCONNECTED.
	 */
	public final static AssocState ASSOC_STATE_DISCONNECTED = new AssocState(
			M_ASSOC_STATE_DISCONNECTED);
	/**
	 * Identifies a AssocState object that constructs the class with the field
	 * constant M_ASSOC_STATE_HANDOFF_IN_PRGS.
	 */
	public final static AssocState ASSOC_STATE_HANDOFF_IN_PRGS = new AssocState(
			M_ASSOC_STATE_HANDOFF_IN_PRGS);
	/**
	 * Identifies a AssocState object that constructs the class with the field
	 * constant M_ASSOC_STATE_FAILOVER_IN_PRGS.
	 */
	public final static AssocState ASSOC_STATE_FAILOVER_IN_PRGS = new AssocState(
			M_ASSOC_STATE_FAILOVER_IN_PRGS);
	/**
	 * Identifies a AssocState object that constructs the class with the field
	 * constant M_ASSOC_STATE_FORCED_SHUTDOWN_IN_PRGS.
	 */
	public final static AssocState ASSOC_STATE_FORCED_SHUTDOWN_IN_PRGS = new AssocState(
			M_ASSOC_STATE_FORCED_SHUTDOWN_IN_PRGS);
	/**
	 * Identifies a AssocState object that constructs the class with the field
	 * constant M_ASSOC_STATE_GRACEFUL_SHUTDOWN_IN_PRGS.
	 */
	public final static AssocState ASSOC_STATE_GRACEFUL_SHUTDOWN_IN_PRGS = new AssocState(
			M_ASSOC_STATE_GRACEFUL_SHUTDOWN_IN_PRGS);

	private int assocState = -1;

	private AssocState(int assocStateIdle) {
		this.assocState = assocStateIdle;
	}

	public int getAssocState() {
		return this.assocState;
	}

	public static final AssocState getObject(int value) {

		switch (value) {
		case M_ASSOC_STATE_IDLE:
			return ASSOC_STATE_IDLE;
		case M_ASSOC_STATE_REG_IN_PRGS:
			return ASSOC_STATE_REG_IN_PRGS;
		case M_ASSOC_STATE_REG_COMPL:
			return ASSOC_STATE_REG_COMPL;
		case M_ASSOC_STATE_DISCONNECTED:
			return ASSOC_STATE_DISCONNECTED;
		case M_ASSOC_STATE_HANDOFF_IN_PRGS:
			return ASSOC_STATE_HANDOFF_IN_PRGS;
		case M_ASSOC_STATE_FAILOVER_IN_PRGS:
			return ASSOC_STATE_FAILOVER_IN_PRGS;
		case M_ASSOC_STATE_FORCED_SHUTDOWN_IN_PRGS:
			return ASSOC_STATE_FORCED_SHUTDOWN_IN_PRGS;
		case M_ASSOC_STATE_GRACEFUL_SHUTDOWN_IN_PRGS:
			return ASSOC_STATE_GRACEFUL_SHUTDOWN_IN_PRGS;
		default:
			throw new IllegalArgumentException(
					"Wrogn value passed, there is no assoc state with code: "
							+ value);

		}

	}

}
