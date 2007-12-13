package javax.slee.profile;

import javax.slee.management.ComponentDescriptor;

/**
 * This interface provides access to deployment-specific attributes that
 * describe an installed profile specification.
 */
public interface ProfileSpecificationDescriptor extends ComponentDescriptor {
    /**
     * Get the name of the profile specification's CMP interface.
     * @return the name of the profile specification's CMP interface.
     */
    public String getCMPInterfaceName();
}

