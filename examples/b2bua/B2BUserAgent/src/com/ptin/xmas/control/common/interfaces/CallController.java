
/** Java interface "CallController.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package com.ptin.xmas.control.common.interfaces;


/**
 * <p>
 * Interface used by both legs. Implemented by Comm3pcc and Incall.
 * </p>
 * </p>
 */
public interface CallController {

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

/**
 * <p>
 * Represents ...
 * </p>
 */
    static final int fromConnected = 1; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    static final int callEstablished = 2; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    static final int fromDisconnected = 3; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    static final int toDisconnected = 4; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    static final int fromRejected = 5; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    static final int fromBusy = 6; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    static final int fromNoAnswer = 7; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    static final int fromUnavailable = 8; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    static final int fromNetworkUnavailable = 9; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    static final int toRejected = 10; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    static final int toBusy = 11; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    static final int toNoAnswer = 12; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    static final int toUnavailable = 13; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    static final int toNetworkUnavailable = 14; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    static final int incomingCall = 15; 
    static final int FROM_RINGING = 17;
    
    static final int TO_RINGING = 18; 
    
    static final int TO_CONNECTED = 19;
    
    static final int CALL_REDIRECTION_SUCCESS = 20;
    
    static final int CALL_REDIRECTION_FAILED = 21;
    
    static final int TO_MEDIA_UNSUPPORTED = 22;

    static final int TO_TRYING = 23;
    
    static final int PEER_DISCONNECTED = 24;
    
    static final int STATUS_INVITING_PEER = 25;

    static final int STATUS_PEER_CONNECTED = 26;
    
    static final int STATUS_PEER_RINGING = 27;

    static final int STATUS_OUTCOM_FAILED = 28;
    
    static final int STATUS_PEER_DISCONNECTED = 29;
    
    static final int STATUS_ONCOM = 30;

    static final int STATUS_FINISHED = 31;
    
    static final int EVENT_ACK = 32;
    
    static final int EVENT_BYE = 33;
    
    
    static final int STATUS_INCOM_ACCEPTED = 34;
    
    static final int STATUS_INCOM_RINGING = 35;
    // An event during communication to change media, ie, reinvite
    static final int EVENT_MEDIA_CHANGE_REQ = 36;
    
    static final int EVENT_REGISTERED = 37;

    static final int EVENT_UNREGISTERED = 38;
    // Esta constante dever ser inicializada através do ficheiro de propriedades
    static final String CLASS_END_POINT_CALL_CONTROLLER = "inocrea.control.callControl.Comm"; 
    
/**
 * <p>
 * Represents ...
 * </p>
 */
    static final int noControl = 0; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    static final int basicControl = 1; 

    

   ///////////////////////////////////////
  // associations

    

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
 * Gets the call data record for the call.
 * </p>
 * </p>
 */
/**
 * <p>
 * Gets the to field of the call.
 * </p>
 * </p>
 */
    public String getToName();
/**
 * <p>
 * Gets the from field of the call.
 * </p>
 * </p>
 */
    public String getFromName();
/**
 * <p>
 * Returns the callId of the call. (the one that serves as an index on the calls list)
 * </p>
 * </p>
 */
    public String getCallId();
/**
 * <p>
 * Gets the user uri the will pay for the call.
 * </p>
 * </p>
 */
    public String getPayer();
/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * 
 * @param listener ...
 * </p>
 */
    public void setCallControllerListener(CallControllerListener listener);
/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * 
 * @param controlLevel ...
 * </p>
 */
    public void setControlLevel(int controlLevel);
/**
 * <p>
 * Encaminha chamada para endereco definido na variavel to. Esta operacao
 * nao e valida depois de receber o evento toConnected. Situacoes tipicas
 * de uso:
 * </p>
 * <p>
 * 1- chamadas de entrada recebidas na interface InComListener;
 * </p>
 * <p>
 * 2- estebelecimento de chamada efectuado atraves do metodo SetUpCall da
 * interface InoCallControl depois de receber o evento fromConnected mas
 * antes de receber o evento toConnected.
 * </p>
 * <p>
 * 
 * 
 * @param to : endereco para onde a chamada e' encaminhada
 * </p>
 */
    public void connectTo(String to, String body);
/**
 * <p>
 * 
 * </p>
 * <p>
 * 
 * 
 * @return url para um ficheiro com a imagem do originador da chamada se
 * disponivel nas mensagens SIP. Senao pode-se questionar a BD pelo
 * ficheiro em funcao do sipUrl.
 * </p>
 */
    public String getCallerPicture();
/**
 * <p>
 * Chamada em espera &#233; recuperada. So e valido se a chamada estiver em
 * espera, i.e., depois de receber o evento callOnHold (!!?)
 * </p>
 */
    public void resumeCall();
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
    
 public String getSdpFrom();
 
 public String getSdpTo();
 
 public void setSdpFrom(String sdp);
 public void setSdpTo(String sdp);
    
 public void start();

 public void ack();
 
 public void reject();
 
 public void ringing();
 
// public void setStatus(int status);

} // end CallController





