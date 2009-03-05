package javax.megaco.message.descriptor;

import java.io.Serializable;


import javax.megaco.message.Descriptor;
import javax.megaco.message.DescriptorType;

/**
 * The class extends JAIN MEGACO Descriptor. This class describes the event
 * buffer descriptor.
 */
public class EventBufferDescriptor extends Descriptor implements Serializable {

	private EventBufParam[] eventBufParam;

	/**
	 * Constructs a Event Buffer Descriptor with the vector of event buffer
	 * parameter. The event buffer parameters gives the package information, the
	 * attached stream id and the parameter name and value for the events
	 * buffered.
	 * 
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the reference of vector of
	 *             Event Buffer Param passed to this method is NULL.
	 */
	public EventBufferDescriptor(EventBufParam[] evtBufParam) throws IllegalArgumentException {
		super();

		if (evtBufParam == null) {
			throw new IllegalArgumentException("EventBufParam[] must not be null");
		}

		if (evtBufParam.length == 0) {
			throw new IllegalArgumentException("EventBufParam[] must not be empty");
		}
		this.eventBufParam = evtBufParam;

		super.descriptorId = DescriptorType.M_EVENT_BUF_DESC;
	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * returns that the descriptor identifier is of type Event Buffer
	 * descriptor. This method overrides the corresponding method of the base
	 * class Descriptor.
	 * 
	 * @return Returns an integer value that identifies this object as the type
	 *         of event buffer descriptor. It returns that it is Event Buffer
	 *         Descriptor i.e., M_EVENT_BUF_DESC.
	 */
	public int getDescriptorId() {
		return super.descriptorId;
	}

	/**
	 * The method can be used to get the pkdgName, eventother and the event
	 * stream in the event buffer descriptor for multiple events. This method
	 * gives the package information, the attached stream id and the parameter
	 * name and value for the multiple events.
	 * 
	 * @return bufParam - The vector of object reference for the event buffer
	 *         parameter that contains the package name, item name, pramaeter
	 *         name, parameter value and the stream id.
	 */
	public EventBufParam[] getEventBufParam() {
		return this.eventBufParam;
	}

}
