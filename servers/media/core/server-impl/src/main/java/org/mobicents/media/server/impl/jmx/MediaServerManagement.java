package org.mobicents.media.server.impl.jmx;

import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;
import org.mobicents.media.server.impl.Version;

/**
 * 
 * @author amit.bhayani
 * 
 */

public class MediaServerManagement implements MediaServerManagementMBean {

	private static Logger logger = Logger
			.getLogger(MediaServerManagement.class);
	
	private List<TrunkManagement> listOfTrunks = new ArrayList<TrunkManagement>();

	public MediaServerManagement() {

	}

	public void create() {
		logger.info("[[[[[[[[[ " + getVersion() + " starting... ]]]]]]]]]");
	}

	public void start() {
		logger.info("[[[[[[[[[ " + getVersion() + " Started " + "]]]]]]]]]");
	}

	public void stop() {
		logger.info("[[[[[[[[[ " + getVersion() + " Stopped " + "]]]]]]]]]");
	}

	public void destroy() {
	}

	public void addTrunk(TrunkManagement trunkManagement) {
		listOfTrunks.add(trunkManagement);
		logger.info("Added Trunk "+trunkManagement);
	}

	public void removeTrunk(TrunkManagement trunkManagement) {
		listOfTrunks.remove(trunkManagement);
		logger.info("removed Trunk "+trunkManagement);
	}

	public String getVersion() {
		return Version.instance.toString();
	}

	/**
	 * Moves platform into STOPING state. In this state it does not allow to
	 * create any more endpoints. It awaits for endpoints to die. Once they
	 * terminate platform state shifts to STOPED
	 */
	public void stopPlatform() {

	}

	/**
	 * Moves platform into runnign state. Allows endpoints to be created.
	 */
	public void startPlatform() {

	}

}
