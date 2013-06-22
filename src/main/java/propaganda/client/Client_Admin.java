package propaganda.client;

import fpg.sundry.*;

import java.util.Set;
import propaganda.data.*;
import propaganda.server.*;
import propaganda.connector.*;
import propaganda.server.clientsupport.*;
import propaganda.exception.*;

import static propaganda.data.AddrType.*;
import static propaganda.context.Config.*;

/**
 * The Admin of the server itself. Address '@'
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
				sendMsg(new Datagram(serverAddrType,
					datagram.getSender(),
					new Message("list-id-is",
					"" + server.clientghost_hm.keySet())));
			    } catch (PropagandaException ex) {
				S.pL("ClientGhost.registerMsg: Can't send 'registered' (1) " + ex);
			    }

			} else if ("list".equals(datagram.getMessage().getMessage())) {
			    try {
				StringBuilder sb = new StringBuilder();
				for (String key : server.clientghost_hm.keySet()) {
				    if (sb.length() > 0) {
					sb.append(";");
				    }
				    sb.append(key);
				    final ClientGhost cg = server.clientghost_hm.get(key);
				    final Set<String> addrTypeIdSet = cg.getAddrTypeIdSet();
				    sb.append("@" + addrTypeIdSet.toString().replace(" ", ""));
				}
				sendMsg(new Datagram(serverAddrType,
					datagram.getSender(),
					new Message("list-is",
					"" + sb.toString())));
			    } catch (PropagandaException ex) {
				S.pL("ClientGhost.registerMsg: Can't send 'registered' (1) " + ex);
			    }

			} else if ("version".equals(datagram.getMessage().getMessage())) {
			    try {
				sendMsg(new Datagram(serverAddrType,
					datagram.getSender(),
					new Message("version",
					"" + propaganda.Version.projectVersion + ' ' + propaganda.Version.mavenBuildTimestamp)));
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
	}
	if (datagram.getMessage().getAddendum() == null) {
	    final String message = datagram.getMessage().getMessage();
	    client_addr = createAddrType(mkAddrType(message));
	}

	if (client_addr != null) {
	    getLogger().finest("register: " + client_addr);
	    ClientGhost client_ghost = null;
	    if (!client_addr.getAddrTypeId().equals("$ADMIN")) {
		client_ghost = server.getRegisteredClientGhost(client_addr.getName());
		if (client_ghost == null) {
		    if (request_status) {
			// can't send; no recipient
			return null;
		    } else {
			client_ghost = new ClientGhost(client_addr.getName(), client_addr.getAddrTypeId(), orig_connector);
			orig_connector.attachClientGhost(client_ghost); // this connector might have another ClGh.othername FATAL?
			boolean again = server.addClientGhost(client_ghost);
			try {
			    client_ghost.sendToClient(new Datagram(serverAddrType,
				    client_addr,
				    new Message("registered",
				    client_addr.getAddrTypeString() + " @" + client_ghost.getAddrTypeIdSet())));
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
					client_addr.getAddrTypeString() + " @" + client_ghost.getAddrTypeIdSet() + status)));
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
					client_addr.getAddrTypeString() + " @" + client_ghost.getAddrTypeIdSet())));
			    } catch (PropagandaException ex) {
				S.pL("ClientGhost.registerMsg: Can't send 'registered' (1) " + ex);
			    }
			} else {
			    client_ghost.addAddrTypeId(client_addr.getAddrTypeId());
			    try {
				client_ghost.sendToClient(new Datagram(serverAddrType,
					client_addr,
					new Message("registered",
					client_addr.getAddrTypeString() + " @" + client_ghost.getAddrTypeIdSet() + status)));
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
	ClientGhost client_ghost = server.getRegisteredClientGhost(client_addr.getName());
	if (client_ghost != null) {
	    PropagandaConnector current_connector = client_ghost.getConnector();
	    current_connector.dettachClientGhost(client_ghost);
	    server.deleteRegisteredClientGhost(client_addr.getName(), client_addr.getAddrTypeId(), orig_connector); // remove client ghost if it has no more addr_type_id left
	}
    }
}
