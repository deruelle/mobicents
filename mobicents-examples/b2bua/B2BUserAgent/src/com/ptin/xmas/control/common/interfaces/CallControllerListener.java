
/** Java interface "CallControllerListener.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package com.ptin.xmas.control.common.interfaces;

import com.ptin.xmas.control.exceptions.CallException;

import java.util.*;


/**
 * <p>
 * 
 * 
 * @author  xest228
 * </p>
 * </p>
 */
public interface CallControllerListener {

  ///////////////////////////////////////
  //attributes


/**
 * <p>
 * Represents ...
 * </p>
 */
    CallController call = null; 

  ///////////////////////////////////////
  // operations

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * 
 * @param callEvent ...
 * </p>
 */
    public void processEvent(final int callEvent) throws CallException;

} // end CallControllerListener





