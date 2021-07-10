package com.femtioprocent.propaganda.client;

import com.femtioprocent.fpd.sundry.S;
import com.femtioprocent.propaganda.server.PropagandaServer;
import com.femtioprocent.propaganda.exception.PropagandaException;
import com.femtioprocent.propaganda.data.Datagram;
import com.femtioprocent.propaganda.data.Message;
import com.femtioprocent.propaganda.data.MessageType;

import static com.femtioprocent.propaganda.context.Config.*;

public class Client_Status extends PropagandaClient {

    public Client_Status(String name) {
	super(name);
	init();
    }

    protected void init() {
	final int[] status_cnt = new int[1];

	Thread th = new Thread(() -> {
	    while (connector == null) {
		S.m_sleep(1000);
	    }

	    for (;;) {
		try {
		    Datagram datagram = recvMsg();
		    getLogger().finest("msg: " + S.ct() + ' ' + name + " → " + datagram);

		    if (standardProcessMessage(datagram) == MessageTypeFilter.NOT_PROCESSED) {
			if (false) {
			    status_cnt[0]++;
			    PropagandaServer server = PropagandaServer.getDefaultServer();

			    int level = 0;
			    try {
				level = Integer.parseInt(datagram.getMessage().getMessage());
			    } catch (NumberFormatException ex) {
			    }

			    sendMsg(new Datagram(getDefaultAddrType(),
				    datagram.getSender(),
				    MessageType.plain,
				    new Message("status",
					    "status-cnt=" + status_cnt[0] + ' '
					    + server.getStatus(level)
				    )));
			}
		    }
		} catch (PropagandaException ex) {
		    System.err.println("Status: " + ex);
		}
	    }
	});
	th.start();
    }
}
