package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.SessionTerminationRequest;
import net.java.slee.resource.diameter.base.events.avp.TerminationCauseType;

import org.jdiameter.api.Avp;
import org.jdiameter.api.Message;

public class SessionTerminationRequestImpl  extends SessionTerminationAnswerImpl implements SessionTerminationRequest{

  public SessionTerminationRequestImpl(Message message) {
    super(message);
  }
  
	@Override
	public String getLongName() {
		
		return "Session-Termination-Request";
	}

	@Override
	public String getShortName() {

		return "STR";
	}

	public boolean hasTerminationCause() {
	  return message.getAvps().getAvp(Avp.TERMINATION_CAUSE) != null;
	}

	public TerminationCauseType getTerminationCause() {
	  return TerminationCauseType.fromInt(getAvpAsInt32(Avp.TERMINATION_CAUSE));
	}

	public void setTerminationCause(TerminationCauseType terminationCause) {
	  setAvpAsInt32(Avp.TERMINATION_CAUSE, terminationCause.getValue(), true);
	}

}
