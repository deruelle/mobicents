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
package org.mobicents.slee.container.management.console.client.log;

import java.util.ArrayList;

import org.mobicents.slee.container.management.console.client.Logger;
import org.mobicents.slee.container.management.console.client.ServerConnection;
import org.mobicents.slee.container.management.console.client.common.CommonControl;
import org.mobicents.slee.container.management.console.client.common.ListPanel;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author baranowb
 * 
 */
public class AddGenericHadlerPanel extends Composite implements CommonControl {

	private String loggerName = null;

	private ListPanel options = new ListPanel();

	// private TextBox _levelBox = new TextBox();

	private ListBox _levelList = new ListBox();

	private TextBox _nameBox = new TextBox();

	private TextBox _handlerClassName = new TextBox();

	private TextBox _formaterClassNameBox = new TextBox();

	private TextBox _filterClassNameBox = new TextBox();

	// private TextBox _handlerClassName=new TextBox();

	private Hyperlink addParam = new Hyperlink("Add", null);
	private Hyperlink removeParam = new Hyperlink("Remove", null);

	private ListPanel paramsList = new ListPanel();

	// used to display everything we have here.
	private ListPanel inner = new ListPanel();
	private Hyperlink createLink = new Hyperlink("Create Handler", null);

	//TODO: Enclose this in some kind of object which can be reused!!!
	private ArrayList paramTypes = new ArrayList();

	private ArrayList paramValues = new ArrayList();

	private CommonControl parent=null;
	
	public AddGenericHadlerPanel(String loggerName, CommonControl _this) {
		super();
		this.loggerName = loggerName;
		this.parent=_this;
		initWidget(options);

		// This is becase for some reason
	}

	public void addStyleName(String style) {
		options.addStyleName(style);
	}

	public void emptyTable() {
		options.emptyTable();
	}

	public String getStyleName() {
		return options.getStyleName();
	}

	public void setStyleName(String style) {
		options.setStyleName(style);
	}

	// We dont need those :]
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.slee.container.management.console.client.common.CommonControl#onHide()
	 */
	public void onHide() {
		options.emptyTable();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.slee.container.management.console.client.common.CommonControl#onInit()
	 */
	public void onInit() {
		// TODO Auto-generated method stub
		inner.setCell(0, 0, new Label("Handler Name:"));
		inner.setCell(0, 1, _nameBox);
		inner.setCell(0, 2, new Label("Handler Level:"));
		inner.setCell(0, 3, _levelList);
		inner.setCell(1, 0, new Label("Formatter class:"));
		inner.setCell(1, 1, _formaterClassNameBox);
		inner.setCell(1, 2, new Label("Filter class:"));
		inner.setCell(1, 3, _filterClassNameBox);
		inner.setCell(2, 0, new Label("Handler class:"));
		inner.setCell(2, 1, _handlerClassName);

		for (int i = 0; i < LogTreeNode._LEVELS.length; i++) {
			_levelList.addItem(LogTreeNode._LEVELS[i], LogTreeNode._LEVELS[i]);

		}

		_levelList.setSelectedIndex(0);

		

		// Add clickers
		// For add/remove consturctor params

		class AddNewConstructorParameterClickListener implements ClickListener {

			public void onClick(Widget arg0) {

				TextBox paramType = new TextBox();
				TextBox paramValue = new TextBox();
				paramTypes.add(paramType);
				paramValues.add(paramValue);
				redisplayParamaters();
			}
		}

		class RemoveConstructorParameterClickListener implements ClickListener {

			public void onClick(Widget arg0) {

				if (paramTypes.size() > 0) {
					paramTypes.remove(paramTypes.size() - 1);
					paramValues.remove(paramValues.size() - 1);
					redisplayParamaters();
				}
			}
		}

		class CreateGenericHandlerClickListener implements ClickListener {

			public void onClick(Widget arg0) {

				class CreateGenericHandlerCallBack implements AsyncCallback {

					public void onFailure(Throwable arg0) {

						Logger.error("Failed to create generic handler due to["
								+ arg0.getMessage() + "]");

					}

					public void onSuccess(Object arg0) {

						// FIXME: Should we remove everything here?

						//We have to refresh parent.
						parent.onHide();
						parent.onShow();
					}

				}
				String[] _paramTypes = new String[paramTypes.size()];
				String[] _paramValues = new String[paramValues.size()];

				for (int i = 0; i < paramTypes.size(); i++) {
					_paramTypes[i] = ((TextBox) paramTypes.get(i)).getText();
					_paramValues[i] = ((TextBox) paramValues.get(i)).getText();
				}

				ServerConnection.logServiceAsync.createGenericHandler(
						loggerName, _nameBox.getText(), _levelList.getItemText(_levelList.getSelectedIndex()),_handlerClassName
								.getText(), _paramTypes, _paramValues,
						_filterClassNameBox.getText(), null, null,
						_formaterClassNameBox.getText(), null, null,
						new CreateGenericHandlerCallBack());
			}

		}
		
		
		//ADD
		removeParam.addClickListener(new RemoveConstructorParameterClickListener());
		addParam.addClickListener(new AddNewConstructorParameterClickListener());
		createLink.addClickListener(new CreateGenericHandlerClickListener());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.slee.container.management.console.client.common.CommonControl#onShow()
	 */
	public void onShow() {

		// This leaves a lot of place in the right - to be filled with params
		// for filter and formatter!!!!
		// options.setHorizontalAlignment(DockPanel.ALIGN_CENTER);
		options.setCell(0, 0, inner);
		options.setCell(1, 0, paramsList);
		options.setCell(2, 0, createLink);

		redisplayParamaters();

		options.setCellAlignment(1, 0, HasVerticalAlignment.ALIGN_MIDDLE,
				HasHorizontalAlignment.ALIGN_CENTER);

		options.setWidth("100%");
		options.setHeight("100%");

	}

	private void redisplayParamaters() {

		paramsList.emptyTable();
		// Now part with constructor params

		// Small trick
		FlexTable ft = new FlexTable();
		ft.setWidget(0, 0, addParam);
		ft.setText(0, 1, "/");
		ft.setWidget(0, 2, removeParam);

		paramsList.setHeader(0, ft);
		paramsList.setHeader(1, "Type");
		paramsList.setHeader(2, "Value");
		paramsList.setColumnWidth(0, "8%");
		paramsList.setColumnWidth(1, "46%");
		paramsList.setColumnWidth(2, "46%");
		
		
		for(int index=0;index<paramTypes.size();index++)
		{
			paramsList.setCell(index+1, 0, new Label("#"+(index+1)));
			paramsList.setCell(index+1, 1, (Widget) paramTypes.get(index));
			paramsList.setCell(index+1, 2, (Widget) paramValues.get(index));
		}
		
		
	}

}
