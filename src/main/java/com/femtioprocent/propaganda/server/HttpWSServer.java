package com.femtioprocent.propaganda.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import com.femtioprocent.propaganda.client.PropagandaClient;
import com.femtioprocent.propaganda.connector.Connector_Queue;
import com.femtioprocent.propaganda.connector.PropagandaConnector;
import com.femtioprocent.propaganda.connector.PropagandaConnectorFactory;
import com.femtioprocent.propaganda.data.AddrType;
import com.femtioprocent.propaganda.data.Datagram;

@WebService(name = "HttpWSServive", serviceName = "HttpWSService", portName = "HttpWSServiceSOAPHttpPort")
public class HttpWSServer {

    static int cnt = 1;
    static PropagandaServer server;
    static public Endpoint e;
    Connector_Queue connector;
    AtomicInteger ws_cnt = new AtomicInteger(1000);

    static class WsClient extends PropagandaClient {

        public WsClient() {
            super("Ws");
        }
    }
    WsClient wsClient;

    public HttpWSServer() {
        wsClient = new WsClient();
        connector = (Connector_Queue) PropagandaConnectorFactory.create("Queue", "WS-" + ws_cnt.incrementAndGet(), server, wsClient);
    }

    @WebMethod
    public String[] processDatagram(String[] datagramString, int to) {
        List<String> list = new ArrayList<String>();
        List<String> list2 = new ArrayList<String>();
        String myId = autoRegister(connector, list2);
        for (String dg_s : list2) {
            Datagram datagram = new Datagram(dg_s);
            int n = server.dispatcher.dispatchMsg(connector, datagram);
        }
        for (String dg_s : datagramString) {
            if (connector.getDefaultClientGhost() != null) {
                final AddrType defaultAddrType = connector.getDefaultClientGhost().getDefaultAddrType();
                dg_s = dg_s.replaceFirst("^[ ]*_ ", defaultAddrType.getAddrTypeString() + " ");
            }
            Datagram datagram = new Datagram(dg_s);
            int n = server.dispatcher.dispatchMsg(connector, datagram);
        }
        if ( to == 0 )
            to = 1000;
        for (;;) {
            String r = retrieveDatagram(to);
            list.add(r);
            if (r == null) {
                Datagram ur = new Datagram(autoUnRegister(myId));
                int n = server.dispatcher.dispatchMsg(connector, ur);
                return list.toArray(new String[list.size()]);
            }
        }
        
    }

    private String retrieveDatagram(int to) {
        Datagram recv_datagram = connector.recvMsg(to);
        return recv_datagram == null ? null : recv_datagram.getDatagramString();
    }

    public static void start(PropagandaServer server) {
        HttpWSServer.server = server;
        e = Endpoint.publish("http://0.0.0.0:8877/propaganda", new HttpWSServer());
    }
    
    AtomicInteger i_cnt = new AtomicInteger();
    
    private String autoRegister(PropagandaConnector connector, List<String> hm) {
//        String myId = "" + connector.so.getLocalAddress().getHostAddress() + '-' + connector.so.getPort()+ '-' + this.hashCode() + '@' + "AUTOREGISTRED";
        String myId = "" + "WS" + '-' + connector.name + '-' + i_cnt.incrementAndGet() + '@' + "AUTOREGISTRED" + '-' + this.hashCode();
        hm.add(". @ register;request-id " + myId);
        return myId;
    }

    private String autoUnRegister(String autoAddr) {
        return autoAddr + " @ unregister;";
    }

}
