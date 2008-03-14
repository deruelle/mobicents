/*
 * Mobicents: The Open Source VoIP Middleware Platform
 *
 * Copyright 2003-2008, Mobicents, 
 * and individual contributors as indicated
 * by the @authors tag. See the copyright.txt 
 * in the distribution for a full listing of   
 * individual contributors.
 *
 * This is free software; you can redistribute it
 * and/or modify it under the terms of the 
 * GNU General Public License (GPL) as
 * published by the Free Software Foundation; 
 * either version 2 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that 
 * it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR 
 * PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the 
 * GNU General Public
 * License along with this software; 
 * if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, 
 * Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 */
package org.mobicents.slee.container.management.console.client.log;

import org.mobicents.slee.container.management.console.client.Logger;
import org.mobicents.slee.container.management.console.client.ServerConnection;
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

/**
 * This component allows user to perform some operations - setting defualt
 * handler/logger level, readingCFG, reseting/clearing loggers. It also
 * 
 * @author baranowb
 * 
 */
public class DefaultOperationsPanel extends Composite implements CommonControl {

	private ListBox _defaultHandlerLevel = new ListBox();

	private ListBox _defaultLoggerLevel = new ListBox();

	private TextBox _resetLoggerBox = new TextBox();

	private TextBox _clearLoggerBox = new TextBox();

	private TextBox _cfgReadBox = new TextBox();

	private TextBox _defaultNotificationInterval = new TextBox();
	// This has to also be present in logger details panel, to add child.
	private TextBox _loggerToAdd = new TextBox();
	private Hyperlink clearLink = new Hyperlink("Trigger", null);
	private Hyperlink addLoggerLink = new Hyperlink("Trigger", null);
	private Hyperlink readLink = new Hyperlink("Trigger", null);
	private Hyperlink resetLink = new Hyperlink("Trigger", null);
	private Hyperlink defaultNotificationIntervalLink = new Hyperlink(
			"Trigger", null);
	// private Hyperlink deafaultLoggerLevelLink = new Hyperlink("Trigger",
	// null);
	// private Hyperlink defaultHandlerLevelLink = new Hyperlink("Trigger",
	// null);
	private CommonControl tree = null;

	private static final String _ll_Explanation = "Sets default logger level - this is used when no level is specified, loggers are being reset or create via tree.";
	private static final String _hl_Explanation = "Sets default handler level - this is used when no level is specified and handler is beeing created";
	private static final String _cfg_Explanation = "Reads java.util.logging properties file in order to configure loggers, requires URI - can be left empty";
	private static final String _cclear_Explanation = "Set logger levels to OFF, removes all handlers. Requires regex to match logger names or empty to match mobicents domain.";
	private static final String _reset_Explanation = "Resets logger levels to current default value. Requires regex to match logger names or empty to match mobicents domain.";
	private static final String _addLogger_Explanation = "Type fqdn name of logger that is to be inserted.";
	private static final String _defaultNotificationInterval_Explanation = "Set number of log records per notification fired by handlers.";

	private static final String __CLEAR_LINK = "__CLEAR_LINK";
	private static final String __ADD_LOGGER_LINK = "__ADD_LOGGER_LINK";
	private static final String __READ_CFG_LINK = "__READ_CFG_LINK";
	private static final String __RESET_LOGGERS_LINK = "__RESET_LOGGERS_LINK";
	private static final String __DEFAULT_NOTIFICATION_LINK = "__DEFAULT_NOTIFICATION_LINK";

	private DockPanel display = new DockPanel();

	public DefaultOperationsPanel(CommonControl tree) {
		super();

		this.tree = tree;

		initWidget(display);

	}

