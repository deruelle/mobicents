/**
 * Start time:09:16:42 2009-04-22<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 * 
 */
package org.mobicents.ss7.isup.impl;

import java.io.IOException;

import org.mobicents.ss7.SS7Provider;
import org.mobicents.ss7.isup.ISUPClientTransaction;
import org.mobicents.ss7.isup.ISUPListener;
import org.mobicents.ss7.isup.ISUPMessageFactory;
import org.mobicents.ss7.isup.ISUPProvider;
import org.mobicents.ss7.isup.ISUPServerTransaction;
import org.mobicents.ss7.isup.ISUPTransaction;
import org.mobicents.ss7.isup.ParameterRangeInvalidException;
import org.mobicents.ss7.isup.TransactionAlredyExistsException;
import org.mobicents.ss7.isup.message.ISUPMessage;

import junit.framework.TestCase;

/**
 * Start time:09:16:42 2009-04-22<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public class MessageHarness extends TestCase implements ISUPProvider{

	
	protected ISUPMessageFactory messageFactory=new ISUPMessageFactoryImpl(this);
	//FIXME: add code to check values :)
	
	
	protected boolean makeCompare(byte[] b1,byte[] b2)
	{
		if(b1.length != b2.length)
			return false;
		
	
		
		for(int index = 0;index<b1.length;index++)
		{
			if(b1[index] != b2[index])
				return false;
		}
		
		return true;
		
	}
	
	
	
	protected String makeStringCompare(byte[] b1,byte[] b2)
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



	/* (non-Javadoc)
	 * @see org.mobicents.ss7.isup.ISUPProvider#addListener(org.mobicents.ss7.isup.ISUPListener)
	 */
	public void addListener(ISUPListener listener) {
		// TODO Auto-generated method stub
		
	}



	/* (non-Javadoc)
	 * @see org.mobicents.ss7.isup.ISUPProvider#createClientTransaction(org.mobicents.ss7.isup.message.ISUPMessage)
	 */
	public ISUPClientTransaction createClientTransaction(ISUPMessage msg) throws TransactionAlredyExistsException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}



	/* (non-Javadoc)
	 * @see org.mobicents.ss7.isup.ISUPProvider#createServerTransaction(org.mobicents.ss7.isup.message.ISUPMessage)
	 */
	public ISUPServerTransaction createServerTransaction(ISUPMessage msg) throws TransactionAlredyExistsException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}



	/* (non-Javadoc)
	 * @see org.mobicents.ss7.isup.ISUPProvider#getMessageFactory()
	 */
	public ISUPMessageFactory getMessageFactory() {
		// TODO Auto-generated method stub
		return null;
	}



	/* (non-Javadoc)
	 * @see org.mobicents.ss7.isup.ISUPProvider#getTransportProvider()
	 */
	public SS7Provider getTransportProvider() {
		// TODO Auto-generated method stub
		return null;
	}



	/* (non-Javadoc)
	 * @see org.mobicents.ss7.isup.ISUPProvider#removeListener(org.mobicents.ss7.isup.ISUPListener)
	 */
	public void removeListener(ISUPListener listener) {
		// TODO Auto-generated method stub
		
	}



	/* (non-Javadoc)
	 * @see org.mobicents.ss7.isup.ISUPProvider#sendMessage(org.mobicents.ss7.isup.message.ISUPMessage)
	 */
	public void sendMessage(ISUPMessage msg) throws ParameterRangeInvalidException, IOException {
		// TODO Auto-generated method stub
		
	}



	/* (non-Javadoc)
	 * @see org.mobicents.ss7.isup.ISUPProvider#sendMessage(org.mobicents.ss7.isup.ISUPTransaction)
	 */
	public void sendMessage(ISUPTransaction msg) throws ParameterRangeInvalidException, IOException {
		// TODO Auto-generated method stub
		
	}
	
}
