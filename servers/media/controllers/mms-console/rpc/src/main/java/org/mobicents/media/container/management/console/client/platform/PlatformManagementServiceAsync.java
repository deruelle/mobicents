package org.mobicents.media.container.management.console.client.platform;






import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PlatformManagementServiceAsync {

	
	public void stop(AsyncCallback callback);
	public void start(AsyncCallback callback);
	public void tearDown(AsyncCallback callback);
	public void getState(AsyncCallback callback);
	public void getVersion(AsyncCallback callback);
}
