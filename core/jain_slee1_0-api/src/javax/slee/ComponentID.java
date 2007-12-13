package javax.slee;

/**
 * The <code>ComponentID</code> interface is the common base interface
 * for all other component identifier interfaces.  All deployable components
 * installed in the SLEE have some component identity, specified as a derivitive
 * interface of <code>ComponentID</code>.
 */
public interface ComponentID {
    /**
     * Compare this component identifier for equality with another object.
     * @param obj the object to compare this with.
     * @return <code>true</code> if <code>obj</code> is a component
     *        identifier that has the same component type and identity
     *        as this, <code>false</code> otherwise.
     * @see Object#equals(Object)
     */
    public boolean equals(Object obj);

    /**
     * Get a hash code value for this component identifier.
     * @return a hash code value for this identifier.
     * @see Object#hashCode()
     */
    public int hashCode();

    /**
     * Get a string representation for this component identifier.
     * @return a string representation for this identifier.
     * @see Object#toString()
     */
    public String toString();
}

