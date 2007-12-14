package org.mobicents.slee.services.sip.proxy;

import gov.nist.javax.sip.address.SipUri;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Logger;

import javax.sip.ClientTransaction;
import javax.sip.InvalidArgumentException;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipProvider;
import javax.sip.TransactionState;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.RecordRouteHeader;
import javax.sip.header.RouteHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.mobicents.slee.services.sip.common.MessageHandlerInterface;
import org.mobicents.slee.services.sip.common.MessageUtils;
import org.mobicents.slee.services.sip.common.ProxyConfiguration;
import org.mobicents.slee.services.sip.common.SipLoopDetectedException;
import org.mobicents.slee.services.sip.common.SipSendErrorResponseException;
import org.mobicents.slee.services.sip.registrar.RegistrationBinding;


public class ProxyMachine extends MessageUtils implements
		MessageHandlerInterface {
	protected static final Logger log=Logger.getLogger("ProxyMachine.class");
	
	protected RegistrationInformationAccess reg = null;

	protected AddressFactory af = null;

	protected HeaderFactory hf = null;

	protected MessageFactory mf = null;

	protected SipProvider provider = null;

	protected ProxyCallBackInterface call = null;

	protected HashSet<URI> localMachineInterfaces=new HashSet<URI>();
	
	protected ProxyConfiguration pc=null;
	public ProxyMachine(ProxyConfiguration config,
			RegistrationInformationAccess registrarAccess, AddressFactory af,
			HeaderFactory hf, MessageFactory mf, SipProvider prov,
			ProxyCallBackInterface call,ProxyConfiguration pc) throws ParseException {
		super(config);
		reg = registrarAccess;
		this.mf = mf;
		this.af = af;
		this.hf = hf;
		this.provider = prov;
		this.call = call;
		this.pc=pc;
		
		SipUri localMachineURI=new SipUri();
		localMachineURI.setHost(this.config.getSipHostname());
		localMachineURI.setPort(this.config.getSipPort());
		this.localMachineInterfaces.add(localMachineURI);
	}

	public void onCTimer(ClientTransaction ctx,ServerTransaction stx, List forkedCTXList,
			Map acumulatedResponses, Map localCancelByeTxMap) {

		
		doCancelAndByeTx(ctx, localCancelByeTxMap);
	}

	public void processRequest(ServerTransaction stx, Request req,
			List forkedCTXList, RegistrationInformationAccess location) {
		log.entering(this.getClass().getName(), "processResponse");
		try {
			Request tmpNewRequest = (Request) req.clone();

			// 16.3 Request Validation
			validateRequest(stx, tmpNewRequest);

			// 16.4 Route Information Preprocessing
			routePreProcess(tmpNewRequest);

			// logger.fine("Server transaction " + stx);
			// 16.5 Determining Request Targets
			List targets = determineRequestTargets(tmpNewRequest);

			Iterator it = targets.iterator();
			while (it.hasNext()) {
				Request newRequest = (Request) tmpNewRequest.clone();
				URI target = (URI) it.next();

				//Part of loop detection, here we will stop initial reqeust that makes loop in local stack
				if(isLocalMachine(target))
				{
					
					continue;
				}
				// logger.fine("SIP Proxy Forwarding: "
				// + req.getMethod() + " to URI target: " + target);
				// 16.6 Request Forwarding
				// 1. Copy request

				// 2. Request-URI
				newRequest.setRequestURI(target);
				
				// *NEW* CANCEL processing
				// CANCELs are hop-by-hop, so here must remove any existing Via
				// headers,
				// Record-Route headers. We insert Via header below so we will
				// get response.
				if (newRequest.getMethod().equals(Request.CANCEL)) {
					newRequest.removeHeader(ViaHeader.NAME);
					newRequest.removeHeader(RecordRouteHeader.NAME);
				} else {
					// 3. Max-Forwards
					decrementMaxForwards(newRequest);
					// 4. Record-Route
					addRecordRouteHeader(newRequest);
				}

				// 5. Add Additional Header Fields
				// TBD
				// 6. Postprocess routing information
				// TBD
				// 7. Determine Next-Hop Address, Port and Transport
				// TBD

				// 8. Add a Via header field value
				addViaHeader(newRequest);

				// 9. Add a Content-Leangth header field if necessary
				// TBD
	
				// 10. Forward Request
				
				ClientTransaction ctx=forwardRequest(stx, newRequest, forkedCTXList);
				
				// 11. Set timer C
				if(ctx!=null && req.getMethod().equals(Request.INVITE))
				{
		
						call.setCTimer(ctx, pc.getCTimerTimeOut());
				}
				
			}

		} catch (SipSendErrorResponseException se) {
			se.printStackTrace();
			int statusCode = se.getStatusCode();
			sendErrorResponse(stx, req, statusCode);
		}catch(SipLoopDetectedException slde)
		{
			log.warning("Loop detected, droping message.");
			slde.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void processResponse(ServerTransaction stx, ClientTransaction ctx,
			Response resp, List forkedCTXList, Map acumulatedResponses, Map localCancelByeTxMap) {

		// Now check if we really want to send it right away
		
		//log.entering(this.getClass().getName(), "processResponse");
		
		if(!localCancelByeTxMap.containsKey(ctx))
		{
		try {
			
			Response newResponse = (Response) resp.clone();

			// 16.7 Response Processing
			// 1. Find appropriate response context

			// 2. Update timer C for provisional responses
			if(stx.getRequest().getMethod().equals(Request.INVITE))
				if(100<resp.getStatusCode() && resp.getStatusCode()<200)
				{
					call.updateCTimer(ctx,config.getCTimerTimeOut());
				}else if(resp.getStatusCode()>=200)
				{//remove it if response is final
					call.cancelCTimer(ctx);	
				}
	
				
			// 3. Remove topmost via
			Iterator viaHeaderIt = newResponse.getHeaders(ViaHeader.NAME);
			viaHeaderIt.next();
			viaHeaderIt.remove();
			if (!viaHeaderIt.hasNext())
				return; // response was meant for this proxy

			// 4. Add the response to the response context
			acumulatedResponses.put(ctx, resp);
			// 5. Check to see if this response should be forwarded immediately
			if (newResponse.getStatusCode() == Response.TRYING) {
				return;
			}

			
			//EXAMPLE CODE for B2BUA
			//Lets increment count of final responses
			//if(199<newResponse.getStatusCode() && newResponse.getStatusCode()<300)
			//{
				//If its 2xx response we have to forward it immediatly - this will finish server stx
				//so we have to indicate that all branches are no good fo us
				//call.setFinishedBranches(acumulatedResponses.size());
				
			//}
			//Now we will forward imediatly 2xx and 100<x<200 responses, all other are waiting for their turn in response context
			//However if no active branches are present, this response is forwarded.



			//This place is a real PAIN
			
//			 6. When necessary, choose the best final response from the
			// response context
			//if(newResponse!=null)
			//{
				//1xx(101+) provisional response or 2xx
				//choosen=newResponse;
			//}else
			//{
				//We have to choose
				//101+ and 2xx are forwarded anyway so here we have to choose from 300+ responses and ignore 300-
			//	TreeSet responses=new TreeSet(new ResponseseComparator());
			//	responses.addAll(acumulatedResponses.values());
			//  ~choosen=responses.firstObject();
			//}
			// 7. Aggregate authorization header fields if necessary

			// 8. Optionally rewrite Record-Route header field values

			// 9. Forward the response
			forwardResponse(stx, newResponse);

			// 10. Generate any necessary CANCEL requests
			if(stx.getRequest().getMethod().equals(Request.INVITE))
				if(199<newResponse.getStatusCode() && newResponse.getStatusCode()<300) //THIS MIGHT BE WRONG
				{
					//WE HAVE TO CANCEL ALL OTHER TXs and CTimers
					call.cancelCTimer(ctx);
					forkedCTXList.remove(ctx);
					
					Iterator it=forkedCTXList.iterator();
					
					while(it.hasNext())
					{
						ClientTransaction ct=(ClientTransaction) it.next();
						
						if(ct.getState().getValue()!=TransactionState._COMPLETED && ct.getState().getValue()!=TransactionState._TERMINATED)
						{
						
							call.cancelCTimer(ct);
							//GENERATE CANCEL AND BYE
							doCancelAndByeTx(ct, localCancelByeTxMap);
						}
						
					}
					
					
				}
			
				

		} catch (Exception e) {
			e.printStackTrace();
		}
		}else
		{
			
			//If it did contain we got response for our local CANCEL or BYE on ctx for INVITE request
			//which was not succesful - we had to CANCEL or BYE it
			if(resp.getStatusCode()==487)
			{
				//Other side won the race, and before our CANCEL hit it, it has created dialog
				//We have to send BYE
				//?
			}
			
		}
	}

	
	
	
	protected void doCancelAndByeTx(ClientTransaction ctx, Map localCancelByeTxMap)
	{
		
		try {

			Request cancel=ctx.createCancel();
			localCancelByeTxMap.put(call.sendRequest(cancel, true), ctx);
			// For BYE we need:
			// * original: CallID,From, To,RequestURI
			// * CSeq +1 with BYE as method
			FromHeader from=(FromHeader) cancel.getHeader(FromHeader.NAME);
			ToHeader to=(ToHeader) cancel.getHeader(ToHeader.NAME);
			CallIdHeader callID= (CallIdHeader) ctx.getRequest().getHeader(CallIdHeader.NAME);
			CSeqHeader o_cseq=(CSeqHeader) ctx.getRequest().getHeader(CSeqHeader.NAME);
			CSeqHeader cseq=this.hf.createCSeqHeader(o_cseq.getSeqNumber()+1, Request.BYE);
			ViaHeader via=(ViaHeader) cancel.getHeader(ViaHeader.NAME);
			ViaHeader viaClone=(ViaHeader) via.clone();
			MaxForwardsHeader maxF=this.hf.createMaxForwardsHeader(70);
			
			//Now lets change branch
			//as BYE needs it own tx
			viaClone.setBranch("z9hG4bK"+System.currentTimeMillis()+"_"+Math.random()+"_"+System.currentTimeMillis());
			ArrayList vias=new ArrayList();
			vias.add(viaClone);
			
			Request bye=this.mf.createRequest(cancel.getRequestURI(),Request.BYE, callID,cseq,from,to,vias,maxF);
			
			log.finer("==============================================");
			log.finer("CANCEL:\n"+cancel);
			log.finer("==============================================");
			log.finer("BYE:\n"+bye);
			log.finer("==============================================");
			
			ClientTransaction byeCtx=this.provider.getNewClientTransaction(bye);
			byeCtx.sendRequest();
			localCancelByeTxMap.put(byeCtx, ctx);
			
		} catch (SipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public ClientTransaction forwardRequest(ServerTransaction serverTransaction,
			Request request, List forkedCTX) {
		ClientTransaction toReturn=null;
		log.info("Forwarding request "+request.getMethod()+" of server tx "+serverTransaction.getBranchId());
		try {
			
			if (request.getMethod().equals(Request.ACK)) {

				call.sendStatelessRequest(request);

			} else if (request.getMethod().equals(Request.CANCEL)) {

				call.sendRequest(request, false);
			} else {

				ClientTransaction clientTransaction = call.sendRequest(request,
						true);
				
				if (!forkedCTX.contains(clientTransaction))
				{
					log.finest("[$$$] addingTxToForkedCTX:\n------------------------------------------------------\n"+clientTransaction+"\n----------------------------------");
					forkedCTX.add(clientTransaction);
				}
				return clientTransaction;
			}

		} catch (Exception e) {
			//log.info( "Exception during forwardRequest-->"+ e);
			e.printStackTrace();
			if (!serverTransaction.getRequest().getMethod().endsWith(Request.CANCEL)) {
				// send back error if it's nt a cancel, because that one already got a 200 ok
				sendErrorResponse(serverTransaction, serverTransaction.getRequest(),
						Response.SERVER_INTERNAL_ERROR);
			}
		}
		return toReturn;
	}

	public void sendErrorResponse(ServerTransaction txn, Request request,
			int statusCode) {
		try {
			// trace(Level.FINE, "Ending transaction - subsequent messages will
			// be ignored");
			// sipaci.setTransactionTerminated(true);
			call.endCallProcessing();
			Response response = mf.createResponse(statusCode, request);
			if (response.getHeader(MaxForwardsHeader.NAME) == null) {
				response.addHeader(hf.createMaxForwardsHeader(69));
			}
			txn.sendResponse(response);
		} catch (Exception e) {
			// trace(Level.WARNING, "Exception during sendErrorResponse", e);
			e.printStackTrace();
		}
	}

	public void forwardResponse(ServerTransaction txn, Response response) {
		log.info("Forwarding response "+response.getStatusCode()+" of server tx "+txn.getBranchId());
		try {
			// trace(Level.FINEST, "Forwarding response:\n" + response);
			if (txn != null) {
				txn.sendResponse(response);
			} else {
				// forward statelessly anyway
				call.sendStatelessResponse(response);
			}
		} catch (Exception e) {
			 log.severe("Exception during forwardResponse:"+ e);
		}
	}

	/**
	 * Performs request validation as per RFC 3261 section 16.3. If a request
	 * fails validation, throw exception to cause appropriate error response to
	 * client.
	 * 
	 * @param txn
	 *            the server transaction of the request.
	 * @param request
	 *            the SIP request to be validated.
	 * @throws SipLoopDetectedException - error that is beeing thrown when local stack goes into loop
	 * @throws SipSendErrorResponseException - thrown uri of passed message is not supported
	 */
	public void validateRequest(ServerTransaction txn, Request request)
			throws SipSendErrorResponseException, SipLoopDetectedException {
		// 1. Reasonable syntax

		// 2. URI scheme
		URI requestURI = null;
		requestURI = request.getRequestURI();

		boolean supportedURIScheme = false;
		supportedURIScheme = isSupportedURIScheme(requestURI);

		if (!supportedURIScheme) {
			throw new SipSendErrorResponseException("Unsupported URI scheme",
					Response.UNSUPPORTED_URI_SCHEME);
		}

		// 3. Max-Forwards
		checkMaxForwards(txn, request);

		// 4. Loop Detection - TBD, now its simple, so we wont spin requests to local node
		SipUri localNodeURI=new SipUri();
		try {
			localNodeURI.setHost(this.config.getSipHostname());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		localNodeURI.setPort(this.config.getSipPort());
		if(requestURI.equals(localNodeURI))
		{
			//throw new SipSendErrorResponseException("Possible local looping on node",Response.LOOP_DETECTED);
			throw new SipLoopDetectedException("Possible loop detected on message:n"+request+"\n====================================");
		}
		// 5. Proxy-Require - TBD
		// 6. Proxy-Authorization - TBD
	}

	/**
	 * Validate the max-forwards header throw a user error exception (too many
	 * hops) if max-forwards reaches 0.
	 * 
	 * @param txn
	 * @param request
	 * @throws SipSendErrorResponseException
	 */
	public void checkMaxForwards(ServerTransaction txn, Request request)
			throws SipSendErrorResponseException {
		MaxForwardsHeader mfh = (MaxForwardsHeader) request
				.getHeader(MaxForwardsHeader.NAME);
		if (mfh == null)
			return;

		int maxForwards = 0;
		maxForwards = ((MaxForwardsHeader) request
				.getHeader(MaxForwardsHeader.NAME)).getMaxForwards();

		if (maxForwards > 0) {
			return;
		} else {
			// MAY respond to OPTIONS, otherwise return 483 Too Many Hops
			throw new SipSendErrorResponseException("Too many hops",
					Response.TOO_MANY_HOPS);
		}

	}

	/**
	 * Attempts to find a locally registered contact address for the given URI,
	 * using the location service interface.
	 */
	public LinkedList findLocalTarget(URI uri) throws SipSendErrorResponseException {
		String addressOfRecord = uri.toString();

		Map bindings = null;
		LinkedList listOfTargets=new LinkedList();
		bindings = reg.getBindings(addressOfRecord);

		if (bindings == null) {
			throw new SipSendErrorResponseException("User not found",
					Response.NOT_FOUND);
		}
		if (bindings.isEmpty()) {
			throw new SipSendErrorResponseException(
					"User temporarily unavailable",
					Response.TEMPORARILY_UNAVAILABLE);
		}

		Iterator it = bindings.values().iterator();
		URI target = null;
		while (it.hasNext()) {
			RegistrationBinding binding = (RegistrationBinding) it.next();
			// logger.fine("BINDINGS: " + binding);
			ContactHeader header = binding.getContactHeader(af, hf);
			// logger.fine("CONTACT HEADER: " + header);
			if (header == null) { // entry expired
				continue; // see if there are any more contacts...
			}
			Address na = header.getAddress();
			// logger.fine("Address: " + na);
			//target = na.getURI();
			//break;
			listOfTargets.add(na.getURI());
			
		}
		if (listOfTargets.size()== 0) {
			// logger.fine("findLocalTarget: No contacts for "
			// + addressOfRecord + " found.");
			throw new SipSendErrorResponseException(
					"User temporarily unavailable",
					Response.TEMPORARILY_UNAVAILABLE);
		}
		return listOfTargets;
	}

	/**
	 * Adds a default Via header to the request. Override to provide a different
	 * Via header.
	 */
	public void addViaHeader(Request request) {
		try {
			//ViaHeader via = hf.createViaHeader(config.getSipHostname(), config.getSipPort(), config.getSipTransport(), null);
			ViaHeader via = null;
			if(request.getMethod().equals(Request.CANCEL) || request.getMethod().equals(Request.ACK)) {
				via = call.getForwardedInviteViaHeader();
			}
			else {
				via = hf.createViaHeader(config.getSipHostname(), config.getSipPort(), config.getSipTransports()[0], "z9hG4bK"+System.currentTimeMillis()+"_"+Math.random()+"_"+System.currentTimeMillis());
				if(request.getMethod().equals(Request.INVITE)) {
					call.setForwardedInviteViaHeader(via);
				}
			}
			
			//THIS: config.getSipTransports()[0] // has to be changed!!!
			log.finer("[&&&] addViaHeader\n"+via+"");
			//via.setParameter("ID", ""+System.currentTimeMillis()+"_"+Math.random()+"_"+config.getSipHostname()+":"+config.getSipPort());
			request.addHeader(via);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds a default Record-Route header to the request. Override to provide a
	 * different Record-Route header.
	 */
	public void addRecordRouteHeader(Request request) {
		try {
			// Add our record-route header to list...
			SipURI myURI = af.createSipURI(null, config.getSipHostname());
			myURI.setPort(config.getSipPort());
			myURI.setMAddrParam(config.getSipHostname());
			myURI.setTransportParam(config.getSipTransports()[0]);
			myURI.setParameter("cluster", "mobi-cents");
			Address myName = af.createAddress(myURI);

			RecordRouteHeader myHeader = hf.createRecordRouteHeader(myName);
			request.addHeader(myHeader);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Decrement the value of max-forwards. If no max-forwards header present,
	 * create a max-forwards header with the RFC3261 recommended default value
	 * 
	 * @param request
	 * @throws SipSendErrorResponseException
	 */
	public void decrementMaxForwards(Request request)
			throws SipSendErrorResponseException {
		MaxForwardsHeader max = (MaxForwardsHeader) request
				.getHeader(MaxForwardsHeader.NAME);
		try {
			if (max == null) {
				// add max-forwards with default 70 hops
				max = hf.createMaxForwardsHeader(70);
				request.setHeader(max);
			} else {
				// decrement max-forwards
				int maxForwards = max.getMaxForwards();
				maxForwards--;
				max.setMaxForwards(maxForwards);
				request.setHeader(max);
			}
		} catch (Exception e) {
			throw new SipSendErrorResponseException(
					"Error updating max-forwards",
					Response.SERVER_INTERNAL_ERROR);
		}
	}

	/**
	 * Check for strict-routing style route headers and swap with Request-URI if
	 * applicable.
	 */
	public void routePreProcess(Request request)
			throws SipSendErrorResponseException {
		URI requestURI = null;
		requestURI = request.getRequestURI();

		if ((requestURI.isSipURI())
				&& (((SipURI) requestURI).getUser() == null)
				&& (((SipURI) requestURI).getHost().equalsIgnoreCase(config
						.getSipHostname()))) {
			// client is a strict router, replace request-URI with last
			// value in Route header field...
			try {
				ListIterator it = request.getHeaders(RouteHeader.NAME);
				LinkedList l = new LinkedList();
				// need last value in list
				while (it.hasNext()) {
					RouteHeader r = (RouteHeader) it.next();
					l.add(r);
				}
				if (l.size() == 0)
					return;

				RouteHeader route = (RouteHeader) l.getLast();

				l.removeLast(); // Remove the last route header from the list,
				// possibly leaving an empty list

				request.removeHeader(RouteHeader.NAME); // Remove all route
				// headers from the
				// message

				// Re-add the remaining headers to the message
				for (int i = 0; i < l.size(); i++) {
					RouteHeader routeHeader = (RouteHeader) l.get(i);
					request.addHeader(routeHeader);
				}

				URI newURI = route.getAddress().getURI();
				request.setRequestURI(newURI);

			} catch (Exception e) {
				e.printStackTrace();
				throw new SipSendErrorResponseException(
						"Error updating route headers",
						Response.SERVER_INTERNAL_ERROR);

			}
		}
		// From RFC3261 16.4:
		// If the first value in the Route header field indicates this proxy,
		// the proxy MUST remove that value from the request.
		Iterator routeHeaders = request.getHeaders(RouteHeader.NAME);
		if (routeHeaders.hasNext()) {
			RouteHeader r = (RouteHeader) routeHeaders.next();
			// is this route header for our hostname & port?
			URI uri = r.getAddress().getURI();
			if (uri.isSipURI()) {
				SipURI sipURI = (SipURI) uri;
				int uriPort = sipURI.getPort();
				if (uriPort <= 0)
					uriPort = 5060; // WARNING: Assumes stack impl returns <= 0
				// if port not specified in URI
				String cluster = sipURI.getParameter("cluster");

				boolean isMobicents = (cluster != null && cluster
						.equals("mobi-cents"));

				// JAIN SIP does not specify but NIST SIP returns -1
				if (((sipURI.getHost()
						.equalsIgnoreCase(config.getSipHostname())) && (uriPort == config
						.getSipPort()))
						|| isMobicents) {
					// logger.fine("Cluster = " + cluster);
					// remove this route header
					routeHeaders.remove();
					// if this was the last one, remove the header entirely (why
					// isn't this automatic?)
					if (!routeHeaders.hasNext())
						request.removeHeader(RouteHeader.NAME);
				}
			}
		}

	}

	/**
	 * Determines target SIP URI(s) for request, using location service or other
	 * criteria.
	 * 
	 * TODO: Forking (return more than one target)
	 * 
	 * @param request
	 *            the SIP request being forwarded
	 * @return a list of URIs
	 */
	public List determineRequestTargets(Request request)
			throws SipSendErrorResponseException {
		LinkedList targets = new LinkedList();

		URI requestURI = null;
		URI target = null;
		boolean localDomain = false;
		requestURI = request.getRequestURI();
		localDomain = isLocalDomain(requestURI);
		
		if (request.getMethod().equals(Request.ACK)
				|| request.getMethod().equals(Request.BYE)) {
			RouteHeader rh = (RouteHeader) request.getHeader(RouteHeader.NAME);
			if (rh != null) {
				target = rh.getAddress().getURI();
			} else
				target = request.getRequestURI();
			
			targets.add(target);
		} else if (localDomain) {
			// determine local SIP target(s) using location service etc
			targets = findLocalTarget(requestURI);
			//This is done in findLocalTarget
			//if (target == null) { // not found (or not currently registered)
			//	throw new SipSendErrorResponseException("User not registered",
			//			Response.TEMPORARILY_UNAVAILABLE);
			//}
		} else {
			// destination addr is outside our domain
			target = requestURI;
			targets.add(target);
		}
		
		return targets;
	}

	public boolean isLocalMachine(URI hostURI)
	{
		
		return this.localMachineInterfaces.contains(hostURI);
		
	}
	
	//This is possbile implementation of class that sorts responses based on their code - its done this way that firs one  after sorting should
	//be choosen to be sent.
	protected class ResponseComparator implements Comparator
	{

		public int compare(Object arg0, Object arg1) {
			Response resp1, resp2;
			resp1=(Response) arg0;
			resp2=(Response) arg1;
			
			int code1,code2;
			code1=resp1.getStatusCode();
			code2=resp2.getStatusCode();
			
			if(code1==code2)
			{
				
				String firstAddess=((ToHeader)resp1.getHeader(ToHeader.NAME)).getAddress().getURI().toString();
				String secondAddress=((ToHeader)resp2.getHeader(ToHeader.NAME)).getAddress().getURI().toString();
				return firstAddess.compareTo(secondAddress);
			}else if(code1<300)
			{
				code1=+1000;
				//so those responses end up way above other ones
				
			}
			
			return code1-code2;
		}
		
	}
	
}
