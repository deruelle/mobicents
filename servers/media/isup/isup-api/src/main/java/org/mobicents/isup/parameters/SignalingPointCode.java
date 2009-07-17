/**
 * Start time:12:23:47 2009-04-02<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

import org.mobicents.isup.ParameterRangeInvalidException;

/**
 * Start time:12:23:47 2009-04-02<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class SignalingPointCode extends AbstractPointCode {

	
	public static final int _PARAMETER_CODE = 0x1E;
		public int getCode() {

		return _PARAMETER_CODE;
	}
		public SignalingPointCode() {
			super();
			// TODO Auto-generated constructor stub
		}
		public SignalingPointCode(byte[] b) throws ParameterRangeInvalidException {
			super(b);
			// TODO Auto-generated constructor stub
		}
		
}
