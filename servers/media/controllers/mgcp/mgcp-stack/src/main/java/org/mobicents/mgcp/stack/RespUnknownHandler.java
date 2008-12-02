package org.mobicents.mgcp.stack;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;

import java.text.ParseException;




public class RespUnknownHandler extends TransactionHandler {

	public RespUnknownHandler(JainMgcpStackImpl stack) {
		super(stack);
	}
	
	@Override
	public JainMgcpCommandEvent decodeCommand(String message)
			throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JainMgcpResponseEvent decodeResponse(String message)
			throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encode(JainMgcpCommandEvent event) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encode(JainMgcpResponseEvent event) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JainMgcpResponseEvent getProvisionalResponse() {
		// TODO Auto-generated method stub
		return null;
	}

}
