package com.femtioprocent.propaganda.server;

import static com.femtioprocent.propaganda.context.Config.getLogger;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;

import com.femtioprocent.propaganda.client.PropagandaClient;
import com.femtioprocent.propaganda.client.PropagandaClientFactory;
import com.femtioprocent.propaganda.client.Client_Admin;
import com.femtioprocent.propaganda.client.Client_Monitor;
import com.femtioprocent.propaganda.client.Client_Status;
import com.femtioprocent.propaganda.connector.PropagandaConnector;
import com.femtioprocent.propaganda.connector.PropagandaConnectorFactory;
import com.femtioprocent.propaganda.connector.Connector_Local;
import com.femtioprocent.propaganda.connector.Connector_Plain;
import com.femtioprocent.propaganda.data.AddrType;
import com.femtioprocent.propaganda.dispatcher.Dispatcher;
import com.femtioprocent.propaganda.exception.PropagandaException;
import com.femtioprocent.propaganda.server.clientsupport.ClientGhost;
import com.femtioprocent.fpd.sundry.S;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class PropagandaServer {

    private static PropagandaServer default_server;
    public HashMap<String, ClientGhost> clientghost_hm;
    public Client_Monitor client_monitor;
    public Client_Admin client_admin;
    public Client_Status client_status;
    public Dispatcher dispatcher;
    private Date started = new Date();
    public int serverPort = 8899;
    private String name = "";

    public PropagandaServer() {
        this(8899);
    }

    public PropagandaServer(int port) {
        serverPort = port;
        if (default_server == null) {
            default_server = this;
        }

        getLogger().setLevel(Level.ALL);
        initServer();
        initPropagandaClients();
        initBroadcastDiscoverServer();
    }

    private void initServer() {
        clientghost_hm = new HashMap<String, ClientGhost>();
        dispatcher = new Dispatcher(this, clientghost_hm);
    }

    private void initPropagandaClients() {
        try {
            PropagandaClient client;
            ;

            if (true) {
                getLogger().finest("--------- admin --------");
                client_admin = (Client_Admin) PropagandaClientFactory.create("Admin", "propaganda-admin");
                Connector_Local connector_local = (Connector_Local) client_admin.createConnector("Local", "propaganda-admin", this);
                // we need to attach the ClientGhost since Admin can't register itself in the normal way
                ClientGhost client_ghost = new ClientGhost(client_admin.getName(), client_admin.getName(), "$ADMIN", connector_local);
                connector_local.attachClientGhost(client_ghost);
                client_admin.setServer(this);
                addClientGhost(client_ghost);
                // not needed ->	    client_admin.register("ADMIN");
            }

            if (true) {
                getLogger().finest("--------- monitor --------");
                client_monitor = (Client_Monitor) PropagandaClientFactory.create("Monitor", "monitor-logger");
                client_monitor.createConnector("Local", "monitor-logger", this);
                client_monitor.register("MONITOR");
            }

            if (true) {
                getLogger().finest("--------- status --------");
                client_status = (Client_Status) PropagandaClientFactory.create("Status", "propaganda-status");
                client_status.createConnector("Local", "propaganda-status", this);
                client_status.register("$STATUS");
            }

        } catch (PropagandaException ex) {
            getLogger().log(Level.SEVERE, "Fatal! ", ex);
            System.exit(1);
        }

        PlainConnectorSupport plain_connector_support = new PlainConnectorSupport(serverPort);
        HttpConnectorSupport http_connector_support = new HttpConnectorSupport();
        WsConnectorSupport ws_connector_support = new WsConnectorSupport(8877);
        MqttConnectorSupport mqtt_connector_support = new MqttConnectorSupport();

// 	if ( server_appl != null && server_appl.flags.get("demo") != null ) {
// 	    Client demo1 = ClientFactory.create("Demo", "demo-1");
// 	    Client demo2 = ClientFactory.create("Demo", "demo-2");
// 	    Client demo3 = ClientFactory.create("Demo", "demo-3");
// 	}
    }
    static AtomicInteger defaultId = new AtomicInteger((int) (System.currentTimeMillis() % 1000000));

    public String createDefaultId(PropagandaConnector connector) {
        if (connector == null) {
            return "pid-" + defaultId.addAndGet(1);
        }
        return connector.name;
    }

    private void initBroadcastDiscoverServer() {
        BroadcastDiscoverServer bds = new BroadcastDiscoverServer();
        bds.start();
    }

    public class PlainConnectorSupport {

        private AtomicInteger cnt = new AtomicInteger(1000);
        int port;

        public PlainConnectorSupport(int port) {
            this.port = port;
            init();
            S.pL("Running PlainConnectorSupport: " + port);
        }

        void init() {
            Thread th = new Thread(new Runnable() {
                public void run() {
                    try {
                        for (;;) {
                            Connector_Plain.initListen(port);
                            Socket so = Connector_Plain.acceptClient();
                            Connector_Plain connector_plain = (Connector_Plain) PropagandaConnectorFactory.create("Plain", null, PropagandaServer.this, null);
                            connector_plain.serve(so);
                        }
                    } catch (IOException ex) {
                        S.pL("Can't run PlainConnectorSupport: " + ex);
                    }
                }
            });
            th.start();
        }
    }

    class HttpConnectorSupport {

        HttpServer http_server;
        int httpPort = 8888;

        HttpConnectorSupport() {
            init();
            S.pL("Running HttpConnectorSupport: " + httpPort);
        }

        HttpConnectorSupport(int httpPort) {
            this.httpPort = httpPort;
            init();
            S.pL("Running HttpConnectorSupport: " + httpPort);
        }

        void init() {
            try {
                http_server = new HttpServer(PropagandaServer.this, httpPort);
                http_server.start();
            } catch (IOException ex) {
                getLogger().log(Level.SEVERE, "Fatal! Can't start http server: port " + httpPort, ex);
            }
        }
    }

    public class WsConnectorSupport {

        private AtomicInteger cnt = new AtomicInteger(1000);
        int port;

        public WsConnectorSupport(int port) {
            this.port = port;
            init();
            S.pL("Running WsConnectorSupport: " + port);
        }

        void init() {
            Thread th = new Thread(new Runnable() {
                public void run() {
                    try {
                        for (;;) {
                            HttpWSServer.start(PropagandaServer.this);
                            break;
                        }
                    } catch (Exception ex) {
                        S.pL("Can't run WsConnectorSupport: " + ex);
                    }
                }
            });
            th.start();
        }
    }

    public class MqttConnectorSupport {

        private AtomicInteger cnt = new AtomicInteger(1000);
        int port;

        public MqttConnectorSupport() {
            this.port = port;
            init();
            S.pL("Running MqttConnectorSupport: " + port);
        }

        void init() {
            Thread th = new Thread(new Runnable() {
                public void run() {
                    try {
                        for (;;) {
                            MQTTServer s = new MQTTServer(PropagandaServer.this);
                            s.start(PropagandaServer.this);
                            for (;;) {
                                s.processMqttPayload();
                            }
                        }
                    } catch (Exception ex) {
                        S.pL("Can't run MqttConnectorSupport: " + ex);
                    }
                }
            });
            th.start();
        }
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * using the secured address (not SHA1)
     *
     * @param client_name
     * @return
     */
    public ClientGhost getRegisteredClientGhost(String client_name) {
        client_name = client_name.toLowerCase();

        getLogger().finest(": " + client_name + ' ' + clientghost_hm.keySet() + ' ' + clientghost_hm.get(client_name.toLowerCase()));
        return clientghost_hm.get(client_name.toLowerCase());
    }

    /**
     * Using the secure address (SHA1)
     *
     * @param secured_client_name
     * @return
     */
    public ClientGhost getRegisteredClientGhostSecured(String secured_client_name) {
        secured_client_name = secured_client_name.toLowerCase();

        for (ClientGhost cg : clientghost_hm.values()) {
            getLogger().finest(": trying " + secured_client_name + ' ' + cg);
            if (cg.getId().equals(secured_client_name)) {
                getLogger().finest(": " + secured_client_name + ' ' + cg);
                return cg;
            }
        }
        return null;
    }

    public void deleteRegisteredClientGhost(String client_name, String addr_type_id, PropagandaConnector orig_connector) {
        client_name = client_name.toLowerCase();

        ClientGhost cg = clientghost_hm.get(client_name);
        if (cg == null) {
            return;
        }
        //	if ( cg.
        S.pL("removing " + cg);
        if (cg.removeAddrTypeId(addr_type_id)) {
            clientghost_hm.remove(client_name);
        }
        S.pL("removed: " + client_name + ' ' + addr_type_id + ' ' + clientghost_hm);
    }

    /**
     * Any old client with same name are removed.
     */
    public boolean addClientGhost(ClientGhost client_ghost) {
        String client_name = client_ghost.getId().toLowerCase();

        boolean again = clientghost_hm.containsKey(client_name);

        clientghost_hm.put(client_name, client_ghost);
        getLogger().finest("added: " + again + ' ' + client_ghost);

        return again;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public String getStatus(int level) {
        return "started=" + started.toString().replace(' ', '_') + " "
                + " clients=" + (level > 0 ? clientghost_hm.toString() : clientghost_hm.keySet()) + ' '
                + dispatcher.getStatus(level);
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public static PropagandaServer getDefaultServer(String name) {
        if (default_server == null) {
            default_server = new PropagandaServer();
            default_server.name = name;
        }
        return default_server;
    }

    public static PropagandaServer getDefaultServer() {
        return getDefaultServer("DefaultPropagandaServer");
    }
    
    public String getName() {
        return name;
    }

    public String toString() {
        return "Server{port=" + serverPort + "}";
    }
}
