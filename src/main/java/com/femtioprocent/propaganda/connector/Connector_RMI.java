package com.femtioprocent.propaganda.connector;

import static com.femtioprocent.propaganda.data.AddrType.anonymousAddrType;
import static com.femtioprocent.propaganda.data.AddrType.serverAddrType;
import static com.femtioprocent.propaganda.data.MessageType.register;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.femtioprocent.propaganda.data.Datagram;
import com.femtioprocent.propaganda.data.Message;
import com.femtioprocent.propaganda.exception.PropagandaException;
import com.femtioprocent.fpd.appl.Appl;
import com.femtioprocent.fpd.sundry.S;

public class Connector_RMI extends PropagandaConnector {

    private BlockingQueue<Datagram> message_toserver_q = new LinkedBlockingQueue<Datagram>();
    private BlockingQueue<Datagram> message_toclient_q = new LinkedBlockingQueue<Datagram>();

    public Connector_RMI(String name) {
	super(name);
	init();
    }

    void init() {
	Thread th = new Thread(new Runnable() {
	    public void run() {
		for (;;) {
		    try {
			Datagram datagram = message_toserver_q.take();
			dispatchMsg(datagram);
		    } catch (InterruptedException ex) {
		    }
		}
	    }
	});
	th.start();
    }

    @Override
    public Datagram recvMsg(long timeout_ms) {
	try {
	    Datagram datagram;
	    if (timeout_ms == -1) {
		datagram = message_toclient_q.take();
	    } else {
		datagram = message_toclient_q.poll(timeout_ms, TimeUnit.MILLISECONDS);
	    }
	    return datagram;
	} catch (InterruptedException ex) {
	}
	return null;
    }

    @Override
    protected void transmitMsgToClient(Datagram datagram) throws PropagandaException {
	try {
	    message_toclient_q.put(datagram);
	    return;// true;
	} catch (InterruptedException ex) {
	    throw new PropagandaException("transmit error: " + datagram);
	}
    }

    @Override
    protected void transmitMsgToClientGhost(Datagram datagram) throws PropagandaException {
	try {
	    message_toserver_q.put(datagram);
	    return;// true;
	} catch (InterruptedException ex) {
	    throw new PropagandaException("transmit ghost error: " + datagram);
	}
    }

    @Override
    public String toString() {
	return "Connector_RMI{" + name + "}";
    }

    static class Main extends Appl {

	class MainClient extends com.femtioprocent.propaganda.client.PropagandaClient {

	    MainClient() {
		super("Main");
	    }

	    void start() {
		try {
		    sendMsg(new Datagram(anonymousAddrType,
			    serverAddrType,
			    register,
			    new Message("main.test@DEMO")
		    ));
		    for (;;) {
			Datagram datagram = getConnector().recvMsg();
			System.err.println("got: " + datagram);
		    }
		} catch (PropagandaException ex) {
		    System.err.println("MainClient: " + ex);
		}
	    }
	}

	@Override
	public void main() {
	    Connector_RMI conn = new Connector_RMI("Main");
	    MainClient client = new MainClient();

	    client.setConnector(conn);
	    conn.attachClient(client);
	    System.err.println("conn " + conn);

	    client.start();
	}

	public static void main(String[] args) {
	    decodeArgs(args);
	    main(new Connector_RMI.Main());
	}
    }
}
