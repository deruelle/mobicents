package tests;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;

import javax.naming.NamingException;
import javax.sdp.SdpException;

import junit.framework.Test;
import junit.framework.TestCase;

import org.apache.cactus.ServletTestSuite;
import org.mobicents.media.protocol.PushBufferStream;
import org.mobicents.media.server.impl.BaseConnection;
import org.mobicents.media.server.impl.ann.AnnEndpointImpl;
import org.mobicents.media.server.impl.ann.LocalProxy;
import org.mobicents.media.server.impl.test.audio.SineGenerator;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionListener;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.EndpointQuery;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.ResourceUnavailableException;
import org.mobicents.media.server.spi.TooManyConnectionsException;
import org.mobicents.media.server.spi.events.Announcement;
import org.mobicents.media.server.spi.events.NotifyEvent;
import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsConnectionEvent;
import org.mobicents.mscontrol.MsConnectionListener;
import org.mobicents.mscontrol.MsSession;
import org.mobicents.mscontrol.impl.MsProviderImpl;
/**
 * This class is a test field for tests, little example how to write
 * @author baranowb
 *
 */
public class ZFetchTest extends TestCase implements ConnectionListener, NotificationListener{

	static final String sdp1="v=0\n"
				+"o=- 3 2 IN IP4 127.0.0.1\n"
				+"s=CounterPath X-Lite 3.0\n"
				+"c=IN IP4 127.0.0.1\n"
				+"t=0 0\n"
				+"m=audio 32482 RTP/AVP 107 119 100 106 0 105 98 8 101\n"
				+"a=alt:1 1 : XfjZyuJE QYPfx3U6 127.0.0.1 ";
				
	static int port=0;
	static final  String sdp2="a=fmtp:101 0-15\n"
		+"a=rtpmap:107 BV32/16000\n"
		+"a=rtpmap:119 BV32-FEC/16000\n"
		+"a=rtpmap:100 SPEEX/16000\n"
		+"a=rtpmap:106 SPEEX-FEC/16000\n"
		+"a=rtpmap:105 SPEEX-FEC/8000\n"
		+"a=rtpmap:98 iLBC/8000\n"
		+"a=rtpmap:101 telephone-event/8000\n"
		+"a=sendrecv";
	
	
	 Socket recvSocket;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub


		System.setProperty("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
		System.setProperty("java.naming.provider.url", "jnp://localhost:1099");
		System.setProperty("java.naming.factory.url.pkgs", "org.jnp.interfaces");
		
	
		
		
		
		
	}
	

	
	
	private void doTest2()
	{
		
		try {
		MsProviderImpl provider = new MsProviderImpl();
		MsSession session = provider.createSession();
		MsConnection connection = session.createNetworkConnection("media/trunk/Announcement/$");
		//MediaConnectionListener listener = new MediaConnectionListener();
		
		MsConnectionListener listener=new MsConnectionListener()
		{

			public void connectionCreated(MsConnectionEvent event) {
				System.out.println("-----[1]");
				
			}

			public void connectionDeleted(MsConnectionEvent event) {
				System.out.println("-----[2]");
				
			}

			public void connectionModifed(MsConnectionEvent event) {
				System.out.println("-----[3]");
				
			}

			public void txFailed(MsConnectionEvent event) {
				System.out.println("-----[4]");
				
			}

		
			
		};
		port=12345;
		String totalSDP=sdp1+port+"\n"+sdp2;
		//listener.setInviteRequest(request);
		connection.addConnectionListener(listener);
		connection.modify("$", totalSDP);
		
		
		//final ServerSocket ss=new ServerSocket(port);
		InetAddress[] locals=InetAddress.getAllByName("localhost");
		
		InetAddress local=null;
		for(InetAddress tmp:locals)
		{
			if(tmp.getAddress()[0]==127)
			{
				local=tmp;
				break;
			}
		}
		
		final DatagramSocket ss=new DatagramSocket(port,local);
		
		
		new Thread(new Runnable(){

			public void run() {
				// TODO Auto-generated method stub
				while(true)
				try {
					System.out.println("---> WAITING CONNT");
					int length=512;
					byte[] buffer= new byte[length];
					DatagramPacket p=new DatagramPacket(buffer,length);
					ss.receive(p);
					System.out.println("---> RECV CONNT["+p+"]");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}}).start();

		System.out.println("SDP:\n"+connection.getLocalDescriptor()+"\nENDPOINT:"+connection.getEndpoint());
		
			
		
			new Thread(new TestSignal((AnnEndpointImpl) EndpointQuery.find(connection.getEndpoint()))).start();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ResourceUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
	//---------------------------------------------
	
	private void toDest1()
	{
		
		final ZFetchTest ft=new ZFetchTest();
		try {
			AnnEndpointImpl aepi=(AnnEndpointImpl) EndpointQuery.findAny("media/trunk/Announcement");
			aepi.addNotifyListener(ft);
			Connection conn=aepi.createConnection(Connection.MODE_SEND_ONLY);
			conn.addListener(ft);
			port=12345;
			//final ServerSocket ss=new ServerSocket(port);
			InetAddress[] locals=InetAddress.getAllByName("localhost");
			
			InetAddress local=null;
			for(InetAddress tmp:locals)
			{
				if(tmp.getAddress()[0]==127)
				{
					local=tmp;
					break;
				}
			}
			
			final DatagramSocket ss=new DatagramSocket(port,local);
			
			
			new Thread(new Runnable(){

				public void run() {
					// TODO Auto-generated method stub
					while(true)
					try {
						System.out.println("---> WAITING CONNT");
						int length=512;
						byte[] buffer= new byte[length];
						DatagramPacket p=new DatagramPacket(buffer,length);
						ss.receive(p);
						System.out.println("---> RECV CONNT["+p+"]");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}}).start();
			
			String totalSDP=sdp1+port+"\n"+sdp2;
			conn.setRemoteDescriptor(totalSDP);
			
			System.out.println("SDP:\n"+conn.getLocalDescriptor());
			
			
			new Thread(new TestSignal(aepi)).start();
			
			
			//aepi.deleteAllConnections();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ResourceUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TooManyConnectionsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SdpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void newReceiveStream(Connection connection) {

		System.out.println("====----====");
		
	}

	public void update(NotifyEvent event) {
		
		System.out.println("=====? "+event);
		
	}

	
	static class TestSignal implements Runnable
	{
	
		private AnnEndpointImpl endpoint=null;
		private PushBufferStream stream=null;
		public TestSignal(AnnEndpointImpl endpoint) {
			super();
			this.endpoint = endpoint;
			this.stream=new SineGenerator(10000,new int[]{1}).getStreams()[0];
		}

		public void run() {
	        try {
	            
	            Collection <BaseConnection> list = endpoint.getConnections();
	            for (BaseConnection connection : list) {
	            	System.out.println("WROKING ON CONN["+connection+"]");
	                LocalProxy resource = (LocalProxy) endpoint.getResource(
	                        Endpoint.RESOURCE_AUDIO_SOURCE, connection.getId());
	                resource.setInputStream(stream);
	            }
	        } catch (Exception e) {
	            NotifyEvent report = new NotifyEvent(endpoint,
	                    Announcement.FAIL,
	                    Announcement.CAUSE_FACILITY_FAILURE,
	                    e.getMessage());
	            //this.sendEvent(report);
	        }
	    }
		
	}

	public static Test suite()
    {
        ServletTestSuite suite = new ServletTestSuite();
        suite.addTestSuite(ZFetchTest.class);
        return suite;
    }
	
	public void testCactus()
	{
		System.out.println("===== PASSED");
		
	}
	
}
