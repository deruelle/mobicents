package javax.slee;

/**
 * This exception is thrown by methods in <code>SbbContext</code> if an SBB entity
 * attempts to obtain or modify the event mask for an activity it is not attached to.
 */
public class NotAttachedException extends Exception {
    /**
     * Create a <code>NotAttachedException</code> with no detail message.
     */
    public NotAttachedException() {}

    /**
     * Create a <code>NotAttachedException</code> with a detail message.
     * @param message the detail message.
     */
    public NotAttachedException(String message) {
        super(message);
    }
}

