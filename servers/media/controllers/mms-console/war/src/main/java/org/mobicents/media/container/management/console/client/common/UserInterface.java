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
package org.mobicents.media.container.management.console.client.common;

import org.mobicents.media.container.management.console.client.common.pages.EndpointManagementPage;
import org.mobicents.media.container.management.console.client.common.pages.RTPManagementPage;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UserInterface extends VerticalPanel implements HasHorizontalAlignment {
	
	static private UserInterface instance = new UserInterface();
	
	static public UserInterface getInstance() {
		return instance;
	}
	
	static private LogPanel logPanel;
	
	private UserInterface() {
		
		setWidth("800px");
		setHeight("100%");

		logPanel = new LogPanel();

		TopPanel topPanel = new TopPanel();

		SmartTabPanel smartTabPanel = new SmartTabPanel();
						
		smartTabPanel.add(RTPManagementPage.getInfo());	
		smartTabPanel.add(EndpointManagementPage.getInfo());
		add(topPanel);
		add(smartTabPanel);
		add(logPanel);

		setCellHeight(smartTabPanel, "100%");
		
		RootPanel.get().add(this);

	}
	
	static public LogPanel getLogPanel() {
		return logPanel;
	}

}
