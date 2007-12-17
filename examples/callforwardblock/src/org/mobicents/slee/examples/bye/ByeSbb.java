package org.mobicents.slee.examples.bye;

import gov.nist.javax.sip.stack.SIPDialog;

import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.Transaction;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.slee.ActivityContextInterface;

import org.apache.log4j.Logger;
import org.mobicents.slee.examples.callforwardblock.BaseSbb;
import org.mobicents.slee.examples.callforwardblock.events.ByeReqEvent;

/**
 * SBB for handling ByeReqEvents from other services.  This SBB will handle the original
 * BYE request bye issuing a new BYE request to destination and attaching this SBB to the activity
 * context (ClientTransaction).  All success (200 OK) response are handled by this SBB by creating
 * a new response to send back to the originator.
 * <p>
 * Assumption, dialog support is turned ON (either manually or by stack) in order for this Sbb to function properly.
 * The requesting dialog (stored in ByeReqEvent) should contain application data of the second dialog
 * to send BYE request.
 * <p>
 * If Dialog.getApplicationData() returns <code>null<code> then assume the requesting dialog is the destination
 * to send BYE request
 * @author hchin
 */
public abstract class ByeSbb extends BaseSbb {
	private static Logger log = Logger.getLogger(ByeSbb.class);

	/**
	 * Handle custom ByeReqEvent sent from other services within JSLEE
	 * @param event ByeReqEvent to process
	 * @param aci ActivityContextInterface
	 */
	public void onByeReqEvent(ByeReqEvent event, ActivityContextInterface aci) {
		log.info("ByeSbb: Processing onByeReqEvent");

		try {
			Dialog reqDialog = event.getReqDialog();
			Dialog dstDialog = (Dialog)reqDialog.getApplicationData();
			if (dstDialog == null) {
				// assume requesting dialog is the one to send BYE
				dstDialog = reqDialog;
			}
			log.info("Creating BYE request from dialog");
			Request byeReq = dstDialog.createRequest(Request.BYE);

			// attach this SBB to the activity context of new ClientTransaction, response events handled by this SBB
			ClientTransaction ct = sipProvider.getNewClientTransaction(byeReq);
			ActivityContextInterface ctAci = activityContextInterfaceFactory.getActivityContextInterface(ct);
			ctAci.attach(sbbContext.getSbbLocalObject());
			dstDialog.sendRequest(ct);
		} catch (Exception e) {
			log.error(e.getClass().getName() + " creating bye request: " + e.getMessage());
		}
	}
	
	/**
	 * Success (200 OK) response received from destination UA (callee).
	 * Send back 200 OK response back to caller
	 * @param event ResponseEvent encapsulating response from UA
	 * @param aci ActivityContextInterface
	 */
	public void onSuccessRespEvent(ResponseEvent event, ActivityContextInterface aci) {
		log.info("ByeSbb processing onSuccessRespEvent");

		SIPDialog dialog = (SIPDialog)event.getDialog().getApplicationData();
		if (dialog == null) {
			dialog = (SIPDialog)event.getDialog();
		}
		Transaction lastTx = dialog.getLastTransaction();
		Request req = lastTx.getRequest();
		try {
			Response okResp = msgFactory.createResponse(Response.OK, req);
			if (lastTx instanceof ServerTransaction) {
				log.info("Sending 200 OK response");
				ServerTransaction stx = (ServerTransaction)lastTx;
				stx.sendResponse(okResp);
			}
		} catch (Exception e) {
			log.error("Error sending 200 OK response", e);
		}
	}
	
}
