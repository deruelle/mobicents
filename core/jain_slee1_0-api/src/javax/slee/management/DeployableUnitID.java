package javax.slee.management;

/**
 * The <code>DeployableUnitID</code> interface is a marker interface to
 * encapsulate the identity of deployable units installed in the SLEE.
 */
public interface DeployableUnitID {
    /**
     * Compare this deployable unit identifer for equality with another object.
     * @param obj the object to compare this with.
     * @return <code>true</code> if <code>obj</code> is a deployable unit
     *        identifier with the same identity as this, <code>false</code>
     *        otherwise.
     * @see Object#equals(Object)
     */
    public boolean equals(Object obj);

    /**
     * Get a hash code value for this deployable unit identifier.
     * @return a hash code value for this identifier.
     * @see Object#hashCode()
     */
    public int hashCode();

    /**
     * Get a string representation for this deployable unit identifier.
     * @return a string representation for this identifier.
     * @see Object#toString()
     */
    public String toString();
}

