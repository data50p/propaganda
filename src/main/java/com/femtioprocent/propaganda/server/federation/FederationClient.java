package com.femtioprocent.propaganda.server.federation;

import com.femtioprocent.propaganda.connector.Connector_Plain;
import com.femtioprocent.propaganda.data.Datagram;
import com.femtioprocent.propaganda.server.PropagandaServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static com.femtioprocent.propaganda.context.Config.getLogger;
import com.femtioprocent.propaganda.util.Util;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FederationClient {

    PropagandaServer server;

    int port;
    String host;

    private Socket socket = null;
    private PrintWriter pw = null;
    private static final ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    public FederationClient(PropagandaServer server, String federation_join, int federation_port) throws IOException {
	port = federation_port;
	host = federation_join;
	this.server = server;
	startFederationClient();
	System.err.println("FederationClient: new " + federation_join + ' ' + federation_port);
    }

    public String getId() {
	return "FederationClient-" + this.hashCode();
    }

    private void startFederationClient() {
	System.err.println("Start FedClient");
	pool.execute(() -> {
	    for (;;) {
		try {
		    Future future = pool.submit(() -> {
			try {
			    socket = new Socket(host, port);
			    System.err.println("FedClient connected: " + socket);
			    BufferedReader rd = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
			    pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"), true);

			    for (;;) {
				String sin = rd.readLine();
				System.err.println("FedClient got: " + sin);
				if (sin == null) {
				    break;
				}
				Datagram datagram = new Datagram(sin);
				System.err.println("FedClient dispatch: " + datagram);
				server.dispatcher.dispatchMsg(null, datagram, pw);
			    }
			} catch (Exception ex) {
			    System.err.println("FedClient ex: " + ex);
			    Util.msleep(3000);
			} finally {
			    if (pw != null) {
				pw.close();
			    }
			    pw = null;
			}
		    });
		    future.get();
		} catch (InterruptedException ex) {
    		    System.err.println("FedClient ex: " + ex);
		} catch (ExecutionException ex) {
    		    System.err.println("FedClient ex: " + ex);
//		    Logger.getLogger(FederationClient.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
		}
	    }
	});
    }

    public void sendToFed(String s, PrintWriter avoid) {
	System.err.println("sendToFed fso: " + socket);
	if (socket == null) {
	    return;
	}

	if (pw != null && pw != avoid) {
	    pw.println(s);
	    System.err.println("sendToFed send: " + s);
	}
    }
}
