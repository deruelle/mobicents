/**
 * Start time:14:11:03 2009-04-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.messages.parameters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.mobicents.isup.ISUPComponent;
import org.mobicents.isup.ParameterRangeInvalidException;
import org.mobicents.isup.parameters.RedirectionNumber;
import org.mobicents.isup.parameters.TransitNetworkSelection;

/**
 * Start time:14:11:03 2009-04-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class TransitNetworkSelectionTest extends ParameterHarness {

	/**
	 * @throws IOException
	 */
	public TransitNetworkSelectionTest() throws IOException {
		super.badBodies.add(new byte[1]);

	}

	public void testBody1EncodedValues() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException, ParameterRangeInvalidException {
		TransitNetworkSelection bci = new TransitNetworkSelection(getBody(false, TransitNetworkSelection._NIP_PDNIC, TransitNetworkSelection._TONI_ITU_T, getSixDigits()));

		String[] methodNames = { "isOddFlag", "getNetworkIdentificationPlan", "getTypeOfNetworkIdentification",  "getAddress" };
		Object[] expectedValues = { false, TransitNetworkSelection._NIP_PDNIC, TransitNetworkSelection._TONI_ITU_T, getSixDigitsString()};
		super.testValues(bci, methodNames, expectedValues);
	}

	private byte[] getBody(boolean isODD, int _NIP, int _TONI, byte[] digits) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// we will use odd number of digits, so we leave zero as MSB

		if (isODD) {
			bos.write(0x80 | (_TONI << 4) | _NIP);
		} else {
			bos.write((_TONI << 4) | _NIP);
		}

		bos.write(digits);
		return bos.toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mobicents.isup.messages.parameters.ParameterHarness#getTestedComponent
	 * ()
	 */
	@Override
	public ISUPComponent getTestedComponent() {
		return new TransitNetworkSelection("11", 1, 1);
	}

}
