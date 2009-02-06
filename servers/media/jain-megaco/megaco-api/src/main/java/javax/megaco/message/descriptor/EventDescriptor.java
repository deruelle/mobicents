package javax.megaco.message.descriptor;

import javax.megaco.InvalidArgumentException;
import javax.megaco.message.Descriptor;
import javax.megaco.message.DescriptorType;

/**
 * The class extends JAIN MEGACO Descriptor. This class describes the Event
 * descriptor of the MEGACO protocol.
 * 
 * 
 */
public class EventDescriptor extends Descriptor {

	private int requestId;

	/**
	 * Constructs a Event Descriptor with specific request identifier.
	 * 
	 * @param requestId
	 *            requestId - An integer value specifying the request
	 *            identifier, which uniquely identifies the event.
	 * @throws javax.megaco.InvalidArgumentException :
	 *             This exception is raised if the value of request identifier
	 *             passed to this method is less than 0.
	 */
	public EventDescriptor(int requestId)
			throws javax.megaco.InvalidArgumentException {
		if (requestId < 0) {
			InvalidArgumentException invalidArgumentException = new InvalidArgumentException(
					"requestId cannot be less than 0 for EventDescriptor");

			// TODO set ExceptionInfoCode ?
			throw invalidArgumentException;
		}
		this.requestId = requestId;
	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * returns that the descriptor identifier is of type descriptor Event. This
	 * method overrides the corresponding method of the base class Descriptor.
	 */
	@Override
	public final int getDescriptorId() {
		return DescriptorType.M_EVENT_DESC;
	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * returns an integer value identifying the RequestID field of the event
	 * descriptor.
	 * 
	 * @return Returns an integer value that identifies request identifier which
	 *         uniquely identifies the event descriptor.
	 */
	public final int getRequestId() {
		return this.requestId;
	}

	public final void setRequestedEventParam(
			RequestedEventParam[] requestedParam)
			throws javax.megaco.InvalidArgumentException {

	}

}
