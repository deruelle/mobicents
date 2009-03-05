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
	public final static int M_R900 = 900;
	/**
	 * Identifies the service change reason towards the peer shall be 901 - Cold
	 * Boot.
	 */
	public final static int M_R901 = 901;
	/**
	 * Identifies the service change reason towards the peer shall be 902 - Warm
	 * Boot.
	 */
	public final static int M_R902 = 902;
	/**
	 * Identifies the service change reason towards the peer shall be 903 - MGC
	 * Directed Change.
	 */
	public final static int M_R903 = 903;
	/**
	 * Identifies the service change reason towards the peer shall be 904 -
	 * Termination Malfunctioning.
	 */
	public final static int M_R904 = 904;
	/**
	 * Identifies the service change reason towards the peer shall be 905 -
	 * Termination Taken Out Of Service.
	 */
	public final static int M_R905 = 905;
	/**
	 * Identifies the service change reason towards the peer shall be 906 - Loss
	 * Of Lower Layer Connectivity.
	 */
	public final static int M_R906 = 906;
	/**
	 * Identifies the service change reason towards the peer shall be 907 -
	 * Transmission Failure.
	 */
	public final static int M_R907 = 907;
	/**
	 * Identifies the service change reason towards the peer shall be 908 - MG
	 * Impending Failure.
	 */
	public final static int M_R908 = 908;
	/**
	 * Identifies the service change reason towards the peer shall be 909 - MGC
	 * Impending Failure.
	 */
	public final static int M_R909 = 909;
	/**
	 * Identifies the service change reason towards the peer shall be 910 -
	 * Media Capability Failure.
	 */
	public final static int M_R910 = 910;
	/**
	 * Identifies the service change reason towards the peer shall be 911 -
	 * Modem Capability Failure.
	 */
	public final static int M_R911 = 911;
	/**
	 * Identifies the service change reason towards the peer shall be 912 - Mux
	 * Capability Failure.
	 */
	public final static int M_R912 = 912;
	/**
	 * Identifies the service change reason towards the peer shall be 913 -
	 * Signal Capability Failure.
	 */
	public final static int M_R913 = 913;
	/**
	 * Identifies the service change reason towards the peer shall be 914 -
	 * Event Capability Failure.
	 */
	public final static int M_R914 = 914;
	/**
	 * Identifies the service change reason towards the peer shall be 915 -
	 * State Loss.
	 */
	public final static int M_R915 = 915;

	/**
	 * Identifies a SrvChngReason object that constructs the class with the
	 * constant M_R900.
	 */
	public static final SrvChngReason R900 = new SrvChngReason(M_R900);
	/**
	 * Identifies a SrvChngReason object that constructs the class with the
	 * constant M_R901.
	 */
	public static final SrvChngReason R901 = new SrvChngReason(M_R901);
	/**
	 * Identifies a SrvChngReason object that constructs the class with the
	 * constant M_R902.
	 */
	public static final SrvChngReason R902 = new SrvChngReason(M_R902);
	/**
	 * Identifies a SrvChngReason object that constructs the class with the
	 * constant M_R903.
	 */
	public static final SrvChngReason R903 = new SrvChngReason(M_R903);
	/**
	 * Identifies a SrvChngReason object that constructs the class with the
	 * constant M_R904.
	 */
	public static final SrvChngReason R904 = new SrvChngReason(M_R904);
	/**
	 * Identifies a SrvChngReason object that constructs the class with the
	 * constant M_R905.
	 */
	public static final SrvChngReason R905 = new SrvChngReason(M_R905);
	/**
	 * Identifies a SrvChngReason object that constructs the class with the
	 * constant M_R906.
	 */
	public static final SrvChngReason R906 = new SrvChngReason(M_R906);
	/**
	 * Identifies a SrvChngReason object that constructs the class with the
	 * constant M_R907.
	 */
	public static final SrvChngReason R907 = new SrvChngReason(M_R907);
	/**
	 * Identifies a SrvChngReason object that constructs the class with the
	 * constant M_R908.
	 */
	public static final SrvChngReason R908 = new SrvChngReason(M_R908);
	/**
	 * Identifies a SrvChngReason object that constructs the class with the
	 * constant M_R909.
	 */
	public static final SrvChngReason R909 = new SrvChngReason(M_R909);
	/**
	 * Identifies a SrvChngReason object that constructs the class with the
	 * constant M_R910.
	 */
	public static final SrvChngReason R910 = new SrvChngReason(M_R910);
	/**
	 * Identifies a SrvChngReason object that constructs the class with the
	 * constant M_R911.
	 */
	public static final SrvChngReason R911 = new SrvChngReason(M_R911);
	/**
	 * Identifies a SrvChngReason object that constructs the class with the
	 * constant M_R912.
	 */
	public static final SrvChngReason R912 = new SrvChngReason(M_R912);
	/**
	 * Identifies a SrvChngReason object that constructs the class with the
	 * constant M_R913.
	 */
	public static final SrvChngReason R913 = new SrvChngReason(M_R913);
	/**
	 * Identifies a SrvChngReason object that constructs the class with the
	 * constant M_R914.
	 */
	public static final SrvChngReason R914 = new SrvChngReason(M_R914);
	/**
	 * Identifies a SrvChngReason object that constructs the class with the
	 * constant M_R915.
	 */
	public static final SrvChngReason R915 = new SrvChngReason(M_R915);

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
		case M_R900:
			return R900;

		case M_R901:
			return R901;

		case M_R902:
			return R902;

		case M_R903:
			return R903;
		case M_R904:
			return R904;
		case M_R905:
			return R905;
		case M_R906:
			return R906;
		case M_R907:
			return R907;
		case M_R908:
			return R908;
		case M_R909:
			return R909;
		case M_R910:
			return R910;
		case M_R911:
			return R911;
		case M_R912:
			return R912;
		case M_R913:
			return R913;
		case M_R914:
			return R914;
		case M_R915:
			return R915;
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
		case M_R900:
			return "SrvChngReason[R900]";

		case M_R901:
			return "SrvChngReason[R901]";

		case M_R902:
			return "SrvChngReason[R902]";

		case M_R903:
			return "SrvChngReason[R903]";

		case M_R904:
			return "SrvChngReason[R904]";

		case M_R905:
			return "SrvChngReason[R905]";

		case M_R906:
			return "SrvChngReason[R906]";

		case M_R907:
			return "SrvChngReason[R907]";

		case M_R908:
			return "SrvChngReason[R908]";

		case M_R909:
			return "SrvChngReason[R909]";

		case M_R910:
			return "SrvChngReason[R910]";

		case M_R911:
			return "SrvChngReason[R911]";

		case M_R912:
			return "SrvChngReason[R912]";

		case M_R913:
			return "SrvChngReason[R913]";

		case M_R914:
			return "SrvChngReason[R914]";

		case M_R915:
			return "SrvChngReason[R915]";

		default:
			return "SrvChngReason[" + this.srvChngReasonId + "]";
		}
	}

}
