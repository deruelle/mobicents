/**
 * Start time:11:16:47 2009-09-01<br>
 * Project: mobicents-jainslee-server-core<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.ftf.resource;

import org.jboss.cache.Fqn;
import org.mobicents.ftf.FTFListener;

/**
 * Start time:11:16:47 2009-09-01<br>
 * Project: mobicents-jainslee-server-core<br>
 * This class is place holder for fqn of data we want to index/listen to.
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
public class ResourceGroupData implements Comparable<ResourceGroupData> {

	private int priority = 0;
	/**
	 * Fqn of data qwatched, NOTE: this must be create with string elements, since if there is object it wont match fqn passed from cache to listener !!!
	 */
	private Fqn dataFqn;
	private FTFListener callback;

	/**
	 * 	
	 */
	public ResourceGroupData() {
		// TODO Auto-generated constructor stub
	}

	public ResourceGroupData(int priority, Fqn dataFqn, FTFListener callback) {
		super();

		this.priority = priority;
		this.dataFqn = dataFqn;
		this.callback = callback;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public Fqn getDataFqn() {
		return dataFqn;
	}

	public void setDataFqn(Fqn dataFqn) {
		this.dataFqn = dataFqn;
	}

	public FTFListener getCallback() {
		return callback;
	}

	public void setCallback(FTFListener callback) {
		this.callback = callback;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(ResourceGroupData o) {
		if (o == null) {
			return 1;
		}

		if (o == this) {
			return 0;
		}
		if (this.dataFqn.equals(o.dataFqn)) {
			return 0;
		}
		return this.priority - o.priority;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataFqn == null) ? 0 : dataFqn.hashCode());
		result = prime * result + priority;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResourceGroupData other = (ResourceGroupData) obj;
		if (dataFqn == null) {
			if (other.dataFqn != null)
				return false;
		} else if (!dataFqn.equals(other.dataFqn))
			return false;
		if (priority != other.priority)
			return false;
		return true;
	}

}
