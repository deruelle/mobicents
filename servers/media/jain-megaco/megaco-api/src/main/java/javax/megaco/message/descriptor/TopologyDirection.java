package javax.megaco.message.descriptor;

import java.io.Serializable;

/**
 * Topology direction constants used in package javax.megaco.message.descriptor.
 * This class defines the topology direction for the megaco package.
 * 
 */
public class TopologyDirection implements Serializable {
	public static final int M_BOTHWAY = 0;
	public static final int M_ISOLATE = 1;
	public static final int M_ONEWAY = 2;

	public static final TopologyDirection BOTHWAY = new TopologyDirection(M_BOTHWAY);

	public static final TopologyDirection ISOLATE = new TopologyDirection(M_ISOLATE);

	public static final TopologyDirection ONEWAY = new TopologyDirection(M_ONEWAY);

	private int topology_direction;

	private TopologyDirection(int topology_direction) {
		this.topology_direction = topology_direction;
	}

	public int getTopologyDirection() {
		return this.topology_direction;
	}

	public static final TopologyDirection getObject(int value) throws IllegalArgumentException {
		TopologyDirection t = null;
		switch (value) {
		case M_BOTHWAY:
			t = BOTHWAY;
			break;
		case M_ISOLATE:
			t = ISOLATE;
			break;
		case M_ONEWAY:
			t = ONEWAY;
			break;

		default:
			throw new IllegalArgumentException("No TopologyDirection for value = " + value);
		}

		return t;
	}

	private Object readResolve() {
		return this.getObject(this.topology_direction);
	}

	@Override
	public String toString() {
		String t = null;
		switch (this.topology_direction) {
		case M_BOTHWAY:
			t = "TopologyDirection[BOTHWAY]";
			break;
		case M_ISOLATE:
			t = "TopologyDirection[ISOLATE]";
			break;
		case M_ONEWAY:
			t = "TopologyDirection[ONEWAY]";
			break;

		default:
			t = "TopologyDirection[" + this.topology_direction + "]";
		}

		return t;
	}

}
