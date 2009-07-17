/**
 * Start time:13:20:04 2009-04-26<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.messages.parameters;

import java.io.IOException;

import org.mobicents.isup.ISUPComponent;
import org.mobicents.isup.ParameterRangeInvalidException;
import org.mobicents.isup.parameters.MLPPPrecedence;
import org.mobicents.isup.parameters.NatureOfConnectionIndicators;

/**
 * Start time:13:20:04 2009-04-26<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class NatureOfConnectionIndicatorsTest extends ParameterHarness {

	public NatureOfConnectionIndicatorsTest() {
		super();
		super.badBodies.add(new byte[2]);
		super.goodBodies.add(new byte[1]);
		super.goodBodies.add(new byte[] { 0x0E });
	}

	public void testBody1EncodedValues() throws IOException, ParameterRangeInvalidException {

		NatureOfConnectionIndicators eci = new NatureOfConnectionIndicators(getBody(NatureOfConnectionIndicators._SI_ONE_SATELLITE, NatureOfConnectionIndicators._CCI_REQUIRED_ON_THIS_CIRCUIT,
				NatureOfConnectionIndicators._ECDI_INCLUDED));

		String[] methodNames = { "getSatelliteIndicator", "getContinuityCheckIndicator", "isEchoControlDeviceIndicator" };
		Object[] expectedValues = { NatureOfConnectionIndicators._SI_ONE_SATELLITE, NatureOfConnectionIndicators._CCI_REQUIRED_ON_THIS_CIRCUIT, NatureOfConnectionIndicators._ECDI_INCLUDED };

		super.testValues(eci, methodNames, expectedValues);
	}

	private byte[] getBody(int siOneSatellite, int cciRequiredOnThisCircuit, boolean ecdiIncluded) {
		
		
		byte b=  (byte) (siOneSatellite | (cciRequiredOnThisCircuit<<2) | (ecdiIncluded? (0x01<<4):(0x00 << 4)));
		
		return new byte[]{b};
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
		return new NatureOfConnectionIndicators(new byte[1]);
	}

}
