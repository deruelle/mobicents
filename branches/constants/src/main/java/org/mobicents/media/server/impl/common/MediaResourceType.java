package org.mobicents.media.server.impl.common;

public enum MediaResourceType {

    AUDIO_SOURCE("AUDIO_SOURCE"), AUDIO_SINK("AUDIO_SINK"),
    DTMF_DETECTOR("DTMF_DETECTOR"), DTMF_GENERATOR("DTMF_GENERATOR");

    private MediaResourceType(String name) {
        this.name = name;
    }
    
    private String name;
}
