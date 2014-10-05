package com.femtioprocent.propaganda.dispatcher;

import com.femtioprocent.fpd.sundry.S;
import com.femtioprocent.propaganda.connector.PropagandaConnector;
import static com.femtioprocent.propaganda.context.Config.*;
import com.femtioprocent.propaganda.data.AddrType;
import static com.femtioprocent.propaganda.data.AddrType.*;
import com.femtioprocent.propaganda.data.Datagram;
import com.femtioprocent.propaganda.data.Message;
import com.femtioprocent.propaganda.data.MessageType;
import com.femtioprocent.propaganda.exception.PropagandaException;
import com.femtioprocent.propaganda.server.PropagandaServer;
import com.femtioprocent.propaganda.server.clientsupport.ClientGhost;
import com.femtioprocent.propaganda.server.clientsupport.FederationServer;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.regex.*;

public class Dispatcher {

    private PropagandaServer server;
    private HashMap<String, ClientGhost> clientghost_hm;
    public HashMap<String, FederationServer> federatedghost_hm = new HashMap<String, FederationServer>();
    private int cnt_register, cnt_unregister, cnt_ping, cnt_pong, cnt_plain, cnt_RM, cnt_sendClient;

    /**
     * Validate the sender (it must belong to the connector)
     *
     * @param datagram
     * @param orig_connector
     * @return
     */
    private boolean datagramInvalid(Datagram datagram, PropagandaConnector orig_connector) {
        if (datagram.getMessageType() == MessageType.monitor) {
            return false;
        }
        return !orig_connector.validateDatagram(datagram);
    }

    private void sendToMonitor(PropagandaConnector orig_connector, Datagram datagram, String what) {
        try {
            AddrType sender = null;
            if (orig_connector != null && orig_connector.getDefaultClientGhost() != null) {
                sender = orig_connector.getDefaultClientGhost().getDefaultSecureAddrType();
            }
            if (sender == null) {
                sender = AddrType.serverAddrType;
            }

            server.client_monitor.sendMsg(new Datagram(sender,
                    createAddrType("*", "MONITOR"),
                    MessageType.monitor,
                    new Message(what, datagram.getDatagramString())));
        } catch (PropagandaException ex) {
            S.pL("Can't send '" + what + "' to MONITOR: " + datagram.getDatagramString() + ' ' + ex);
        }
    }

    class RememberEntry {

        long when;
        Datagram datagram;

        RememberEntry(long when, Datagram datagram) {
            this.when = when;
            this.datagram = datagram;
        }

        @Override
        public String toString() {
            return "" + new Date(when) + ':' + datagram;

        }
    }
    /**
     * Remember things to be resent (type RM).
     */
    @SuppressWarnings("unchecked")
    private Map<String, RememberEntry> resend_messages_map = (Map<String, RememberEntry>) Collections.synchronizedMap(new HashMap());

    /**
     * Dispatch messages to all registred receiver.
     */
    public Dispatcher(PropagandaServer server, HashMap<String, ClientGhost> clientghost_hm) {
        this.server = server;
        this.clientghost_hm = clientghost_hm;
        getLogger().finest("created: " + this);
    }

    /**
     * Can this datagram be sent to the client ghost?
     */
    private boolean matching(ClientGhost client_ghost, Datagram datagram) {
        AddrType receiver = datagram.getReceiver();
        if (receiver == null) {
            return false;
        }

        if (receiver.getAddrTypeString().equals("@") && datagram.getSender().getAddrTypeString().equals("@")) {
            getLogger().warning("sending from @ to @, ignored: " + this);
            return false;
        }

        if (receiver.getId().equals("*!")) {
            if (datagram.getSender().getId().equals(client_ghost.getDefaultAddrType().getId())) {
                return false;
            }
        }
        return receiver.equals(allAddrType) || client_ghost.matchAddrType(receiver);
    }

