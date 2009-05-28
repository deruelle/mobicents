/**
 * Mobicents Media Gateway
 *
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */
package org.mobicents.media.server.impl.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSource;
import org.mobicents.media.Outlet;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.spi.Connection;

/**
 * Combines several signals for transmission over a single medium. A
 * demultiplexor completes the process by separating multiplexed signals from a
 * transmission line. Frequently a multiplexor and demultiplexor are combined
 * into a single device capable of processing both outgoing and incoming
 * signals.
 * <br>Multiplexer combines data and sends them, it is used as output for components.
 * @author Oleg Kulikov
 */
public class Multiplexer extends AbstractSink implements Outlet {

    private Format[] outputFormats = null;
    private final static Format[] inputFormats = new Format[0];
    

    private Map<String, Input> inputs = new ConcurrentHashMap<String, Input>();
    private Output output;
    private int seq = 0;

    public Multiplexer(String name) {
        super(name);
        output = new Output(name + ".Output");
    }

    public MediaSource getOutput() {
        return output;
    }

    public Format[] getFormats() {
        return inputFormats;
    }

    @Override
    public void setConnection(Connection connection) {
        super.setConnection(connection);
        output.setConnection(connection);
    }
    
    @Override
    public void connect(MediaSource source) {
    	if(inputs.containsKey(((AbstractSource) source).getId()))
    	{
    		IllegalStateException e =new IllegalStateException("Cannot connect source - its already connected - this: "+this+", source: "+source);
    		e.printStackTrace();
    		throw e;
    	}
        Input input = new Input(getName() + ".Input");
        source.connect(input);
        inputs.put(((AbstractSource) source).getId(), input);
        reassemblyFormats();
    }

    @Override
    public void disconnect(MediaSource source) {
    	//FIXME: add throw on null
    	  Input input = inputs.remove(((AbstractSource) source).getId());
          if (input != null) {
              source.disconnect(input);

              reassemblyFormats();
          }
    }

    @Override
	public void disconnect(MediaSource otherParty, boolean doCallOther) {
    	Input input = inputs.remove(((AbstractSource) otherParty).getId());
        if (input != null) {
        	if(doCallOther)
        		((AbstractSource) otherParty).disconnect(input,false);

            reassemblyFormats();
        }
	}

	@Override
	public boolean isConnected(MediaSource source) {
		
		return inputs.containsKey(((MediaSource) source).getId());
	}

    /**
     * Reassemblies the list of used formats. This method is called each time
     * when connected/disconnected source
     */
    private void reassemblyFormats() {
        ArrayList list = new ArrayList();
        Collection<Input> sources = inputs.values();
        for (Input input : sources) {
            Format[] fmts = input.getOtherPartyFormats();
            for (Format format : fmts) {
                if (!list.contains(format)) {
                    list.add(format);
                }
            }
        }

        outputFormats = new Format[list.size()];
        list.toArray(outputFormats);
    }

    public boolean isAcceptable(Format fmt) {
        return true;
    }

    public void receive(Buffer buffer) {
    }

    class Input extends AbstractSink {

        public Input(String name) {
            super(name);
        }

        public boolean isAcceptable(Format fmt) {
            return true;
        }

        public void receive(Buffer buffer) {
            deliver(buffer);
        }

        public Format[] getFormats() {
            return inputFormats;
        }
        
        protected Format[] getOtherPartyFormats() {
            return otherParty.getFormats();
        }

		@Override
		public void disconnect(MediaSource otherParty, boolean doCallOther) {
			
			//Yes , we overide it, its bad, but its a bit faster not to perform twice  the same check :)
			if (this.otherParty != null) {
				
				// ((AbstractSource) otherParty).otherParty = null;
				if (((AbstractSource) otherParty).isConnected(this)) {
					//we need to inform other side so it can perform its task, but let it not call us, we already know of disconnect
					if(doCallOther)
						((AbstractSource) otherParty).disconnect(this,false);
					
					Input src = inputs.remove(((AbstractSource) otherParty).getId());
					
					this.otherParty = null;
				} else {
					//throw new IllegalArgumentException("Disconnect. Other party does not match. Local: " + this.otherParty + ", passed: " + otherParty);
					//System.err.println("Disconnect["+this+"]. Other party does not match. Local: " + this.otherParty + ", passed: " + otherParty);
				}

			}
		}
        
    }

    class Output extends AbstractSource {

        private volatile boolean stopped = true;

        public Output(String name) {
            super(name);
        }

        protected boolean isConnected() {
            return otherParty != null;
        }
        
        public void start() {
            stopped = false;
        }

        public void stop() {
            stopped = true;
        }

        public Format[] getFormats() {
            return outputFormats;
        }
        
        protected void deliver(Buffer buffer) {
            otherParty.receive(buffer);
        }
    }

    public synchronized void deliver(Buffer buffer) {
        if (!output.stopped && output.isConnected()) {
            buffer.setSequenceNumber(seq);
            buffer.setTimeStamp(seq * 20);
            output.deliver(buffer);
        } else {
            buffer.dispose();
        }

        seq++;
    }

}

