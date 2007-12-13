package javax.slee.usage;

import javax.slee.SbbID;
import javax.slee.ServiceID;
import javax.slee.InvalidArgumentException;
import javax.management.Notification;
import javax.management.NotificationFilter;

/**
 * A notification filter that only allows through {@link UsageNotification}s where the
 * Service component identifier, SBB component identifier, and the usage parameter name
 * match specified values, and the value of the usage parameter contained in the
 * notification falls outside a specified range.
 * <p>
 * If the notification contains usage information for some other Service, SBB, or usage
 * parameter, or the value of the usage parameter falls within the specified range, the
 * notification is suppressed.
 * <p>
 * Notifications that are not instances of {@link UsageNotification} are suppressed
 * by this filter.
 */
public class UsageOutOfRangeFilter implements NotificationFilter {
    /**
     * Create a <code>UsageOutOfRangeFilter</code>.
     * @param service the component identifier of the Service whose usage parameter
     *        should be monitored.
     * @param sbb the component identifier of the SBB whose usage parameter should be
     *        monitored.
     * @param paramName the name of a usage parameter defined by the SBB.
     * @param lowValue the lower bound of the range.  Notifications are allowed through
     *        this filter if the value of the specified usage parameter is less than
     *        this value.
     * @param highValue the upper bound of the range.  Notifications are allowed through
     *        this filter if the value of the specified usage parameter is greater than
     *        this value.
     * @throws NullPointerException if <code>service</code>, <code>sbb</code>, or
     *        <code>paramName</code> is <code>null</code>.
     * @throws InvalidArgumentException if <code>highValue < lowValue</code>.
     */
    public UsageOutOfRangeFilter(ServiceID service, SbbID sbb, String paramName, long lowValue, long highValue) throws NullPointerException, InvalidArgumentException {
        if (service == null) throw new NullPointerException("service is null");
        if (sbb == null) throw new NullPointerException("sbb is null");
        if (paramName == null) throw new NullPointerException("paramName is null");

        if (highValue < lowValue) throw new InvalidArgumentException("highValue < lowValue");

        this.service = service;
        this.sbb = sbb;
        this.paramName = paramName;
        this.lowValue = lowValue;
        this.highValue = highValue;
    }

    /**
     * Determine whether the specified notification should be delivered to notification
     * listeners using this notification filter.
     * @param notification the notification to be sent.
     * @return <code>true</code> if the notification should be delivered to notification
     *        listeners, <code>false</code> otherwise.  This method always returns
     *        <code>false</code> if <code>notification</code> is not an instance of
     *        {@link UsageNotification}.
     */
    public boolean isNotificationEnabled(Notification notification) {
        if (!(notification instanceof UsageNotification)) return false;

        UsageNotification usageNotification = (UsageNotification)notification;
        return (usageNotification.getService().equals(service))
            && (usageNotification.getSbb().equals(sbb))
            && (usageNotification.getUsageParameterName().equals(paramName))
            && (usageNotification.getValue() < lowValue || usageNotification.getValue() > highValue);
    }


    private final ServiceID service;
    private final SbbID sbb;
    private final String paramName;
    private final long lowValue;
    private final long highValue;
}

