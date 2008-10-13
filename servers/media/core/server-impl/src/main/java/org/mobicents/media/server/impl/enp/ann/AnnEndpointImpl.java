/*
 * AnnEndpointImpl.java
 *
 *
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
package org.mobicents.media.server.impl.enp.ann;

import java.util.HashMap;
import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.BaseVirtualEndpoint;
import org.mobicents.media.server.impl.Generator;
import org.mobicents.media.server.impl.events.announcement.AudioPlayer;
import org.mobicents.media.server.spi.Endpoint;


/**
 * Implements Announcement access point.
 * 
 * @author Oleg Kulikov
 */
public class AnnEndpointImpl extends BaseVirtualEndpoint {
    
    //private transient Logger logger;
    
    /**
     * Creates a new instance of AnnEndpointImpl
     * 
     * @param localName the local name of the endpoint.
     * @param endpointsMap 
     */
    public AnnEndpointImpl(String localName, HashMap<String, Endpoint> endpointsMap) {
        super(localName,endpointsMap);
        this.setMaxConnectionsAvailable(1);
        
        logger = Logger.getLogger(AnnEndpointImpl.class);
    }


    @Override
    public Endpoint doCreateEndpoint(String localName) {
        return new AnnEndpointImpl(localName,super.endpoints);
    }

    @Override
    public HashMap initMediaSources() {
        HashMap map = new HashMap();
        
        //init audio player
        map.put(Generator.AUDIO_PLAYER, new AudioPlayer());
        
        return map;
    }

    @Override
    public HashMap initMediaSinks() {
        return new HashMap();
    }


}
