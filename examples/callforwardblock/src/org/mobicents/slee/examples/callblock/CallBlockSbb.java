package org.mobicents.slee.examples.callblock;

import java.text.ParseException;

import javax.sip.InvalidArgumentException;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.message.Response;
import javax.slee.*;

import org.apache.log4j.Logger;
import org.mobicents.slee.examples.callforwardblock.BaseSbb;
import org.mobicents.slee.examples.callforwardblock.events.CallBlockReqEvent;

/**
 * Call Block Service Building Block
 * Request: INVITE
 * Response: 486 Busy Here
 * @author hchin
 */
public abstract class CallBlockSbb extends BaseSbb {
	private static Logger log = Logger.getLogger(CallBlockSbb.class);
	
	/**
	 * Process CallBlockReqEvent request
	 * @param event incoming RequestEvent
	 * @param aci ActivityContextInterface - access to activity context
	 */
	public void onCallBlockReqEvent(CallBlockReqEvent event, ActivityContextInterface aci) {
		if (log.isDebugEnabled())
			log.debug("onCallBlockReqEvent received: " + event.getRequest().getMethod());

		// Send busy response back to UA
		ServerTransaction st = (ServerTransaction) aci.getActivity();
		Response response;
		try {
			response = msgFactory.createResponse(Response.BUSY_HERE, event.getRequest());
			st.sendResponse(response);
		} catch (ParseException pe) {
			log.error("Error parsing SIP message", pe);
		} catch (SipException se) {
			log.error("Error sending response", se);
		} catch (InvalidArgumentException iae) {
			log.error("Error invalid argument", iae);
		}
	}
}
