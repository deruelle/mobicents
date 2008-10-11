package org.mobicents.media.container.management.console.client.common.pages;

import org.mobicents.media.container.management.console.client.common.BrowseContainer;
import org.mobicents.media.container.management.console.client.common.ControlledTabedBar;
import org.mobicents.media.container.management.console.client.common.SmartTabPage;
import org.mobicents.media.container.management.console.client.endpoint.EndpointListPanel;
import org.mobicents.media.container.management.console.client.endpoint.EndpointType;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;


public class EndpointManagementPage extends SmartTabPage{

	
	protected BrowseContainer display=new BrowseContainer();
	protected ControlledTabedBar endpointTypesDisplay=new ControlledTabedBar();
	protected EndpointType[] viewTypes=EndpointType.defined;
	public static SmartTabPageInfo getInfo() {
		return new SmartTabPageInfo("<image src='images/components.gif' /> Endpoint Management", "Endpoint Management") {
			protected SmartTabPage createInstance() {
				return new EndpointManagementPage();
			}
		};
	}

	public EndpointManagementPage() {
		super();
		initWidget(this.display);
		
		this.display.setWidth("100%");
		this.display.setHeight("100%");
		DockPanel tmp=new DockPanel();
		
		for(int i=0;i<viewTypes.length;i++)
		{
			this.endpointTypesDisplay.add(new EndpointListPanel(tmp,endpointTypesDisplay, this.display,viewTypes[i]), viewTypes[i].getType());
		}
		
		tmp.add(endpointTypesDisplay, tmp.CENTER);
		tmp.setCellHeight(endpointTypesDisplay, "100%");
		tmp.setCellWidth(endpointTypesDisplay, "100%");
		tmp.setCellVerticalAlignment(endpointTypesDisplay, HasVerticalAlignment.ALIGN_TOP);
		tmp.setWidth("100%");
		tmp.setHeight("100%");
		this.display.add("Endpoints", tmp);
		this.endpointTypesDisplay.setHeight("100%");
		this.endpointTypesDisplay.setWidth("100%");
		
	}

	
	public void onHide() {
		
		super.onHide();
		this.endpointTypesDisplay.onHide();
	}

	
	public void onInit() {
		
		super.onInit();
		this.endpointTypesDisplay.onInit();
	}

	
	public void onShow() {
		
		super.onShow();
		this.endpointTypesDisplay.onShow();
	}
	
}
