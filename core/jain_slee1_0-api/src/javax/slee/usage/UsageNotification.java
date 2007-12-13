package javax.slee.usage;

import javax.slee.SbbID;
import javax.slee.ServiceID;
import javax.management.Notification;

/**
 * This notification is emitted by an {@link SbbUsageMBean} object to indicate a
 * counter-type usage parameter has been updated or a sample-type usage parameter has
 * accumulated a new sample.
 * <p>
 * The notification type of all usage notifications is specified by the
 * {@link SbbUsageMBean#USAGE_NOTIFICATION_TYPE} attribute.
 */
public class UsageNotification extends Notification {
    /**
     * Create a <code>UsageNotification</code> containing the updated value of an
     * SBB's usage parameter.
     * @param notificationSource the <code>SbbUsageMBean</code> object that is
     *        emitting this notification.
     * @param serviceID the component identifier of the Service whose SBB's usage
     *        parameter was updated.
     * @param sbbID the component identifier of the SBB whose usage parameter was updated.
     * @param paramSet the name of the SBB usage parameter set containing the usage parameter
     *        that was updated.  If the unamed usage parameter set was updated, this value
     *        is <code>null</code>.
     * @param paramName the name of the usage parameter that was updated.
     * @param value this is either the new value of the usage parameter (for counter-type
     *        usage parameters), or a sample value (for sample-type usage parameters).
     * @param sequenceNumber the notification sequence number within the source
     *        <code>SbbUsageMBean</code>.
     * @param timeStamp the time (in ms since January 1, 1970 UTC) that the notification
     *        was generated.
     * @throws NullPointerException if <code>notificationSource</code>, <code>serviceID</code>,
     *        <code>sbbID</code> or <code>paramName</code> is <code>null</code>.
     */
    public UsageNotification(SbbUsageMBean notificationSource, ServiceID serviceID, SbbID sbbID, String paramSet, String paramName, boolean counter, long value, long sequenceNumber, long timeStamp) throws NullPointerException {
        super(SbbUsageMBean.USAGE_NOTIFICATION_TYPE, notificationSource, sequenceNumber, timeStamp,
              "Usage parameter \"" + paramName + "\" updated" + (paramSet != null ? " in usage parameter set " + paramSet : ""));

        if (notificationSource == null) throw new NullPointerException("notificationSource is null");
        if (serviceID == null) throw new NullPointerException("serviceID is null");
        if (sbbID == null) throw new NullPointerException("sbbID is null");
        if (paramName == null) throw new NullPointerException("paramName is null");

        this.serviceID = serviceID;
        this.sbbID = sbbID;
        this.paramSet = paramSet;
        this.paramName = paramName;
        this.counter = counter;
        this.value = value;
    }

    /**
     * Get the component identifier of the Service whose SBB's usage parameter was
     * updated.
     * @return the component identifier of the Service whose SBB's usage parameter was
     * updated.
     */
    public final ServiceID getService() {
        return serviceID;
    }

    /**
     * Get the component identifier of the SBB whose usage parameter was updated.
     * @return the component identifier of the SBB whose usage parameter was updated.
     */
    public final SbbID getSbb() {
        return sbbID;
    }

    /**
     * Get the name of the usage parameter set containing the usage parameter thas was updated.
     * @return the name of the usage parameter set, or <code>null</code> if the usage parameter
     *        was a member of the unnamed usage parameter set for the SBB.
     */
    public final String getUsageParameterSetName() {
        return paramSet;
    }

    /**
     * Get the name of the usage parameter that was updated.
     * @return the name of the usage parameter that was updated.
     */
    public final String getUsageParameterName() {
        return paramName;
    }

    /**
     * Determine if the usage parameter updated is counter-type or sample-type.
     * @return <code>true</code> if the usage parameter updated is counter-type,
     *         <code>false</code> if the usage parameter updated is sample-type.
     */
    public final boolean isCounter() {
        return counter;
    }

    /**
     * Get the updated value or emitted sample of the usage parameter.  If {@link #isCounter}
     * returns <code>true</code> this value is the updated value of the counter-type usage
     * parameter.  If {@link #isCounter} returns <code>false</code> this value is a sample
     * value for the usage parameter.
     * @return the updated value or emitted sample of the usage parameter.
     */
    public final long getValue() {
        return value;
    }

    /**
     * Compare this notification for equality with another object.
     * @param obj the object to compare this with.
     * @return <code>true</code> if <code>obj</code> is an instance of this class and the
     *        Service identifier, SBB identifier, usage parameter set name, and usage parameter
     *        name attributes of <code>obj</code> are the same as the corresponding attributes
     *        of <code>this</code>.
     */
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof UsageNotification)) return false;

        UsageNotification that = (UsageNotification)obj;
        return (this.serviceID.equals(that.serviceID))
            && (this.sbbID.equals(that.sbbID))
            && (this.paramSet == null ? that.paramSet == null : this.paramSet.equals(that.paramSet))
            && (this.paramName.equals(that.paramName))
            && (this.counter == counter);
    }

    /**
     * Get a hash code value for this notification.  The hash code is the logical
     * XOR of the hash codes of the Service identifier, the SBB identifier, and the
     * usage parameter name.
     * @return a hash code for this notification.
     */
    public int hashCode() {
        return serviceID.hashCode() ^ sbbID.hashCode() ^ paramName.hashCode();
    }

    /**
     * Get a string representation for this notification.
     * @return a string representation for this notification.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("UsageNotification[timestamp=")
            .append(getTimeStamp())
            .append(",service=")
            .append(serviceID)
            .append(",sbb=")
            .append(sbbID)
            .append(",paramSet=")
            .append(paramSet)
            .append(",param=")
            .append(paramName)
            .append(",counter=")
            .append(counter)
            .append(",value=")
            .append(value)
            .append("]");
        return buf.toString();
    }


    private final ServiceID serviceID;
    private final SbbID sbbID;
    private final String paramSet;
    private final String paramName;
    private final boolean counter;
    private final long value;
}
