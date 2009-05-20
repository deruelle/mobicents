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
import static org.junit.Assert.*;

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.Utils;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.EndpointImpl;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.clock.TimerImpl;


/**
 *
 * @author kulikov
 */
public class MultiplexerTest {

    private Multiplexer mux;
    private final static Format f1 = new AudioFormat("f1");
    private final static Format f2 = new AudioFormat("f2");
    
    private LocalMediaSink sink;
    private LocalMediaSource source1;
    private LocalMediaSource source2;
    
    public MultiplexerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        TimerImpl timer = new TimerImpl();
        EndpointImpl endpoint = new EndpointImpl();
        endpoint.setTimer(timer);
        mux = new Multiplexer("test");
        
        this.sink = new LocalMediaSink("sink");
        this.source1 = new LocalMediaSource("source1");
        this.source2 = new LocalMediaSource("source2");
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testOutputFormats() {
        Format[] supported = mux.getFormats();
        assertEquals(0, supported.length);

        mux.connect(new Source1(""));
        
        supported = mux.getOutput().getFormats();
        assertEquals(1, supported.length);
        
        Format[] formats = new Format[] {new AudioFormat("f1")};
        assertEquals(true, Utils.checkFormats(supported,formats ));
        
        mux.connect(new Source2(""));
        supported = mux.getOutput().getFormats();
        assertEquals(2, supported.length);
        formats = new Format[] {new AudioFormat("f1"), new AudioFormat("f2")};
        assertEquals(true, Utils.checkFormats(supported,formats ));
    }
    
    
    @Test
    public void testConnect() {
    	mux.connect(source1);
    	mux.connect(source2);
    	mux.getOutput().connect(sink);
    	mux.getOutput().start();
    	Buffer toSend = new Buffer();
    	toSend.setData(new byte[255]);
    	toSend.setFormat(f1);
    	Buffer toSend2 = new Buffer();
    	toSend.setData(new byte[255]);
    	toSend.setFormat(f2);
    	
    	this.source1.doPush(toSend);
    	this.source2.doPush(toSend2);
    	assertEquals("No enough received data in sink",2, sink.getReceived().size());
    	
    }
    
    @Test
    public void testConnectReverse() {
    	source1.connect(mux);
    	source2.connect(mux);
    	mux.getOutput().connect(sink);
    	mux.getOutput().start();
    	Buffer toSend = new Buffer();
    	toSend.setData(new byte[255]);
    	toSend.setFormat(f1);
    	Buffer toSend2 = new Buffer();
    	toSend.setData(new byte[255]);
    	toSend.setFormat(f2);
    	
    	this.source1.doPush(toSend);
    	this.source2.doPush(toSend2);
    	assertEquals("No enough received data in sink",2, sink.getReceived().size());
    	
    }
    
    @Test
    public void testDisconnect() {
    	mux.connect(source1);
    	mux.connect(source2);
    	mux.disconnect(source1);
    	mux.getOutput().connect(sink);
    	mux.getOutput().start();
    	Buffer toSend = new Buffer();
    	toSend.setData(new byte[255]);
    	toSend.setFormat(f1);
    	Buffer toSend2 = new Buffer();
    	toSend.setData(new byte[255]);
    	toSend.setFormat(f2);
    	try{
    		this.source1.doPush(toSend);
    		fail("We should get exception here.");
    	}catch(NullPointerException npe)
    	{
    		
    	}
    	this.source2.doPush(toSend2);
    	assertEquals("No enough/to much data received data in sink",1, sink.getReceived().size());
    	
    }
    
    @Test
    public void testDisconnectReverse() {
    	source1.connect(mux);
    	source2.connect(mux);
    	source2.disconnect(mux);
    	mux.getOutput().connect(sink);
    	mux.getOutput().start();
    	Buffer toSend = new Buffer();
    	toSend.setData(new byte[255]);
    	toSend.setFormat(f1);
    	Buffer toSend2 = new Buffer();
    	toSend.setData(new byte[255]);
    	toSend.setFormat(f2);
    	
    	this.source1.doPush(toSend);
    	try{
    		this.source2.doPush(toSend);
    		fail("We should get exception here.");
    	}catch(NullPointerException npe)
    	{
    		
    	}
    	assertEquals("No enough/to much data received data in sink",1, sink.getReceived().size());
    	
    }
    
    @Test
    public void testDisconnectCrossComp() {
    	source1.connect(mux);
    	source2.connect(mux);
    	mux.disconnect(source2);
    	
    	mux.getOutput().connect(sink);
    	mux.getOutput().start();
    	Buffer toSend = new Buffer();
    	toSend.setData(new byte[255]);
    	toSend.setFormat(f1);
    	Buffer toSend2 = new Buffer();
    	toSend.setData(new byte[255]);
    	toSend.setFormat(f2);
    	
    	this.source1.doPush(toSend);
    	try{
    		this.source2.doPush(toSend);
    		fail("We should get exception here.");
    	}catch(NullPointerException npe)
    	{
    		
    	}
    	assertEquals("No enough/to much data received data in sink",1, sink.getReceived().size());
    	
    }
    
    @Test
    public void testWeird() {
    	source1.connect(mux);
    	source2.connect(mux);
    	mux.disconnect(source2);
    	
    	mux.getOutput().connect(sink);
    	mux.getOutput().start();
    	
    	Buffer toSend = new Buffer();
    	toSend.setData(new byte[255]);
    	toSend.setFormat(f1);
    	Buffer toSend2 = new Buffer();
    	toSend.setData(new byte[255]);
    	toSend.setFormat(f2);
    	
    	this.source1.doPush(toSend);
    	this.source1.disconnect(mux);
    	try{
    		this.source2.doPush(toSend);
    		fail("We should get exception here.");
    	}catch(NullPointerException npe)
    	{
    		
    	}
    	assertEquals("No enough/to much data received data in sink",1, sink.getReceived().size());
    }
    
    
    private class Source1 extends AbstractSource {

        private Format f = new AudioFormat("f1");
        
        public Source1(String name) {
            super(name);
        }
        
        public Format[] getFormats() {
            return new Format[]{f};
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
    
    private class Source2 extends AbstractSource {

        private Format f = new AudioFormat("f2");
        
        public Source2(String name) {
            super(name);
        }
        
        public Format[] getFormats() {
            return new Format[]{f};
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
    
}