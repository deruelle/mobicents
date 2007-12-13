package javax.slee.management;

import javax.slee.facilities.Level;
import javax.management.Notification;
import javax.management.NotificationFilter;

/**
 * A notification filter that filters {@link AlarmNotification}s based on their
 * alarm level.  Only alarm notifications of the specified level or greater are
 * be allowed through this filter.
 * <p>
 * Notifications that are not instances of {@link AlarmNotification} are suppressed
 * by this filter.
 */
public class AlarmLevelFilter implements NotificationFilter {
    /**
     * Create an <code>AlarmLevelFilter</code>.
     * @param minLevel this minimum alarm level of alarm notifications allowed
     *        through this filter.
     */
    public AlarmLevelFilter(Level minLevel) {
        this.minLevel = minLevel;
    }

    /**
     * Determine whether the specified notification should be delivered to notification
     * listeners using this notification filter.
     * @param notification the notification to be sent.
     * @return <code>true</code> if the notification should be delivered to notification
     *        listeners, <code>false</code> otherwise.  This method always returns
     *        <code>false</code> if <code>notification</code> is not an instance of
     *        {@link AlarmNotification}.
     */
    public boolean isNotificationEnabled(Notification notification) {
        if (!(notification instanceof AlarmNotification)) return false;

        return !minLevel.isHigherLevel(((AlarmNotification)notification).getLevel());
    }


    private final Level minLevel;
}

