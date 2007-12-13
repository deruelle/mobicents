package javax.slee.profile;

import javax.slee.Address;

/**
 * This interface can be used as is or extended for profile specifications that will
 * be used for a Service's Address Profile Table.
 */
public interface AddressProfileCMP {
    /**
     * Get the addresses for this profile.
     * @return the addresses for this profile.
     */
    public Address[] getAddresses();

    /**
     * Set the addresses for this profile.
     * @param addresses the addresses for this profile.
     */
    public void setAddresses(Address[] addresses);

}

