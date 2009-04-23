/**
 * Start time:09:16:42 2009-04-22<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.messages.parameters;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mobicents.isup.ISUPComponent;

import junit.framework.TestCase;

/**
 * Start time:09:16:42 2009-04-22<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public abstract class ParameterHarness extends TestCase {

	// FIXME: add code to check values :)

	protected List<byte[]> goodBodies = new ArrayList<byte[]>();

	protected List<byte[]> badBodies = new ArrayList<byte[]>();

	protected String makeCompare(byte[] b1, byte[] b2) {
		int totalLength = 0;
		if (b1.length >= b2.length) {
			totalLength = b1.length;
		} else {
			totalLength = b2.length;
		}

		String out = "";

		for (int index = 0; index < totalLength; index++) {
			if (b1.length > index) {
				out += "b1[" + Integer.toHexString(b1[index]) + "]";
			} else {
				out += "b1[NOP]";
			}

			if (b2.length > index) {
				out += "b2[" + Integer.toHexString(b2[index]) + "]";
			} else {
				out += "b2[NOP]";
			}
			out += "\n";
		}

		return out;
	}

	public void testDecodeEncode() throws IOException {

		for (byte[] goodBody : this.goodBodies) {
			ISUPComponent component = this.getTestedComponent();
			doTestDecode(goodBody, true, component);
			byte[] encodedBody = component.encodeElement();
			boolean equal = Arrays.equals(goodBody, encodedBody);
			assertTrue(makeCompare(goodBody, encodedBody), equal);

		}

		for (byte[] badBody : this.badBodies) {
			ISUPComponent component = this.getTestedComponent();
			doTestDecode(badBody, false, component);
			byte[] encodedBody = component.encodeElement();

		}

	}

	public abstract ISUPComponent getTestedComponent();

	protected void doTestDecode(byte[] presumableBody, boolean shouldPass, ISUPComponent component) {
		try {
			component.decodeElement(presumableBody);
			if (!shouldPass) {
				fail("Decoded parameter[" + component.getClass() + "], should not happen. Passed data: " + dumpData(presumableBody));
			}

		} catch (IllegalArgumentException iae) {
			if (shouldPass) {
				fail("Failed to decode parameter[" + component.getClass() + "], should not happen. " + iae + ".Passed data: " + dumpData(presumableBody));
				iae.printStackTrace();
			}
		} catch (Exception e) {
			fail("Failed to decode parameter[" + component.getClass() + "]." + e + ". Passed data: " + dumpData(presumableBody));
			e.printStackTrace();
		}
	}

	protected String dumpData(byte[] b) {
		String s = "\n";
		for (byte bb : b) {
			s += Integer.toHexString(bb);
		}

		return s;
	}

	public void testValues(ISUPComponent component, String[] getterMethodNames, Object[] expectedValues) {
		try {
			Class cClass = component.getClass();
			for (int index = 0; index < getterMethodNames.length; index++) {
				Method m = cClass.getMethod(getterMethodNames[index], null);
				// Should not be null by now
				Object v = m.invoke(component, null);
				if (v == null && expectedValues != null) {
					fail("Failed to validate values in component: " + component.getClass().getName() + ". Value of: " + getterMethodNames[index] + " is null, but test values is not.");
				}
				assertEquals("Failed to validate values in component: " + component.getClass().getName() + ". Value of: " + getterMethodNames[index] + " is " + v + ", but test values is: "
						+ expectedValues[index], expectedValues[index], v);
			}

		} catch (Exception e) {
			fail("Failed to check values on component: " + component.getClass().getName() + ", due to: " + e);
			e.printStackTrace();
		}
	}

}
