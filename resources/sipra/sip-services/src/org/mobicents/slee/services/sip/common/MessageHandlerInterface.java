package org.mobicents.slee.services.sip.common;

import java.util.List;
import java.util.Map;

import javax.sip.ClientTransaction;
import javax.sip.ServerTransaction;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.mobicents.slee.services.sip.proxy.RegistrationInformationAccess;




public interface MessageHandlerInterface {

	void processRequest(ServerTransaction stx, Request req,List forkedCTXList,RegistrationInformationAccess location);
	void processResponse(ServerTransaction stx, ClientTransaction ctx,Response resp,List forkedCTXList, Map acumulatedResponses, Map localCancelByeTxMap);
	
	void onCTimer(ClientTransaction ctx,ServerTransaction stx,List forkedCTXList, Map acumulatedResponses, Map localCancelByeTxMap);
}
