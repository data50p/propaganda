package com.femtioprocent.propaganda.connector;

import com.femtioprocent.propaganda.client.PropagandaClient;
import com.femtioprocent.propaganda.data.Datagram;
import com.femtioprocent.propaganda.exception.PropagandaException;

public interface ClientConnector
{
    public void attachClient(PropagandaClient client);

    /**
       Send a message to server (= client ghost)
     */
    public abstract void sendMsg(Datagram datagram) throws PropagandaException;

    /**
       Receive a message from server (= client ghost). Block until there is a message
     */
    public abstract Datagram recvMsg();

    /**
       Receive a message from server (= client ghost). Block until there is a message, but max timeout_ms
     */
    public abstract Datagram recvMsg(long timeout_ms);
}
