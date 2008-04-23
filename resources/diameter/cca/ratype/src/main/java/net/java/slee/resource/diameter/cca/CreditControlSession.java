package net.java.slee.resource.diameter.cca;

import net.java.slee.resource.diameter.base.events.ReAuthAnswer;
import net.java.slee.resource.diameter.cca.events.CreditControlRequest;

public interface CreditControlSession {

	
	/**
	 * Provides session state information. CC session must conform to CC FSM as described in <a href="link http://rfc.net/rfc4006.html#s7">section 7 of rfc4006</a>
	 * 
	 * @return instance of {@link CreditControlSessionState}
	 */
	public CreditControlSessionState getState();

	/**
	 * Unique session-id for this session
	 * @return
	 */
	public String getSessionId();

	public void sendCreditControlRequest(CreditControlRequest ccr);

	public void sendReAuthAnswer(ReAuthAnswer raa);
}
