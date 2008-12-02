package org.mobicents.media.container.management.console.client.endpoint;

import java.util.Date;

import org.mobicents.media.container.management.console.client.ServerConnection;
import org.mobicents.media.container.management.console.client.common.BrowseContainer;
import org.mobicents.media.container.management.console.client.common.CommonControl;
import org.mobicents.media.container.management.console.client.common.ControlledTabedBar;
import org.mobicents.media.container.management.console.client.common.ListPanel;
import org.mobicents.media.container.management.console.client.common.UserInterface;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class EndpointListPanel extends DockPanel implements CommonControl {

	protected BrowseContainer display = null;
	protected EndpointType type = null;
	protected ListPanel endpointList = new ListPanel();
	protected OnErrorAction onErrorAction = new OnErrorAction();
	protected Widget parent = null;
	protected ControlledTabedBar lister=null;
	public EndpointListPanel(Widget parent,ControlledTabedBar lister,
			BrowseContainer display, EndpointType type) {
		super();
		this.display = display;
		this.type = type;
		this.parent = parent;
		this.lister=lister;
		this.add(this.endpointList, DockPanel.CENTER);
		this.setCellHeight(this.endpointList, "100%");
		this.setCellWidth(this.endpointList, "100%");

		this.setHeight("100%");
		this.setWidth("100%");

	}

	public void onHide() {

		endpointList.emptyTable();

	}

	public void onInit() {

	}

	public void onShow() {

		ServerConnection.endpointManagementServiceAsync.getEndpointsShortInfo(
				type, new AsyncCallback() {

					public void onFailure(Throwable t) {

						UserInterface.getLogPanel()
								.error(
										"Failed to load info on endpoints type: "
												+ type + ". Due to:\n"
												+ t.getMessage());

					}

					public void onSuccess(Object o) {

						EndpointShortInfo[] infos = (EndpointShortInfo[]) o;

						endpointList.setHeader(0, "#");
						// endpointList.setHeader(1, "GEN");
						endpointList.setHeader(1, "Name");
						endpointList.setHeader(2, "Connections");
						endpointList.setHeader(3, "Packets");
						endpointList.setHeader(4, "Data");
						endpointList.setHeader(5, "Life time");

						endpointList.setColumnWidth(0, "5%");
						// endpointList.setColumnWidth(1, "10%");
						endpointList.setColumnWidth(1, "60%");
						endpointList.setColumnWidth(2, "5%");
						endpointList.setColumnWidth(3, "10%");
						endpointList.setColumnWidth(4, "10%");
						endpointList.setColumnWidth(5, "10%");

						for (int i = 0; i < infos.length; i++) {

							EndpointShortInfo info = infos[i];
							Hyperlink link = new Hyperlink();
							link.setText("#" + i);
							link
									.addClickListener(new ChooseEndpointLinkListener(
											info.getName(), type));
							endpointList.setCell(i, 0, link);
							// endpointList.setCellText(i, 1, info.getGEN()+"");
							endpointList.setCellText(i, 1, info.getName() + "");
							endpointList.setCellText(i, 2, info
									.getConnections()
									+ "");
							endpointList.setCellText(i, 3, "" + ((double)info.getPackets()/(1000000))+" M");
							endpointList.setCellText(i, 4, "" + ((double)info.getNumberOfBytes() / ( 1024 * 1024))+" M");
							endpointList.setCellText(i, 5, new Date(info
									.getCreationTime())
									+ "");
						}

					}
				});

	}

	private class ChooseEndpointLinkListener implements ClickListener {

		protected String name = null;
		protected EndpointType type = null;

		public ChooseEndpointLinkListener(String name, EndpointType type) {
			super();
			this.name = name;
			this.type = type;
		}

		public void onClick(Widget arg0) {
			display.add(name, new EndpointDataManagementPanel(display, type,
					name, onErrorAction));

		}

	}

	private class OnErrorAction extends ActionPerform {

		public void performAction() {
			display.select(parent);
			lister.onHide();
			lister.onShow();

		}
	}

}
