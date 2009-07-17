/**
 * Start time:13:37:14 2009-04-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.messages.parameters;

import org.mobicents.isup.ISUPComponent;
import org.mobicents.isup.ParameterRangeInvalidException;
import org.mobicents.isup.parameters.CallDiversionInformation;

/**
 * Start time:13:37:14 2009-04-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>ca </a>
 */
public class CallDiversionInformationTest extends ParameterHarness {

	public CallDiversionInformationTest() {
		super();
		// TODO Auto-generated constructor stub
		super.badBodies.add(new byte[0]);
		super.badBodies.add(new byte[2]);

		super.goodBodies.add(getBody1());
		
	}
	private byte[] getBody1() {
		//Notif sub options : 010 - presentation allowed
		//redirect reason   : 0100 - deflection during alerting
		//whole : 00100010
		return new byte[]{0x22};
	}
	
	
	public void testBody1EncodedValues() throws ParameterRangeInvalidException
	{
		CallDiversionInformation cdi = new CallDiversionInformation(getBody1());
		String[] methodNames = { "getNotificationSubscriptionOptions", "getRedirectingReason"};
		Object[] expectedValues = { cdi._NSO_P_A_WITH_RN, cdi._REDIRECTING_REASON_DDA};
		super.testValues(cdi, methodNames, expectedValues);
	}
	/* (non-Javadoc)
	 * @see org.mobicents.isup.messages.parameters.ParameterHarness#getTestedComponent()
	 */
	@Override
	public ISUPComponent getTestedComponent() throws ParameterRangeInvalidException {
		return new CallDiversionInformation(new byte[1]);
	}

}
