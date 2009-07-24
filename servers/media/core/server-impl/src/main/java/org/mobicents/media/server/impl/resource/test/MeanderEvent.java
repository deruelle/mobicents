/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.resource.test;

import org.mobicents.media.server.impl.BaseComponent;
import org.mobicents.media.server.impl.NotifyEventImpl;

/**
 *
 * @author kulikov
 */
public class MeanderEvent extends NotifyEventImpl {

    public final static int EVENT_MEANDER = 0;
    public final static int EVENT_OUT_OF_SEQUENCE = 1;
    public final static int EVENT_FORMAT_MISSMATCH = 2;
    
    public  MeanderEvent(BaseComponent component, int eventID) {
        super(component, eventID);
    }
    
}
