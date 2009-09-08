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
import org.mobicents.ss7.isup.impl.message.parameter.EndOfOptionalParametersImpl;
import org.mobicents.ss7.isup.message.CallProgressMessage;
import org.mobicents.ss7.isup.message.parameter.BackwardCallIndicators;
import org.mobicents.ss7.isup.message.parameter.ConnectedNumber;
import org.mobicents.ss7.isup.message.parameter.EventInformation;
import org.mobicents.ss7.isup.message.parameter.TransmissionMediumUsed;

/**
 * Start time:15:07:07 2009-07-17<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class CPGTest extends MessageHarness{

	public void testOne() throws Exception
	{
		//FIXME: for now we strip MTP part
		byte[] message={
				CallProgressMessage._MESSAGE_CODE_CPG
				//EventInformation
				,0x02
				//no mandatory varialbe part, no pointer
				//pointer to optioanl part
				,0x01
				
				//backward call idnicators
				,BackwardCallIndicators._PARAMETER_CODE
				,0x02
				,0x02
				,0x03
				
				//TransmissionMedium Used
				,TransmissionMediumUsed._PARAMETER_CODE
				,0x01
				,0x03
				
				//Connected Number
				,ConnectedNumber._PARAMETER_CODE
				,0x05
				,0x01
				,0x4B
				,(byte) (0x83&0xFF)
				,0x60
				,0x38
				
				
				//End of Opt Part
				,0x00

		};

		CallProgressMessage cpg=new CallProgressMessageImpl(this,message);
		byte[] encodedBody = cpg.encodeElement();
		boolean equal = Arrays.equals(message, encodedBody);
		assertTrue(super.makeStringCompare(message, encodedBody),equal);
	}
	
	public void testTwo_Parameters() throws Exception
	{
		//FIXME: for now we strip MTP part
		byte[] message={
				CallProgressMessage._MESSAGE_CODE_CPG
				//EventInformation
				,0x02
				//no mandatory varialbe part, no pointer
				//pointer to optioanl part
				,0x01
				
				//backward call idnicators
				,BackwardCallIndicators._PARAMETER_CODE
				,0x02
				,0x02
				,0x03
				
				//TransmissionMedium Used
				,TransmissionMediumUsed._PARAMETER_CODE
				,0x01
				,0x03
				
				//Connected Number
				,ConnectedNumber._PARAMETER_CODE
				,0x05
				,0x01
				,0x4B
				,(byte) (0x83&0xFF)
				,0x60
				,0x38
				
				
				//End of Opt Part
				,0x00

		};

		CallProgressMessage cpg=new CallProgressMessageImpl(this,message);
		assertNotNull(cpg.getParameter(EventInformation._PARAMETER_CODE));
		assertNotNull(cpg.getParameter(BackwardCallIndicators._PARAMETER_CODE));
		assertNotNull(cpg.getParameter(TransmissionMediumUsed._PARAMETER_CODE));
		assertNotNull(cpg.getParameter(ConnectedNumber._PARAMETER_CODE));
		
		
		
	}
}
