/**
 * Start time:20:07:45 2009-04-26<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.messages.parameters;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.mobicents.isup.ISUPComponent;
import org.mobicents.isup.ParameterRangeInvalidException;
import org.mobicents.isup.parameters.CalledPartyNumber;
import org.mobicents.isup.parameters.RedirectionInformation;

/**
 * Start time:20:07:45 2009-04-26<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class RedirectionInformationTest extends ParameterHarness {

	
	
	
	
	public RedirectionInformationTest() {
		super();
		super.goodBodies.add(new byte[]{(byte) 0xC5,(byte) 0x03});
		
		
		
		super.badBodies.add(new byte[]{(byte) 0xC5,(byte) 0x0F});
		super.badBodies.add(new byte[1]);
		super.badBodies.add(new byte[3]);
	}

	
	public void testBody1EncodedValues() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException, ParameterRangeInvalidException {
		RedirectionInformation bci = new RedirectionInformation(getBody(RedirectionInformation._RI_CALL_D_RNPR,RedirectionInformation._ORR_UNA,1,RedirectionInformation._RR_DEFLECTION_IE));
	
		String[] methodNames = { "getRedirectingIndicator", 
								 "getOriginalRedirectionReason", 
								 "getRedirectionCounter", 
								 "getRedirectionReason" };
		Object[] expectedValues = { RedirectionInformation._RI_CALL_D_RNPR,
								    RedirectionInformation._ORR_UNA,
								    1,
								    RedirectionInformation._RR_DEFLECTION_IE };
		super.testValues(bci, methodNames, expectedValues);
	}
	
	
	
	private byte[] getBody(int riCallDRnpr, int orrUna, int counter, int rrDeflectionIe) {
		byte[] b = new byte[2];
		
		b[0] = (byte) riCallDRnpr;
		b[0]|= orrUna <<4;
		
		
		b[1] = (byte) counter;
		b[1]|= rrDeflectionIe <<4;
		return b;
	}


	/* (non-Javadoc)
	 * @see org.mobicents.isup.messages.parameters.ParameterHarness#getTestedComponent()
	 */
	@Override
	public ISUPComponent getTestedComponent() throws IllegalArgumentException, ParameterRangeInvalidException {
		return new RedirectionInformation(new byte[]{0,1});
	}

}
