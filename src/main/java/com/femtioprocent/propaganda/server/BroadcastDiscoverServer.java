/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.femtioprocent.propaganda.server;

import com.femtioprocent.fpd.sundry.S;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lars
 */
public class BroadcastDiscoverServer {

    public static final String MCA = "224.0.0.1";
    public static final int default_port = 8833;
    int port;

    public BroadcastDiscoverServer() {
        this(default_port);
    }

    public BroadcastDiscoverServer(int port) {
        this.port = port;
    }

    public void start() {
        final ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(1);
        newFixedThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                for (;;) {
                    try {
                        S.pL("Running BroadcastDiscoverServer: " + port);
                        MulticastSocket s = new MulticastSocket(port);
                        InetAddress ma = InetAddress.getByName(MCA);
                        s.joinGroup(ma);
                        byte[] buf = new byte[256];
                        DatagramPacket p = new DatagramPacket(buf, buf.length);
                        for (;;) {
                            S.pL("BroadcastDiscoverServer: revc...");
                            s.receive(p);
                            S.pL("BroadcastDiscoverServer: got " + new String(p.getData(), 0, p.getLength()));
                            InetAddress a = p.getAddress();
                            int po = p.getPort();
                            S.pL("BroadcastDiscoverServer: from " + a + ' ' + po);
                            String rpl = "I'm port: " + PropagandaServer.getDefaultServer().serverPort;

                            String ip;
                            try {
                                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                                while (interfaces.hasMoreElements()) {
                                    NetworkInterface iface = interfaces.nextElement();
                                    // filters out 127.0.0.1 and inactive interfaces
                                    if (iface.isLoopback() || !iface.isUp()) {
                                        continue;
                                    }

                                    Enumeration<InetAddress> addresses = iface.getInetAddresses();
                                    while (addresses.hasMoreElements()) {
                                        InetAddress addr = addresses.nextElement();
                                        ip = addr.getHostAddress();
                                        if (iface.getDisplayName().startsWith("en")) {
                                            if (addr instanceof Inet4Address) {
                                                rpl += ", host: " + addr.getCanonicalHostName();
                                            }
                                        }
                                        System.out.println(iface.getDisplayName() + " " + ip);
                                    }
                                }
                            } catch (SocketException e) {
                                throw new RuntimeException(e);
                            }

//                        InetAddress[] localaddr;
//
//                        try {
//                            localaddr = InetAddress.getAllByName("host.name");
//
//                            for (int i = 0; i < localaddr.length; i++) {
//
//                                rpl += localaddr[i].getHostAddress() + "\n";
//
//                            }
//
//
//                        } catch (UnknownHostException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
                            byte[] sbuf = rpl.getBytes("utf-8");
                            p = new DatagramPacket(sbuf, sbuf.length, a, po);
                            s.send(p);
                            S.pL("BroadcastDiscoverServer: sent " + rpl);
                        }
                    } catch (Exception ex) {
                        System.err.println("BroadcastDiscoverServer: " + ex);
                    }
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        });
    }
}
