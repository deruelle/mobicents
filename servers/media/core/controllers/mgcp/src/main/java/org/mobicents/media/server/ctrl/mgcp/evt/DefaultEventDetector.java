/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package org.mobicents.media.server.ctrl.mgcp.evt;

import jain.protocol.ip.mgcp.message.parms.RequestedAction;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 *
 * @author kulikov
 */
public class DefaultEventDetector extends EventDetector {

    private ActionNotify actionNotify;
    
    public DefaultEventDetector(String pkgName, String eventName, 
            String resourceName, int eventID, String params, RequestedAction[] actions) {
        super(pkgName, eventName, resourceName, eventID, params, actions);
    }

    @Override
    public void performAction(NotifyEvent event, RequestedAction action) {
        if (!event.getResourceName().matches(this.getResourceName())) {
            return;
        }
        
        if (event.getEventID() != this.getEventID()) {
            return;
        }

        //@TODO implement action selector
        getRequest().sendNotify(this.getEventName());
        
    }

    
}
