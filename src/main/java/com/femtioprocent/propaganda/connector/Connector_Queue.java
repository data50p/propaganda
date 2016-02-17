package com.femtioprocent.propaganda.connector;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.femtioprocent.propaganda.data.Datagram;
import com.femtioprocent.propaganda.exception.PropagandaException;

public class Connector_Queue extends PropagandaConnector {

    private BlockingQueue<Datagram> message_toserver_q = new LinkedBlockingQueue<Datagram>();
    private BlockingQueue<Datagram> message_toclient_q = new LinkedBlockingQueue<Datagram>();

    public Connector_Queue(String name) {
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
	return "Connector_Queue{" + name + "}";
    }
}
