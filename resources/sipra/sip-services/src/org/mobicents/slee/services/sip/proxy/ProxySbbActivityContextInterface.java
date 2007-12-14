package org.mobicents.slee.services.sip.proxy;

import javax.sip.ClientTransaction;
import javax.slee.ActivityContextInterface;
import javax.slee.facilities.TimerID;

public interface ProxySbbActivityContextInterface extends
		ActivityContextInterface {

	
	/**
     * This flag tells the SBB that the transaction has been terminated, and that
     * any further messages on this transaction (eg. ACKs after the proxy returns
     * 404 NOT_FOUND) should be ignored and not forwarded.
     */
    public boolean getTransactionTerminated();
    public void setTransactionTerminated(boolean transactionTerminated);
    
    
    /**
	 * This method is ment for alliasing purposes of service chaining. Should
	 * return true if some other service has handled sip call.
	 * 
	 * @return
	 * <li><b>true</b> - if this call has been handled by service lower in
	 * chain.
	 * <li><b>false</b> - otheriwse
	 */
	public boolean getHandledByAncestor();

	/**
	 * This method is ment for alliasing purposes of service chaining. If this
	 * call has been handled by service lower in chain (
	 * {@link #getHandledByAncestor()} returned <b>true</b> or this call is
	 * beeing handled by proxy, paramter should be <b>true</b>. Otherwise it
	 * should be <b>false</b>.
	 * 
	 * @param handled
	 */
	public void setHandledByMe(boolean handled);
	// if someone needs it.
	public boolean getHandledByMe();
	
	/**
	 * Sets ClientTransaction for which this aci is Ctimer aci.
	 * @param ctx
	 */
	public void setCTimedTransaction(ClientTransaction ctx);
	public ClientTransaction getCTimedTransaction();
	
	public void setCTimerTID(TimerID tid);
	public TimerID getCTimerTID();
}
