package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.ReAuthAnswer;

import org.jdiameter.api.Message;

/**
 * 
 * Implementation of Re-Auth-Answer Diameter Message.
 *
 * <br>Super project:  mobicents
 * <br>3:45:15 PM May 21, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author Erick Svenson
 */
public class ReAuthAnswerImpl extends ExtensionDiameterMessageImpl implements ReAuthAnswer
{

	public ReAuthAnswerImpl(Message message) {
    super(message);
  }
	
	@Override
	public String getLongName() {
		
		return "Re-Auth-Answer";
	}

	@Override
	public String getShortName() {

		return "RAA";
	}
  
}
