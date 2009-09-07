/**
 * Start time:09:26:46 2009-04-22<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 * 
 */
package org.mobicents.ss7.isup.impl;

import java.util.Arrays;

import org.mobicents.ss7.isup.impl.AddressCompleteMessageImpl;
import org.mobicents.ss7.isup.message.AnswerMessage;
import org.mobicents.ss7.isup.message.parameter.BackwardCallIndicators;

/**
 * Start time:09:26:46 2009-04-22<br>
 * Project: mobicents-isup-stack<br>
 * Test for ANM
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ANMTest extends MessageHarness {

	public void testXXX() throws Exception
	{
		
	}
	
	public void testOne() throws Exception
	{
		//FIXME: for now we strip MTP part
		byte[] message={
//				(byte) 0x9B 
//				,(byte) 0xE6
//				,0x13
//				,(byte) 0xC5
//				,0x00
//				,(byte) 0xB7 
//				,(byte) 0xD1 
//				,(byte) 0x8D 
//				,0x08 
//				,0x00, 
				AnswerMessage._MESSAGE_CODE_ANM
				,0x01 // ptr to variable part
				
				
				//Call reference
				,0x01
				,0x05
				,0x01
				,0x01
				,0x01
				,(byte)0xDE
				,0x01
				//ServiceActivation
				,0x33
				,0x07
				,0x01
				,0x02
				,0x03
				,0x04
				,0x05
				,0x06
				,0x07
				
				
				
				
				//End of optional part
				,0x0

				

		};

		AnswerMessageImpl acm=new AnswerMessageImpl(this,message);
		byte[] encodedBody = acm.encodeElement();
		boolean equal = Arrays.equals(message, encodedBody);
		assertTrue(super.makeCompare(message, encodedBody),equal);
	}
	
	public void _testTwo_Params() throws Exception
	{
		//FIXME: for now we strip MTP part
		byte[] message={
//				(byte) 0x9B 
//				,(byte) 0xE6
//				,0x13
//				,(byte) 0xC5
//				,0x00
//				,(byte) 0xB7 
//				,(byte) 0xD1 
//				,(byte) 0x8D 
//				,0x08 
//				,0x00, 
				 0x06 
				,0x01 
				,0x20 
				,0x01 
				,0x29 
				,0x01 
				,0x01 
				,0x12 
				,0x02 
				,(byte) 0x82 
				,(byte) 0x9C 
				,0x00
				

		};

		AnswerMessageImpl acm=new AnswerMessageImpl(this,message);
	
	
		
	}
	
}
