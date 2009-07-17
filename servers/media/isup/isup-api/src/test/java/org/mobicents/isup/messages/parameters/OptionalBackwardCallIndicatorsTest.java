/**
 * Start time:16:20:47 2009-04-26<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.messages.parameters;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.mobicents.isup.ISUPComponent;
import org.mobicents.isup.ParameterRangeInvalidException;
import org.mobicents.isup.parameters.NetworkRoutingNumber;
import org.mobicents.isup.parameters.OptionalBakwardCallIndicators;

/**
 * Start time:16:20:47 2009-04-26<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class OptionalBackwardCallIndicatorsTest extends ParameterHarness {

	private static final int _TURN_ON = 1;
	private static final int _TURN_OFF = 0;
	
	public OptionalBackwardCallIndicatorsTest() {
		super();
		super.goodBodies.add(new byte[] { 8 });
		super.badBodies.add(new byte[] { 8, 8 });
	}

	public void testBody1EncodedValues() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException, ParameterRangeInvalidException {
		OptionalBakwardCallIndicators bci = new OptionalBakwardCallIndicators(getBody(OptionalBakwardCallIndicators._IBII_AVAILABLE, OptionalBakwardCallIndicators._CDI_NO_INDICATION,
				OptionalBakwardCallIndicators._SSIR_NO_ADDITIONAL_INFO, OptionalBakwardCallIndicators._MLLPUI_USER));

		String[] methodNames = {    "isInbandInformationIndicator", 
				                    "isCallDiversionMayOccurIndicator", 
				                    "isSimpleSegmentationIndicator", 
				                    "isMllpUserIndicator" };
		Object[] expectedValues = { OptionalBakwardCallIndicators._IBII_AVAILABLE, 
									OptionalBakwardCallIndicators._CDI_NO_INDICATION, 
									OptionalBakwardCallIndicators._SSIR_NO_ADDITIONAL_INFO,
									OptionalBakwardCallIndicators._MLLPUI_USER };
		super.testValues(bci, methodNames, expectedValues);
	}

	private byte[] getBody(boolean ibiiAvailable, boolean cdiNoIndication, boolean ssirNoAdditionalInfo, boolean mllpuiUser) {
		byte b = (byte) ((ibiiAvailable ? _TURN_ON : _TURN_OFF));
		b |= ((cdiNoIndication ? _TURN_ON : _TURN_OFF) << 1);
		b |= ((ssirNoAdditionalInfo ? _TURN_ON : _TURN_OFF) << 2);
		b |= ((mllpuiUser ? _TURN_ON : _TURN_OFF) << 3);
		return new byte[] { b };
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
		return new OptionalBakwardCallIndicators(new byte[1]);
	}

}
