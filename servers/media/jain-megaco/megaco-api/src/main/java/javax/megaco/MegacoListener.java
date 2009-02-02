package javax.megaco;

import java.util.EventListener;

/**
 * This interface defines the methods that must be implemented by any MEGACO
 * user application to receive and process JAIN MEGACO Events emitted by an
 * object that implements the MegacoProvider interface. Separate methods for
 * handling JAIN MEGACO Command Event Objects and JAIN MEGACO Association Event
 * Objects are specified. An instance of a class that implements this interface
 * must register with an instance of a class that implements the MegacoProvider
 * interface to receive these Event Objects.
 * 
 * 
 * 
 */
public interface MegacoListener extends EventListener {

	/**
	 * Processes a MEGACO Command event received from a MegacoProvider. Command
	 * level event occurs when the MEGACO stack receives a command from the
	 * peer. The stack after parsing the command parameters passes the command
	 * to the application through this listener interface.
	 * 
	 * @param cmdEvent
	 *            The JAIN MEGACO Command Event Object that is to be processed.
	 */
	public void processMegacoEvent(CommandEvent cmdEvent);

	/**
	 * Processes a MEGACO Association Event received from a MegacoProvider.
	 * Association level event occurs whenever stack wants to send the response
	 * of a request from the listener application or when the stack wants to
	 * give indication of the change in the state of the association at the
	 * stack.
	 * 
	 * 
	 * @param assocEvent
	 *            The JAIN MEGACO Association Event Object that is to be
	 *            processed.
	 */
	public void processMegacoEvent(AssociationEvent assocEvent);
}
