package org.mobicents.slee.container.management.console.client.log;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class LogEntries implements IsSerializable {

	
	private String loggerName=null;
	
	private ArrayList entries=null;

	private long accessTime=System.currentTimeMillis();
	
	private int limitCount=-1;
	
	public String getLoggerName() {
		return loggerName;
	}

	public ArrayList getEntries() {
		return entries;
	}

	public LogEntries(String loggerName, ArrayList entries) {
		super();
		this.loggerName = loggerName;
		this.entries = entries;
	}
	
	public LogEntries() {
		super();

	}
	
	public void append(ArrayList entries)
	{
		this.entries.addAll(entries);
		if(limitCount==-1)
		{
			
		}else
		{
			//Iterator lit=this.entries.listIterator(this.entries.size()-limitCount-1);
			//this.entries.retainAll(this.entries.subList(this.entries.size()-limitCount-1, this.entries.size()-1));
			//this.entries.clear();
			int toRemove=this.entries.size()-limitCount;
			while(toRemove>0)
				this.entries.remove(0);
			
			
		}
	}
	
	public int size()
	{
		return this.entries.size();
	}

	public long getAccessTime() {
		return accessTime;
	}

	public void updateAccessTime()
	{
		this.accessTime=System.currentTimeMillis();
	}
	public int getLimitCount() {
		return limitCount;
	}

	public void setLimitCount(int limitCount) {
		this.limitCount = limitCount;
	}
	
	public void trim(int tailSize)
	{
		int toRemove=this.entries.size()-tailSize;
		//Logger.global.info("TO REMOVE["+toRemove+"]");
		while(toRemove>0)
		{
			this.entries.remove(0);
			toRemove--;
		}
	}
	
	public LogEntries clone()
	{
		LogEntries le=new LogEntries(loggerName,new ArrayList());
		le.append(entries);
		le.limitCount=limitCount;
		return le;
		
		
	}
	
}
