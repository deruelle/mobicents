package org.mobicents.examples.wakeupbot.events;

import java.util.Date;
import java.util.Random;
import java.io.Serializable;

public final class WakeUpRequestEvent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private java.util.Date date=null;
	private String UID=null;
	private long timeMillisDifference=-1;
	public WakeUpRequestEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof WakeUpRequestEvent) && ((WakeUpRequestEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "WakeUpRequestEvent[" + hashCode() + "]";
	}

	private final long id;
	public Date getDate()
	{return date;}
	public void setDate(Date date)
	{this.date=date;}
	public String getUID()
	{return UID;}
	public void setUID(String UID)
	{this.UID=UID;}
	public long getTimeMillisDifference()
	{return timeMillisDifference;}
	public void setTimeMillisDifference(long timeMillisDifference)
	{this.timeMillisDifference=timeMillisDifference;}

}
