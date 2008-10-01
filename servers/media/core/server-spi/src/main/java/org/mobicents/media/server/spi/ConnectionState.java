package org.mobicents.media.server.spi;

public enum ConnectionState {

    NULL("NULL"), HALF_OPEN("HALF_OPEN"), OPEN("OPEN"), CLOSED("CLOSED");

    private ConnectionState(String stateName) {
        this.stateName = stateName;
    }
    
    @Override
    public String toString() {
        return stateName;
    }
    
    private String stateName;
}
