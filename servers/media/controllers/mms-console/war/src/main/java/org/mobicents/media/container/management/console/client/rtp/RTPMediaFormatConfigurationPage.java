package org.mobicents.media.container.management.console.client.rtp;

import org.mobicents.media.container.management.console.client.common.ControlContainer;
import org.mobicents.media.container.management.console.client.common.ListPanel;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class RTPMediaFormatConfigurationPage extends Composite {

	protected XFormat[] predefined = null;
	// Yeah possibly we shoudl only pass XFormat array and bjectName but hat if
	// object name is configurable?
	protected XFormat[] info = null;
	protected ControlContainer rootPanel = new ControlContainer();
	protected ListBox predefinedListBox = new ListBox();
	protected ListBox presentFormatsBox = new ListBox();
	protected PerformActionClass action = null;
	protected String objectName = null;
	protected RTPFormatEditPanel editPanel = null;

	public RTPMediaFormatConfigurationPage(XFormat[] info, XFormat[] predefined, PerformActionClass action, String objectName) {
		super();
		this.info = info;
		this.predefined = predefined;
		this.action = action;
		this.objectName = objectName;
		predefinedListBox.setMultipleSelect(true);
		predefinedListBox.setWidth("100%");
		predefinedListBox.setHeight("100%");
		presentFormatsBox.setMultipleSelect(true);
		presentFormatsBox.setWidth("100%");
		presentFormatsBox.setHeight("100%");
		AddNewFormatClickListener addListener = new AddNewFormatClickListener();
		EditFormatClickListener editListener=new EditFormatClickListener();
		predefinedListBox.addClickListener(addListener);
		presentFormatsBox.addClickListener(editListener);
		// FIXME: add change listener
		rootPanel.setHeight("100%");
		rootPanel.setWidth("100%");
		initWidget(rootPanel);
		refreshView(info);

	}

	public void refreshView(XFormat[] info) {
		this.info = info;

		rootPanel.clear();
		ListPanel formatsDisplayPanel = new ListPanel();
		formatsDisplayPanel.setHeader(0, "Codecs/Predefined");
		formatsDisplayPanel.setHeader(1, "");
		formatsDisplayPanel.setHeader(2, "Defined Formats");

		formatsDisplayPanel.setColumnWidth(0, "50%");
		formatsDisplayPanel.setColumnWidth(1, "100px");
		formatsDisplayPanel.setColumnWidth(2, "50%");

		predefinedListBox.clear();
		presentFormatsBox.clear();

		// predefinedListBox.insertItem("", "", 0);
		for (int i = 0; i < predefined.length; i++) {
			predefinedListBox.insertItem(predefined[i].toString(), predefined[i].toString(), i);
		}
		// presentFormatsBox.insertItem("", "", 0);
		for (int i = 0; i < info.length; i++) {
			presentFormatsBox.insertItem(info[i].toString(), info[i].toString(), i);
		}

		formatsDisplayPanel.setCell(0, 0, predefinedListBox);
		formatsDisplayPanel.setCell(0, 2, presentFormatsBox);
		formatsDisplayPanel.setCell(0, 1, new Label(""));
		formatsDisplayPanel.setCellAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP, HasHorizontalAlignment.ALIGN_CENTER);
		formatsDisplayPanel.setCellAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP, HasHorizontalAlignment.ALIGN_CENTER);
		formatsDisplayPanel.setCellAlignment(0, 2, HasVerticalAlignment.ALIGN_TOP, HasHorizontalAlignment.ALIGN_CENTER);
		rootPanel.setWidget(0, 0, formatsDisplayPanel);
		formatsDisplayPanel.setWidth("100%");
		formatsDisplayPanel.setHeight("100%");
		editPanel=new RTPFormatEditPanel(this);
		
		rootPanel.setWidget(1, 0, editPanel);
		editPanel.setWidth("100%");
		editPanel.setHeight("100%");

	}

	private class AddNewFormatClickListener implements ClickListener {

		public void onClick(Widget w) {
			ListBox lb = (ListBox) w;

			int index = lb.getSelectedIndex();
			XFormat xf = predefined[index];
			xf = (XFormat) xf.clone();
			editPanel.setCreateNew(xf, objectName, action, presentFormatsBox, -1);
		}
	}
	private class EditFormatClickListener implements ClickListener {

		public void onClick(Widget w) {
			ListBox lb = (ListBox) w;

			int index = lb.getSelectedIndex();
			XFormat xf = XFormat.fromString(presentFormatsBox.getItemText(index))[0];
			
			editPanel.setEdit(xf, objectName, action, presentFormatsBox,index);
		}
	}
}
