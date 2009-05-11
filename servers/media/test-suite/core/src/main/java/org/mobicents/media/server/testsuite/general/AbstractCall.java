/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.testsuite.general;

import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicLong;
import javax.sdp.Attribute;
import javax.sdp.MediaDescription;
import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SessionDescription;
import org.mobicents.media.server.testsuite.general.rtp.RtpPacket;
import org.mobicents.mgcp.stack.JainMgcpExtendedListener;
import org.mobicents.mgcp.stack.JainMgcpStackProviderImpl;

/**
 *
 * @author baranowb
 */
public abstract class AbstractCall implements JainMgcpExtendedListener, Runnable, Serializable{
    //We are serializable, just in case.
    
    //Some static vals:
    protected transient  static final AtomicLong _GLOBAL_SEQ = new AtomicLong(-1);
    
    
    protected final long sequence;
    
    protected CallState state = CallState.INITIAL;
    protected int avgJitter ;
    protected int peakJitter ;
    
    protected long lastDeliverTimeStamp;
    
   
    //Data dump
    protected transient java.io.File dataFileName;
    protected transient FileOutputStream fos;
    protected transient ObjectOutputStream dataDumpChannel;
    
    //Media part
    
    protected String endpointName =""; //endpoint name with wildcard - used to send to mms to get actual EI
    protected EndpointIdentifier endpointIdentifier;
    protected CallIdentifier callIdentifier;
    
    // Below is part we dont want to propagate to other side :)
    
    protected transient AbstractTestCase testCase;
    //RTP part
    protected transient DatagramChannel datagramChannel;
    protected transient final ScheduledExecutorService readerThread = Executors.newSingleThreadScheduledExecutor();
    protected transient ScheduledFuture readerTask;
    protected transient boolean receiveRTP;
    //MGCP part
    protected transient JainMgcpStackProviderImpl provider; 
    public AbstractCall(AbstractTestCase testCase) throws IOException
    {
        this.testCase = testCase;
        this.callIdentifier = testCase.getProvider().getUniqueCallIdentifier();
        boolean finished = false;
        try{
            this.initSocket();
            this.sequence = _GLOBAL_SEQ.incrementAndGet();
            this.setDumpDir(this.testCase.getTestDumpDirectory());
            this.fos = new FileOutputStream(dataFileName);
            this.dataDumpChannel = new ObjectOutputStream(fos);
            this.provider = testCase.getProvider();
            
            
            finished = true;
        }finally
        {
            if(!finished)
            {
                try{
                 if(datagramChannel.isConnected() || datagramChannel.isOpen())
                 {
                    datagramChannel.close();
                 }
                 this.datagramChannel = null;
                }catch(Exception e)
                {
                }
                
                try{
                 if(dataDumpChannel!=null)
                 {
                     dataDumpChannel.close();
                 }
                 this.dataDumpChannel = null;
                
                 this.fos = null;
                }catch(Exception e)
                {
                }
            }
            
        }
    }

    void setDumpDir(File testDumpDirectory) {
       this.dataFileName  = new File(testDumpDirectory,this.sequence+".rtp");
    }
    
    
    private void initSocket() throws IOException   {
       datagramChannel = DatagramChannel.open();  
       DatagramSocket socket = this.datagramChannel.socket();
       
       InetAddress bindAddress = this.testCase.getClientTestNodeAddress();
       for (int i = 1024; i < 65535; i++) {
            try {
                SocketAddress address = new InetSocketAddress(bindAddress, i);
                socket.bind(address);
               
                return;
            } catch (SocketException e) {
                continue;
            }
        }
        throw new SocketException();
    }   
     public List<RtpPacket> getRtp() throws IOException {
        return this.loadRtp();
    }    
    private List<RtpPacket> loadRtp() throws IOException {
        ArrayList<RtpPacket> list = new ArrayList();
        FileInputStream fin = new FileInputStream(this.dataFileName);
        ObjectInputStream in = new ObjectInputStream(fin);
        
        while(in.available()>0) {
            try {
                RtpPacket p = (RtpPacket) in.readObject();
                list.add(p);
            } catch (ClassNotFoundException e) {
            }
        }
        return list;
    }    
    public CallState getState()
    {
        return state;
    }
    public int getAvgJitter()
    {
        return this.avgJitter;
    }
    public int getPeakJitter()
    {
        return this.peakJitter;
    }
    public EndpointIdentifier getEndpoint()
    {
        return this.endpointIdentifier;
    }
    public CallIdentifier getCallID()
    {
        return this.callIdentifier;
    }
    public long getSequence()
    {
        return this.sequence;
    }
    
