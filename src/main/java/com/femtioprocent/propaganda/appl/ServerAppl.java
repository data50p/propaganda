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
            S.pL("-MB     start Moquette Broker (MQTT)");
            return;
        }
        
	server = PropagandaServer.getDefaultServer();

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
