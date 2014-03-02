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
    private String      id;
    public String       unsecure_id;
    private Set<String> addr_type_group_set;
            PropagandaServer      server;
            PropagandaConnector   connector;
    private HashMap<String, Object> clientData;

    public ClientGhost(String id, String unsecure_id, String addr_type_group, PropagandaConnector connector)
    {
	this.id = id;
        this.unsecure_id = unsecure_id;
	addr_type_group_set = new TreeSet<String>();
	addAddrTypeGroup(addr_type_group);
	this.connector = connector;
    }

    public String getId()
    {
	return id;
    }

    public boolean removeAddrTypeId(String id)
    {
	if ( id.equals("*") )
	    addr_type_group_set = new TreeSet<String>();
	else
	    addr_type_group_set.remove(id);

	return addr_type_group_set.size() == 0;
    }

    public void addAddrTypeGroup(String addr_type_group)
    {
	addr_type_group_set.add(addr_type_group);
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
	for(String s : addr_type_group_set ) {
	    if ( matchString(matching_addr_type_id, s) )
		return true;
	}
	return false;
    }

    private boolean matchAddrId(String matching_addr_id)
    {
	if ( matchString(matching_addr_id, id) )
	    return true;
	return false;
    }

    public boolean matchAddrType(AddrType addr_type)
    {
	String g = addr_type.getAddrTypeGroup();
	String at_id    = addr_type.getId();

	if ( g.equals("") && matchAddrTypeId("$ADMIN") )
	    return true;
	if ( (g.equals("*")||g.equals("*!")) && matchAddrId(at_id) )
	    return true;
	if ( (g.equals("*")||g.equals("*!")) && at_id.equals("*") )
	    return true;
	if ( (at_id.equals("*")||at_id.equals("*!")) && matchAddrTypeId(g) )
	    return true;
	if ( matchAddrId(at_id) && matchAddrTypeId(g) )
	    return true;
	return false;
    }

    public AddrType getDefaultAddrType()
    {
        if ( addr_type_group_set.size() > 0 )
            return createAddrType(id, addr_type_group_set.iterator().next());
        return createAddrType("@");
    }

    public AddrType getDefaultSecureAddrType()
    {
        if ( id.equalsIgnoreCase(unsecure_id) ) {
            return getDefaultAddrType();
        }
        if ( addr_type_group_set.size() > 0 )
            return AddrType.createSecureAddrType(unsecure_id + "@" + addr_type_group_set.iterator().next(), "");
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

    public Set<String> getAddrTypeGroupSet() {
	return addr_type_group_set;
    }

     public HashMap<String, Object> getClientData() {
 	if ( clientData == null )
 	    clientData = new HashMap<String, Object>();

 	return clientData;
     }


    @Override
    public String toString()
    {
	return "ClientGhost{" + id + ' ' + addr_type_group_set + ' ' + connector + ' ' + clientData + "}";
    }
}
