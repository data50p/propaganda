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
import java.util.concurrent.atomic.AtomicInteger;

public class FederationServer {

    private int port;
    private ServerSocket serverSocket;
    HashMap<Integer, FederationClientData> fcMap = new HashMap<Integer, FederationClientData>();
    AtomicInteger fcIndex = new AtomicInteger(0);

    private int putFcMap(FederationClientData fcd) {
	int ix = fcIndex.incrementAndGet();
	synchronized (fcMap) {
	    fcMap.put(ix, fcd);
	}
	System.err.println("Fed Server add: " + ix + ' ' + fcMap);
	return ix;
    }

    private void remFcMap(int ix) {
	synchronized (fcMap) {
	    fcMap.remove(ix);
	}
	System.err.println("Fed Server rem: " + ix + ' ' + fcMap);
    }

    class FederationClientData {

	PrintWriter pw;
    }

    public FederationServer(int federation_port) throws IOException {
	port = federation_port;
	startFederationServer();
	System.err.println("new FederationServer: " + federation_port);
    }

    public void sendToFederatedPropaganda(String s, PrintWriter avoid) {
	synchronized (fcMap) {
	    System.out.println("FederationServer to federation clients: " + fcMap.size() + ' ' + s);
	    for (FederationClientData fcd : fcMap.values()) {
		if (avoid != fcd.pw) {
		    fcd.pw.println(s);
		    System.err.println(">> " + fcd.pw);
		}
	    }
	}
    }

    @Override
    public String toString() {
	return "FederationServer{" + fcMap.size() + "}";
    }

    private void startFederationServer() throws IOException {
	serverSocket = new ServerSocket(port);
	System.err.println("FederationServer: " + port);
	Thread th;
	th = new Thread(() -> {
	    try {
		for (;;) {
		    Socket so = serverSocket.accept();
		    System.err.println("Fed Server: accept " + so);
		    FederationClientData fcd = new FederationClientData();
		    int fc_index = putFcMap(fcd);
		    Thread sth = new Thread(() -> {
			try {
			    InputStream is = so.getInputStream();
			    OutputStream os = so.getOutputStream();
			    BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
			    fcd.pw = new PrintWriter(new OutputStreamWriter(os, "utf-8"), true);
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
			    System.err.println("Fed Server: Can't io " + ex);
			} finally {
			    remFcMap(fc_index);
			    if (fcd.pw != null) {
				fcd.pw.close();
			    }
			    System.err.println("Fed Server: pw closed");
			}
		    });
		    sth.start();
		}
	    } catch (IOException ex) {
		System.err.println("Fed Server: Can't run " + ex);
	    }
	});
	th.start();
    }
}
