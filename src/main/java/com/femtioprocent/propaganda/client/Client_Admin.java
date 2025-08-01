package com.femtioprocent.propaganda.client;

import com.femtioprocent.fpd.sundry.S;
import com.femtioprocent.propaganda.Constants;
import com.femtioprocent.propaganda.server.federation.ClientGhost;
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
import com.femtioprocent.propaganda.util.Util;
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
public class Client_Admin extends PropagandaClient {

    PropagandaServer server;

    static AtomicInteger ordinal = new AtomicInteger((int) System.currentTimeMillis());

    public Client_Admin(String name) {
	super(name);
	init();
    }

    protected void init() {
	Thread th = new Thread(() -> {
	    for (;;) {
		if (connector != null) {
		    Datagram datagram = recvMsg();
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
			    System.err.println("ClientGhost.registerMsg: Can't send 'registered' (1) " + ex);
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
				if (sb.length() > 0) {
				    sb.append(";");
				}
				sb.append(cg.getDefaultSecureAddrType().getUnsecureId());
				final Set<String> atgSet = cg.getAddrTypeGroupSet();
				sb.append("@" + atgSet.toString().replace(" ", ""));
			    }
			    sendMsg(new Datagram(serverAddrType,
				    datagram.getSender(),
				    new Message("list-is",
					    "" + sb.toString())));
			} catch (PropagandaException ex) {
			    System.err.println("ClientGhost.registerMsg: Can't send 'registered' (1) " + ex);
			}

		    } else if ("list-me".equals(datagram.getMessage().getMessage())) {
			try {
			    HashMap<String, ClientGhost> map = new HashMap<String, ClientGhost>();
			    for (final ClientGhost cg : server.clientghost_hm.values()) {
				if ( cg.getDefaultAddrType().getId().equals(datagram.getSender().getId()) )
				    map.put(cg.getDefaultAddrType().getId(), cg);
			    }
			    StringBuilder sb = new StringBuilder();
			    for (Map.Entry<String, ClientGhost> ent : map.entrySet()) {
				String id = ent.getKey();
				ClientGhost cg = ent.getValue();
				if (sb.length() > 0) {
				    sb.append(";");
				}
				sb.append(cg.getDefaultSecureAddrType().getUnsecureId());
				final Set<String> atgSet = cg.getAddrTypeGroupSet();
				sb.append("@" + atgSet.toString().replace(" ", ""));
			    }
			    sendMsg(new Datagram(serverAddrType,
				    datagram.getSender(),
				    new Message("list-me-is",
					    "" + sb.toString())));
			} catch (PropagandaException ex) {
			    System.err.println("ClientGhost.registerMsg: Can't send 'registered' (1) " + ex);
			}

		    } else if ("list-group".equals(datagram.getMessage().getMessage())) {
			try {
			    Set<String> set = new HashSet<>();
			    for (final ClientGhost cg : server.clientghost_hm.values()) {
				set.addAll(cg.getAddrTypeGroupSet());
			    }
			    sendMsg(new Datagram(serverAddrType,
				    datagram.getSender(),
				    new Message("list-group-is",
					    "" + set.toString().replace(" ", ""))));
			} catch (PropagandaException ex) {
			    System.err.println("ClientGhost.registerMsg: Can't send 'registered' (1) " + ex);
			}

		    } else if ("list-connector".equals(datagram.getMessage().getMessage())) {
			try {
			    HashMap<String, ClientGhost> map = new HashMap<>();
			    for (final ClientGhost cg : server.clientghost_hm.values()) {
				map.put(cg.getDefaultAddrType().getId(), cg);
			    }
			    StringBuilder sb = new StringBuilder();
			    for (Map.Entry<String, ClientGhost> ent : map.entrySet()) {
				String id = ent.getKey();
				ClientGhost cg = ent.getValue();
				if (sb.length() > 0) {
				    sb.append(";");
				}
				sb.append(cg.getConnector().name);
				sb.append(Constants.CONNECTOR_INDICATOR);
				sb.append(cg.getDefaultSecureAddrType().getUnsecureId());
				sb.append(cg.getAddrTypeGroupSet().toString().replace(" ", ""));
			    }
			    sendMsg(new Datagram(serverAddrType,
				    datagram.getSender(),
				    new Message("list-connector-is",
					    "" + sb.toString())));
			} catch (PropagandaException ex) {
			    System.err.println("ClientGhost.registerMsg: Can't send 'registered' (1) " + ex);
			}

		    } else if ("list-ports".equals(datagram.getMessage().getMessage())) {
			try {
			    HashMap<String, ClientGhost> map = new HashMap<>();
			    for (final ClientGhost cg : server.clientghost_hm.values()) {
				map.put(cg.getDefaultAddrType().getId(), cg);
			    }
			    StringBuilder sb = new StringBuilder();
			    sb.append("port=");
			    sb.append(PropagandaServer.DEFAULT_SERVER_PORT);
			    sb.append(" http=");
			    sb.append(PropagandaServer.DEFAULT_HTTP_PORT);
			    sb.append(" ws=");
			    sb.append(PropagandaServer.DEFAULT_WS_PORT);
			    sb.append(" discover=");
			    sb.append(PropagandaServer.DEFAULT_DISCOVER_PORT);
			    sb.append(" fed=");
			    sb.append(PropagandaServer.DEFAULT_FEDERATION_PORT);
			    sendMsg(new Datagram(serverAddrType,
				    datagram.getSender(),
				    new Message("list-ports-is",
					    "" + sb.toString())));
			} catch (PropagandaException ex) {
			    System.err.println("ClientGhost.registerMsg: Can't send 'registered' (1) " + ex);
			}

		    } else if ("version".equals(datagram.getMessage().getMessage())) {
			try {
			    sendMsg(new Datagram(serverAddrType,
				    datagram.getSender(),
				    new Message("version",
					    "" + com.femtioprocent.propaganda.Version.projectVersion + ' ' + com.femtioprocent.propaganda.Version.mavenBuildTimestamp)));
			} catch (PropagandaException ex) {
			    System.err.println("ClientGhost.registerMsg: Can't send 'registered' (1) " + ex);
			}

		    }
		} else {
		    S.m_sleep(200);
		}
	    }
	});
	th.start();
    }

    public void setServer(PropagandaServer server) {
	this.server = server;
    }

    private String mkAddrType(String s, PropagandaConnector connector) {
	if (s == null || s.length() == 0) {
	    return mkAddrType();
	}
	if (s.startsWith("@")) {
	    String nId = server.createDefaultId(connector);
	    return "cb-" + SecureUtil.getSecureId(nId + s, "connectorBound");
	} else {
	    return s;
	}
    }

    private String mkAddrType() {
	return "propaganda-" + SecureUtil.getSecureId("" + ordinal.incrementAndGet(), "autoId");
    }

    public ClientGhost registerMsg(Datagram datagram, PropagandaConnector orig_connector) {
	getLogger().finest("args: " + datagram + ' ' + orig_connector);

	AddrType client_addr = null;
	boolean request_status = false;

	if ("request-status".equals(datagram.getMessage().getMessage())) {
	    client_addr = createAddrType(mkAddrType(datagram.getMessage().getAddendum(), orig_connector));
	    request_status = true;
	} else if ("request-id".equals(datagram.getMessage().getMessage())) {
	    client_addr = createAddrType(mkAddrType(datagram.getMessage().getAddendum(), orig_connector));
	} else if ("request-id-strict".equals(datagram.getMessage().getMessage())) {
	    client_addr = createAddrType(mkAddrType(datagram.getMessage().getAddendum(), orig_connector));
	    client_addr.strict = true;
	} else if ("request-secure-id".equals(datagram.getMessage().getMessage())) {
	    String at = datagram.getMessage().getAddendum();
	    String salt = "";
	    int ix = at.indexOf(' ');
	    if (ix > 0) {
		salt = at.substring(ix).trim();
		at = at.substring(0, ix).trim();
	    }
	    client_addr = createSecureAddrType(mkAddrType(at, orig_connector), salt);
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
	    String myId;
	    if ( Util.empty(message) ) {
		myId = datagram.getSender().getAddrTypeString();
	    } else
		myId = message;
	    if ( AddrType.anonymousAddrType.getAddrTypeString().equals(myId) ) {
	        System.err.println("ClientGhost.registerMsg: Invalid addr " + myId);
	    } else {
		client_addr = createAddrType(mkAddrType(myId, orig_connector));
	    }
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
			client_ghost.strict = client_addr.strict;
			orig_connector.attachClientGhost(client_ghost); // this connector might have another ClGh.othername FATAL?
			boolean again = server.addClientGhost(client_ghost);
			try {
			    client_ghost.sendToClient(new Datagram(serverAddrType,
				    client_addr,
				    new Message("registered",
					    client_addr.getAddrTypeString() + " @" + client_ghost.getAddrTypeGroupSet())));
			} catch (PropagandaException ex) {
			    System.err.println("ClientGhost.registerMsg: Can't send 'registered' (1) " + ex);
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
						">" + orig_connector.name)));
			    } catch (PropagandaException ex) {
				System.err.println("ClientGhost.registerMsg: Can't send 'registered' (2) " + ex);
			    }
			    client_ghost.setConnector(orig_connector);
			    orig_connector.attachClientGhost(client_ghost);
			    status = " hostile-takeover-from";
			    try {
				client_ghost.sendToClient(new Datagram(serverAddrType,
					client_addr,
					new Message("registered",
						client_addr.getAddrTypeString() + status + " @" + client_ghost.getAddrTypeGroupSet() + Constants.CONNECTOR_INDICATOR + current_connector.name)));
			    } catch (PropagandaException ex) {
				System.err.println("ClientGhost.registerMsg: Can't send 'registered' (2) " + ex);
			    }
			    client_ghost.addAddrTypeGroup(client_addr.getAddrTypeGroup());
			}
		    } else if (request_status) {
			try {
			    client_ghost.sendToClient(new Datagram(serverAddrType,
				    client_addr,
				    new Message("already-registered-as",
					    client_addr.getAddrTypeString() + " @" + client_ghost.getAddrTypeGroupSet())));
			} catch (PropagandaException ex) {
			    System.err.println("ClientGhost.registerMsg: Can't send 'registered' (1) " + ex);
			}
		    } else {
			client_ghost.addAddrTypeGroup(client_addr.getAddrTypeGroup());
			try {
			    client_ghost.sendToClient(new Datagram(serverAddrType,
				    client_addr,
				    new Message("registered",
					    client_addr.getAddrTypeString() + " @" + client_ghost.getAddrTypeGroupSet() + status)));
			} catch (PropagandaException ex) {
			    System.err.println("ClientGhost.registerMsg: Can't send 'registered' (2) " + ex);
			}
		    }
		}
	    } else {
		getLogger().finest("OK: already registered myself: " + client_addr);
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
	    int howMany = server.deleteRegisteredClientGhost(client_addr.getId(), client_addr.getAddrTypeGroup(), orig_connector); // remove client ghost if it has no more addr_type_id left
	    if (howMany == 0) {
		current_connector.dettachClientGhost(client_ghost);
	    }
	}
    }
}
