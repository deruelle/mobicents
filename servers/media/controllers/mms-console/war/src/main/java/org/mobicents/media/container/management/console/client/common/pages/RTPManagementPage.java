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
package org.mobicents.media.container.management.console.client.common.pages;

import org.mobicents.media.container.management.console.client.ServerConnection;
import org.mobicents.media.container.management.console.client.common.CardControl;
import org.mobicents.media.container.management.console.client.common.SmartTabPage;
import org.mobicents.media.container.management.console.client.common.UserInterface;
import org.mobicents.media.container.management.console.client.rtp.RTPManagerDetailsCard;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author baranowb
 * 
 */
public class RTPManagementPage extends SmartTabPage {

	private CardControl cardControl = new CardControl();

	public static SmartTabPageInfo getInfo() {
		return new SmartTabPageInfo("<image src='images/rtp.management.gif' /> RTP Management", "RTP Management") {
			protected SmartTabPage createInstance() {
				return new RTPManagementPage();
			}
		};
	}

	public RTPManagementPage() {
		super();
		// initWidget(switcher);
		
		initWidget(cardControl);
		

	}

	public void onHide() {

		super.onHide();
		this.cardControl.onHide();
	}

	public void onInit() {

		
		this.cardControl.onInit();
		ServerConnection.rtpManagementServiceAsync.listRTPMBeans(new FetchRTPManagersListAsyncCallback(this));
		
	}

	public void onShow() {

		super.onShow();
		this.cardControl.onShow();

	}

	public void resetTo(String objectName)
	{
		
	}
	
	
	/**
	 * Creates an HTML fragment that places an image & caption together, for use
	 * in a group header.
	 * 
	 * @param imageUrl
	 *            the url of the icon image to be used
	 * @param caption
	 *            the group caption
	 * @return the header HTML fragment
	 */
	private String createHeaderHTML(String imageUrl, String caption) {
		return "<table align='left'><tr>" + "<td><img src='" + imageUrl + "'></td>" + "<td style='vertical-align:middle'><b style='white-space:nowrap'>" + caption + "</b></td>"
				+ "</tr></table>";
	}

	private class FetchRTPManagersListAsyncCallback implements AsyncCallback {
		protected RTPManagementPage parent=null;
		
		public FetchRTPManagersListAsyncCallback(RTPManagementPage parent) {
			super();
			this.parent = parent;
		}

		public void onFailure(Throwable arg0) {
			UserInterface.getLogPanel().error("Failed to obtain list of RTP management mbeans due to:\n" + arg0.getMessage());

		}

		public void onSuccess(Object arg0) {
			String[] list = (String[]) arg0;
			if (list != null)
				for (int i = 0; i < list.length; i++) {
					String name = list[i];
					//FIXME:Add cards
					
					name=name.substring(name.indexOf(":")+1);
					name=name.substring(name.indexOf("service=RTPManager,")+"service=RTPManager,".length());
					cardControl.add(new RTPManagerDetailsCard(list[i], parent), "<image align='absbottom' src='images/rtp.manager.gif' /> "+
							name , true);
					
						
				}
			
			cardControl.setWidth("100%");
			cardControl.selectTab(0);
		}

	}

	
	
}
