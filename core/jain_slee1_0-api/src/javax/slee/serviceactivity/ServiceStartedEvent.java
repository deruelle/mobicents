package javax.slee.serviceactivity;

/**
 * This interface is implemented by Service started events genererated by
 * the SLEE when a Service is started.  A Service starts at the moment both
 * of the following two transitions have occurred:
 * <ul>
 *   <li>the SLEE transitions to the Running state
 *   <li>the Service transitions to the Active state.
 * </ul>
 * <p>
 * The event type name of Service started events is
 * "<code>javax.slee.serviceactivity.ServiceStartedEvent</code>".
 */
public interface ServiceStartedEvent {}

