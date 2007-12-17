/** Java class "Writerx.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package com.ptin.xmas.control.common;

import com.ptin.xmas.control.common.SystemInfo;
import java.io.*;
import java.io.FileOutputStream;
import java.util.*;

/**
 * <p>
 * 
 * @author	Sérgio Almeida
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
public class Writerx {

  ///////////////////////////////////////
  // attributes


/**
 * <p>
 * Represents ...
 * </p>
 */
    private static Writerx mySelf = null; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private WriterThread writeThread = null; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private LoggerThread loggerThread = null; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private CDRThread cdrThread = null; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private LinkedList writeList = null; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private LinkedList logList = null; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private LinkedList cdrList = null; 

  ///////////////////////////////////////
  // operations


/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a Writerx with ...
 * </p>
 */
    private  Writerx() {        
        writeList=new LinkedList();
        logList=new LinkedList();
        cdrList=new LinkedList();
        writeThread=new WriterThread();
        loggerThread=new LoggerThread();
        cdrThread=new CDRThread();
        writeThread.start();
        loggerThread.start();
        cdrThread.start();
    } // end Writerx        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a Writerx with ...
 * </p>
 */
    public static Writerx getReference() {        
        if (mySelf==null) mySelf=new Writerx();
        return mySelf;
    } // end getReference        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * @param filename ...
 * </p><p>
 * @param in ...
 * </p>
 */
    public void addRegist(String filename, StringBuffer in) {        
        String fileNamex=new String(filename);
        StringBuffer out=new StringBuffer(in.toString());
        writeList.addLast((new Regist(fileNamex,out)));
    } // end addRegist        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * @param in ...
 * </p>
 */
    public void addLogRegist(StringBuffer in) {        
        String out=new String(in.toString());
        logList.addLast(out);
    } // end addLogRegist        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * @param in ...
 * </p>
 */
    public void addCDRRegist(StringBuffer in) {        
        String out=new String(in.toString());
        cdrList.addLast(out);
    } // end addCDRRegist        


  ///////////////////////////////////////
  // inner classes/interfaces

/**
 * <p>
 * 
 * </p>
 */
class Regist {

  ///////////////////////////////////////
  // attributes


/**
 * <p>
 * Represents ...
 * </p>
 */
    String filename = null; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    StringBuffer registBuf = null; 

  ///////////////////////////////////////
  // operations


/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a Regist with ...
 * </p><p>
 * @param f ...
 * </p><p>
 * @param b ...
 * </p>
 */
     Regist(String f, StringBuffer b) {        
        filename=f;
        registBuf=b;
    } // end Regist        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p>
 */
    String getFileName() {        
        return filename;
    } // end getFileName        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a byte[] with ...
 * </p>
 */
    byte[] getData() {        
        return registBuf.toString().getBytes();
    } // end getData        

} // end Writerx.Regist
/**
 * <p>
 * 
 * </p>
 */
class WriterThread extends Thread {

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
        while(true){
            try{
                Thread.sleep(10);
            }catch(Exception e){System.err.println(""+e);}
            if (!writeList.isEmpty()){
                Regist regist=(Regist)writeList.removeFirst();
                writeThis(regist.getFileName(), regist.getData());
            }
        }
    } // end run        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * @param file ...
 * </p><p>
 * @param data ...
 * </p>
 */
    private synchronized void writeThis(String file, byte[] data) {        
        try{
            FileOutputStream x=null;
            try{
                x=new FileOutputStream(file,true); 
            }catch(FileNotFoundException fnfe){System.err.println(fnfe);}
            x.write(data);
            x.close();    
           }catch(Exception e){System.err.println("write error... "+e.getMessage());}
    } // end writeThis        

} // end Writerx.WriterThread
/**
 * <p>
 * 
 * </p>
 */
class LoggerThread extends Thread {

