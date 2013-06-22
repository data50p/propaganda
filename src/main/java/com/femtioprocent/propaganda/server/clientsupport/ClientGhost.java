package com.femtioprocent.propaganda.server.clientsupport;

import java.util.HashMap;
import static com.femtioprocent.propaganda.data.AddrType.createAddrType;

import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.femtioprocent.propaganda.connector.PropagandaConnector;
import com.femtioprocent.propaganda.data.AddrType;
import com.femtioprocent.propaganda.data.Datagram;
import com.femtioprocent.propaganda.exception.PropagandaException;
import com.femtioprocent.propaganda.server.PropagandaServer;

public class ClientGhost
{
    private String      name;
    private Set<String> addr_type_id_set;
            PropagandaServer      server;
            PropagandaConnector   connector;
    private HashMap<String, Object> clientData;

    public ClientGhost(String name, String addr_type_id, PropagandaConnector connector)
    {
	this.name = name;
	addr_type_id_set = new TreeSet<String>();
	addAddrTypeId(addr_type_id);
	this.connector = connector;
    }

    public String getName()
    {
	return name;
    }

    public boolean removeAddrTypeId(String id)
    {
	if ( id.equals("*") )
	    addr_type_id_set = new TreeSet<String>();
	else
	    addr_type_id_set.remove(id);

	return addr_type_id_set.size() == 0;
    }

    public void addAddrTypeId(String addr_type_id)
    {
	addr_type_id_set.add(addr_type_id);
    }

    private boolean matchString(String s_match, String s_with)
    {
	int len = s_match.length();

	if ( s_match.endsWith("*") && len > 1 ) {
	    return s_with.toLowerCase().startsWith(s_match.substring(0, len-1).toLowerCase());
	}
	return s_with.equalsIgnoreCase(s_match);
    }

    @SuppressWarnings("unused")
    private boolean matchRegexp(String s_match, String s_with)
    {
	Pattern pat = Pattern.compile(s_match);
	Matcher matcher = pat.matcher(s_with);
	return matcher.matches();
    }

    private boolean matchAddrTypeId(String matching_addr_type_id) // abcde or ab*
    {
	for(String s : addr_type_id_set ) {
	    if ( matchString(matching_addr_type_id, s) )
		return true;
	}
	return false;
    }

    private boolean matchAddrName(String matching_addr_name)
    {
	if ( matchString(matching_addr_name, name) )
	    return true;
	return false;
    }

    public boolean matchAddrType(AddrType addr_type)
    {
	String id = addr_type.getAddrTypeId();
	String name    = addr_type.getName();

	if ( id.equals("") && matchAddrTypeId("$ADMIN") )
	    return true;
	if ( id.equals("*") && matchAddrName(name) )
	    return true;
	if ( id.equals("*") && name.equals("*") )
	    return true;
	if ( name.equals("*") && matchAddrTypeId(id) )
	    return true;
	if ( matchAddrName(name) && matchAddrTypeId(id) )
	    return true;
	return false;
    }

    public AddrType getDefaultAddrType()
    {
        if ( addr_type_id_set.size() > 0 )
            return createAddrType(name, addr_type_id_set.iterator().next());
        return createAddrType("@");
    }

    public void setConnector(PropagandaConnector connector)
    {
	this.connector = connector;
    }

    public PropagandaConnector getConnector()
    {
	return connector;
    }

    public void sendToClient(Datagram datagram) throws PropagandaException
    {
	connector.sendToClient(datagram);
    }

    public Set<String> getAddrTypeIdSet() {
	return addr_type_id_set;
    }

     public HashMap<String, Object> getClientData() {
 	if ( clientData == null )
 	    clientData = new HashMap<String, Object>();

 	return clientData;
     }


    @Override
    public String toString()
    {
	return "ClientGhost{" + name + ' ' + addr_type_id_set + ' ' + connector + ' ' + clientData + "}";
    }
}