    protected void setState(CallState state)
    {
        if(state == this.state)
        {
            return;
        }
        
        this.state = state;
        
        switch(this.state)
        {
            case ENDED:
            case IN_ERROR:
                 try{
                 if(datagramChannel.isConnected() || datagramChannel.isOpen())
                 {
                    datagramChannel.close();
                 }
                 this.datagramChannel = null;
                }catch(Exception e)
                {
                }
                
                try{
                 if(dataDumpChannel!=null)
                 {
                     dataDumpChannel.close();
                 }
                 this.dataDumpChannel = null;
                
                 this.fos = null;
                }catch(Exception e)
                {
                }
                break;
                
            default:
                break;
        }
        
        this.testCase.callStateChanged(this);
        
    }
    
    
    
    protected String getLocalDescriptor(int port) {

		SessionDescription localSDP = null;
        String userName = "Mobicents-Call-Generator";
        long sessionID = System.currentTimeMillis() & 0xffffff;
        long sessionVersion = sessionID;

        String networkType = javax.sdp.Connection.IN;
        String addressType = javax.sdp.Connection.IP4;
        
        SdpFactory sdpFactory  = testCase.getSdpFactory();
                
        try {
            localSDP = sdpFactory.createSessionDescription();
            localSDP.setVersion(sdpFactory.createVersion(0));
            localSDP.setOrigin(sdpFactory.createOrigin(userName, sessionID, sessionVersion, networkType, addressType, this.testCase.getClientTestNodeAddress().getHostAddress()));
            localSDP.setSessionName(sdpFactory.createSessionName("session"));
            localSDP.setConnection(sdpFactory.createConnection(networkType, addressType, this.testCase.getClientTestNodeAddress().getHostAddress()));

            Vector<Attribute> attributes = testCase.getSDPAttributes();
            int[] audioMap = new int[attributes.size()];
            for(int index = 0;index<audioMap.length;index++)
            {
                String m=attributes.get(index).getValue().split(" ")[0];
                audioMap[index] = Integer.valueOf(m);
            }
            // generate media descriptor
            MediaDescription md = sdpFactory.createMediaDescription("audio", port, 1, "RTP/AVP", audioMap);

            // set attributes for formats
           
            md.setAttributes(attributes);
            Vector descriptions = new Vector();
            descriptions.add(md);

            localSDP.setMediaDescriptions(descriptions);
        } catch (SdpException e) {
            e.printStackTrace();
        }

        //System.out.println("Local SDP: " + localSDP.toString());

        return localSDP.toString();
    }
	
    
    
    
    
    
    
    
    //Run method for read, we are run in readerThread
    public void run() {
    	try{
    		while (receiveRTP) {
    			//byte[] buffer = new byte[1024];
    			//DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        ByteBuffer packetBuffer = ByteBuffer.allocate(1024);
    			try {
    				datagramChannel.receive(packetBuffer);
    				
    				RtpPacket rtp = new RtpPacket(packetBuffer.array());
    				rtp.setTime(new Date(System.currentTimeMillis()));
    				//rtpTraffic.add(rtp);
    				dataDumpChannel.writeObject(rtp);
    				
                                //FIXME: add calcualtion of jiiter stuff
    			} catch (IOException e) {
    			}
    		}

    	}finally {
        	
    	}
    }
    
    
    public abstract void start();
    public abstract void timeOut();
    public abstract void stop();
    
}
