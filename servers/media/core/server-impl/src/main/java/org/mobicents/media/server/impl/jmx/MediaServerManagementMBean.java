package org.mobicents.media.server.impl.jmx;

import javax.management.ObjectName;

/**
 * The Media Server Managemnet interface that is responsible for starting and
 * stopping of server.
 * 
 * This MBean displays the current version of MMS
 * 
 * @author amit.bhayani
 * 
 */
public interface MediaServerManagementMBean {

	public String getVersion();

	/**
	 * Moves platform into STOPING state. In this state it does not allow to
	 * create any more endpoints. It awaits for endpoints to die. Once they
	 * terminate platform state shifts to STOPED
	 */
	public void stopPlatform();

	/**
	 * Moves platform into runnign state. Allows endpoints to be created.
	 */
	public void startPlatform();
	/**
	 * Moves platform to STOPED state - terminates any processing that may be in
	 * progress.
	 */

}
