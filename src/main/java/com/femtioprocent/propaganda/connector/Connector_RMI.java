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
	Thread th = new Thread(() -> {
	    for (;;) {
		try {
		    Datagram datagram = message_toserver_q.take();
		    dispatchMsg(datagram);
		} catch (InterruptedException ex) {
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
}
