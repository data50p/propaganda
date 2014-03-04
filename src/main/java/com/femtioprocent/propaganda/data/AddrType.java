package com.femtioprocent.propaganda.data;

import com.femtioprocent.fpd.sundry.Base64;
import com.femtioprocent.propaganda.util.SecureUtil;
import com.femtioprocent.propaganda.util.Util;


/**
 * id@group
 * 
 * @author lars
 */
public class AddrType
{
    private String id;
    String unsecureId;
    String group;
    String addr_type = "?";
    boolean regex = false;
    boolean secure = false;

    public static AddrType serverAddrType = new AddrType("@");
    public static AddrType anonymousAddrType = new AddrType(".");
    @Deprecated
    public static AddrType anyAddrType = new AddrType("*"); 
    public static AddrType allAddrType = new AddrType("*");
    public static AddrType allOtherAddrType = new AddrType("*!"); // all other but myself
    public static AddrType defaultAddrType = new AddrType("_"); // changed to first registred

    private AddrType(String s)
    {
	int ix = s.indexOf('@');
	if ( ix == -1 ) {
	    this.id = s;
	    this.group   = "";
	    this.addr_type = s;
	}
	else {
	    this.id = s.substring(0, ix);
	    this.group   = s.substring(ix+1);
	    this.addr_type = s;
	}
    }

    private AddrType(String id, String group)
    {
	this.id = id;
	this.group = group;
	this.addr_type = id + '@' + group;
    }

    public static AddrType createAddrType(String id, String group)
    {
	AddrType addr_type = new AddrType(id, group);
	return addr_type;
    }

    public static AddrType createSecureAddrType(String addr_type, String seasalt)
    {
	AddrType at = new AddrType(addr_type);
        at.unsecureId = at.id;
        at.id = SecureUtil.getSecureId(at.id, seasalt);
        at.addr_type = at.id + '@' + at.group;
        at.secure = true;
	return at;
    }

    public static AddrType createAddrType(String s)
    {
	if ( s == null )
	    return defaultAddrType;

	if ( s.equals("_") )
	    return defaultAddrType;

	if ( s.equals(".") )
	    return anonymousAddrType;

	if ( s.equals("*") || s.equals("*@*") )
	    return allAddrType;

	if ( s.equals("*!") )
	    return allOtherAddrType;

	if ( s.equals("@") )
	    return serverAddrType;

	return new AddrType(s);
    }

    public String getAddrTypeString()
    {
        if ( addr_type == null )
            addr_type = id + '@' + group;
	return addr_type;
    }

    public String getUnsecureAddrTypeString()
    {
        if ( secure )
            return getUnsecureId() + "@" + getAddrTypeGroup();
        return getId() + "@" + getAddrTypeGroup();
    }

    public String getId()
    {
	return this.id;
    }

    public String getUnsecureId()
    {
	return secure ? "(" + this.unsecureId + ")" : this.id;
    }

    public String getUnsecureIdALt()
    {
	return secure ? this.unsecureId : this.id;
    }

    public String getAddrTypeGroup()
    {
	return this.group;
    }

    @Override
    public String toString()
    {
        if ( secure )
            return "AddrType{" + getUnsecureAddrTypeString() + "}";
        else
            return "AddrType{" + getAddrTypeString() + "}";
    }

    @Override
    public boolean equals(Object at)
    {
	return at instanceof AddrType ? getAddrTypeString().equals(((AddrType)at).getAddrTypeString()) : false;
    }
}
