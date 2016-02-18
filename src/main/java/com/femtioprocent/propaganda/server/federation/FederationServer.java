package com.femtioprocent.propaganda.server.federation;

import static com.femtioprocent.propaganda.context.Config.getLogger;
import static com.femtioprocent.propaganda.data.AddrType.defaultAddrType;
import com.femtioprocent.propaganda.data.Datagram;
import com.femtioprocent.propaganda.server.PropagandaServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import static com.femtioprocent.propaganda.context.Config.getLogger;
import static com.femtioprocent.propaganda.context.Config.getLogger;
import static com.femtioprocent.propaganda.context.Config.getLogger;
import java.io.OutputStreamWriter;

public class FederationServer {

    private String id;

    int port;
    private ServerSocket serverSocket;

    class FederationClientData {
	PrintWriter pw;
    }

    HashMap<Integer, FederationClientData> fcMap = new HashMap<Integer, FederationClientData>();

    public FederationServer(String id) {
	this.id = id;
	System.err.println("new FederationServer: " + id);
    }

    public FederationServer(int federation_port) throws IOException {
	port = federation_port;
	startFederationServer();
	System.err.println("new FederationServer: " + federation_port);
    }

    public String getId() {
	return id;
    }

//    public void sendToFederatedPropaganda(Datagram datagram) throws PropagandaException {
//        System.out.println(">F> Send to federation: " + id + ' ' + datagram);
//	pw.println(datagram.toString());
//    }
//
    public void sendToFederatedPropaganda(String s, PrintWriter avoid) {
	System.out.println(">F> Send to federation server: " + id + ' ' + s);
	for (FederationClientData fcd : fcMap.values()) {
	    if ( avoid != fcd.pw )
		fcd.pw.println(s);
	}
    }

    @Override
    public String toString() {
	return "FederationServer{" + id + "}";
    }

    private void startFederationServer() throws IOException {
	serverSocket = new ServerSocket(port);
	System.err.println("Fed Server: " + port);
	Thread th;
	th = new Thread(() -> {
	    try {
		for (;;) {
		    Socket so = serverSocket.accept();
		    System.err.println("Fed Server: accept " + so);
		    FederationClientData fcd = new FederationClientData();
		    Thread sth = new Thread(() -> {
			try {
			    InputStream is = so.getInputStream();
			    OutputStream os = so.getOutputStream();
			    BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
			    fcd.pw = new PrintWriter(new OutputStreamWriter(os, "utf-8"), true);
			    fcMap.put(fcd.hashCode(), fcd);
			    NEXT_LINE:
			    for (;;) {
				System.err.println("Fed Server: read...");
				String s = br.readLine();
				System.err.println("Fed Server: read > " + s);
				if (s == null) {
				    is.close();
				    br.close();
				    return;
				}
				try {
				    Datagram datagram = new Datagram(s);

				    if (datagram.getSender() == defaultAddrType) {
					System.err.println("_ ignored: " + s);
					continue NEXT_LINE;
				    }
				    if (datagram.getReceiver() == defaultAddrType) {
					System.err.println("_ ignored: " + s);
					continue NEXT_LINE;
				    }

				    int dmCnt = PropagandaServer.getDefaultServer().dispatcher.dispatchMsg(null, datagram, fcd.pw);

				    String receipt = datagram.getReceipt();
				    if (receipt != null) {
					//transmitReceipt(receipt + ',' + dmCnt);
				    }
				} catch (Exception ex) {
				    getLogger().log(Level.SEVERE, "exception: [" + s + "];", ex);
				}
			    }
			} catch (IOException ex) {
			    System.err.println("Can't io " + ex);
			} finally {
			    fcMap.remove(fcd.hashCode());
			    if (fcd.pw != null) {
				fcd.pw.close();
			    }
			    System.err.println("fed pw closed");
			}
		    });
		    sth.start();
		}
	    } catch (IOException ex) {
		System.err.println("Can't run " + ex);
	    }
	});
	th.start();
    }
}
