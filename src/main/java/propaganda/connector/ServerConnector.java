package propaganda.connector;

import propaganda.data.Datagram;
import propaganda.exception.PropagandaException;
import propaganda.server.PropagandaServer;
import propaganda.server.clientsupport.ClientGhost;

public interface ServerConnector
{
    /**
     */
    public void attachServer(PropagandaServer server);

    /**
     */
    public void attachClientGhost(ClientGhost client_ghost);



    /**
       Send a message to the corresponding client
     */
    public void sendToClient(Datagram datagram) throws PropagandaException;
}
