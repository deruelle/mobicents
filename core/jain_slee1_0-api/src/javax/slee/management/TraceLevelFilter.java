package javax.slee.management;

import javax.slee.facilities.Level;
import javax.management.Notification;
import javax.management.NotificationFilter;

/**
 * A notification filter that filters {@link TraceNotification}s based on their
 * trace level.  Only trace notifications of the specified level or greater are
 * be allowed through this filter.
 * <p>
 * Notifications that are not instances of {@link TraceNotification} are suppressed
 * by this filter.
 */
public class TraceLevelFilter implements NotificationFilter {
    /**
     * Create a <code>TraceLevelFilter</code>.
     * @param minLevel this minimum trace level of trace notifications allowed
     *        through this filter.
     */
    public TraceLevelFilter(Level minLevel) {
        this.minLevel = minLevel;
    }

    /**
     * Determine whether the specified notification should be delivered to notification
     * listeners using this notification filter.
     * @param notification the notification to be sent.
     * @return <code>true</code> if the notification should be delivered to notification
     *        listeners, <code>false</code> otherwise.  This method always returns
     *        <code>false</code> if <code>notification</code> is not an instance of
     *        {@link TraceNotification}.
     */
    public boolean isNotificationEnabled(Notification notification) {
        if (!(notification instanceof TraceNotification)) return false;

        return !minLevel.isHigherLevel(((TraceNotification)notification).getLevel());
    }


    private final Level minLevel;
}

