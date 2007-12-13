package javax.slee.management;

/**
 * The <code>AlarmMBean</code> interface defines the management interface for the
 * {@link javax.slee.facilities.AlarmFacility} (and other vendor-specific alarm
 * generating interfaces).  No management operations are defined by the
 * <code>AlarmMBean</code> interface, instead it is used as a notification source
 * in the generation of {@link AlarmNotification Alarm} notifications.
 * <p>
 * The Object Name of a <code>AlarmMBean</code> object can be obtained by
 * a management client via the {@link SleeManagementMBean#getAlarmMBean}
 * method.
 * <p>
 * <b>Notifications</b><br>
 * Since <code>AlarmMBean</code> objects can emit {@link AlarmNotification Alarm}
 * notifications, it is required that that <code>AlarmMBean</code> object implement
 * the <code>javax.management.NotificationBroadcaster</code> interface.
 */
public interface AlarmMBean {
    /**
     * The notification type of {@link AlarmNotification Alarm} notifications
     * emitted by this MBean.  The notification type is equal to the string
     * "javax.slee.management.alarm".
     */
    public static final String ALARM_NOTIFICATION_TYPE = "javax.slee.management.alarm";

}
