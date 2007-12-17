/** Java class "InoCallControllerBean.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package com.ptin.xmas.control.common;

import com.ptin.xmas.control.exceptions.*;

import com.ptin.xmas.control.common.interfaces.CallControllerListener;
import com.ptin.xmas.control.common.interfaces.CallController;
import com.ptin.xmas.control.common.interfaces.CallControllerManager;
import com.ptin.xmas.control.common.interfaces.CallManager;

import java.util.Hashtable;
import java.util.Vector;

/**
 * <p>
 * 
 * </p>
 */
public class CallControllerManagerImpl implements CallControllerManager {

  ///////////////////////////////////////
  // attributes

private Hashtable calls = null;
private String appName = "";
private String appPasswd = "";
    
/**
 * <p>
 * Represents ...
 * </p>
 */
    
    private static CallManager callProvider = null; 

  ///////////////////////////////////////
  // operations


/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a InoCallControllerBean with ...
 * </p>
 */
    public CallControllerManagerImpl(CallManager registrar,  /*InComListener listener,*/ int registrationRate, int timeout, String name, String password  ) {        
       //SystemInfo.log.debug("construtor CallControllerManagerImpl");
       System.out.println("construtor CallControllerManagerImpl");
       
// incluir registration rate       
    callProvider = registrar;
    appName = name;
    appPasswd = password;
    
    calls = new Hashtable();
    
    //callProvider.processRegistering(name, password/*, listener*/);
    }
      

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
    public CallController setUpCall(String uid, CallControllerListener listener, String from, String fromName, String to, String sdp, int controlLevel) {        
        
        //SystemInfo.log.debug("[CallControllerManager] SETUP CALL FROM: "+from+" TO: "+to+" SDP "+sdp); 
        
        CallController call = null;
        
            call = callProvider.startCall(uid, appName, listener, from, fromName, to, controlLevel, 2);
        
        // PCh: Tirar o próximo comentario qdo se passar referencia deste objecto para callController fazer remove call no fim da chamada.
        // Necessario para fazer getCalls. Ver como conjugar c/ ongoing calls do CallManager
        
/*        calls.put(from,callController);
        calls.put(to,callController);
    */    
        return call;
        
    } // end setUpCall        
  
    
    public CallController consultTransfer(String from, int to, CallController call1, CallController call2) {
        return null;
    }
    
    public Vector getCalls(String user) {
        return null;
        
    }
    
    public void removeCall(CallController call) {
        if (calls.containsKey(call.getFrom()))
            calls.remove(call.getFrom());
        if (calls.containsKey(call.getTo()))
            calls.remove(call.getTo());
    }
    
    public void addCall(CallController call) {
        calls.put(call.getFrom(),call);
        calls.put(call.getTo(),call);
    }
    
 // end setSessionContext        

} // end InoCallControllerBean





