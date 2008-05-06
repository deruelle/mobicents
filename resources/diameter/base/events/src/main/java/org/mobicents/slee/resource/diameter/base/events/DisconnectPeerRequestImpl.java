package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.DiameterCommand;
import net.java.slee.resource.diameter.base.events.DiameterHeader;
import net.java.slee.resource.diameter.base.events.DisconnectPeerRequest;
import net.java.slee.resource.diameter.base.events.avp.AvpList;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.base.events.avp.DisconnectCauseType;

public class DisconnectPeerRequestImpl implements DisconnectPeerRequest
{

  public DisconnectCauseType getDisconnectCause()
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

  public boolean hasDisconnectCause()
  {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean hasOriginHost()
  {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean hasOriginRealm()
  {
    // TODO Auto-generated method stub
    return false;
  }

  public void setDisconnectCause( DisconnectCauseType disconnectCause )
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

  public void setSessionId( String sessionId )
  {
    // TODO Auto-generated method stub

  }

  public Object clone()
  {
    // TODO Auto-generated method stub
    return null;
  }

}
