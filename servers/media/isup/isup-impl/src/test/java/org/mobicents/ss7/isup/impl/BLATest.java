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
import org.mobicents.ss7.isup.message.ReleaseMessage;

/**
 * Start time:15:07:07 2009-07-17<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class BLATest extends MessageHarness{

	public void testOne() throws Exception
	{
	
		byte[] message={
	
				BlockingAckMessage._MESSAGE_CODE_BLA

		};

		
		BlockingAckMessage bla=super.messageFactory.createBLA();
		bla.decodeElement(message);
		byte[] encodedBody = bla.encodeElement();
		boolean equal = Arrays.equals(message, encodedBody);
		assertTrue(super.makeStringCompare(message, encodedBody),equal);
	}
	
}
