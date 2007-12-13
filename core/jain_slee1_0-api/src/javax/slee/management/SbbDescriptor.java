package javax.slee.management;

import javax.slee.SbbID;
import javax.slee.EventTypeID;
import javax.slee.profile.ProfileSpecificationID;
import javax.slee.resource.ResourceAdaptorTypeID;

/**
 * This interface provides access to deployment-specific attributes that
 * describe an installed SBB.
 */
public interface SbbDescriptor extends ComponentDescriptor {
    /**
     * Get the component identifiers of the SBBs used by this SBB.  These SBBs may
     * be child SBBs or stored in CMP fields of this SBB.
     * @return the component identifiers of the SBBs used by this SBB.
     */
    public SbbID[] getSbbs();

    /**
     * Get the component identifiers of the event types used by this SBB.
     * @return the component identifiers of the event types used by this SBB.
     */
    public EventTypeID[] getEventTypes();

    /**
     * Get the component identifiers of the profile specifications used by this SBB.
     * @return the component identifiers of the profile specifications used by this SBB.
     */
    public ProfileSpecificationID[] getProfileSpecifications();

    /**
     * Get the component identifier of the profile specification used for Address
     * Profiles for this SBB.
     * @return the component identifier of the Address Profile profile specification
     *        used by this SBB, or <code>null</code> if the SBB does not use Address
     *        Profiles.
     */
    public ProfileSpecificationID getAddressProfileSpecification();

    /**
     * Get the component identifiers of the resource adaptor types used by this SBB.
     * @return the component identifiers of the resource adaptor types used by this SBB.
     */
    public ResourceAdaptorTypeID[] getResourceAdaptorTypes();

    /**
     * Get the names of the resource adaptor entity links used by this SBB.
     * @return the names of the resource adaptor entity links used by this SBB.
     */
    public String[] getResourceAdaptorEntityLinks();
}

