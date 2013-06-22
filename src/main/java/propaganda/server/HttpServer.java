package propaganda.server;

import static propaganda.context.Config.getLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import propaganda.connector.Connector_Http;
import propaganda.connector.PropagandaConnectorFactory;

import propaganda.server.PropagandaServer;

public class HttpServer extends Thread {
    PropagandaServer server;
    int port;

    private final ServerSocket serverSocket;
    private final ExecutorService pool;
    private AtomicInteger connection_cnt = new AtomicInteger(1000);

    public HttpServer(PropagandaServer server, int port)  throws IOException {
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
	    Connector_Http connector_http = (Connector_Http)PropagandaConnectorFactory.create("Http", "http-" + connection_cnt.incrementAndGet(), server, null);
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
