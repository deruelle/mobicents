/**
 * Start time:12:56:06 2009-03-30<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import org.mobicents.isup.ISUPComponent;

/**
 * Start time:12:56:06 2009-03-30<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface ISUPParameter extends ISUPComponent {
	/**
	 * Returns tag representing this element.
	 * 
	 * @return
	 */
	byte[] getTag();
	//Returns value of certain _PARAMETER_CODE element;
	int getCode();
}
