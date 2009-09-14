/**
 * 
 */
package org.mobicents.cluster;

import java.util.Comparator;

/**
 * @author martins
 *
 */
public class ClientLocalListenerComparator implements Comparator<ClientLocalListener> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(ClientLocalListener o1, ClientLocalListener o2) {
		if (o1.equals(o2)) {
			return 0;
		}
		else {
			if (o1.getPriority() > o2.getPriority()) {
				return -1;
			}
			else {
				return 1;
			}
		}
	}

}
