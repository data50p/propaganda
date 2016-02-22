package com.femtioprocent.propaganda.client;

import com.femtioprocent.fpd.sundry.S;
import com.femtioprocent.propaganda.exception.PropagandaException;
import com.femtioprocent.propaganda.data.Datagram;
import com.femtioprocent.propaganda.data.MessageType;

import static com.femtioprocent.propaganda.context.Config.*;

public class Client_Monitor extends PropagandaClient {

    public Client_Monitor(String name) {
	super(name);
	init();
    }

    protected void init() {
	Thread th = new Thread(() -> {
	    for (;;) {
		try {
		    if (connector != null) {
			Datagram datagram = connector.recvMsg();
			if (standardProcessMessage(datagram, MessageType.plain) == MessageTypeFilter.FILTERED) {
			    getLogger("monitor").fine("got: " + S.ct() + ' ' + name + " → " + datagram);
			}
		    } else {
			S.m_sleep(200);
		    }
		} catch (PropagandaException ex) {
		    System.err.println("Monitor: " + ex);
		}
	    }
	});
	th.start();
    }
}
