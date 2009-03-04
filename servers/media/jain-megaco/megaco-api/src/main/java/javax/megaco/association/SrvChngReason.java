package javax.megaco.association;

import java.io.Serializable;

/**
 * 
 Service change reason constants used in package javax.megaco.association.
 * This forms the class for the Service change reason parameters of the Jain
 * Megaco package.
 */
public class SrvChngReason implements Serializable {

	/**
	 * Identifies the service change reason towards the peer shall be 900 -
	 * Service restored.
	 */
	public final static int M_SVC_CHNG_REASON_900 = 900;
	/**
	 * Identifies the service change reason towards the peer shall be 901 - Cold
	 * Boot.
	 */
	public final static int M_SVC_CHNG_REASON_901 = 901;
	/**
	 * Identifies the service change reason towards the peer shall be 902 - Warm
	 * Boot.
	 */
	public final static int M_SVC_CHNG_REASON_902 = 902;
	/**
	 * Identifies the service change reason towards the peer shall be 903 - MGC
	 * Directed Change.
	 */
	public final static int M_SVC_CHNG_REASON_903 = 903;
	/**
	 * Identifies the service change reason towards the peer shall be 904 -
	 * Termination Malfunctioning.
	 */
	public final static int M_SVC_CHNG_REASON_904 = 904;
	/**
	 * Identifies the service change reason towards the peer shall be 905 -
	 * Termination Taken Out Of Service.
	 */
	public final static int M_SVC_CHNG_REASON_905 = 905;
	/**
	 * Identifies the service change reason towards the peer shall be 906 - Loss
	 * Of Lower Layer Connectivity.
	 */
	public final static int M_SVC_CHNG_REASON_906 = 906;
	/**
	 * Identifies the service change reason towards the peer shall be 907 -
	 * Transmission Failure.
	 */
	public final static int M_SVC_CHNG_REASON_907 = 907;
	/**
	 * Identifies the service change reason towards the peer shall be 908 - MG
	 * Impending Failure.
	 */
	public final static int M_SVC_CHNG_REASON_908 = 908;
	/**
	 * Identifies the service change reason towards the peer shall be 909 - MGC
	 * Impending Failure.
	 */
	public final static int M_SVC_CHNG_REASON_909 = 909;
	/**
	 * Identifies the service change reason towards the peer shall be 910 -
	 * Media Capability Failure.
	 */
	public final static int M_SVC_CHNG_REASON_910 = 910;
	/**
	 * Identifies the service change reason towards the peer shall be 911 -
	 * Modem Capability Failure.
	 */
	public final static int M_SVC_CHNG_REASON_911 = 911;
	/**
	 * Identifies the service change reason towards the peer shall be 912 - Mux
	 * Capability Failure.
	 */
	public final static int M_SVC_CHNG_REASON_912 = 912;
	/**
	 * Identifies the service change reason towards the peer shall be 913 -
	 * Signal Capability Failure.
	 */
	public final static int M_SVC_CHNG_REASON_913 = 913;
	/**
	 * Identifies the service change reason towards the peer shall be 914 -
	 * Event Capability Failure.
	 */
	public final static int M_SVC_CHNG_REASON_914 = 914;
	/**
	 * Identifies the service change reason towards the peer shall be 915 -
	 * State Loss.
	 */
	public final static int M_SVC_CHNG_REASON_915 = 915;

