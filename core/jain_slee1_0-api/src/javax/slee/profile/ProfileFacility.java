package javax.slee.profile;

import java.util.Collection;
import javax.slee.TransactionRolledbackLocalException;
import javax.slee.facilities.FacilityException;

/**
 * The Profile Facility allows SBB entities to interrogate the profile database to find
 * profiles that match a selection criteria.
 * <p>
 * <dl>
 *   <dt><b>SBB JNDI Location:</b>
 *   <dd><code>java:comp/env/slee/facilities/profile</code>
 * </dl>
 */
public interface ProfileFacility {
    /**
     * Get a collection of <code>ProfileID</code> objects that identify all the profiles
     * contained in the specified profile table.  The collection returned is immutable.  Any
     * attempt to modify it, either directly or indirectly, will result in a
     * <code>java.lang.UnsupportedOperationException</code> being thrown.
     * <p>
     * <i>Note:</i> A profile identifier for the profile table's default profile will not be
     * included in the collection returned by this method as the default profile has no such
     * identifier.
     * <p>
     * This method is a required transactional method.
     * @param profileTableName the name of the profile table.
     * @return a read-only collection of {@link ProfileID} objects identifying the profiles
     *        contained in the specified profile table.
     * @throws NullPointerException if <code>profileTableName</code> is <code>null</code>.
     * @throws UnrecognizedProfileTableNameException if a profile table with the specified
     *        name does not exist.
     * @throws TransactionRolledbackLocalException if this method was invoked without
     *        a valid transaction context and the transaction started by this method
     *        failed to commit.
     * @throws FacilityException if the iterator could not be obtained due to a system-level failure.
     */
    public Collection getProfiles(String profileTableName)
        throws NullPointerException, UnrecognizedProfileTableNameException,
               TransactionRolledbackLocalException, FacilityException;

    /**
     * Get a collection of <code>ProfileID</code> objects that identify the profiles contained
     * in the specified profile table where the specified profile attribute is set to the specified value.
     * The collection returned is immutable.  Any attempt to modify it, either directly or indirectly,
     * will result in a <code>java.lang.UnsupportedOperationException</code> being thrown.
     * <p>
     * <i>Note:</i> The profile table's default profile is not considered when determining
     * matching profiles as it has no profile identifier that can be included in the collection
     * returned by this method.
     * <p>
     * This method is a required transactional method.
     * @param profileTableName the name of the profile table.
     * @param attributeName the name of the profile's attribute to check.
     * @param attributeValue the value to compare the attribute with.
     * @return a read-only collection of {@link ProfileID} objects identifying the profiles
     *        contained in the specified profile table, where the specified attribute of each profile
     *        equals the specified value.
     * @throws NullPointerException if any argument is <code>null</code>.
     * @throws UnrecognizedProfileTableNameException if a profile table with the specified
     *        name does not exist.
     * @throws UnrecognizedAttributeException if an attribute with the specified name is
     *        not defined in the profile specification for the specified profile table.
     * @throws AttributeNotIndexedException if the specified attribute is not indexed
     *        in the profile specification for the specified profile table.
     * @throws AttributeTypeMismatchException if the type of the supplied attribute value does not
     *        match the type of the specified indexed attribute.
     * @throws TransactionRolledbackLocalException if this method was invoked without
     *        a valid transaction context and the transaction started by this method
     *        failed to commit.
     * @throws FacilityException if the iterator could not be obtained due to a system-level failure.
     */
    public Collection getProfilesByIndexedAttribute(String profileTableName, String attributeName, Object attributeValue)
        throws NullPointerException, UnrecognizedProfileTableNameException,
               UnrecognizedAttributeException, AttributeNotIndexedException,
               AttributeTypeMismatchException, TransactionRolledbackLocalException,
               FacilityException;

    /**
     * Get a <code>ProfileID</code> object that identifies the profile contained in the specified
     * profile table, where the specified profile attribute is set to the specified value.
     * <p>
     * <i>Note:</i> The profile table's default profile is not considered when determining
     * matching profiles as it has no profile identifier that can be returned by this method.
     * <p>
     * This method is a required transactional method.
     * @param profileTableName the name of the profile table.
     * @param attributeName the name of the profile's attribute to check.
     * @param attributeValue the value to compare the attribute with.
     * @return the {@link ProfileID profile identifier} for the first matching profile, or
     *        <code>null</code> if no matching profile was found.
     * @throws NullPointerException if any attribute is <code>null</code>.
     * @throws UnrecognizedProfileTableNameException if a profile table with the specified
     *        name does not exist.
     * @throws UnrecognizedAttributeException if an attribute with the specified name is
     *        not defined in the profile specification for the specified profile table.
     * @throws AttributeNotIndexedException if the specified attribute is not indexed
     *        in the profile specification for the specified profile table.
     * @throws AttributeTypeMismatchException if the type of the supplied attribute value does not
     *        match the type of the specified indexed attribute.
     * @throws TransactionRolledbackLocalException if this method was invoked without
     *        a valid transaction context and the transaction started by this method
     *        failed to commit.
     * @throws FacilityException if the profile identifier could not be obtained due to a system-level failure.
     */
    public ProfileID getProfileByIndexedAttribute(String profileTableName, String attributeName, Object attributeValue)
        throws NullPointerException, UnrecognizedProfileTableNameException,
               UnrecognizedAttributeException, AttributeNotIndexedException,
               AttributeTypeMismatchException, TransactionRolledbackLocalException,
               FacilityException;

    /**
     * Get a <code>ProfileTableActivity</code> object for a profile table.
     * <p>
     * This method is a required transactional method.
     * @param profileTableName the name of the profile table.
     * @return a {@link ProfileTableActivity} object for the profile table.
     * @throws NullPointerException if <code>profileTableName</code> is <code>null</code>.
     * @throws UnrecognizedProfileTableNameException if a profile table with the specified
     *        name does not exist.
     * @throws TransactionRolledbackLocalException if this method was invoked without
     *        a valid transaction context and the transaction started by this method
     *        failed to commit.
     * @throws FacilityException if the activity could not be obtained due to a system-level failure.
     */
    public ProfileTableActivity getProfileTableActivity(String profileTableName)
        throws NullPointerException, UnrecognizedProfileTableNameException,
               TransactionRolledbackLocalException, FacilityException;
}

