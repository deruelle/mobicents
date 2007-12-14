package org.mobicents.slee.services.sip.registrar;

import javax.slee.ActivityContextInterface;

/**
 * 
 * RegistrarActivityContextInterfaceProvided by the Sbb Developer
 * 
 * @author F.Moggia
 */
public interface RegistrarActivityContextInterface extends ActivityContextInterface{
    /** sipAddress - User's public, well-known SIP address */
    public String getSipAddress();
    public void setSipAddress(String sipAddress);

    /** sipContactAddress - Physical network address registered for above sipAddress */
    public String getSipContactAddress();
    public void setSipContactAddress(String sipContactAddress);

    /** callId - SIP callId that was used in the REGISTER request */
    public String getCallId();
    public void setCallId(String callId);

    /** cSeq - SIP sequence number that was used in the REGISTER request */
    public long getCSeq();
    public void setCSeq(long cSeq);
}
