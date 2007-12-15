
/** Java interface "Call.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package com.ptin.xmas.control.common.interfaces;

import java.util.*;

/**
 * <p>
 * 
 * @author Paulo Chainho
 * </p>
 * <p>
 * Interface used by Call Legs.
 * </p>
 */
public interface Call {

  static int  STARTING = 0;
  static int  PROCESSING_INVITE_FROM = 1;
  static int  INVITING_TO = 2;
  static int  PROCESSING_OK_TO = 3;
  static int  RESPONDING_OK_FROM = 4;
  static int  ACKING = 5;
  static int  TALKING = 6;
  static int  BYE = 7;
  static int  HOLD =8;
  static int  WAIT =9;
  static int  REJECT =10;
  static int  BUSY =12;
  static int  NETWORKUNAVAILABLE =13;
  static int  NOANSWER =14;
  static int  UNAVAILABLE =15;
  static int  RINGING=11;
    
  ///////////////////////////////////////
  // operations

/**
 * <p>
 * Method used to send a ringing message to the UAC.
 * </p>
 * <p>
 * 
 * </p>
 * 
 */
    public void processRinging();
/**
 * <p>
 * sets the sdp from one leg to the other.
 * </p>
 * <p>
 * 
 * </p>
 * 
 */
    public void setSdp(String ctype_, String csubtype_, String sdp_, boolean isDestination);
/**
 * <p>
 * Sets the status code of a Leg, for logic control.
 * </p>
 * 
 */
    public void setStatusCode(int x, boolean isDestination);

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * @param fromURI ...
 * </p><p>
 * @param toURI ...
 * </p><p>
 * @param redirectURI ...
 * </p>
 */
    public void forward(String fromURI, String toURI, String redirectURI,String domain);

    
    
    public void restartMe(boolean isDestination);
    
    public String getAppName();
    
} // end Call





