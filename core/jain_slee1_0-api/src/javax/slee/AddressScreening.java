package javax.slee;

import java.io.Serializable;
import java.io.StreamCorruptedException;

/**
 * This class defines an enumerated type that encapsulates the values defining
 * whether an address has been screened by a user application.  The application can
 * choose one of these constants depending on whether it has screened the address
 * and the outcome received.
 * <p>
 * A singleton instance of each enumerated value is guaranteed (via an implementation
 * of <code>readResolve()</code> - refer {@link java.io.Serializable java.io.Serializable}),
 * so that equality tests using <code>==</code> are always evaluated correctly.  (This
 * equality test is only guaranteed if this class is loaded in the application's boot class
 * path, rather than dynamically loaded at runtime.)
 * @see Address
 */
public final class AddressScreening implements Serializable{
    /**
     * An integer representation of {@link #UNDEFINED}.
     */
    public final static int ADDRESS_SCREENING_UNDEFINED = 0;

    /**
     * An integer representation of {@link #USER_NOT_VERIFIED}.
     */
    public final static int ADDRESS_SCREENING_USER_NOT_VERIFIED = 1;

    /**
     * An integer representation of {@link #USER_VERIFIED_PASSED}.
     */
    public final static int ADDRESS_SCREENING_USER_VERIFIED_PASSED = 2;

    /**
     * An integer representation of {@link #USER_VERIFIED_FAILED}.
     */
    public final static int ADDRESS_SCREENING_USER_VERIFIED_FAILED = 3;

    /**
     * An integer representation of {@link #NETWORK}.
     */
    public final static int ADDRESS_SCREENING_NETWORK = 4;

    /**
     * The UNDEFINED value indicates that the address screening
     * is undefined.
     */
    public final static AddressScreening UNDEFINED = new AddressScreening(ADDRESS_SCREENING_UNDEFINED);

    /**
     * The USER_NOT_VERIFIED value indicates that the user-provided
     * address has not been verified.
     */
    public final static AddressScreening USER_NOT_VERIFIED = new AddressScreening(ADDRESS_SCREENING_USER_NOT_VERIFIED);

    /**
     * The USER_VERIFIED_PASSED value indicates that the user-provided
     * address has been verified and passed.
     */
    public final static AddressScreening USER_VERIFIED_PASSED = new AddressScreening(ADDRESS_SCREENING_USER_VERIFIED_PASSED);

    /**
     * The USER_VERIFIED_FAILED value indicates that the user-provider
     * address has been verified but failed.
     */
    public final static AddressScreening USER_VERIFIED_FAILED = new AddressScreening(ADDRESS_SCREENING_USER_VERIFIED_FAILED);

    /**
     * The NETWORK value indicates that the address is a
     * network or application provided address.
     */
    public final static AddressScreening NETWORK = new AddressScreening(ADDRESS_SCREENING_NETWORK);

    /**
     * Get an <code>AddressScreening</code> object from an integer value.
     * @param value the address screening value as an integer.
     * @return an <code>AddressScreening</code> object corresponding to <code>value</code>.
     * @throws IllegalArgumentException if <code>value</code> is not a valid
     *        address screening value.
     */
    public static AddressScreening fromInt(int value) throws IllegalArgumentException {
        switch (value) {
            case ADDRESS_SCREENING_UNDEFINED: return UNDEFINED;
            case ADDRESS_SCREENING_USER_NOT_VERIFIED: return USER_NOT_VERIFIED;
            case ADDRESS_SCREENING_USER_VERIFIED_PASSED: return USER_VERIFIED_PASSED;
            case ADDRESS_SCREENING_USER_VERIFIED_FAILED: return USER_VERIFIED_FAILED;
            case ADDRESS_SCREENING_NETWORK: return NETWORK;
            default: throw new IllegalArgumentException("Invalid value: " + value);
        }
    }

    /**
     * Get an integer value representation for this <code>AddressScreening</code> object.
     * @return an integer value representation for this <code>AddressScreening</code> object.
     */
    public int toInt() {
        return value;
    }

    /**
     * Determine if this AddressScreening object represents the UNDEFINED address
     * screening value.
     * <p>
     * This method is effectively equivalent to the conditional test:
     * <code>(this == UNDEFINED)</code>, ie. the code:
     * <p>
     * <code>&nbsp;&nbsp;&nbsp;&nbsp;if (screening.isUndefined()) ...</code>
     * <p>
     * is interchangeable with the code:
     * <p>
     * <code>&nbsp;&nbsp;&nbsp;if (screening == AddressScreening.UNDEFINED) ...</code>
     * <p>
     * @return <code>true</code> if this object represents the UNDEFINED address screening
     *       value, <code>false</code> otherwise.
     */
    public boolean isUndefined() {
        return value == ADDRESS_SCREENING_UNDEFINED;
    }