    private long calculateValidUpTo(String duration) {
        long now = S.ct();

        try {
            if (duration.equals("-1")) {
                return 0;
            }
            if (duration.equals("0")) {
                return now;
            }

            Pattern pat = Pattern.compile("(([0-9]*)h)?(([0-9]*)m)?(([0-9]*)s)?");

            Matcher matcher = pat.matcher(duration);
            if (matcher.matches()) {
                int h = matcher.group(2) == null ? 0 : Integer.parseInt(matcher.group(2));
                int m = matcher.group(4) == null ? 0 : Integer.parseInt(matcher.group(4));
                @SuppressWarnings("unused")
                int s = matcher.group(6) == null ? 0 : Integer.parseInt(matcher.group(6));

                return now + ((h * 24 + m) * 60 + s) * 1000;
            }
        } catch (Exception ex) {
            getLogger().severe("can_t-calculate-timout: " + duration);
        }
        return now;
    }

    public synchronized int dispatchMsg(PropagandaConnector orig_connector, Datagram datagram) {
        
        if ( orig_connector == null ) { // FederationServer
            return dispatchMsg1(datagram);
        }
        
        if (datagram.getStatus() == Datagram.Status.IGNORE) {
            return 0;
        }
        if (datagram.getStatus() == Datagram.Status.BAD) {
            if (com.femtioprocent.fpd.appl.Appl.flags.get("nomonitor") == null) {
                sendToMonitor(orig_connector, datagram, "datagram-bad");
            }
            return 0;
        }

        if (datagram.getMessageType() == MessageType.single)
            ;

        if (datagram.getMessageType() == MessageType.fedjoin) {

            getLogger().finer("fedjoin: " + datagram + ' ' + orig_connector);
            if (datagram.getReceiver() != serverAddrType) {
                getLogger().severe("fedjoin-bad: " + datagram + ' ' + orig_connector);
                return 0;
            }
            String pfId = datagram.getMessage().getMessage();
            federatedghost_hm.put(pfId, new FederationServer(pfId));
            Datagram sdatagram = new Datagram(serverAddrType,
                    datagram.getSender(),
                    MessageType.plain,
                    new Message("joined fed " + pfId + ' ' + federatedghost_hm.size()));
            try {
                orig_connector.sendMsg(sdatagram);
            } catch (PropagandaException ex) {
                Logger.getLogger(Dispatcher.class.getName()).log(Level.SEVERE, null, ex);
            }
            //int dmCnt = dispatchMsg(orig_connector, sdatagram);
            return 0;
        }

        if (datagram.getMessageType() == MessageType.register) {                      // register
            getLogger().finer("register: " + datagram + ' ' + orig_connector);
            if (datagram.getReceiver() != serverAddrType) {
                getLogger().severe("register-bad: " + datagram + ' ' + orig_connector);
                return 0;
            }

            ClientGhost client_ghost = server.client_admin.registerMsg(datagram, orig_connector);
            if (client_ghost == null) {
                return 0;
            }
            cnt_register++;

            long now = S.ct();
            ArrayList<String> removelist = new ArrayList<String>();
            synchronized (resend_messages_map) {
                for (String id : resend_messages_map.keySet()) {
                    RememberEntry rent = resend_messages_map.get(id);
                    if (rent.when < now) {// this is an old entry, remove it
                        removelist.add(id);
                    } else {
                        try {
                            Datagram rent_datagram = new Datagram(rent.datagram);
                            if (client_ghost.matchAddrType(rent_datagram.getReceiver())) {
                                rent_datagram.setReceiver(client_ghost.getDefaultAddrType());
                                client_ghost.sendToClient(rent_datagram);
                                //			    dispatchMsg1(rent_datagram);
                            }
                        } catch (PropagandaException ex) {
                            S.pL("Can't resend message " + rent + ' ' + ex);
                        }
                    }
                }
                for (String id : removelist) {
                    resend_messages_map.remove(id);
                }
            }

            if (com.femtioprocent.fpd.appl.Appl.flags.get("nomonitor") == null) {
                sendToMonitor(null, datagram, "special");
            }
            return 0;
        }
        if (datagram.getMessageType() == MessageType.unregister) {                      // unregister
            getLogger().finer("unregister: " + datagram + ' ' + orig_connector);
            if (datagramInvalid(datagram, orig_connector)) {
                sendToMonitor(orig_connector, datagram, "invalid-sender-address");
                return 0;
            }
            if (datagram.getReceiver() != serverAddrType) {
                getLogger().severe("unregister-bad: " + datagram + ' ' + orig_connector);
                return 0;
            }

            server.client_admin.unregisterMsg(datagram, orig_connector);
            cnt_unregister++;

            if (com.femtioprocent.fpd.appl.Appl.flags.get("nomonitor") == null) {
                sendToMonitor(null, datagram, "special");
            }
            return 0;
        }

        if (datagram.getMessageType() == MessageType.ping) {                          // ping
            if (datagramInvalid(datagram, orig_connector)) {
                sendToMonitor(orig_connector, datagram, "invalid-sender-address");
                return 0;
            }
            getLogger().finer("ping: " + datagram + ' ' + orig_connector);
            cnt_ping++;
            if (datagram.getReceiver() == serverAddrType) {
                int dmCnt = dispatchMsg(orig_connector,
                        new Datagram(serverAddrType,
                                datagram.getSender(),
                                MessageType.pong,
                                datagram.getMessage()));
                return dmCnt;
            }
        } else if (datagram.getMessageType() == MessageType.pong) {                     // pong
            getLogger().finer("pong: " + datagram + ' ' + orig_connector);
            cnt_pong++;
            if (datagram.getReceiver() == serverAddrType) {
                return 1;
            }
        } else if (datagram.getMessageType() == MessageType.RM) {                       // RM
            if (datagramInvalid(datagram, orig_connector)) {
                sendToMonitor(orig_connector, datagram, "invalid-sender-address");
                return 0;
            }
            String[] message_type_arg_Arr = datagram.getMessageTypeArg().split(":");    // group:duration
            String key_id = datagram.getSender().getId() + ':' + message_type_arg_Arr[0];
            String duration_s = message_type_arg_Arr[1];

            long valid_upto = calculateValidUpTo(duration_s);
            RememberEntry rent = new RememberEntry(valid_upto, datagram);
            resend_messages_map.put(key_id, rent);

            getLogger().finer("resend_HM (" + valid_upto + "): " + key_id + ' ' + rent + ' ' + resend_messages_map);
            cnt_RM++;

            // dont send if timeout == -1
            if (com.femtioprocent.fpd.appl.Appl.flags.get("nomonitor") == null) {
                if (valid_upto == 0) {
                    sendToMonitor(null, datagram, "message");
                    return 0;
                }
            }
        }
        if (datagramInvalid(datagram, orig_connector)) {
            sendToMonitor(orig_connector, datagram, "invalid-sender-address");
            return 0;
        }
        return dispatchMsg1(datagram);
    }

