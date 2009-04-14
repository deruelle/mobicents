package org.mobicents.jain.protocol.ip.mgcp.pkg;

import jain.protocol.ip.mgcp.pkg.MgcpEvent;

/**
 * 
 * @author amit bhayani
 *
 */
public class AUMgcpEvent {
	
	
	/* _______________________________________________________________________ 
	 * | Symbol       |   Definition           |  R   |   S       Duration   | 
	 * |______________|________________________|______|______________________|  
	 * | pa(parms)    |   PlayAnnouncement     |      |   TO      variable   | 
     * | pc(parms)    |   PlayCollect          |      |   TO      variable   | 
     * | pr(parms)    |   PlayRecord           |      |   TO      variable   | 
     * | es(parm)     |   EndSignal            |      |   BR                 | 
     * | oc(parms)    |   OperationComplete    |  x   |                      | 
     * | of(parms)    |   OperationFailed      |  x   |                      | 
     * |______________|________________________|______|______________________| 
	 */
	
	
	 public static final int PLAY_ANNOUNCEMENT = 200;
	 public static final MgcpEvent aupa = MgcpEvent.factory("aupa",PLAY_ANNOUNCEMENT);
	 
	 
	 public static final int PLAY_COLLECT = 201;
	 public static final MgcpEvent aupc = MgcpEvent.factory("aupc", PLAY_COLLECT);
	 

	 public static final int PLAY_RECORD = 202;
	 public static final MgcpEvent aupr = MgcpEvent.factory("aupr",PLAY_RECORD);
	 
	 public static final int END_SIGNAL = 203;
	 public static final MgcpEvent aues = MgcpEvent.factory("aues",END_SIGNAL);
	 
	 public static final int OPERATION_COMPLETE = 204;
	 public static final MgcpEvent auoc = MgcpEvent.factory("auoc", OPERATION_COMPLETE);
	 
	 public static final int OPERATION_FAIL = 205;
	 public static final MgcpEvent auof = MgcpEvent.factory("auof", OPERATION_FAIL);
	 
	 
	public static void main(String args[]){
		System.out.println("rfc2897pa = "+ aupa.toString());
		System.out.println("PLAY_ANNOUNCEMENT = "+ PLAY_ANNOUNCEMENT);
		
		
		System.out.println("rfc2897pc = "+ aupc.toString());
		System.out.println("PLAY_COLLECT = "+ PLAY_COLLECT);
		
		
		System.out.println("rfc2897of = "+ auof.toString());
		System.out.println("OPERATION_FAIL = "+ OPERATION_FAIL);
		
		
	}
}
