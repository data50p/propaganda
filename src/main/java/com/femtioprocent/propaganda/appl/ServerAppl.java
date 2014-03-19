package com.femtioprocent.propaganda.appl;

import com.femtioprocent.fpd.sundry.Appl;
import com.femtioprocent.fpd.sundry.S;
import com.femtioprocent.propaganda.server.PropagandaServer;
import static com.femtioprocent.fpd.sundry.Appl.flags;

//import propaganda.util.*;
public class ServerAppl extends Appl {

    public PropagandaServer server;

    @Override
    public void main() {
        if ((flags.get("?")) != null || (flags.get("h")) != null) {
            S.pL("-MB              start Moquette Broker (MQTT)");
            S.pL("-port=<port>     set the port of propaganda server (=8899)");
            S.pL("-http=<port>     set the port of propaganda http server (=8888)");
            S.pL("-ws=<port>       set the port of propaganda WS server (=8877)");
            S.pL("-discover=<port> set the port of propaganda discover server (=8833)");
            return;
        }

        String port_s = "8899";
        String fl;

        if ((fl = flags.get("port")) != null) {
            port_s = fl;
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

        server = PropagandaServer.getDefaultServer("CCF-PropagandaServer", Integer.parseInt(port_s));

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
