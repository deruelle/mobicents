/** Java interface "Manager.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package com.ptin.xmas.control.common.interfaces;

import javax.sip.ServerTransaction;
import javax.sip.SipProvider;
import javax.sip.message.Request;




/**
 * <p>
 * 
 * @author  xest188
 * </p>
 * </p>
 */
public interface CallManager {

  ///////////////////////////////////////
  // operations

	/**
	 * <p>
	 * Method that is called by CommProvider to receive an incomming Invite and to process it.
	 * </p>
	 * <p>
	 * It generates a new InCall object that runs on a new Thread.
	 * </p>
	 * </p>
	 */
	    public void processInvite(final Request inviteRequest,final ServerTransaction serverTransaction) throws Exception;
	    public void processInvite(final CallController incom) throws Exception;
	
/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * @param callid ...
 * </p>
 */
    public void removeCall(String callid) throws Exception;
/**
 * <p>
 * Function that returns an InCallController object with the respective callId.
 * </p>
 * </p>
 */
    public InCallController getCall(String callid) throws Exception;
/**
 * <p>
 * Function that returns an InCallController object that contains a from field
 * </p>
 * <p>
 * equal to the given parameter String from.
 * </p>
 * </p>
 */
    public InCallController getCallFrom(String from) throws Exception;
/**
 * <p>
 * Function that returns commProvider... no longer used because CommProvider can
 * </p>
 * <p>
 * be referenced from the static function CommProviderImpl.getReference()
 * </p>
 * </p>
 */
    public CommProvider getProvider();

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a CallController with ...
 * </p><p>
 * @param from ...
 * </p><p>
 * @param to ...
 * </p><p>
 * @param controlLevel ...
 * </p>
 */
    public CallController startCall(String uid, String onwner, CallControllerListener listener, final String from, final String fromName, final String to, int controlLevel, int flow);
    

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * @param newAppName ...
 * </p><p>
 * @param newAppPassword ...
 * </p><p>
 * @param newAppListener ...
 * </p>
 */
    //public void processRegistering(String newAppName, String newAppPassword, InComListener newAppListener);
    
    //public void processUnRegistering(String appName);

/**
 * <p>
 * Method used to stop all SIP Services.
 * </p>
 * </p>
 */
    public void stop();
/**
 * <p>
 * Method used to start all SIP Services.
 * </p>
 * </p>
 */
    public void start();
/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p><p>
 * @param in ...
 * </p>
 */
    public String whoIsTalkingTo(String in);
/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * @param user1 ...
 * </p><p>
 * @param user2 ...
 * </p>
 */
    public void addTalk(String user1, String user2);
/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * @param user1 ...
 * </p><p>
 * @param user2 ...
 * </p>
 */
    public void removeTalk(String user1, String user2);
/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * @param eventUser ...
 * </p>
 */
    public void setCallEventUser(String eventUser);
/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * @param listener ...
 * </p>
 */
    //public void setInComListener(InComListener listener);

    public CallController setCallModel(CallController currentCallController, String callModel, Leg leg);
    
    /**
     * <p>
     * Function that returns and URI of the type:
     * </p>
     * <p>
     * 2132@inocrea.org
     * </p>
     * <p>
     * ATTENTION!: it mantains the domain existant domain. If the domain doesn't
     * </p>
     * <p>
     * exist, it uses the proxy domain!
     * </p>
     * <p>
     * entry may be:
     * </p>
     * <p>
     * sip:2132@inocrea.org:5060
     * </p>
     * <p>
     * 2132@inocrea.org
     * </p>
     * <p>
     * 2132@inocrea.org:5060
     * </p>
     * <p>
     * sip:2132
     * </p>
     * <p>
     * 2132
     * </p>
     * </p>
     */
    public String getURI(String in);
    
    public SipProvider getSipProvider();
    public void setSipProvider(SipProvider provider);
} // end Manager







