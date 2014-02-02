package com.femtioprocent.propaganda.appl;

import com.femtioprocent.fpd.sundry.Appl;
import com.femtioprocent.fpd.sundry.S;
import com.femtioprocent.propaganda.server.PropagandaServer;
import static com.femtioprocent.fpd.sundry.Appl.flags;

//import propaganda.util.*;

public class ServerAppl extends Appl
{
    public PropagandaServer server;

    @Override
    public void main() {
	if ( (flags.get("?")) != null) {
            S.pL("-MB                      start Moquette Broker (MQTT)");
            S.pL("-propaganda.port=<port>  set the port of propaganda server");
            return;
        }
        
        String port_s = "8899";
        String fl_port_s;
        
	if ((fl_port_s = flags.get("propaganda.port")) != null)
             port_s = fl_port_s;

        server = PropagandaServer.getDefaultServer("CCF-PropagandaServer", Integer.parseInt(port_s));

	String s = "ALL";
	if ((s = flags.get("log")) != null)
             com.femtioprocent.propaganda.context.Config.setLogLevel(s.length() == 0 ? "ALL"
                                                           : s);

	//server.invoke();
    }

    public static void main(String[] args) {
	decodeArgs(args);
	main(new ServerAppl());
    }
}
