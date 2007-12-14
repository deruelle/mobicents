/*
 * MyRouter.java
 * 
 * Created on Jul 21, 2005
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

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import javax.sip.SipStack;
import javax.sip.address.Hop;
import javax.sip.address.Router;
import javax.sip.message.Request;

/**
 * This gets pinged by the proxy on the slee. 
 */
public class MyRouter extends UnicastRemoteObject implements Router, UdpForwarder {
    private String myHost;

    private int myPort;

    private LinkedList myProxyList;

    private Timer timer;
    
    private static Logger logger = Logger.getLogger(MyRouter.class.getName());

    private class MyTimerTask extends TimerTask {

        public MyTimerTask() {
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.TimerTask#run()
         */
        public void run() {
            // Remove any proxy servers that we have not heard from
            // in the last 10 seconds.
            synchronized (this) {
                long currentTime = System.currentTimeMillis();
                for (Iterator it = myProxyList.iterator(); it.hasNext();) {
                    Ping p = (Ping) it.next();
                    if (currentTime - p.getLastPing() > 10 * 1000) {
                        logger.fine("Dead proxy : " + p.getHost() + ":"
                                + p.getPort());
                        it.remove();
                    }
                }
            }
        }

    }

    private Ping getFirstProxy() {
        if (myProxyList.isEmpty())
            return null;
        else
            return (Ping) this.myProxyList.getFirst();
    }

    public synchronized Ping handlePing(Ping ping) throws RemoteException {
        logger.fine("got a ping!" + ping.getHost() + ":"
                + ping.getPort());
        boolean found = false;
        for (Iterator it = myProxyList.iterator(); it.hasNext();) {
            Ping p = (Ping) it.next();
            if (p.getHost().equals(ping.getHost())
                    && p.getPort() == ping.getPort()) {
                p.setPingTime();
                found = true;

            }
        }
        if (!found) {
            ping.setPingTime();
            myProxyList.add(ping);

        }

        return new Ping(myHost, myPort);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.sip.address.Router#getOutboundProxy()
     */
    public Hop getOutboundProxy() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.sip.address.Router#getNextHops(javax.sip.message.Request)
     */
    public ListIterator getNextHops(Request request) {
     
        Ping ping = getFirstProxy();
        if (ping == null) {
            return null;
        } else {
            LinkedList retval = new LinkedList();
            retval.add(new HopImpl(ping.getHost(), ping.getPort()));
            return retval.listIterator();
        }

    }

    public MyRouter(SipStack sipStack, String proxyPort) throws Exception{
       try {
            this.myHost = sipStack.getIPAddress();
            this.myPort = Integer.parseInt(proxyPort);
            this.myProxyList = new LinkedList();
            timer = new Timer();
            LocateRegistry.createRegistry(2000);
            Registry registry = LocateRegistry.getRegistry("127.0.0.1", 2000);

            registry.rebind("UdpForwarder", this);

            timer.schedule(new MyTimerTask(), 0, 1000);
       } catch (Exception ex) {
           ex.printStackTrace();
       }
        
    }

    /* (non-Javadoc)
     * @see javax.sip.address.Router#getNextHop(javax.sip.message.Request)
     */
    public Hop getNextHop(Request request) {
        Iterator it = this.getNextHops(request);
        if ( it == null || !it.hasNext() ) return null;
        else return (Hop) this.getNextHops(request).next();
        
    }

}

