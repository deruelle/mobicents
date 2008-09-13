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
import org.mobicents.media.container.management.console.client.common.BrowseContainer;
import org.mobicents.media.container.management.console.client.common.SmartTabPage;
import org.mobicents.media.container.management.console.client.common.UserInterface;
import org.mobicents.media.container.management.console.client.rtp.RTPManagerInfo;
import org.mobicents.media.container.management.console.client.rtp.XFormat;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.StackPanel;

/**
 * @author baranowb
 *
 */
public class RTPManagementPage extends SmartTabPage {

	
	
	private StackPanel switcher=new StackPanel();

	private BrowseContainer browseContainer=new BrowseContainer();
	
	protected RichTextArea box=new RichTextArea();
	protected StringBuffer buffer=new StringBuffer();
	
	public static SmartTabPageInfo getInfo() {
        return new SmartTabPageInfo("<image src='images/log.mgmt.1.jpg' /> RTP Management",
                "RTP Management") {
            protected SmartTabPage createInstance() {
                return new RTPManagementPage();
            }
        };
    }
	
	public RTPManagementPage() {
		super();
		//initWidget(switcher);
		
		initWidget(box);
	}

	public void onHide() {
		// TODO Auto-generated method stub
		super.onHide();
	}

	public void onInit() {
		//this.switcher.setHeight("100%");
		//this.switcher.setWidth("100%");
		//this.switcher.add(logTree, createHeaderHTML("images/log.mgmt.log_configuration.jpg", "Logger Tree"), true);
		//logTree.onInit();
		
		//this.switcher.add(new Hyperlink("CONSOLE",true,null), createHeaderHTML("images/log.mgmt.log_console.jpg", "Console"), true);
	}


	public void onShow() {
		// TODO Auto-generated method stub
		super.onShow();
		//logTree.onShow();
		ServerConnection.rtpManagementServiceAsync.listRTPMBeans(new AsyncCallback(){

			public void onFailure(Throwable arg0) {
				box.setText(arg0.getMessage());
				
			}

			public void onSuccess(Object arg0) {
				String[] test=(String[]) arg0;
				UserInterface.getLogPanel().info("RETURN:["+test.length+"]");
				for(int i=0;i<test.length;i++)
				{
					String on=test[i];
					UserInterface.getLogPanel().info("ON["+on+"]");
					doCall(on);
					
					
				}
				
			}});
		
	}
	private void doCall(String on)
	{
		AsyncCallback asyncCallback =new AsyncCB(on);
		ServerConnection.rtpManagementServiceAsync.getManagerInfo(on, asyncCallback);
	}
	private class AsyncCB implements AsyncCallback
	{

		String on=null;
		public AsyncCB(String on) {
			super();
			this.on = on;
		}

		public void onFailure(Throwable arg0) {
			buffer.append("ERRRO["+on+"]:\n"+arg0.getMessage()+"\n");
			box.setText(buffer.toString());
			UserInterface.getLogPanel().info("getMGRInfo Error["+arg0.getMessage()+"]");
		}

		public void onSuccess(Object arg0) {
			UserInterface.getLogPanel().info("getMGRInfo Success[================]");
			try{
			RTPManagerInfo info=(RTPManagerInfo)arg0;
			UserInterface.getLogPanel().info("getMGRInfo Success["+info.getObjectName()+"]");
			UserInterface.getLogPanel().info("[ON]:["+info.getObjectName()+"]\n");
			UserInterface.getLogPanel().info("[BIND]:["+info.getBindAddress()+"]\n");
			UserInterface.getLogPanel().info("[JNDI]:["+info.getJndiName()+"]\n");
			UserInterface.getLogPanel().info("[STUN_P]:["+info.getPublicAddressFromStun()+"]\n");
			UserInterface.getLogPanel().info("[STUN]:["+info.getStunServerAddress()+"]\n");
			UserInterface.getLogPanel().info("[AF]:[");
			XFormat[] xfs=info.getAudioFormats();
			if(xfs!=null)
			for(int i=0;i<xfs.length;i++)
				if(xfs[i]!=null)
					UserInterface.getLogPanel().info("rtp:"+xfs[i].getRtpMap()+"- format:"+xfs[i].getFormat());
				else
					UserInterface.getLogPanel().info("rtp["+i+"]");
			else
				UserInterface.getLogPanel().info(xfs+"");
			UserInterface.getLogPanel().info("]\n");
			UserInterface.getLogPanel().info("[VF]:[");
			xfs=info.getVideoFormats();
			if(xfs!=null)
			for(int i=0;i<xfs.length;i++)
				if(xfs[i]!=null)
					UserInterface.getLogPanel().info("rtp:"+xfs[i].getRtpMap()+"- format:"+xfs[i].getFormat());
				else
					UserInterface.getLogPanel().info("rtp["+i+"]");
			else
				UserInterface.getLogPanel().info(xfs+"");
			UserInterface.getLogPanel().info("]\n");
			UserInterface.getLogPanel().info("[JITTER]:["+info.getJitter()+"]\n");
			UserInterface.getLogPanel().info("[PP]:["+info.getPacketizationPeriod()+"]\n");
			UserInterface.getLogPanel().info("[PR]:["+info.getPortRange()+"]\n");
			UserInterface.getLogPanel().info("[SSP]:["+info.getStunServerPort()+"]\n");
			UserInterface.getLogPanel().info("[PORT MAP]:["+info.getUsePortMapping()+"]\n");
			UserInterface.getLogPanel().info("[U STUN]:["+info.getUseStun()+"]\n");

			box.setText(buffer.toString());
			box.setVisible(false);
			box.setVisible(true);
			}catch(Exception e)
			{
				UserInterface.getLogPanel().error(e.getMessage());
			}
			
		}
		
	}
	
	/**
	   * Creates an HTML fragment that places an image & caption together, for use
	   * in a group header.
	   * 
	   * @param imageUrl the url of the icon image to be used
	   * @param caption the group caption
	   * @return the header HTML fragment
	   */
	  private String createHeaderHTML(String imageUrl, String caption) {
	    return "<table align='left'><tr>" + "<td><img src='" + imageUrl + "'></td>"
	      + "<td style='vertical-align:middle'><b style='white-space:nowrap'>"
	      + caption + "</b></td>" + "</tr></table>";
	  }
	
	
	
}
