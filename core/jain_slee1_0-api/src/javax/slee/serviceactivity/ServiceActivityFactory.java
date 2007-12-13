package javax.slee.serviceactivity;

import javax.slee.FactoryException;
import javax.slee.TransactionRequiredLocalException;

/**
 * The Service Activity Factory is used by SBBs to get their Service activity object.
 * <p>
 * <dl>
 *   <dt><b>SBB JNDI Location:</b>
 *   <dd><code>java:comp/env/slee/serviceactivity/factory</code>
 * </dl>
 *
 * @see ServiceActivity
 * @see ServiceActivityContextInterfaceFactory
 */
public interface ServiceActivityFactory {
    /**
     * Get the Service activity object for the invoking SBB.
     * <p>
     * As this method takes no arguments, it is the responsibility of the SLEE to
     * provide an appropriate implementation for the SBBs in each Service.
     * <p>
     * This method is a mandatory transactional method.
     * @throws TransactionRequiredLocalException if this method is invoked without a
     *        valid transaction context.
     * @throws FactoryException if the Service activity could not be obtained due to a
     *       system-level failure.
     */
    public ServiceActivity getActivity()
        throws TransactionRequiredLocalException, FactoryException;
}