    /**
     * Send message to clients, (assume it is plain or RM (not special))
     */
    private synchronized int dispatchMsg1(Datagram datagram) {
        Set<ClientGhost> copy = new HashSet<ClientGhost>();
        copy.addAll(clientghost_hm.values());

        int dmCnt = 0;

        SEND:
        for (ClientGhost client_ghost : copy) {

            if (matching(client_ghost, datagram)) {
                getLogger().fine("msg: ->  " + client_ghost + "  <- " + datagram);

                // reset _ to client addr???
                try {
                    client_ghost.sendToClient(datagram);
                    dmCnt++;
                    cnt_sendClient++;
                    if (datagram.getMessageType() == MessageType.single) {
                        break SEND;
                    }
                } catch (PropagandaException ex) {
                    S.pL("" + ex);
                    clientghost_hm.remove(client_ghost.getId());
                }
            } else {
                getLogger().fine("msg: ->| " + client_ghost + " |<- " + datagram);
            }
        }
        if (com.femtioprocent.fpd.appl.Appl.flags.get("nomonitor") == null) {
            if (datagram.getMessageType() != MessageType.monitor) {
                sendToMonitor(null, datagram, "message");
            }
        }

        for (FederationServer fg : federatedghost_hm.values()) {
            try {
                fg.sendToFederatedPropaganda(datagram);
            } catch (PropagandaException ex) {
                Logger.getLogger(Dispatcher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        cnt_plain++;
        return dmCnt;
    }

    public String getStatus(int level) {
        return "cnt_plain=" + cnt_plain
                + " cnt_RM=" + cnt_RM
                + " cnt_ping=" + cnt_ping
                + " cnt_pong=" + cnt_pong
                + " cnt_register=" + cnt_register
                + " cnt_unregister=" + cnt_unregister
                + " cnt_sendClient=" + cnt_sendClient;
    }

    static public void main(String[] args) {
        Dispatcher d = new Dispatcher(null, null);
        S.pL("" + d.calculateValidUpTo(args[0]));
    }
}
