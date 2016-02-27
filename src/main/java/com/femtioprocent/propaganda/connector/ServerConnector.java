package com.femtioprocent.propaganda.connector;

import com.femtioprocent.propaganda.data.Datagram;
import com.femtioprocent.propaganda.exception.PropagandaException;
import com.femtioprocent.propaganda.server.PropagandaServer;
import com.femtioprocent.propaganda.server.federation.ClientGhost;

public interface ServerConnector {

    /**
     */
    public void attachServer(PropagandaServer server);

    /**
     */
    public void attachClientGhost(ClientGhost client_ghost);

    /**
     * Send a message to the corresponding client
     */
    public void sendToClient(Datagram datagram) throws PropagandaException;
}
