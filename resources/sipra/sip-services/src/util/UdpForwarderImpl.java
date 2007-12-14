/*
 * UdpForwarderImpl.java
 * 
 * Created on Jul 14, 2005
 * 
 * Created by: M. Ranganathan
 *
 * The Mobicents Open SLEE project
 * 
 * A SLEE for the people!
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

package util;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.ListeningPoint;
import javax.sip.PeerUnavailableException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.HeaderFactory;
import javax.sip.header.RecordRouteHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

/**
 * A stateless UDP Forwarder that listens at a port and forwards to multiple
 * outbound addresses. It keeps a timer thread around that pings the list of
 * proxy servers and sends to the first proxy server.
 * 
 * @author M. Ranganathan
 *  
 */
public class UdpForwarderImpl implements SipListener {

    private SipProvider internalSipProvider;

    private SipProvider externalSipProvider;

    private String myHost;

    private int myPort;

    private int myExternalPort;

    private AddressFactory addressFactory;

    private MessageFactory messageFactory;

    private HeaderFactory headerFactory;

    private SipStack sipStack;
    
    private static Logger logger = Logger.getLogger(UdpForwarder.class.getName()); 

    public void init() {
        SipFactory sipFactory = null;
        sipStack = null;
        sipFactory = SipFactory.getInstance();
        sipFactory.setPathName("gov.nist");
        Properties properties = new Properties();
        properties.setProperty("javax.sip.IP_ADDRESS", myHost);
        properties.setProperty("javax.sip.RETRANSMISSION_FILTER", "true");
        properties.setProperty("javax.sip.STACK_NAME", "shootme");
        // You need 16 for logging traces. 32 for debug + traces.
        // Your code will limp at 32 but it is best for debugging.
        properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "32");
        properties.setProperty("gov.nist.javax.sip.DEBUG_LOG",
                "forwarderdebug.txt");
        properties
                .setProperty("gov.nist.javax.sip.SERVER_LOG", "forwarder.txt");
        properties.setProperty("javax.sip.ROUTER_PATH", "util.MyRouter");
        properties.setProperty("javax.sip.OUTBOUND_PROXY", Integer
                .toString(myPort));

        try {
            // Create SipStack object
            sipStack = sipFactory.createSipStack(properties);
            logger.fine("sipStack = " + sipStack);
        } catch (PeerUnavailableException e) {
            // could not find
            // gov.nist.jain.protocol.ip.sip.SipStackImpl
            // in the classpath
            logger.log(Level.WARNING, "Failed to create SIP Stack", e);
            System.exit(0);
        }

