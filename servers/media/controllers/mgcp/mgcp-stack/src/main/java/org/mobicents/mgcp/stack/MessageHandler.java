/*
 * File Name     : MessageHandler.java
 *
 * The JAIN MGCP API implementaion.
 *
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */
package org.mobicents.mgcp.stack;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * 
 * @author Oleg Kulikov
 */
public class MessageHandler {

	private JainMgcpStackImpl stack;
	private static Logger logger = Logger.getLogger(MessageHandler.class);
	
	private static final Pattern p = Pattern.compile("[\\w]{4}(\\s|\\S)*");

	/** Creates a new instance of MessageHandler */
	

	public MessageHandler(JainMgcpStackImpl jainMgcpStackImpl) {
		
		this.stack = jainMgcpStackImpl;
		
		
	}

	/**
	 * RFC 3435, $3.5.5: split piggy backed messages again
	 * <P>
	 * Messages within the packet are split on their separator "EOL DOT EOL".
	 * 
	 * @param packet
	 *            the packet to split
	 * @return array of all separate messages
	 */
	public static String[] piggyDismount(String packet) {
		final String pb = "\r?\n\\.\r?\n";
		final Pattern p = Pattern.compile(pb);
		int idx = 0;
		ArrayList<String> mList = new ArrayList<String>();

		Matcher m = p.matcher(packet);
		while (m.find()) {
			mList.add(packet.substring(idx, m.start()) + "\n");
			idx = m.end();
		}
		mList.add(packet.substring(idx));
		String[] result = new String[mList.size()];
		return (String[]) mList.toArray(result);
	}

	public boolean isRequest(String header) {
		Matcher m = p.matcher(header);
		return m.matches();
		// return header.matches("[\\w]{4}(\\s|\\S)*");
	}

	

