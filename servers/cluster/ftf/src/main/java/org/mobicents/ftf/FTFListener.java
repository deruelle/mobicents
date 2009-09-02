package org.mobicents.ftf;

import java.util.Map;

import org.jboss.cache.Fqn;

/**
 * Start time:15:13:19 2009-08-04<br>
 * Project: jb51-cluster<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */

/**
 * Start time:15:13:19 2009-08-04<br>
 * Project: jb51-cluster<br>
 * This interface defines callback methods which will be called on certain
 * occasions like:
 * <ul>
 * <li>node has been removed from local copy</li>
 * <li>node ownership changed to another</li>
 * 
 * </ul>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
public interface FTFListener {

	/**
	 * Determines that local node has been removed. Reasons for this method to
	 * be called can be various - in case of data gravitation it could mean than
	 * ownership changed. In any case, if there are any resources associated
	 * with current task they should be removed.
	 * 
	 * @param key
	 *            - FQN of removed node
	 * @param values
	 *            - values stored in particular node before remove operation has
	 *            been performed.
	 */
	public void onNodeRemoved(Fqn key, Map values);

	/**
	 * Determines that ownership has changed.
	 * 
	 * @param key
	 *            - FQN of node which changed ownership
	 * @param values
	 *            - values stored in particular node after ownership has
	 *            changed.
	 * @param toLocal
	 *            - indicates if local node is owner of this node.
	 */
	public void onNodeOwnershipMoved(Fqn key, Map values, boolean toLocal);

}
