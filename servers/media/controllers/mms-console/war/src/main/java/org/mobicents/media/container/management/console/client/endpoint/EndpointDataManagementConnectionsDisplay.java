package org.mobicents.media.container.management.console.client.endpoint;


import java.util.Date;

import org.mobicents.media.container.management.console.client.ServerConnection;
import org.mobicents.media.container.management.console.client.common.BrowseContainer;
import org.mobicents.media.container.management.console.client.common.ListPanel;
import org.mobicents.media.container.management.console.client.common.UserInterface;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.TextBoxBase.TextAlignConstant;


public class EndpointDataManagementConnectionsDisplay extends Composite {

	
	
	protected EndpointFullInfo info=null;
	protected DockPanel dataDisplay=new DockPanel();
	protected ListPanel connectionList=new ListPanel();
	protected BrowseContainer display=null;
	protected ActionPerform refreshDataAction;
	protected ActionPerform onErrorAction;
	public EndpointDataManagementConnectionsDisplay(ActionPerform refreshAction,ActionPerform onErrorAction) {
		super();
		this.refreshDataAction=refreshAction;
		this.onErrorAction=onErrorAction;
		initWidget(dataDisplay);
		
		this.setHeight("100%");
		this.setWidth("100%");
		
		this.dataDisplay.setHeight("100%");
		this.dataDisplay.setWidth("100%");
		Label ll=new Label("ADD HEADER");
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
		connectionList.setHeader(4, "Other end");
		connectionList.setHeader(5, "Local SDP");
		connectionList.setHeader(6, "Remote SDP");
		connectionList.setHeader(7, "State");
		connectionList.setHeader(8, "Mode");
		connectionList.setHeader(9, "Break");

		
		connectionList.setColumnWidth(0, "5%");
		connectionList.setColumnWidth(1, "20%");
		connectionList.setColumnWidth(2, "20%");
		//connectionList.setColumnWidth(3, "5%");
		connectionList.setColumnWidth(4, "25%");
		connectionList.setColumnWidth(5, "10%");
		connectionList.setColumnWidth(6, "5%");
		connectionList.setColumnWidth(7, "5%");
		connectionList.setColumnWidth(8, "5%");
		connectionList.setColumnWidth(9, "5%");

	
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
			connectionList.setCellText(i,4, cInfo.getOtherEnd());
			Hyperlink localSDP=new Hyperlink();
			localSDP.setText("View");
			//PopupPanel localSDPPopup=new PopupPanel();
			connectionList.setCell(i,5, localSDP);
			Hyperlink remoteSDP=new Hyperlink();
			remoteSDP.setText("View");
			connectionList.setCell(i,6, remoteSDP);
			connectionList.setCellText(i,7, cInfo.getState());
			connectionList.setCellText(i,8, cInfo.getMode());
			connectionList.setCell(i,9, destroy);
			
			remoteSDP.addClickListener(new MakePopupClickListener(cInfo.getRemoteSdp()));
			localSDP.addClickListener(new MakePopupClickListener(cInfo.getLocalSdp()));
			

		}
		
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
		}


		public void onClick(final Widget w) {
			pp.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
		          public void setPosition(int offsetWidth, int offsetHeight) {
		            int left = w.getAbsoluteLeft();
		            int top = w.getAbsoluteTop();
		            pp.setPopupPosition(left, top);
		          }
		        });

			
		}
	}
	
}
