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
import org.mobicents.ss7.isup.message.CircuitGroupQueryMessage;
import org.mobicents.ss7.isup.message.CircuitGroupQueryResponseMessage;
import org.mobicents.ss7.isup.message.CircuitGroupResetMessage;
import org.mobicents.ss7.isup.message.parameter.*;


/**
 * Start time:09:26:46 2009-04-22<br>
 * Project: mobicents-isup-stack<br>
 * Test for CQR
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class CQRTest extends MessageHarness {
	
	public void testOne() throws Exception
	{

		byte[] message={

				CircuitGroupQueryResponseMessage._MESSAGE_CODE_CQR

				,0x02 // ptr to variable part
				,0x03
				//,0x07
				//no optional, so no pointer
				//RangeAndStatus._PARAMETER_CODE
				,0x01
				,0x01
				//CircuitStateIndicator
				,0x03
				,0x01
				,0x02
				,0x03
				//facilityIndicator
				//,0x01
				//,0x01
				
		};

		CircuitGroupQueryResponseMessage grs=new CircuitGroupQueryResponseMessageImpl(this,message);
		byte[] encodedBody = grs.encodeElement();
		boolean equal = Arrays.equals(message, encodedBody);
		assertTrue(super.makeStringCompare(message, encodedBody),equal);
	}
	
	public void _testTwo_Params() throws Exception
	{
		byte[] message={

				CircuitGroupQueryResponseMessage._MESSAGE_CODE_CQR

				,0x01 // ptr to variable part
				//no optional, so no pointer
				//RangeAndStatus._PARAMETER_CODE
				,0x01
				,0x01
				
		};

		CircuitGroupQueryMessage grs=new CircuitGroupQueryMessageImpl(this,message);

		
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
