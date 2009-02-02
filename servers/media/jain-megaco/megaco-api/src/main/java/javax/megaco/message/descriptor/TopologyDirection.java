package javax.megaco.message.descriptor;

import java.io.Serializable;

/**
 * Topology direction constants used in package javax.megaco.message.descriptor.
 * This class defines the topology direction for the megaco package.
 * 
 */
public class TopologyDirection implements Serializable {
	public static final int M_TOPO_DIR_BOTHWAY = 0;
	public static final int M_TOPO_DIR_ISOLATE = 1;
	public static final int M_TOPO_DIR_ONEWAY = 2;

	public static final TopologyDirection TOPO_DIR_BOTHWAY = new TopologyDirection(
			M_TOPO_DIR_BOTHWAY);

	public static final TopologyDirection TOPO_DIR_ISOLATE = new TopologyDirection(
			M_TOPO_DIR_ISOLATE);

	public static final TopologyDirection TOPO_DIR_ONEWAY = new TopologyDirection(
			M_TOPO_DIR_ONEWAY);

	private int topology_direction;

	private TopologyDirection(int topology_direction) {
		this.topology_direction = topology_direction;
	}
	
	public int getTopologyDirection(){
		return this.topology_direction;
	}

	public static final TopologyDirection getObject(int value)
			throws IllegalArgumentException {
		TopologyDirection t = null;
		switch (value) {
		case M_TOPO_DIR_BOTHWAY:
			t = TOPO_DIR_BOTHWAY;
			break;
		case M_TOPO_DIR_ISOLATE:
			t = TOPO_DIR_ISOLATE;
			break;
		case M_TOPO_DIR_ONEWAY:
			t = TOPO_DIR_ONEWAY;
			break;

		default:
			throw new IllegalArgumentException(
					"No TopologyDirection for value = " + value);
		}

		return t;
	}

	private Object readResolve() {
		return this.getObject(this.topology_direction);
	}
}
