/*
 * Media4c.java
 *
 * Created on 20 de Julho de 2004, 12:34
 */

package com.ptin.xmas.control.common;

import java.util.*;


/**
 *
 * @author  4023742
 */

public class MediaData {
    
    protected Hashtable media = null;
    
    /**
     * <p>
     * Represents ...
     * </p>
     */
    public static final String AUDIO_MEDIA_CAPTURE = "AUDIO_MEDIA_CAPTURE";
    
    /**
     * <p>
     * Represents ...
     * </p>
     */
    public static final String AUDIO_MEDIA_RENDER = "AUDIO_MEDIA_RENDER";
    
    /**
     * <p>
     * Represents ...
     * </p>
     */
    public static final String AUDIO_VIDEO_MEDIA_CAPTURE = "AUDIO_VIDEO_MEDIA_CAPTURE";
    
    /**
     * <p>
     * Represents ...
     * </p>
     */
    public static final String AUDIO_VIDEO_MEDIA_RENDER = "AUDIO_VIDEO_MEDIA_RENDER";
    
    public static final String AVAILABLE_MEDIA = "AVAILABLE_MEDIA";
    
    public static final String MEDIA_DESCRIPTION = "description";
    
    public static final String MEDIA_TYPE = "type";
    
    public static final String NO_MEDIA = "noMedia";
    
    public static final String REMOVE_MEDIA_COMMAND = "remove";
    
    public static final String SET_MEDIA_COMMAND = "set";
    
    public static final int FEATURE_LISTEN_MEDIA = 13;
  
    public static final int FEATURE_TRANSMIT_MEDIA = 14;

    public static final int EVENT_MEDIA_STARTED = 15;
    
    public static final int EVENT_MEDIA_STOPPED = 16;
    
    public static final String ACTION_MEDIA_PLAY_CURRENT = "current";
    
    public static final String ACTION_MEDIA_PLAY_NEXT = "next";
    
    public static final String ACTION_MEDIA_PLAY_PREVIOUS = "previous";
    
    public static final String ACTION_MEDIA_PLAY_ROOT = "root";
    
    public static final String ACTION_MEDIA_PLAY = "play";

    public static final String ACTION_MEDIA_STOP = "stop";
    
    public static final String ACTION_MEDIA_READY = "ready";
    
    public static final String ACTION_MEDIA_UNAVAILABLE = "unavailable";

    public static final String ACTION_MEDIA_PAUSE = "pause";
    
    public static final String ACTION_MEDIA_TONE_1 = "1";
    
    public static final String ACTION_MEDIA_TONE_2 = "2";
    
    public static final String ACTION_MEDIA_TONE_3 = "3";
    
    public static final String ACTION_MEDIA_TONE_4 = "4";
    
    public static final String ACTION_MEDIA_TONE_5 = "5";
    
    public static final String ACTION_MEDIA_TONE_6 = "6";
    
    public static final String ACTION_MEDIA_TONE_7 = "7";
    
    public static final String ACTION_MEDIA_TONE_8 = "8";
    
    public static final String ACTION_MEDIA_TONE_9 = "9";
    
    public static final String ACTION_MEDIA_TONE_0 = "0";
    
    public static final String ACTION_MEDIA_TONE_ASTERISK = "*";
    
    public static final String ACTION_MEDIA_TONE_POUND = "#";
    
    /**
     * <p>
     * Represents ...
     * </p>
     */
    public static final String VIDEO_MEDIA_CAPTURE = "VIDEO_MEDIA_CAPTURE";
    
    public static final String MY_OWN_AUDIO_MEDIA_ADDRESS = "TAGARELA VOX";
    
    public static final String MY_OWN_VIDEO_MEDIA_ADDRESS = "TAGARELA VIDEO";
    
    private boolean mediaAvailability = false;
    
    /**
     * <p>
     * Represents ...
     * </p>
     */
    public static final String VIDEO_MEDIA_RENDER = "VIDEO_MEDIA_RENDER";
    
    /** Creates a new instance of Media4c */
    
    
    public MediaData() {
        media = new Hashtable();
    }
    
    public void setMedia(String mediaType, String mediaDescription){
        media.put(mediaType,mediaDescription);
    }
    
    public void setMediaAvailability(boolean availability){
    mediaAvailability = availability;
    }
    
    public boolean isReady(){
    return mediaAvailability;
    }

    public void removeMedia(String mediaType){
        media.remove(mediaType);
    }
    
    public String getMedia(String mediaType){
        
        if (media.containsKey(mediaType) )
            return (String)media.get(mediaType);
        else
            return MediaData.NO_MEDIA;
    }
    
    public boolean isEmpty(){
        return media.isEmpty();
    }
    
    public boolean isMediaSupported(String sdp){
        
        String type = getMediaType(sdp);
        
        return isMediaTypeSupported(type);
        
    }
    
    public boolean isMediaTypeSupported(String mediaType){
        if ( !getMedia(mediaType ).equals(MediaData.NO_MEDIA) )
            return true;
        else
            return false;
    }
    
    public String getMediaType(String sdp){
        if (sdp.indexOf("audio") != -1 && sdp.indexOf("video") != -1 )
            return MediaData.AUDIO_VIDEO_MEDIA_RENDER;
        else if ((sdp.indexOf("audio") != -1) )
            return MediaData.AUDIO_MEDIA_RENDER;
        else if ((sdp.indexOf("video") != -1) )
            return MediaData.VIDEO_MEDIA_RENDER;
        else
            return MediaData.NO_MEDIA;
            
        }
    
    public String getMediaType4OwnMediaAddress(String address){
        
        System.out.println("[MediaData.getMediaType4OwnMediaAddress] for "+address);
        
        if (address.equals(MediaData.MY_OWN_AUDIO_MEDIA_ADDRESS ))
            return MediaData.AUDIO_MEDIA_RENDER;
        else if (address.equals(MediaData.MY_OWN_VIDEO_MEDIA_ADDRESS ))
            return MediaData.AUDIO_VIDEO_MEDIA_RENDER;
        else
            return MediaData.NO_MEDIA;
        
        }

    
    // /pch: gera Enumeration c/ 1 xml por cada tipo de media e respectiva descrição sdp
   // <MediaData.MEDIA_TYPE>key</MediaData.MEDIA_TYPE><MediaData.MEDIA_DESCRIPTION>value</MediaData.MEDIA_MEDIA_DESCRIPTION>
    
    public Enumeration toXml() {
        Enumeration mediaTypes = media.keys();
        Vector mediaList = new Vector();
        
        while (mediaTypes.hasMoreElements()){
            String mediaType = (String)mediaTypes.nextElement();
            String media = getMedia(mediaType);
            mediaList.add("<"+MediaData.MEDIA_TYPE+">"+mediaType+"</"+MediaData.MEDIA_TYPE+"><"+MediaData.MEDIA_DESCRIPTION+">"+media+"</"+MediaData.MEDIA_DESCRIPTION+">");
            
        }
        return mediaList.elements();
    }    
    
}
