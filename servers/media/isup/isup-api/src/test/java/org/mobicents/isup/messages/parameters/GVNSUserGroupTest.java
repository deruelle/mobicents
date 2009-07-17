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
import org.mobicents.isup.parameters.GVNSUserGroup;
import org.mobicents.isup.parameters.GenericNumber;

/**
 * Start time:14:11:03 2009-04-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class GVNSUserGroupTest extends ParameterHarness {

	/**
	 * @throws IOException
	 */
	public GVNSUserGroupTest() throws IOException {

		super.goodBodies.add(getBody(getSixDigits(), false));

	}

	private byte[] getBody(byte[] digits, boolean isODD) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// we will use odd number of digits, so we leave zero as MSB
		int header = digits.length;
		if (isODD) {
			header |= 0x01 << 7;
		}
		bos.write(header);
		bos.write(digits);
		return bos.toByteArray();
	}

	public void testBody1EncodedValues() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException, ParameterRangeInvalidException {
		GVNSUserGroup bci = new GVNSUserGroup(getBody(getSixDigits(), false));

		String[] methodNames = { "getAddress", "getGugLengthIndicator" };
		Object[] expectedValues = { getSixDigitsString(), 3 };
		super.testValues(bci, methodNames, expectedValues);
	}

	public void testBody2EncodedValues() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException, ParameterRangeInvalidException {
		GVNSUserGroup bci = new GVNSUserGroup(getBody(getFiveDigits(), true));

		String[] methodNames = { "getAddress", "getGugLengthIndicator" };
		Object[] expectedValues = { getFiveDigitsString(), 3 };
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
		return new GVNSUserGroup("12");
	}

}
