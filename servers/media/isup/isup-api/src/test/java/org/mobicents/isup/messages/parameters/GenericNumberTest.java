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

/**
 * Start time:14:11:03 2009-04-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class GenericNumberTest extends ParameterHarness {

	/**
	 * @throws IOException
	 */
	public GenericNumberTest() throws IOException {
		super.badBodies.add(new byte[1]);

		super.goodBodies.add(getBody(GenericNumber._NQIA_CONNECTED_NUMBER, false, GenericNumber._NAI_INTERNATIONAL, GenericNumber._NI_COMPLETE, GenericNumber._NPI_ISDN,
				GenericNumber._APRI_NOT_AVAILABLE, GenericNumber._SI_USER_PROVIDED_VERIFIED_FAILED, getSixDigits()));

	}

	private byte[] getBody(int _NQI, boolean isODD, int _NAI, boolean _NI, int _NPI, int _APR, int _SI, byte[] digits) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// we will use odd number of digits, so we leave zero as MSB

		int nai = _NAI;
		if (isODD)
			nai |= 0x01 << 7;
		int bit3 = _SI;
		bit3 |= _APR << 2;
		bit3 |= _NPI << 4;
		if (_NI)
			bit3 |= 0x01 << 7;

		bos.write(_NQI);
		bos.write(nai);
		bos.write(bit3);
		bos.write(digits);
		return bos.toByteArray();
	}

	public void testBody1EncodedValues() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException, ParameterRangeInvalidException {
		GenericNumber bci = new GenericNumber(getBody(GenericNumber._NQIA_CONNECTED_NUMBER, false, GenericNumber._NAI_INTERNATIONAL, GenericNumber._NI_COMPLETE, GenericNumber._NPI_ISDN,
				GenericNumber._APRI_NOT_AVAILABLE, GenericNumber._SI_USER_PROVIDED_VERIFIED_FAILED, getSixDigits()));

		String[] methodNames = { "getNumberQualifierIndicator", "getNatureOfAddressIndicator", "isNumberIncomplete", "getNumberingPlanIndicator", "getAddressRepresentationRestrictedIndicator",
				"getScreeningIndicator", "getAddress" };
		Object[] expectedValues = { GenericNumber._NQIA_CONNECTED_NUMBER, GenericNumber._NAI_INTERNATIONAL, GenericNumber._NI_COMPLETE, GenericNumber._NPI_ISDN, GenericNumber._APRI_NOT_AVAILABLE,
				GenericNumber._SI_USER_PROVIDED_VERIFIED_FAILED, getSixDigitsString() };
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
	public ISUPComponent getTestedComponent() {
		return new GenericNumber(0, "1", 1, 1, 1, false, 1);
	}

}
