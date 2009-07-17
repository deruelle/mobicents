/**
 * Start time:21:30:13 2009-04-26<br>
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
import org.mobicents.isup.parameters.CalledINNumber;
import org.mobicents.isup.parameters.TerminatingNetworkRoutingNumber;

/**
 * Start time:21:30:13 2009-04-26<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class TerminatingNetworkRoutingNumberTest extends ParameterHarness {

	public TerminatingNetworkRoutingNumberTest() throws IOException {
		super();
		super.goodBodies.add(getBody(false, TerminatingNetworkRoutingNumber._NPI_ISDN, TerminatingNetworkRoutingNumber._NAI_NATIONAL_SN, getSixDigits(),getSixDigits().length));
		super.goodBodies.add(getBody(false, TerminatingNetworkRoutingNumber._NPI_ISDN, TerminatingNetworkRoutingNumber._NAI_NATIONAL_SN, null,-1));
		super.goodBodies.add(getBody(false, TerminatingNetworkRoutingNumber._NPI_ISDN, TerminatingNetworkRoutingNumber._NAI_NATIONAL_SN, null,0));

		super.badBodies.add(getBody(false, TerminatingNetworkRoutingNumber._NPI_ISDN, TerminatingNetworkRoutingNumber._NAI_NATIONAL_SN, new byte[8],8));
		//The diff is that this is odd - only 15 digits :)
		super.goodBodies.add(getBody(true, TerminatingNetworkRoutingNumber._NPI_ISDN, TerminatingNetworkRoutingNumber._NAI_NATIONAL_SN, new byte[8],8));
	}

	public void testBody1EncodedValues() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException, ParameterRangeInvalidException {
		TerminatingNetworkRoutingNumber bci = new TerminatingNetworkRoutingNumber(getBody(false, TerminatingNetworkRoutingNumber._NPI_ISDN, TerminatingNetworkRoutingNumber._NAI_NATIONAL_SN,
				getSixDigits(),getSixDigits().length));

		String[] methodNames = { "isOddFlag", "getNumberingPlanIndicator", "getNatureOfAddressIndicator", "getAddress", "getTnrnLengthIndicator" };
		Object[] expectedValues = { false, TerminatingNetworkRoutingNumber._NPI_ISDN, TerminatingNetworkRoutingNumber._NAI_NATIONAL_SN, getSixDigitsString(), 4 };
		super.testValues(bci, methodNames, expectedValues);
	}

	private byte[] getBody(boolean isODD, int npiIsdn, int naiNationalSn, byte[] sixDigits, int tnrL) throws IOException {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int v = 0;
		if (isODD)
			v |= 0x80;
		v |= npiIsdn << 4;
		//v |= sixDigits.length + 1;
		v |= tnrL+1;

		bos.write(v);
		if(tnrL!=-1)
			bos.write(naiNationalSn);
		if(sixDigits!=null && sixDigits.length>0)
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
		return new TerminatingNetworkRoutingNumber("10", 1, 1);
	}
	
	
}
