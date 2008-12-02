package org.mobicents.media.container.management.console.client.endpoint;


import java.util.Date;

import org.mobicents.media.container.management.console.client.ServerConnection;
import org.mobicents.media.container.management.console.client.common.BrowseContainer;
import org.mobicents.media.container.management.console.client.common.ListPanel;
import org.mobicents.media.container.management.console.client.common.UserInterface;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


public class EndpointDataManagementConnectionsDisplay extends Composite {

	
	
	protected EndpointFullInfo info=null;
	protected DockPanel dataDisplay=new DockPanel();
	protected ListPanel connectionList=new ListPanel();
	protected BrowseContainer display=null;
	protected ActionPerform refreshDataAction;
	protected ActionPerform onErrorAction;
	
	//protected Timer refreshTimer=null;
	
	
	protected final static String imageUrl="images/endpoints.connections.jpg";
	
	public EndpointDataManagementConnectionsDisplay(ActionPerform refreshAction,ActionPerform onErrorAction) {
		super();
		this.refreshDataAction=refreshAction;
		this.onErrorAction=onErrorAction;
		initWidget(dataDisplay);
		
		this.setHeight("100%");
		this.setWidth("100%");
		
		this.dataDisplay.setHeight("100%");
		this.dataDisplay.setWidth("100%");
		//Label ll=new Label("ADD HEADER");
		Image ll=new Image(imageUrl);
		this.dataDisplay.add(ll, this.dataDisplay.NORTH);
		this.dataDisplay.setCellHeight(ll, "20px");
		this.dataDisplay.setCellWidth(ll, "100%");
		this.dataDisplay.setCellHorizontalAlignment(ll, HasHorizontalAlignment.ALIGN_CENTER);
		this.dataDisplay.setCellVerticalAlignment(ll, HasVerticalAlignment.ALIGN_TOP);
		
		this.dataDisplay.add(connectionList, this.dataDisplay.CENTER);
		this.dataDisplay.setCellHeight(connectionList, "100%");
		this.dataDisplay.setCellWidth(connectionList, "100%");
		this.dataDisplay.setCellHorizontalAlignment(connectionList, HasHorizontalAlignment.ALIGN_LEFT);
		this.dataDisplay.setCellVerticalAlignment(connectionList, HasVerticalAlignment.ALIGN_TOP);
		
		
		
		
		
	}

	public void populateWithData(EndpointFullInfo info)
	{
		try{

		this.info=info;
		if(info==null)
		{
			onErrorAction.performAction();
		}			

		connectionList.emptyTable();
		ConnectionInfo[] infos=info.getConnectionsInfo();
		connectionList.setHeader(0, "#");
		connectionList.setHeader(1, "Connection Id");
		connectionList.setHeader(2, "Creation Time");
		//connectionList.setHeader(3, "Packets");
		connectionList.setHeader(3, "Other end");
		connectionList.setHeader(4, "Local SDP");
		connectionList.setHeader(5, "Remote SDP");
		connectionList.setHeader(6, "Parameters");
		connectionList.setHeader(7, "Break");

		
		connectionList.setColumnWidth(0, "5%");
		connectionList.setColumnWidth(1, "20%");
		connectionList.setColumnWidth(2, "20%");
		//connectionList.setColumnWidth(3, "5%");
		connectionList.setColumnWidth(3, "25%");
		connectionList.setColumnWidth(4, "10%");
		connectionList.setColumnWidth(5, "5%");
		connectionList.setColumnWidth(6, "10%");
		connectionList.setColumnWidth(7, "5%");
		

	
		for(int i=0;i<infos.length;i++)
		{

			ConnectionInfo cInfo=infos[i];
			Button destroy=new Button();
			destroy.setText("Trigger");
			destroy.addClickListener(new DestroyConnectionButtonClickListener(cInfo.getConnecitonId()));
			connectionList.setCellText(i,0, i+"");
			connectionList.setCellText(i,1,cInfo.getConnecitonId());
			Date d=new Date(cInfo.getCreationTime());
			connectionList.setCellText(i,2,  d.getHours()+":"+d.getMinutes()+":"+d.getSeconds());
			//connectionList.setCellText(i,3, ""+cInfo.getNumberOfPackets());
			connectionList.setCellText(i,3, cInfo.getOtherEnd());
			Hyperlink localSDP=new Hyperlink();
			localSDP.setText("View");
			//PopupPanel localSDPPopup=new PopupPanel();
			connectionList.setCell(i,4, localSDP);
			Hyperlink remoteSDP=new Hyperlink();
			remoteSDP.setText("View");
			connectionList.setCell(i,5, remoteSDP);
			Hyperlink connectionParameters=new Hyperlink();
			connectionParameters.setText("View");
			connectionList.setCell(i,6, connectionParameters);
			connectionList.setCell(i,7, destroy);
			
			remoteSDP.addClickListener(new MakePopupClickListener(cInfo.getRemoteSdp()));
			localSDP.addClickListener(new MakePopupClickListener(cInfo.getLocalSdp()));
			connectionParameters.addClickListener(new MakeConnectionParameterPopUp(cInfo));

		}
		//refreshTimer=new MainClassRefreshTimer();
		//refreshTimer.schedule(15*1000);
		}catch(Exception e)
		{
			UserInterface.getLogPanel().error(e.getMessage());
		}
		
	}
	
	
	private class DestroyConnectionButtonClickListener implements ClickListener
	{

