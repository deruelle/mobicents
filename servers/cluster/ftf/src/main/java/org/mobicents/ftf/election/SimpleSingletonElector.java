/**
 * Start time:22:14:24 2009-09-01<br>
 * Project: mobicents-jainslee-server-core<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.ftf.election;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jgroups.stack.IpAddress;

/**
 * Start time:22:14:24 2009-09-01<br>
 * Project: mobicents-jainslee-server-core<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
public class SimpleSingletonElector implements SingletonElector {

	/**
	 * 	
	 */
	public SimpleSingletonElector() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.mobicents.ftf.election.SingletonElector#elect(java.util.List)
	 */
	public IpAddress elect(List<IpAddress> list) {
		//FIXME: add something better
		Collections.sort(list, new Comparator<IpAddress>(){

			public int compare(IpAddress o1, IpAddress o2) {
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
