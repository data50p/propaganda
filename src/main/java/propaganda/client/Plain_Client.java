package propaganda.client;

import java.util.ArrayList;

import propaganda.connector.Connector_Plain;

public class Plain_Client extends PropagandaClient
{
    Connector_Plain conn;

    Plain_Client(String name)
    {
	super(name);
	this.name = name;
	addrtypeid_list = new ArrayList<String>();
	conn = new Connector_Plain("Conn-" + name, this);
	setConnectorAndAttach(conn);
    }

    public static boolean sendMsg(String from, String addr_to, String msg) 
    {
	PropagandaClient client = new Plain_Client(from);
	return client.sendMsg(addr_to, msg);
    }
}