		private String connectionId=null;
		
		
		public DestroyConnectionButtonClickListener(String connectionId) {
			super();
			this.connectionId = connectionId;
		}


		public void onClick(Widget arg0) {
			ServerConnection.endpointManagementServiceAsync.destroyConnection(info.getName(), info.getType(), connectionId, new DestroyConnectionAsyncCallback());
			
		}
		
	}
	
	private class DestroyConnectionAsyncCallback implements AsyncCallback
	{

		public void onFailure(Throwable t) {
			
			UserInterface.getLogPanel().error("Failed to destroy connection due to: \n"+t.getMessage());
			refreshDataAction.performAction();
		}

		public void onSuccess(Object arg0) {
			if(info.getConnections()==1)
			{
				onErrorAction.performAction();
			}
			else
			{
				refreshDataAction.performAction();
			}
		}
		
	}
	
	
	private class MakePopupClickListener implements ClickListener
	{
		PopupPanel pp=null;
		
	
		public MakePopupClickListener(String content) {
			super();
			TextArea ta=new TextArea();
			
			ta.setReadOnly(true);
			ta.setTextAlignment(ta.ALIGN_LEFT);
			ta.setWidth("300px");
			ta.setHeight("350px");
			if(content==null)
			{
				ta.setText("Connection is local.\nNo SDP is available");
			}else
			{
				ta.setText(content);
			}
			pp=new PopupPanel(true);
			pp.setWidget(ta);
			
			pp.addPopupListener(new ClassPopupClickListener());
		}


