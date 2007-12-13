package javax.slee;

/**
 * This exception indicates a failure in the underlying SLEE container.
 */
public class SLEEException extends RuntimeException {
    /**
     * Create a <code>SLEEException</code> with a detail message.
     * @param message the detail message.
     */
    public SLEEException(String message) {
        this(message, null);
    }

    /**
     * Create a <code>SLEEException</code> with a detail message and cause.
     * @param message the detail message.
     * @param cause the reason this exception was thrown.
     */
    public SLEEException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    /**
     * Get the cause (if any) for this exception.
     * @return the cause.
     */
    public Throwable getCause() {
        return cause;
    }


    private final Throwable cause;
}

