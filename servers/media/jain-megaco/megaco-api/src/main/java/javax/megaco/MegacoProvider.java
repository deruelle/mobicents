package javax.megaco;

/**
 * This interface must be implemented by any class that represents the JAIN
 * MEGACO abstraction of an MEGACO stack and that interacts directly with a
 * proprietary (vendor-specific) implementation of that MEGACO stack.
 * 
 */
public interface MegacoProvider {

	public int addMegacoListener(MegacoListener listener, UserId UserId)
			throws java.util.TooManyListenersException,
			javax.megaco.InvalidUserIdException,
			javax.megaco.AssocHandleExhaustedException,
			IllegalArgumentException;

	public MegacoStack getMegacoStack();

	public int getAssocHandle(UserId UserId)
			throws javax.megaco.NonExistentAssocException;

	public void removeMegacoListener(MegacoListener listener, int assocHandle)
			throws IllegalArgumentException;

	public void sendMegacoAssocEvent(AssociationEvent jainMegacoAssocEvent);

	public int sendMegacoCmdEvent(CommandEvent jainMegacoCmdEvent);
}
