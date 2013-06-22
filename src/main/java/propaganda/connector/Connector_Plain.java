package propaganda.connector;


import java.util.logging.Logger;
import static propaganda.context.Config.getLogger;
import static propaganda.data.AddrType.anonymousAddrType;
import static propaganda.data.AddrType.serverAddrType;
import static propaganda.data.AddrType.unknownAddrType;
import static propaganda.data.MessageType.register;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

import propaganda.client.PropagandaClient;
import propaganda.data.Datagram;
import propaganda.data.Message;
import propaganda.exception.PropagandaException;
import propaganda.server.PropagandaServer;
import propaganda.server.clientsupport.ClientGhost;
import fpg.sundry.Appl;
import fpg.sundry.S;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class Connector_Plain extends PropagandaConnector
{
    Socket so;

    public Connector_Plain(String name)
    {
	this(name, false);
    }

    public Connector_Plain(String name, boolean do_connect)
    {
	super(name);
	init();
	if ( do_connect )
	    connect();
    }

    public Connector_Plain(String name, PropagandaClient client)
    {
	super(name);
	init();
	connect();
	client.setConnectorAndAttach(this);
    }

    static ServerSocket sso;

    public static void initListen() throws IOException
    {
	initListen("localhost", 8899);
    }
    public static void initListen(int port) throws IOException
    {
	initListen("localhost", port);
    }
    public static void initListen(String host, int port) throws IOException
    {
	if ( sso != null )
	    return;

	sso = new ServerSocket(port);
	getLogger().finest("started: " + sso.toString() + ' ' + port);
    }

    /**
       Connect a client with Socket to server.
     */
    public boolean connect()
    {
	try {
	    int port = 8899;
	    String host = "localhost";

	    so = new Socket(host, port);
	    getLogger().fine("connected : " + so);
            return true;
	}
	catch (IOException ex) {
	    getLogger().severe("no-connection: ");
	}
        return false;
    }

    /**
       Connect a client with Socket to server.
     */
    public boolean connect(String host, int port)
    {
	try {
	    so = new Socket(host, port);
	    getLogger().fine("connected : " + so);
	    return true;

	}
	catch (IOException ex) {
	    getLogger().severe("no-connection: ");
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
       This is run in the server. Listen to incomming messages and send them to dispatcher
     */
    class MyServerThread extends Thread {
	Socket so;

	MyServerThread(Socket so)
	{
	    this.so = so;
	}

	@Override
         public void run()
	{
	    S.pL("ServerThread running " + so);
	    try {
		BufferedReader rd = new BufferedReader(new InputStreamReader(so.getInputStream(), "utf-8"));

		for(;;) {
		    String sin = rd.readLine();

		    if ( sin == null )
			break;
		    try {
			Datagram datagram = new Datagram(sin);

			if ( datagram.getSender() == unknownAddrType ) {
			    ClientGhost gc = getDefaultClientGhost();
			    if ( gc != null )
				datagram.setSender(gc.getDefaultAddrType());
			}
			if ( datagram.getReceiver() == unknownAddrType ) {
			    ClientGhost gc = getDefaultClientGhost();
			    if ( gc != null )
				datagram.setReceiver(gc.getDefaultAddrType());
			}

			int dmCnt = server.dispatcher.dispatchMsg(Connector_Plain.this, datagram);

			String receipt = datagram.getReceipt();
			if ( receipt != null ) {
			    transmitReceipt(receipt + ',' + dmCnt);
			}
		    }
		    catch (Exception ex) {
			getLogger().log(Level.SEVERE, "exception: [" + sin + "];", ex);
		    }
		}
	    }
	    catch (IOException ex) {
	    }
	}
    }

    public void serve(final Socket so)
    {
	init_clientSocket(so);

	MyServerThread th = new MyServerThread(so);
	th.start();
    }


    public static Socket acceptClient() throws IOException
    {
	Socket so = sso.accept();
	getLogger().finest("accept: " + sso.toString());

	return so;
    }

    /**
     */
    @Override
    public void attachServer(PropagandaServer server)
    {
	this.server = server;
    }

    void init()
    {
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
       Run in client side.
     */

    BufferedReader rd = null;

    @Override
    public  Datagram recvMsg(long timeout_ms)
    {
	try {
	    try {
		if ( rd == null )
		    rd = new BufferedReader(new InputStreamReader(so.getInputStream(), "utf-8"));

		for(;;) {
		    String sin = rd.readLine();
		    if ( sin == null )
			break;
		    Datagram datagram = new Datagram(sin);
		    return datagram;
		}
	    }
	    catch (IOException ex) {
	    }
	    catch (NullPointerException ex) {
	    }
	}
	finally {
	}
	return null;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    BufferedWriter cl_dos = null;

    public void init_clientSocket(Socket so)
    {
	try {
	    cl_dos = new BufferedWriter(new OutputStreamWriter(so.getOutputStream(), "utf-8"));
	}
	catch (IOException ex) {
	    getLogger().severe("no-socket: " + so.toString());
	}
    }

    protected void transmitReceipt(String receipt)
    {
	if ( cl_dos != null ) {
	    try {
		synchronized(cl_dos) {
                    String s = "[" + receipt + "]\n";
		    cl_dos.write(s);
		    cl_dos.flush();
		}
	    }
	    catch (IOException ex) {
	    }
	}
    }

    @Override
    protected void transmitMsgToClient(Datagram datagram) throws PropagandaException
    {
	if ( cl_dos != null ) {
	    try {
		synchronized(cl_dos) {
                    String s = datagram.getDatagramString();
                    cl_dos.write(s);
		    cl_dos.newLine();
		    cl_dos.flush();
		    getLogger().finest("transm2client: " + ' ' + this + ' ' + datagram);

		}
	    }
	    catch (IOException ex) {
		throw new PropagandaException("transmit error 1: " + datagram);
	    }
	}
    }



    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    BufferedWriter cg_dos = null;

    @Override
    protected void transmitMsgToClientGhost(Datagram datagram) throws PropagandaException
    {
	try {
	    if ( cg_dos == null )
		cg_dos = new BufferedWriter(new OutputStreamWriter(so.getOutputStream(), "utf-8"));

	    synchronized(cg_dos) {
		String data = datagram.getDatagramString();
		cg_dos.write(data);
		cg_dos.newLine();
		cg_dos.flush();
		getLogger().finest("transmitted: " + so.toString() + ' ' + data);
	    }

	    return;
	}
	catch (IOException ex) {
	    getLogger().finest("ex: " + so.toString() + ' ' + ex);
	    throw new PropagandaException("transmit error 2: " + datagram);
	}
	catch (NullPointerException ex) {
	    getLogger().finest("ex: " + ex);
	    throw new PropagandaException("transmit error 3: " + datagram);
	}
	finally {//catch (InterruptedException ex) {
	}
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    @Override
    public String toString()
    {
	return "Connector_Plain{" + name + ',' + so + "}";
    }

    static class Main extends Appl
    {
	class MainClient extends propaganda.client.PropagandaClient
	{
	    MainClient()
	    {
		super("MainPlain");
	    }

	    void start()
	    {
		try {
		    sendMsg(new Datagram(anonymousAddrType,
					 serverAddrType,
					 register,
					 new Message("mainplain.test@DEMO")));
		    for(;;) {
			Datagram datagram = getConnector().recvMsg();
			S.pL("Connector_Plain.Main got: " + datagram);
			if ( datagram == null )
			    break;
		    }
		}
		catch (PropagandaException ex) {
		    S.pL("MainClient: " + ex);
		}
	    }
	}

	@Override
         public void main() {
	    Connector_Plain conn = new Connector_Plain("MainPlain");
	    MainClient client = new MainClient();
	    conn.connect();

	    client.setConnector(conn);
	    conn.attachClient(client);
	    S.pL("conn " + conn);

	    client.start();
	}

	public static void main(String[] args)
	{
	    decodeArgs(args);
	    main(new Connector_Plain.Main());
	}
    }
}
