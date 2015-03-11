package com.femtioprocent.propaganda.appl;

import com.femtioprocent.fpd.appl.Appl;
import com.femtioprocent.fpd.sundry.S;
import com.femtioprocent.propaganda.server.PropagandaServer;
import static com.femtioprocent.fpd.appl.Appl.flags;

//import propaganda.util.*;
public class ServerAppl extends Appl {

    public PropagandaServer server;

    @Override
    public void main() {
        if ((flags.get("?")) != null || (flags.get("h")) != null) {
            S.pL("-name                 propaganda server name");
            S.pL("-MB                   start Moquette Broker (MQTT)");
            S.pL("-port=<port>          set the port of propaganda server (=8899)");
            S.pL("-http=<port>          set the port of propaganda http server (=8888)");
            S.pL("-ws=<port>            set the port of propaganda WS server (=8877)");
            S.pL("-discover=<port>      set the port of propaganda discover server (=8833)");
            S.pL("-fed.port=<port>      set the port of propaganda federation listener (=0) - EXPERIMENTAL");
            S.pL("-fed.join=<host:port> set the host:port for joining federation (='') - EXPERIMENTAL");
            return;
        }

        String port_s = "8899";
        String fl;

        if ((fl = flags.get("port")) != null) {
            port_s = fl;
        }

        if ((fl = flags.get("name")) != null) {
            PropagandaServer.DEFAULT_NAME = fl;
        }

        if ((fl = flags.get("http")) != null) {
            PropagandaServer.DEFAULT_HTTP_PORT = Integer.parseInt(fl);
        }

        if ((fl = flags.get("ws")) != null) {
            PropagandaServer.DEFAULT_WS_PORT = Integer.parseInt(fl);
        }

        if ((fl = flags.get("discover")) != null) {
            PropagandaServer.DEFAULT_DISCOVER_PORT = Integer.parseInt(fl);
        }

        if ((fl = flags.get("fed.port")) != null) {
            PropagandaServer.DEFAULT_FEDERATION_PORT = Integer.parseInt(fl);
        }

        if ((fl = flags.get("fed.join")) != null) {
            PropagandaServer.DEFAULT_FEDERATION_JOINHOST = fl;
        }

        server = PropagandaServer.getDefaultServer("", Integer.parseInt(port_s));

        String s = "ALL";
        if ((s = flags.get("log")) != null) {
            com.femtioprocent.propaganda.context.Config.setLogLevel(s.length() == 0 ? "ALL"
                    : s);
        }

        //server.invoke();
    }

    public static void main(String[] args) {
        decodeArgs(args);
        main(new ServerAppl());
    }
}
