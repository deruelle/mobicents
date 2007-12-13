package javax.slee.management;

import java.util.Collection;
import javax.slee.InvalidArgumentException;
import javax.slee.profile.ProfileID;
import javax.slee.profile.ProfileSpecificationID;
import javax.slee.profile.AttributeNotIndexedException;
import javax.slee.profile.AttributeTypeMismatchException;
import javax.slee.profile.ProfileAlreadyExistsException;
import javax.slee.profile.ProfileTableAlreadyExistsException;
import javax.slee.profile.UnrecognizedProfileTableNameException;
import javax.slee.profile.UnrecognizedProfileNameException;
import javax.slee.profile.UnrecognizedAttributeException;
import javax.slee.profile.UnrecognizedProfileSpecificationException;
import javax.management.ObjectName;

/**
 * The <code>ProfileProvisiningMBean</code> interface defines management operations
 * for creating, removing, and interacting with profiles and profile tables.
 * <p>
 * The Object Name of a <code>ProfileProvisioningMBean</code> object can be obtained by
 * a management client via the {@link SleeManagementMBean#getProfileProvisioningMBean()}
 * method.
 */
public interface ProfileProvisioningMBean {
    /**
     * Create a new profile table from a profile specification.
     * @param id the component identifier of the profile specification that the
     *        profile table should be created from.
     * @param newProfileTableName the name of the profile table to create.  The name
     *        cannot include the '<tt>/</tt>' character.
     * @throws NullPointerException if <code>newProfileTableName</code> is <code>null</code>.
     * @throws UnrecognizedProfileSpecificationException if <code>id</code> is not a
     *        recognizable <code>ProfileSpecificationID</code> for the SLEE or it does
     *        not correspond with a profile specification installed in the SLEE.
     * @throws InvalidArgumentException if <code>newProfileTableName</code> is zero-length
     *        or contains a '<tt>/</tt>' character.
     * @throws ProfileTableAlreadyExistsException if a profile table with the same
     *        name already exists.
     * @throws ManagementException if the profile table could not be created due to
     *        a system-level failure.
     */
    public void createProfileTable(ProfileSpecificationID id, String newProfileTableName)
        throws NullPointerException, UnrecognizedProfileSpecificationException,
               InvalidArgumentException, ProfileTableAlreadyExistsException,
               ManagementException;

    /**
     * Remove a profile table.
     * @param profileTableName the name of the profile table to remove.
     * @throws NullPointerException if <code>profileTableName<code> is <code>null</code>.
     * @throws UnrecognizedProfileTableNameException if a profile table with the specified
     *        name does not exist.
     * @throws ManagementException if the profile table could not be removed due to
     *        a system-level failure.
     */
    public void removeProfileTable(String profileTableName)
        throws NullPointerException, UnrecognizedProfileTableNameException, ManagementException;

    /**
     * Get the component identifier of the profile specification that a profile table
     * was created with.
     * @param profileTableName the name of the profile table.
     * @return the component identifier of the profile specification that the profile
     *        table was created with.
     * @throws NullPointerException if <code>profileTableName<code> is <code>null</code>.
     * @throws UnrecognizedProfileTableNameException if a profile table with the specified
     *        name does not exist.
     * @throws ManagementException if the component identifier could not be obtained
     *        due to a system-level failure.
     */
    public ProfileSpecificationID getProfileSpecification(String profileTableName)
        throws NullPointerException, UnrecognizedProfileTableNameException, ManagementException;

    /**
     * Rename a profile table.
     * @param oldProfileTableName the name of the profile table to rename.
     * @param newProfileTableName the new name for the profile table.
     * @throws NullPointerException if either argument is <code>null</code>.
     * @throws UnrecognizedProfileTableNameException if a profile table with the name
     *        <code>oldProfileTableName</code> does not exist.
     * @throws InvalidArgumentException if <code>newProfileTableName</code> is zero-length
     *        or contains a '<tt>/</tt>' character.
     * @throws ProfileTableAlreadyExistsException if a profile table with the same
     *        name as <code>newProfileTableName</code> already exists.
     * @throws ManagementException if the profile table could not be renamed due to
     *        a system-level failure.
     */
    public void renameProfileTable(String oldProfileTableName, String newProfileTableName)
        throws NullPointerException, UnrecognizedProfileTableNameException,
               InvalidArgumentException, ProfileTableAlreadyExistsException,
               ManagementException;

    /**
     * Get the JMX Object Name of the default profile for a profile table.  Every profile
     * table has one default profile.  New profiles created in a profile table obtain
     * their intial values from the default profile.
     * @param profileTableName the name of the profile table.
     * @return the Object Name of the default profile for the specified profile table.
     * @throws NullPointerException if <code>profileTableName</code> is <code>null</code>.
     * @throws UnrecognizedProfileTableNameException if a profile table with the specified
     *        name does not exist.
     * @throws ManagementException if the Object Name could not be obtained due to a
     *        system-level failure.
     */
    public ObjectName getDefaultProfile(String profileTableName)
        throws NullPointerException, UnrecognizedProfileTableNameException, ManagementException;

