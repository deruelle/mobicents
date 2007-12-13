package javax.slee.management;

import javax.slee.facilities.Level;
import javax.management.Notification;

/**
 * This notification is emitted by a {@link TraceMBean} object when an installed
 * component generates a trace message at a level high enough not to be filtered
 * by the <code>TraceMBean</code>.
 * <p>
 * The notification type of all trace notifications is specified by the
 * {@link TraceMBean#TRACE_NOTIFICATION_TYPE} attribute.
 */
public class TraceNotification extends Notification {
    /**
     * Create a <code>TraceNotification</code> to notify listeners of a trace message.
     * @param notificationSource the <code>TraceMBean</code> object that is emitting
     *        this notification.
     * @param messageType the type of the trace message being generated and
     *        correspondingly the sub-type of the notification.
     * @param messageSource a component identifier that identifies the component that
     *        generated the trace message, for example an {@link javax.slee.SbbID}.
     * @param traceLevel the trace level.
     * @param message the trace message.
     * @param cause an optional cause for the trace message.
     * @param sequenceNumber the notification sequence number within the source
     *        <code>TraceMBean</code> object.
     * @param timeStamp the time (in ms since January 1, 1970 UTC) that the trace message
     *        was generated.
     * @throws NullPointerException if <code>notificationSource</code>, <code>messageType</code>,
     *        <code>traceLevel</code>, or <code>message</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>traceLevel == </code> {@link Level#OFF}.
     */
    public TraceNotification(TraceMBean notificationSource, String messageType, Object messageSource, Level traceLevel, String message, Throwable cause, long sequenceNumber, long timeStamp) throws NullPointerException, IllegalArgumentException {
        super(TraceMBean.TRACE_NOTIFICATION_TYPE, notificationSource, sequenceNumber, timeStamp, message);

        if (notificationSource == null) throw new NullPointerException("notificationSource is null");
        if (messageType == null) throw new NullPointerException("messageType is null");
        if (traceLevel == null) throw new NullPointerException("traceLevel is null");
        if (message == null) throw new NullPointerException("message is null");

        if (traceLevel.isOff()) throw new IllegalArgumentException("traceLevel cannot be Level.OFF");

        this.messageType = messageType;
        this.messageSource = messageSource;
        this.traceLevel = traceLevel;
        this.cause = cause;
    }

    /**
     * Get the type of the trace message.
     * @return the trace message type.
     */
    public final String getMessageType() {
        return messageType;
    }

    /**
     * Get the object that identifies the source of the trace message.
     * @return the trace message source.
     */
    public final Object getMessageSource() {
        return messageSource;
    }

    /**
     * Get the trace level of the trace message.
     * @return the trace level.
     */
    public final Level getLevel() {
        return traceLevel;
    }

    /**
     * Get the cause (if any) for this trace notification.
     * @return the cause for this trace notification, or <code>null</code> if there wasn't a cause.
     */
    public final Throwable getCause() {
        return cause;
    }

    /**
     * Compare this notification for equality with another object.
     * @param obj the object to compare this with.
     * @return <code>true</code> if <code>obj</code> is an instance of this class and the
     *      message type, message source, trace level and message attributes of <code>obj</code>
     *      are the same as the corresponding attributes of <code>this</code>.
     */
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof TraceNotification)) return false;

        TraceNotification that = (TraceNotification)obj;
        return (this.messageType.equals(that.messageType))
            && (this.messageSource.equals(that.messageSource))
            && (this.traceLevel.equals(that.traceLevel))
            && (this.getMessage().equals(that.getMessage()));
    }

    /**
     * Get a hash code value for this notification.  The hash code is the hash code
     * of the notification's message.
     * @return a hash code for this notification.
     */
    public int hashCode() {
        return getMessage().hashCode();
    }

    /**
     * Get a string representation for this notification.
     * @return a string representation for this notification.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("TraceNotification[timestamp=")
            .append(getTimeStamp())
            .append(",type=")
            .append(messageType)
            .append(",source=")
            .append(messageSource)
            .append(",level=")
            .append(traceLevel)
            .append(",message=")
            .append(getMessage())
            .append(",cause=")
            .append(cause)
            .append("]");
        return buf.toString();
    }


    private final String messageType;
    private final Object messageSource;
    private final Level traceLevel;
    private final Throwable cause;
}
