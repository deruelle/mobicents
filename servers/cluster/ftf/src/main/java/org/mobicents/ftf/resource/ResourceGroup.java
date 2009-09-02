/**
 * Start time:10:39:42 2009-09-01<br>
 * Project: mobicents-jainslee-server-core<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.ftf.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jboss.cache.Fqn;
import org.mobicents.ftf.FTFListener;
import org.mobicents.ftf.FTFReplicationListener;

/**
 * Start time:10:39:42 2009-09-01<br>
 * Project: mobicents-jainslee-server-core<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
public class ResourceGroup {

	private List<ResourceGroupData> registeredResources = new ArrayList<ResourceGroupData>();
	private Map<Fqn, ResourceGroupData> registeredFQNS = new HashMap<Fqn, ResourceGroupData>();
	private String resourceGroupName = "NOT-SET-RG-NAME";
	//Conveniant place to store this.
	private FTFReplicationListener replicationListener;

	public ResourceGroup(String resourceGroupName) {
		super();
		this.resourceGroupName = resourceGroupName;
	}

	
	public void setResource(ResourceGroupData rgd)
	{
		this.addResource(rgd);
	}

	public void addResource(String fqn, int priority, FTFListener callback) {
		Fqn _fqn = Fqn.fromString(fqn);
		ResourceGroupData rgd = new ResourceGroupData(priority, _fqn, callback);
		this.addResource(rgd);
	}
	public void addResource(ResourceGroupData rgd)
	{
		if (this.registeredResources.contains(rgd)) {
			throw new IllegalArgumentException("Fqn: " + rgd.getDataFqn() + " , is already present!");
		} else {
			this.orderResourceData(rgd);
			this.registeredFQNS.put(rgd.getDataFqn(), rgd);
		}
	}
	public void removeResource(String fqn) {
		Fqn _fqn = Fqn.fromString(fqn);
		if (this.registeredFQNS.containsKey(_fqn)) {
			ResourceGroupData data = this.registeredFQNS.remove(_fqn);
			this.registeredResources.remove(data);
		}
	}

	public boolean belongsToResourceGroup(Fqn fqn) {
		return this.registeredFQNS.containsKey(fqn);
	}

	public List<ResourceGroupData> getOrderedResources() {
		return new ArrayList<ResourceGroupData>(this.registeredResources);
	}

	public String getResourceGroupName() {
		return resourceGroupName;
	}
	
	public FTFReplicationListener getReplicationListener() {
		return replicationListener;
	}


	public void setReplicationListener(FTFReplicationListener replicationListener) {
		this.replicationListener = replicationListener;
	}


	public ResourceGroupData getData(Fqn fqn)
	{
		return this.registeredFQNS.get(fqn);
	}

	/**
	 * @param rgd
	 */
	private void orderResourceData(ResourceGroupData rgd) {
		
		int priority = rgd.getPriority();

		if (this.registeredResources.size() > 0) {
			for (int index = 0; index < this.registeredResources.size(); index++) {
				if (priority > this.registeredResources.get(index).getPriority()) {
					this.registeredResources.add(index, rgd);
				} else {
					continue;
				}
			}

			this.registeredResources.add(rgd);
		} else {
			this.registeredResources.add(rgd);
		}

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((resourceGroupName == null) ? 0 : resourceGroupName.hashCode());
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
		ResourceGroup other = (ResourceGroup) obj;
		if (resourceGroupName == null) {
			if (other.resourceGroupName != null)
				return false;
		} else if (!resourceGroupName.equals(other.resourceGroupName))
			return false;
		return true;
	}

	//FACTORY Method
	
	public static ResourceGroupData createResourceGroupData(String fqn, int priority, FTFListener callback)
	{

		Fqn _fqn = Fqn.fromString(fqn);
		ResourceGroupData rgd = new ResourceGroupData(priority, _fqn, callback);
		return rgd;
	}


	@Override
	public String toString() {
		
		return super.toString()+"["+this.getResourceGroupName()+"]";
	}
	
}
