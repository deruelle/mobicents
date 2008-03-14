package org.mobicents.slee.container.management.console.client.log;

import java.util.ArrayList;
import java.util.Iterator;

import org.mobicents.slee.container.management.console.client.Logger;
import org.mobicents.slee.container.management.console.client.common.CommonControl;
import org.mobicents.slee.container.management.console.client.common.LogPanel;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

public class LogDumpPanel extends LogPanel implements CommonControl {

	// We are displayed in this one, this comopnent should remove itself upon
	// call to apriopriate link
	private ControlledTabedBar parent = null;
	private Hyperlink removeMeLink = new Hyperlink("Remove", null);
	private Widget _this = null;

	private String loggerName=null;
	private LogEntries entries = null;

	public LogDumpPanel(ControlledTabedBar parent, String loggerName) {
		super();
		this.parent = parent;
		this.entries = new LogEntries(loggerName, new ArrayList());
		this.entries.setLimitCount(150);
		this.loggerName=loggerName;
		_this = this;
	}

	public void onHide() {

	}

	public void onInit() {

		super.header.add(removeMeLink);

	}

	public void onShow() {

	}

	class RemoveMeClickListener implements ClickListener {

		public void onClick(Widget arg0) {

			parent.remove(_this);

		}

	}

	protected void populateContent(LogEntries entries) {

		int totalCoount = entries.size() + this.entries.size();
		if (totalCoount > this.entries.getLimitCount()) {
			super.onClean();
		}

		this.entries.append(entries.getEntries());

		Iterator it = this.entries.getEntries().iterator();

		while (it.hasNext()) {
			LogEntry entry = (LogEntry) it.next();

			String level = entry.getLevel();
			if (level.equals(LogTreeNode._LEVEL_SEVERE)) {
				this.error(entry.getMsg());
			} else if (level.equals(LogTreeNode._LEVEL_WARNING)) {
				this.warning(entry.getMsg());
			} else if (level.equals(LogTreeNode._LEVEL_INFO)) {
				this.info(entry.getMsg());
			} else if (level.equals(LogTreeNode._LEVEL_CONFIG)) {
				this.config(entry.getMsg());
			} else if (level.equals(LogTreeNode._LEVEL_FINE)) {
				this.fine(entry.getMsg());
			} else if (level.equals(LogTreeNode._LEVEL_FINER)) {
				this.finer(entry.getMsg());
			} else if (level.equals(LogTreeNode._LEVEL_FINEST)) {
				this.finest(entry.getMsg());
			} else {
				Logger.error("Couldnt find log Level for [" + entry + "]");
			}
		}
	}


	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((loggerName == null) ? 0 : loggerName.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		
		final LogDumpPanel other = (LogDumpPanel) obj;
		if (loggerName == null) {
			if (other.loggerName != null)
				return false;
		} else if (!loggerName.equals(other.loggerName))
			return false;
		return true;
	}

	

	
	
}
