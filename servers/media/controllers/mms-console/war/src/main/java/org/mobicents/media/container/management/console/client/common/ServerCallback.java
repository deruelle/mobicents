package org.mobicents.media.container.management.console.client.common;



import org.mobicents.media.container.management.console.client.Logger;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public abstract class ServerCallback implements AsyncCallback {
	
	protected Widget widget;
	
	public ServerCallback(Widget widget) {
		this.widget = widget;
	}

	public void onFailure(Throwable caught) {
		Logger.error(caught.getMessage());
	}

	public void onSuccess(Object result) {
	}
}