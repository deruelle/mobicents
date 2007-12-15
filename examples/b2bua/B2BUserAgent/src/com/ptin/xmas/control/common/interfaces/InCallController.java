/** Java interface "InCallController.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package com.ptin.xmas.control.common.interfaces;


/**
 * <p>
 * Interface used by InCallWebController, it is implemented by Comm3pcc and InCall.
 * </p>
 * </p>
 */
public interface InCallController {

  ///////////////////////////////////////
  //attributes


/**
 * <p>
 * Represents ...
 * </p>
 */
    public static int HOLD = 8; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    public static int WAIT = 9; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    public static int REJECT = 10; 

  ///////////////////////////////////////
  // operations

/**
 * <p>
 * Hangup the call.
 * </p>
 * </p>
 */
    public void hangup();
/**
 * <p>
 * Forward the call. Only use when the call is on hold.
 * </p>
 * <p>
 * Parameters:
 * </p>
 * <p>
 * String sourceUri-> the From field that should appear on the invite message.
 * </p>
 * <p>
 * String destinationUri -> the destination where the message was supposed to be sent.
 * </p>
 * <p>
 * String finalToUri -> the destination where the message will be sent to.
 * </p>
 * </p>
 */
    public void forward(String sourceUri, String destinationUri, String finalToUri, String domain);

/**
 * <p>
 * Gets the to field of the call.
 * </p>
 * </p>
 */
    public String getTo();
/**
 * <p>
 * Gets the from field of the call.
 * </p>
 * </p>
 */
    public String getFrom();
/**
 * <p>
 * Method used to send a ringing message to the UAC.
 * </p>
 * </p>
 */
    public void processRinging();
/**
 * <p>
 * Returns the callId of the call. (the one that serves as an index on the calls list)
 * </p>
 * </p>
 */
    public String getCallId();
/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p>
 */
    public void reject();

} // end InCallController







