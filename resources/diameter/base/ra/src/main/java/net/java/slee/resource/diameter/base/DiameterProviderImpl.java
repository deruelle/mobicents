package net.java.slee.resource.diameter.base;

import java.io.IOException;

import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;

import org.apache.log4j.Logger;

public class DiameterProviderImpl implements DiameterProvider
{

  private static Logger logger = Logger.getLogger(DiameterProviderImpl.class);

  private DiameterBaseResourceAdaptor ra;

  public DiameterProviderImpl(DiameterBaseResourceAdaptor ra)
  {
    this.ra = ra;
  }
  
  public DiameterActivity createActivity() throws CreateActivityException
  {
    // TODO Auto-generated method stub
    return null;
  }

  public DiameterActivity createActivity( DiameterIdentityAvp destinationHost, DiameterIdentityAvp destinationRealm ) throws CreateActivityException
  {
    // TODO Auto-generated method stub
    return null;
  }

  public DiameterMessageFactory getDiameterMessageFactory()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public int getPeerCount()
  {
    // TODO Auto-generated method stub
    return 0;
  }

  public DiameterMessage sendSyncRequest( DiameterMessage message ) throws IOException
  {
    // TODO Auto-generated method stub
    return null;
  }

public DiameterIdentityAvp[] getConnectedPeers() {
	// TODO Auto-generated method stub
	return null;
}

public DiameterAvpFactory getDiameterAvpFactory() {
	// TODO Auto-generated method stub
	return null;
}

}
