/**
 * Start time:14:31:11 2009-08-18<br>
 * Project: mobicents-jainslee-server-core<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.slee.runtime.cache;


/**
 * Start time:14:31:11 2009-08-18<br>
 * Project: mobicents-jainslee-server-core<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
public interface TimerTaskDataUser {

	/**
	 * @param data
	 */
	void recoverLocalResource(Object data);

	/**
	 * @param taskID
	 * @return
	 */
	boolean containsLocalResource(Object taskID);

	/**
	 * @param taskID
	 */
	void removeLocalResource(Object taskID);

}
