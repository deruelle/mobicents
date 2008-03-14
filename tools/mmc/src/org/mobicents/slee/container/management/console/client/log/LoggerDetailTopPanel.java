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

import org.mobicents.slee.container.management.console.client.Logger;
import org.mobicents.slee.container.management.console.client.ServerConnection;
import org.mobicents.slee.container.management.console.client.common.CommonControl;
import org.mobicents.slee.container.management.console.client.common.ListPanel;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author baranowb
 * 
 */
public class LoggerDetailTopPanel extends ListPanel {

	private final static String _TRUE="True";
	private final static String _FALSE="False";
	
	private  LoggerInfo info = null;
	private String shortName = null;

	private TextBox filterClassName=new TextBox();
	private final CommonControl tree;
	/**
	 * 
	 */
	public LoggerDetailTopPanel(final CommonControl tree,final LoggerInfo info, String shortName) {
		super();
		this.tree=tree;
		this.info = info;
		this.shortName = shortName;
		this.setCellText(0, 0, "Short name:");
		Hyperlink l = new Hyperlink(shortName, null);
		l.setTitle(info.getFullName());

		this.setCell(0, 1, l);
		this.setCellText(0, 2, "Use parent handlers:");

		final ListBox ups = new ListBox();
		ups.addItem("Yes", _TRUE);
		ups.addItem("No", _FALSE);
		ups.setVisibleItemCount(1);
		ups.setSelectedIndex(info.isUseParentHandlers() ? 0 : 1);
		this.setCell(0, 3, ups);
		ListBox levelB = new ListBox();

		for (int i = 0; i < LogTreeNode._LEVELS.length; i++) {
			levelB.addItem(LogTreeNode._LEVELS[i], LogTreeNode._LEVELS[i]);
			if (LogTreeNode._LEVELS[i].equals(info.getLevel()))
				levelB.setSelectedIndex(i);
		}

		this.setCellText(2, 0, "Level:");
		this.setCell(2, 1, levelB);
		this.setCellText(2, 2, "Filter class name:");
		
		
		Hyperlink setFilterLink=new Hyperlink("Set",null);
		filterClassName.setText(info.getFilterClass());
		
		//this.setCellText(2, 3, info.getFilterClass());

		HorizontalPanel filterEditPanel=new HorizontalPanel();
		filterEditPanel.setWidth("100%");
		this.setCell(2, 3, filterEditPanel);
		filterEditPanel.add(setFilterLink);
		filterEditPanel.add(filterClassName);
		filterEditPanel.setHorizontalAlignment(filterEditPanel.ALIGN_LEFT);
		
		
		
		
		
		
		// Change listeners for ListBoxes:
		class UseParentHandlerListener implements ChangeListener {

			/*
			 * (non-Javadoc)
			 * 
			 * @see com.google.gwt.user.client.ui.ChangeListener#onChange(com.google.gwt.user.client.ui.Widget)
			 */
			public void onChange(Widget sender) {

				final ListBox ss = (ListBox) sender;
				final String value = ss.getValue(ss.getSelectedIndex());
				final boolean sendValue;
				if(value.equals(_TRUE))
					sendValue=(true);
				else
					sendValue=(false);
				
				
				class UseParentHandlerCallback implements AsyncCallback
				{

					/* (non-Javadoc)
					 * @see com.google.gwt.user.client.rpc.AsyncCallback#onFailure(java.lang.Throwable)
					 */
					public void onFailure(Throwable caught) {

						Logger.error("Could not set \"UseParentHandler\" flag for logger ["+info.getFullName()+"] due to["+caught.getMessage()+"]");
						if(sendValue)
						{
							ss.setItemSelected(1, true);
						}else
						{
							ss.setItemSelected(0, true);
						}
					}

					/* (non-Javadoc)
					 * @see com.google.gwt.user.client.rpc.AsyncCallback#onSuccess(java.lang.Object)
					 */
					public void onSuccess(Object result) {

						info.setUseParentHandlers(sendValue);
						
					}}
				
				ServerConnection.logServiceAsync.setUseParentHandlers(info.getFullName(), sendValue, new UseParentHandlerCallback());
				
			}
		}

		
		class LevelChangeListener implements ChangeListener
		{


			public void onChange(Widget sender) {
				ListBox ss=(ListBox) sender;
				final String logLevel=ss.getValue(ss.getSelectedIndex());
				
				
				class LevelChangeCallBack implements AsyncCallback
				{

			
					public void onFailure(Throwable caught) {
						Logger.error("Could not set logger level for logger ["+info.getFullName()+"] due to["+caught.getMessage()+"]");
					}

				
					public void onSuccess(Object result) {

						info.setLevel(logLevel);
						tree.onShow();
						
					}
					
				}
				
				ServerConnection.logServiceAsync.setLoggerLevel(info.getFullName(), logLevel, new LevelChangeCallBack());
			}
			
		}
		
		class SetFilterClassNameListener implements ClickListener
		{

			public void onClick(Widget arg0) {


				class SetFilterClassNameAsyncCallBack implements AsyncCallback
				{

					public void onFailure(Throwable arg0) {
						
						//ugly, class in class? but it allows us to have set everything in proper way
						class GetFilterClassNameCallBack implements AsyncCallback
						{

							public void onFailure(Throwable arg0) {
								// TODO Auto-generated method stub
								
							}

							public void onSuccess(Object arg0) {
								
								filterClassName.setText((String) arg0);
								
							}}
						
						ServerConnection.logServiceAsync.getLoggerFilterClassName(info.getFullName(), new GetFilterClassNameCallBack());
						
					}

					public void onSuccess(Object arg0) {
						
						info.setFilterClass(filterClassName.getText());
						
					}}
				
				
				ServerConnection.logServiceAsync.setLoggerFilterClassName(info.getFullName(), filterClassName.getText(),null,null, new SetFilterClassNameAsyncCallBack());
				
				
			}
			
		}
		
		
		
		//add listeners
		ups.addChangeListener(new UseParentHandlerListener());
		levelB.addChangeListener(new LevelChangeListener());
		setFilterLink.addClickListener(new SetFilterClassNameListener());
		
		
	}

}
