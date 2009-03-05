package javax.megaco.message.descriptor;


import javax.megaco.ParameterNotSetException;
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
	private RequestedEventParam[] requestedEventParam = null;

	/**
	 * Constructs a Event Descriptor with specific request identifier.
	 * 
	 * @param requestId
	 *            requestId - An integer value specifying the request
	 *            identifier, which uniquely identifies the event.
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the value of request identifier
	 *             passed to this method is less than 0.
	 */
	public EventDescriptor(int requestId) throws IllegalArgumentException {
		if (requestId < 0) {
			IllegalArgumentException invalidArgumentException = new IllegalArgumentException("requestId cannot be less than 0 for EventDescriptor");

			// TODO set ExceptionInfoCode ?
			throw invalidArgumentException;
		}
		this.requestId = requestId;
		super.descriptorId = DescriptorType.M_EVENT_DESC;
	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * returns that the descriptor identifier is of type descriptor Event. This
	 * method overrides the corresponding method of the base class Descriptor.
	 */
	@Override
	public final int getDescriptorId() {
		return super.descriptorId;
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

	/**
	 * This method sets the Requested Event Params field of the event
	 * descriptor.
	 * 
	 * @param requestedParam
	 *            - Sets the requested params. There can be multiple requested
	 *            parameters set, but atleaset one should be present.
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the reference of request event
	 *             params as passed to this method is NULL.
	 */
	public final void setRequestedEventParam(RequestedEventParam[] requestedParam) throws IllegalArgumentException {

		// FIXME: add zero length check?
		if (requestedParam == null) {

			throw new IllegalArgumentException();
		}

		this.requestedEventParam = requestedParam;
	}

	/**
	 * This method gets the Request Events Params field of the event descriptor.
	 * This method returns vector of objects of type RequsetedEventParams.
	 * 
	 * @return Returns the vector of the request event params. If requested
	 *         event parameter has not been set for the event descriptor, then
	 *         this method would return NULL.

	 */
	public final RequestedEventParam[] getRequestedEventParam() {

		return this.requestedEventParam;
	}

}
