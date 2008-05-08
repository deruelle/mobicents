package org.mobicents.slee.resource.diameter.base.events;

import org.jdiameter.api.Message;

import net.java.slee.resource.diameter.base.events.DiameterCommand;

public class DiameterCommandImpl implements DiameterCommand
{

	
	//private long applicationId;
	//private int code;
	private String longName, shortName;
	//private boolean isProxiable, isRequest;
	private Message msg=null;
	
  public DiameterCommandImpl(Message msg, String longName, String shortName) {
		super();
		this.msg=msg;
		//this.applicationId=msg.getApplicationId();
		//this.code=msg.getCommandCode();
		this.longName=longName;
		this.shortName=shortName;
		//this.isProxiable=msg.isProxiable();
		//this.isRequest=msg.isRequest();
	}

public long getApplicationId()
  {
    
    //return this.applicationId;
	return msg.getApplicationId();
  }

  public int getCode()
  {

    //return this.code;
	  return msg.getCommandCode();
  }

  public String getLongName()
  {
    
    return this.longName;
  }

  public String getShortName()
  {
   
    return this.shortName;
  }

  public boolean isProxiable()
  {
    
    //return this.isProxiable;
	  return msg.isProxiable();
  }

  public boolean isRequest()
  {
   
    //return this.isRequest;
	  return this.msg.isRequest();
  }

	public String toString()
  {
    return "DiameterCommand = applicationId[" + msg.getApplicationId() + "], " +
        "code[" + msg.getCommandCode() + "], " + 
        "longName[" + longName + "], " + 
        "shortName[" + shortName + "], " +
        "isProxiable[" + msg.isProxiable() + "], " +
        "isRequest[" + msg.isRequest() + "].";
  }

}
