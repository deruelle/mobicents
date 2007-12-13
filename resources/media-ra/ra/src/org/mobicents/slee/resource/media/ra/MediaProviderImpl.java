/***************************************************
 *                                                 *
 *  Mobicents: The Open Source VoIP Platform       *
 *                                                 *
 *  Distributable under GPL license.              *
 *  See terms of license at gnu.org.               *
 *                                                 *
 ***************************************************/
package org.mobicents.slee.resource.media.ra;

import javax.slee.resource.ActivityAlreadyExistsException;
import javax.slee.resource.CouldNotStartActivityException;

import org.apache.log4j.Logger;
import org.mobicents.slee.resource.media.ra.ivr.AnnouncementContextImpl;
import org.mobicents.slee.resource.media.ra.ivr.IVRContextImpl;
import org.mobicents.slee.resource.media.ra.rtp.RtpMediaConnectionImpl;
import org.mobicents.slee.resource.media.ratype.MediaConnection;
import org.mobicents.slee.resource.media.ratype.MediaContext;
import org.mobicents.slee.resource.media.ratype.MediaProvider;
import org.mobicents.slee.resource.media.ratype.MediaSession;
/**
 *
 * Provides SBBs with an interface for creating Media Session activities
 *
 * @author torosvi
 * @author Ivelin Ivanov
 * @author Oleg Kulikov
 */
public class MediaProviderImpl implements MediaProvider {
    private static Logger logger = Logger.getLogger(MediaProviderImpl.class);
    
    // Reference to the Media Resource Adaptor
    private MediaResourceAdaptor mediaRa;
    
    // Contructor
    public MediaProviderImpl(MediaResourceAdaptor ra) {
        this.mediaRa = ra;
    }
    
    public MediaResourceAdaptor getMediaRA() {
        logger.debug("getMediaRA() called.");
        
        return mediaRa;
    }
    
    
    /**
     * SBBs invoke this method to create a new Media Session.
     */
    public MediaSession getNewMediaSession() {
        
        MediaSession mediaSessionActivity = new MediaSessionImpl(mediaRa);
        
        try {
            // Generate the activity handle which uniquely identifies the
            // appropriate activity context
            MediaActivityHandle handle = new MediaActivityHandle(mediaSessionActivity.getSessionId());
            
            mediaRa.getSleeEndpoint().activityStartedSuspended(handle);
        } catch (ActivityAlreadyExistsException e) {
            logger.warn("Failed to create new media session", e);
        } catch (CouldNotStartActivityException e) {
            logger.warn("Failed to create new media session", e);
        }
        return mediaSessionActivity;
    }
    
    /**
     * SBBs invoke this method to create a new Media Session.
     */
    public MediaSession getNewMediaSession(String sdp) {
        
        MediaSession mediaSessionActivity = new MediaSessionImpl(mediaRa, sdp);
        
        try {
            // Generate the activity handle which uniquely identifies the
            // appropriate activity context
            MediaActivityHandle handle = new MediaActivityHandle(mediaSessionActivity.getSessionId());
            mediaRa.getSleeEndpoint().activityStartedSuspended(handle);
        } catch (ActivityAlreadyExistsException e) {
            logger.warn("Failed to create new media session", e);
        } catch (CouldNotStartActivityException e) {
            logger.warn("Failed to create new media session", e);
        }
        
        return mediaSessionActivity;
    }
    
    public MediaConnection createConnection(int topology) {
        switch (topology) {
            case MediaConnection.RTP :
                return new RtpMediaConnectionImpl(mediaRa);
            default :
                return null;
        }
    }
    
}