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
import org.mobicents.isup.parameters.InformationIndicators;
import org.mobicents.isup.parameters.InformationRequestIndicators;

/**
 * Start time:11:34:01 2009-04-24<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class InformationIndicatorsTest extends ParameterHarness {
	private final static int _TURN_ON = 1;
	private final static int _TURN_OFF = 0;

	public InformationIndicatorsTest() {
		super();
		// super.goodBodies.add(new byte[] { 67, 12 });
		// super.badBodies.add(new byte[3]);
		// super.badBodies.add(new byte[1]);
	}

	private byte[] getBody(boolean _CIRI_INCLUDED, int _CPARI_ADDRESS_INCLUDED, boolean _CPCRI_CATEOGRY_INCLUDED, boolean _HPI_NOT_PROVIDED, boolean _SII_UNSOLICITED, int reserved) {

		int b0 = 0;
		int b1 = 0;
		b0 |= _CPARI_ADDRESS_INCLUDED;
		b0 |= (_HPI_NOT_PROVIDED ? _TURN_ON : _TURN_OFF) << 2;
		b0 |= (_CPCRI_CATEOGRY_INCLUDED ? _TURN_ON : _TURN_OFF) << 5;
		b0 |= (_CIRI_INCLUDED ? _TURN_ON : _TURN_OFF) << 6;
		b0 |= (_SII_UNSOLICITED ? _TURN_ON : _TURN_OFF) << 7;

		b1 |= (reserved & 0x0F) << 4;
	
		return new byte[] { (byte) b0, (byte) b1 };

	}

	public void testBody1EncodedValues() throws ParameterRangeInvalidException {
		InformationIndicators eci = new InformationIndicators(getBody(InformationIndicators._CIRI_INCLUDED, 
																	  InformationIndicators._CPARI_ADDRESS_INCLUDED,
																	  InformationIndicators._CPCRI_CATEOGRY_INCLUDED, 
																	  InformationIndicators._HPI_NOT_PROVIDED, 
																	  InformationIndicators._SII_UNSOLICITED, 
																	  10));
	
		
		String[] methodNames = { 									  "isChargeInformationResponseIndicator", 
																	  "getCallingPartyAddressResponseIndicator", 
																	  "isCallingPartysCategoryResponseIndicator", 
																	  "isHoldProvidedIndicator",
																	  "isSolicitedInformationIndicator", 
																	  "getReserved" };
		Object[] expectedValues = { InformationIndicators._CIRI_INCLUDED, InformationIndicators._CPARI_ADDRESS_INCLUDED, InformationIndicators._CPCRI_CATEOGRY_INCLUDED,
				InformationIndicators._HPI_NOT_PROVIDED, InformationIndicators._SII_UNSOLICITED, 10 };
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
		return new InformationIndicators(new byte[2]);
	}

}
