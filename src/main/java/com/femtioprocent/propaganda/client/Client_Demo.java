package com.femtioprocent.propaganda.client;

import static java.lang.System.exit;
import static com.femtioprocent.propaganda.context.Config.getLogger;
import static com.femtioprocent.propaganda.data.AddrType.createAddrType;

import java.util.logging.Level;

import com.femtioprocent.propaganda.connector.PropagandaConnector;
import com.femtioprocent.propaganda.connector.PropagandaConnectorFactory;
import com.femtioprocent.propaganda.data.AddrType;
import com.femtioprocent.propaganda.data.Datagram;
import com.femtioprocent.propaganda.data.Message;
import com.femtioprocent.propaganda.data.MessageType;
import com.femtioprocent.propaganda.exception.PropagandaException;
import com.femtioprocent.propaganda.server.PropagandaServer;
import com.femtioprocent.fpd.sundry.S;
import java.util.Random;

public class Client_Demo extends PropagandaClient {

    public Client_Demo(String name) {
	super(name);
	init();
    }

    @Override
    protected void init() {
	final PropagandaConnector connector;

	connector = PropagandaConnectorFactory.create("Queue", name, PropagandaServer.getDefaultServer(), this);
	try {
	    register("DEMO");
	} catch (PropagandaException ex) {
	    getLogger().log(Level.SEVERE, "register: ", ex);
	    exit(0);
	}

	Thread th = new Thread(new Runnable() {
	    public void run() {
		try {
		    AddrType receiver_at = createAddrType("*@DEMO");

		    int cnt = 0;
		    Random rand = new Random();
		    for (;;) {
			S.m_sleep(rand.nextInt(cnt == 0 ? 2000 : 20000));
			sendMsg(new Datagram(getDefaultAddrType(), receiver_at, new Message("hello-" + name + '-' + cnt++)));
		    }
		} catch (PropagandaException ex) {
		    System.err.println("Demo: " + ex);
		}
	    }
	});
	th.start();

	Thread th2 = new Thread(new Runnable() {
	    public void run() {
		try {
		    for (;;) {
			Datagram datagram = connector.recvMsg();
			if (standardProcessMessage(datagram, MessageType.plain) == MessageTypeFilter.FILTERED) {
			    getLogger().finest("datagram: " + S.ct() + ' ' + name + " =----> " + datagram);
			}
		    }
		} catch (PropagandaException ex) {
		    System.err.println("Demo " + ex);
		}
	    }
	});
	th2.start();
    }

    public void run() {
    }
}
