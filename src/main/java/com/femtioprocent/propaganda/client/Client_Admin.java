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
import com.femtioprocent.propaganda.util.SecureUtil;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Admin of the server itself. Address '
 *
 * @'
 *
 * @author lars
 */
public class Client_Admin extends PropagandaClient {

    PropagandaServer server;

    public Client_Admin(String name) {
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
                        if ("list-id".equals(datagram.getMessage().getMessage())) {
                            try {
                                Set<String> set = new HashSet<String>();
                                for (final ClientGhost cg : server.clientghost_hm.values()) {
                                    set.add(cg.getDefaultSecureAddrType().getUnsecureId());
                                }
                                sendMsg(new Datagram(serverAddrType,
                                        datagram.getSender(),
                                        new Message("list-id-is",
                                        "" + set.toString().replace(" ", ""))));
                            } catch (PropagandaException ex) {
                                S.pL("ClientGhost.registerMsg: Can't send 'registered' (1) " + ex);
                            }

                        } else if ("list".equals(datagram.getMessage().getMessage())) {
                            try {
                                HashMap<String, ClientGhost> map = new HashMap<String, ClientGhost>();
                                for (final ClientGhost cg : server.clientghost_hm.values()) {
                                    map.put(cg.getDefaultAddrType().getId(), cg);
                                }
                                StringBuilder sb = new StringBuilder();
                                for (Map.Entry<String, ClientGhost> ent : map.entrySet()) {
                                    String id = ent.getKey();
                                    ClientGhost cg = ent.getValue();
                                    if ( sb.length() > 0 )
                                        sb.append(";");
                                    sb.append(cg.getDefaultSecureAddrType().getUnsecureId());
                                    final Set<String> atgSet = cg.getAddrTypeGroupSet();
                                    sb.append("@" + atgSet.toString().replace(" ", ""));
                                }
                                sendMsg(new Datagram(serverAddrType,
                                        datagram.getSender(),
                                        new Message("list-is",
                                        "" + sb.toString())));
                            } catch (PropagandaException ex) {
                                S.pL("ClientGhost.registerMsg: Can't send 'registered' (1) " + ex);
                            }

                        } else if ("list-group".equals(datagram.getMessage().getMessage())) {
                            try {
                                Set<String> set = new HashSet<String>();
                                for (final ClientGhost cg : server.clientghost_hm.values()) {
                                    set.addAll(cg.getAddrTypeGroupSet());
                                }
                                sendMsg(new Datagram(serverAddrType,
                                        datagram.getSender(),
                                        new Message("list-group-is",
                                        "" + set.toString().replace(" ", ""))));
                            } catch (PropagandaException ex) {
                                S.pL("ClientGhost.registerMsg: Can't send 'registered' (1) " + ex);
                            }

                        } else if ("version".equals(datagram.getMessage().getMessage())) {
                            try {
                                sendMsg(new Datagram(serverAddrType,
                                        datagram.getSender(),
                                        new Message("version",
                                        "" + com.femtioprocent.propaganda.Version.projectVersion + ' ' + com.femtioprocent.propaganda.Version.mavenBuildTimestamp)));
                            } catch (PropagandaException ex) {
                                S.pL("ClientGhost.registerMsg: Can't send 'registered' (1) " + ex);
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

    private String mkAddrType(String s) {
        if (s == null || s.length() == 0) {
            return null;
        }
        if (s.startsWith("@")) {
            String nId = server.createDefaultId();
            return "" + nId + s;
        } else {
            return s;
        }
    }

    public ClientGhost registerMsg(Datagram datagram, PropagandaConnector orig_connector) {
        getLogger().finest("args: " + datagram + ' ' + orig_connector);

        AddrType client_addr = null;
        boolean request_status = false;

        if ("request-status".equals(datagram.getMessage().getMessage())) {
            client_addr = createAddrType(mkAddrType(datagram.getMessage().getAddendum()));
            request_status = true;
        } else if ("request-id".equals(datagram.getMessage().getMessage())) {
            client_addr = createAddrType(mkAddrType(datagram.getMessage().getAddendum()));
        } else if ("request-secure-id".equals(datagram.getMessage().getMessage())) {
            String at = datagram.getMessage().getAddendum();
            String salt = "";
            int ix = at.indexOf(' ');
            if ( ix > 0 ) {
                salt = at.substring(ix).trim();
                at = at.substring(0, ix).trim();
            }
            client_addr = createSecureAddrType(mkAddrType(at), salt);
        } else if ("unsecure-id".equals(datagram.getMessage().getMessage())) {
            client_addr = datagram.getSender();
            ClientGhost client_ghost = null;
            client_ghost = server.getRegisteredClientGhostSecured(client_addr.getId());
            try {
                client_ghost.sendToClient(new Datagram(serverAddrType,
                        client_addr,
                        new Message("unsecure-id-is",
                        SecureUtil.lookupUnsecureId(client_ghost.getId()) + "@" + client_addr.getAddrTypeGroup())));
            } catch (PropagandaException ex) {
                Logger.getLogger(Client_Admin.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
        if (datagram.getMessage().getAddendum() == null) {
            final String message = datagram.getMessage().getMessage();
            client_addr = createAddrType(mkAddrType(message));
        }

        if (client_addr != null) {
            getLogger().finest("register: " + client_addr);
            ClientGhost client_ghost = null;
            if (!client_addr.getAddrTypeGroup().equals("$ADMIN")) {
                client_ghost = server.getRegisteredClientGhost(client_addr.getId());
                if (client_ghost == null) {
                    if (request_status) {
                        // can't send; no recipient
                        return null;
                    } else {
                        client_ghost = new ClientGhost(client_addr.getId(), client_addr.getUnsecureIdALt(), client_addr.getAddrTypeGroup(), orig_connector);
                        orig_connector.attachClientGhost(client_ghost); // this connector might have another ClGh.othername FATAL?
                        boolean again = server.addClientGhost(client_ghost);
                        try {
                            client_ghost.sendToClient(new Datagram(serverAddrType,
                                    client_addr,
                                    new Message("registered",
                                    client_addr.getAddrTypeString() + " @" + client_ghost.getAddrTypeGroupSet())));
                        } catch (PropagandaException ex) {
                            S.pL("ClientGhost.registerMsg: Can't send 'registered' (1) " + ex);
                        }
                    }
                } else {
                    PropagandaConnector current_connector = client_ghost.getConnector();

                    getLogger().finest("client_ghost exist, connectors: " + client_ghost + ' ' + current_connector + ' ' + orig_connector);

                    String status = " again";
                    if (current_connector != orig_connector) {  // the old connector is invalid, use the new one
                        if (request_status) {
                            // do not send; no recipient
                            return null;
                        } else {
                            try {
                                client_ghost.sendToClient(new Datagram(serverAddrType,
                                        client_addr,
                                        new Message("hostile-takeover",
                                        "")));
                            } catch (PropagandaException ex) {
                                S.pL("ClientGhost.registerMsg: Can't send 'registered' (2) " + ex);
                            }
                            client_ghost.setConnector(orig_connector);
                            orig_connector.attachClientGhost(client_ghost);
                            status = " hostile-takeover";
                            try {
                                client_ghost.sendToClient(new Datagram(serverAddrType,
                                        client_addr,
                                        new Message("registered",
                                        client_addr.getAddrTypeString() + " @" + client_ghost.getAddrTypeGroupSet() + status)));
                            } catch (PropagandaException ex) {
                                S.pL("ClientGhost.registerMsg: Can't send 'registered' (2) " + ex);
                            }
                        }
                    } else {
                        if (request_status) {
                            try {
                                client_ghost.sendToClient(new Datagram(serverAddrType,
                                        client_addr,
                                        new Message("already-registered",
                                        client_addr.getAddrTypeString() + " @" + client_ghost.getAddrTypeGroupSet())));
                            } catch (PropagandaException ex) {
                                S.pL("ClientGhost.registerMsg: Can't send 'registered' (1) " + ex);
                            }
                        } else {
                            client_ghost.addAddrTypeGroup(client_addr.getAddrTypeGroup());
                            try {
                                client_ghost.sendToClient(new Datagram(serverAddrType,
                                        client_addr,
                                        new Message("registered",
                                        client_addr.getAddrTypeString() + " @" + client_ghost.getAddrTypeGroupSet() + status)));
                            } catch (PropagandaException ex) {
                                S.pL("ClientGhost.registerMsg: Can't send 'registered' (2) " + ex);
                            }
                        }
                    }
                }
            } else {
                getLogger().finest("OK: already registered my self: " + client_addr);
            }
            return client_ghost;
        }
        return null;
    }

    public void unregisterMsg(Datagram datagram, PropagandaConnector orig_connector) {
        getLogger().finest("args: " + datagram + ' ' + orig_connector);

        AddrType client_addr = datagram.getSender();
//        ClientGhost client_ghost = server.getRegisteredClientGhost(client_addr.getId());
        ClientGhost client_ghost = server.getRegisteredClientGhostSecured(client_addr.getId());
        if (client_ghost != null) {
            PropagandaConnector current_connector = client_ghost.getConnector();
            current_connector.dettachClientGhost(client_ghost);
            server.deleteRegisteredClientGhost(client_addr.getId(), client_addr.getAddrTypeGroup(), orig_connector); // remove client ghost if it has no more addr_type_id left
        }
    }
}
