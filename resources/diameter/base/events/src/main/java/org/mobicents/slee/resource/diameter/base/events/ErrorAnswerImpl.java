package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.ErrorAnswer;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;

import org.jdiameter.api.Message;

/**
 * 
 * Implementation of Error-Answer Diameter Message.
 *
 * <br>Super project:  mobicents
 * <br>3:37:40 PM May 21, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author Erick Svenson
 */
public class ErrorAnswerImpl extends ExtensionDiameterMessageImpl implements
		ErrorAnswer {

	public ErrorAnswerImpl(Message message) {
		super(message);
	}

	public ProxyInfoAvp getProxyInfo() {
		if(hasProxyInfo())
			return super.getProxyInfos()[0];
		else
			return null;
	}

	public boolean hasProxyInfo() {
		ProxyInfoAvp[] infos = super.getProxyInfos();
		if (infos != null && infos.length > 0)
			return true;
		else
			return false;
	}

}
