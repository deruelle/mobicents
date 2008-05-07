package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.DiameterHeader;

public class DiameterHeaderImpl implements DiameterHeader, Cloneable
{

  private short version;
  
  private boolean isRequest;
  private boolean isProxiable;
  private boolean isError;
  private boolean isPotentiallyRetransmitted;
  
  private int messageLength;
  private int commandCode;
  private int applicationId;
  private int hopByHopId;
  private int endToEndId;
  
  public DiameterHeaderImpl(short version, short commandFlags, int messageLength, int commandCode, int applicationId, int hopByHopId, int endToEndId)
  {
    this.version = version;

    this.isRequest = (commandFlags & 0x80) == 1;
    this.isProxiable = (commandFlags & 0x40) == 1;
    this.isError = (commandFlags & 0x20) == 1;
    this.isPotentiallyRetransmitted = (commandFlags & 0x10) == 1;
    
    this.messageLength = messageLength;
    this.commandCode = commandCode;
    this.applicationId = applicationId;
    this.hopByHopId = hopByHopId;
    this.endToEndId = endToEndId;
  }
  
  public short getVersion()
  {
    return this.version;
  }

  public boolean isRequest()
  {
    return isRequest;
  }

  public boolean isProxiable()
  {
    return isProxiable;
  }

  public boolean isError()
  {
    return this.isError;
  }

  public boolean isPotentiallyRetransmitted()
  {
    return isPotentiallyRetransmitted;
  }

  public int getMessageLength()
  {
    return this.messageLength;
  }

  public int getCommandCode()
  {
    return this.commandCode;
  }

  public int getApplicationId()
  {
    return this.applicationId;
  }

  public int getHopByHopId()
  {
    return this.hopByHopId;
  }

  public int getEndToEndId()
  {
    return this.endToEndId;
  }

  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException cnse)
    {
      throw new RuntimeException("Clone not supported.", cnse);
    }
  }
  
  public String toString()
  {
    return "DiameterHeader = version[" + version + "], " +
        "isRequest[" + isRequest + "], " +
        "isProxiable[" + isProxiable + "], " +
        "isError[" + isError + "], " +
        "isPotentiallyRetransmitted[" + isPotentiallyRetransmitted + "], " +
        "messageLength[" + messageLength + "], " +
        "commandCode[" + commandCode + "], " +
        "applicationId[" + applicationId + "], " +
        "hopByHopId[" + hopByHopId + "], " + 
        "endToEndId[" + endToEndId + "].";
  }
  
}
