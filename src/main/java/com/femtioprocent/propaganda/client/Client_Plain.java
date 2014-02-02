package com.femtioprocent.propaganda.client;

import java.util.ArrayList;

import com.femtioprocent.propaganda.connector.Connector_Plain;

public class Client_Plain extends PropagandaClient
{
    Connector_Plain conn;

    Client_Plain(String name)
    {
	super(name);
	this.name = name;
	addrtypeGroup_list = new ArrayList<String>();
	conn = new Connector_Plain("Conn-" + name, this);
	setConnectorAndAttach(conn);
    }

    public static boolean sendMsg(String from, String addr_to, String msg) 
    {
	PropagandaClient client = new Client_Plain(from);
	return client.sendMsg(addr_to, msg);
    }
}
