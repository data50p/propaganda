/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.femtioprocent.propaganda.appl;

import com.femtioprocent.fpd.appl.Appl;
import static com.femtioprocent.fpd.appl.Appl.decodeArgs;
import static com.femtioprocent.fpd.appl.Appl.flags;
import static com.femtioprocent.fpd.appl.Appl.main;
import com.femtioprocent.fpd.sundry.S;
import com.femtioprocent.propaganda.server.PropagandaServer;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import static com.femtioprocent.fpd.appl.Appl.decodeArgs;
import static com.femtioprocent.fpd.appl.Appl.main;
import static com.femtioprocent.fpd.appl.Appl.decodeArgs;
import static com.femtioprocent.fpd.appl.Appl.main;
import static com.femtioprocent.fpd.appl.Appl.decodeArgs;
import static com.femtioprocent.fpd.appl.Appl.main;

/**
 *
 * @author lars
 */
public class BroadcastDiscoverClient extends Appl {

    int port;

    public BroadcastDiscoverClient() {
	this(PropagandaServer.DEFAULT_DISCOVER_PORT);
    }

    public BroadcastDiscoverClient(int port) {
	this.port = port;
    }

    @Override
    public void main() {
	if ((flags.get("?")) != null || (flags.get("h")) != null) {
	    System.err.println("-discover=<port>      set the port of propaganda discover server (=8839)");
	    return;
	}

	String fl;
	if ((fl = flags.get("discover")) != null) {
	    PropagandaServer.DEFAULT_DISCOVER_PORT = Integer.parseInt(fl);
	}

	BroadcastDiscoverClient bdc = new BroadcastDiscoverClient();
	final JSONArray jarr = bdc.discover();
	JSONObject obj = new JSONObject();
	obj.put("discovered", jarr);
	System.out.println("" + obj.toString());
    }

    public JSONArray discover() {
	JSONArray jsonArr = new JSONArray();
	Set<String> set = new HashSet<String>();

	try {

//            final InetAddress mca = InetAddress.getByName(BroadcastDiscoverServer.MCA);
	    final InetAddress bca = InetAddress.getByName("255.255.255.255");
	    String msg = "discover";
	    DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), bca, port);
	    DatagramSocket s = new DatagramSocket();
	    s.setSoTimeout(5000);
	    System.err.println("BroadcastDiscoverClient: send... " + bca + ' ' + port + ' ' + hi);
	    s.send(hi);

	    // Broadcast the message over all the network interfaces
	    Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
	    while (interfaces.hasMoreElements()) {
		NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();

		if (networkInterface.isLoopback() || !networkInterface.isUp()) {
		    continue; // Don't want to broadcast to the loopback interface
		}

		for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
		    InetAddress broadcast = interfaceAddress.getBroadcast();
		    if (broadcast == null) {
			continue;
		    }

		    // Send the broadcast package!
		    try {
			DatagramPacket sendPacket = new DatagramPacket(msg.getBytes(), msg.length(), broadcast, port);
			s.send(sendPacket);
		    } catch (Exception e) {
		    }

		    System.out.println(getClass().getName() + ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
		}
	    }

	    // get their responses!
	    byte[] buf = new byte[1000];
	    DatagramPacket recv = new DatagramPacket(buf, buf.length);
	    System.err.println("BroadcastDiscoverClient: recv...");
	    for (;;) {
		s.receive(recv);
		final String rpl = new String(recv.getData(), recv.getOffset(), recv.getLength(), "utf-8");
		System.err.println("BroadcastDiscoverClient: got " + rpl);
		set.add(rpl);
	    }
	} catch (UnknownHostException ex) {
	    System.err.println("BroadcastDiscoverClient: " + ex);
	} catch (IOException ex) {
	    System.err.println("BroadcastDiscoverClient: " + ex);
	}

	for (String rpl : set) {
	    if (rpl.startsWith("{\"name\":")) {
		JSONTokener t = new JSONTokener(rpl);
		JSONObject root = new JSONObject(t);
		jsonArr.put(root);
	    }
	}
	return jsonArr;
    }

    public static void main(String[] args) {
	decodeArgs(args);
	main(new BroadcastDiscoverClient());
    }
}
