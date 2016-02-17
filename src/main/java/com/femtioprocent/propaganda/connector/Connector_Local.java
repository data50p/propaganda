package com.femtioprocent.propaganda.connector;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.femtioprocent.propaganda.data.Datagram;
import com.femtioprocent.propaganda.exception.PropagandaException;

public class Connector_Local extends PropagandaConnector {

    private BlockingQueue<Datagram> message_q = new LinkedBlockingQueue<Datagram>();
    Thread th;

    public Connector_Local(String name) {
	super(name);
    }

    // - - - - - - - - - - - - - - - - - - - -
    /**
     * Called by a client sending this massage to server via corresponding client ghost
     */
    @Override
    public Datagram recvMsg(long timeout_ms) {
	try {
	    Datagram datagram = message_q.take();
	    return datagram;
	} catch (InterruptedException ex) {
	    return null;
	}
    }

    // - - - - - - - - - - - - - - - - - - - -
    /**
     * Send the data physically from client ghost to client.
     */
    @Override
    protected void transmitMsgToClient(Datagram datagram) throws PropagandaException {
	for (;;) {
	    try {
		message_q.put(datagram);
		return;
	    } catch (InterruptedException ex) {
		throw new PropagandaException("transmit error: " + datagram);
	    }
	}
    }

    /**
     * Called by a client sending this massage to server via corresponding client ghost
     */
    @Override
    protected void transmitMsgToClientGhost(Datagram datagram) throws PropagandaException {
	dispatchMsg(datagram);
    }

    // - - - - - - - - - - - - - - - - - - - -
    @Override
    public String toString() {
	return "Connector_Local{" + name + "}";
    }
}