  ///////////////////////////////////////
  // attributes


/**
 * <p>
 * Represents ...
 * </p>
 */
    FileOutputStream x = null; 

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
        info=new SystemInfo("incom");
        try{
            x=new FileOutputStream(info.getLogFileName(),true); 
        }catch(FileNotFoundException fnfe){System.err.println(fnfe);}
        
        
        while(true){
            try{
                Thread.sleep(10);
            }catch(Exception e){System.err.println(""+e);}
            if (!logList.isEmpty()){
                String data=(String)logList.removeFirst();
                writeThis(data.getBytes());
            }
        }
    } // end run        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * @param data ...
 * </p>
 */
    private synchronized void writeThis(byte[] data) {        
        try{
            checkFile();
            x.write(data);
           }catch(Exception e){System.err.println("write error... "+e.getMessage());}
    } // end writeThis        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p>
 */
    private void checkFile() {        
        try{
            if (x.getChannel().size()>=5000000){
                x.close();
                File file=new File(info.getLogFileName());
                file.renameTo(new File(info.getLogPathName()+System.currentTimeMillis()+".log"));
                x=new FileOutputStream(info.getLogFileName(),true);
            }
        }catch(Exception e ){System.out.println(""+e);}
    } // end checkFile        

} // end Writerx.LoggerThread
/**
 * <p>
 * 
 * </p>
 */
class CDRThread extends Thread {

  ///////////////////////////////////////
  // attributes


/**
 * <p>
 * Represents ...
 * </p>
 */
    FileOutputStream z = null; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    SystemInfo info = null; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    String toAdd = "VoIP3.0-A-INOCREA.xsd,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\nhttp://www.ipdr.org/namespaces/ipdr,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\nsubscriberID,hostname,ipAddress,startTime,endTime,timeZoneOffset,callCompletionCode,originalDestinationId,uniqueCallId,imsiIngress,esnIngress,callProgressState,disconnectReason,destinationId,thirdPartyId,ani,oLIiiDigit,iiDigits,dnis,pin,serviceConsumerType,startAccessTime,endAccessTime,callSetupDuration,callDuration,totalDuration,tearDownDuration,averageLatency,type,paymentType,feature,codec,silenceCompressionMode,modem,supplementaryService,extendedReasonCode,disconnectLocation,proprietaryErrorCode,unitsConsumed,inboundByteCount,outboundByteCount,inboundPacketCount,outboundPacketCount,inboundLostPacketCount,outboundLostPacketCount,inboundRxmtPacketCount,outboundRxmtPacketCount,subscribedQoSClasses,callClarityIndex,voiceQualityIndex,transmissionRatingRFactor,userPerceivedRFactor,faxPerformanceMetric,faxPageTxCount,faxPageRxCount,packetLossPercentage,outOfSequencePackets,correctSequencePackets,packetDelayVariation,ipAddressIngressDevice,ipAddressEgressDevice,portNumber,imsiEngress,esnEgress,homeLocationIngress,homeLocationIdEgress\n"; 

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
        info=new SystemInfo("incom");
        try{
            z=new FileOutputStream(info.getCDRFileName(),true); 
        }catch(FileNotFoundException fnfe){System.err.println(fnfe);}
        
        
        while(true){
           // System.out.println("Im workin'!!!"+info.getCDRFileName());
            try{
                Thread.sleep(100);
            }catch(Exception e){System.err.println(""+e);}
            if (!cdrList.isEmpty()){
                String data=(String)cdrList.removeFirst();
                writeThis(data.getBytes());
            }
        }
    } // end run        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * @param data ...
 * </p>
 */
    private synchronized void writeThis(byte[] data) {        
        try{
            this.checkFile();
            z.write(data);
           }catch(Exception e){System.err.println("write error... "+e.getMessage());}
    } // end writeThis        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p>
 */
    private void checkFile() {        
        try{
            if (z.getChannel().size()>=2000000){
                z.close();
                File file=new File(info.getCDRFileName()); 
                file.renameTo(new File(info.getCDRPathName()+System.currentTimeMillis()+".csv"));  
                z=new FileOutputStream(info.getCDRFileName(),true); 
                z.write(toAdd.getBytes());
            }else if (z.getChannel().size()<=10){
                z.write(toAdd.getBytes());
            }
            
        }catch(Exception e ){System.out.println(""+e);}
    } // end checkFile        

} // end Writerx.CDRThread

} // end Writerx





