/** Java interface "Leg.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package com.ptin.xmas.control.common.interfaces;

import java.util.*;
import javax.sip.*;
import javax.sip.message.*;

/**
 * <p>
 * Interface implemented by LegUAC and LegUAS.
 * </p>
 * <p>
 * It is used by CommProvider to process incoming messages, and by CallController.
 * </p>
 * </p>
 */
public interface Leg {

  ///////////////////////////////////////
  // operations

/**
 * <p>
 * Method used to process a received response message.
 * </p>
 * </p>
 */
    public void processResponse(Response response,ClientTransaction clientTransaction);
/**
 * <p>
 * Method used to process a received request message.
 * </p>
 * </p>
 */
     public void processRequest(Request request,ServerTransaction serverTransaction);
/**
 * <p>
 * Method used to set an SDP, that will be used on the next messages, if required.
 * </p>
 * </p>
 */
     public void setSdp(String cType_,String cSubType_,String sdp_);
/**
 * <p>
 * Method used to give an order to the Leg.
 * </p>
 * <p>
 * REMEMBER: The logic control is done on InCall or Comm3pcc.
 * </p>
 * <p>
 * static int  STARTING = 0;
 * </p>
 * <p>
 * static int  PROCESSING_INVITE_FROM = 1;
 * </p>
 * <p>
 * static int  INVITING_TO = 2;
 * </p>
 * <p>*/
 static int  PROCESSING_OK_TO = 3;
 /* </p>
 * <p>
 * static int  RESPONDING_OK_FROM = 4;
 * </p>
 * <p>*/
  static int  ACKING_TO = 5;
 
  static int  ACKING_TO_WITH_SDP = 12;
  /* </p>
 * <p>
 * static int  TALKING = 6;
 * </p>
 * <p>*/
  static int  BYE = 7;
 /* </p>
 * <p>
 * static int  HOLD =8;
 * </p>
 * <p>
 * static int  WAIT =9;
 * </p>
 * <p>
 * static int  REJECT =10;
 * </p>
 * <p>
 * static int  RINGING=11;
 * </p>
 * </p>
 */
    public void setStatusCode(int stts);
/**
 * <p>
 * Method used to hang up the call.
 * </p>
 * </p>
 */
    public void hangup();
/**
 * <p>
 * Method used to start a new Invite transaction.(used when the media session
 * </p>
 * <p>
 * is to be changed).
 * </p>
 * </p>
 */
    public void restart();

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p>
 */
   public void processTimeout(Request request,Transaction transaction);

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p>
 */
    public String getCallEndReason();
/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a boolean with ...
 * </p>
 */
    public boolean isDestination();

    public String getCType();
    public String getCSubType();
    public String getSDP();
    
    public void sendInfo(String type, String subType, String body);
    
} // end Leg







