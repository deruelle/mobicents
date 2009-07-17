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
import org.mobicents.isup.parameters.OptionalForwardCallIndicators;

/**
 * Start time:16:20:47 2009-04-26<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class OptionalForwardCallIndicatorsTest extends ParameterHarness {

	private static final int _TURN_ON = 1;
	private static final int _TURN_OFF = 0;
	
	public OptionalForwardCallIndicatorsTest() {
		super();
		super.goodBodies.add(new byte[] { 7 });
		super.badBodies.add(new byte[] { 8, 8 });
	}

	public void testBody1EncodedValues() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException, ParameterRangeInvalidException {
		OptionalForwardCallIndicators bci = new OptionalForwardCallIndicators(getBody(OptionalForwardCallIndicators._CUGCI_CUG_CALL_OAL,
																					  OptionalForwardCallIndicators._SSI_ADDITIONAL_INFO,
																					  OptionalForwardCallIndicators._CLIRI_REQUESTED));

		String[] methodNames = {    "getClosedUserGroupCallIndicator", 
				                    "isSimpleSegmentationIndicator", 
				                    "isConnectedLineIdentityRequestIndicator"};
		Object[] expectedValues = { OptionalForwardCallIndicators._CUGCI_CUG_CALL_OAL,
				  					OptionalForwardCallIndicators._SSI_ADDITIONAL_INFO,
				  					OptionalForwardCallIndicators._CLIRI_REQUESTED };
		super.testValues(bci, methodNames, expectedValues);
	}

	private byte[] getBody(int i, boolean _SSI, boolean _CLIRI){
		
		
		byte v = (byte) i;
		v |= ((_SSI ? _TURN_ON : _TURN_OFF) << 2);
		v |= ((_CLIRI ? _TURN_ON : _TURN_OFF) << 7);
		return new byte[] { (byte) v };
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
		return new OptionalForwardCallIndicators(new byte[1]);
	}

}
