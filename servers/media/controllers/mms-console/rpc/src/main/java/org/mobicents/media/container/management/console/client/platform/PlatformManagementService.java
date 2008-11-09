package org.mobicents.media.container.management.console.client.platform;

import org.mobicents.media.container.management.console.client.ManagementConsoleException;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;

public interface PlatformManagementService extends RemoteService{

	public void stop() throws ManagementConsoleException;
	public void start() throws ManagementConsoleException;
	public void tearDown() throws ManagementConsoleException;
	public ServerState getState() throws ManagementConsoleException;
	public String getVersion() throws ManagementConsoleException;
	
}
