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

package org.mobicents.mscontrol.impl;

import org.mobicents.mscontrol.events.MsEventFactory;
import org.mobicents.mscontrol.events.MsRequestedEvent;
import org.mobicents.mscontrol.events.MsRequestedSignal;
import org.mobicents.mscontrol.impl.events.DefaultRequestedEvent;

/**
 *
 * @author Oleg Kulikov
 */
public class MsEventFactoryImpl implements MsEventFactory {
    private String toCapital(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1, text.length()).toLowerCase();
    }
    
    private String className(String eventID, String suffix) {
        eventID = eventID.replace("media.events", "mscontrol.impl.events");
        String tokens[] = eventID.split("\\.");
        
        String packageName = "";
        for (int i = 0; i < tokens.length - 1; i++) {
            packageName += tokens[i] + ".";
        }
        
        String cls = toCapital(tokens[tokens.length - 1]) + suffix;
        return packageName + cls;
    }
    
    public MsRequestedSignal createRequestedSignal(String signalID) throws ClassNotFoundException {
        String className = this.className(signalID, "RequestedSignalImpl");
        ClassLoader classLoader = MsEventFactoryImpl.class.getClassLoader();
        Class cls = classLoader.loadClass(className);
        try {
            return (MsRequestedSignal) cls.newInstance();
        } catch (Exception e) {
            return null;
        }
    }
    
    public MsRequestedEvent createRequestedEvent(String eventID) throws ClassNotFoundException {
        String className = this.className(eventID, "RequestedEventImpl");
        ClassLoader classLoader = MsEventFactoryImpl.class.getClassLoader();
        Class cls = null;
        try {
            cls = classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            cls = classLoader.loadClass("org.mobicents.mscontrol.impl.events.DefaultRequestedEvent");
        }
        
        MsRequestedEvent evt = null; 
        try {
            evt = (MsRequestedEvent) cls.newInstance();
        } catch (Exception e) {
            return null;
        }
        
        if (evt instanceof DefaultRequestedEvent) {
            ((DefaultRequestedEvent) evt).setID(eventID);
        }
        
        return evt;
    }
    
}
