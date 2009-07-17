/**
 * Start time:09:16:42 2009-04-22<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.messages;

import junit.framework.TestCase;

/**
 * Start time:09:16:42 2009-04-22<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class MessageHarness extends TestCase{

	//FIXME: add code to check values :)
	
	
	
	
	
	
	protected String makeCompare(byte[] b1,byte[] b2)
	{
		int totalLength = 0;
		if(b1.length>=b2.length)
		{
			totalLength = b1.length;
		}else
		{
			totalLength = b2.length;
		}
		
		String out = "";
		
		for(int index = 0;index<totalLength;index++)
		{
			if(b1.length>index)
			{
				out+="b1["+Integer.toHexString(b1[index])+"]";
			}else
			{
				out+="b1[NOP]";
			}
			
			if(b2.length>index)
			{
				out+="b2["+Integer.toHexString(b2[index])+"]";
			}else
			{
				out+="b2[NOP]";
			}
			out+="\n";
		}
		
		return out;
	}
	
}
