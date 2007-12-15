/*
 * CallData.java
 *
 * Created on 2 de Julho de 2004, 15:54
 */

package com.ptin.xmas.control.common;

/**
 *
 * @author  xest228
 */
public class CallData {
    
  public static final int FEATURE_INCOM = 1;
//enable -> fica tudo indisponivel - combo from sip address , ligar->aceitar transferir redireccionar
  public static final int FEATURE_OUTCOM = 2;
  //enable ligar on combos on e botao ligar
  public static final int FEATURE_ONCOM = 3;
//enable apenas ligar fica ligado
  public static final int FEATURE_ONCOM_ADVANCED = 4;
  //enable liga o transferir para
  public static final int FEATURE_OUTCOM_TRY_AGAIN = 5;
    //aparece tentar de novo em ligar 
  public static final String INCOM = "incom";
  public static final String OUTCOM = "outcom";
  
  public static final int FEATURE_INCOM_ACCEPTING = 6;

  public static final int FEATURE_INCOM_FWDING = 7;
  
  public static final int FEATURE_INCOM_FWDED = 8;
  
  public static final int FEATURE_INCOM_DECLINED = 9;
  
  public static final int FEATURE_ONCOM_REDIRECTING = 10;
  
  public static final int FEATURE_ONCOM_REDIRECTED = 11;
  
  public static final int FEATURE_OUTCOM_CALLING = 12;
  
  public static final int FEATURE_NONE = 0;
  
  public static final String OUTCOMM = "outComm";
  public static final String INCALL = "inCall";
  public static final String NO_MEDIA = "noMedia";
  public static final String MYSELF = "myself";
 
    
    /** Creates a new instance of CallData */
    public CallData() {
    }
    
}
