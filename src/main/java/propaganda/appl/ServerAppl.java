package propaganda.appl;

import fpg.sundry.*;
import static fpg.sundry.Appl.flags;

//import propaganda.util.*;
import propaganda.server.*;

public class ServerAppl extends Appl
{
    public PropagandaServer server;

    @Override
    public void main() {
	if ( (flags.get("?")) != null) {
            S.pL("-MB     start Moquette Broker (MQTT)");
            return;
        }
        
	server = new PropagandaServer();

	String s = "ALL";
	if ((s = flags.get("log")) != null)
             propaganda.context.Config.setLogLevel(s.length() == 0 ? "ALL"
                                                           : s);

	//server.invoke();
    }

    public static void main(String[] args) {
	decodeArgs(args);
	main(new ServerAppl());
    }
}
