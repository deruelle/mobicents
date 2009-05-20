/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.resource;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.Buffer;
import static org.junit.Assert.*;
import org.mobicents.media.Format;
import org.mobicents.media.Utils;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.AbstractSource;

/**
 *
 * @author kulikov
 */
public class DemultiplexerTest {

    private final static Format f1 = new AudioFormat("f1");
    private final static Format f2 = new AudioFormat("f2");
    
    private final static Format[] formats = new Format[]{f1, f2};
    
    private Demultiplexer demux;
    private LocalMediaSink sink1;
    private LocalMediaSink sink2;
    private LocalMediaSource source;
    public DemultiplexerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        demux = new Demultiplexer("test");
        sink1 = new LocalMediaSink("sink1");
        sink2 = new LocalMediaSink("sink2");
        source = new LocalMediaSource("source");
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getFormats method, of class Demultiplexer.
     */
    @Test
    public void testInputFormats() {
        Format[] supported = demux.getInput().getFormats();
        assertEquals(0, supported.length);
    }

    @Test
    public void testOutputFormats() {
        Format[] supported = demux.getFormats();
        assertEquals(0, supported.length);
        
        demux.getInput().connect(new Source1(""));
        supported = demux.getFormats();
        
        assertEquals(2, supported.length);
        assertEquals(true, Utils.checkFormats(formats, supported));
    }
    
    /**
     * Test of connect method, of class Demultiplexer.
     */
    @Test
    public void testConnect() {
    	demux.connect(sink1);
    	demux.connect(sink2);
    	Buffer toSend = new Buffer();
    	toSend.setData(new byte[255]);
    	toSend.setFormat(f1);
    	demux.getInput().connect(this.source);
    	demux.start();
    	this.source.doPush(toSend);
    	
    	assertTrue("No received data in sink1", sink1.getReceived().size()==1);
    	assertTrue("No received data in sink2", sink2.getReceived().size()==1);
    }

    
    @Test
    public void testConnect2() {
    	sink1.connect(demux);
    	sink2.connect(demux);
    	Buffer toSend = new Buffer();
    	toSend.setData(new byte[255]);
    	toSend.setFormat(f1);
    	demux.getInput().connect(this.source);
    	demux.start();
    	this.source.doPush(toSend);
    	
    	assertTrue("No received data in sink1", sink1.getReceived().size()==1);
    	assertTrue("No received data in sink2", sink2.getReceived().size()==1);
    }

    /**
     * Test of disconnect method, of class Demultiplexer.
     */
    @Test
    public void testDisconnect() {
    	demux.connect(sink1);
    	demux.connect(sink2);
    	demux.disconnect(sink1);
    	
    	Buffer toSend = new Buffer();
    	toSend.setData(new byte[255]);
    	toSend.setFormat(f1);
    	demux.getInput().connect(this.source);
    	demux.start();
    	this.source.doPush(toSend);
    	
    	assertEquals("No received data in sink1",0, sink1.getReceived().size());
    	assertEquals("No received data in sink2",1, sink2.getReceived().size());
    }

    
    @Test
    public void testDisconnect2() {
    	sink1.connect(demux);
    	sink2.connect(demux);
    	sink1.disconnect(demux);
    	
    	Buffer toSend = new Buffer();
    	toSend.setData(new byte[255]);
    	toSend.setFormat(f1);
    	demux.getInput().connect(this.source);
    	demux.start();
    	this.source.doPush(toSend);
    	
    	assertEquals("No received data in sink1",0, sink1.getReceived().size());
    	assertEquals("No received data in sink2",1, sink2.getReceived().size());
    }
    
    
    @Test
    public void testCrossActions() {
    	sink1.connect(demux);
    	sink2.connect(demux);
    	demux.disconnect(sink1);
    	
    	Buffer toSend = new Buffer();
    	toSend.setData(new byte[255]);
    	toSend.setFormat(f1);
    	demux.getInput().connect(this.source);
    	demux.start();
    	this.source.doPush(toSend);
    	
    	assertEquals("No received data in sink1",0, sink1.getReceived().size());
    	assertEquals("No received data in sink2",1, sink2.getReceived().size());
    }

    @Test
    public void testWeird() {
    	demux.getInput().connect(this.source);
    	sink1.connect(demux);
    	demux.connect(sink2);
    	//sink1.disconnect(demux);
    	demux.disconnect(sink1);
    	demux.start();
    	
    	Buffer toSend = new Buffer();
    	toSend.setData(new byte[255]);
    	toSend.setFormat(f1);
    	
    	
    	this.source.doPush(toSend);
    	demux.disconnect(sink2);
    	assertEquals("No received data in sink1",0, sink1.getReceived().size());
    	assertEquals("No received data in sink2",1, sink2.getReceived().size());
    }

    
    private class Source1 extends AbstractSource {

        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Source1(String name) {
            super(name);
        }
        
        public Format[] getFormats() {
            return new Format[]{f1, f2};
        }

        public boolean isAcceptable(Format format) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void receive(Buffer buffer) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void start() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void stop() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
    
    private class LocalMediaSink extends AbstractSink
    {

    	private List<Buffer> received = new ArrayList<Buffer>();
    	 public LocalMediaSink(String name) {
			super(name);
			
		}

		public Format[] getFormats() {
             return new Format[]{f1, f2};
         }

		public boolean isAcceptable(Format format) {
			if(format.matches(getFormats()[0]) || format.matches(getFormats()[1]))
			{
				return true;
			}else
			{
				return false;
			}
		}

		public void receive(Buffer buffer) {
			received.add(buffer);
			
		}

		public List<Buffer> getReceived() {
			return received;
		}
    	
		
    }
    
    private class LocalMediaSource extends AbstractSource
    {

		public LocalMediaSource(String name) {
			super(name);
			// TODO Auto-generated constructor stub
		}

		public Format[] getFormats() {
			return new Format[]{f1, f2};
		}

		public void start() {
			// TODO Auto-generated method stub
			
		}

		public void stop() {
			// TODO Auto-generated method stub
			
		}
		public void doPush(Buffer b)
		{
			super.otherParty.receive(b);
		}
    	
    }
}