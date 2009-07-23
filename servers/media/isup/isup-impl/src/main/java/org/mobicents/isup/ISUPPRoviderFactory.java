/**
 * Start time:09:31:37 2009-07-18<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * 
 */
package org.mobicents.isup;

import org.mobicents.mtp.MTPProvider;

/**
 * Start time:09:31:37 2009-07-18<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ISUPPRoviderFactory {

	private ISUPPRoviderFactory() {

	}

	public static ISUPProvider createProvider(MTPProvider mtp) {

		ISUPProviderImpl providerImpl = new ISUPProviderImpl();
		providerImpl.setTransportProvider(mtp);
		return providerImpl;

	}

}
