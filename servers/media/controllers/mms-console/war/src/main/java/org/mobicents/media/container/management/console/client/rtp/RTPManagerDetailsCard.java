package org.mobicents.media.container.management.console.client.rtp;

import org.mobicents.media.container.management.console.client.ServerConnection;
import org.mobicents.media.container.management.console.client.common.BrowseContainer;
import org.mobicents.media.container.management.console.client.common.Card;
import org.mobicents.media.container.management.console.client.common.UserInterface;
import org.mobicents.media.container.management.console.client.common.pages.RTPManagementPage;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class RTPManagerDetailsCard extends Card {
	
	
	protected String objectName=null;
	protected BrowseContainer browser=new BrowseContainer();
	protected RTPManagementPage parent=null;
	public RTPManagerDetailsCard(String rtpManagementMBeanObjectName,RTPManagementPage parent) {
		super();
		objectName=rtpManagementMBeanObjectName;
		this.parent=parent;
		initWidget(browser);
		browser.setHeight("100%");
		browser.setWidth("100%");
		super.setHeight("100");
		super.setWidth("100%");
	}

	

	public void onHide() {
		

	}

	public void onInit() {
		ServerConnection.rtpManagementServiceAsync.getManagerInfo(objectName, new PopulateRTPManagerDataIntoDisplayAsyncCallback(objectName));
	}

	public void onShow() {
		
		
	}
	
	private class PopulateRTPManagerDataIntoDisplayAsyncCallback implements AsyncCallback
	  {
		  String on=null;

		public PopulateRTPManagerDataIntoDisplayAsyncCallback(String on) {
			super();
			this.on = on;
		}

		public void onFailure(Throwable arg0) {
			UserInterface.getLogPanel().error("Failed to obtain RTPManagerData due to:\n"+arg0.getMessage());
			
		}

		public void onSuccess(Object arg0) {
			RTPManagerInfo info=(RTPManagerInfo) arg0;
			browser.add(info.getObjectName(), new RTPManagerDetailsPage(browser,info,parent));
		}
	  }
	
}
