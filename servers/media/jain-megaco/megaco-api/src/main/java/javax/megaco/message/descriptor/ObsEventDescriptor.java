package javax.megaco.message.descriptor;

import javax.megaco.message.Descriptor;

/**
 * 
 * The class extends JAIN MEGACO Descriptor. This class describes the observed
 * event descriptor.
 * 
 */
public class ObsEventDescriptor extends Descriptor {
	private int reqId;
	private ObservedEvent[] obsEvents = null;

	/**
	 * Constructs a ObservedEvent Descriptor with the requesteId corresponding
	 * to the request Id received in the event descriptor. It also sends the
	 * vector of object references to ObservedEvents that store the list of
	 * events that have been observed with the stream id on which they have been
	 * observed.
	 * 
	 * @param reqId
	 *            This specifies an request identifier specifying the events
	 *            that have been observed correspond to which event descriptor.
	 * @param obsEvents
	 *            This specifies a vector of object refernces for the observed
	 *            events.
	 * @throws javax.megaco.InvalidArgumentException
	 *             if the parameters set for observed events are imcompatible.
	 */
	public ObsEventDescriptor(int reqId, ObservedEvent[] obsEvents) throws javax.megaco.InvalidArgumentException {
		// TODO Throw InvalidArgumentException if the parameters set for
		// observed events are imcompatible.

		this.reqId = reqId;
		this.obsEvents = obsEvents;

	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * returns that the descriptor identifier is of type Observed Event
	 * descriptor. This method overrides the corresponding method of the base
	 * class Descriptor.
	 */
	@Override
	public final int getDescriptorId() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * This method retrieves the request identifier set in the observed event
	 * descriptor.
	 * 
	 * @return Returns the request id for the observed event descriptor.
	 */
	public int getRequestId() {
		return this.reqId;
	}

	/**
	 * This method vector of object references set in the observed event
	 * descriptor.
	 * 
	 * @return Returns the vector of observed event object references for the
	 *         observed event descriptor.
	 */
	public ObservedEvent[] getObservedEvent() {
		return this.obsEvents;
	}
}
