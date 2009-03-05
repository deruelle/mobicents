package javax.megaco.association;

import java.io.Serializable;

/**
 * Constants used in package javax.megaco.association. This forms the class for
 * the association state of the Association package. The association here refers
 * to the association w.r.t the peer i.e., MG-MGC association. The state here
 * refers to the state of the control association between MG-MGC. These states
 * would assist the application in making decisions regarding when and what
 * commands to initiate towards the peer. For eg. untill the association is in
 * state M_REG_COMPL, the application should not send any commands to the stack.
 * Similarly, if the association is in M_GRACEFUL_SHUTDOWN_IN_PRGS state and if
 * the application is MGC, then it should refrain from establishing new calls,
 * therefore it should not send new ADD commands.
 * 
 */
public class AssocState implements Serializable {

	/**
	 * Identifies association is in IDLE state. Its value shall be set to 1.
	 */
	public final static int M_IDLE = 1;
	/**
	 * Identifies association is in Registration state. Its value shall be set
	 * to 2.
	 */
	public final static int M_REG_IN_PRGS = 2;
	/**
	 * Identifies association is in Registration completed state. Its value
	 * shall be set to 3.
	 */
	public final static int M_REG_COMPL = 3;
	/**
	 * Identifies association is in Disconnected state. Its value shall be set
	 * to 4.
	 */
	public final static int M_DISCONNECTED = 4;
	/**
	 * Identifies association is in Handoff state. Its value shall be set to 5.
	 */
	public final static int M_HANDOFF_IN_PRGS = 5;
	/**
	 * Identifies association is in Handoff state. Its value shall be set to 6.
	 */
	public final static int M_FAILOVER_IN_PRGS = 6;
	/**
	 * Identifies association is in Forced shutdown state. Its value shall be
	 * set to 7.
	 */
	public final static int M_FORCED_SHUTDOWN_IN_PRGS = 7;
	/**
	 * Identifies association is in Registration state. Its value shall be set
	 * to 8.
	 */
	public final static int M_GRACEFUL_SHUTDOWN_IN_PRGS = 8;
	/**
	 * Identifies a AssocState object that constructs the class with the
	 * constant M_IDLE.
	 */
	public final static AssocState IDLE = new AssocState(M_IDLE);
	/**
	 * Identifies a AssocState object that constructs the class with the field
	 * constant M_REG_IN_PRGS.
	 */
	public final static AssocState REG_IN_PRGS = new AssocState(M_REG_IN_PRGS);
	/**
	 * Identifies a AssocState object that constructs the class with the field
	 * constant M_REG_COMPL.
	 */
	public final static AssocState REG_COMPL = new AssocState(M_REG_COMPL);
	/**
	 * Identifies a AssocState object that constructs the class with the field
	 * constant M_DISCONNECTED.
	 */
	public final static AssocState DISCONNECTED = new AssocState(M_DISCONNECTED);
	/**
	 * Identifies a AssocState object that constructs the class with the field
	 * constant M_HANDOFF_IN_PRGS.
	 */
	public final static AssocState HANDOFF_IN_PRGS = new AssocState(M_HANDOFF_IN_PRGS);
	/**
	 * Identifies a AssocState object that constructs the class with the field
	 * constant M_FAILOVER_IN_PRGS.
	 */
	public final static AssocState FAILOVER_IN_PRGS = new AssocState(M_FAILOVER_IN_PRGS);
	/**
	 * Identifies a AssocState object that constructs the class with the field
	 * constant M_FORCED_SHUTDOWN_IN_PRGS.
	 */
	public final static AssocState FORCED_SHUTDOWN_IN_PRGS = new AssocState(M_FORCED_SHUTDOWN_IN_PRGS);
	/**
	 * Identifies a AssocState object that constructs the class with the field
	 * constant M_GRACEFUL_SHUTDOWN_IN_PRGS.
	 */
	public final static AssocState GRACEFUL_SHUTDOWN_IN_PRGS = new AssocState(M_GRACEFUL_SHUTDOWN_IN_PRGS);

	private int assocState = -1;

	private AssocState(int assocStateIdle) {
		this.assocState = assocStateIdle;
	}

	public int getAssocState() {
		return this.assocState;
	}

	public static final AssocState getObject(int value) {

		switch (value) {
		case M_IDLE:
			return IDLE;
		case M_REG_IN_PRGS:
			return REG_IN_PRGS;
		case M_REG_COMPL:
			return REG_COMPL;
		case M_DISCONNECTED:
			return DISCONNECTED;
		case M_HANDOFF_IN_PRGS:
			return HANDOFF_IN_PRGS;
		case M_FAILOVER_IN_PRGS:
			return FAILOVER_IN_PRGS;
		case M_FORCED_SHUTDOWN_IN_PRGS:
			return FORCED_SHUTDOWN_IN_PRGS;
		case M_GRACEFUL_SHUTDOWN_IN_PRGS:
			return GRACEFUL_SHUTDOWN_IN_PRGS;
		default:
			throw new IllegalArgumentException("Wrogn value passed, there is no assoc state with code: " + value);

		}

	}

	@Override
	public String toString() {
		switch (this.assocState) {
		case M_IDLE:
			return "AssocState[IDLE]";
		case M_REG_IN_PRGS:
			return "AssocState[REG_IN_PRGS";
		case M_REG_COMPL:
			return "AssocState[REG_COMPL";
		case M_DISCONNECTED:
			return "AssocState[DISCONNECTED";
		case M_HANDOFF_IN_PRGS:
			return "AssocState[HANDOFF_IN_PRGS";
		case M_FAILOVER_IN_PRGS:
			return "AssocState[FAILOVER_IN_PRGS";
		case M_FORCED_SHUTDOWN_IN_PRGS:
			return "AssocState[FORCED_SHUTDOWN_IN_PRGS";
		case M_GRACEFUL_SHUTDOWN_IN_PRGS:
			return "AssocState[GRACEFUL_SHUTDOWN_IN_PRGS";
		default:
			return "AssocState[" + this.assocState + "]";

		}
	}

}
