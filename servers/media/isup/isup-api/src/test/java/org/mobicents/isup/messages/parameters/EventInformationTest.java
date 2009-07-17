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
import org.mobicents.isup.parameters.EchoControlInformation;
import org.mobicents.isup.parameters.EventInformation;

/**
 * Start time:11:34:01 2009-04-24<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class EventInformationTest extends ParameterHarness {

	public EventInformationTest() {
		super();
		super.goodBodies.add(new byte[] { 67 });
		super.badBodies.add(new byte[2]);
	}

	private byte[] getBody(int _EI, boolean _RI) {
		byte[] b = new byte[1];
		int v = _EI;
		if (_RI)
			v |= 0x01 << 7;

		b[0] = (byte) v;

		return b;
	}

	public void testBody1EncodedValues() throws ParameterRangeInvalidException {
		EventInformation eci = new EventInformation(getBody(EventInformation._EVENT_INDICATOR_CFONNR, EventInformation._EVENT_PRESENTATION_IPR));

		String[] methodNames = { "getEventIndicator", "isEventPresentationRestrictedIndicator" };
		Object[] expectedValues = { EventInformation._EVENT_INDICATOR_CFONNR, EventInformation._EVENT_PRESENTATION_IPR };
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
		return new EventInformation(new byte[1]);
	}

}
