package org.mobicents.media.container.management.console.client.common.pages;

import org.mobicents.media.container.management.console.client.common.BrowseContainer;
import org.mobicents.media.container.management.console.client.common.ControlledTabedBar;
import org.mobicents.media.container.management.console.client.common.SmartTabPage;
import org.mobicents.media.container.management.console.client.common.UserInterface;
import org.mobicents.media.container.management.console.client.endpoint.EndpointListPanel;
import org.mobicents.media.container.management.console.client.endpoint.EndpointType;
import org.mobicents.media.container.management.console.client.endpoint.VirtualEndpointListPanel;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;


public class VirtualEndpointManagementPage extends SmartTabPage{

	
	protected BrowseContainer display=new BrowseContainer();
	protected ControlledTabedBar endpointTypesDisplay=new ControlledTabedBar();
	protected EndpointType[] viewTypes=EndpointType.defined;
	public static SmartTabPageInfo getInfo() {
		return new SmartTabPageInfo("<image src='images/endpoints.virtual.gif' /> VEndpoint Management", "Virtual Endpoint Management") {
			protected SmartTabPage createInstance() {
				return new VirtualEndpointManagementPage();
			}
		};
	}

	public VirtualEndpointManagementPage() {
		super();
		initWidget(this.display);
		
		this.display.setWidth("100%");
		this.display.setHeight("100%");
		DockPanel tmp=new DockPanel();
		
		for(int i=0;i<viewTypes.length;i++)
		{
		
			HorizontalPanel hp=new HorizontalPanel();
			Image img=new Image();
			img.setUrl("images/"+viewTypes[i].getImageName());
			//Label l=new Label(viewTypes[i].getType());
			HTML l=new HTML(viewTypes[i].getType().trim());
			
			hp.add(img);
			hp.add(l);
			hp.setCellWidth(img, "100%");
			hp.setCellWidth(l, "100%");
			hp.setWidth("100%");
			this.endpointTypesDisplay.add(new VirtualEndpointListPanel(tmp,endpointTypesDisplay, this.display,viewTypes[i]),hp );
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
