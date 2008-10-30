package org.mobicents.media.container.management.console.client.endpoint;

import org.mobicents.media.container.management.console.client.ServerConnection;
import org.mobicents.media.container.management.console.client.common.BrowseContainer;
import org.mobicents.media.container.management.console.client.common.UserInterface;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

public class EndpointDataManagementPanel extends Composite{

	
	protected BrowseContainer display=null;
	protected DockPanel dataDisplay=new DockPanel();
	protected EndpointType type;
	protected String endpointName;
	protected ActionPerform onRefreshAction=null;
	protected ActionPerform onErrorAction=null;
	protected boolean isVirtual=false;
	//DataPresentation
	EndpointDataManagementTopInfoPanel topPanel=null;
	EndpointDataManagementConnectionsDisplay centerPanel=null;
	
	public EndpointDataManagementPanel(BrowseContainer display,
			EndpointType type, String endpointName,ActionPerform onErrorAction) {
	this(display, type, endpointName, false, onErrorAction);
	}
	public EndpointDataManagementPanel(BrowseContainer display,
			EndpointType type, String endpointName, boolean isVirtual,ActionPerform onErrorAction) {
		super();
		this.display = display;
		this.type = type;
		this.endpointName = endpointName;
		initWidget(dataDisplay);
		
		this.onErrorAction=onErrorAction;
		this.onRefreshAction=new RefreshAction();
			
			
		this.topPanel=new EndpointDataManagementTopInfoPanel(this.onRefreshAction,this.onErrorAction);	
		this.centerPanel=new EndpointDataManagementConnectionsDisplay(this.onRefreshAction,this.onErrorAction);
		
		dataDisplay.setWidth("100%");
		//dataDisplay.setHeight("100%");
		
		
		dataDisplay.add(topPanel, dataDisplay.NORTH);
		dataDisplay.setCellWidth(topPanel, "100%");
		
		dataDisplay.setCellVerticalAlignment(topPanel, HasVerticalAlignment.ALIGN_TOP);
		
		dataDisplay.add(centerPanel, dataDisplay.CENTER);
		dataDisplay.setCellWidth(centerPanel, "100%");
		dataDisplay.setCellVerticalAlignment(centerPanel, HasVerticalAlignment.ALIGN_TOP);
		this.onRefreshAction.performAction();
		

	}
	private void populateWithData(EndpointFullInfo info)
	{

		topPanel.populateWithData(info);

		centerPanel.populateWithData(info);
	
	}
	
	private class RefreshAction extends ActionPerform
	{

		
		public void performAction() {
			ServerConnection.endpointManagementServiceAsync.getEndpointInfo(endpointName, type, new RefreshCallback());
			
		}
		
	}
	
	private class RefreshCallback implements AsyncCallback
	{

		
		public void onFailure(Throwable arg0) {
			UserInterface.getLogPanel().error("Failed to refresh due to:\n"+arg0.getMessage());
			onErrorAction.performAction();
			
		}

		
		public void onSuccess(Object o) {
			populateWithData((EndpointFullInfo) o);
			
		}
		
	}
	
	
}
