package com.femtioprocent.propaganda.appl;

import com.femtioprocent.fpd.appl.Appl;
import com.femtioprocent.fpd.sundry.S;
import com.femtioprocent.propaganda.server.PropagandaServer;
import static com.femtioprocent.fpd.appl.Appl.flags;
import com.femtioprocent.propaganda.util.Util;

//import propaganda.util.*;
public class ServerAppl extends Appl {

    public PropagandaServer server;

    @Override
    public void main() {
	if ((flags.get("?")) != null || (flags.get("h")) != null) {
	    System.err.println("-name                 propaganda server name");
	    System.err.println("-MB                   start Moquette Broker (MQTT)");
	    System.err.println("-port=<port>          set the port of propaganda server (=8899)");
	    System.err.println("-http=<port>          set the port of propaganda http server (=8889)");
	    System.err.println("-ws=<port>            set the port of propaganda WS server (=8879)");
	    System.err.println("-discover=<port>      set the port of propaganda discover server (=8839)");
	    System.err.println("-fed.port=<port>      set the port of propaganda federation listener (=8859) - EXPERIMENTAL");
	    System.err.println("-fed.join=<host:port> set the host:port for joining federation (='') - EXPERIMENTAL");
	    System.err.println("-portprefix=<portprefix> set the port prefix (='') - EXPERIMENTAL");
	    return;
	}

	String fl;

	if ((fl = flags.get("port")) != null) {
	    PropagandaServer.DEFAULT_SERVER_PORT = Integer.parseInt(fl);
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

	if ((fl = flags.get("portprefix")) != null) {
	    PropagandaServer.PORT_PREFIX = Integer.parseInt(fl);
	    PropagandaServer.DEFAULT_SERVER_PORT = PropagandaServer.PORT_PREFIX * 100 + (PropagandaServer.DEFAULT_SERVER_PORT % 100);
	    PropagandaServer.DEFAULT_HTTP_PORT = PropagandaServer.PORT_PREFIX * 100 + (PropagandaServer.DEFAULT_HTTP_PORT % 100);
	    PropagandaServer.DEFAULT_WS_PORT = PropagandaServer.PORT_PREFIX * 100 + (PropagandaServer.DEFAULT_WS_PORT % 100);
	    PropagandaServer.DEFAULT_DISCOVER_PORT = PropagandaServer.PORT_PREFIX * 100 + (PropagandaServer.DEFAULT_DISCOVER_PORT % 100);
	    PropagandaServer.DEFAULT_FEDERATION_PORT = PropagandaServer.PORT_PREFIX * 100 + (PropagandaServer.DEFAULT_FEDERATION_PORT % 100);

	}

	server = PropagandaServer.getDefaultServer("name", PropagandaServer.DEFAULT_SERVER_PORT, PropagandaServer.DEFAULT_FEDERATION_JOINHOST, PropagandaServer.DEFAULT_FEDERATION_PORT);

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
