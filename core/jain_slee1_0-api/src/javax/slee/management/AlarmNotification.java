package javax.slee.management;

import javax.slee.facilities.Level;
import javax.management.Notification;

/**
 * This notification is emitted by an {@link AlarmMBean} object to indicate some
 * component or subsystem in the SLEE is experiencing a significant problem.
 * <p>
 * The notification type of all alarm notifications is specified by the
 * {@link AlarmMBean#ALARM_NOTIFICATION_TYPE} attribute.
 */
public class AlarmNotification extends Notification {
    /**
     * Create an <code>AlarmNotification</code> to notify listeners of a alarm.
     * @param notificationSource the <code>AlarmMBean</code> object that is emitting
     *        this notification.
     * @param alarmType the type of the alarm being generated. Typically a management client
     *        should be able to infer the type of the <code>alarmSource</code> object by
     *        inspecting this type.
     * @param alarmSource an object that identifies the object that generated the alarm, for
     *        example an {@link javax.slee.SbbID}.
     * @param alarmLevel the alarm level.
     * @param message the alarm message.
     * @param cause an optional cause for the alarm.
     * @param sequenceNumber the notification sequence number within the source
     *        <code>AlarmMBean</code> object.
     * @param timeStamp the time (in ms since January 1, 1970 UTC) that the alarm was generated.
     * @throws NullPointerException if <code>notificationSource</code>, <code>alarmType</code>,
     *        <code>alarmLevel</code>, or <code>message</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>alarmLevel == </code> {@link Level#OFF}.
     */
    public AlarmNotification(AlarmMBean notificationSource, String alarmType, Object alarmSource, Level alarmLevel, String message, Throwable cause, long sequenceNumber, long timeStamp) throws NullPointerException, IllegalArgumentException {
        super(AlarmMBean.ALARM_NOTIFICATION_TYPE, notificationSource, sequenceNumber, timeStamp, message);

        if (notificationSource == null) throw new NullPointerException("notificationSource is null");
        if (alarmType == null) throw new NullPointerException("alarmType is null");
        if (alarmLevel == null) throw new NullPointerException("alarmLevel is null");
        if (message == null) throw new NullPointerException("message is null");

        if (alarmLevel.isOff()) throw new IllegalArgumentException("alarmLevel cannot be Level.OFF");

        this.alarmType = alarmType;
        this.alarmSource = alarmSource;
        this.alarmLevel = alarmLevel;
        this.cause = cause;
    }

    /**
     * Get the type of the alarm.
     * @return the alarm type.
     */
    public final String getAlarmType() {
        return alarmType;
    }

    /**
     * Get the object that identifies the source of the alarm.
     * @return the alarm source.
     */
    public final Object getAlarmSource() {
        return alarmSource;
    }

    /**
     * Get the alarm level of the alarm.
     * @return the alarm level.
     */
    public final Level getLevel() {
        return alarmLevel;
    }

    /**
     * Get the cause (if any) for this alarm notification.
     * @return the cause for this alarm notification, or <code>null</code> if there wasn't a cause.
     */
    public final Throwable getCause() {
        return cause;
    }

    /**
     * Compare this notification for equality with another object.
     * @param obj the object to compare this with.
     * @return <code>true</code> if <code>obj</code> is an instance of this class and the
     *      alarm type, alarm source, alarm level and message attributes of <code>obj</code>
     *      are the same as the corresponding attributes of <code>this</code>.
     */
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof AlarmNotification)) return false;

        AlarmNotification that = (AlarmNotification)obj;
        return (this.alarmType.equals(that.alarmType))
            && (this.alarmSource.equals(that.alarmSource))
            && (this.alarmLevel.equals(that.alarmLevel))
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
        buf.append("AlarmNotification[timestamp=")
            .append(getTimeStamp())
            .append(",type=")
            .append(alarmType)
            .append(",source=")
            .append(alarmSource)
            .append(",level=")
            .append(alarmLevel)
            .append(",message=")
            .append(getMessage())
            .append(",cause=")
            .append(cause)
            .append("]");
        return buf.toString();
    }


    private final String alarmType;
    private final Object alarmSource;
    private final Level alarmLevel;
    private final Throwable cause;
}
