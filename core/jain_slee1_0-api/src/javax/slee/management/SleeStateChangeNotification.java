package javax.slee.management;

import javax.management.Notification;

/**
 * This notification is emitted by a {@link SleeManagementMBean} object to
 * indicate a change in the operational state of the SLEE.
 * <p>
 * The base type of all slee state change notifications is specified by the
 * {@link SleeManagementMBean#SLEE_STATE_CHANGE_NOTIFICATION_TYPE} attribute.
 */
public class SleeStateChangeNotification extends Notification {
    /**
     * Create a <code>SleeStateChangeNotification</code> to notify listeners of a
     * change in the operational state of the SLEE. Notifications are broadcast
     * <i>after</i> the SLEE has changed to the new state.
     * @param notificationSource the <code>SleeManagementMBean</code> object that is
     *        emitting this notification.
     * @param newState the new operational state of the SLEE.
     * @param oldState the old operational state of the SLEE.
     * @param sequenceNumber the notification sequence number within the source
     *        <code>SleeProviderMBean</code> object.
     * @throws NullPointerException if <code>notificationSource</code>, <code>newState</code>,
     *        or <code>oldState</code> is <code>null</code>.
     */
    public SleeStateChangeNotification(SleeManagementMBean notificationSource, SleeState newState, SleeState oldState, long sequenceNumber) throws NullPointerException {
        super(SleeManagementMBean.SLEE_STATE_CHANGE_NOTIFICATION_TYPE, notificationSource, sequenceNumber, System.currentTimeMillis(),
              "SLEE state changed from " + oldState + " to " + newState
             );

        if (notificationSource == null) throw new NullPointerException("notificationSource is null");
        if (newState == null) throw new NullPointerException("newState is null");
        if (oldState == null) throw new NullPointerException("oldState is null");

        this.newState = newState;
        this.oldState = oldState;
    }

    /**
     * Get the new operational state of the SLEE.
     * @return the new state.
     */
    public final SleeState getNewState() {
        return newState;
    }

    /**
     * Get the state the SLEE was in before the change to the new state.
     * @return the old state.
     */
    public final SleeState getOldState() {
        return oldState;
    }

    /**
     * Get a string representation for this notification.
     * @return a string representation for this notification.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("SleeStateChangeNotification[timestamp=")
            .append(getTimeStamp())
            .append(",oldState=")
            .append(oldState)
            .append(",newState=")
            .append(newState)
            .append("]");
        return buf.toString();
    }


    private final SleeState newState;
    private final SleeState oldState;
}

