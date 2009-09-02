/**
 * Start time:10:45:17 2009-08-05<br>
 * Project: jb51-cluster<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.ftf;

import java.util.Collection;
import java.util.List;

import org.jboss.ha.framework.interfaces.ClusterNode;
import org.jgroups.stack.IpAddress;

/**
 * Start time:10:45:17 2009-08-05<br>
 * Project: jb51-cluster<br>
 * Implementation takes care of translating jgroup address into cluster node in
 * order to allow HaSingletonElectionPolicy to process.
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
public interface Cache2ClusterNodeTransalator {

	public List<ClusterNode> translate(Collection<IpAddress> members);
	
}
