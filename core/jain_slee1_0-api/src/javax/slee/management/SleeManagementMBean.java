package javax.slee.management;

import javax.slee.InvalidStateException;
import javax.management.ObjectName;

/**
 * The <code>SleeManagementMBean</code> interface defines the central management
 * interface for the SLEE.  This interface provides access a management client with
 * the JMX Object Names of other SLEE management MBeans, and allows the operational
 * state of the SLEE to be changed.
 * <p>
 * <b>Notifications</b><br>
 * Every time the operational state of the SLEE changes, the <code>SleeManagementMBean</code>
 * object must emit a {@link SleeStateChangeNotification SleeStateChange} notification.
 * Therefore it is required that the <code>SleeManagementMBean</code> object implement the
 * <code>javax.management.NotificationBroadcaster</code> interface.
 */
public interface SleeManagementMBean {
    /**
     * The notification type of {@link SleeStateChangeNotification SleeStateChange}
     * notifications emitted by this MBean.  The notification type is equal to the
     * string "<code>javax.slee.management.sleestatechange</code>".
     */
    public static final String SLEE_STATE_CHANGE_NOTIFICATION_TYPE =
        "javax.slee.management.sleestatechange";


    /**
     * Get the current operational state of the SLEE.
     * @return a <code>SleeState</code> object that indicates the current operational
     *        state of the SLEE.
     * @throws ManagementException if the operatioanl state could not be determined
     *        due to a system-level failure.
     */
    public SleeState getState() throws ManagementException;

    /**
     * Request that the SLEE's event routing subsystem be started.  The SLEE
     * must be in the Stopped state, and transitions to the Starting state during
     * this method invocation.  The SLEE spontaneously moves out of the Starting
     * state when conditions dictate.
     * @throws InvalidStateException if the SLEE is not currently in the
     *       Stopped state.
     * @throws ManagementException if the operational state of the SLEE could not
     *       be changed due to a system-level failure.
     */
    public void start() throws InvalidStateException, ManagementException;

    /**
     * Request that the SLEE's event routing subsystem be stopped.  The SLEE
     * must be in the Running state, and transitions to the Stopping state during
     * this method invocation.  The SLEE spontaneously moves out of the Stopping
     * state when conditions dictate.
     * @throws InvalidStateException if the SLEE is not currently in the
     *       Running state.
     * @throws ManagementException if the operational state of the SLEE could not
     *       be changed due to a system-level failure.
     */
    public void stop() throws InvalidStateException, ManagementException;

    /**
     * Shutdown and terminate all SLEE processes related to this server image.
     * In a distributed SLEE all nodes should terminate in response to this request.
     * This method should never return, and does not cause the emission of a
     * {@link SleeStateChangeNotification SleeStateChange} notification.
     * @throws InvalidStateException if the SLEE is not currently in the
     *       Stopped state.
     * @throws ManagementException if the operational state of the SLEE could not
     *       be changed due to a system-level failure.
     */
    public void shutdown() throws InvalidStateException, ManagementException;

    /**
     * Get the JMX Object Name of the SLEE's {@link DeploymentMBean} object.
     * @return the Object Name of the <code>DeploymentMBean</code> object.
     */
    public ObjectName getDeploymentMBean();

    /**
     * Get the JMX Object Name of the SLEE's {@link ServiceManagementMBean} object.
     * @return the Object Name of the <code>ServiceManagementMBean</code> object.
     */
    public ObjectName getServiceManagementMBean();

    /**
     * Get the JMX Object Name of the SLEE's {@link ProfileProvisioningMBean} object.
     * @return the Object Name of the <code>ProfileProvisioningMBean</code> object.
     */
    public ObjectName getProfileProvisioningMBean();

    /**
     * Get the JMX Object Name of the SLEE's {@link TraceMBean} object.
     * @return the Object Name of the <code>TraceMBean</code> object.
     */
    public ObjectName getTraceMBean();

    /**
     * Get the JMX Object Name of the SLEE's {@link AlarmMBean} object.
     * @return the Object Name of the <code>AlarmMBean</code> object.
     */
    public ObjectName getAlarmMBean();

}

