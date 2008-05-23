package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.ReAuthRequest;
import net.java.slee.resource.diameter.base.events.avp.ReAuthRequestTypeAvp;

import org.jdiameter.api.Avp;
import org.jdiameter.api.Message;

/**
 * 
 * Implementation of Re-Auth-Request Diameter Message.
 *
 * <br>Super project:  mobicents
 * <br>3:46:21 PM May 21, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author Erick Svenson
 */
public class ReAuthRequestImpl extends ExtensionDiameterMessageImpl implements ReAuthRequest
{
	
	public ReAuthRequestImpl(Message message) {
    super(message);
  }

  public boolean hasReAuthRequestType() {
    return message.getAvps().getAvp(Avp.RE_AUTH_REQUEST_TYPE) != null;
  }

  public ReAuthRequestTypeAvp getReAuthRequestType() {
    return ReAuthRequestTypeAvp.fromInt(getAvpAsInt32(Avp.RE_AUTH_REQUEST_TYPE));
  }

  public void setReAuthRequestType(ReAuthRequestTypeAvp reAuthRequestType) {
    setAvpAsInt32(Avp.RE_AUTH_REQUEST_TYPE, reAuthRequestType.getValue(), true);
  }

	@Override
	public String getLongName() {
		
		return "Re-Auth-Request";
	}

	@Override
	public String getShortName() {

		return "RAR";
	}

	
}
