package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.DiameterCommand;
import net.java.slee.resource.diameter.base.events.DiameterHeader;
import net.java.slee.resource.diameter.base.events.ExtensionDiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.AvpList;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;

public class ExtensionDiameterMessageImpl implements ExtensionDiameterMessage
{

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

  public AvpList getExtensionAvps()
  {
    // TODO Auto-generated method stub
    return null;
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

  public void setExtensionAvps( AvpList avps ) throws AvpNotAllowedException
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

  public AvpList getAvps()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public DiameterCommand getCommand()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public DiameterHeader getHeader()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public Object clone()
  {
    // TODO Auto-generated method stub
    return null;
  }
}
