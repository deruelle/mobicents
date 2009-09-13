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
import org.mobicents.ss7.isup.message.BlockingMessage;
import org.mobicents.ss7.isup.message.ReleaseMessage;

/**
 * Start time:15:07:07 2009-07-17<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class BLOTest extends MessageHarness{

	public void testOne() throws Exception
	{
	
		byte[] message={
	
				BlockingMessage._MESSAGE_CODE_BLO

		};

		
		BlockingMessage blo=super.messageFactory.createBLO();
		blo.decodeElement(message);
		byte[] encodedBody = blo.encodeElement();
		boolean equal = Arrays.equals(message, encodedBody);
		assertTrue(super.makeStringCompare(message, encodedBody),equal);
	}
	
}
