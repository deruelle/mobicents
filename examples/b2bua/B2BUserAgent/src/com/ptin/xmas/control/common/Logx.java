/** Java class "Logx.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package com.ptin.xmas.control.common;


import java.io.*;
import java.util.*;
import java.util.Date;

/**
 * <p>
 * 
 * @author	João Pedro Patriarca
 * </p>
 * <p>
 * @version
 * </p>
 * <p>
 * @see
 * </p>
 * <p>
 * @since
 * </p>
 * </p>
 */
public class Logx {

  ///////////////////////////////////////
  // attributes


/**
 * <p>
 * Represents ...
 * </p>
 */
    private static Logx mySelf = null; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private StringBuffer buf = null; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private WriteThread writeThread = null; 

  ///////////////////////////////////////
  // operations


/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a Logx with ...
 * </p>
 */
    private  Logx() {        
        buf=new StringBuffer();
        writeThread=new WriteThread();
        writeThread.start();
    } // end Logx        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a Logx with ...
 * </p>
 */
    public static Logx getReference() {        
        if (mySelf==null) mySelf=new Logx();
        return mySelf;
    } // end getReference        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p><p>
 * @param n ...
 * </p>
 */
    private String printTab(int n) {        
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < n; ++i) {
          sb.append(' ');
        }
        return sb.toString();
    } // end printTab        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * @param message ...
 * </p>
 */
    public void log(String message) {        
        log(message, 0);
    } // end log        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * @param message ...
 * </p>
 */
    public void trace(String message) {        
        trace(message, 0);
    } // end trace        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * @param message ...
 * </p>
 */
    public void error(String message) {        
        error(message, 0);
    } // end error        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * @param message ...
 * </p><p>
 * @param n ...
 * </p>
 */
    public void log(String message, int n) {        
        System.out.println(printTab(n)+"LOG: " + message);
        buf.append(printTab(n)+(new Date(System.currentTimeMillis())).toString()+"LOG: " + message+"\n");
    } // end log        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * @param message ...
 * </p><p>
 * @param n ...
 * </p>
 */
    public void trace(String message, int n) {        
        System.out.println(printTab(n)+"TRACE: " + message);
        buf.append(printTab(n)+(new Date(System.currentTimeMillis())).toString()+"TRACE: " + message+"\n");
    } // end trace        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * @param message ...
 * </p><p>
 * @param n ...
 * </p>
 */
    public void error(String message, int n) {        
        System.out.println(printTab(n)+"ERROR: " + message);
        buf.append(printTab(n)+(new Date(System.currentTimeMillis())).toString()+"ERROR: " + message+"\n");
    } // end error        


  ///////////////////////////////////////
  // inner classes/interfaces

/**
 * <p>
 * 
 * </p>
 */
class WriteThread extends Thread {

  ///////////////////////////////////////
  // attributes


/**
 * <p>
 * Represents ...
 * </p>
 */
    Writerx writer = null; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    SystemInfo info = null; 

  ///////////////////////////////////////
  // operations


/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p>
 */
    public void run() {        
        writer=Writerx.getReference();
        info=new SystemInfo("incom");
        while(true){
            try{
                Thread.sleep(2000);
                if (buf.length()>2){
                writer.addLogRegist(buf);
                buf=null;
                buf=new StringBuffer();
                }
            }catch(Exception e){System.err.println(""+e);}
        }
    } // end run        

} // end Logx.WriteThread

} // end Logx





