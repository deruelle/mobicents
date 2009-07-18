/**
 * Start time:15:07:07 2009-07-17<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.messages;

import java.util.Arrays;

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

		ReleaseCompleteMessage iam=new ReleaseCompleteMessage(this,message);
		byte[] encodedBody = iam.encodeElement();
		boolean equal = Arrays.equals(message, encodedBody);
		assertTrue(super.makeCompare(message, encodedBody),equal);
	}
	
}
