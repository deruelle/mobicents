/*
 * Mobicents Media Gateway
 *
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */
package org.mobicents.media.server.impl.rtp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.Logger;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.CachedBuffersPool;
import org.mobicents.media.server.local.management.WorkDataGatherer;

/**
 * Implements jitter buffer.
 *  
 * A jitter buffer temporarily stores arriving packets in order to minimize 
 * delay variations. If packets arrive too late then they are discarded. 
 * A jitter buffer may be mis-configured and be either too large or too small. 
 *
 * If a jitter buffer is too small then an excessive number of packets may be 
 * discarded, which can lead to call quality degradation. If a jitter buffer is 
 * too large then the additional delay can lead to conversational difficulty.
 *
 * A typical jitter buffer configuration is 30mS to 50mS in size. 
 * In the case of an adaptive jitter buffer then the maximum size may be set 
 * to 100-200mS. Note that if the jitter buffer size exceeds 100mS then the 
 * additional delay introduced can lead to conversational difficulty.
 *
 * @author Oleg Kulikov
 */
public class JitterBuffer implements Serializable {

    private RtpSocket rtpSocket;
    private boolean ready = false;
    private int jitter;
    private int seq = 0;
    private int period;
    private ReentrantLock state = new ReentrantLock();
    //No need for sync queue, everything works inside statelock
    private List<RtpPacket> buffers = new ArrayList<RtpPacket>();
    //private PriorityBlockingQueue<RtpPacket> buffers=new PriorityBlockingQueue<RtpPacket>(0,new RtpPacktsSeqComparator());
    private int maxSize;
    private transient Logger logger = Logger.getLogger(JitterBuffer.class);
    private WorkDataGatherer gatherer=null;
    //This helps to get along with packets lost :]
    //Its much easier to keep track of consumed Seq, than update list when late packet arrive
    private int lastConsumedSeq=0;
    private int interArrivalJitter=0;
    private long lastArrivalTimeStamp=0;
    /**
     * Creates new instance of jitter.
     * 
     * @param fmt the format of the received media
     * @param jitter the size of the jitter in milliseconds.
     */
    public JitterBuffer(RtpSocket rtpSocket, int jitter, int period,WorkDataGatherer gatherer) {
        this.rtpSocket = rtpSocket;
        this.maxSize = 2*jitter / period;
        this.period = period;
        this.gatherer=gatherer;
        //this.buffers.
    }

    /**
     * Gets the size of jitter.
     * 
     * @return the size of jitter in milliseconds
     */
    public int getJitter() {
        return jitter;
    }

    public void setPeriod(int period) {
        this.period = period;
        maxSize = jitter / period;
    }

    /**
     * Writes media packet to the jitter buffer.
     * 
     * @param buffer the media packet.
     */
    public void write(RtpPacket rtpPacket) {
        try {
            state.lock();
            //check size of the jitter buffer.
            //if buffer is full drop to most "top" packet and push
            //current packet into the jitter buffer
            
            if (buffers.size() == this.maxSize) {
                RtpPacket p = buffers.remove(0);
            	//RtpPacket p = buffers.poll();
                this.gatherer.packetsLost(1);
            }
            
            //Reorganize, Yeah someone could say that this suck, but linear search is faster here
            //we have less than 30 elements for jitter == 500ms, if packets are not reversed this will do good
            //possibly we can add some hybrid of binary search here
            if(buffers.size()==0)
            {
            	buffers.add(rtpPacket);
            }else
            {
            	for(int lastRightIndex=buffers.size();lastRightIndex>=0;lastRightIndex--)
            	{
            		if(rtpPacket.getSeqNumber()>buffers.get(lastRightIndex-1).getSeqNumber())
        			{
        				buffers.add(lastRightIndex, rtpPacket);
        				break;
        			}
            	}
            }
            if (!ready && buffers.size() >= this.maxSize/2) {
                ready = true;
            }
            
            if(this.gatherer.isGatherStats())
            {
            	long stamp=System.currentTimeMillis();
            	interArrivalJitter=(int)(interArrivalJitter+stamp-lastArrivalTimeStamp)/2;
            	lastArrivalTimeStamp=stamp;
            	this.gatherer.interArrivalJitter(interArrivalJitter);
            }else
            {
            	interArrivalJitter=0;
            }

        } finally {
            state.unlock();
        }
    }

    /**
     * Reads media packet from jitter buffer.
     * 
     * @return media packet.
     */
    public Buffer read() {
        
       

        //read next packet and fill Buffer object.
        try {
        	//buffer should be fully populated before first read will be available
            state.lock();
            if (!ready) {
                return null;
            }
            if (buffers.size() > 0) {
                //takes top rtp packet
                RtpPacket rtpPacket = buffers.remove(0);
            	

                //allocate media buffer
                Buffer buff = new Buffer();//CachedBuffersPool.allocate();

                //fill media buffer 
                buff.setSequenceNumber(seq);
                buff.setTimeStamp(seq * period);
                buff.setDuration(period);

                HashMap<Integer, Format> formats = rtpSocket.getRtpMap();
                Format fmt = (Format) formats.get(rtpPacket.getPayloadType());

                buff.setFormat(fmt);
                buff.setData(rtpPacket.getPayload());
                //byte[] data = (byte[]) buff.getData();
                //byte[] payload = rtpPacket.getPayload();

                //System.arraycopy(payload, 0, data, 0, payload.length);

                buff.setOffset(0);
                buff.setLength(rtpPacket.getPayload().length);
                
                seq++;
               
                if(this.gatherer.isGatherStats())
                {
                	this.gatherer.octetsReceived(rtpPacket.getPayload().length);
                	this.gatherer.packetsReceived(1);
                	if(this.lastConsumedSeq==0)
                	{
                		
                	}else
                	{
                		//FIXME: add check on last 5 seq#
                		// -1 cause 3-2=1, but its a zero loss
                		this.gatherer.packetsLost(rtpPacket.getSeqNumber()-1-this.lastConsumedSeq);
                		
                	}
                	
                }
                this.lastConsumedSeq=rtpPacket.getSeqNumber();
                return buff;
            }

            return null;
        } finally {
            state.unlock();
        }
    }
    
  
    
    
}
