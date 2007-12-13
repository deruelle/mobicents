/*
 * ConfMediaContextImpl.java
 *
 * The Simple Media API RA
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

package org.mobicents.slee.resource.media.ra.conf;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentReaderHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import javax.media.Manager;
import javax.media.protocol.DataSource;
import org.apache.log4j.Logger;
import org.mobicents.slee.resource.media.ra.BaseMediaConnection;
import org.mobicents.slee.resource.media.ra.BaseMediaContext;
import org.mobicents.slee.resource.media.ra.MediaContextErrorEventImpl;
import org.mobicents.slee.resource.media.ra.MediaContextEventImpl;
import org.mobicents.slee.resource.media.ra.MediaResourceAdaptor;
import org.mobicents.slee.resource.media.ratype.Cause;
import org.mobicents.slee.resource.media.ratype.ConfMediaContext;
import org.mobicents.slee.resource.media.ratype.MediaConnection;
import org.mobicents.slee.resource.media.ratype.MediaContext;
import org.mobicents.slee.resource.media.ratype.MediaContextEvent;

/**
 *
 * @author Oleg Kulikov
 */
public class ConfMediaContextImpl extends BaseMediaContext implements ConfMediaContext {
    
    private MediaResourceAdaptor ra;
    protected ConcurrentReaderHashMap parties = new ConcurrentReaderHashMap();
    private Logger logger = Logger.getLogger(ConfMediaContextImpl.class);
    
    /** Creates a new instance of ConfMediaContextImpl */
    public ConfMediaContextImpl(MediaResourceAdaptor ra) {
        this.ra = ra;
    }
    
    public void add(MediaConnection connection) throws IllegalStateException {
        ConfParty confParty = new ConfParty(this, (BaseMediaConnection)connection);
        parties.put(confParty.getId(), confParty);

        if (logger.isDebugEnabled()) {
            logger.debug("Added new party: " + confParty.getId() + ",count=" + parties.size() + ",Transcoding input stream to local format");
        }
        confParty.transcodeInputStream();
    }
        
    private DataSource[] getInputStreams(ConfParty confParty) {
        DataSource[] streams = new DataSource[parties.size() - 1];
        
        Iterator list = parties.values().iterator();
        int i = 0;
        
        while (list.hasNext()) {
            ConfParty otherParty = (ConfParty) list.next();
            if (otherParty != confParty) {
                streams[i++] = confParty.getInputStream();
                logger.debug("PartyID= " + confParty.getId() + ", append for multiplexing " + otherParty.getId());
            }
        }
        
        return streams;
    }
    
    protected void mux() {
        if (logger.isDebugEnabled()) {
            logger.debug("total parties: " + parties.size());
        }
        if (parties.size() == 1) {
            return;
        }
        
        Iterator list = parties.values().iterator();
        while (list.hasNext()) {
            ConfParty confParty = (ConfParty) list.next();
            DataSource[] streams = getInputStreams(confParty);
            if (logger.isDebugEnabled()) {
                logger.debug("Multiplexing  " + streams.length + " streams for party " + confParty.getId());
            }
            try {
                DataSource muxStream = Manager.createMergingDataSource(streams);
                confParty.setOutputStream(muxStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void subtract(MediaConnection connection) throws IllegalStateException, IllegalArgumentException {
        parties.remove(connection.getId());
        mux();
    }
    
    public void add(MediaContext context) throws IllegalStateException {
    }
    
    public void subtract(MediaContext context) throws IllegalStateException, IllegalArgumentException {
    }
    
    public Collection getConnections() {
        ArrayList list = new ArrayList();
        Iterator it = parties.values().iterator();
        while (it.hasNext()) {
            ConfParty confParty = (ConfParty) it.next();
            list.add(confParty.connection);
        }
        return list;
    }
 
    protected void notifyAdded(ConfParty confParty) {
        MediaContextEvent evt = new MediaContextEventImpl(this, Cause.NORMAL);
        ra.fireConnectionAttachedEvent(evt);
    }
    
    protected void notifyRemoved(ConfParty confParty) {
        MediaContextEvent evt = new MediaContextEventImpl(this, Cause.NORMAL);
        ra.fireConnectionDetachedEvent(evt);
    }
    
    protected void notifyFailed(ConfParty confParty, int cause,String msg) {
        MediaContextEvent evt = new MediaContextErrorEventImpl(this, Cause.NORMAL,msg);
        ra.fireActionErrorEvent(evt);
    }
}
