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

/**
 * Start time:14:11:03 2009-04-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class CalledINNumberTest extends ParameterHarness {

	/**
	 * @throws IOException
	 */
	public CalledINNumberTest() throws IOException {
		super.badBodies.add(new byte[1]);
		

		super.goodBodies.add(getBody1());
		super.goodBodies.add(getBody2());
	}

	private byte[] getBody1() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// we will use odd number of digits, so we leave zero as MSB

		bos.write(CalledINNumber._NAI_NETWORK_SPECIFIC);
		int v = CalledINNumber._APRI_ALLOWED << 2;
		v |= CalledINNumber._NPI_ISDN << 4;
		bos.write(v);
		bos.write(super.getSixDigits());
		return bos.toByteArray();
	}

	private byte[] getBody2() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		bos.write(CalledINNumber._NAI_NETWORK_SPECIFIC | (0x01 << 7));
		int v = CalledINNumber._APRI_ALLOWED << 2;
		v |= CalledINNumber._NPI_ISDN << 4;
		bos.write(v);
		bos.write(super.getFiveDigits());
		return bos.toByteArray();
	}

	public void testBody1EncodedValues() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException, ParameterRangeInvalidException {
		CalledINNumber bci = new CalledINNumber(getBody1());
	
		String[] methodNames = { "getNumberingPlanIndicator", "getAddressRepresentationRestrictedIndicator", "getNatureOfAddressIndicator", "isOddFlag", "getAddress" };
		Object[] expectedValues = { CalledINNumber._NPI_ISDN, CalledINNumber._APRI_ALLOWED, CalledINNumber._NAI_NETWORK_SPECIFIC, false, super.getSixDigitsString() };
		super.testValues(bci, methodNames, expectedValues);
	}

	public void testBody2EncodedValues() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException, ParameterRangeInvalidException {
		CalledINNumber bci = new CalledINNumber(getBody2());

		String[] methodNames = { "getNumberingPlanIndicator", "getAddressRepresentationRestrictedIndicator", "getNatureOfAddressIndicator", "isOddFlag", "getAddress" };
		Object[] expectedValues = { CalledINNumber._NPI_ISDN, CalledINNumber._APRI_ALLOWED, CalledINNumber._NAI_NETWORK_SPECIFIC, true, super.getFiveDigitsString() };
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
		return new CalledINNumber(0, "1", 1, 1);
	}

}
