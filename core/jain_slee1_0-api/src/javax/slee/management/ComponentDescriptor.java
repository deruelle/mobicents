package javax.slee.management;

import javax.slee.ComponentID;

/**
 * This interface is the common base interface for the interfaces
 * that define the attributes of a component descriptor.
 * The <code>ComponentDescriptor</code> interface is the common base interface
 * for all other component descriptor interfaces.  All deployable components
 * installed in the SLEE have a descriptor containing deployment-related
 * information, specified as a derivitive interface of <code>ComponentDescriptor</code>.
 */
public interface ComponentDescriptor {
    /**
     * Get the identifier of the deployable unit from which this component was installed.
     * @return the identifier of the deployable unit from which this component was installed.
     */
    public DeployableUnitID getDeployableUnit();

    /**
     * Get the name of the source object from which this component was installed.
     * For services, this is the name of the deployment descriptor XML file
     * specified in the respective <tt>&lt;service-xml&gt;</tt> element of the
     * enclosing deployable unit's deployment descriptor.  For components installed
     * from a component jar file, the source is the name of the component jar file
     * as specified in the respective <tt>&lt;jar&gt;</tt> element of the enclosing
     * deployable unit's deployment descriptor.
     * @return the name of the source object from where this component was installed.
     */
    public String getSource();

    /**
     * Get the component identifier for this descriptor.
     * @return the component identifier for this descriptor.
     */
    public ComponentID getID();

    /**
     * Get the name of the component.
     * @return the name of the component.
     */
    public String getName();

    /**
     * Get the vendor of the component.
     * @return the vendor of the component.
     */
    public String getVendor();

    /**
     * Get the version of the component.
     * @return the version of the component.
     */
    public String getVersion();
}

