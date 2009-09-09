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


import org.mobicents.ss7.isup.message.CircuitGroupBlockingMessage;
import org.mobicents.ss7.isup.message.CircuitGroupResetMessage;
import org.mobicents.ss7.isup.message.parameter.*;


/**
 * Start time:09:26:46 2009-04-22<br>
 * Project: mobicents-isup-stack<br>
 * Test for GRA
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class GRSTest extends MessageHarness {
	
	public void testOne() throws Exception
	{

		byte[] message={

				CircuitGroupResetMessage._MESSAGE_CODE_GRS

				,0x01 // ptr to variable part
				//no optional, so no pointer
				//RangeAndStatus._PARAMETER_CODE
				,0x01
				,0x01
				
		};

		//CircuitGroupResetMessage grs=new CircuitGroupResetMessageImpl(this,message);
		CircuitGroupResetMessage grs=super.messageFactory.createGRS();
		grs.decodeElement(message);
		byte[] encodedBody = grs.encodeElement();
		boolean equal = Arrays.equals(message, encodedBody);
		assertTrue(super.makeStringCompare(message, encodedBody),equal);
	}
	
	public void testTwo_Params() throws Exception
	{
		byte[] message={

				CircuitGroupResetMessage._MESSAGE_CODE_GRS

				,0x01 // ptr to variable part
				//no optional, so no pointer
				//RangeAndStatus._PARAMETER_CODE
				,0x01
				,0x01
				
		};

		//CircuitGroupResetMessage grs=new CircuitGroupResetMessageImpl(this,message);
		CircuitGroupResetMessage grs=super.messageFactory.createGRS();
		grs.decodeElement(message);
		
		try{
			RangeAndStatus RS = (RangeAndStatus) grs.getParameter(RangeAndStatus._PARAMETER_CODE);
			assertNotNull("Range And Status return is null, it should not be",RS);
			if(RS == null)
				return;
			byte range = RS.getRange();
			assertEquals("Range is wrong,",0x01, range);
			byte[] b=RS.getStatus();
			assertNull("RangeAndStatus.getRange() is not null",b);
		
			
		}catch(Exception e)
		{
			e.printStackTrace();
			super.fail("Failed on get parameter["+CallReference._PARAMETER_CODE+"]:"+e);
		}
		
	}
	
}
