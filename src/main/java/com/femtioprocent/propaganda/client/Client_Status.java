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

    @Override
    protected void init() {
        super.init();
        final int[] status_cnt = new int[1];

        Thread th2 = new Thread(new Runnable() {
            public void run() {
                while (connector == null) {
                    S.m_sleep(1000);
                    System.err.print(".");
                }

                for (;;) {
                    try {
                        Datagram datagram = connector.recvMsg();
                        getLogger().finest("msg: " + S.ct() + ' ' + name + " → " + datagram);

                        if (datagram.getMessageType() == MessageType.ping) {
                            sendMsg(new Datagram(getDefaultAddrType(), datagram.getSender(), MessageType.pong, datagram.getMessage()));
                        } else if (datagram.getMessageType() == MessageType.pong) {
                        } else {
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
                        S.pL("Status: " + ex);
                    }
                }
            }
        });
        th2.start();
    }
}
