/*
 * Created on 24/Ago/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ptin.xmas.control.common;

import com.ptin.xmas.control.common.interfaces.CallControllerListener;
import com.ptin.xmas.control.common.interfaces.CallController;
import com.ptin.xmas.control.common.interfaces.Call;
import com.ptin.xmas.control.common.InCall;
import com.ptin.xmas.control.exceptions.*;

/**
 * @author 10045724
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CallControllerListenerImpl implements CallControllerListener{
	
	public Call call;

	public void processEvent(int callEvent) throws CallException {

	      System.out.println("B2BMain - processEvent ");
			
	      switch (callEvent){
          	case CallController.fromConnected: 
          	    ((InCall)call).connectTo("abc@10.112.128.233","");
	      }
	      // meter novos estados de acordo c/ estado incom: INCOM_WAITING, INCOM_ON_HOLD, INCOM_ACCEPTING, INCOM_FWDING, INCOM_FWDED
 
	}
}
