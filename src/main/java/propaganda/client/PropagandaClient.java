package propaganda.client;

import static propaganda.context.Config.getLogger;
import static propaganda.data.AddrType.anonymousAddrType;
import static propaganda.data.AddrType.createAddrType;
import static propaganda.data.AddrType.serverAddrType;
import static propaganda.data.MessageType.plain;

import java.util.ArrayList;

import propaganda.connector.PropagandaConnector;
import propaganda.connector.PropagandaConnectorFactory;
import propaganda.data.AddrType;
import propaganda.data.Datagram;
import propaganda.data.Message;
import propaganda.data.MessageType;
import propaganda.exception.PropagandaException;
import propaganda.server.PropagandaServer;

abstract public class PropagandaClient
{
    PropagandaConnector connector;
    public String name;
    ArrayList<String> addrtypeid_list;

    public PropagandaClient()
    {
	this.name = "anonymous-" + hashCode();
	addrtypeid_list = new ArrayList<String>();
    }

    public PropagandaClient(String name)
    {
	this.name = name;
	addrtypeid_list = new ArrayList<String>();
    }

    public PropagandaClient(String name, PropagandaConnector conn)
    {
	this.name = name;
	addrtypeid_list = new ArrayList<String>();
	setConnectorAndAttach(conn);
    }

    protected void init()
    {
    }

    public void register(String addr_type_id) throws PropagandaException
    {
        int ix = addr_type_id.indexOf('@');
        if ( ix != -1 ) {
            String id = addr_type_id.substring(0, ix);
            String at = addr_type_id.substring(ix + 1);
            register(id, at);
            return;
        }
        
	if ( addrtypeid_list.contains(addr_type_id) )
	    throw new PropagandaException("Already used: " + addr_type_id);

	Datagram register_datagram = new Datagram(anonymousAddrType,
						  serverAddrType,
						  MessageType.register,
						  new Message("request-id",
							      createAddrType(name, addr_type_id).getAddrTypeString()));

	getLogger().finest("datagram: " + register_datagram);

	sendMsg(register_datagram);

	addrtypeid_list.add(addr_type_id);
	// wait for confirmation?
    }

    public void register(String id, String addr_type_id) throws PropagandaException
    {
        String id_at = id + '@' + addr_type_id;
	if ( addrtypeid_list.contains(id_at) )
	    throw new PropagandaException("Already used: " + id_at);

	Datagram register_datagram = new Datagram(anonymousAddrType,
						  serverAddrType,
						  MessageType.register,
						  new Message("request-id",
							      createAddrType(id, addr_type_id).getAddrTypeString()));

	getLogger().finest("datagram: " + register_datagram);

	sendMsg(register_datagram);

	addrtypeid_list.add(id_at);
	// wait for confirmation?
    }

    public String getName()
    {
	return name;
    }

    public AddrType getDefaultAddrType(String addr_type_id)
    {
	return createAddrType(name, addr_type_id);
    }

    public AddrType getDefaultAddrType()
    {
	if ( addrtypeid_list.size() > 0 )
	    return getDefaultAddrType(addrtypeid_list.get(0));
	return AddrType.unknownAddrType;
    }


    public void setConnector(PropagandaConnector con)
    {
	this.connector = con;
    }

    public void setConnectorAndAttach(PropagandaConnector con)
    {
	this.connector = con;
	con.attachClient(this);
    }

    public PropagandaConnector getConnector()
    {
	return connector;
    }

    public PropagandaConnector createConnector(String class_id, String name) {
        return createConnector(class_id, name, PropagandaServer.getDefaultServer());
    }

    public PropagandaConnector createConnector(String class_id, String name, PropagandaServer server) {
        return PropagandaConnectorFactory.create(class_id, name, server, this);
    }
    /**
       Send a message to receiver via connector
     */
    public void sendMsg(Datagram datagram) throws PropagandaException
    {
	getLogger().finest("sending >>>>> " + connector + ' ' + datagram);
	connector.sendMsg(datagram);
    }

    /**
       Send a message to receiver via connector
     */
    public boolean sendMsg(String to_addr, String msg)
    {
	try {
	    Datagram datagram = new Datagram(AddrType.unknownAddrType,
					     createAddrType(to_addr),
					     new Message(msg));
	    getLogger().finest("sending >>>>> " + connector + ' ' + datagram);
	    connector.sendMsg(datagram);
	    return true;
	}
	catch (PropagandaException ex) {
	    return false;
	}
    }

    /**
       Send a message to receiver via connector
       Use datagram sender as reseiver address.
     */
    public void replyMsg(Datagram datagram, Message message) throws PropagandaException
    {
	Datagram sdatagram = new Datagram(getDefaultAddrType(),
					  datagram.getSender(),
					  plain,
					  message);
	sendMsg(sdatagram);
    }

    /**
       Send a message to receiver via connector
       Use datagram sender as reseiver address.
     */
    public void replyMsg(Datagram datagram, String text) throws PropagandaException
    {
	Datagram sdatagram = new Datagram(getDefaultAddrType(),
					  datagram.getSender(),
					  plain,
					  new Message(text));
	sendMsg(sdatagram);
    }

    /**
       Receive a message via connector
     */
    public Datagram recvMsg()
    {
	Datagram datagram = connector.recvMsg();
	getLogger().finest("receiving <<<<< " + connector + ' ' + datagram);
	return datagram;
    }

    @Override
    public String toString()
    {
	return name + '@' + addrtypeid_list + ' ' + connector;
    }
}
