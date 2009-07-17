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
import org.mobicents.isup.parameters.NetworkManagementControls;

/**
 * Start time:13:20:04 2009-04-26<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class NetworkManagementControlsTest extends ParameterHarness {

	public NetworkManagementControlsTest() {
		super();

		super.goodBodies.add(new byte[1]);
		super.goodBodies.add(new byte[] { 0x0E });
		super.goodBodies.add(new byte[] { 0x0E, 32, 45, 0x0A });
	}

	public void testBody1EncodedValues() throws IOException, ParameterRangeInvalidException {

		boolean[] bools = new boolean[] { true, true, false, true, false, true, true };
		NetworkManagementControls eci = new NetworkManagementControls(getBody1(bools));
		byte[] encoded = eci.encodeElement();
		for (int index = 0; index < encoded.length; index++) {
			if (bools[index] != NetworkManagementControls.isTARControlEnabled(encoded[index])) {
				fail("Failed to get TAR bits, at index: " + index);
			}

			if (index == encoded.length - 1) {
				if (((encoded[index] >> 7) & 0x01) != 1) {
					fail("Last byte must have MSB turned on to indicate last byte, this one does not.");
				}
			}
		}

	}

	private byte[] getBody1(boolean[] tarEnabled) {
		byte[] b = new byte[tarEnabled.length];
		for (int index = 0; index < tarEnabled.length; index++) {
			b[index] = NetworkManagementControls.getTAREnabledByte(tarEnabled[index]);
		}
		return b;
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
		return new NetworkManagementControls(new byte[1]);
	}

}
