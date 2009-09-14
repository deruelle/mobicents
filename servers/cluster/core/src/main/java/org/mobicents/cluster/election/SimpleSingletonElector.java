package org.mobicents.cluster.election;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jgroups.Address;

/**
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author martins
 */
public class SimpleSingletonElector implements SingletonElector {

	/* (non-Javadoc)
	 * @see org.mobicents.ftf.election.SingletonElector#elect(java.util.List)
	 */
	public Address elect(List<Address> list) {
		//FIXME: add something better
		Collections.sort(list, new Comparator<Address>(){

			public int compare(Address o1, Address o2) {
				if(o1 == null)
				{
					return -1;
				}
				if(o2 == null)
				{
					return 1;
				}
				
				if(o1 == o2)
				{
					return 0;
				}
				return o1.toString().compareTo(o2.toString());
			}});
		return list.get(0);
		
	}

}
