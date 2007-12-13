package javax.slee.management;

import javax.slee.SbbID;

/**
 * This interface provides access to deployment-specific attributes that
 * describe an installed Service.
 */
public interface ServiceDescriptor extends ComponentDescriptor {
    /**
     * Get the component identifier of the Service's root SBB.
     * @return the component identifier of the Service's root SBB.
     */
    public SbbID getRootSbb();

    /**
     * Get the name of the Address Profile Table used by the Service.
     * @return the name of the Address Profile Table used by the Service, or
     *        <code>null</code> if the Service does not use one.
     */
    public String getAddressProfileTable();

    /**
     * Get the name of the Resource Info Profile Table used by the Service.
     * @return the name of the Resource Info Profile Table used by the Service, or
     *        <code>null</code> if the Service does not use one.
     */
    public String getResourceInfoProfileTable();

}

