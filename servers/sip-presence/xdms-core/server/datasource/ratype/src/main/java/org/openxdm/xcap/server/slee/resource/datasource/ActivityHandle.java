package org.openxdm.xcap.server.slee.resource.datasource;

/**
 * Activity handle for the DataSource Resource Adaptor.
 * 
 * @author Eduardo Martins
 * 
 */

public class ActivityHandle implements javax.slee.resource.ActivityHandle {
	
	private final String id;

	public ActivityHandle(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public int hashCode() {
		return id.hashCode();
	}

	public boolean equals(Object obj) {
		if (obj != null && obj.getClass() == this.getClass()) {
			ActivityHandle other = (ActivityHandle) obj;
			return this.id
					.equals(other.id);
		}
		return false;
	}

	public String toString() {
		return new StringBuilder("ActivityHandle[id="
				+ id+ "]").toString();
	}
}