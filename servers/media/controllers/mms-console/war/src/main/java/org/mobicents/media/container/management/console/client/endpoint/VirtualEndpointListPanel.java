package org.mobicents.media.container.management.console.client.endpoint;

import java.util.Date;

import org.mobicents.media.container.management.console.client.ServerConnection;
import org.mobicents.media.container.management.console.client.common.BrowseContainer;
import org.mobicents.media.container.management.console.client.common.CommonControl;
import org.mobicents.media.container.management.console.client.common.ControlledTabedBar;
import org.mobicents.media.container.management.console.client.common.ListPanel;
import org.mobicents.media.container.management.console.client.common.UserInterface;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class VirtualEndpointListPanel extends DockPanel implements CommonControl {

	// Just in case we need it
	protected BrowseContainer display = null;
	protected EndpointType type = null;
	protected ListPanel endpointList = new ListPanel();
	protected EndpointDataManagementTopInfoPanel topPanel = null;
	protected OnErrorAction onErrorAction = new OnErrorAction();
	protected OnRefreshAction onRefreshAction = new OnRefreshAction();
	protected Widget parent = null;
	protected ControlledTabedBar lister = null;
	protected final static boolean isVirtual = true;
	//FIXME add proper image
	protected final static String imageUrl="images/endpoints.connections.jpg";
	public VirtualEndpointListPanel(Widget parent, ControlledTabedBar lister, BrowseContainer display, EndpointType type) {
		super();

		this.topPanel = new EndpointDataManagementTopInfoPanel(this.onRefreshAction, this.isVirtual, this.onErrorAction);
		this.display = display;
		this.type = type;
		this.parent = parent;
		this.lister = lister;
		this.add(this.endpointList, DockPanel.SOUTH);
		this.setCellHeight(this.endpointList, "100%");
		this.setCellWidth(this.endpointList, "100%");
		this.setCellVerticalAlignment(this.endpointList, HasVerticalAlignment.ALIGN_TOP);
		Image ll=new Image(imageUrl);
		this.add(ll, DockPanel.CENTER);
		this.setCellHeight(ll, "100%");
		this.setCellWidth(ll, "100%");
		this.setCellVerticalAlignment(ll, HasVerticalAlignment.ALIGN_TOP);
		this.add(this.topPanel, DockPanel.NORTH);
		this.setCellHeight(this.topPanel, "100%");
		this.setCellWidth(this.topPanel, "100%");
		this.setCellVerticalAlignment(this.topPanel, HasVerticalAlignment.ALIGN_TOP);
		this.setHeight("100%");
		this.setWidth("100%");

	}

	public void onHide() {

		endpointList.emptyTable();

	}

	public void onInit() {

	}

	public void onShow() {

		ServerConnection.endpointManagementServiceAsync.getEndpointTrunkInfo(type, new PopulateDataAsyncCallback());

	}

	private class OnErrorAction extends ActionPerform {

		public void performAction() {
			display.select(parent);
			lister.onHide();
			lister.onShow();

		}
	}

	private class OnRefreshAction extends ActionPerform {

		public void performAction() {
			onHide();
			onShow();

		}
	}

	private class PopulateDataAsyncCallback implements AsyncCallback {

		public void onFailure(Throwable caught) {
			onErrorAction.performAction();

		}

		public void onSuccess(Object result) {

			EndpointFullInfo info = (EndpointFullInfo) result;
			topPanel.populateWithData(info);

			// Now we have to list our minions :)

			endpointList.setHeader(0, "#");
			endpointList.setHeader(1, "Name");
			endpointList.setHeader(2, "Connections");
			endpointList.setHeader(3, "Packets");
			endpointList.setHeader(4, "Creation time");
			endpointList.setHeader(5, "Destroy");

			endpointList.setColumnWidth(0, "5%");
			endpointList.setColumnWidth(1, "40%");
			endpointList.setColumnWidth(2, "5%");
			endpointList.setColumnWidth(3, "10%");
			endpointList.setColumnWidth(4, "25%");
			endpointList.setColumnWidth(5, "10%");

			EndpointShortInfo[] infos = info.getChildrenInfo();
			if (infos != null) {
				for (int index = 0; index < infos.length; index++) {
					EndpointShortInfo sInfo = infos[index];
					endpointList.setCellText(index, 0, "#" + index);
					endpointList.setCellText(index, 1, sInfo.getName());
					endpointList.setCellText(index, 2, "" + sInfo.getConnections());
					endpointList.setCellText(index, 3, "" + sInfo.getPackets());
					endpointList.setCellText(index, 4, "" + new Date(sInfo.getCreationTime()));
					Button deleteEndpointButton = new Button("Trigger");
					deleteEndpointButton.addClickListener(new DeleteEndpointButtonClickListener(sInfo.getName(),sInfo.getType()));
					endpointList.setCell(index, 5, deleteEndpointButton);
				}
			}

		}

	}

	private class DeleteEndpointButtonClickListener implements ClickListener {
		String endpointName = null;
		EndpointType type = null;

		public DeleteEndpointButtonClickListener(String endpointName, EndpointType type) {
			super();
			this.endpointName = endpointName;
			this.type = type;
		}

		public void onClick(Widget sender) {
			ServerConnection.endpointManagementServiceAsync.destroyEndpoint(endpointName, type, new OnDeleteAsyncCallBack());
			
		}	}
	private class OnDeleteAsyncCallBack implements AsyncCallback
	{

		public void onFailure(Throwable caught) {
			onErrorAction.performAction();
			
		}

		public void onSuccess(Object result) {
			onRefreshAction.performAction();
			
		}
		
		
	}
	
}
