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
import org.mobicents.ss7.isup.message.parameter.BackwardCallIndicators;

/**
 * Start time:09:26:46 2009-04-22<br>
 * Project: mobicents-isup-stack<br>
 * Test for ACM
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ACMTest extends MessageHarness {

	
	
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

		AddressCompleteMessageImpl acm=new AddressCompleteMessageImpl(this,message);
		byte[] encodedBody = acm.encodeElement();
		boolean equal = Arrays.equals(message, encodedBody);
		assertTrue(super.makeStringCompare(message, encodedBody),equal);
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

		AddressCompleteMessageImpl acm=new AddressCompleteMessageImpl(this,message);
	
		assertNotNull("BackwardCallIndicator is null", acm.getBackwardCallIndicators());
		assertNotNull("OptionalBackwardCallIndicator is null", acm.getOptionalBakwardCallIndicators());
		assertNotNull("Cause Indicator is null", acm.getCauseIndicators());
		
		BackwardCallIndicators bci = acm.getBackwardCallIndicators();
		assertEquals("BackwardCallIndicator charge indicator does not match",bci.getChargeIndicator(), 1);
		assertEquals("BackwardCallIndicator called party status does not match",bci.getCalledPartysStatusIndicator(), 0);
		assertEquals("BackwardCallIndicator called party category does not match",bci.getCalledPartysCategoryIndicator(), 0);
		assertFalse(bci.isInterworkingIndicator());
		assertFalse(bci.isEndToEndInformationIndicator());
		assertFalse(bci.isIsdnAccessIndicator());
		assertFalse(bci.isHoldingIndicator());
		assertTrue(bci.isEchoControlDeviceIndicator());
		assertEquals("BackwardCallIndicator sccp method does not match",bci.getSccpMethodIndicator(), 0);
		
	}
	
}
