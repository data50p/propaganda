/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.femtioprocent.propaganda.server;

import com.femtioprocent.fpd.appl.Appl;
import com.femtioprocent.propaganda.Version;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author lars
 */
public class BroadcastDiscoverServer {

    public static final String MCA = "224.0.0.1";
    int port;

    public BroadcastDiscoverServer() {
	this(PropagandaServer.DEFAULT_DISCOVER_PORT);
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
			System.err.println("Running BroadcastDiscoverServer: " + port);

//                        MulticastSocket s = new MulticastSocket(port);
//                        InetAddress ma = InetAddress.getByName(MCA);
//                        s.joinGroup(ma);
			//Keep a socket open to listen to all the UDP trafic that is destined for this port
			DatagramSocket s = new DatagramSocket(port, InetAddress.getByName("0.0.0.0"));
			s.setBroadcast(true);

			byte[] buf = new byte[256];
			DatagramPacket p = new DatagramPacket(buf, buf.length);
			for (;;) {
			    s.receive(p);
			    System.err.println("BroadcastDiscoverServer: got " + new String(p.getData(), 0, p.getLength()));
			    InetAddress a = p.getAddress();
			    int po = p.getPort();
			    System.err.println("BroadcastDiscoverServer: from " + a + ' ' + po);

			    List<String> hostList = new ArrayList<String>();
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
					if (!iface.getDisplayName().startsWith("vmnet") && addr instanceof Inet4Address) {
					    hostList.add("\"" + addr.getCanonicalHostName() + "\"");
					}
					System.out.println(iface.getDisplayName() + " " + ip);
				    }
				}
			    } catch (SocketException e) {
				throw new RuntimeException(e);
			    }

			    Runtime r = Runtime.getRuntime();
			    Process pr = r.exec("uname -n");
			    pr.waitFor();
			    BufferedReader b = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			    String hostname = "";

			    while ((hostname = b.readLine()) != null) {
				System.out.println(" --> " + hostname);
				break;
			    }

			    String rpl = "{\"name\":\""
				    + PropagandaServer.getDefaultServer().getName() + "\", \"version\":\""
				    + Version.projectVersion + "\", \"buildtime\":\""
				    + Version.mavenBuildTimestamp + "\", \"host\":\""
				    + hostname + "\", \"hosts\":"
				    + hostList.toString().replace(" ", "") + ", \"port\":\""
				    + PropagandaServer.getDefaultServer().serverPort
				    + "\", \"flags\":\"" + Appl.flags.toString() + "\""
				    + "}";

			    byte[] sbuf = rpl.getBytes("utf-8");
			    p = new DatagramPacket(sbuf, sbuf.length, a, po);
			    s.send(p);
			    System.err.println("BroadcastDiscoverServer: sent " + rpl);
			}
//                        s.leaveGroup(ma);
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
