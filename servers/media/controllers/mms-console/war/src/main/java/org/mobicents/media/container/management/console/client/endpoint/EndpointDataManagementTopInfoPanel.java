package org.mobicents.media.container.management.console.client.endpoint;

import java.util.Date;

import org.mobicents.media.container.management.console.client.ServerConnection;
import org.mobicents.media.container.management.console.client.common.ListPanel;
import org.mobicents.media.container.management.console.client.common.UserInterface;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class EndpointDataManagementTopInfoPanel extends ListPanel {

	protected EndpointFullInfo info = null;
	protected ListBox rtpFactoryNameListBox = new ListBox();
	protected ListBox gatherDataListBox = new ListBox();
	protected Button destroyButton = new Button("Destroy");
	protected ActionPerform refreshDataAction;
	protected ActionPerform onErrorAction;

	public EndpointDataManagementTopInfoPanel(ActionPerform refreshAction,
			ActionPerform onErrorAction) {
		super();
		this.refreshDataAction = refreshAction;
		this.onErrorAction = onErrorAction;

		this.setWidth("100%");
		// this.setHeight("100%");

		this.gatherDataListBox.addItem("Yes", new Boolean(true).toString());
		this.gatherDataListBox.addItem("No", new Boolean(false).toString());
		this.gatherDataListBox
				.addChangeListener(new GatherDataChangeListener());
		this.rtpFactoryNameListBox
				.addChangeListener(new ChangeRTPManagerChangeListener());
	}

	public void populateWithData(EndpointFullInfo info) {
		this.info = info;

		setCellText(0, 0, "Name:");
		setCellText(1, 0, "Rtp Factory:");
		setCellText(2, 0, "Gather stats:");
		setCellText(3, 0, "Type:");
		setCellText(4, 0, "Created:");
		setColumnWidth(0, "10%");

		setCellText(0, 2, "Connections:");
		setCellText(1, 2, "Packets:");
		setCellText(2, 2, "Data:");
		setCellText(3, 2, "Destroy:");
		setColumnWidth(2, "10%");

		setColumnWidth(1, "55%");
		setColumnWidth(3, "25%");

		setCellText(0, 1, info.getName());
		setCell(1, 1, rtpFactoryNameListBox);
		setCell(2, 1, gatherDataListBox);
		setCellText(3, 1, info.getType().type);
		setCellText(4, 1, new Date(info.getCreationTime()).toString());

		setCellText(0, 3, "" + info.getConnections());
		setCellText(1, 3, "" + info.getPackets());
		setCellText(2, 3, "" + (info.getNumberOfBytes() / (8 * 1024 * 1024)));
		setCell(3, 3, destroyButton);
		destroyButton
				.addClickListener(new DestroyEndpointButtonClickListener());
		updateGatherDataView(info.isGetherPerformance());
		updateRTPList(info.getRtpFactoryJNDIName());
		ServerConnection.rtpManagementServiceAsync
				.getAvailableRTPManagersJNDIName(new PopulateRTPManagersListBoxCallBack());

	}

	private void updateGatherDataView(boolean value) {

		UserInterface.getLogPanel().info("SETTING : "+value);
		for (int i = 0; i < this.gatherDataListBox.getItemCount(); i++) {
			boolean v = Boolean.valueOf(gatherDataListBox.getValue(i))
					.booleanValue();
			UserInterface.getLogPanel().info("SETTING2 :"+gatherDataListBox.getValue(i)+": "+v);	
			if (v == value) {
				
				gatherDataListBox.setSelectedIndex(i);
				return;
			}

		}

	}

	private void updateRTPList(String name) {

		for (int i = 0; i < this.rtpFactoryNameListBox.getItemCount(); i++) {
			String value = rtpFactoryNameListBox.getValue(i);
			if (name.equals(value)) {

				rtpFactoryNameListBox.setSelectedIndex(i);
				return;
			}
		}
	}

	private class PopulateRTPManagersListBoxCallBack implements AsyncCallback {

		public void onFailure(Throwable t) {
			UserInterface.getLogPanel().error(
					"Failed to get RTP managers list due to:\n"
							+ t.getMessage());

		}

		public void onSuccess(Object o) {

			String[] list = (String[]) o;
			rtpFactoryNameListBox.clear();

			for (int i = 0; i < list.length; i++) {
				rtpFactoryNameListBox.addItem(list[i], list[i]);

			}

			updateRTPList(info.getRtpFactoryJNDIName());
		}

	}

	private class GatherDataChangeListener implements ChangeListener {

		public void onChange(Widget w) {
			ListBox lb = (ListBox) w;
			boolean value = Boolean.valueOf(lb.getValue(lb.getSelectedIndex()))
					.booleanValue();
			UserInterface.getLogPanel().info("SENDING:"+value);
			ServerConnection.endpointManagementServiceAsync
					.setGatherPerformanceData(info.getName(), info.getType(),
							value, new GatherDataSetAsyncCallback());

		}

	}

	private class GatherDataSetAsyncCallback implements AsyncCallback {

		public void onFailure(Throwable t) {

			UserInterface.getLogPanel().error(
					"Failed to set gather data due to:\n" + t.getMessage());

		}

		public void onSuccess(Object o) {
			boolean value = Boolean.valueOf(
					gatherDataListBox.getValue(gatherDataListBox
							.getSelectedIndex())).booleanValue();
			info.setGetherPerformance(value);

		}
	}

	private class ChangeRTPManagerChangeListener implements ChangeListener {

		public void onChange(Widget w) {
			ListBox lb = (ListBox) w;
			String jndiName = lb.getValue(lb.getSelectedIndex());
			ServerConnection.endpointManagementServiceAsync
					.setRTPFactoryJNDIName(info.getName(), info.getType(),
							jndiName, new ChangeRTPManagerAsyncCallback());
		}

	}

	private class ChangeRTPManagerAsyncCallback implements AsyncCallback {

		public void onFailure(Throwable t) {
			UserInterface.getLogPanel().error(
					"Failed to set new RTPManager due: \n" + t.getMessage());

			ServerConnection.rtpManagementServiceAsync
					.getAvailableRTPManagersJNDIName(new PopulateRTPManagersListBoxCallBack());

		}

		public void onSuccess(Object o) {
			String jndiName = rtpFactoryNameListBox
					.getValue(rtpFactoryNameListBox.getSelectedIndex());
			updateRTPList(jndiName);
		}

	}

	private class DestroyEndpointButtonClickListener implements ClickListener {

		public void onClick(Widget arg0) {
			ServerConnection.endpointManagementServiceAsync.destroyEndpoint(
					info.getName(), info.getType(),
					new DestroyActionAsyncCallback());

		}

	}

	private class DestroyActionAsyncCallback implements AsyncCallback {

		public void onFailure(Throwable arg0) {
			onErrorAction.performAction();

		}

		public void onSuccess(Object arg0) {
			onErrorAction.performAction();

		}

	}

}
