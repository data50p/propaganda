package com.femtioprocent.propaganda.server.federation;

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
import java.util.logging.Level;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import static com.femtioprocent.propaganda.context.Config.getLogger;

public class FederationServer {

    private PropagandaServer server;
    private int port;
    private ServerSocket serverSocket;
    private Set<FederationClientData> fcSet = new HashSet<FederationClientData>();
    private static final ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    private FederationClientData add(FederationClientData fcd) {
	synchronized (fcSet) {
	    fcSet.add(fcd);
	System.err.println("Fed Server add: " + fcd + ' ' + fcSet);
	    return fcd;
	}
    }

    private void rem(FederationClientData fcd) {
	synchronized (fcSet) {
	    fcSet.remove(fcd);
	    if (fcd.pw != null) {
		fcd.pw.close();
		System.err.println("Fed Server: pw closed");
	    }
	}
	System.err.println("Fed Server rem: " + fcd + ' ' + fcSet);
    }

    class FederationClientData {

	PrintWriter pw;

	boolean isOk(PrintWriter avoid) {
	    return pw != avoid;
	}

	void close() {
	    if (pw != null) {
		pw.close();
	    }
	}

	public String toString() {
	    return "@" + hashCode();
	}
	@Override
	public boolean equals(Object o) {
	    if (o instanceof FederationClientData) {
		return pw == ((FederationClientData) o).pw;
	    }
	    return false;
	}
    }

    public FederationServer(PropagandaServer server, int federation_port) throws IOException {
	this.server = server;
	port = federation_port;
	startFederationServer();
    }

    public void sendToFederatedPropaganda(String s, PrintWriter avoid) {
	synchronized (fcSet) {
	    System.out.println("FederationServer to federation clients: " + fcSet.size() + ' ' + s);
	    for (FederationClientData fcd : fcSet) {
		if (fcd.isOk(avoid)) {
		    fcd.pw.println(s);
		    System.err.println(">> " + fcd.pw);
		}
	    }
	}
    }

    @Override
    public String toString() {
	return "FederationServer{" + fcSet.size() + "}";
    }

    private void startFederationServer() throws IOException {
	serverSocket = new ServerSocket(port);
	pool.execute(() -> {
	    try {
		for (;;) {
		    Socket so = serverSocket.accept();
		    System.err.println("Fed Server: accept " + so);

		    FederationClientData fcd = add(new FederationClientData());

		    pool.execute(() -> {
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

				    int dmCnt = server.dispatcher.dispatchMsg(null, datagram, fcd.pw);

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
			    rem(fcd);
			}
		    });
		}
	    } catch (IOException ex) {
		System.err.println("Fed Server: Can't run " + ex);
	    }
	});
    }
}
