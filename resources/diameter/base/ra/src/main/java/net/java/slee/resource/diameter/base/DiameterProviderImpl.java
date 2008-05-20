package net.java.slee.resource.diameter.base;

import java.io.IOException;
import java.util.concurrent.Future;

import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;

import org.apache.log4j.Logger;
import org.jdiameter.api.Message;
import org.jdiameter.api.Session;
import org.mobicents.slee.resource.diameter.base.events.AbortSessionAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.AccountingAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.CapabilitiesExchangeAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.DeviceWatchdogAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.base.events.DisconnectPeerAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.ReAuthAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.SessionTerminationAnswerImpl;

public class DiameterProviderImpl implements DiameterProvider
{

  private static Logger logger = Logger.getLogger(DiameterProviderImpl.class);

  private DiameterBaseResourceAdaptor ra;
  private Session session;
  
  public DiameterProviderImpl(DiameterBaseResourceAdaptor ra)
  {
    this.ra = ra;
  }
  
  public DiameterActivity createActivity() throws CreateActivityException
  {
    logger.info("Creating activity context.");
    
    return null;
  }

  public DiameterActivity createActivity( DiameterIdentityAvp destinationHost, DiameterIdentityAvp destinationRealm ) throws CreateActivityException
  {
    // TODO Auto-generated method stub
    return null;
  }


  public AccountingClientSessionActivity createAccountingActivity( DiameterIdentityAvp destinationHost, DiameterIdentityAvp destinationRealm ) throws CreateActivityException
  {
    // TODO Auto-generated method stub
    return null;
  }

  public AccountingClientSessionActivity createAccountingActivity() throws CreateActivityException
  {
    // TODO Auto-generated method stub
    return null;
  }

  public AuthClientSessionActivity createAuthenticationActivity( DiameterIdentityAvp destinationHost, DiameterIdentityAvp destinationRealm ) throws CreateActivityException
  {
    // TODO Auto-generated method stub
    return null;
  }

  public AuthClientSessionActivity createAuthenticationActivity() throws CreateActivityException
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  public DiameterMessageFactory getDiameterMessageFactory()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public DiameterAvpFactory getDiameterAvpFactory()
  {
    return null;
  }

  public DiameterMessage sendSyncRequest( DiameterMessage message ) throws IOException
  {
    try
    {
      if(message instanceof DiameterMessageImpl)
      {
        DiameterMessageImpl msg = (DiameterMessageImpl)message;
        
        Future<Message> result = this.session.send( msg.getGenericData() );
        
        Message response;
        
        if(result != null && (response = result.get()) != null)
        {
          switch (response.getCommandCode())
          {
            case 274: // ASR/ASA
              return new AbortSessionAnswerImpl( response );
            case 271: // ACR/ACA
              return new AccountingAnswerImpl( response );
            case 257: // CER/CEA
              return new CapabilitiesExchangeAnswerImpl( response );
            case 280: // DWR/DWA
              return new DeviceWatchdogAnswerImpl( response );
            case 282: // DPR/DPA
              return new DisconnectPeerAnswerImpl( response );
            case 258: // RAR/RAA
              return new ReAuthAnswerImpl( response );
            case 275: // STR/STA
              return new SessionTerminationAnswerImpl( response );
          }        
        }
      }
    }
    catch (Exception e)
    {
      logger.error( "Failure sending sync request.", e );
    }
    
    //FIXME Throw unknown message exception?
    return null;
  }

  public DiameterIdentityAvp[] getConnectedPeers()
  {
    return this.ra.getConnectedPeers();
  }
  
  public int getPeerCount()
  {
    return this.ra.getConnectedPeers().length;
  }

}
