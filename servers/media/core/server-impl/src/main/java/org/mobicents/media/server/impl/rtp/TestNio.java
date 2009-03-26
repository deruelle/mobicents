/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl.rtp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author kulikov
 */
public class TestNio {

    private static ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
    private Thread worker;
    private volatile boolean started = false;

    public static void main(String[] args) throws Exception {
        TestNio t = new TestNio();
        t.doTest();
    }

    public void doTest() throws Exception {
        int N = 900;

        started = true;
        Receiver r = new Receiver();

        Server1[] servers = new Server1[N];
        for (int i = 0; i < N; i++) {
            servers[i] = new Server1(i);
            r.add(servers[i]);
        }

        System.out.println("Servers are ready ");
        
        worker = new Thread(r);
        worker.start();
        
        Client[] clients = new Client[N];
        for (int i = 0; i < N; i++) {
            clients[i] = new Client(i);
            timer.scheduleAtFixedRate(clients[i], 0, 20, TimeUnit.MILLISECONDS);
        }

        Thread.currentThread().sleep(15000);

        timer.shutdown();
        started = false;

        for (int i = 0; i < N; i++) {
            servers[i].stop();
        }

        clients[0].printTicks();
        System.out.println("===============================");
        servers[0].printTicks();
    }

    private class Client implements Runnable {

        private DatagramSocket socket;
        private InetSocketAddress destination;
        private ArrayList<Long> ticks = new ArrayList(5000);

        public Client(int index) throws SocketException {
            int port = 4000 + index;
            InetSocketAddress address = new InetSocketAddress("192.168.1.2", port);
            destination = new InetSocketAddress("192.168.1.2", port - 3000);
            socket = new DatagramSocket(address);
        }

        public void run() {
            byte[] buffer = new byte[160];
            try {
                DatagramPacket p = new DatagramPacket(buffer, buffer.length, destination);
                for (int i = 0; i < 160; i++) {
                    buffer[i] = (byte) (100 * 2 + 20 / 10 + 40 / 2 + 20 * 10);
                }
                socket.send(p);
                ticks.add(System.currentTimeMillis());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void printTicks() {
            System.out.println("Packets " + ticks.size());
            for (int i = 1; i < ticks.size(); i++) {
                System.out.println("diff =" + (ticks.get(i) - ticks.get(i - 1)));
            }
        }
    }

    private class Server implements Runnable {

        private DatagramSocket socket;
        private boolean stopped = false;
        private ArrayList<Long> ticks = new ArrayList(5000);

        public Server(int index) throws SocketException {
            int port = 1000 + index;
            InetSocketAddress address = new InetSocketAddress("192.168.1.2", port);
            socket = new DatagramSocket(address);
            new Thread(this).start();
        }

        public void stop() {
            stopped = true;
            socket.close();
        }

        public void run() {
            byte[] buffer = new byte[1000];
            DatagramPacket packet = new DatagramPacket(buffer, 1000);

            while (!stopped) {
                try {
                    socket.receive(packet);
                    ticks.add(System.currentTimeMillis());
                } catch (IOException e) {
                }
            }
        }

        public void printTicks() {
            for (int i = 1; i < ticks.size(); i++) {
                System.out.println("diff =" + (ticks.get(i) - ticks.get(i - 1)));
            }
        }
    }

    private class Receiver implements Runnable {

        private ArrayList<Server1> list = new ArrayList();

        public void add(Server1 s) {
            list.add(s);
        }
        
        public void run() {
            System.out.println("Worker started: " + list.size() + "started = " + started);
            while (started) {
                for (Server1 receiver : list) {
                    receiver.run();
                }
                try {
                    Thread.currentThread().sleep(10);
                } catch (InterruptedException e) {
                    
                }
  
            }
            System.out.println("Worker terminated");
        }
    }

    private class Server1 implements Runnable {

        private DatagramChannel channel;
        private ByteBuffer buffer = ByteBuffer.allocate(1000);
        private ArrayList<Long> ticks = new ArrayList(5000);
        private Selector selector;
        
        public Server1(int index) throws SocketException, IOException {
            int port = 1000 + index;
            InetSocketAddress address = new InetSocketAddress("192.168.1.2", port);
            channel = DatagramChannel.open();
            System.out.println("Channel is open " + index);
            
            channel.socket().bind(address);
            System.out.println("Socket is bound to " + address);
            
            channel.connect(new InetSocketAddress("192.168.1.2", port + 3000));
            System.out.println("Socket is connected to port " + (port + 3000));
            
            selector = Selector.open();
            channel.configureBlocking(false);
            
            System.out.println("Selected opened");
            
            channel.register(selector, SelectionKey.OP_READ);
            System.out.println("Selected - 0");
        }

        public void stop() throws IOException {
            selector.close();
            channel.disconnect();
            channel.close();
            channel.socket().close();
        }

        public void run() {
            try {
                //selector.select();
                int count = channel.read(buffer);
                buffer.flip();
                buffer.clear();
                if (count > 0) {
                    ticks.add(System.currentTimeMillis());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void printTicks() {
            System.out.println("Packets " + ticks.size());
            for (int i = 1; i < ticks.size(); i++) {
                System.out.println("diff =" + (ticks.get(i) - ticks.get(i - 1)));
            }
        }
    }
}
