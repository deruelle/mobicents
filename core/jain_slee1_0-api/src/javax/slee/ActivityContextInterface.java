package javax.slee;

/**
 * The <code>ActivityContextInterface</code> interface provides an SBB object with
 * a Java interface to an Activity Context.
 * <p>
 * An SBB may define shareable state to be stored in the Activity Context by extending
 * the <code>ActivityContextInterface</code> interface and adding CMP-style accessor
 * methods for the state attributes.  State can be shared either between SBB objects
 * of the same SBB type, or between all SBB objects of any SBB type.  The configuration
 * of an SBB's Activity Context Interface is declared in the SBB's deployment descriptor.
 */
public interface ActivityContextInterface {
    /**
     * Get the Activity object for the activity encapsulated by this Activity Context.
     * <p>
     * This method is a mandatory transactional method.
     * @return the Activity object.
     * @throws TransactionRequiredLocalException if this method is invoked without a valid transaction
     *        context.
     * @throws SLEEException if the activity could not be obtained due to a system-level
     *        failure.
     */
    public Object getActivity()
        throws TransactionRequiredLocalException, SLEEException;

    /**
     * Attach an SBB entity to the Activity Context.  The SBB entity will subsequently
     * begin to receive events that it is interested in that occur on the underlying activity.
     * If the SBB has any <code>&lt;event&gt;</code> deployment descriptor elements
     * with the <code>mask-on-attach</code> attribute set to <tt>True</tt>, those events
     * will automatically be masked from the SBB entity until the event mask is explicitly
     * changed via the {@link SbbContext#maskEvent SbbContext.maskEvent} method.
     * <p>
     * If the specified SBB entity is already attached to the Activity Context, this method
     * has no effect.
     * <p>
     * This method is a mandatory transactional method.
     * @throws NullPointerException if <code>sbb</code> is <code>null</code>.
     * @throws TransactionRequiredLocalException if this method is invoked without a valid transaction
     *        context.
     * @throws TransactionRolledbackLocalException if <code>sbb</code> does not reference
     *        a valid SBB entity.
     * @throws SLEEException if the SBB entity could not be attached due to a system-level
     *        failure.
     */
    public void attach(SbbLocalObject sbb)
        throws NullPointerException, TransactionRequiredLocalException, TransactionRolledbackLocalException,
               SLEEException;

    /**
     * Detach an SBB entity from the Activity Context.  The SBB entity will no longer
     * receive any events that occur on the underlying activity.
     * <p>
     * If the specified SBB entity is not attached to the Activity Context, this method
     * has no effect.
     * <p>
     * This method is a mandatory transactional method.
     * @throws NullPointerException if <code>sbb</code> is <code>null</code>.
     * @throws TransactionRequiredLocalException if this method is invoked without a valid transaction
     *        context.
     * @throws TransactionRolledbackLocalException if <code>sbb</code> does not reference
     *        a valid SBB entity.
     * @throws SLEEException if the SBB entity could not be detached due to a system-level
     *        failure.
     */
    public void detach(SbbLocalObject sbb)
        throws NullPointerException, TransactionRequiredLocalException, TransactionRolledbackLocalException,
               SLEEException;

    /**
     * Determine if the activity context is in the ending state.  Events cannot be
     * fired by SBBs on and activity context that is in the ending state.
     * <p>
     * This method is a mandatory transactional method.
     * @return <code>true</code> if the activity context is in the ending state,
     *        <code>false</code> otherwise.
     * @throws TransactionRequiredLocalException if this method is invoked without a valid transaction
     *        context.
     * @throws SLEEException if the ending state of the activity context could not be
     *        determined due to a system-level failure.
     */
    public boolean isEnding()
        throws TransactionRequiredLocalException, SLEEException;

}

