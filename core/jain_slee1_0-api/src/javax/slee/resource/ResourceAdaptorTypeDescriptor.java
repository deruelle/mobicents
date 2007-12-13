package javax.slee.resource;

import javax.slee.EventTypeID;
import javax.slee.management.ComponentDescriptor;

/**
 * This interface provides access to deployment-specific attributes that
 * describe an installed resource adaptor type.
 */
public interface ResourceAdaptorTypeDescriptor extends ComponentDescriptor {
    /**
     * Get the component identifiers of the event types used by this resource adaptor type.
     * @return the component identifiers of the event types used by this resource adaptor type.
     */
    public EventTypeID[] getEventTypes();

}

