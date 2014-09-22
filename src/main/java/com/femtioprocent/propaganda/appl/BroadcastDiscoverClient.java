/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.femtioprocent.propaganda.appl;

import com.femtioprocent.propaganda.server.BroadcastDiscoverServer;
import com.femtioprocent.propaganda.server.PropagandaServer;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lars
 */
public class BroadcastDiscoverClient {

    int port;

    public BroadcastDiscoverClient() {
        this(PropagandaServer.DEFAULT_DISCOVER_PORT);
    }

    public BroadcastDiscoverClient(int port) {
        this.port = port;
    }

    public List<String> discover() {
        List<String> arr = new ArrayList<String>();
        try {
            final InetAddress mca = InetAddress.getByName(BroadcastDiscoverServer.MCA);
            String msg = "discover";
            DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(),
                    mca, port);
            DatagramSocket s = new DatagramSocket();
            s.setSoTimeout(3000);
            System.err.println("BroadcastDiscoverClient: send... " + hi);
            s.send(hi);

            // get their responses!
            byte[] buf = new byte[1000];
            DatagramPacket recv = new DatagramPacket(buf, buf.length);
            System.err.println("BroadcastDiscoverClient: recv...");
            for (;;) {
                s.receive(recv);
                final String rpl = new String(recv.getData(), "utf-8");
                System.err.println("BroadcastDiscoverClient: got " + rpl);
                if (rpl.startsWith("{\"name\":")) {
                    arr.add(rpl);
                }
            }
        } catch (UnknownHostException ex) {
            System.err.println("BroadcastDiscoverClient: " + ex);
        } catch (IOException ex) {
            System.err.println("BroadcastDiscoverClient: " + ex);
        }
        return arr;
    }

    public static void main(String[] args) {
        BroadcastDiscoverClient bdc = new BroadcastDiscoverClient();
        final List<String> l = bdc.discover();
        System.out.println("discovered: " + l);
    }
}
