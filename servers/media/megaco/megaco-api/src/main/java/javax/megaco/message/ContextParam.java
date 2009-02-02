package javax.megaco.message;

import java.io.Serializable;

import javax.megaco.ExceptionInfoCode;
import javax.megaco.InvalidArgumentException;
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

	public ContextParam() {

	}

	public boolean isEMPresent() {
		return this.isEMPresent;
	}

	public TopologyDescriptor[] getTopologyDescriptor() {
		return this.topologyDescriptors;

	}

	public boolean isPriorityPresent() {
		return this.isPriorityPresent;
	}

	public int getPriority() throws javax.megaco.ParameterNotSetException {
		if (!this.isPriorityPresent) {
			throw new ParameterNotSetException(
					"Priority not set for ContextParam");
		}
		return this.priority;
	}

	public void setEM(boolean isEMPresent) {
		this.isEMPresent = isEMPresent;
	}

	public void setPriority(int priority)
			throws javax.megaco.InvalidArgumentException {
		if (priority < 0 || priority > 15) {
			InvalidArgumentException invalidArgumentException = new InvalidArgumentException(
					"The priority for Contextparam should be between 0 and 15. The value passed is = "
							+ priority);

			invalidArgumentException
					.setInfoCode(ExceptionInfoCode.INV_PRIORITY_VAL);
			throw invalidArgumentException;
		}
		this.priority = priority;
	}

	public void setTopologyDescriptor(TopologyDescriptor topologyDescriptor[])
			throws javax.megaco.InvalidArgumentException {
		if (topologyDescriptor == null) {
			InvalidArgumentException invalidArgumentException = new InvalidArgumentException(
					"The TopologyDescriptor[] cannot be null for ContextParam");
			throw invalidArgumentException;
		}
		this.topologyDescriptors = topologyDescriptor;
	}

}