	public void scheduleMessages(PacketRepresentation pr) {
		
		
		final InetAddress address=pr.getRemoteAddress();
		final int port=pr.getRemotePort();
		for (String msg : piggyDismount(new String(pr.getRawData()))) {

			int pos = msg.indexOf("\n");
			//System.out.println(" --- RECEIVING:"+msg);
			// extract message header to determine transaction handle parameter
			// and type of the received message
			String header = msg.substring(0, pos).trim();
			if (logger.isDebugEnabled()) {
				logger.debug("Message header: " + header);
			}

			// check message type
			// if this message is command then create new transaction handler
			// for specified type of this message.
			// if received message is a response then try to find corresponded
			// transaction to handle this message
			String tokens[] = header.split("\\s");
			if (isRequest(header)) {

				String verb = tokens[0];
				String remoteTxIdString = tokens[1];

				if (logger.isDebugEnabled()) {
					logger.debug("Processing command message = " + verb + " remote Tx = " + remoteTxIdString);
				}

				Integer remoteTxIdIntegere = new Integer(remoteTxIdString);
				
				//Check if the Response still in responseTx Map
				//FIXME: baranowb: this checks seems bad, why do we itterate and than get value for key?
				Set<Integer> completedTxSet = stack.getCompletedTransactions().keySet();
				for(Integer completedTx : completedTxSet){
					if( completedTx.equals(remoteTxIdIntegere)){
						if (logger.isDebugEnabled()) {
							logger.debug("Received Command for which stack has already sent response Tx = "+completedTx );
						}
						TransactionHandler completedTxHandler = stack.getCompletedTransactions().get(completedTx);
						
						EndpointHandler eh=completedTxHandler.getEndpointHandler();
						completedTxHandler.markRetransmision();
						eh.scheduleTransactionHandler(completedTxHandler);
						
						//this.stack.jainMgcpStackImplPool.execute(completedTxHandler);
						//(new Thread(completedTxHandler)).start();
						return;
					}
				}
				
				//TransactionHandler completedTxHandler =stack.getCompletedTransaction(remoteTxIdIntegere);
				//if(completedTxHandler!=null)
				//{
				//	EndpointHandler eh=completedTxHandler.getEndpointHandler();
				//	completedTxHandler.markRetransmision();
				//	eh.scheduleTransactionHandler(completedTxHandler);
				//}
				//TODO:
				//Check if Tx is currently executing then send provisional response
				//FIXME: I wonder if it should be moved? Maybe we should always send provisional response upon receival?
				//FIXME: baranowb: this checks seems bad, why do we itterate and than get value for key?
				Set<Integer> ongoingTxSet = stack.getRemoteTxToLocalTxMap().keySet();
				for(Integer ongoingTx : ongoingTxSet){
					if( ongoingTx.equals(remoteTxIdIntegere)){
						if (logger.isDebugEnabled()) {
							logger.debug("Received Command for ongoing Tx = "+ongoingTx );							
						}
						Integer tmpLoaclTID = stack.getRemoteTxToLocalTxMap().get(ongoingTx);
						TransactionHandler ongoingTxHandler = stack.getLocalTransactions().get(tmpLoaclTID);
						ongoingTxHandler.sendProvisionalResponse();
						return;
					}
				}

				//Integer tmpLoaclTID = stack.getLocalTXIDFromRemoteTXID(remoteTxIdIntegere);
				//if(tmpLoaclTID!=null)
				//{
				//	TransactionHandler ongoingTxHandler = stack.getLocalTransaction(tmpLoaclTID);
				//	ongoingTxHandler.sendProvisionalResponse();
				//}
				//If we are here, it means this is new TX, we have to create TxH and EH
				
				
				TransactionHandler handler;
				if (verb.equalsIgnoreCase("crcx")) {
					handler = new CreateConnectionHandler(stack, address, port);
				} else if (verb.equalsIgnoreCase("mdcx")) {
					handler = new ModifyConnectionHandler(stack, address, port);
				} else if (verb.equalsIgnoreCase("dlcx")) {
					handler = new DeleteConnectionHandler(stack, address, port);
				} else if (verb.equalsIgnoreCase("epcf")) {
					handler = new EndpointConfigurationHandler(stack, address, port);
				} else if (verb.equalsIgnoreCase("rqnt")) {
					handler = new NotificationRequestHandler(stack, address, port);
				} else if (verb.equalsIgnoreCase("ntfy")) {
					handler = new NotifyHandler(stack, address, port);
				} else if (verb.equalsIgnoreCase("rsip")) {
					handler = new RestartInProgressHandler(stack, address, port);	
				} else if (verb.equalsIgnoreCase("auep")) {
					handler = new AuditEndpointHandler(stack, address, port);
				} else if (verb.equalsIgnoreCase("aucx")) {
					handler = new AuditConnectionHandler(stack, address, port);
				} else {
					logger.warn("Unsupported message verbose " + verb);
					return;
				}
				
				//This makes this command to be set in queue to process
				handler.receiveRequest(msg);
				boolean useFakeOnWildcard=false;
				if(handler instanceof CreateConnectionHandler)
				{
					useFakeOnWildcard=EndpointHandler.isAnyOfWildcard(handler.getEndpointId());
				}
				EndpointHandler eh=stack.getEndpointHandler(handler.getEndpointId(),useFakeOnWildcard);
				eh.addTransactionHandler(handler);
				
				eh.scheduleTransactionHandler(handler);
				//handle.receiveCommand(msg);
			} else {
				// RESPONSE HANDLING
				if (logger.isDebugEnabled()) {
					logger.debug("Processing response message");
				}
				// String domainName = address.getHostName();
				String tid = tokens[1];
				
				//XXX:TransactionHandler handler = (TransactionHandler) stack.getLocalTransaction(Integer.valueOf(tid));
				TransactionHandler handler = (TransactionHandler) stack.getLocalTransactions().get(Integer.valueOf(tid));
				if (handler == null) {
					logger.warn("---  Address:"+address+"\nPort:"+port+"\nID:"+this.hashCode()+"\n Unknown transaction: " + tid);
					return;
				}
				handler.receiveResponse(msg);
				//EndpointHandler eh=stack.getEndpointHandler(handler.getEndpointId());
				EndpointHandler eh=handler.getEndpointHandler();
				eh.scheduleTransactionHandler(handler);
				

			}
		}
		
		
	}
}
