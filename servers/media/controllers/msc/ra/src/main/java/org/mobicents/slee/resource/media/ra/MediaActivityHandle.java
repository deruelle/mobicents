package org.mobicents.slee.resource.media.ra;

import java.io.Serializable;

import javax.slee.resource.ActivityHandle;

/**
 * 
 * @author amit bhayani
 * 
 */
public class MediaActivityHandle implements Serializable, ActivityHandle {

	private static final long serialVersionUID = 3573219377986401628L;
	private final String id;

	public MediaActivityHandle(String id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass() == this.getClass()) {
			return ((MediaActivityHandle) obj).id.equals(this.id);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "MediaActivityHandle(id=" + id + ")";
	}

}
