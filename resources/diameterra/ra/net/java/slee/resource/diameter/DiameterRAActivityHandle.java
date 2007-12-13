package net.java.slee.resource.diameter;

import javax.slee.resource.ActivityHandle;

import org.apache.log4j.Logger;

public class DiameterRAActivityHandle implements ActivityHandle {
    private static Logger logger = Logger.getLogger(DiameterResourceAdaptor.class);

    private String handle = null;

    public DiameterRAActivityHandle(String id) {
        logger.debug("DiameterRAActivityHanlde(" + id + ") called.");
        this.handle = id;
    }

    // ActivityHandle interface
    public boolean equals(Object obj) {
        if (obj instanceof DiameterRAActivityHandle) {
            return handle.equals(((DiameterRAActivityHandle) obj).handle);
        } else
            return false;
    }

    // ActivityHandle interface
    public int hashCode() {
        return handle.hashCode();
    }
}
