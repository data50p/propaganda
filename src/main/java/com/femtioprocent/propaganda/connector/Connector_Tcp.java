package com.femtioprocent.propaganda.connector;

import static com.femtioprocent.propaganda.data.AddrType.defaultAddrType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

import com.femtioprocent.propaganda.client.PropagandaClient;
import com.femtioprocent.propaganda.data.Datagram;
import com.femtioprocent.propaganda.exception.PropagandaException;
import com.femtioprocent.propaganda.server.federation.ClientGhost;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import static com.femtioprocent.propaganda.context.Config.getLogger;

public class Connector_Tcp extends PropagandaConnector {

    int listenPort = 0;
    Socket so;

    public Connector_Tcp(String name) {
	this(name, false);
    }

    public Connector_Tcp(String name, boolean do_connect) {
	super(name);
	if (do_connect) {
	    connect();
	}
    }

    public Connector_Tcp(String name, PropagandaClient client) {
	super(name);
	connect();
	client.setConnectorAndAttach(this);
    }

    static ServerSocket sso;

    public static void initListen() throws IOException {
	initListen("localhost", 8899);
    }

    public static void initListen(int port) throws IOException {
	initListen("localhost", port);
    }

    public static void initListen(String host, int port) throws IOException {
	if (sso != null) {
	    return;
	}
	sso = new ServerSocket(port);
	getLogger().finest("started: " + sso.toString() + ' ' + port);
    }

    /**
     * Connect a client with Socket to server.
     */
    public boolean connect() {
	int port = sso.getLocalPort();
	String host = "localhost";
	return connect(host, port);
    }

    /**
     * Connect a client with Socket to server.
     */
    public boolean connect(String host, int port) {
	try {
	    so = new Socket(host, port);
	    getLogger().fine("Connector_Tcp: connected : " + so);
	    return true;

	} catch (IOException ex) {
	    getLogger().severe("Connector_Tcp: no-connection: ");
	}
	return false;
    }

    public void close() {
	try {
	    so.getInputStream().close();
	    so.getOutputStream().close();
	    so.close();
	} catch (IOException ex) {
	    getLogger().info("" + ex);
	}
    }

//     public void setDataOutputStream(DataOutputStream dos)
//     {
// 	this.dos = dos;
//     }
    /**
     * This is run in the server. Listen to incomming messages and send them to dispatcher
     */
    class MyServerThread implements Runnable {

	Socket so;

	MyServerThread(Socket so) {
	    this.so = so;
	}

	@Override
	public void run() {
	    System.err.println("Connector_Tcp: ServerThread running " + so);
	    try {
		BufferedReader rd = new BufferedReader(new InputStreamReader(so.getInputStream(), "utf-8"));

		for (;;) {
		    String sin = rd.readLine();

		    if (sin == null) {
			break;
		    }
		    try {
			Datagram datagram = new Datagram(sin);

			if (datagram.getSender() == defaultAddrType) {
			    ClientGhost gc = getDefaultClientGhost();
			    if (gc != null) {
				datagram.setSender(gc.getDefaultAddrType());
			    }
			}
			if (datagram.getReceiver() == defaultAddrType) {
			    ClientGhost gc = getDefaultClientGhost();
			    if (gc != null) {
				datagram.setReceiver(gc.getDefaultAddrType());
			    }
			}

			int dmCnt = server.dispatcher.dispatchMsg(Connector_Tcp.this, datagram, null);

			String receipt = datagram.getReceipt();
			if (receipt != null) {
			    transmitReceipt(receipt + ',' + dmCnt);
			}
		    } catch (Exception ex) {
			getLogger().log(Level.SEVERE, "Connector_Tcp: exception: [" + sin + "];", ex);
		    }
		}
	    } catch (IOException ex) {
	    }
	}
    }

    private static final ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    public void serve(final Socket so) {
	init_clientSocket(so);

	pool.purge();
	pool.execute(new MyServerThread(so));
	String state = pool.toString();

	getLogger().log(Level.INFO, "Connector_Tcp: plain thread pool state: " + state);
    }

    public static Socket acceptClient() throws IOException {
	Socket so = sso.accept();
	getLogger().finest("Connector_Tcp: accept: " + sso.toString());

	return so;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * Run in client side.
     */
    BufferedReader rd = null;

    @Override
    public Datagram recvMsg(long timeout_ms) {
	try {
	    try {
		if (rd == null) {
		    rd = new BufferedReader(new InputStreamReader(so.getInputStream(), "utf-8"));
		}

		for (;;) {
		    String sin = rd.readLine();
		    if (sin == null) {
			break;
		    }
		    Datagram datagram = new Datagram(sin);
		    return datagram;
		}
	    } catch (IOException ex) {
	    } catch (NullPointerException ex) {
	    }
	} finally {
	}
	return null;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    BufferedWriter cl_dos = null;

    public void init_clientSocket(Socket so) {
	try {
	    cl_dos = new BufferedWriter(new OutputStreamWriter(so.getOutputStream(), "utf-8"));
	} catch (IOException ex) {
	    getLogger().severe("Connector_Tcp: no-socket: " + so.toString());
	}
    }

    protected void transmitReceipt(String receipt) {
	if (cl_dos != null) {
	    try {
		synchronized (cl_dos) {
		    String s = "[" + receipt + "]\n";
		    cl_dos.write(s);
		    cl_dos.flush();
		}
	    } catch (IOException ex) {
	    }
	}
    }

    @Override
    protected void transmitMsgToClient(Datagram datagram) throws PropagandaException {
	if (cl_dos != null) {
	    try {
		synchronized (cl_dos) {
		    String s = datagram.getDatagramString();
		    cl_dos.write(s);
		    cl_dos.newLine();
		    cl_dos.flush();
		    getLogger().finest("Connector_Tcp: transm2client: " + ' ' + this + ' ' + datagram);

		}
	    } catch (IOException ex) {
		throw new PropagandaException("Connector_Tcp: transmit error 1: " + datagram);
	    }
	}
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    BufferedWriter cg_dos = null;

    @Override
    protected void transmitMsgToClientGhost(Datagram datagram) throws PropagandaException {
	try {
	    if (cg_dos == null) {
		cg_dos = new BufferedWriter(new OutputStreamWriter(so.getOutputStream(), "utf-8"));
	    }

	    synchronized (cg_dos) {
		String data = datagram.getDatagramString();
		cg_dos.write(data);
		cg_dos.newLine();
		cg_dos.flush();
		getLogger().finest("Connector_Tcp: transmitted: " + so.toString() + ' ' + data);
	    }

	    return;
	} catch (IOException ex) {
	    getLogger().finest("ex: " + so.toString() + ' ' + ex);
	    throw new PropagandaException("Connector_Tcp: transmit error 2: " + datagram);
	} catch (NullPointerException ex) {
	    getLogger().finest("ex: " + ex);
	    throw new PropagandaException("Connector_Tcp: transmit error 3: " + datagram);
	} finally {//catch (InterruptedException ex) {
	}
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    @Override
    public String toString() {
	return "Connector_Tcp{" + name + ',' + so + "}";
    }
}
