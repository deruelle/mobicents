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
package org.mobicents.media.server.impl.resource.zap;

import java.io.RandomAccessFile;
import org.mobicents.media.Component;
import org.mobicents.media.ComponentFactory;
import org.mobicents.media.Format;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.ResourceUnavailableException;

/**
 *
 * @author kulikov
 */
public class BChannelFactory implements ComponentFactory {

    private int span;
    private int timeslot;
    
    private RandomAccessFile file;
    private Format format;
    
    public int getSpan() {
        return span;
    }
    
    public void setSpan(int span) {
        this.span = span;
    }
    
    public int getTimeslot() {
        return timeslot;
    }
    
    public void setTimeslot(int timeslot) {
        this.timeslot = timeslot;
    }
    
    public Format getFormat() {
        return format;
    }
    
    public void setFormat(Format format) {
        this.format = format;
    }
    
    public Component newInstance(Endpoint endpoint) throws ResourceUnavailableException {
        String devName = "/dev/zap/" + (31*span - 29 + timeslot);
        BChannel bchannel = new BChannel(devName);
        bchannel.setFormat(format);
        bchannel.getSource().setSyncSource(endpoint.getTimer());
        return bchannel;
    }

}
