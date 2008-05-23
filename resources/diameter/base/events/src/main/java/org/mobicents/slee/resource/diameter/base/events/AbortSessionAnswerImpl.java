package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.AbortSessionAnswer;

import org.jdiameter.api.Message;

/**
 * 
 * Implementation of Abort-Session-Answer Diameter Message.
 * 
 * <br>Super project:  mobicents
 * <br>3:03:32 PM May 21, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author Erick Svenson
 */
public class AbortSessionAnswerImpl extends ExtensionDiameterMessageImpl implements
		AbortSessionAnswer {

	public AbortSessionAnswerImpl(Message message) {
        super(message);
    }

	@Override
	public String getLongName() {

		return "Abort-Session-Answer";
	}

	@Override
	public String getShortName() {

		return "ASA";
	}
}
