/**
 * Start time:12:31:58 2009-04-02<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import org.mobicents.isup.ParameterRangeInvalidException;

/**
 * Start time:12:31:58 2009-04-02<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class OriginatingISCPointCode extends AbstractPointCode {
	public static final int _PARAMETER_CODE = 0x2B;

	public int getCode() {

		return _PARAMETER_CODE;
	}

	public OriginatingISCPointCode() {
		super();
		// TODO Auto-generated constructor stub
	}
	public OriginatingISCPointCode(byte[] b) throws ParameterRangeInvalidException {
		super(b);
		
	}
}
