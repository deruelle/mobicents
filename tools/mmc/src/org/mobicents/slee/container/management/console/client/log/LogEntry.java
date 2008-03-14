package org.mobicents.slee.container.management.console.client.log;

import com.google.gwt.user.client.rpc.IsSerializable;

public class LogEntry implements IsSerializable {

	
	private String level=null;
	private String msg=null;
	private String loggerName=null;
	public String getLevel() {
		return level;
	}
	public String getMsg() {
		return msg;
	}
	public LogEntry(String level, String msg, String loggerName) {
		super();
		this.level = level;
		this.msg = msg;
		this.loggerName=loggerName;
	}
	
	public LogEntry() {
		super();

	}
	
	public String toString()
	{
		return "L["+loggerName+"]["+level+"]["+msg+"]";
	}
	
}
