package org.openxdm.xcap.server.slee.resource.datasource;

import java.util.concurrent.atomic.AtomicInteger;

public class ActivityObject {

	protected DataSourceResourceAdaptor ra;
	protected AtomicInteger timesCreated;
	protected final String id;
	
	protected ActivityObject(String id,DataSourceResourceAdaptor ra) {
		this.id = id;
		this.ra = ra;
		timesCreated = new AtomicInteger(1);
	}
	
	public int hashCode() {
		return id.hashCode();
	}

	public boolean equals(Object obj) {
		if (obj != null && obj.getClass() == this.getClass()) {
			ActivityObject other = (ActivityObject) obj;
			return this.id
					.equals(other.id);
		}
		return false;
	}
	
	protected void created() {
		timesCreated.incrementAndGet();
	}
	
	public void remove() {
		if (timesCreated.decrementAndGet() == 0) {
			ra.endActivity(new ActivityHandle(id));
		}
	}
	
}
