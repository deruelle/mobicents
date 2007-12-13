package javax.slee.serviceactivity;

/**
 * This interface is implemented by Service activity objects.  Service activities can
 * be used by SBBs to monitor the managed lifecycle of the Service they are a part of.
 * When a Service is started, a Service activity is created and a {@link ServiceStartedEvent}
 * is fired on the activity.  When a Service is stopped, the Service activity is ended
 * and a {@link javax.slee.ActivityEndEvent} is fired on the activity.
 * <p>
 * This interface is intentionally opaque to avoid exposing <code>ServiceID</code>
 * objects to SBB entities in foreign services, in the event such as SBB entity gets
 * a reference to a Service activity via a mechanism such as the Activity Context
 * Naming Facility.
 *
 * @see ServiceActivityFactory
 * @see ServiceActivityContextInterfaceFactory
 */
public interface ServiceActivity {}

