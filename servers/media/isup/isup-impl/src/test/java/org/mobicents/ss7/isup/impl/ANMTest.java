package org.mobicents.ss7.isup.impl;

import java.util.Arrays;


import org.mobicents.ss7.isup.message.AnswerMessage;
import org.mobicents.ss7.isup.message.parameter.BackwardCallIndicators;
import org.mobicents.ss7.isup.message.parameter.CallReference;
import org.mobicents.ss7.isup.message.parameter.ServiceActivation;

/**
 * Start time:09:26:46 2009-04-22<br>
 * Project: mobicents-isup-stack<br>
 * Test for ANM
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ANMTest extends MessageHarness {

	
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
				//No mandatory varaible part, no ptr
				,0x01 // ptr to optional part
				
				
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

		AnswerMessageImpl ANM=new AnswerMessageImpl(this,message);
		byte[] encodedBody = ANM.encodeElement();
		boolean equal = Arrays.equals(message, encodedBody);
		assertTrue(super.makeStringCompare(message, encodedBody),equal);
	}
	
	public void testTwo_Params() throws Exception
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
				//No mandatory varaible part, no ptr
				,0x01 // ptr to optional part
				
				
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

		AnswerMessageImpl ANM=new AnswerMessageImpl(this,message);
		try{
			CallReference cr = (CallReference) ANM.getParameter(CallReference._PARAMETER_CODE);
			assertNotNull("Call Reference return is null, it should not be",cr);
			if(cr == null)
				return;
			super.assertEquals("CallIdentity missmatch",65793, cr.getCallIdentity());
			super.assertEquals("SignalingPointCode missmatch",478, cr.getSignalingPointCode());
			
		}catch(Exception e)
		{
			e.printStackTrace();
			super.fail("Failed on get parameter["+CallReference._PARAMETER_CODE+"]:"+e);
		}
		try{
			ServiceActivation sa = (ServiceActivation) ANM.getParameter(ServiceActivation._PARAMETER_CODE);
			assertNotNull("Service Activation return is null, it should not be",sa);
			if(sa == null)
				return;
			
			byte[] b=sa.getFeatureCodes();
			assertNotNull("ServerActivation.getFeatureCodes() is null",b);
			if(b == null)
			{
				return;
			}	
			assertEquals("Length of param is wrong",7 ,b.length);
			if(b.length != 7)
				return;
			assertTrue("Content of ServiceActivation.getFeatureCodes is wrong", super.makeCompare(b, new byte[]{0x01
					,0x02
					,0x03
					,0x04
					,0x05
					,0x06
					,0x07}));
			
		}catch(Exception e)
		{
			e.printStackTrace();
			super.fail("Failed on get parameter["+CallReference._PARAMETER_CODE+"]:"+e);
		}
		
	}
	public static void main(String[] args)
	{
		System.err.println( ((0x01 & 0x3F)<<8) | (0xDE & 0xFF));
		System.err.println( ((0x01 & 0x3F)<<16) | ((0x01 & 0xFF)<<8)  |(0x01 & 0xFF));
		
		
	}
}