	public void onHide() {
		this.display.clear();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.slee.container.management.console.client.common.CommonControl#onInit()
	 */
	public void onInit() {

		ListPanel operations = new ListPanel();

		this.display.setHorizontalAlignment(DockPanel.ALIGN_CENTER);
		this.display.setVerticalAlignment(DockPanel.ALIGN_TOP);
		this.display.add(operations, DockPanel.CENTER);
		this.display.setCellHeight(operations, "100%");
		this.display.setCellWidth(operations, "100%");

		operations.setWidth("100%");

		operations.setHeader(0, "Operation name");
		operations.setHeader(1, "Parameters");
		operations.setHeader(2, "Operation trigger");
		// operations.setHeader(3, "Operation detials");

		operations.setColumnWidth(0, "33%");
		operations.setColumnWidth(1, "33%");
		operations.setColumnWidth(2, "33%");
		// operations.setColumnWidth(3, "55%");

		// Operations
		// Set degault logger level
		operations.setCellText(1, 0, "Default logger level");
		operations.setCell(1, 1, null);
		operations.setCell(1, 2, _defaultLoggerLevel);
		// operations.setCellText(1, 3, _ll_Explanation);

		_defaultLoggerLevel.setTitle(_ll_Explanation);

		// Set default handler level
		operations.setCellText(2, 0, "Default handler level");
		operations.setCell(2, 1, null);
		operations.setCell(2, 2, _defaultHandlerLevel);
		// operations.setCell(2, 3, new Label(_hl_Explanation,true));
		_defaultHandlerLevel.setTitle(_hl_Explanation);

		// read cfg

		// final TextBox uri=new TextBox();
		operations.setCellText(3, 0, "Read logger cfg");
		operations.setCell(3, 1, _cfgReadBox);
		operations.setCell(3, 2, readLink);
		// operations.setCellText(3, 3, _cfg_Explanation);
		readLink.setTitle(_cfg_Explanation);

		// reset loggers
		// Hyperlink resetLink = new Hyperlink("Trigger", null);
		// final TextBox resetRegex=new TextBox();
		operations.setCellText(4, 0, "Reset loggers level");
		operations.setCell(4, 1, _resetLoggerBox);
		operations.setCell(4, 2, resetLink);
		resetLink.setTitle(_reset_Explanation);

		// clear loggers loggers
		// final TextBox clearRegex=new TextBox();
		operations.setCellText(5, 0, "Turns off loggers");
		operations.setCell(5, 1, _clearLoggerBox);
		operations.setCell(5, 2, clearLink);
		clearLink.setTitle(_cclear_Explanation);

		// add logger - pass fqdn
		operations.setCellText(6, 0, "Default notification interval");
		operations.setCell(6, 1, _defaultNotificationInterval);
		operations.setCell(6, 2, defaultNotificationIntervalLink);
		defaultNotificationIntervalLink
				.setTitle(_defaultNotificationInterval_Explanation);
		// final TextBox clearRegex=new TextBox();
		operations.setCellText(7, 0, "Add logger");
		operations.setCell(7, 1, _loggerToAdd);
		operations.setCell(7, 2, addLoggerLink);
		addLoggerLink.setTitle(_addLogger_Explanation);

		// init click handler for boxes

		// ADD

		//DefaultOptionsClickListener commonListener = new DefaultOptionsClickListener();
		clearLink.addClickListener(new DefaultOptionsClickListener(__CLEAR_LINK));
		addLoggerLink.addClickListener(new DefaultOptionsClickListener(__ADD_LOGGER_LINK));
		readLink.addClickListener(new DefaultOptionsClickListener(__READ_CFG_LINK));
		resetLink.addClickListener(new DefaultOptionsClickListener(__RESET_LOGGERS_LINK));
		defaultNotificationIntervalLink.addClickListener(new DefaultOptionsClickListener(__DEFAULT_NOTIFICATION_LINK));

		//clearLink.setTitle(__CLEAR_LINK);
		//addLoggerLink.setTitle(__ADD_LOGGER_LINK);
		//readLink.setTitle(__READ_CFG_LINK);
		//resetLink.setTitle(__RESET_LOGGERS_LINK);
		//defaultNotificationIntervalLink.setTitle(__DEFAULT_NOTIFICATION_LINK);

		_defaultHandlerLevel.addChangeListener(new LevelChangeListener(true));
		_defaultLoggerLevel.addChangeListener(new LevelChangeListener(false));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.slee.container.management.console.client.common.CommonControl#onShow()
	 */
	public void onShow() {

		// INJECT VALUES

		ServerConnection.logServiceAsync
				.getDefaultHandlerLevel(new AsyncCallback() {

					public void onFailure(Throwable arg0) {

						Logger
								.error("Failed to fetch Defautl handler level due to: "
										+ arg0.getMessage());

					}

					public void onSuccess(Object result) {

						for (int i = 0; i < LogTreeNode._LEVELS.length; i++) {
							_defaultHandlerLevel.addItem(
									LogTreeNode._LEVELS[i],
									LogTreeNode._LEVELS[i]);

							if (result.equals(LogTreeNode._LEVELS[i]))
								_defaultHandlerLevel.setSelectedIndex(i);
						}

					}
				});

		ServerConnection.logServiceAsync
				.getDefaultLoggerLevel(new AsyncCallback() {

					public void onFailure(Throwable arg0) {

						Logger
								.error("Failed to fetch Defautl logger level due to: "
										+ arg0.getMessage());
					}

					public void onSuccess(Object result) {
						for (int i = 0; i < LogTreeNode._LEVELS.length; i++) {

							_defaultLoggerLevel.addItem(LogTreeNode._LEVELS[i],
									LogTreeNode._LEVELS[i]);
							if (result.equals(LogTreeNode._LEVELS[i]))
								_defaultLoggerLevel.setSelectedIndex(i);
						}

					}
				});

	}

	class DefaultOptionsClickListener implements ClickListener {

		private String actionTitle=null;
		
		public DefaultOptionsClickListener(String action)
		{
			this.actionTitle=action;
		}
		
		
		public void onClick(Widget source) {

			//String title = source.getTitle();

			class DefaultOperationsCallBack implements AsyncCallback {

				public DefaultOperationsCallBack() {

				}

				public void onFailure(Throwable arg0) {

					Logger.error("Failed to perform operation due to:"
							+ arg0.getMessage());

				}

				public void onSuccess(Object arg0) {

					tree.onHide();
					tree.onShow();

				}

			}
			;

			if (actionTitle.equals(__ADD_LOGGER_LINK)) {

				ServerConnection.logServiceAsync.addLogger(_loggerToAdd
						.getText(), _defaultLoggerLevel
						.getValue(_defaultLoggerLevel.getSelectedIndex()),
						new DefaultOperationsCallBack());

			} else if (actionTitle.equals(__CLEAR_LINK)) {

				ServerConnection.logServiceAsync.clearLoggers(_loggerToAdd
						.getText(), new DefaultOperationsCallBack());

			} else if (actionTitle.equals(__READ_CFG_LINK)) {

				ServerConnection.logServiceAsync.reReadConf(_cfgReadBox
						.getText(), new DefaultOperationsCallBack());

			} else if (actionTitle.equals(__RESET_LOGGERS_LINK)) {

				ServerConnection.logServiceAsync.resetLoggerLevel(
						_resetLoggerBox.getText(),
						new DefaultOperationsCallBack());

			}else if (actionTitle.equals(__DEFAULT_NOTIFICATION_LINK)) {

				ServerConnection.logServiceAsync
						.setDefaultNotificationInterval(
								_defaultNotificationInterval.getText(),
								new DefaultOperationsCallBack());

			} else {

				Logger.error("Failed to perform ooperation, source[" + source
						+ "] [" + actionTitle + "]");
			}

		}

	}

	class LevelChangeListener implements ChangeListener {
		private boolean defaultHandlerLevel = false;

		public LevelChangeListener(boolean defaultHandlerLevel) {
			this.defaultHandlerLevel = defaultHandlerLevel;
		}

		public void onChange(Widget source) {

			class DumpCallBack implements AsyncCallback {

				public void onFailure(Throwable arg0) {
					// TODO Auto-generated method stub

				}

				public void onSuccess(Object arg0) {
					// TODO Auto-generated method stub

				}

			}

			String level = null;

			if (this.defaultHandlerLevel) {
				level = _defaultHandlerLevel.getValue(_defaultHandlerLevel
						.getSelectedIndex());
				ServerConnection.logServiceAsync.setDefaultHandlerLevel(level,
						new DumpCallBack());
			} else {
				level = _defaultLoggerLevel.getValue(_defaultLoggerLevel
						.getSelectedIndex());
				ServerConnection.logServiceAsync.setDefaultLoggerLevel(level,
						new DumpCallBack());
			}

		}

	}

}
