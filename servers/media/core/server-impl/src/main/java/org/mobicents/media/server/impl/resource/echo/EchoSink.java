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

import java.io.IOException;

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.AbstractSink;

/**
 *
 * @author kulikov
 */
public class EchoSink extends AbstractSink {
    protected EchoSource source;
    private Format[] supported = new Format[0];
    
    public EchoSink(String name) {
        super(name);
        source = new EchoSource("inner." + name, this);
    }
    
    public EchoSink(String name, EchoSource source) {
        super(name);
        this.source = source;
    }

    public Format[] getFormats() {
        return supported;
    }

    public boolean isAcceptable(Format format) {
        return true;
    }

    public void onMediaTransfer(Buffer buffer) throws IOException {
        source.delivery(buffer);
    }
}
