
/** Java interface "InoCallController.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package com.ptin.xmas.control.common.interfaces;

import com.ptin.xmas.control.exceptions.CallException;
import com.ptin.xmas.control.exceptions.SetUpCallException;


import java.util.Vector;

/**
 * <p>
 * 
 * @author xest228
 * </p>
 * <p>
 * Correspondente ao JccProvider.
 * </p>
 */
public interface CallControllerManager {

  ///////////////////////////////////////
  // operations

/**
 * <p>
 * Estabelece uma chamada retornando o seu objecto de controlo.
 * </p>
 * <p>
 * Correspondente Jcc: JccProvider.createCall +
 * JccCall.addConnectionListener() + JccCall.routeCall()
 * </p>
 * <p>
 * 
 * @return a CallController with ...
 * </p>
 * <p>
 * @param from ...
 * </p>
 * <p>
 * @param to ...
 * </p>
 * <p>
 * @param controlLevel ...
 * </p>
 */
    public CallController setUpCall(String uid, CallControllerListener listener, String from, String fromName, String to, String sdp, int controlLevel) throws CallException;
/**
 * <p>
 * Transfer uma chamada que esteja a decorrer para outra chamada que esteja
 * a decorrer originando, para todos os efeitos, uma nova chamada. O
 * originador desta transferencia nao e participante nesta nova chamada.
 * Este metodo e usado tipicamente para transferir uma chamada ap&#243;s
 * consulta. Para evitar maus comportamento no uso deste metodo (com
 * intencao ou nao) as chamadas call1 e call2 tem de ter um elemento em
 * comum a que nao corresponde nem o from nem o to (a confirmar!!!) que nao
 * sera participante na nova chamada. Apos uma transferencia com sucesso a
 * call1 e a call2 serao removidas.
 * </p>
 * <p>
 * Jcc / Jcat : JccCall.consultTransfer()
 * </p>
 * <p>
 * 
 * @param call ...
 * </p>
 * <p>
 * @param from ...
 * </p>
 * <p>
 * @param to ...
 * </p>
 */
    public CallController consultTransfer(String from, int to, CallController call1, CallController call2);
/**
 * <p>
 * 
 * @return a Vector with the set of calls (CallController or
 * MultiPartyCallController) where user is involved
 * </p>
 * <p>
 * @param user : userId to identify the user to look for in existing calls.
 * </p>
 * <p>
 * To check how to implement this feature when more than one Application
 * Server is involved (to register in a DB the complete url including the
 * server address and its state to enable FaultTolerance?).
 * </p>
 * <p>
 * Jcc / Jcat Relationship: JcatProvider.getCalls()
 * </p>
 */
    public Vector getCalls(String user);

    public void removeCall(CallController call);
    
    public void addCall(CallController call);
    
} // end InoCallController





