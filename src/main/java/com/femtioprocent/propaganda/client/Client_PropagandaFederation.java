package com.femtioprocent.propaganda.client;

import com.femtioprocent.fpd.sundry.S;
import com.femtioprocent.propaganda.server.clientsupport.ClientGhost;
import com.femtioprocent.propaganda.server.PropagandaServer;
import com.femtioprocent.propaganda.exception.PropagandaException;
import com.femtioprocent.propaganda.data.Datagram;
import com.femtioprocent.propaganda.data.Message;
import com.femtioprocent.propaganda.data.AddrType;
import com.femtioprocent.propaganda.connector.PropagandaConnector;

import java.util.Set;

import static com.femtioprocent.propaganda.data.AddrType.*;
import static com.femtioprocent.propaganda.context.Config.*;
import com.femtioprocent.propaganda.server.clientsupport.FederationServer;
import com.femtioprocent.propaganda.util.SecureUtil;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Admin of the server itself. Address '
 *
 * @'
 *
 * @author lars
 */
public class Client_PropagandaFederation extends PropagandaClient {

    PropagandaServer server;

    static AtomicInteger ordinal = new AtomicInteger((int) System.currentTimeMillis());

    public Client_PropagandaFederation(String name) {
	super(name);
	init();
    }

    @Override
    protected void init() {
	super.init();
	Thread th2 = new Thread(new Runnable() {
	    public void run() {
		for (;;) {
		    if (connector != null) {
			Datagram datagram = connector.recvMsg();
			getLogger().finest("datagram: " + S.ct() + ' ' + name + " =----> " + datagram);
			if ("join".equals(datagram.getMessage().getMessage())) {
			    try {
				String ad = datagram.getSender().getAddrTypeString();
				server.dispatcher.federatedghost_hm.put(ad, new FederationServer(ad));
				sendMsg(new Datagram(serverAddrType, datagram.getSender(),
					new Message("joined", ad)));
			    } catch (PropagandaException ex) {
				S.pL("PropagandaFederation.join: Can't send 'joined' (1) " + ex);
			    }
			}
		    } else {
			S.m_sleep(200);
		    }
		}
	    }
	});
	th2.start();
    }

    public void setServer(PropagandaServer server) {
	this.server = server;
    }
}
