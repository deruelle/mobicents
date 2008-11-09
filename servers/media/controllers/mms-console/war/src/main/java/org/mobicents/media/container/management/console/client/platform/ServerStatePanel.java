/*
 * Mobicents: The Open Source VoIP Middleware Platform
 *
 * Copyright 2003-2006, Mobicents, 
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
package org.mobicents.media.container.management.console.client.platform;

import org.mobicents.media.container.management.console.client.ServerConnection;
import org.mobicents.media.container.management.console.client.common.ServerCallback;
import org.mobicents.media.container.management.console.client.common.UserInterface;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Zappaterra
 * 
 */
public class ServerStatePanel extends VerticalPanel {

	protected HTML state = new HTML("NOT AVAILABLE");

	protected Button startButton = new Button();

	protected Button stopButton = new Button();

	protected Button tearDownButton = new Button();

	protected Timer timer;

	private boolean isTimerRunning = false;

	private String version = null;

	private ServerState serverState = ServerState.RUNNING;

	private PlatformManagementServiceAsync service = ServerConnection.platformManagementServiceAsync;

	public ServerStatePanel() {
		super();

		startButton.setTitle("Start");
		stopButton.setTitle("Stop");
		tearDownButton.setTitle("Teardown");
		final Label header = new Label("MMS is");
		ServerCallback callback = new ServerCallback(this) {
			public void onSuccess(Object result) {
				header.setText((String) result + " is");
			}
		};

		service.getVersion(callback);
		HorizontalPanel buttonsPanel = new HorizontalPanel();
		buttonsPanel.setSpacing(5);
		buttonsPanel.add(startButton);
		buttonsPanel.add(stopButton);
		buttonsPanel.add(tearDownButton);

		startButton.setEnabled(false);
		stopButton.setEnabled(false);
		tearDownButton.setEnabled(false);

		startButton.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				onStartButton();
			}
		});

		stopButton.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				onStopButton();
			}
		});

		tearDownButton.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				onTeardownButton();
			}
		});

		timer = new Timer() {
			public void run() {
				updateState();
			}
		};

		add(header);
		add(state);
		add(buttonsPanel);

		setStyleName("serverstate-ServerStatePanel");
		setSpacing(5);
		state.setStyleName("serverstate-ServerStatePanel-state");

		setCellHorizontalAlignment(header, HasHorizontalAlignment.ALIGN_CENTER);
		setCellHorizontalAlignment(state, HasHorizontalAlignment.ALIGN_CENTER);
		setCellHorizontalAlignment(buttonsPanel, HasHorizontalAlignment.ALIGN_CENTER);

	}

	protected void onStartButton() {
		ServerCallback callback = new ServerCallback(this) {
		};
		service.start(callback);
	}

	protected void onStopButton() {
		ServerCallback callback = new ServerCallback(this) {
		};
		service.stop(callback);
	}

	protected void onTeardownButton() {
		ServerCallback callback = new ServerCallback(this) {
		};
		service.tearDown(callback);
	}

	private void updateStateLabel() {

		state.setHTML("<image src='images/" + serverState.getImageName() + "' align='absbottom' />&nbsp;" + serverState.getState());
		
		state.setStyleName("serverstate-ServerStatePanel-state");
		state.addStyleName("serverstate-ServerStatePanel-state-" + serverState.getState().toLowerCase());

	}

	private void setButtonEnabled(Button button, boolean enabled) {
		String e = enabled ? "" : ".disabled";
		button.setHTML("<image src='images/server.state." + button.getTitle().toLowerCase() + e + ".gif' align='absbottom'/>" + button.getTitle());
		
		button.setEnabled(enabled);
	}

	private void updateButtons() {

		if (serverState.getState().equals(ServerState.STOPPED.getState())) {
			setButtonEnabled(startButton, true);
			setButtonEnabled(stopButton, false);
			setButtonEnabled(tearDownButton, false);
		} else if (serverState.getState().equals(ServerState.RUNNING.getState())) {
			setButtonEnabled(startButton, false);
			setButtonEnabled(stopButton, true);
			setButtonEnabled(tearDownButton, true);
		} else if (serverState.getState().equals(ServerState.STOPPING.getState())) {
			setButtonEnabled(startButton, true);
			setButtonEnabled(stopButton, false);
			setButtonEnabled(tearDownButton, true);
		}
	}

	private void updateGUI() {

		updateStateLabel();

		updateButtons();

	}

	private void updateState() {
		ServerCallback callback = new ServerCallback(this) {
			public void onSuccess(Object result) {

				serverState = (ServerState) result;

				updateGUI();

				timer.schedule(1000);

			}
		};
		service.getState(callback);
	}

	protected void startUpdating() {
		if (isTimerRunning)
			return;

		timer.run();
		isTimerRunning = true;
	}

	protected void stopUpdating() {
		if (!isTimerRunning)
			return;

		timer.cancel();
		isTimerRunning = false;
	}

}
