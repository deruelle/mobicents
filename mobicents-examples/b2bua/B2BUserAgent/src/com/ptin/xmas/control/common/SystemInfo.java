/** Java class "SystemInfo.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package com.ptin.xmas.control.common;

import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

/**
 * <p>
 * 
 * </p>
 */
public class SystemInfo {

  ///////////////////////////////////////
  // attributes


/**
 * <p>
 * Represents ...
 * </p>
 */
    private String myIp = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String jabberServer = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String userAgent = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String sipProxy = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String webServer = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String vmail = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String voiceMSGW = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String controller = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String subFolder = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String passwd = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String regExp = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String cativar = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String audioRate = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String videoRate = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String lowCreditAlert = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String videoRcv = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String audioRcv = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String logFileName = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String logPathName = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String cdrFileName = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String cdrPathName = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String authpassword = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String creditServer = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String creditServerPort = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String creditServerPath = ""; 
    
    private String domain = ""; 
    
    private static SystemInfo mySelf = null;
    
    public static Logger log = Logger.getLogger("inocrea");


  ///////////////////////////////////////
  // operations


/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a SystemInfo with ...
 * </p><p>
 * @param file ...
 * </p>
 */
    public  SystemInfo(String file) {
    	BasicConfigurator.configure();
               Properties p = null;
               
               mySelf = this;
                // set up new properties object
        	// from file "myProperties.txt"
               try {

                FileInputStream propFile = new FileInputStream(file+".properties");
                
                p = new Properties(System.getProperties());
                p.load(propFile);
                propFile.close();
               } catch (Exception e){}; 
               myIp=(String)p.getProperty("myip");
               userAgent=(String)p.getProperty("useragent");
               sipProxy=(String)p.getProperty("sipproxy");
               passwd=(String)p.getProperty("passwd");
               audioRcv=(String)p.getProperty("audiorcv");
               videoRcv=(String)p.getProperty("videorcv");
               authpassword=(String)p.getProperty("authpassword");

    } // end SystemInfo        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p>
 */
    
    public static SystemInfo getReference(String file) throws Exception{
        if (mySelf==null) try{
            mySelf=new SystemInfo(file);
        }catch(Exception e){throw e;}
        return mySelf;
    } // end getReference        

    public String getDomain() {        
        return domain;
    } // end getIp        
    
    public String getIp() {        
        return myIp;
    } // end getIp        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p>
 */
    public String getUA() {        
        return userAgent;
    } // end getUA        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p>
 */
    public String getProxy() {        
        return sipProxy;
    } // end getProxy        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p>
 */
    public String getJabberServer() {        
        return jabberServer;
    } // end getJabberServer        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p>
 */
    public String getWebServer() {        
        return webServer;
    } // end getWebServer        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p>
 */
    public String getVmail() {        
        return vmail;
    } // end getVmail        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p>
 */
    public String getVoiceMSGW() {        
        return voiceMSGW;
    } // end getVoiceMSGW        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p>
 */
    public String getSubFolder() {        
        return subFolder;
    } // end getSubFolder        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p>
 */
    public String getController() {        
        return controller;
    } // end getController        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p>
 */
    public String getPasswd() {        
        return passwd;
    } // end getPasswd        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p>
 */
    public String getRegExp() {        
        return regExp;
    } // end getRegExp        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a long with ...
 * </p>
 */
    public long getCativar() {        
        return Long.parseLong(cativar);
    } // end getCativar        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a int with ...
 * </p>
 */
    public int getAudioRate() {        
        return Integer.parseInt(audioRate);
    } // end getAudioRate        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a int with ...
 * </p>
 */
    public int getVideoRate() {        
        return Integer.parseInt(videoRate);
    } // end getVideoRate        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a int with ...
 * </p>
 */
    public int getLowCreditAlert() {        
        return Integer.parseInt(lowCreditAlert);
    } // end getLowCreditAlert        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a int with ...
 * </p>
 */
    public int getAudioRcv() {        
        return Integer.parseInt(audioRcv);
    } // end getAudioRcv        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a int with ...
 * </p>
 */
    public int getVideoRcv() {        
        return Integer.parseInt(videoRcv);
    } // end getVideoRcv        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p>
 */
    public String getLogFileName() {        
        return logFileName;
    } // end getLogFileName        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p>
 */
    public String getCDRFileName() {        
        return cdrFileName;
    } // end getCDRFileName        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p>
 */
    public String getLogPathName() {        
        return logFileName;
    } // end getLogPathName        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p>
 */
    public String getCDRPathName() {        
        return cdrFileName;
    } // end getCDRPathName        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p>
 */
    public String getAuthenticationPassword() {        
        return authpassword;
    } // end getAuthenticationPassword        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p>
 */
    public String getCreditServer() {        
        return  creditServer;
    } // end getCreditServer        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p>
 */
    public String getCreditServerPort() {        
        return creditServerPort;
    } // end getCreditServerPort        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p>
 */
    public String getCreditServerPath() {        
        return creditServerPath;
    } // end getCreditServerPath        

} // end SystemInfo





