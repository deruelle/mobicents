package javax.megaco.message;

import java.io.Serializable;

import javax.megaco.ExceptionInfoCode;

import javax.megaco.ParameterNotSetException;
import javax.megaco.message.descriptor.TopologyDescriptor;

/**
 * The Context Parameter object is a class that shall be used to set the context
 * level parameters i.e., topology descriptor, priority, and whether emergency
 * is active or not. This is an independent class derived from java.util.Object
 * and shall not have any derived classes
 * 
 * 
 */
public class ContextParam implements Serializable {
	private boolean isEMPresent;
	private TopologyDescriptor[] topologyDescriptors = null;
	private boolean isPriorityPresent;
	private int priority;

	/**
	 * Constructs a class that shall specify the context level parameters.
	 */
	public ContextParam() {

	}

	/**
	 * The method can be used to check if emergency is present as a part of the
	 * context level parameters. By default, if the setEM() method has not been
	 * invoked, then this method should return FALSE to indicate that Emeregency
	 * is not present.
	 * 
	 * @return Returns true if Emergency is present.
	 */
	public boolean isEMPresent() {
		return this.isEMPresent;
	}

	/**
	 * Gets the object refernces of all the topology descriptors specified for
	 * this context.
	 * 
	 * @return The vector of the reference to the object identifier of type
	 *         topology descriptor information.
	 */
	public TopologyDescriptor[] getTopologyDescriptor() {
		return this.topologyDescriptors;

	}

	/**
	 * The method can be used to check if the prority value is specified for
	 * this context.
	 * 
	 * @return Returns true if priority is present. The function is called prior
	 *         to getPriority function to check if priroty is present.
	 */
	public boolean isPriorityPresent() {
		return this.isPriorityPresent;
	}

	/**
	 * The method can be used to get the prority value specified for this
	 * context. Before invoking this method, isPriorityPresent method must be
	 * invoked to verify the presence of priority field in the class object.
	 * 
	 * @return Returns the priority value specified for this context.
	 * @throws javax.megaco.ParameterNotSetException
	 *             : This exception is raised if the priority is not set and the
	 *             get method is invoked.
	 */
	public int getPriority() throws javax.megaco.ParameterNotSetException {
		if (!this.isPriorityPresent) {
			throw new ParameterNotSetException("Priority not set for ContextParam");
		}
		return this.priority;
	}

	/**
	 * The method can be used to set if the EM is present as a part of the
	 * context level parameters.
	 * 
	 * @param isEMPresent
	 *            - The boolean value to indicate if the emergency is present as
	 *            a part of the context level parameters.
	 */
	public void setEM(boolean isEMPresent) {
		this.isEMPresent = isEMPresent;
	}

	/**
	 * The method can be used to set the priority value for this context.
	 * 
	 * @param priority
	 *            priority - Integer value of the priority that shall be set.
	 *            The only valid values specified by the protocol for this
	 *            parameters is between 0 to 15.
	 * @throws IllegalArgumentException
	 *             : The valid values of priority ranges between 0 to 15. If any
	 *             value other than this is set then the exception is raised.
	 */
	public void setPriority(int priority) throws IllegalArgumentException {
		if (priority < 0 || priority > 15) {
			IllegalArgumentException invalidArgumentException = new IllegalArgumentException("The priority for Contextparam should be between 0 and 15. The value passed is = " + priority);

			// invalidArgumentException.setInfoCode(ExceptionInfoCode.INV_PRIORITY_VAL);
			throw invalidArgumentException;
		}
		this.priority = priority;
	}

	/**
	 * Sets the vector of Topology Descriptor Information for this command.
	 * 
	 * @param topologyDescriptor
	 *            The vector of reference to the object identifier of type
	 *            topology descriptor information
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the reference of Topology
	 *             Descriptor passed to this method is NULL.
	 */
	public void setTopologyDescriptor(TopologyDescriptor topologyDescriptor[]) throws IllegalArgumentException {
		if (topologyDescriptor == null) {
			IllegalArgumentException invalidArgumentException = new IllegalArgumentException("The TopologyDescriptor[] cannot be null for ContextParam");
			throw invalidArgumentException;
		}
		this.topologyDescriptors = topologyDescriptor;
	}

}
