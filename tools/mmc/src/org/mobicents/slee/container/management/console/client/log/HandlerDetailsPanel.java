package org.mobicents.slee.container.management.console.client.log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.mobicents.slee.container.management.console.client.Logger;
import org.mobicents.slee.container.management.console.client.ServerConnection;
import org.mobicents.slee.container.management.console.client.common.BrowseContainer;
import org.mobicents.slee.container.management.console.client.common.CommonControl;
import org.mobicents.slee.container.management.console.client.common.ListPanel;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class HandlerDetailsPanel extends Composite implements CommonControl {

	private BrowseContainer browseContainer = null;
	private HandlerInfo localInfo = null;

	// Logger name and FQD name
	private String loggerName = null;
	private String loggerFullName = null;

	// FIXME: Change this to be more general, now we allow only simple changes -
	// in case of formatter and filter... redo after incremental setter is
	// enclosed in GUI object
	private TextBox _h_nameBox = new TextBox();

	private TextBox _h_filterClassNameBox = new TextBox();

	private TextBox _h_formatterClassNameBox = new TextBox();

	private ListBox _h_levelList = new ListBox();

	private TextBox _h_notificatonInterval = new TextBox();

	private Hyperlink _th_name = new Hyperlink("Change", null);

	private Hyperlink _th_filterClassName = new Hyperlink("Change", null);

	private Hyperlink _th_formatterClassName = new Hyperlink("Change", null);

	private Hyperlink _th_NotificationInterval = new Hyperlink("Change", null);

	// GWT
	private DockPanel display = new DockPanel();

	public HandlerDetailsPanel(BrowseContainer myDisplay,
			HandlerInfo localInfo, String loggerName, String loggerFullName) {
		super();
		this.browseContainer = myDisplay;
		this.localInfo = localInfo;
		this.loggerName = loggerName;
		this.loggerFullName = loggerFullName;

		// FIXME: TMP until browse container does this!!!
		onInit();
		onShow();

	}

	public void onHide() {
		this.display.clear();

	}

	public void onInit() {
		this.initWidget(display);
		display.setWidth("100%");
		display.setHeight("100%");
		display.setVerticalAlignment(DockPanel.ALIGN_TOP);
		display.setHorizontalAlignment(DockPanel.ALIGN_LEFT);
		for (int i = 0; i < LogTreeNode._LEVELS.length; i++) {
			_h_levelList
					.addItem(LogTreeNode._LEVELS[i], LogTreeNode._LEVELS[i]);
			if (LogTreeNode._LEVELS[i].equals(localInfo.getLevel()))
				_h_levelList.setSelectedIndex(i);
		}

	}

	public void onShow() {

		Label topLabel = new Label("Handler settings");
		ListPanel options = new ListPanel();

		DockPanel innerDisplay = new DockPanel();
		// Add what we have and set some options

		// display.add(topLabel, DockPanel.NORTH);
		// display.setCellHorizontalAlignment(topLabel, DockPanel.ALIGN_CENTER);

		// display.add(options, DockPanel.CENTER);
		// display.setCellHorizontalAlignment(options, DockPanel.ALIGN_LEFT);
		// display.setCellWidth(options, "100%");
		innerDisplay.add(topLabel, DockPanel.NORTH);
		innerDisplay.setCellHorizontalAlignment(topLabel,
				DockPanel.ALIGN_CENTER);

		innerDisplay.add(options, DockPanel.CENTER);
		innerDisplay.setCellHorizontalAlignment(options, DockPanel.ALIGN_LEFT);
		innerDisplay.setCellWidth(options, "100%");

		display.add(innerDisplay, DockPanel.CENTER);
		display.setCellHorizontalAlignment(innerDisplay, DockPanel.ALIGN_LEFT);

		// options.setHeight("200px");
		options.setHeight("100%");
		options.setWidth("630px");

		// For now we support only simple init for formatter and filters.

		// Now we have to fill options, add click handlers,etc.
		Label l = new Label("Parameter");
		l.setTitle("Parameter name to be set.");
		options.setHeader(0, l);

		l = new Label("Trigger");
		l.setTitle("Triger that sets new value if needed.");
		options.setHeader(1, l);
		l = new Label("Value");
		l.setTitle("Present and new value.");
		options.setHeader(2, l);
		options.setColumnWidth(0, "45px");
		options.setColumnWidth(1, "30px");
		options.setColumnWidth(2, "100%");
		// FIXME:
		// Here we may want to refresh here, if removed, we have to make
		// browsecontainer go up
		// Problem is what should we do when handler shift? this one is removed
		// and one after this one in array shifts onto this place?
		// This should be automatical extraction - problem==Arrays?
		// We will depend on info passed in HandlerInfo.otherOptions now all
		// parameters are ready only, and passed as simple String->Stirng
		// mapping, afterwards
		// it will be String(name)->ParameterInfo(isEditable, valueSet, value,
		// isLimited);

		// FIXME: This code is tmp, just to enable basic features
		l = new Label("Name");
		l
				.setTitle("Name of the handler, if this is notification handler name cannot be changed. It exists only for human readoable purpose.");
		options.setCell(1, 0, l);
		options.setCell(1, 1, _th_name);
		options.setCell(1, 2, _h_nameBox);

		_h_nameBox.setText(localInfo.getName());
		
		if(localInfo.getName().toLowerCase().endsWith("NotificationHandler"))
		{
			_th_name.setVisible(false);
			_h_nameBox.setEnabled(false);
		}
		
		l = new Label("Level");
		l
				.setTitle("Level on which handler will act. Determines kinds of messages handler will receive.");
		options.setCell(2, 0, l);
		// options.setCell(2, 1, null);
		options.setCell(2, 2, _h_levelList);

		l = new Label("Classname");
		l.setTitle("Name of the class of this Handler.");
		options.setCell(3, 0, l);
		// options.setCell(3, 1, _th_name);
		options.setCellText(3, 2, "" + localInfo.getHandlerClassName());

		l = new Label("Filter classname");
		l
				.setTitle("Name of the class of filter that has been applied to this Handler.");
		options.setCell(4, 0, l);
		options.setCell(4, 1, _th_filterClassName);
		options.setCell(4, 2, _h_filterClassNameBox);
		_h_filterClassNameBox.setText(localInfo.getFilterClassName());	
		
		
		l = new Label("Formatter classname");
		l.setTitle("Name of the class of formatter applied to this Handler.");
		options.setCell(5, 0, l);
		options.setCell(5, 1, _th_formatterClassName);
		options.setCell(5, 2, _h_formatterClassNameBox);
		_h_formatterClassNameBox.setText(localInfo.getFormatterClassName());
		// Create some clickers - code is repeating, but otherwise it would get
		// a little bit tooo complicated
		// TODO: Genereate generic ClickListener with two interfaces - one to
		// fetch data, second to push it, and do error.

		class NameChangeClickListener implements ClickListener {

			public void onClick(Widget arg0) {

				class NameChangeAsyncCallback implements AsyncCallback {

					public void onFailure(Throwable arg0) {

						Logger.error("Failed to set new name due to:"
								+ arg0.getMessage());
						// Possibly rollback to screen above

					}

					public void onSuccess(Object arg0) {

						localInfo.setName(_h_nameBox.getText());

					}

				}

				ServerConnection.logServiceAsync.setHandlerName(loggerName,
						localInfo.getIndex(), _h_nameBox.getText(),
						new NameChangeAsyncCallback());

			}

		}

		class FilterClassChangeClickListener implements ClickListener {

			public void onClick(Widget arg0) {

				class FilterClassChangeAsyncCallback implements AsyncCallback {

					public void onFailure(Throwable arg0) {

						Logger.error("Failed to set new name due to:"
								+ arg0.getMessage());
						// Possibly rollback to screen above
						_h_filterClassNameBox.setText(localInfo
								.getFilterClassName());
					}

					public void onSuccess(Object arg0) {

						localInfo.setFilterClassName(_h_filterClassNameBox
								.getText());

					}

				}

				ServerConnection.logServiceAsync
						.setGenericHandlerFilterClassName(loggerName, localInfo
								.getIndex(), _h_filterClassNameBox.getText(),
								new FilterClassChangeAsyncCallback());

			}

		}

		class FormatterClassChangeClickListener implements ClickListener {

			public void onClick(Widget arg0) {

				class ForatterClassChangeAsyncCallback implements AsyncCallback {

					public void onFailure(Throwable arg0) {

						Logger.error("Failed to set new name due to:"
								+ arg0.getMessage());
						// Possibly rollback to screen above
						_h_formatterClassNameBox.setText(localInfo
								.getFormatterClassName());
					}

					public void onSuccess(Object arg0) {

						localInfo.setFilterClassName(_h_formatterClassNameBox
								.getText());

					}

				}

				ServerConnection.logServiceAsync
						.setGenericHandlerFormatterClassName(loggerName,
								localInfo.getIndex(), _h_formatterClassNameBox
										.getText(),
								new ForatterClassChangeAsyncCallback());

			}

		}

		class LevelChangeListener implements ChangeListener {

			public void onChange(Widget sender) {
				ListBox ss = (ListBox) sender;
				final String logLevel = ss.getValue(ss.getSelectedIndex());

				class LevelChangeCallBack implements AsyncCallback {

					public void onFailure(Throwable caught) {
						Logger.error("Could not set logger level for due to["
								+ caught.getMessage() + "]");
					}

					public void onSuccess(Object result) {

						localInfo.setLevel((String) result);

					}

				}

				ServerConnection.logServiceAsync.setGenericHandlerLevel(
						loggerName, localInfo.getIndex(), _h_levelList
								.getValue(_h_levelList.getSelectedIndex()),
						new LevelChangeCallBack());
			}

		}

		// Add

		_h_levelList.addChangeListener(new LevelChangeListener());

		_th_name.addClickListener(new NameChangeClickListener());

		_th_filterClassName
				.addClickListener(new FilterClassChangeClickListener());

		_th_formatterClassName
				.addClickListener(new FormatterClassChangeClickListener());

		boolean setNotofication = false;

		if (localInfo.getHandlerClassName().toLowerCase().endsWith(
				"NotificationHandler".toLowerCase())) {
			setNotofication = true;
			_th_NotificationInterval.setVisible(false);
			_h_notificatonInterval.setEnabled(false);
			l = new Label("Notification interval");
			l
					.setTitle("Number of log entries per notification - thus interval between refresh of log screens. This is not time between refresh.");
			options.setCell(6, 0, l);
			options.setCell(6, 1, _th_NotificationInterval);
			options.setCell(6, 2, _h_notificatonInterval);

			class NotificationIntervalChangeClickListener implements
					ClickListener {

				public void onClick(Widget arg0) {

					class NotificationIntervalChangeAsyncCallback implements
							AsyncCallback {

						public void onFailure(Throwable arg0) {

							Logger.error("Failed to set new name due to:"
									+ arg0.getMessage());
							// Possibly rollback to screen above
							_h_notificatonInterval.setText((String) localInfo
									.getOtherOptions().get(
											"notificationInterval"));
						}

						public void onSuccess(Object arg0) {

							localInfo.getOtherOptions().put(
									"notificationInterval",
									_h_formatterClassNameBox.getText());

						}

					}

					ServerConnection.logServiceAsync
							.setHandlerNotificationInterval(
									loggerName,
									Integer.parseInt(_h_notificatonInterval
											.getText()),
									new NotificationIntervalChangeAsyncCallback());

				}

			}
			_th_NotificationInterval
					.addClickListener(new NotificationIntervalChangeClickListener());
		}

		// For now all other properties are read only.

		int index = 6;
		if (setNotofication)
			index++;

		HashMap mp = localInfo.getOtherOptions();

		Iterator it = mp.keySet().iterator();

		while (it.hasNext()) {
			String key = (String) it.next();
			l = new Label("" + key);
			l.setTitle("Not supported, read only option.");
			options.setCell(index, 0, l);
			// options.setCell(index, 1, _th_NotificationInterval);
			options.setCell(index, 2, new Label("" + mp.get(key)));
			index++;

		}

	}
}
