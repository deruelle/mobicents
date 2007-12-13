package javax.slee.management;

import java.util.Date;
import javax.slee.ComponentID;

/**
 * This interface provides access to deployment-specific attributes that
 * describe an installed deployable unit.
 */
public interface DeployableUnitDescriptor {
    /**
     * Get the URL that the deployable unit was installed from.
     * @return the URL that the deployable unit was installed from.
     */
    public String getURL();

    /**
     * Get the date that the deployable unit was installed.
     * @return the date that the deployable unit was installed.
     */
    public Date getDeploymentDate();

    /**
     * Get the component identifiers of the components installed with this
     * deployable unit.
     * @return the component identifiers of the components installed with
     *        this deployable unit.
     */
    public ComponentID[] getComponents();
}

