package com.femtioprocent.propaganda.server;

import static com.femtioprocent.propaganda.context.Config.getLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import com.femtioprocent.propaganda.connector.Connector_Http;
import com.femtioprocent.propaganda.connector.PropagandaConnectorFactory;

import com.femtioprocent.propaganda.server.PropagandaServer;

public class HttpServer extends Thread {

    PropagandaServer server;
    int port;

    private final ServerSocket serverSocket;
    private final ExecutorService pool;
    private AtomicInteger connection_cnt = new AtomicInteger(1000);

    public HttpServer(PropagandaServer server, int port) throws IOException {
	super("httpd");
	this.server = server;
	this.port = port;

	serverSocket = new ServerSocket(port);
	pool = Executors.newCachedThreadPool();
    }

    public String getHostName() {
	return ""; // sso.getInetAddress().getHostName();
    }

    public void serve() {
	try {
	    for (;;) {
		pool.execute(new Handler(serverSocket.accept()));
		getLogger().info("pool status: : " + pool.toString());
	    }
	} catch (IOException ex) {
	    pool.shutdown();
	    getLogger().severe("pool-shutdown: " + ex);
	}
    }

    class Handler implements Runnable {

	private final Socket connectedSocket;

	Handler(Socket socket) {
	    this.connectedSocket = socket;
	}

	public void run() {
	    Connector_Http connector_http = (Connector_Http) PropagandaConnectorFactory.create("Http", "http", server, null);
	    connector_http.setSocket(connectedSocket);

	    HttpConnectedServer con = new HttpConnectedServer(connector_http, HttpServer.this);
	    con.prepareAndServe();
	}
    }

    @Override
    public void run() {
	serve();
    }
}
