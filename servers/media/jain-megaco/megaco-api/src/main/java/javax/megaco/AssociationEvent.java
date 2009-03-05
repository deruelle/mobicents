package javax.megaco;

import java.util.EventObject;

public abstract class AssociationEvent extends EventObject {

	private int assocHandle;

	/**
	 * Constructs a Association Event object. This is an abstract class and can
	 * be called only by the derived classes.
	 * 
	 * @param source
	 *            A reference to the object, the "source", that is logically
	 *            deemed to be the object upon which the Event in question
	 *            initially occurred.
	 * @param assocHandle
	 *            The association handle to uniquely identify the MG-MGC pair.
	 *            This is allocated by the stack when the Listener registers
	 *            with the provider with a unique MG-MGC identity.
	 * @throws IllegalArgumentException
	 *             if the parameters send in the constructor are invalid.
	 */
	public AssociationEvent(java.lang.Object source, int assocHandle)
			throws IllegalArgumentException {
		super(source);
		this.assocHandle = assocHandle;
	}

	/**
	 * Gets the Association Handle for which this association level event is to
	 * be sent. This is required by the stack for each association level event
	 * from the application to the stack, for stack to identify to which remote
	 * address the command has to be sent and also from which of the local
	 * transport it needs to be sent. This is allocated by the stack when
	 * Listener registers with the provider.
	 * 
	 * @return Reference to the association handle which uniquely identifies the
	 *         MG-MGC pair.
	 */
	public int getAssocHandle() {
		return this.assocHandle;
	}

	/**
	 * This is a virtual method and shall be defined in the derived classes. See
	 * javax.megaco.association.AssocEventType for the definition of the
	 * constants for the Association events. This is not set in this object but
	 * is retrieved from the derived classes. Hence all derived classes need to
	 * implement this method.
	 * 
	 * @return Returns an integer value that identifies this event object as a
	 *         association creation/ deletion/ modification request event or
	 *         association creation or deletion or modification response event
	 *         or association indication event.
	 */
	public abstract int getAssocOperIdentifier();
	//FIXME: should this be object ?
}