        try {
            headerFactory = sipFactory.createHeaderFactory();
            addressFactory = sipFactory.createAddressFactory();
            messageFactory = sipFactory.createMessageFactory();
            ListeningPoint lp = sipStack.createListeningPoint(myPort, "udp");

            internalSipProvider = sipStack.createSipProvider(lp);

            internalSipProvider.addSipListener(this);

            logger.fine("internal SipProvider "
                    + this.internalSipProvider);

            lp = sipStack.createListeningPoint(myExternalPort, "udp");

            externalSipProvider = sipStack.createSipProvider(lp);

            logger.fine("external SipProvider "
                    + this.externalSipProvider);
            externalSipProvider.addSipListener(this);

            logger.fine("all ready to go! " + myPort + ":"
                    + this.myExternalPort);
        } catch (Exception ex) {
            logger.log(Level.WARNING, ex.getMessage(), ex);

        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.sip.SipListener#processRequest(javax.sip.RequestEvent)
     */
    public void processRequest(RequestEvent requestEvent) {
        try {
            SipProvider sipProvider = (SipProvider) requestEvent.getSource();
            if (sipProvider == this.externalSipProvider) {
                Request request = requestEvent.getRequest();
                
                logger.fine(" -> Got a request \n" + request);
                // Tack our own via header to the request.
                // Tack on our internal port so the other side responds to me.
                ViaHeader viaHeader = headerFactory.createViaHeader(
                        this.myHost, this.myPort, "udp", null);
                // Add the via header to the top of the header list.
                request.addHeader(viaHeader);

                // Record route the invite so the bye comes to me.
                if (request.getMethod().equals(Request.INVITE)) {
                    SipURI sipUri = this.addressFactory
                            .createSipURI(null, sipProvider.getListeningPoint(
                                    "udp").getIPAddress());
                    sipUri.setPort(sipProvider.getListeningPoint("udp")
                            .getPort());
                    Address address = this.addressFactory.createAddress(sipUri);
                    address.setURI(sipUri);
                    RecordRouteHeader recordRoute = headerFactory
                            .createRecordRouteHeader(address);
                    request.addHeader(recordRoute);
                } /* else if ( request.getMethod().equals(Request.BYE)) {
                    // Should have me on the route list.
                    request.removeFirst(RouteHeader.NAME);
                } */

                logger.fine("Forwarding request --> \n" + request);
                this.internalSipProvider.sendRequest(request);

            } else {
                // This should never happen. None of my own clients will ever
                // send me a request.
                // They will just use my IP Address and place my ip address and
                // port in the outgoing
                // via header.
                throw new Exception("Illegal state!! unexpected request");

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.sip.SipListener#processResponse(javax.sip.ResponseEvent)
     */
    public void processResponse(ResponseEvent responseEvent) {
        try {
            SipProvider sipProvider = (SipProvider) responseEvent.getSource();
            Response response = responseEvent.getResponse();

            logger.fine("got a response \n" + response);

            // Topmost via header is me.
            response.removeFirst(ViaHeader.NAME);
            if (sipProvider == this.internalSipProvider) {
                //None other than my internal clients ever send me a response.
            	logger.fine("Forwarding the response \n" + response);
                this.externalSipProvider.sendResponse(response);
            } else {
                throw new Exception("Illegal State!! unexpected response");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.sip.SipListener#processTimeout(javax.sip.TimeoutEvent)
     */
    public void processTimeout(TimeoutEvent arg0) {
        // TODO Auto-generated method stub

    }

    private UdpForwarderImpl(String ipAddress, int myPort, int externalPort) {

        this.myHost = ipAddress;
        this.myExternalPort = externalPort;
        // This is where my proxy servers reply.
        this.myPort = myPort;
        this.init();
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
        	logger.fine("Insufficient args");
            throw new IllegalArgumentException(
                    "Bad args: supply ip address and port");
        }
        // This is my IP Address
        String ipAddress = args[0];
        int port;
        int externalPort;
        try {
            // This is the proxy port at which I am listening.
            port = Integer.parseInt(args[1]);
            externalPort = Integer.parseInt(args[2]);
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            logger.fine("arguments are IPAddress port");
            throw nfe;
        }

        new UdpForwarderImpl(ipAddress, port, externalPort);

        /*
         * while (true) { byte[] buf = new byte[len]; DatagramPacket p = new
         * DatagramPacket(buf, len); inputSock.receive(p); Ping ping =
         * udpFwd.getFirstProxy(); if (ping == null) { logger.fine("no
         * proxy registgered"); continue; } else { logger.fine("got
         * something-- forwarding to : " + ping.getInetAddress() + ":" +
         * ping.getPort()); } DatagramPacket newPacket = new DatagramPacket(buf,
         * 0, buf.length, ping.getInetAddress(), ping.getPort());
         * outputSock.send(newPacket); }
         */
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.sip.SipListener#processIOException(javax.sip.IOExceptionEvent)
     */
    public void processIOException(IOExceptionEvent arg0) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.sip.SipListener#processTransactionTerminated(javax.sip.TransactionTerminatedEvent)
     */
    public void processTransactionTerminated(TransactionTerminatedEvent arg0) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.sip.SipListener#processDialogTerminated(javax.sip.DialogTerminatedEvent)
     */
    public void processDialogTerminated(DialogTerminatedEvent arg0) {
        // TODO Auto-generated method stub

    }
}

