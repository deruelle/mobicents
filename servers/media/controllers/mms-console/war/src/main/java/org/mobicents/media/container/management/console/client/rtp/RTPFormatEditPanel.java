package org.mobicents.media.container.management.console.client.rtp;

import org.mobicents.media.container.management.console.client.common.ControlContainer;
import org.mobicents.media.container.management.console.client.common.ListPanel;
import org.mobicents.media.container.management.console.client.common.UserInterface;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class RTPFormatEditPanel extends Composite {

	protected ControlContainer rootPanel = new ControlContainer();
	protected PerformActionClass action = null;
	protected int index = -1;
	protected ListBox targetBox=null;
	protected XFormat xf=null;
	protected TextBox payloadTypeBox = new TextBox();
	protected TextBox mediaFormatBox = new TextBox();
	protected RTPMediaFormatConfigurationPage page;
	public RTPFormatEditPanel(final RTPMediaFormatConfigurationPage page) {
		super();
		this.page=page;
		initWidget(rootPanel);
		rootPanel.setWidth("100%");
		rootPanel.setHeight("100%");

		ListPanel optionsPanel = new ListPanel();
		optionsPanel.setHeader(0, "Payload Type");
		optionsPanel.setHeader(1, "Media Format");
		optionsPanel.setHeader(3, "#");

		
		Button confirmButton = new Button("Confirm");
		optionsPanel.setCell(0, 0, payloadTypeBox);
		optionsPanel.setCell(0, 1, mediaFormatBox);
		optionsPanel.setCell(0, 2, confirmButton);

		// SETUP
		mediaFormatBox.setEnabled(false);
		mediaFormatBox.setWidth("100%");
		optionsPanel.setColumnWidth(0, "25%");
		optionsPanel.setColumnWidth(1, "65%");
		
		
		confirmButton.addClickListener(new ClickListener(){

			public void onClick(Widget arg0) {
				
				try{
					Integer.valueOf(payloadTypeBox.getText());
				}catch(Exception e)
				{
					UserInterface.getLogPanel().error("Payload Type must be interger!!!");
					return;
				}
				
				xf.setRtpMap(payloadTypeBox.getText());
				if(index>=0)
				{
					targetBox.setItemText(index, xf.toString());
				}else
				{
					int itemCount=targetBox.getItemCount();
					for(int i=0;i<itemCount;i++)
					{
						if(targetBox.getItemText(i).trim().equals(xf.toString().trim()))
						{
							return;
						}
					}
					targetBox.addItem(xf.toString(), xf.toString());
				}
				
				
				
				int itemCount=targetBox.getItemCount();
				String allFormats="";
				
				for(int i=0;i<itemCount;i++)
				{
					allFormats+=targetBox.getItemText(i);
					
					if(i<itemCount-1)
						allFormats+=";";
					
					
				}
				UserInterface.getLogPanel().info("Sending: "+allFormats);
				XFormat[] xfs=XFormat.fromString(allFormats);
				action.perform(xfs, page);
				
			}});
		
		
		rootPanel.setWidget(0, 0, optionsPanel);
		
		
	}

	public void setCreateNew(XFormat xf, String objectName, PerformActionClass action, ListBox box, int index) {

		this.index=index;
		this.targetBox=box;
		this.action=action;
		this.xf=xf;
		this.payloadTypeBox.setText(this.xf.getRtpMap());
		this.mediaFormatBox.setText(this.xf.getFormat());
		
	}

	public void setEdit(XFormat xf, String objectName, PerformActionClass action, ListBox presentFormatsBox, int index) {
		this.index=index;
		this.targetBox=presentFormatsBox;
		this.action=action;
		this.xf=xf;
		this.payloadTypeBox.setText(this.xf.getRtpMap());
		this.mediaFormatBox.setText(this.xf.getFormat());
	}

}
