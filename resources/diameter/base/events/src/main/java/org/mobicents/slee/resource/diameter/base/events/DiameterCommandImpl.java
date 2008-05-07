package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.DiameterCommand;

public class DiameterCommandImpl implements DiameterCommand
{

  private int applicationId;
  private int code;
  private String longName;
  private String shortName;
  private boolean isProxiable;
  private boolean isRequest;
  
  public int getApplicationId()
  {
    return this.applicationId;
  }

  public int getCode()
  {
    return this.code;
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
    return this.isProxiable;
  }

  public boolean isRequest()
  {
    return this.isRequest;
  }
  
  public String toString()
  {
    return "DiameterCommand = applicationId[" + applicationId + "], " +
        "code[" + code + "], " + 
        "longName[" + longName + "], " + 
        "shortName[" + shortName + "], " +
        "isProxiable[" + isProxiable + "], " +
        "isRequest[" + isRequest + "].";
  }

}
