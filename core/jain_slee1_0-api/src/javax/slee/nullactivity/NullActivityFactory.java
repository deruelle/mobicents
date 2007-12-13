package javax.slee.nullactivity;

import javax.slee.FactoryException;
import javax.slee.TransactionRequiredLocalException;

/**
 * The Null Activity Factory is used by SBBs to create new null activities.
 * <p>
 * <dl>
 *   <dt><b>SBB JNDI Location:</b>
 *   <dd><code>java:comp/env/slee/nullactivity/factory</code>
 * </dl>
 *
 * @see NullActivity
 * @see NullActivityContextInterfaceFactory
 */
public interface NullActivityFactory {
    /**
     * Create a new null activity.
     * <p>
     * This method is a mandatory transactional method.  The null activity will
     * only be created if the transaction invoking this method commits successfully.
     * @throws TransactionRequiredLocalException if this method is invoked without a valid transaction
     *        context.
     * @throws FactoryException if the null activity could not be created due to a
     *       system-level failure.
     */
    public NullActivity createNullActivity()
        throws TransactionRequiredLocalException, FactoryException;
}

