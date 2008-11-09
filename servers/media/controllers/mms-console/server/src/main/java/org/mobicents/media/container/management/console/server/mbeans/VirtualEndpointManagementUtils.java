package org.mobicents.media.container.management.console.server.mbeans;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.mobicents.media.container.management.console.client.ManagementConsoleException;
import org.mobicents.media.container.management.console.client.platform.PlatformManagementService;
import org.mobicents.media.container.management.console.client.platform.ServerState;

public class VirtualEndpointManagementUtils implements PlatformManagementService {

	private ObjectName mgrName = null;
	private MBeanServerConnection mbeanServer = null;

	public VirtualEndpointManagementUtils(MBeanServerConnection mbeanServer, ObjectName platformManagerName) {
		this.mgrName = platformManagerName;
		this.mbeanServer = mbeanServer;
	}

	public ServerState getState() throws ManagementConsoleException {

		try {
			String stateString = (String) mbeanServer.invoke(mgrName, "getPlatformStateAsString", null, null);
			return ServerState.fromString(stateString);
		} catch (Exception e) {
			throw new ManagementConsoleException(MMSManagementMBeanUtils.doMessage(e));
		}

	}

	public void start() throws ManagementConsoleException {
		try {
			mbeanServer.invoke(mgrName, "startPlatform", null, null);

		} catch (Exception e) {
			throw new ManagementConsoleException(MMSManagementMBeanUtils.doMessage(e));
		}
	}

	public void stop() throws ManagementConsoleException {
		try {
			mbeanServer.invoke(mgrName, "stopPlatform", null, null);

		} catch (Exception e) {
			throw new ManagementConsoleException(MMSManagementMBeanUtils.doMessage(e));
		}

	}

	public void tearDown() throws ManagementConsoleException {
		try {
			mbeanServer.invoke(mgrName, "tearDownPlatform", null, null);

		} catch (Exception e) {
			throw new ManagementConsoleException(MMSManagementMBeanUtils.doMessage(e));
		}

	}

	public String getVersion() throws ManagementConsoleException {
		try {
		return (String)	mbeanServer.invoke(mgrName, "getVersion", null, null);

		} catch (Exception e) {
			throw new ManagementConsoleException(MMSManagementMBeanUtils.doMessage(e));
		}
	}

}
