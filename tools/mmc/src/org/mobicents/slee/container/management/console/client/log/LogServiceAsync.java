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

import java.util.Set;

import org.mobicents.slee.container.management.console.client.ManagementConsoleException;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author baranowb
 * 
 */
public interface LogServiceAsync {

	public void getLoggerNames(AsyncCallback callBack);

	public void setLoggerLevel(String loggerName, String level,
			AsyncCallback callBack);

	public void resetLoggerLevel(String loggerName, AsyncCallback callBack);

	public void fetchLoggerInfo(String loggerName, AsyncCallback callBack);

	public void removeHandlerAtIndex(String loggerName, int index,
			AsyncCallback callBack);

	public void getUseParentHandlers(String loggerName, AsyncCallback callBack);

	public void setUseParentHandlers(String loggerName, boolean value,
			AsyncCallback callBack);

	public void setLoggerFilterClassName(String loggerName, String className,
			String[] paramTypes, String[] paramValues, AsyncCallback callBack);

	public void getLoggerFilterClassName(String loggerName,
			AsyncCallback callBack);

	public void createGenericHandler(String loggerName, String handlerName,
			String handlerLevel, String handlerClassName,
			String[] constructorParameterTypes, String[] parameterValues,
			String filterClassName, String[] fi_constructorParameterTypes,
			String[] fi_parameterValues, String formatterClassName,
			String[] fo_constructorParameterTypes, String[] fo_parameterValues,
			AsyncCallback callback);

	public void createSocketHandler(String loggerName, String handlerLevel,
			String handlerName, String formaterClassName,
			String filterClassName, String host, int port,
			AsyncCallback callback);

	public void createNotificationHandler(String loggerName,
			int numberOfEntries, String level, String formaterClassName,
			String filterClassName, AsyncCallback callback);

	
	public void removeNotificationHandler(String loggerName, AsyncCallback callback);

	
	public void setGenericHandlerFilterClassName(String loggerName, int index,
			String className, AsyncCallback callBack);

	public void setGenericHandlerFormatterClassName(String loggerName,
			int index, String className, AsyncCallback callBack);

	public void getGenericHandlerFilterClassName(String loggerName, int index,
			AsyncCallback callBack);

	public void getGenericHandlerFormatterClassName(String loggerName,
			int index, AsyncCallback callBack);

	public void getGenericHandlerLevel(String loggerName, int index,
			AsyncCallback callBack);

	public void setGenericHandlerLevel(String loggerName, int index,
			String level, AsyncCallback callBack);

	public void getHandlerName(String loggerName, int index,
			AsyncCallback callBack);

	public void setHandlerName(String loggerName, int index, String newName,
			AsyncCallback callBack);

	public void getHandlerNotificationInterval(String loggerName,
			AsyncCallback callBack);

	//public void getHandlerClassName(String loggerName, String handlerName,
	//		AsyncCallback callBack);

	public void setHandlerNotificationInterval(String loggerName,
			int numberOfEntries, AsyncCallback callBack);

	
	public void addLogger(String name, String level, AsyncCallback callBack);
	
	public void clearLoggers(String name, AsyncCallback callBack);
	
	public void reReadConf(String uri, AsyncCallback callBack); 
	

	public void setDefaultNotificationInterval(String numberOfEntries, AsyncCallback callBack);
	
	public void setDefaultLoggerLevel(String l, AsyncCallback callBack);
	
	public void setDefaultHandlerLevel(String l, AsyncCallback callBack);
	
	public void getDefaultHandlerLevel(AsyncCallback callBack);
	public void getDefaultLoggerLevel(AsyncCallback callBack);
	public void getDefaultNotificationInterval(AsyncCallback callBack);
	
	public void getLoggerLevel(String loggerName, AsyncCallback callback);
	
	
	//public void getLoggerNamesWithNotifications(AsyncCallback callback);
	//public void getLogEntries(String loggerName, AsyncCallback callback);
	
	
}
