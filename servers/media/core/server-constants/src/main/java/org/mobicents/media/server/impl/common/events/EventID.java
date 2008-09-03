package org.mobicents.media.server.impl.common.events;

public enum EventID {

    IDLE(""),
    PLAY("org.mobicents.media.ann.PLAY"),
    COMPLETE("org.mobicents.media.ann.COMPLETE"),
    FAIL("org.mobicents.media.ann.FAIL"),
    PLAY_RECORD("org.mobicents.media.au.PLAY_RECORD"),
    PROMPT_AND_COLLECT("org.mobicents.media.au.PROMPT_AND_COLLECT"),
    DTMF("org.mobicents.media.dtmf.DTMF"),
    TEST_SPECTRA("org.mobicents.media.test.TEST_SPECTRA"),
    TEST_SINE("org.mobicents.media.test.TEST_SINE"),
    INVALID("");
    private String eventID;

    private EventID(String eventID) {
        this.eventID = eventID;
    }

    public static EventID getEvent(String eventID) {
        String tokens[] = eventID.split("\\.");
        return Enum.valueOf(EventID.class, tokens[tokens.length - 1]);
    }
    
    @Override
    public String toString() {
        return eventID;
    }
}
