package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.DiameterCommand;
import net.java.slee.resource.diameter.base.events.DiameterHeader;
import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.AvpList;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;

public class DiameterMessageImpl implements DiameterMessage
{

  private DiameterHeader header;
  private DiameterCommand command;
  private AvpList avps;
  
  public DiameterMessageImpl(DiameterHeader header, DiameterCommand command, AvpList avps)
  {
    this.header = header;
    this.command = command;
    this.avps = avps;
  }
  
  public AvpList getAvps()
  {
    return this.avps;
  }

  public DiameterCommand getCommand()
  {
    return this.command;
  }

  public DiameterIdentityAvp getDestinationHost()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public DiameterIdentityAvp getDestinationRealm()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public DiameterHeader getHeader()
  {
    return this.header;
  }

  public DiameterIdentityAvp getOriginHost()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public DiameterIdentityAvp getOriginRealm()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public String getSessionId()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public void setDestinationHost( DiameterIdentityAvp destinationHost )
  {
    // TODO Auto-generated method stub

  }

  public void setDestinationRealm( DiameterIdentityAvp destinationRealm )
  {
    // TODO Auto-generated method stub

  }

  public void setOriginHost( DiameterIdentityAvp originHost )
  {
    // TODO Auto-generated method stub

  }

  public void setOriginRealm( DiameterIdentityAvp originRealm )
  {
    // TODO Auto-generated method stub

  }

  public void setSessionId( String sessionId )
  {
    // TODO Auto-generated method stub

  }

  public Object clone()
  {
    try
    {
      DiameterMessageImpl newMessage = (DiameterMessageImpl)super.clone();
      newMessage.header = (DiameterHeader) this.header.clone();
      newMessage.command = (DiameterCommand)this.command;
      newMessage.avps = (AvpList)this.avps.clone();
      
      return newMessage;
    }
    catch ( CloneNotSupportedException cnse )
    {
      throw new RuntimeException("Clone not supported.", cnse);
    }
  }

}
