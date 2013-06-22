package propaganda.connector;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import propaganda.data.Datagram;
import propaganda.exception.PropagandaException;

public class Connector_Http extends PropagandaConnector
{
    Socket connectedSocket;
    private BlockingQueue<Datagram> message_toclient_q = new LinkedBlockingQueue<Datagram>();

    public Connector_Http(String name)
    {
	super(name);
	init();
    }

    private void init()
    {
    }

    @Override
    public  Datagram recvMsg(long timeout_ms)
    {

	try {
	    Datagram datagram;
	    if ( timeout_ms == -1 )
		datagram = message_toclient_q.take();
	    else
		datagram = message_toclient_q.poll(timeout_ms, TimeUnit.MILLISECONDS);
	    return datagram;
	}
	catch (InterruptedException ex) {
	}
	return null;
    }

    @Override
    protected void transmitMsgToClient(Datagram datagram) throws PropagandaException
    {
	try {
	    message_toclient_q.put(datagram);
	    return;// true;
	}
	catch (InterruptedException ex) {
	    throw new PropagandaException("transmit error: " + datagram);
	}
    }

    @Override
    protected void transmitMsgToClientGhost(Datagram datagram) throws PropagandaException
    {
	throw new PropagandaException("http Ghost?");
    }

    public void setSocket(Socket so)
    {
	this.connectedSocket = so;
    }

    public Socket getSocket()
    {
	return connectedSocket;
    }

    @Override
    public String toString() 
    {
	return "Connector_Http{" + name + "}";
    }

    public String getHostAddress() {
        return connectedSocket.getLocalAddress().getHostAddress();
    }
}
