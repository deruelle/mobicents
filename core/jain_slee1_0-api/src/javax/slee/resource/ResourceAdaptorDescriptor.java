package javax.slee.resource;

import javax.slee.management.ComponentDescriptor;

/**
 * This interface provides access to deployment-specific attributes that
 * describe an installed resource adaptor.
 */
public interface ResourceAdaptorDescriptor extends ComponentDescriptor {
    /**
     * Get the component identifier of the resource adaptor type that this
     * resource adaptor implements.
     * @return the component identifier of the resource adaptor type that this
     *        resource adaptor implements.
     */
    public ResourceAdaptorTypeID getResourceAdaptorType();

}

