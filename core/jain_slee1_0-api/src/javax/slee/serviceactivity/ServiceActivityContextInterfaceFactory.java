package javax.slee.serviceactivity;

import javax.slee.ActivityContextInterface;
import javax.slee.FactoryException;
import javax.slee.UnrecognizedActivityException;
import javax.slee.TransactionRequiredLocalException;

/**
 * The Service Activity Context Interface Factory is used by SBBs to obtain
 * an <code>ActivityContextInterface</code> object for a Service activity.
 * <p>
 * <dl>
 *   <dt><b>SBB JNDI Location:</b>
 *   <dd><code>java:comp/env/slee/serviceactivity/activitycontextinterfacefactory</code>
 * </dl>
 *
 * @see ServiceActivity
 * @see ServiceActivityFactory
 */
public interface ServiceActivityContextInterfaceFactory {
    /**
     * Get an <code>ActivityContextInterface</code> object for a Service activity.
     * <p>
     * This method is a mandatory transactional method.
     * @param activity the Service activity.
     * @return an <code>ActivityContextInterface</code> object that encapsulates the
     *        Service activity.
     * @throws NullPointerException if <code>activity</code> is <code>null</code>.
     * @throws TransactionRequiredLocalException if this method is invoked without a
     *        valid transaction context.
     * @throws UnrecognizedActivityException if <code>activity</code> is not a
     *        valid Service activity created by the SLEE.
     * @throws FactoryException if the <code>ActivityContextInterface</code> object
     *        could not be created due to a system-level failure.
     */
    public ActivityContextInterface getActivityContextInterface(ServiceActivity activity)
        throws NullPointerException, TransactionRequiredLocalException,
               UnrecognizedActivityException, FactoryException;
}