    /**
     * Determine if this AddressScreening object represents the USER_NOT_VERIFIED address
     * screening value.
     * <p>
     * This method is effectively equivalent to the conditional test:
     * <code>(this == USER_NOT_VERIFIED)</code>, ie. the code:
     * <p>
     * <code>&nbsp;&nbsp;&nbsp;&nbsp;if (screening.isUserNotVerified()) ...</code>
     * <p>
     * is interchangeable with the code:
     * <p>
     * <code>&nbsp;&nbsp;&nbsp;if (screening == AddressScreening.USER_NOT_VERIFIED) ...</code>
     * <p>
     * @return <code>true</code> if this object represents the USER_NOT_VERIFIED address screening
     *       value, <code>false</code> otherwise.
     */
    public boolean isUserNotVerified() {
        return value == ADDRESS_SCREENING_USER_NOT_VERIFIED;
    }

    /**
     * Determine if this AddressScreening object represents the USER_VERIFIED_PASSED address
     * screening value.
     * <p>
     * This method is effectively equivalent to the conditional test:
     * <code>(this == USER_VERIFIED_PASSED)</code>, ie. the code:
     * <p>
     * <code>&nbsp;&nbsp;&nbsp;&nbsp;if (screening.isUserVerifiedPassed()) ...</code>
     * <p>
     * is interchangeable with the code:
     * <p>
     * <code>&nbsp;&nbsp;&nbsp;if (screening == AddressScreening.USER_VERIFIED_PASSED) ...</code>
     * <p>
     * @return <code>true</code> if this object represents the USER_VERIFIED_PASSED address screening
     *       value, <code>false</code> otherwise.
     */
    public boolean isUserVerifiedPassed() {
        return value == ADDRESS_SCREENING_USER_VERIFIED_PASSED;
    }

    /**
     * Determine if this AddressScreening object represents the USER_VERIFIED_FAILED address
     * screening value.
     * <p>
     * This method is effectively equivalent to the conditional test:
     * <code>(this == USER_VERIFIED_FAILED)</code>, ie. the code:
     * <p>
     * <code>&nbsp;&nbsp;&nbsp;&nbsp;if (screening.isUserVerifiedFailed()) ...</code>
     * <p>
     * is interchangeable with the code:
     * <p>
     * <code>&nbsp;&nbsp;&nbsp;if (screening == AddressScreening.USER_VERIFIED_FAILED) ...</code>
     * <p>
     * @return <code>true</code> if this object represents the USER_VERIFIED_FAILED address screening
     *       value, <code>false</code> otherwise.
     */
    public boolean isUserVerifiedFailed() {
        return value == ADDRESS_SCREENING_USER_VERIFIED_FAILED ;
    }

    /**
     * Determine if this AddressScreening object represents the NETWORK address
     * screening value.
     * <p>
     * This method is effectively equivalent to the conditional test:
     * <code>(this == NETWORK)</code>, ie. the code:
     * <p>
     * <code>&nbsp;&nbsp;&nbsp;&nbsp;if (screening.isNetword()) ...</code>
     * <p>
     * is interchangeable with the code:
     * <p>
     * <code>&nbsp;&nbsp;&nbsp;if (screening == AddressScreening.NETWORK) ...</code>
     * <p>
     * @return <code>true</code> if this object represents the NETWORK address screening
     *       value, <code>false</code> otherwise.
     */
    public boolean isNetwork() {
        return value == ADDRESS_SCREENING_NETWORK;
    }

    /**
     * Compare this address screening for equality with another.
     * @param obj the object to compare this with.
     * @return <code>true</code> if <code>obj</code> is an instance of this class
     *        representing the same address screening value as this,
     *        <code>false</code> otherwise.
     */
    public boolean equals(Object obj) {
        if (obj == this) return true;

        return (obj instanceof AddressScreening)
            && ((AddressScreening)obj).value == value;
    }

    /**
     * Get a hash code value for this address screening.
     * @return a hash code value.
     */
    public int hashCode() {
        return value;
    }

    /**
     * Get the textual representation of the address screening object.
     * @return the textual representation of the address screening object.
     */
    public String toString() {
        switch (value) {
            case ADDRESS_SCREENING_UNDEFINED: return "Undefined";
            case ADDRESS_SCREENING_USER_NOT_VERIFIED: return "User Not Verified";
            case ADDRESS_SCREENING_USER_VERIFIED_PASSED: return "User Verified Passed";
            case ADDRESS_SCREENING_USER_VERIFIED_FAILED: return "User Verified Failed";
            case ADDRESS_SCREENING_NETWORK: return "Network";
            default: return "Address Screening in Unknown and Invalid State";
        }
    }

    /**
     * Private constructor to prevent unauthorised object creation.
     */
    private AddressScreening(int value) {
        this.value = value;
    }

    /**
     * Resolve deserialisation references so that the singleton property of each
     * enumerated object is preserved.
     */
    private Object readResolve() throws StreamCorruptedException {
        if (value == ADDRESS_SCREENING_UNDEFINED) return UNDEFINED;
        if (value == ADDRESS_SCREENING_USER_NOT_VERIFIED) return USER_NOT_VERIFIED;
        if (value == ADDRESS_SCREENING_USER_VERIFIED_PASSED) return USER_VERIFIED_PASSED;
        if (value == ADDRESS_SCREENING_USER_VERIFIED_FAILED) return USER_VERIFIED_FAILED;
        if (value == ADDRESS_SCREENING_NETWORK) return NETWORK;
        throw new StreamCorruptedException("Invalid internal state found");
    }


    private final int value;
}

