/**
 * Start time:11:34:01 2009-04-24<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.messages.parameters;

import org.mobicents.isup.ISUPComponent;
import org.mobicents.isup.ParameterRangeInvalidException;
import org.mobicents.isup.parameters.EchoControlInformation;
import org.mobicents.isup.parameters.MCIDRequestIndicators;

/**
 * Start time:11:34:01 2009-04-24<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class MCIDRequestIndicatorsTest extends ParameterHarness {
	private static final int _TURN_ON = 1;
	private static final int _TURN_OFF = 0;

	public MCIDRequestIndicatorsTest() {
		super();
		super.goodBodies.add(new byte[] { 3 });
		super.badBodies.add(new byte[2]);
	}

	private byte[] getBody(boolean mcidRequest, boolean holdingRequested) {
		int b0 = 0;

		b0 |= (mcidRequest ? _TURN_ON : _TURN_OFF);
		b0 |= ((holdingRequested ? _TURN_ON : _TURN_OFF)) << 1;

		return new byte[] { (byte) b0 };
	}

	public void testBody1EncodedValues() throws ParameterRangeInvalidException {
		MCIDRequestIndicators eci = new MCIDRequestIndicators(getBody(MCIDRequestIndicators._INDICATOR_REQUESTED, MCIDRequestIndicators._INDICATOR_REQUESTED));

		String[] methodNames = { "isMcidRequestIndicator", "isHoldingIndicator" };
		Object[] expectedValues = { MCIDRequestIndicators._INDICATOR_REQUESTED, MCIDRequestIndicators._INDICATOR_REQUESTED };
		super.testValues(eci, methodNames, expectedValues);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mobicents.isup.messages.parameters.ParameterHarness#getTestedComponent
	 * ()
	 */
	@Override
	public ISUPComponent getTestedComponent() throws ParameterRangeInvalidException {
		return new MCIDRequestIndicators(new byte[1]);
	}

}
