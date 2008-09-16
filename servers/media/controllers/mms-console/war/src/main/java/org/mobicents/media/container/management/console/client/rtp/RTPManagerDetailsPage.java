package org.mobicents.media.container.management.console.client.rtp;

import java.util.HashMap;

import org.mobicents.media.container.management.console.client.ServerConnection;
import org.mobicents.media.container.management.console.client.common.BrowseContainer;
import org.mobicents.media.container.management.console.client.common.ControlContainer;
import org.mobicents.media.container.management.console.client.common.ListPanel;
import org.mobicents.media.container.management.console.client.common.UserInterface;
import org.mobicents.media.container.management.console.client.common.pages.RTPManagementPage;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class RTPManagerDetailsPage extends Composite {

	protected RTPManagerInfo info = null;
	protected RTPManagementPage parent = null;
	protected BrowseContainer display = null;
	protected ControlContainer rootPanel = new ControlContainer();
	protected final static String _OBJECT_NAME = "Object Name";
	protected final static String _BIND_ADDRESS = "Bind Address";
	protected final static String _JNDI_BINDING = "JNDI Binding Name";
	protected final static String _PACKETIZATION_PERIOD = "Packetization Period";
	protected final static String _JIITER = "Jitter";
	protected final static String _LOW_PORT = "Low Port";
	protected final static String _HIGH_PORT = "High Port";
	protected final static String _STUN_SERVER_ADDRESS = "Stun Server Address";
	protected final static String _STUN_SERVER_PORT = "Stun Server Port";
	protected final static String _STUN_PUBLIC_ADDRESS = "Stun Public Address";
	protected final static String _USE_STUN = "Use Stun";
	protected final static String _USE_PORT_MAPPING = "Use Port Mapping";

	// INFO display/gather parts
	protected TextBox objectNameBox = new TextBox();
	protected TextBox bindAddressBox = new TextBox();
	protected TextBox jndiNameBox = new TextBox();
	protected TextBox packetizationPeriodBox = new TextBox();
	protected TextBox jitterBox = new TextBox();
	protected TextBox lowPortRangeBox = new TextBox();
	protected TextBox highPortRange = new TextBox();
	protected TextBox stunServerAddressBox = new TextBox();
	protected TextBox stunServerPortBox = new TextBox();
	protected TextBox publicAddressFromStunBox = new TextBox();
	protected CheckBox useStunCheckBox = new CheckBox();
	protected CheckBox usePortMappingCheckBox = new CheckBox();

	protected final static int _OPERATION_SET = 0;
	protected final static int _OPERATION_REFRESH = 1;

	protected final static XFormat[] predefinedAudioFormats;
	protected final static XFormat[] predefinedVideoFormats;
	static {
		XFormat xf = new XFormat("telephone-event", "101");

		// FIXME:Add more
		predefinedAudioFormats = new XFormat[] { xf };
		predefinedVideoFormats = new XFormat[0];
	}

	public RTPManagerDetailsPage(BrowseContainer display, RTPManagerInfo info, RTPManagementPage parent) {
		super();

		this.display = display;
		this.info = info;
		this.parent = parent;
		super.initWidget(rootPanel);
		this.refreshData(null, info);
	}

	protected void refreshData(String objectName, RTPManagerInfo info) {
		if (info == null) {

			// FIXME: Do AsyncCallback?
			return;
		}

		this.info = info;
		this.rootPanel.clear();
		ListPanel displayInfoPanel = new ListPanel();
		displayInfoPanel.setHeader(0, "Property");
		displayInfoPanel.setHeader(1, "Value");
		displayInfoPanel.setHeader(2, "Set");
		displayInfoPanel.setHeader(3, "Refresh");
		displayInfoPanel.setColumnWidth(0, "100px");
		displayInfoPanel.setColumnWidth(1, "100%");
		displayInfoPanel.setColumnWidth(2, "50px");
		displayInfoPanel.setColumnWidth(3, "50px");

		ClickListener refreshListener = new RefreshButtonClickListener();
		ClickListener setClickListener = new SetButtonClickListener();
		// ObjectName #0
		Button refresButton = new Button();
		Button setButton = new Button();
		refresButton.setText("Refresh");
		setButton.setText("Set");
		refresButton.setTitle(_OBJECT_NAME);
		setButton.setTitle(_OBJECT_NAME);
		refresButton.addClickListener(refreshListener);
		setButton.addClickListener(setClickListener);
		this.objectNameBox.setText(this.info.getObjectName());
		this.objectNameBox.setEnabled(false);
		this.objectNameBox.setWidth("100%");
		refresButton.setEnabled(false);
		setButton.setEnabled(false);
		displayInfoPanel.setCellText(0, 0, _OBJECT_NAME);
		displayInfoPanel.setCell(0, 1, this.objectNameBox);
		displayInfoPanel.setCell(0, 2, setButton);
		displayInfoPanel.setCell(0, 3, refresButton);

		// JNDI #1
		refresButton = new Button();
		setButton = new Button();
		refresButton.setText("Refresh");
		setButton.setText("Set");
		refresButton.setTitle(_JNDI_BINDING);
		setButton.setTitle(_JNDI_BINDING);
		this.jndiNameBox.setText(this.info.getJndiName());
		this.jndiNameBox.setWidth("100%");
		refresButton.addClickListener(refreshListener);
		setButton.addClickListener(setClickListener);
		displayInfoPanel.setCellText(1, 0, _JNDI_BINDING);
		displayInfoPanel.setCell(1, 1, this.jndiNameBox);
		displayInfoPanel.setCell(1, 2, setButton);
		displayInfoPanel.setCell(1, 3, refresButton);

		// BINDADDRESS #2
		refresButton = new Button();
		setButton = new Button();
		refresButton.setText("Refresh");
		setButton.setText("Set");
		refresButton.setTitle(_BIND_ADDRESS);
		setButton.setTitle(_BIND_ADDRESS);
		refresButton.addClickListener(refreshListener);
		setButton.addClickListener(setClickListener);
		this.bindAddressBox.setText(this.info.getBindAddress());
		this.bindAddressBox.setWidth("100%");
		displayInfoPanel.setCellText(2, 0, _BIND_ADDRESS);
		displayInfoPanel.setCell(2, 1, this.bindAddressBox);
		displayInfoPanel.setCell(2, 2, setButton);
		displayInfoPanel.setCell(2, 3, refresButton);

		// PACKETIZATION #3
		refresButton = new Button();
		setButton = new Button();
		refresButton.setText("Refresh");
		setButton.setText("Set");
		refresButton.addClickListener(refreshListener);
		setButton.addClickListener(setClickListener);
		refresButton.setTitle(_PACKETIZATION_PERIOD);
		setButton.setTitle(_PACKETIZATION_PERIOD);
		this.packetizationPeriodBox.setText(this.info.getPacketizationPeriod() + "");
		displayInfoPanel.setCellText(3, 0, _PACKETIZATION_PERIOD);
		displayInfoPanel.setCell(3, 1, this.packetizationPeriodBox);
		displayInfoPanel.setCell(3, 2, setButton);
		displayInfoPanel.setCell(3, 3, refresButton);

		// JITTER #4
		refresButton = new Button();
		setButton = new Button();
		refresButton.setText("Refresh");
		setButton.setText("Set");
		refresButton.addClickListener(refreshListener);
		setButton.addClickListener(setClickListener);
		refresButton.setTitle(_JIITER);
		setButton.setTitle(_JIITER);
		this.jitterBox.setText(this.info.getJitter() + "");
		displayInfoPanel.setCellText(4, 0, _JIITER);
		displayInfoPanel.setCell(4, 1, this.jitterBox);
		displayInfoPanel.setCell(4, 2, setButton);
		displayInfoPanel.setCell(4, 3, refresButton);

		// LOW PORT #5
		refresButton = new Button();
		setButton = new Button();
		refresButton.setText("Refresh");
		setButton.setText("Set");
		refresButton.addClickListener(refreshListener);
		setButton.addClickListener(setClickListener);
		refresButton.setTitle(_LOW_PORT);
		setButton.setTitle(_LOW_PORT);
		this.lowPortRangeBox.setText(this.info.getPortRange()[0] + "");
		displayInfoPanel.setCellText(5, 0, _LOW_PORT);
		displayInfoPanel.setCell(5, 1, this.lowPortRangeBox);
		displayInfoPanel.setCell(5, 2, setButton);
		displayInfoPanel.setCell(5, 3, refresButton);

		// HIGH PORT #6
		refresButton = new Button();
		setButton = new Button();
		refresButton.setText("Refresh");
		setButton.setText("Set");
		refresButton.addClickListener(refreshListener);
		setButton.addClickListener(setClickListener);
		refresButton.setTitle(_HIGH_PORT);
		setButton.setTitle(_HIGH_PORT);
		this.highPortRange.setText(this.info.getPortRange()[1] + "");
		displayInfoPanel.setCellText(6, 0, _HIGH_PORT);
		displayInfoPanel.setCell(6, 1, this.highPortRange);
		displayInfoPanel.setCell(6, 2, setButton);
		displayInfoPanel.setCell(6, 3, refresButton);

		// STUN SERVER ADDRESS #7
		refresButton = new Button();
		setButton = new Button();
		refresButton.setText("Refresh");
		setButton.setText("Set");
		refresButton.addClickListener(refreshListener);
		setButton.addClickListener(setClickListener);
		refresButton.setTitle(_STUN_SERVER_ADDRESS);
		setButton.setTitle(_STUN_SERVER_ADDRESS);
		this.stunServerAddressBox.setText(this.info.getStunServerAddress());
		displayInfoPanel.setCellText(7, 0, _STUN_SERVER_ADDRESS);
		displayInfoPanel.setCell(7, 1, this.stunServerAddressBox);
		displayInfoPanel.setCell(7, 2, setButton);
		displayInfoPanel.setCell(7, 3, refresButton);

		// STUN SERVER PUBLIC ADDRESS #8
		refresButton = new Button();
		setButton = new Button();
		refresButton.setText("Refresh");
		setButton.setText("Set");
		refresButton.addClickListener(refreshListener);
		setButton.addClickListener(setClickListener);
		refresButton.setTitle(_STUN_PUBLIC_ADDRESS);
		setButton.setTitle(_STUN_PUBLIC_ADDRESS);
		this.publicAddressFromStunBox.setText(this.info.getPublicAddressFromStun());
		displayInfoPanel.setCellText(8, 0, _STUN_PUBLIC_ADDRESS);
		displayInfoPanel.setCell(8, 1, this.publicAddressFromStunBox);
		displayInfoPanel.setCell(8, 2, setButton);
		displayInfoPanel.setCell(8, 3, refresButton);

		// STUN SERVER PORT #9
		refresButton = new Button();
		setButton = new Button();
		refresButton.setText("Refresh");
		setButton.setText("Set");
		refresButton.addClickListener(refreshListener);
		setButton.addClickListener(setClickListener);
		refresButton.setTitle(_STUN_SERVER_PORT);
		setButton.setTitle(_STUN_SERVER_PORT);
		this.stunServerPortBox.setText(this.info.getStunServerPort() + "");
		displayInfoPanel.setCellText(9, 0, _STUN_SERVER_PORT);
		displayInfoPanel.setCell(9, 1, this.stunServerPortBox);
		displayInfoPanel.setCell(9, 2, setButton);
		displayInfoPanel.setCell(9, 3, refresButton);

		CheckBoxClickListener checkBoxListener = new CheckBoxClickListener();

		// USE STUN #10
		refresButton = new Button();
		setButton = new Button();
		refresButton.setText("Refresh");
		setButton.setText("Set");
		refresButton.addClickListener(refreshListener);
		// setButton.addClickListener(setClickListener);
		refresButton.setTitle(_USE_STUN);
		setButton.setTitle(_USE_STUN);
		setButton.setEnabled(false);
		this.useStunCheckBox.setChecked(this.info.getUseStun());
		this.useStunCheckBox.addClickListener(checkBoxListener);
		this.useStunCheckBox.setTitle(_USE_STUN);
		displayInfoPanel.setCellText(10, 0, _USE_STUN);
		displayInfoPanel.setCell(10, 1, this.useStunCheckBox);
		displayInfoPanel.setCell(10, 2, setButton);
		displayInfoPanel.setCell(10, 3, refresButton);

		// USE PORT MAPPING #11
		refresButton = new Button();
		setButton = new Button();
		refresButton.setText("Refresh");
		setButton.setText("Set");
		refresButton.addClickListener(refreshListener);
		// setButton.addClickListener(setClickListener);
		refresButton.setTitle(_USE_PORT_MAPPING);
		setButton.setTitle(_USE_PORT_MAPPING);
		setButton.setEnabled(false);
		this.usePortMappingCheckBox.setChecked(this.info.getUsePortMapping());
		this.usePortMappingCheckBox.setTitle(_USE_PORT_MAPPING);
		this.usePortMappingCheckBox.addClickListener(checkBoxListener);
		displayInfoPanel.setCellText(11, 0, _USE_PORT_MAPPING);
		displayInfoPanel.setCell(11, 1, this.usePortMappingCheckBox);
		displayInfoPanel.setCell(11, 2, setButton);
		displayInfoPanel.setCell(11, 3, refresButton);

		// Add to roon pannel;
		rootPanel.setWidget(0, 0, displayInfoPanel);

		// Add formats
		displayInfoPanel.setHeader(4, "Media Formats");
		displayInfoPanel.setColumnWidth(4, "50px");
		// ListPanel formatPannel=new ListPanel();
		// formatPannel.setHeader(0,"Media Formats");
		Hyperlink nameLink = new Hyperlink("Audio", "Audio");
		nameLink.addClickListener(new DisplayHyperLinkClickListener("Audio", info.getAudioFormats(), predefinedAudioFormats, new PerformAudioFormatsSet()));
		// formatPannel.setCell(0, 0, nameLink);
		displayInfoPanel.setCell(0, 4, nameLink);
		nameLink = new Hyperlink("Video", "Video");
		nameLink.addClickListener(new DisplayHyperLinkClickListener("Video", info.getVideoFormats(), predefinedVideoFormats, new PerformVideoFormatsSet()));
		displayInfoPanel.setCell(1, 4, nameLink);

		for (int i = 2; i < 12; i++) {
			displayInfoPanel.setCell(i, 4, new DockPanel());
		}

		// formatPannel.setCell(1, 0, nameLink);
		// rootPanel.setWidget(0, 1, formatPannel);

	}

	private class DisplayHyperLinkClickListener implements ClickListener {

		XFormat[] predefined = null;
		String namePrefix = null;
		XFormat[] present = null;
		PerformActionClass action = null;

		public DisplayHyperLinkClickListener(String namePrefix, XFormat[] present, XFormat[] predefined, PerformActionClass action) {
			super();
			this.namePrefix = namePrefix;
			this.predefined = predefined;
			this.present = present;
			this.action = action;
		}

		public void onClick(Widget sender) {
			display.add(namePrefix + " Formats Configuration", new RTPMediaFormatConfigurationPage(present, predefined, action, info.getObjectName()));
		}
	}

	private class RefreshButtonClickListener implements ClickListener {

		public void onClick(Widget w) {
			// Button b = (Button) w;
			// String bText = b.getTitle();

			// FIXME: baranowb : do we need per property refresh???
			ServerConnection.rtpManagementServiceAsync.getManagerInfo(info.getObjectName(), new RefreshAsyncCallBack());
		}

	}

	private class SetButtonClickListener implements ClickListener {
		public void onClick(Widget w) {
			Button b = (Button) w;
			String bText = b.getTitle();

			RTPManagementServiceAsync service = ServerConnection.rtpManagementServiceAsync;
			// FIXME: should we perform validation?
			Enumer e = Enumer.getFromString(bText);

			switch (e.oeprationNumber) {
			case 0:
				break;

			case 1:
				service.setBindAddress(bindAddressBox.getText(), info.getObjectName(), new SetOperationAsyncCallBack(1));
				break;
			case 2:
				service.setJndiName(jndiNameBox.getText(), info.getObjectName(), new SetOperationAsyncCallBack(2));

				break;

			case 3:
				try {
					service.setPacketizationPeriod(Integer.valueOf(packetizationPeriodBox.getText()), info.getObjectName(), new SetOperationAsyncCallBack(3));
				} catch (Exception e3) {
					UserInterface.getLogPanel().error("Failed to change packetization period due to:\n" + e3.getMessage());
					packetizationPeriodBox.setText("" + info.getPacketizationPeriod());
				}

			case 4:
				try {
					service.setJitter(Integer.valueOf(jitterBox.getText()), info.getObjectName(), new SetOperationAsyncCallBack(4));
				} catch (Exception e3) {
					UserInterface.getLogPanel().error("Failed to change jitter period due to:\n" + e3.getMessage());
					jitterBox.setText(info.getJitter() + "");
				}
				break;
			case 5:
				try {
					int lowPort = Integer.valueOf(lowPortRangeBox.getText()).intValue();
					if (lowPort > info.getPortRange()[1].intValue()) {
						throw new RTPManagementOperationFailedException("Low Port cant be greater than high port!!! ");
					}
					service.setPortRange(new Integer(lowPort), info.getPortRange()[1], info.getObjectName(), new SetOperationAsyncCallBack(5));
				} catch (Exception e3) {
					UserInterface.getLogPanel().error("Failed to change low port due to:\n" + e3.getMessage());
					lowPortRangeBox.setText("" + info.getPortRange()[0]);
				}
				break;

			case 6:
				try {
					int highPort = Integer.valueOf(highPortRange.getText()).intValue();
					if (highPort < info.getPortRange()[0].intValue()) {
						throw new RTPManagementOperationFailedException("Low Port cant be greater than high port!!! ");
					}
					service.setPortRange(info.getPortRange()[0], new Integer(highPort), info.getObjectName(), new SetOperationAsyncCallBack(6));
				} catch (Exception e3) {
					UserInterface.getLogPanel().error("Failed to change low port due to:\n" + e3.getMessage());
					highPortRange.setText("" + info.getPortRange()[1]);
				}
				break;

			case 7:
				service.setStunServerAddress(stunServerAddressBox.getText(), info.getObjectName(), new SetOperationAsyncCallBack(7));
				break;
			case 8:
				try {
					service.setStunServerPort(Integer.valueOf(stunServerPortBox.getText()), info.getObjectName(), new SetOperationAsyncCallBack(8));
				} catch (Exception e3) {
					UserInterface.getLogPanel().error("Failed to change stun server port due to:\n" + e3.getMessage());
					stunServerPortBox.setText(info.getStunServerPort() + "");
				}
				break;
			case 9:
				service.setPublicAddressFromStun(publicAddressFromStunBox.getText(), info.getObjectName(), new SetOperationAsyncCallBack(9));
				break;
			default:
				break;
			}
		}
	}

	private class CheckBoxClickListener implements ClickListener {

		public void onClick(Widget w) {
			CheckBox cb = (CheckBox) w;
			String bText = cb.getTitle();

			int operation = Enumer.getFromString(bText).oeprationNumber;
			RTPManagementServiceAsync service = ServerConnection.rtpManagementServiceAsync;
			switch (operation) {
			case 10:
				service.setUseStun(new Boolean(useStunCheckBox.isChecked()), info.getObjectName(), new SetOperationAsyncCallBack(10));

				break;
			case 11:
				service.setUsePortMapping(new Boolean(usePortMappingCheckBox.isChecked()), info.getObjectName(), new SetOperationAsyncCallBack(11));
				break;
			default:
				break;
			}
		}
	}

	private class RefreshAsyncCallBack implements AsyncCallback {

		public void onFailure(Throwable t) {

			if (t instanceof RTPManagementMBeanDoesNotExistException) {
				UserInterface.getLogPanel().error("Failed refresh/change data due to:\n" + t.getMessage());
				parent.resetTo(null);
			} else {
				UserInterface.getLogPanel().error("Failed refresh data due to:\n" + t.getMessage());
			}

		}

		public void onSuccess(Object arg0) {
			refreshData(null, (RTPManagerInfo) arg0);

		}
	}

	private static class Enumer implements IsSerializable {
		private String operationName = null;
		private int oeprationNumber = -1;

		public Enumer(String operationName, int oeprationNumber) {
			super();
			this.oeprationNumber = oeprationNumber;
			this.operationName = operationName;
		}

		private static HashMap map = new HashMap();
		static {

			map.put(_OBJECT_NAME, new Enumer(_OBJECT_NAME, 0));
			map.put(_BIND_ADDRESS, new Enumer(_BIND_ADDRESS, 1));
			map.put(_JNDI_BINDING, new Enumer(_JNDI_BINDING, 2));
			map.put(_PACKETIZATION_PERIOD, new Enumer(_PACKETIZATION_PERIOD, 3));
			map.put(_JIITER, new Enumer(_JIITER, 4));
			map.put(_LOW_PORT, new Enumer(_LOW_PORT, 5));
			map.put(_HIGH_PORT, new Enumer(_HIGH_PORT, 6));
			map.put(_STUN_SERVER_ADDRESS, new Enumer(_STUN_SERVER_ADDRESS, 7));
			map.put(_STUN_SERVER_PORT, new Enumer(_STUN_SERVER_PORT, 8));
			map.put(_STUN_PUBLIC_ADDRESS, new Enumer(_STUN_PUBLIC_ADDRESS, 9));
			map.put(_USE_STUN, new Enumer(_USE_STUN, 10));
			map.put(_USE_PORT_MAPPING, new Enumer(_USE_PORT_MAPPING, 11));
		}

		public static Enumer getFromString(String operation) {
			return (Enumer) map.get(operation);
		}

	}

	private class SetOperationAsyncCallBack implements AsyncCallback {
		private int operation = -1;

		public SetOperationAsyncCallBack(int operation) {
			super();
			this.operation = operation;
		}

		public void onFailure(Throwable t) {

			UserInterface.getLogPanel().error("Failed to set property value due to:\n" + t.getMessage());
			// FIXME: do we need specific refresh?
			ServerConnection.rtpManagementServiceAsync.getManagerInfo(info.getObjectName(), new RefreshAsyncCallBack());

		}

		public void onSuccess(Object arg0) {
			// FIXME: do we need specific refresh?
			ServerConnection.rtpManagementServiceAsync.getManagerInfo(info.getObjectName(), new RefreshAsyncCallBack());
		}

	}

	private class PerformAudioFormatsSet implements PerformActionClass {

		public void perform(final XFormat[] xf, final RTPMediaFormatConfigurationPage page) {

			ServerConnection.rtpManagementServiceAsync.setAudioFormats(xf, info.getObjectName(), new AsyncCallback() {

				public void onFailure(Throwable t) {
					if (t instanceof RTPManagementMBeanDoesNotExistException)
						parent.resetTo(null);

					UserInterface.getLogPanel().error("Failed to set audio formats due to:\n" + t.getMessage());

				}

				public void onSuccess(Object arg0) {
					ServerConnection.rtpManagementServiceAsync.getAudioFormats(info.getObjectName(), new AsyncCallback(){

						public void onFailure(Throwable t) {
							// TODO Auto-generated method stub
							if (t instanceof RTPManagementMBeanDoesNotExistException)
								parent.resetTo(null);

							UserInterface.getLogPanel().error("Failed to retrieve audio formats due to:\n" + t.getMessage());
						}

						public void onSuccess(Object o) {
							XFormat[] xfs=(XFormat[]) o;
							info.setAudioFormats(xfs);
							page.refreshView(xfs);
							
						}});

				}
			});
		}

	}

	private class PerformVideoFormatsSet implements PerformActionClass {

		public void perform(final XFormat[] xf, final RTPMediaFormatConfigurationPage page) {

			ServerConnection.rtpManagementServiceAsync.setVideoFormats(xf, info.getObjectName(), new AsyncCallback() {

				public void onFailure(Throwable t) {
					if (t instanceof RTPManagementMBeanDoesNotExistException)
						parent.resetTo(null);

					UserInterface.getLogPanel().error("Failed to set video formats due to:\n" + t.getMessage());

				}

				public void onSuccess(Object arg0) {
					ServerConnection.rtpManagementServiceAsync.getVideoFormats(info.getObjectName(), new AsyncCallback(){

						public void onFailure(Throwable t) {
							// TODO Auto-generated method stub
							if (t instanceof RTPManagementMBeanDoesNotExistException)
								parent.resetTo(null);

							UserInterface.getLogPanel().error("Failed to retrieve video formats due to:\n" + t.getMessage());
						}

						public void onSuccess(Object o) {
							XFormat[] xfs=(XFormat[]) o;
							info.setVideoFormats(xfs);
							page.refreshView(xfs);
							
						}});

				}
			});
		}

	}

}
