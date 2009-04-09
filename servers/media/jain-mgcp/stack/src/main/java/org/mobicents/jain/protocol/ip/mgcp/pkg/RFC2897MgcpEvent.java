package org.mobicents.jain.protocol.ip.mgcp.pkg;

import jain.protocol.ip.mgcp.pkg.MgcpEvent;

/**
 * 
 * @author amit bhayani
 *
 */
public class RFC2897MgcpEvent {
	
	
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
	 public static final MgcpEvent rfc2897pa = MgcpEvent.factory("rfc2897pa",PLAY_ANNOUNCEMENT);
	 
	 
	 public static final int PLAY_COLLECT = 201;
	 public static final MgcpEvent rfc2897pc = MgcpEvent.factory("rfc2897pc", PLAY_COLLECT);
	 

	 public static final int PLAY_RECORD = 202;
	 public static final MgcpEvent rfc2897pr = MgcpEvent.factory("rfc2897pr",PLAY_RECORD);
	 
	 public static final int END_SIGNAL = 203;
	 public static final MgcpEvent rfc2897es = MgcpEvent.factory("rfc2897es",END_SIGNAL);
	 
	 public static final int OPERATION_COMPLETE = 204;
	 public static final MgcpEvent rfc2897oc = MgcpEvent.factory("rfc2897oc", OPERATION_COMPLETE);
	 
	 public static final int OPERATION_FAIL = 205;
	 public static final MgcpEvent rfc2897of = MgcpEvent.factory("rfc2897of", OPERATION_FAIL);
	 
	 
	public static void main(String args[]){
		System.out.println("rfc2897pa = "+ rfc2897pa.toString());
		System.out.println("PLAY_ANNOUNCEMENT = "+ PLAY_ANNOUNCEMENT);
		
		
		System.out.println("rfc2897pc = "+ rfc2897pc.toString());
		System.out.println("PLAY_COLLECT = "+ PLAY_COLLECT);
		
		
		System.out.println("rfc2897of = "+ rfc2897of.toString());
		System.out.println("OPERATION_FAIL = "+ OPERATION_FAIL);
		
		
	}
}
