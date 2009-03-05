package javax.megaco.association;

import java.io.Serializable;

/**
 * Service change method constants used in package javax.megaco.association.
 * This forms the class for the Service change method parameters of the Jain
 * Megaco package.
 */
public class SrvChngMethod implements Serializable {

	/**
	 * Identifies the service change method towards the peer shall be graceful.
	 */
	public final static int M_GRACEFUL = 1;
	/**
	 * Identifies the service change method towards the peer shall be forced.
	 */
	public final static int M_FORCED = 2;
	/**
	 * Identifies the service change method towards the peer shall be restart.
	 */
	public final static int M_RESTART = 3;
	/**
	 * Identifies the service change method towards the peer shall be
	 * disconnected.
	 */
	public final static int M_DISCONNECTED = 4;
	/**
	 * Identifies the service change method towards the peer shall be handoff.
	 */
	public final static int M_HANDOFF = 5;
	/**
	 * Identifies the service change method towards the peer shall be failover.
	 */
	public final static int M_FAILOVER = 6;
	/**
	 * Identifies the service change method towards the peer shall be an
	 * extended value.
	 */
	public final static int M_EXTENSION = 7;

	/**
	 * Identifies a SrvChngMethod object that constructs the class with the
	 * constant M_GRACEFUL.
	 */
	public final static SrvChngMethod GRACEFUL = new SrvChngMethod(M_GRACEFUL);
	/**
	 * Identifies a SrvChngMethod object that constructs the class with the
	 * constant M_FORCED.
	 */
	public final static SrvChngMethod FORCED = new SrvChngMethod(M_FORCED);
	/**
	 * Identifies a SrvChngMethod object that constructs the class with the
	 * constant M_RESTART.
	 */
	public final static SrvChngMethod RESTART = new SrvChngMethod(M_RESTART);
	/**
	 * Identifies a SrvChngMethod object that constructs the class with the
	 * constant M_DISCONNECTED.
	 */
	public final static SrvChngMethod DISCONNECTED = new SrvChngMethod(M_DISCONNECTED);
	/**
	 * Identifies a SrvChngMethod object that constructs the class with the
	 * constant M_HANDOFF.
	 */
	public final static SrvChngMethod HANDOFF = new SrvChngMethod(M_HANDOFF);
	/**
	 * Identifies a SrvChngMethod object that constructs the class with the
	 * constant M_FAILOVER.
	 */
	public final static SrvChngMethod FAILOVER = new SrvChngMethod(M_FAILOVER);
	/**
	 * Identifies a SrvChngMethod object that constructs the class with the
	 * constant {@link javax.megaco.association.SrvChngMethod.M_EXTENSION}.
	 */
	public final static SrvChngMethod EXTENSION = new SrvChngMethod(M_EXTENSION);

	private int srvChngMethodId = -1;

	/**
	 * Constructs a class initialised with value service_change_method as passed
	 * to it in the constructor.
	 * 
	 * @param service_change_method
	 */
	private SrvChngMethod(int service_change_method) {
		this.srvChngMethodId = service_change_method;
	}

	/**
	 * This method returns one of the static field constants defined in this
	 * class.
	 * 
	 * @return Returns an integer value that identifies the service change
	 *         method.
	 */
	public int getSrvChngMethodId() {
		return this.srvChngMethodId;
	}

	/**
	 * Returns reference of the SrvChngMethod object that identifies the service
	 * change method as value passed to this method.
	 * 
	 * @param value
	 *            - It is one of the possible values of the static constant that
	 *            this class provides.
	 * @return Returns reference of the SrvChngMethod object.
	 * @throws IllegalArgumentException
	 *             - If the value passed to this method is invalid, then this
	 *             exception is raised.
	 */
	public static SrvChngMethod getObject(int value) throws IllegalArgumentException {

		switch (value) {
		case M_GRACEFUL:
			return GRACEFUL;
		case M_FORCED:
			return FORCED;
		case M_RESTART:
			return RESTART;
		case M_DISCONNECTED:
			return DISCONNECTED;
		case M_HANDOFF:
			return HANDOFF;
		case M_FAILOVER:
			return FAILOVER;
		case M_EXTENSION:
			return EXTENSION;

		default:
			throw new IllegalArgumentException("Wrogn value passed, there is no change method with code: " + value);
		}

	}

	/**
	 * This method must be implemented to perform instance substitution during
	 * serialization. This method is required for reference comparison. This
	 * method if not implimented will simply fail each time we compare an
	 * Enumeration value against a de-serialized Enumeration instance.
	 * 
	 * @return
	 */
	private Object readResolve() {
		return this.getObject(this.srvChngMethodId);
	}

	@Override
	public String toString() {
		switch (this.srvChngMethodId) {
		case M_GRACEFUL:
			return "SrvChngMethod[SVC_CHNG_METHOD_GRACEFUL]";
		case M_FORCED:
			return "SrvChngMethod[SVC_CHNG_METHOD_FORCED]";

		case M_RESTART:
			return "SrvChngMethod[SVC_CHNG_METHOD_RESTART]";

		case M_DISCONNECTED:
			return "SrvChngMethod[SVC_CHNG_METHOD_DISCONNECTED]";

		case M_HANDOFF:
			return "SrvChngMethod[SVC_CHNG_METHOD_HANDOFF]";

		case M_FAILOVER:
			return "SrvChngMethod[SVC_CHNG_METHOD_FAILOVER]";

		case M_EXTENSION:
			return "SrvChngMethod[SVC_CHNG_METHOD_EXTENSION]";

		default:
			return "SrvChngMethod[" + this.srvChngMethodId + "]";
		}
	}

}
