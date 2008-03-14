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
package org.mobicents.slee.container.management.console.server.log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.InstanceNotFoundException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.servlet.ServletException;

import org.jboss.mx.util.MBeanServerLocator;
import org.mobicents.slee.container.management.console.client.ManagementConsoleException;
import org.mobicents.slee.container.management.console.client.log.HandlerInfo;
import org.mobicents.slee.container.management.console.client.log.LogEntries;
import org.mobicents.slee.container.management.console.client.log.LogEntry;
import org.mobicents.slee.container.management.console.client.log.LogService;
import org.mobicents.slee.container.management.console.client.log.LoggerInfo;
import org.mobicents.slee.container.management.console.server.ManagementConsole;
import org.mobicents.slee.container.management.console.server.mbeans.SleeMBeanConnection;
import org.mobicents.slee.container.management.jmx.log.MobicentsLocalLogRecord;
import org.mobicents.slee.container.management.jmx.log.MobicentsLogNotification;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author baranowb
 * 
 */
public class LogServiceImpl extends RemoteServiceServlet implements LogService,
		NotificationListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ManagementConsole managementConsole = ManagementConsole
			.getInstance();

	private SleeMBeanConnection sleeConnection = managementConsole
			.getSleeConnection();

	/**
	 * This contains set of logger names which: - this servlet has injected with
	 * NotificationHandler - notification has been received - on timer task
	 * turns out ot have notification listener
	 */
	private static Set<String> loggerNamesWithNotifications = new HashSet<String>();

	// private Map<String, LogEntries> entriesStorage = Collections
	// .synchronizedMap(new HashMap<String, LogEntries>());

	private static  Map<String, LogEntries> entriesStorage = new HashMap<String, LogEntries>();

	private static Object _SYNC_LOCK = new Object();

	private static final int _MAX_ENTRIES = 450;

	private static final long _MAX_ENTRIES_AGE = 30000;
	//TODO: Create timer that will check loggers for notification handlers.
	private Timer timer=new Timer();
	
	
	
	
	public LogServiceImpl() {
		super();
		//timer.scheduleAtFixedRate(new EntriesCheckTimerTask(), 5000, 5000);
	}

	@Override
	public void init() throws ServletException {

		super.init();

		// Here we have to register as listener to notifications.
		MBeanServer mbs = MBeanServerLocator.locateJBoss();
		try {
			ObjectName logMgmtBean = new ObjectName("slee:name=LogManagementMBean");
			mbs.addNotificationListener(logMgmtBean, this, null, null);
		} catch (MalformedObjectNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstanceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void destroy() {

		super.destroy();
		// deregister from notificatins
		MBeanServer mbs = MBeanServerLocator.locateJBoss();
		try {
			ObjectName logMgmtBean = new ObjectName("slee:name=LogManagementMBean");
			mbs.removeNotificationListener(logMgmtBean, this);
		} catch (MalformedObjectNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstanceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ListenerNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void handleNotification(Notification notification, Object handback) {
		
		//TMP
		if(true)
			return;
		
		
		if(notification instanceof MobicentsLogNotification)
		{
			synchronized(_SYNC_LOCK)
			{
			MobicentsLogNotification not=(MobicentsLogNotification)notification;
			loggerNamesWithNotifications.add(not.getLoggerName());
			if(!entriesStorage.containsKey(not.getLoggerName()))
			{
				entriesStorage.put(not.getLoggerName(), new LogEntries(not.getLoggerName(),new ArrayList()));
			}
			
			
				LogEntries entries=entriesStorage.get(not.getLoggerName());
				// entries.updateAccessTime();
				ArrayList newEntries=new ArrayList();
				
				ArrayList<MobicentsLocalLogRecord> records=not.getRecords();
				
				for(int i=0;i<records.size();i++)
				{
					MobicentsLocalLogRecord mlr=records.get(i);
					newEntries.add(new LogEntry(mlr.getLevel().toString(),mlr.getFormattedMessage(),mlr.getLoggerName()));
				}
				entries.append(newEntries);
				if(entries.size()>_MAX_ENTRIES)
				{
					entries.trim(_MAX_ENTRIES);
				}
				entries.updateAccessTime();
			}
		}
		
	}

	class EntriesCheckTimerTask extends TimerTask
	{

		@Override
		public void run() {
			

			synchronized(_SYNC_LOCK)
			{

				Iterator<String> keys=entriesStorage.keySet().iterator();

				while(keys.hasNext())
				{

					String key=keys.next();
					LogEntries le=entriesStorage.get(key);

					if(le.getAccessTime()+_MAX_ENTRIES_AGE<System.currentTimeMillis())
					{
						
						keys.remove();
						
					}
				}
				
			}
			
		}
		
	}
	
	// OPERATIONS PART ---------------------

	//public Set getLoggerNamesWithNotifications()
	//		throws ManagementConsoleException {
	//	Logger.global.info("-=-=-=-=>"+loggerNamesWithNotifications);
	//	return loggerNamesWithNotifications;
	//}

	//public LogEntries getLogEntries(String loggerName) throws ManagementConsoleException {
	//
	//	
	//	synchronized(_SYNC_LOCK)
	//	{
	//		
	//		if(!entriesStorage.containsKey(loggerName) && !loggerNamesWithNotifications.contains(loggerName))
	//			throw new ManagementConsoleException("Failed to retrieve info, since no notification logger is present?");
	//		
	//		if(entriesStorage.get(loggerName)==null)
	//		{
	//			return new LogEntries(loggerName,new ArrayList());
	//		}else
	//		{
				
	//			LogEntries le=entriesStorage.get(loggerName);
	//			LogEntries returnValue=le.clone();
	//			le.trim(1);
	//			return returnValue;
	//		}
			
			
			
	//	}
		
		

	//}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.slee.container.management.console.client.log.LogService#getLoggerNames()
	 */
	public List getLoggerNames() throws ManagementConsoleException {

		return sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().getLoggerNames(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.slee.container.management.console.client.log.LogService#activateLogger(java.lang.String)
	 */
	public void setLoggerLevel(String loggerName, String level)
			throws ManagementConsoleException {

		sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().setLoggerLevel(loggerName, level);

	}

	public String resetLoggerLevel(String loggerName)
			throws ManagementConsoleException {
		// This is called when logger is activated.
		sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().addLogger(loggerName, Level.OFF);
		sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().resetLoggerLevel(loggerName);
		return sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().getDefaultLoggerLevel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.slee.container.management.console.client.log.LogService#fetchLoggerInfo(java.lang.String)
	 */
	public LoggerInfo fetchLoggerInfo(String loggerName)
			throws ManagementConsoleException {

		if (!sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().getLoggerNames(null).contains(
						loggerName))
			sleeConnection.getSleeManagementMBeanUtils()
					.getLogManagementMBeanUtils().addLogger(loggerName,
							Level.OFF);
		int handlerNum = sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().numberOfHandlers(loggerName);

		String _name = loggerName;
		String _level = sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().getLoggerLevel(loggerName);
		boolean _parent = sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().getUseParentHandlersFlag(
						loggerName);
		String _filter = sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().getLoggerFilterClassName(
						loggerName);
		HandlerInfo[] hInfos = new HandlerInfo[handlerNum];

		for (int i = 0; i < handlerNum; i++) {

			String _formatterClass = sleeConnection
					.getSleeManagementMBeanUtils().getLogManagementMBeanUtils()
					.getGenericHandlerFormatterClassName(loggerName, i);
			String _filterClass = sleeConnection.getSleeManagementMBeanUtils()
					.getLogManagementMBeanUtils()
					.getGenericHandlerFilterClassName(loggerName, i);
			String _h_level = sleeConnection.getSleeManagementMBeanUtils()
					.getLogManagementMBeanUtils().getGenericHandlerLevel(
							loggerName, i);
			String _h_name = sleeConnection.getSleeManagementMBeanUtils()
					.getLogManagementMBeanUtils().getHandlerName(loggerName, i);
			String _h_className = sleeConnection.getSleeManagementMBeanUtils()
					.getLogManagementMBeanUtils().getHandlerClassName(
							loggerName, i);
			// Add fetch for other options
			HandlerInfo hi = new HandlerInfo(i,
					(_h_name == null ? "" : _h_name), _filterClass,
					_formatterClass, _h_className, _h_level, new HashMap());
			hInfos[i] = hi;
			
			
			//TMP
			//if(_h_name!=null && _h_name.equals("NOTIFICATION"))
			//{
			//	this.loggerNamesWithNotifications.add(loggerName);
			//	if(!entriesStorage.containsKey(loggerName))
			//	{
			//		entriesStorage.put(loggerName, new LogEntries(loggerName,new ArrayList()));
			//	}
			//}

		}

		return new LoggerInfo(_parent, _name, _filter, _level, hInfos);

	}

	public void removeHandlerAtIndex(String loggerName, int index)
			throws ManagementConsoleException {

		sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().removeHandler(loggerName, index);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.slee.container.management.console.client.log.LogService#getUseParentHandlers(java.lang.String)
	 */
	public boolean getUseParentHandlers(String loggerName)
			throws ManagementConsoleException {

		return sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().getUseParentHandlersFlag(
						loggerName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.slee.container.management.console.client.log.LogService#setUseParentHandlers(java.lang.String,
	 *      boolean)
	 */
	public void setUseParentHandlers(String loggerName, boolean value)
			throws ManagementConsoleException {

		sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().setUseParentHandlersFlag(
						loggerName, value);

	}

	public String getLoggerFilterClassName(String loggerName)
			throws ManagementConsoleException {

		return sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().getLoggerFilterClassName(
						loggerName);
	}

	public void setLoggerFilterClassName(String loggerName, String className,
			String[] constructorParameters, String[] paramValues)
			throws ManagementConsoleException {

		sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().setLoggerFilterClassName(
						loggerName, className, constructorParameters,
						paramValues);

	}

	public void createGenericHandler(String loggerName, String handlerName,
			String handlerLevel, String handlerClassName,
			String[] constructorParameterTypes, String[] parameterValues,
			String filterClassName, String[] fi_constructorParameterTypes,
			String[] fi_parameterValues, String formatterClassName,
			String[] fo_constructorParameterTypes, String[] fo_parameterValues)
			throws ManagementConsoleException {

		sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().addHandler(loggerName,
						handlerName, handlerLevel, handlerClassName,
						constructorParameterTypes, parameterValues,
						formatterClassName, fo_constructorParameterTypes,
						fo_parameterValues, filterClassName,
						fi_constructorParameterTypes, fi_parameterValues);

	}

	public void createSocketHandler(String loggerName, String handlerLevel,
			String handlerName, String formaterClassName,
			String filterClassName, String host, int port)
			throws ManagementConsoleException {

		sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().addSocketHandler(loggerName,
						handlerLevel, handlerName, formaterClassName,
						filterClassName, host, port);

	}

	public void createNotificationHandler(String loggerName,
			int numberOfEntries, String level, String formaterClassName,
			String filterClassName) throws ManagementConsoleException {

		sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().addNotificationHandler(
						loggerName, numberOfEntries, level, formaterClassName,
						filterClassName);

		//this.loggerNamesWithNotifications.add(loggerName);
		//if(!entriesStorage.containsKey(loggerName))
		//{
		//	entriesStorage.put(loggerName, new LogEntries(loggerName,new ArrayList()));
		//}
		//Logger.global.info("["+this.loggerNamesWithNotifications+"]");

	}

	public void removeNotificationHandler(String loggerName)
			throws ManagementConsoleException {
		sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().removeHandler(loggerName,
						"NOTIFICATION");
		//synchronized (_SYNC_LOCK) {
		//	this.loggerNamesWithNotifications.remove(loggerName);
		//}
	}

	public String getGenericHandlerFilterClassName(String loggerName, int index)
			throws ManagementConsoleException {

		return sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().getGenericHandlerFilterClassName(
						loggerName, index);
	}

	public String getGenericHandlerFormatterClassName(String loggerName,
			int index) throws ManagementConsoleException {

		return sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils()
				.getGenericHandlerFormatterClassName(loggerName, index);
	}

	public String getGenericHandlerLevel(String loggerName, int index)
			throws ManagementConsoleException {

		return sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().getGenericHandlerLevel(
						loggerName, index);
	}

	public void setGenericHandlerFilterClassName(String loggerName, int index,
			String className) throws ManagementConsoleException {

		sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().setGenericHandlerFilterClassName(
						loggerName, index, className);

	}

	public void setGenericHandlerFormatterClassName(String loggerName,
			int index, String className) throws ManagementConsoleException {

		sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils()
				.setGenericHandlerFormatterClassName(loggerName, index,
						className);

	}

	public void setGenericHandlerLevel(String loggerName, int index,
			String level) throws ManagementConsoleException {
		sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().setGenericHandlerLevel(
						loggerName, index, level);

	}

	public String getHandlerName(String loggerName, int index)
			throws ManagementConsoleException {

		return sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().getHandlerName(loggerName, index);
	}

	public void setHandlerName(String loggerName, int index, String newName)
			throws ManagementConsoleException {
		sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().setHandlerName(loggerName, index,
						newName);

	}

	public int getHandlerNotificationInterval(String loggerName)
			throws ManagementConsoleException {

		return sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().getHandlerNotificationInterval(
						loggerName);
	}

	public void setHandlerNotificationInterval(String loggerName,
			int numberOfEntries) throws ManagementConsoleException {
		sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().setHandlerNotificationInterval(
						loggerName, numberOfEntries);

	}

	public boolean addLogger(String name, String level)
			throws ManagementConsoleException {

		return sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().addLogger(name, level);
	}

	public void clearLoggers(String name) throws ManagementConsoleException {
		sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().clearLoggers(name);

	}

	public void reReadConf(String uri) throws ManagementConsoleException {

		sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().reReadConf(uri);

	}

	public void setDefaultNotificationInterval(String numberOfEntries)
			throws ManagementConsoleException {

		sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().setDefaultNotificationInterval(
						numberOfEntries);

	}

	public void setDefaultHandlerLevel(String l)
			throws ManagementConsoleException {

		sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().setDefaultHandlerLevel(l);

	}

	public void setDefaultLoggerLevel(String l)
			throws ManagementConsoleException {
		sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().setDefaultLoggerLevel(l);

	}

	public String getDefaultHandlerLevel() throws ManagementConsoleException {

		return sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().getDefaultHandlerLevel();
	}

	public String getDefaultLoggerLevel() throws ManagementConsoleException {

		return sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().getDefaultLoggerLevel();
	}

	public Integer getDefaultNotificationInterval()
			throws ManagementConsoleException {

		return sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().getDefaultNotificationInterval();
	}

	public String getLoggerLevel(String loggerName)
			throws ManagementConsoleException {

		String val = sleeConnection.getSleeManagementMBeanUtils()
				.getLogManagementMBeanUtils().getLoggerLevel(loggerName);

		return val;
	}

}
