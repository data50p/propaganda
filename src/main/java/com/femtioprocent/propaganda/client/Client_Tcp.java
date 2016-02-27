package com.femtioprocent.propaganda.client;

import java.util.ArrayList;

import com.femtioprocent.propaganda.connector.Connector_Tcp;

public class Client_Tcp extends PropagandaClient {

    Connector_Tcp conn;

    Client_Tcp(String name) {
	super(name);
	this.name = name;
	addrtypeGroup_list = new ArrayList<String>();
	conn = new Connector_Tcp("Conn-" + name, this);
	setConnectorAndAttach(conn);
    }

    public static boolean sendMsg(String from, String addr_to, String msg) {
	PropagandaClient client = new Client_Tcp(from);
	return client.sendMsg(addr_to, msg);
    }
}