	/**
	 * Identifies a SrvChngReason object that constructs the class with the
	 * constant M_SVC_CHNG_REASON_900.
	 */
	public static final SrvChngReason SVC_CHNG_REASON_900 = new SrvChngReason(M_SVC_CHNG_REASON_900);
	/**
	 * Identifies a SrvChngReason object that constructs the class with the
	 * constant M_SVC_CHNG_REASON_901.
	 */
	public static final SrvChngReason SVC_CHNG_REASON_901 = new SrvChngReason(M_SVC_CHNG_REASON_901);
	/**
	 * Identifies a SrvChngReason object that constructs the class with the
	 * constant M_SVC_CHNG_REASON_902.
	 */
	public static final SrvChngReason SVC_CHNG_REASON_902 = new SrvChngReason(M_SVC_CHNG_REASON_902);
	/**
	 * Identifies a SrvChngReason object that constructs the class with the
	 * constant M_SVC_CHNG_REASON_903.
	 */
	public static final SrvChngReason SVC_CHNG_REASON_903 = new SrvChngReason(M_SVC_CHNG_REASON_903);
	/**
	 * Identifies a SrvChngReason object that constructs the class with the
	 * constant M_SVC_CHNG_REASON_904.
	 */
	public static final SrvChngReason SVC_CHNG_REASON_904 = new SrvChngReason(M_SVC_CHNG_REASON_904);
	/**
	 * Identifies a SrvChngReason object that constructs the class with the
	 * constant M_SVC_CHNG_REASON_905.
	 */
	public static final SrvChngReason SVC_CHNG_REASON_905 = new SrvChngReason(M_SVC_CHNG_REASON_905);
	/**
	 * Identifies a SrvChngReason object that constructs the class with the
	 * constant M_SVC_CHNG_REASON_906.
	 */
	public static final SrvChngReason SVC_CHNG_REASON_906 = new SrvChngReason(M_SVC_CHNG_REASON_906);
	/**
	 * Identifies a SrvChngReason object that constructs the class with the
	 * constant M_SVC_CHNG_REASON_907.
	 */
	public static final SrvChngReason SVC_CHNG_REASON_907 = new SrvChngReason(M_SVC_CHNG_REASON_907);
	/**
	 * Identifies a SrvChngReason object that constructs the class with the
	 * constant M_SVC_CHNG_REASON_908.
	 */
	public static final SrvChngReason SVC_CHNG_REASON_908 = new SrvChngReason(M_SVC_CHNG_REASON_908);
	/**
	 * Identifies a SrvChngReason object that constructs the class with the
	 * constant M_SVC_CHNG_REASON_909.
	 */
	public static final SrvChngReason SVC_CHNG_REASON_909 = new SrvChngReason(M_SVC_CHNG_REASON_909);
	/**
	 * Identifies a SrvChngReason object that constructs the class with the
	 * constant M_SVC_CHNG_REASON_910.
	 */
	public static final SrvChngReason SVC_CHNG_REASON_910 = new SrvChngReason(M_SVC_CHNG_REASON_910);
	/**
	 * Identifies a SrvChngReason object that constructs the class with the
	 * constant M_SVC_CHNG_REASON_911.
	 */
	public static final SrvChngReason SVC_CHNG_REASON_911 = new SrvChngReason(M_SVC_CHNG_REASON_911);
	/**
	 * Identifies a SrvChngReason object that constructs the class with the
	 * constant M_SVC_CHNG_REASON_912.
	 */
	public static final SrvChngReason SVC_CHNG_REASON_912 = new SrvChngReason(M_SVC_CHNG_REASON_912);
	/**
	 * Identifies a SrvChngReason object that constructs the class with the
	 * constant M_SVC_CHNG_REASON_913.
	 */
	public static final SrvChngReason SVC_CHNG_REASON_913 = new SrvChngReason(M_SVC_CHNG_REASON_913);
	/**
	 * Identifies a SrvChngReason object that constructs the class with the
	 * constant M_SVC_CHNG_REASON_914.
	 */
	public static final SrvChngReason SVC_CHNG_REASON_914 = new SrvChngReason(M_SVC_CHNG_REASON_914);
	/**
	 * Identifies a SrvChngReason object that constructs the class with the
	 * constant M_SVC_CHNG_REASON_915.
	 */
	public static final SrvChngReason SVC_CHNG_REASON_915 = new SrvChngReason(M_SVC_CHNG_REASON_915);

	private int srvChngReasonId = -1;

	/**
	 * Constructs a class that initialised with value service_change_reason as
	 * passed to it. The getSrvChngReasonId method of this class object would
	 * always return value service_shange_reason.
	 * 
	 * @param reason
	 */
	private SrvChngReason(int service_change_reason) {
		this.srvChngReasonId = service_change_reason;
	}

