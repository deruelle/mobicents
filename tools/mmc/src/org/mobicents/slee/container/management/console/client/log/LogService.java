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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mobicents.slee.container.management.console.client.ManagementConsoleException;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * @author baranowb
 * 
 */
public interface LogService extends RemoteService {
	public List getLoggerNames() throws ManagementConsoleException;

	public void setLoggerLevel(String loggerName, String level)
			throws ManagementConsoleException;

	public String resetLoggerLevel(String loggerName)
			throws ManagementConsoleException;

	/**
	 * If this logger does not exists it will be created with Level.OFF
	 * 
	 * @param loggerName
	 * @return
	 */
	public LoggerInfo fetchLoggerInfo(String loggerName)
			throws ManagementConsoleException;

	public void removeHandlerAtIndex(String loggerName, int index)
			throws ManagementConsoleException;

	public boolean getUseParentHandlers(String loggerName)
			throws ManagementConsoleException;

	public void setUseParentHandlers(String loggerName, boolean value)
			throws ManagementConsoleException;

	public void setLoggerFilterClassName(String loggerName, String className,
			String[] constructorParameters, String[] paramValues)
			throws ManagementConsoleException;

	public String getLoggerFilterClassName(String loggerName)
			throws ManagementConsoleException;

	public void createGenericHandler(String loggerName, String handlerName,
			String handlerLevel, String handlerClassName,
			String[] constructorParameterTypes, String[] parameterValues,
			String filterClassName, String[] fi_constructorParameterTypes,
			String[] fi_parameterValues, String formatterClassName,
			String[] fo_constructorParameterTypes, String[] fo_parameterValues)
			throws ManagementConsoleException;

	public void createSocketHandler(String loggerName, String handlerLevel,
			String handlerName, String formaterClassName,
			String filterClassName, String host, int port)
			throws ManagementConsoleException;

	public void createNotificationHandler(String loggerName,
			int numberOfEntries, String level, String formaterClassName,
			String filterClassName) throws ManagementConsoleException;

	public void removeNotificationHandler(String loggerName)throws ManagementConsoleException;
	
	public void setGenericHandlerFilterClassName(String loggerName, int index,
			String className) throws ManagementConsoleException;

	public void setGenericHandlerFormatterClassName(String loggerName,
			int index, String className) throws ManagementConsoleException;

	public String getGenericHandlerFilterClassName(String loggerName, int index)
			throws ManagementConsoleException;

	public String getGenericHandlerFormatterClassName(String loggerName,
			int index) throws ManagementConsoleException;

	public String getGenericHandlerLevel(String loggerName, int index)
			throws ManagementConsoleException;

	public void setGenericHandlerLevel(String loggerName, int index,
			String level) throws ManagementConsoleException;

	public String getHandlerName(String loggerName, int index)
			throws ManagementConsoleException;

	public void setHandlerName(String loggerName, int index, String newName)
			throws ManagementConsoleException;

	public int getHandlerNotificationInterval(String loggerName)
			throws ManagementConsoleException;
	public void setHandlerNotificationInterval(String loggerName,
			int numberOfEntries) throws ManagementConsoleException;
	
	public boolean addLogger(String name, String level)
	throws  ManagementConsoleException;
	
	public void clearLoggers(String name) throws ManagementConsoleException;
	public void reReadConf(String uri) throws ManagementConsoleException ;
	
	public void setDefaultNotificationInterval(String numberOfEntries)
	throws ManagementConsoleException;
	
	public void setDefaultLoggerLevel(String l)
	throws ManagementConsoleException;
	
	public void setDefaultHandlerLevel(String l)
	throws ManagementConsoleException ;
	
	public String getDefaultHandlerLevel() throws ManagementConsoleException;
	public String getDefaultLoggerLevel() throws ManagementConsoleException;
	public Integer getDefaultNotificationInterval()	throws ManagementConsoleException;
	
	public String getLoggerLevel(String loggerName)
	throws ManagementConsoleException;

	//Log display == subject to change
	
	//public Set getLoggerNamesWithNotifications() throws ManagementConsoleException;
	//public LogEntries getLogEntries(String loggerName) throws ManagementConsoleException;
	
}
