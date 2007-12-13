package javax.slee.profile;

import javax.slee.ActivityContextInterface;
import javax.slee.FactoryException;
import javax.slee.UnrecognizedActivityException;
import javax.slee.TransactionRequiredLocalException;

/**
 * The Profile Table Activity Context Interface Factory is used by SBBs to obtain
 * an <code>ActivityContextInterface</code> object for a profile table activity.
 * <p>
 * <dl>
 *   <dt><b>SBB JNDI Location:</b>
 *   <dd><code>java:comp/env/slee/facilities/profiletableactivitycontextinterfacefactory</code>
 * </dl>
 */
public interface ProfileTableActivityContextInterfaceFactory {
    /**
     * Get an <code>ActivityContextInterface</code> object for the profile table activity.
     * <p>
     * This method is a mandatory transactional method.
     * @param activity the profile table activity.
     * @return an <code>ActivityContextInterface</code> object that encapsulates the
     *        profile table activity.
     * @throws NullPointerException if <code>activity</code> is <code>null</code>.
     * @throws TransactionRequiredLocalException if this method is invoked without a valid transaction
     *        context.
     * @throws UnrecognizedActivityException if <code>activity</code> is not a
     *        valid profile table activity created by the SLEE.
     * @throws FactoryException if the <code>ActivityContextInterface</code> object
     *        could not be created due to a system-level failure.
     */
    public ActivityContextInterface getActivityContextInterface(ProfileTableActivity activity)
        throws NullPointerException, TransactionRequiredLocalException, UnrecognizedActivityException,
               FactoryException;

}
