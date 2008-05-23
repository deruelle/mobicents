package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.ExtensionDiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;

import org.jdiameter.api.Message;

/**
 * 
 * Implementation of Extension-Diameter-Message Diameter Message.
 *
 * <br>Super project:  mobicents
 * <br>3:39:49 PM May 21, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author Erick Svenson
 */
public class ExtensionDiameterMessageImpl extends DiameterMessageImpl implements ExtensionDiameterMessage
{

  public ExtensionDiameterMessageImpl(Message message) {
    super(message);
  } 

  @Override
	public String getLongName() {
		//FIXME: baranowb; not documented
		return "Extension-Message";
	}

	@Override
	public String getShortName() {
		//FIXME: baranowb; not documented
		return "EM";
	}

  public DiameterAvp[] getExtensionAvps() {
    return getAvps();
  }

  public void setExtensionAvps(DiameterAvp[] avps) throws AvpNotAllowedException {
    for (DiameterAvp a : avps)
      addAvpAsByteArray(a.getCode(), a.byteArrayValue(), a.getMandatoryRule() == 1);
  }

}
