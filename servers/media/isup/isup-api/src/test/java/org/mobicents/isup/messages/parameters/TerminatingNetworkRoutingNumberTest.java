/**
 * Start time:21:30:13 2009-04-26<br>
 * Project: mobicents-jain-isup-stack<br>
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
import org.mobicents.isup.parameters.CalledINNumber;
import org.mobicents.isup.parameters.TerminatingNetworkRoutingNumber;

/**
 * Start time:21:30:13 2009-04-26<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class TerminatingNetworkRoutingNumberTest extends ParameterHarness {

	public void testBody1EncodedValues() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException {
		TerminatingNetworkRoutingNumber bci = new TerminatingNetworkRoutingNumber(getBody(false, TerminatingNetworkRoutingNumber._NPI_ISDN, TerminatingNetworkRoutingNumber._NAI_NATIONAL_SN,
				getSixDigits()));

		String[] methodNames = { "isOddFlag", "getNumberingPlanIndicator", "getNatureOfAddressIndicator", "getAddress", "getTnrnLengthIndicator" };
		Object[] expectedValues = { false, TerminatingNetworkRoutingNumber._NPI_ISDN, TerminatingNetworkRoutingNumber._NAI_NATIONAL_SN, getSixDigitsString(), 3 };
		super.testValues(bci, methodNames, expectedValues);
	}

	private byte[] getBody(boolean isODD, int npiIsdn, int naiNationalSn, byte[] sixDigits) throws IOException {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int v = 0;
		if (isODD)
			v |= 0x80;
		v |= npiIsdn << 4;
		v |= sixDigits.length;

		bos.write(v);
		bos.write(naiNationalSn);
		bos.write(sixDigits);

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
		return new TerminatingNetworkRoutingNumber("10", 1, 1, 1);
	}
}
