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
package org.mobicents.media.server.impl.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.Inlet;
import org.mobicents.media.MediaSink;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.spi.Connection;

/**
 * A Demultiplexer is a media processing component that takes an interleaved 
 * media stream as input, extracts the individual tracks from the stream, and 
 * outputs the resulting tracks. It has one input and multiple outputs.
 * 
 * @author Oleg Kulikov
 */
public class Demultiplexer extends AbstractSource implements Inlet {

    private Format[] outputFormats = new Format[0];
    private Input input = null;
    private Map<String, Output> branches = new ConcurrentHashMap<String, Output>();
    private Buffer buff;

    /**
     * Creates new instance of the demultiplexer.
     * 
     * @param name
     */
    public Demultiplexer(String name) {
        super(name);
        input = new Input(name);
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.Inlet#getInput(). 
     */
    public AbstractSink getInput() {
        return input;
    }

    /**
     * This method allows to access identifier from the inner source 
     * and sink implemetation.
     * 
     * @return the unique identifer of this component.
     */
    private String getIdentifier() {
        return getId();
    }

    @Override
    public void setConnection(Connection connection) {
        super.setConnection(connection);
        input.setConnection(connection);
        for (Output out : branches.values()) {
            out.setConnection(connection);
        }
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.MediaSource#getFormats().
     */
    public Format[] getFormats() {
        return input.getOtherPartyFormats();
    }

    @Override
    public void connect(MediaSink sink) {
        Output out = new Output(getName());
        out.setConnection(getConnection());
        branches.put(sink.getId(), out);
        out.connect(sink);
    }

    @Override
    public void disconnect(MediaSink sink) {
        //remove reference from the branches list
        Output out = (Output) branches.remove(sink.getId());
        //disconnect and clean references to the removed output      
        if (out != null) {
            out.disconnect(sink);
            out.setConnection(null);
            out.setEndpoint(null);
        }
    }

    /**
     * Reassemblies the list of used formats. This method is called each time
     * when connected/disconnected source
     */
    private void reassemblyFormats() {
        ArrayList list = new ArrayList();
        Collection<Output> outputs = branches.values();
        for (Output output : outputs) {
            Format[] fmts = output.getOtherPartyFormats();
            for (Format format : fmts) {
                if (!list.contains(format)) {
                    list.add(format);
                }
            }
        }

        outputFormats = new Format[list.size()];
        list.toArray(outputFormats);
    }

    @Override
    public void evolve(Buffer buffer, long sequenceNumber) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Returns the number of outputs.
     * 
     * @return the number of active outputs.
     */
    public int getBranchCount() {
        return branches.size();
    }

    @Override
    public void start() {
        input.start();
    }
    
    @Override
    public void stop() {
        input.stop();
    }
    /**
     * Implements input stream of the Demultiplxer.
     * 
     */
    private class Input extends AbstractSink {

        /**
         * Creates new instance of input stream.
         * 
         * The name of the demultiplxer.
         */
        public Input(String name) {
            super("input." + name);
        }

        /**
         * (Non Java-doc).
         * 
         * @see org.mobicents.media.MediaSink#isAcceptable(org.mobicents.media.Format) 
         */
        public boolean isAcceptable(Format fmt) {
            Collection<Output> list = branches.values();
            for (Output output : list) {
                if (output.isAcceptable(fmt)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Reads supported formats from other party if connected.
         * 
         * @return if other party connected returns array of supported formats
         * or empty array if not connected.
         */
        protected Format[] getOtherPartyFormats() {
            return otherParty != null ? otherParty.getFormats() : new Format[0];
        }

        /**
         * (Non Java-doc).
         * 
         * @see org.mobicents.media.server.impl.AbstractSink#onMediaTransfer(org.mobicents.media.Buffer). 
         */
        public void onMediaTransfer(Buffer buffer) throws IOException {
            buff = buffer;
            Collection<Output> streams = branches.values();
            for (Output output : streams) {
                output.run();
            }
            buffer.dispose();
        }

        /**
         * (Non Java-doc).
         * 
         * @see org.mobicents.media.MediaSink#getFormats().
         */
        public Format[] getFormats() {
            reassemblyFormats();
            return outputFormats;
        }
    }

    /**
     * Implements output stream.
     */
    private class Output extends AbstractSource {

        public Output(String parent) {
            super("output." + parent);
        }

        @Override
        public String getId() {
            return getIdentifier();
        }

        @Override
        public void start() {
        }

        @Override
        public void stop() {
        }

        /**
         * Checks is other party supports specofied format.
         * 
         * @param fmt the format to check
         * @return true if other party supports this format.
         */
        public boolean isAcceptable(Format fmt) {
            return otherParty != null && otherParty.isAcceptable(fmt);
        }

        /**
         * Gets list of formats supported by other party.
         * 
         * @return array of formats or empty array if not connected yet.
         */
        public Format[] getOtherPartyFormats() {
            return otherParty != null ? otherParty.getFormats() : new Format[0];
        }

        /**
         * (Non Java-doc).
         * 
         * @see org.mobicents.media.MediaSource#getFormats() 
         */
        public Format[] getFormats() {
            return input.getOtherPartyFormats();
        }

        @Override
        public void evolve(Buffer buffer, long sequenceNumber) {
            buffer.copy(buff);
        }
    }
}
