/**
 * Start time:15:07:07 2009-07-17<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * 
 */
package org.mobicents.ss7.isup.impl;

import java.util.Arrays;

import org.mobicents.ss7.isup.impl.ReleaseCompleteMessageImpl;
import org.mobicents.ss7.isup.message.ReleaseCompleteMessage;

/**
 * Start time:15:07:07 2009-07-17<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class RELCTest extends MessageHarness{

	public void testOne() throws Exception
	{
		//FIXME: for now we strip MTP part
		byte[] message={
				//EB,
				//A0,
				//09,
				//C5,
				//46,
				//37,
				//C0,
				//8D,
				//08,
				//00,
				0x10,
				0x00


		};

		//ReleaseCompleteMessageImpl iam=new ReleaseCompleteMessageImpl(this,message);
		ReleaseCompleteMessage iam=super.messageFactory.createRLC();
		iam.decodeElement(message);
		byte[] encodedBody = iam.encodeElement();
		boolean equal = Arrays.equals(message, encodedBody);
		assertTrue(super.makeStringCompare(message, encodedBody),equal);
	}
	
}
