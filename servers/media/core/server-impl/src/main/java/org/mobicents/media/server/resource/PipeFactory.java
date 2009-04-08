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

package org.mobicents.media.server.resource;

/**
 * Used as a factory class for creating inner pipes dynamic upon request
 * using assigned inlet's and outlet's factrories;
 * 
 * @author kulikov
 */
public class PipeFactory {
    
    private String inlet;
    private String outlet;

    public String getInlet() {
        return inlet;
    }

    public String getOutlet() {
        return outlet;
    }

    public void setInlet(String inlet) {
        this.inlet = inlet;
    }

    public void setOutlet(String outlet) {
        this.outlet = outlet;
    }

    public void openPipe(Channel channel) throws UnknownComponentException {
        Pipe pipe = new Pipe(channel);
        pipe.open(inlet, outlet);
    }
}
