package javax.megaco.message.descriptor;

import javax.megaco.InvalidArgumentException;
import javax.megaco.message.Descriptor;
import javax.megaco.message.DescriptorType;
import javax.megaco.message.Termination;

public class TopologyDescriptor extends Descriptor {

	private Termination termA = null;
	private Termination termB = null;
	private TopologyDirection topologyDirect = null;

	public TopologyDescriptor(Termination termA, Termination termB,
			TopologyDirection topologyDirect)
			throws javax.megaco.InvalidArgumentException {
		if (termA == null || termB == null || topologyDirect == null) {
			InvalidArgumentException invalidArgumentException = new InvalidArgumentException(
					"None of Termination A,b and TopologyDirection can be null");
			throw invalidArgumentException;
		}

		this.termA = termA;
		this.termB = termB;
		this.topologyDirect = topologyDirect;
	}

	public final int getDescriptorId() {
		return DescriptorType.M_TOPOLOGY_DESC;
	}

	public Termination getTermA() {
		return this.termA;
	}

	public Termination getTermB() {
		return this.termB;
	}

	public int getTopologyDirection() {
		return this.topologyDirect.getTopologyDirection();
	}
}
