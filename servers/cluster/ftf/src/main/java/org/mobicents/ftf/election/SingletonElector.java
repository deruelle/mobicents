/**
 * Start time:22:12:21 2009-09-01<br>
 * Project: mobicents-jainslee-server-core<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.ftf.election;

import java.util.List;

import org.jgroups.stack.IpAddress;

/**
 * Start time:22:12:21 2009-09-01<br>
 * Project: mobicents-jainslee-server-core<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface SingletonElector {

	
	public IpAddress elect(List<IpAddress> list);
	
	//FIXME: add conf methods here
	
}
