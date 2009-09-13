/**
 * Start time:15:07:07 2009-07-17<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * 
 */
package org.mobicents.ss7.isup.impl;

import java.util.Arrays;

import org.mobicents.ss7.isup.impl.ReleaseMessageImpl;
import org.mobicents.ss7.isup.message.BlockingAckMessage;
import org.mobicents.ss7.isup.message.BlockingMessage;
import org.mobicents.ss7.isup.message.ContinuityCheckRequestMessage;
import org.mobicents.ss7.isup.message.ISUPMessage;
import org.mobicents.ss7.isup.message.LoopbackAckMessage;
import org.mobicents.ss7.isup.message.OverloadMessage;
import org.mobicents.ss7.isup.message.ReleaseMessage;
import org.mobicents.ss7.isup.message.ResetCircuitMessage;

/**
 * Start time:15:07:07 2009-07-17<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class RSCTest extends MessageHarness{

	public void testOne() throws Exception
	{
	
		byte[] message={
	
				ResetCircuitMessage._MESSAGE_CODE_RSC

		};

		
		ResetCircuitMessage bla=super.messageFactory.createRSC();
		bla.decodeElement(message);
		byte[] encodedBody = bla.encodeElement();
		boolean equal = Arrays.equals(message, encodedBody);
		assertTrue(super.makeStringCompare(message, encodedBody),equal);
	}
	
}
