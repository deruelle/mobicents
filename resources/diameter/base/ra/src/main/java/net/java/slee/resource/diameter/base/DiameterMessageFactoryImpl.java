package net.java.slee.resource.diameter.base;

import org.jdiameter.api.Session;

import net.java.slee.resource.diameter.base.events.AbortSessionAnswer;
import net.java.slee.resource.diameter.base.events.AbortSessionRequest;
import net.java.slee.resource.diameter.base.events.AccountingAnswer;
import net.java.slee.resource.diameter.base.events.AccountingRequest;
import net.java.slee.resource.diameter.base.events.CapabilitiesExchangeAnswer;
import net.java.slee.resource.diameter.base.events.CapabilitiesExchangeRequest;
import net.java.slee.resource.diameter.base.events.DeviceWatchdogAnswer;
import net.java.slee.resource.diameter.base.events.DeviceWatchdogRequest;
import net.java.slee.resource.diameter.base.events.DiameterCommand;
import net.java.slee.resource.diameter.base.events.DiameterHeader;
import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.DisconnectPeerAnswer;
import net.java.slee.resource.diameter.base.events.DisconnectPeerRequest;
import net.java.slee.resource.diameter.base.events.ExtensionDiameterMessage;
import net.java.slee.resource.diameter.base.events.ReAuthAnswer;
import net.java.slee.resource.diameter.base.events.ReAuthRequest;
import net.java.slee.resource.diameter.base.events.SessionTerminationAnswer;
import net.java.slee.resource.diameter.base.events.SessionTerminationRequest;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;

/**
 * Diameter Base Message Factory
 * 
 * <br>Super project:  mobicents
 * <br>6:52:13 PM May 9, 2008 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author Erick Svenson
 */
public class DiameterMessageFactoryImpl implements DiameterMessageFactory
{

  public DiameterMessageFactoryImpl(Session session, DiameterIdentityAvp ... avps ) {
		// TODO Auto-generated constructor stub
	}

public AbortSessionAnswer createAbortSessionAnswer( DiameterAvp[] avps ) throws AvpNotAllowedException
  {
    // TODO Auto-generated method stub
    return null;
  }

  public AbortSessionAnswer createAbortSessionAnswer()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public AbortSessionRequest createAbortSessionRequest( DiameterAvp[] avps ) throws AvpNotAllowedException
  {
    // TODO Auto-generated method stub
    return null;
  }

  public AbortSessionRequest createAbortSessionRequest()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public AccountingAnswer createAccountingAnswer( DiameterAvp[] avps ) throws AvpNotAllowedException
  {
    // TODO Auto-generated method stub
    return null;
  }

  public AccountingAnswer createAccountingAnswer()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public AccountingRequest createAccountingRequest( DiameterAvp[] avps ) throws AvpNotAllowedException
  {
    // TODO Auto-generated method stub
    return null;
  }

  public AccountingRequest createAccountingRequest()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public CapabilitiesExchangeAnswer createCapabilitiesExchangeAnswer( DiameterAvp[] avps ) throws AvpNotAllowedException
  {
    // TODO Auto-generated method stub
    return null;
  }

  public CapabilitiesExchangeAnswer createCapabilitiesExchangeAnswer()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public CapabilitiesExchangeRequest createCapabilitiesExchangeRequest( DiameterAvp[] avps ) throws AvpNotAllowedException
  {
    // TODO Auto-generated method stub
    return null;
  }

  public CapabilitiesExchangeRequest createCapabilitiesExchangeRequest()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public DeviceWatchdogAnswer createDeviceWatchdogAnswer( DiameterAvp[] avps ) throws AvpNotAllowedException
  {
    // TODO Auto-generated method stub
    return null;
  }

  public DeviceWatchdogAnswer createDeviceWatchdogAnswer()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public DeviceWatchdogRequest createDeviceWatchdogRequest( DiameterAvp[] avps ) throws AvpNotAllowedException
  {
    // TODO Auto-generated method stub
    return null;
  }

  public DeviceWatchdogRequest createDeviceWatchdogRequest()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public DisconnectPeerAnswer createDisconnectPeerAnswer( DiameterAvp[] avps ) throws AvpNotAllowedException
  {
    // TODO Auto-generated method stub
    return null;
  }

  public DisconnectPeerAnswer createDisconnectPeerAnswer()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public DisconnectPeerRequest createDisconnectPeerRequest( DiameterAvp[] avps ) throws AvpNotAllowedException
  {
    // TODO Auto-generated method stub
    return null;
  }

  public DisconnectPeerRequest createDisconnectPeerRequest()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public ExtensionDiameterMessage createMessage( DiameterCommand command, DiameterAvp[] avps ) throws AvpNotAllowedException
  {
    // TODO Auto-generated method stub
    return null;
  }

  public DiameterMessage createMessage( DiameterHeader header, DiameterAvp[] avps ) throws AvpNotAllowedException
  {
    // TODO Auto-generated method stub
    return null;
  }

  public ReAuthAnswer createReAuthAnswer( DiameterAvp[] avps ) throws AvpNotAllowedException
  {
    // TODO Auto-generated method stub
    return null;
  }

  public ReAuthAnswer createReAuthAnswer()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public ReAuthRequest createReAuthRequest( DiameterAvp[] avps ) throws AvpNotAllowedException
  {
    // TODO Auto-generated method stub
    return null;
  }

  public ReAuthRequest createReAuthRequest()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public SessionTerminationAnswer createSessionTerminationAnswer( DiameterAvp[] avps ) throws AvpNotAllowedException
  {
    // TODO Auto-generated method stub
    return null;
  }

  public SessionTerminationAnswer createSessionTerminationAnswer()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public SessionTerminationRequest createSessionTerminationRequest( DiameterAvp[] avps ) throws AvpNotAllowedException
  {
    // TODO Auto-generated method stub
    return null;
  }

  public SessionTerminationRequest createSessionTerminationRequest()
  {
    // TODO Auto-generated method stub
    return null;
  }

}