	/**
	 * Returns reference of the SrvChngReason object that identifies the service
	 * change reason as value passed to this method.
	 * 
	 * @param value
	 *            - It is one of the possible values of the static constant that
	 *            this class provides.
	 * @return Returns reference of the SrvChngReason object.
	 * @throws IllegalArgumentException
	 *             - If the value passed to this method is invalid, then this
	 *             exception is raised.
	 */
	public static final SrvChngReason getObject(int value) throws IllegalArgumentException {
		switch (value) {
		case M_SVC_CHNG_REASON_900:
			return SVC_CHNG_REASON_900;

		case M_SVC_CHNG_REASON_901:
			return SVC_CHNG_REASON_901;

		case M_SVC_CHNG_REASON_902:
			return SVC_CHNG_REASON_902;

		case M_SVC_CHNG_REASON_903:
			return SVC_CHNG_REASON_903;
		case M_SVC_CHNG_REASON_904:
			return SVC_CHNG_REASON_904;
		case M_SVC_CHNG_REASON_905:
			return SVC_CHNG_REASON_905;
		case M_SVC_CHNG_REASON_906:
			return SVC_CHNG_REASON_906;
		case M_SVC_CHNG_REASON_907:
			return SVC_CHNG_REASON_907;
		case M_SVC_CHNG_REASON_908:
			return SVC_CHNG_REASON_908;
		case M_SVC_CHNG_REASON_909:
			return SVC_CHNG_REASON_909;
		case M_SVC_CHNG_REASON_910:
			return SVC_CHNG_REASON_910;
		case M_SVC_CHNG_REASON_911:
			return SVC_CHNG_REASON_911;
		case M_SVC_CHNG_REASON_912:
			return SVC_CHNG_REASON_912;
		case M_SVC_CHNG_REASON_913:
			return SVC_CHNG_REASON_913;
		case M_SVC_CHNG_REASON_914:
			return SVC_CHNG_REASON_914;
		case M_SVC_CHNG_REASON_915:
			return SVC_CHNG_REASON_915;
		default:
			throw new IllegalArgumentException("Wrogn value passed, there is no change reason with code: " + value);
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
		return this.getObject(this.srvChngReasonId);
	}

	/**
	 * This method returns one of the static field constants defined in this
	 * class.
	 * 
	 * @return Returns an integer value that identifies the service change
	 *         reason.
	 */
	public int getSrvChngReasonId() {
		return this.srvChngReasonId;
	}

	@Override
	public String toString() {
		switch (this.srvChngReasonId) {
		case M_SVC_CHNG_REASON_900:
			return "SrvChngReason[SVC_CHNG_REASON_900]";

		case M_SVC_CHNG_REASON_901:
			return "SrvChngReason[SVC_CHNG_REASON_901]";

		case M_SVC_CHNG_REASON_902:
			return "SrvChngReason[SVC_CHNG_REASON_902]";

		case M_SVC_CHNG_REASON_903:
			return "SrvChngReason[SVC_CHNG_REASON_903]";

		case M_SVC_CHNG_REASON_904:
			return "SrvChngReason[SVC_CHNG_REASON_904]";

		case M_SVC_CHNG_REASON_905:
			return "SrvChngReason[SVC_CHNG_REASON_905]";

		case M_SVC_CHNG_REASON_906:
			return "SrvChngReason[SVC_CHNG_REASON_906]";

		case M_SVC_CHNG_REASON_907:
			return "SrvChngReason[SVC_CHNG_REASON_907]";

		case M_SVC_CHNG_REASON_908:
			return "SrvChngReason[SVC_CHNG_REASON_908]";

		case M_SVC_CHNG_REASON_909:
			return "SrvChngReason[SVC_CHNG_REASON_909]";

		case M_SVC_CHNG_REASON_910:
			return "SrvChngReason[SVC_CHNG_REASON_910]";

		case M_SVC_CHNG_REASON_911:
			return "SrvChngReason[SVC_CHNG_REASON_911]";

		case M_SVC_CHNG_REASON_912:
			return "SrvChngReason[SVC_CHNG_REASON_912]";

		case M_SVC_CHNG_REASON_913:
			return "SrvChngReason[SVC_CHNG_REASON_913]";

		case M_SVC_CHNG_REASON_914:
			return "SrvChngReason[SVC_CHNG_REASON_914]";

		case M_SVC_CHNG_REASON_915:
			return "SrvChngReason[SVC_CHNG_REASON_915]";

		default:
			return "SrvChngReason[" + this.srvChngReasonId + "]";
		}
	}

}
