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
import org.mobicents.isup.parameters.CalledDirectoryNumber;
import org.mobicents.isup.parameters.CalledINNumber;
import org.mobicents.isup.parameters.GenericNumber;
import org.mobicents.isup.parameters.HTRInformation;

/**
 * Start time:14:11:03 2009-04-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class HTRInformationTest extends ParameterHarness {

	/**
	 * @throws IOException
	 */
	public HTRInformationTest() throws IOException {
		super.badBodies.add(new byte[1]);

		super.goodBodies.add(getBody(true,HTRInformation._NAI_NATIONAL,HTRInformation._NPI_ISDN, getFiveDigits()));

	}

	private byte[] getBody( boolean isODD, int _NAI, int _NPI, byte[] digits) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// we will use odd number of digits, so we leave zero as MSB

		int nai = _NAI;
		if (isODD)
			nai |= 0x01 << 7;
		int bit3 = 0;
	
		bit3 |= _NPI << 4;
		
		bos.write(nai);
		bos.write(bit3);
		bos.write(digits);
		return bos.toByteArray();
	}

	public void testBody1EncodedValues() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException, ParameterRangeInvalidException {
		HTRInformation bci = new HTRInformation(getBody(false,HTRInformation._NAI_NATIONAL,HTRInformation._NPI_ISDN, getSixDigits()));

		String[] methodNames = { "isOddFlag", "getNatureOfAddressIndicator", "getNumberingPlanIndicator", "getAddress" };
		Object[] expectedValues = { false,HTRInformation._NAI_NATIONAL,HTRInformation._NPI_ISDN, getSixDigitsString()};
		super.testValues(bci, methodNames, expectedValues);
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
		return new HTRInformation(new byte[3]);
	}

}