    /**
     * Create a new profile with the specified name in the specified profile table.  The
     * <code>ObjectName</code> returned by this method provides the management client with
     * the name of a Profile MBean for the created profile.  This Profile MBean is in the
     * read-write state allowing the management client a chance to configure the initial
     * values for the profile attributes before it is added to the profile table.  The new
     * profile is not visible in the profile table until the Profile MBean state is committed.
     * @param profileTableName the name of the profile table to create the profile in.
     * @param newProfileName the name of the new profile.  The name must be unique
     *        within the scope of the profile table.
     * @return the Object Name of the new profile.
     * @throws NullPointerException if either argument is <code>null</code>.
     * @throws UnrecognizedProfileTableNameException if a profile table with the specified
     *        name does not exist.
     * @throws InvalidArgumentException if <code>newProfileName</code> is zero-length.
     * @throws ProfileAlreadyExistsException if a profile with the same name already
     *        exists in the profile table.
     * @throws ManagementException if the profile could not be created due to a
     *        system-level failure.
     */
    public ObjectName createProfile(String profileTableName, String newProfileName)
        throws NullPointerException, UnrecognizedProfileTableNameException,
               InvalidArgumentException, ProfileAlreadyExistsException,
               ManagementException;

    /**
     * Remove a profile from a profile table.
     * @param profileTableName the name of the profile table to remove the profile from.
     * @param profileName the name of the profile to remove.
     * @throws NullPointerException if either argument is <code>null</code>.
     * @throws UnrecognizedProfileTableNameException if a profile table with the specified
     *        name does not exist.
     * @throws UnrecognizedProfileNameException if a profile with the specified name
     *        does not exist in the profile table.
     * @throws ManagementException if the profile could not be removed due to a
     *        system-level failure.
     */
    public void removeProfile(String profileTableName, String profileName)
        throws NullPointerException, UnrecognizedProfileTableNameException,
               UnrecognizedProfileNameException, ManagementException;

    /**
     * Get the JMX Object Name of a profile.
     * @param profileTableName the name of the profile table to obtain the profile from.
     * @param profileName the name of the profile.
     * @return the Object Name of the profile.
     * @throws NullPointerException if either argument is <code>null</code>.
     * @throws UnrecognizedProfileTableNameException if a profile table with the specified
     *        name does not exist.
     * @throws UnrecognizedProfileNameException if a profile with the specified name
     *        does not exist in the profile table.
     * @throws ManagementException if the profile could not be obtained due to a
     *        system-level failure.
     */
    public ObjectName getProfile(String profileTableName, String profileName)
        throws NullPointerException, UnrecognizedProfileTableNameException,
               UnrecognizedProfileNameException, ManagementException;

    /**
     * Get a collection of <code>java.lang.String</code> objects that identify the names
     * of all the profile tables that have been created in the SLEE.
     * @return a collection of <code>java.lang.String</code> objects identifying the names
     *        of all the profile tables that have been created in the SLEE.
     * @throws ManagementException if the MBean object could not be obtained due to a
     *        system-level failure.
     */
    public Collection getProfileTables()
        throws ManagementException;

    /**
     * Get a collection of <code>ProfileID</code> objects that identify all the profiles
     * contained in a specified profile table.  
     * <p>
     * <i>Note:</i> A profile identifier for the profile table's default profile will not be
     * included in the collection returned by this method as the default profile has no such
     * identifier.
     * @param profileTableName the name of the profile table.
     * @return a collection of {@link ProfileID} objects identifying the profiles
     *        contained in the specified profile table.
     * @throws NullPointerException if <code>profileTableName</code> is <code>null</code>.
     * @throws UnrecognizedProfileTableNameException if a profile table with the specified
     *        name does not exist.
     * @throws ManagementException if the MBean object could not be obtained due to a
     *        system-level failure.
     */
    public Collection getProfiles(String profileTableName)
        throws NullPointerException, UnrecognizedProfileTableNameException, ManagementException;

    /**
     * Get a collection of <code>ProfileID</code> objects that identify the profiles contained
     * in a specified profile table where a specified profile attribute is set to a specified value.
     * <p>
     * <i>Note:</i> The profile table's default profile is not considered when determining
     * matching profiles as it has no profile identifier that can be included in the collection
     * returned by this method.
     * @param profileTableName the name of the profile table.
     * @param attributeName the name of the profile's attribute to check.
     * @param attributeValue the value to compare the attribute with.
     * @return a collection of {@link ProfileID} objects identifying the profiles contained
     *        in the specified profile table, where the specified attribute of each profile
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
     * @throws ManagementException if the MBean object could not be obtained due to a
     *        system-level failure.
     */
    public Collection getProfilesByIndexedAttribute(String profileTableName, String attributeName, Object attributeValue)
        throws NullPointerException, UnrecognizedProfileTableNameException,
               UnrecognizedAttributeException, AttributeNotIndexedException,
               AttributeTypeMismatchException, ManagementException;

}

