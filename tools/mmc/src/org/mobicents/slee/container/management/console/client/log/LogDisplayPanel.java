package org.mobicents.slee.container.management.console.client.log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.mobicents.slee.container.management.console.client.Logger;
import org.mobicents.slee.container.management.console.client.ServerConnection;
import org.mobicents.slee.container.management.console.client.common.CommonControl;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;

public class LogDisplayPanel extends Composite implements CommonControl {

	private ControlledTabedBar logBars = new ControlledTabedBar();

	private HashMap loggerNamesToDisplay = new HashMap();

	private Timer logCheckoutTimer = null;

	public LogDisplayPanel() {

	}

	public void onHide() {
		//This will cause dump not to be refreshed since we dont run timer task...
		logCheckoutTimer.cancel();

	}

	public void onInit() {

		ScrollPanel scroll = new ScrollPanel();
		// TODO: MOVE ALL px to css, HERE SET PROPER VALUE FOR HEIGHT!!!
		scroll.setHeight("630px");
		scroll.setWidth("630px");
		scroll.add(logBars);
		initWidget(scroll);

	}

	public void onShow() {

		if(logCheckoutTimer!=null)
			logCheckoutTimer.cancel();
		logCheckoutTimer=new LogCheckoutTimer();
		logCheckoutTimer.run();
		logCheckoutTimer=new LogCheckoutTimer();
		logCheckoutTimer.scheduleRepeating(3000);
	}

	class LogCheckoutAsyncCallback implements AsyncCallback {

		public void onFailure(Throwable arg0) {

			Logger.error("Failed to retrieve log dump due to:"
					+ arg0.getMessage());
		}

		public void onSuccess(Object result) {

			Logger.info("===>RR["+result+"]");
			//Set entriesSet = (Set) set;
			//Logger.info("RESULT["+set+"]");
			//Iterator it = entriesSet.iterator();
			//while (it.hasNext()) {
				//LogEntries le = (LogEntries) it.next();
			LogEntries le = (LogEntries) result;
				//if (!loggerNamesToDisplay.containsKey(le.getLoggerName())) {
			//		LogDumpPanel ldp = new LogDumpPanel(logBars, le
			//				.getLoggerName());
			//		ldp.onInit();
					// Here possibly we need check on selected index.
					// logBars.remove(ldp);
		//			logBars.add(ldp);
		//			loggerNamesToDisplay.put(le.getLoggerName(), ldp);
		//		}
				try {
					LogDumpPanel ldp = (LogDumpPanel) loggerNamesToDisplay
							.get(le.getLoggerName());
					ldp.populateContent(le);
				} catch (Exception e) {
					Logger.error("Failed to populate data into["
							+ le.getLoggerName() + "] dump screen: "
							+ e.toString());
				}

			//}
		}

	}

	class LogCheckoutTimer extends Timer {

		public void run() {
			
			//TMPS
			/*
			//Logger.info("===>["+logBars+"]");
			ServerConnection.logServiceAsync
					.getLoggerNamesWithNotifications(new AsyncCallback(){

						public void onFailure(Throwable arg0) {
							
							Logger.error("Failed to fetch logger names with notification handlers due:"+arg0.getMessage());
							
						}

						public void onSuccess(Object result) {
							
							Logger.info("===>RESULT1["+result+"]");
							Set stringNames=(Set) result;
							
							Iterator it=stringNames.iterator();
							while(it.hasNext())
							{
								String loggerName=(String) it.next();
								Logger.info("===>RESULT2["+(!loggerNamesToDisplay.containsKey(loggerName))+"]");
								try{
								if (!loggerNamesToDisplay.containsKey(loggerName)) {
									LogDumpPanel ldp = new LogDumpPanel(logBars, loggerName);
									Logger.info("===>12");
									ldp.onInit();
									Logger.info("===>13");
									ldp.setHeight("630px");
									ldp.setWidth("630px");
									//Logger.info("===>14["+logBars+"]");
									// Here possibly we need check on selected index.
									// logBars.remove(ldp);
									logBars.add(ldp,loggerName);
									Logger.info("===>15");
									loggerNamesToDisplay.put(loggerName, ldp);
								}
								}catch(Exception e)
								{
									Logger.info("===>RESULT EE["+e+"]");
								}
								ServerConnection.logServiceAsync.getLogEntries(loggerName, new LogCheckoutAsyncCallback());
							}
						}});
*/
		}
	}

}
