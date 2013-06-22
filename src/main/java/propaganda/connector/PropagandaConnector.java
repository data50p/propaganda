package propaganda.connector;

import java.util.ArrayList;

import propaganda.client.PropagandaClient;
import propaganda.data.Datagram;
import propaganda.exception.PropagandaException;
import propaganda.server.PropagandaServer;
import propaganda.server.clientsupport.ClientGhost;

/**
   One client ghost for each registred name.
   Many AddrType-id for each Client Ghost.
 */
abstract public class PropagandaConnector implements ServerConnector, ClientConnector
{
    public String name; // is this necessary
    PropagandaServer server;
    private ArrayList<ClientGhost> client_ghost_li = new ArrayList<ClientGhost>();
    public PropagandaClient client;             // have client if it is Connector_{Queue,Local}

    public PropagandaConnector(String name)
    {
	this.name = name;
    }

    /**
     */
    public void attachServer(PropagandaServer server)
    {
	this.server = server;
    }

    /**
     */
    public void attachClient(PropagandaClient client)
    {
	this.client = client;
    }

    /**
     */
    public void attachClientGhost(ClientGhost client_ghost)
    {
	client_ghost_li.add(client_ghost);
    }

    public void dettachClientGhost(ClientGhost client_ghost)
    {
	client_ghost_li.remove(client_ghost);
    }

    public ClientGhost getDefaultClientGhost()
    {
	try {
	    return client_ghost_li.get(0);
	}
	catch (IndexOutOfBoundsException ex) {
	}
	catch (NullPointerException ex) {
	}
	return null;
    }

    public void close() {
    }

    // client
    public final void sendMsg(Datagram datagram) throws PropagandaException
    {
	transmitMsgToClientGhost(datagram);
    }

    public final Datagram recvMsg()
    {
	return recvMsg(-1);
    }
    public abstract Datagram recvMsg(long timeout_ms);

    protected abstract void transmitMsgToClientGhost(Datagram datagram) throws PropagandaException;

    // server (client ghost)
    public final synchronized void sendToClient(Datagram datagram) throws PropagandaException
    {
	transmitMsgToClient(datagram);
    }

    protected abstract void transmitMsgToClient(Datagram datagram) throws PropagandaException;

    public int dispatchMsg(Datagram datagram)
    {
	int dmCnt = 0;
	if ( server != null ) {
	    dmCnt = server.dispatcher.dispatchMsg(this, datagram);
	}
	return dmCnt;
    }

    public boolean validateDatagram(Datagram datagram) {
        for(ClientGhost cg : client_ghost_li) {
            if ( cg.matchAddrType(datagram.getSender()))
                return true;
        }
        return false;
    }

}
