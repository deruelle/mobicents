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
package org.mobicents.media.server.impl.resource.echo;

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.AbstractSource;

/**
 *
 * @author kulikov
 */
public class EchoSource extends AbstractSource {
    protected EchoSink sink;
    private Format[] supported = new Format[0];
    
    public EchoSource(String name) {
        super(name);
        sink = new EchoSink("inner." + name, this);
    }
    
    public EchoSource(String name, EchoSink sink) {
        super(name);
        this.sink = sink;
    }

    public void start() {
    }

    public void stop() {
    }

    public Format[] getFormats() {
        return supported;
    }
    
    protected void delivery(Buffer buffer)  {
        if (otherParty != null) {
            otherParty.receive(buffer);
        }
    }
}
