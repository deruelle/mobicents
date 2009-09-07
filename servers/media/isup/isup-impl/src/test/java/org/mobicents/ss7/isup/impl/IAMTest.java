/**
 * Start time:15:07:07 2009-07-17<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * 
 */
package org.mobicents.ss7.isup.impl;

import java.util.Arrays;

import org.mobicents.ss7.isup.impl.InitialAddressMessageImpl;

/**
 * Start time:15:07:07 2009-07-17<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class IAMTest extends MessageHarness{

	public void testOne() throws Exception
	{
		//FIXME: for now we strip MTP part
		byte[] message={
//	MTP PART	E5,
//				9B,
//				22,
//				C5,
//				46,
//				37,
//				C0,
//				8D,
//				08,
//				00,
				0x01,
				0x10,
				0x00,
				0x01,
				0x0A,
				0x03,
				0x02,
				0x0A,
				0x08,
				0x03,
				0x10,
				(byte) 0x83,
				0x60,
				0x38,
				0x04,
				0x10,
				0x65,
				0x0A,
				0x07,
				0x03,
				0x13,
				0x09,
				0x32,
				0x36,
				0x11,
				0x37,
				0x00

		};

		InitialAddressMessageImpl iam=new InitialAddressMessageImpl(this,message);
		byte[] encodedBody = iam.encodeElement();
		boolean equal = Arrays.equals(message, encodedBody);
		assertTrue(super.makeCompare(message, encodedBody),equal);
	}
	
	public void testTwo_Parameters() throws Exception
	{
		//FIXME: for now we strip MTP part
		byte[] message={
//	MTP PART	E5,
//				9B,
//				22,
//				C5,
//				46,
//				37,
//				C0,
//				8D,
//				08,
//				00,
				0x01,
				0x10,
				0x00,
				0x01,
				0x0A,
				0x03,
				0x02,
				0x0A,
				0x08,
				0x03,
				0x10,
				(byte) 0x83,
				0x60,
				0x38,
				0x04,
				0x10,
				0x65,
				0x0A,
				0x07,
				0x03,
				0x13,
				0x09,
				0x32,
				0x36,
				0x11,
				0x37,
				0x00

		};

		InitialAddressMessageImpl iam=new InitialAddressMessageImpl(this,message);
		assertNotNull(iam.getNatureOfConnectionIndicators());
		assertNotNull(iam.getForwardCallIndicators());
		assertNotNull(iam.getCallingPartCategory());
		assertNotNull(iam.getTransmissionMediumRequirement());
		assertNotNull(iam.getCalledPartyNumber());
		assertNotNull(iam.getCallingPartyNumber());
		
	}
}
