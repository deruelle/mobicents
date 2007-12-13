package javax.slee.nullactivity;

import javax.slee.ActivityContextInterface;
import javax.slee.FactoryException;
import javax.slee.UnrecognizedActivityException;
import javax.slee.TransactionRequiredLocalException;

/**
 * The Null Activity Context Interface Factory is used by SBBs to obtain
 * an <code>ActivityContextInterface</code> object for a null activity.
 * <p>
 * <dl>
 *   <dt><b>SBB JNDI Location:</b>
 *   <dd><code>java:comp/env/slee/nullactivity/activitycontextinterfacefactory</code>
 * </dl>
 *
 * @see NullActivity
 * @see NullActivityFactory
 */
public interface NullActivityContextInterfaceFactory {
    /**
     * Get an <code>ActivityContextInterface</code> object for a null activity.
     * <p>
     * This method is a mandatory transactional method.
     * @param activity the null activity.
     * @return an <code>ActivityContextInterface</code> object that encapsulates the
     *        null activity.
     * @throws NullPointerException if <code>activity</code> is <code>null</code>.
     * @throws TransactionRequiredLocalException if this method is invoked without a valid transaction
     *        context.
     * @throws UnrecognizedActivityException if <code>activity</code> is not a
     *        valid null activity created by the SLEE.
     * @throws FactoryException if the <code>ActivityContextInterface</code> object
     *        could not be created due to a system-level failure.
     */
    public ActivityContextInterface getActivityContextInterface(NullActivity activity)
        throws NullPointerException, TransactionRequiredLocalException, UnrecognizedActivityException,
               FactoryException;

}