		public void onClick(final Widget w) {
			//refreshTimer.cancel();
			pp.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
		          public void setPosition(int offsetWidth, int offsetHeight) {
		            int left = w.getAbsoluteLeft();
		            int top = w.getAbsoluteTop();
		            pp.setPopupPosition(left, top);
		          }
		        });

			
		}
	}
	
	private class MakeConnectionParameterPopUp implements ClickListener
	{
		PopupPanel pp=null;
		ConnectionInfo cInfo=null;
	
		ConnectionParametersDisplay display=null;
		public MakeConnectionParameterPopUp(ConnectionInfo  content) {
			super();
		
			this.cInfo=content;
			
			display=new ConnectionParametersDisplay(content);
	
			pp=new PopupPanel(true);

			pp.setWidget(display);
			display.setWidth("100px");
			display.setHeight("100px");
		
			
			pp.addPopupListener(new ClassPopupClickListener());
	

		}


		public void onClick(final Widget w) {
			//if(refreshTimer!=null)
			//	refreshTimer.cancel();
			
			pp.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
		          public void setPosition(int offsetWidth, int offsetHeight) {
		            int left = w.getAbsoluteLeft();
		            int top = w.getAbsoluteTop();
		            pp.setPopupPosition(left, top);
		          }
		        });


		}
	
		
	}
	
	
	protected class ConnectionParametersDisplay extends VerticalPanel
	{
		protected ConnectionInfo cInfo=null;
		protected Timer refreshTimer=null;
		protected ListPanel topList, innerListPanel;
		private ConnectionParametersDisplay(ConnectionInfo info) {
			super();
			cInfo = info;
			
			if(info!=null)
			{
				 innerListPanel=new ListPanel();
				// topList=new ListPanel();
				//topList.setHeader(0, "Connection properties");
				//topList.setCell(0, 0,innerListPanel);
			
			
			
			innerListPanel.setHeader(0, "Name");
			innerListPanel.setHeader(1, "Value");
			innerListPanel.setCellText(0, 0, "Mode");
			
			innerListPanel.setCellText(1, 0, "State");
			
			innerListPanel.setCellText(2, 0, "Packets Lost");
			
			innerListPanel.setCellText(3, 0, "Packets Sent");
			
			innerListPanel.setCellText(4, 0, "Packets Received");
			
			innerListPanel.setCellText(5, 0, "Octets Sent");
			
			innerListPanel.setCellText(6, 0, "Octets Received");
			
			innerListPanel.setCellText(7, 0, "Arrival Jitter");
			
			//this.add(topList);
			this.add(innerListPanel);
			//this.setCellWidth(topList, "100%");
			populateWithData(cInfo);
			}else
			{
				UserInterface.getInstance().getLogPanel().error("NULL CONNNECTION INFO");
			}
		}
		
		protected void populateWithData(ConnectionInfo cInfo)
		{
			this.cInfo=cInfo;	
			innerListPanel.setCellText(0, 1, cInfo.getMode());
			innerListPanel.setCellText(1, 1, cInfo.getState());
			innerListPanel.setCellText(2, 1, ""+cInfo.getPacketsLost());
			innerListPanel.setCellText(3, 1, ""+cInfo.getPacketsSent());
			innerListPanel.setCellText(4, 1, ""+cInfo.getPacketsReceived());
			innerListPanel.setCellText(5, 1, ""+cInfo.getOctetsSent() +" B");
			innerListPanel.setCellText(6, 1, ""+cInfo.getOctetsReceived()+" B");
			innerListPanel.setCellText(7, 1, ""+cInfo.getInterArrivalJitter()+" ms");
			//innerListPanel.setCellText(2, 1, ""+((double)cInfo.getPacketsLost()/(1000000))+" M");
			//innerListPanel.setCellText(3, 1, ""+((double)cInfo.getPacketsSent()/(1000000))+" M");
			//innerListPanel.setCellText(4, 1, ""+((double)cInfo.getPacketsReceived()/(1000000))+" M");
			//innerListPanel.setCellText(5, 1, ""+((double)cInfo.getOctetsSent() / ( 1024 * 1024))+" M");
			//innerListPanel.setCellText(6, 1, ""+((double)cInfo.getOctetsReceived() / ( 1024 * 1024))+" M");
			if(refreshTimer!=null)
			{
				refreshTimer.cancel();
			}
			this.refreshTimer=new ConnectionInfoRefreshTimer();
			this.refreshTimer.schedule(5000);
		}
		
		private class RefreshConnectionInfoAsyncCallback implements AsyncCallback
		{

			public void onFailure(Throwable caught) {
				UserInterface.getLogPanel().warning("Connection or endpoint does not exists, reporting error");
				//try{
					//EndpointDataManagementConnectionsDisplay.this.refreshTimer.cancel();
				//}catch(Exception e)
				//{}
				onErrorAction.performAction();
				
			}

			public void onSuccess(Object result) {
				cInfo=(ConnectionInfo) result;
				populateWithData(cInfo);
				
			}
			
		}
		
		private class ConnectionInfoRefreshTimer extends Timer
		{

			public void run() {
				ServerConnection.endpointManagementServiceAsync.getConnectionInfo(info.getName(), info.getType(),cInfo.getConnecitonId() ,new RefreshConnectionInfoAsyncCallback());
				
			}
			
		}
		
	}
	
	private class MainClassRefreshTimer extends Timer
	{

		public void run() {
			ServerConnection.endpointManagementServiceAsync.getEndpointInfo(info.getName(), info.getType(), new RefreshCallback());
			
		}
		
	}
	
	private class RefreshCallback implements AsyncCallback
	{

		public void onFailure(Throwable caught) {
			//Error here means this endpoitn stoped to exists?
			UserInterface.getLogPanel().warning("Endpointdoes not exist anymore");
			onErrorAction.performAction();
			
		}

		public void onSuccess(Object result) {
			EndpointFullInfo info=(EndpointFullInfo) result;
			populateWithData(info);
		}
		
	}
	private class ClassPopupClickListener implements PopupListener
	{

		public void onPopupClosed(PopupPanel sender, boolean autoClosed) {
			populateWithData(info);
			
		}
		
	}
}
