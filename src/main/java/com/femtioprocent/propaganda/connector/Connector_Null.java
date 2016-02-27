package com.femtioprocent.propaganda.connector;

import com.femtioprocent.fpd.sundry.S;
import com.femtioprocent.propaganda.data.Datagram;

public class Connector_Null extends PropagandaConnector {

    String name;

    public Connector_Null(String name) {
	super(name);
    }

    /**
     * Send a message to server (= client ghost)
     */
    @Override
    public Datagram recvMsg(long timeout_ms) {
	S.m_sleep((int) timeout_ms);
	return null;
    }

    @Override
    public void transmitMsgToClient(Datagram datagram) {
    }

    @Override
    protected void transmitMsgToClientGhost(Datagram datagram) {
    }
}
