/**
 * Start time:08:54:42 2009-08-30<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.ss7.isup;

import org.mobicents.ss7.isup.message.AddressCompleteMessage;
import org.mobicents.ss7.isup.message.InitialAddressMessage;
import org.mobicents.ss7.isup.message.ReleaseCompleteMessage;
import org.mobicents.ss7.isup.message.ReleaseMessage;

/**
 * Start time:08:54:42 2009-08-30<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface ISUPListener {
	//public void onMessage(ISUPMessage msg);
	public void onIAM(InitialAddressMessage IAM);
	public void onACM(AddressCompleteMessage ACM);
	public void onRLC(ReleaseCompleteMessage RLC);
	public void onREL(ReleaseMessage REL);

	//etc
	
	
	public void onTransactionTimeout(ISUPClientTransaction tx);
	public void onTransactionTimeout(ISUPServerTransaction tx);
	public void onTransactionEnded(ISUPClientTransaction tx);
	public void onTransactionEnded(ISUPServerTransaction tx);
	
	
}
