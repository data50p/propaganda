package propaganda.data;


public class AddrType
{
    String name;
    String id;
    String addr_type = "?";
    boolean regex = false;

    public static AddrType serverAddrType = new AddrType("@");
    public static AddrType anonymousAddrType = new AddrType(".");
    public static AddrType anyAddrType = new AddrType("*");
    public static AddrType unknownAddrType = new AddrType("_"); // changed to first registred

    private AddrType(String s)
    {
	int ix = s.indexOf('@');
	if ( ix == -1 ) {
	    this.name = s;
	    this.id   = "";
	    this.addr_type = s;
	}
	else {
	    this.name = s.substring(0, ix);
	    this.id   = s.substring(ix+1);
	    this.addr_type = s;
	}
    }

    private AddrType(String name, String id)
    {
	this.name = name;
	this.id = id;
	this.addr_type = name + '@' + id;
    }

    public static AddrType createAddrType(String name, String id)
    {
	AddrType addr_type = new AddrType(name, id);
	return addr_type;
    }

    public static AddrType createAddrType(String s)
    {
	if ( s == null )
	    return unknownAddrType;

	if ( s.equals("_") )
	    return unknownAddrType;

	if ( s.equals(".") )
	    return anonymousAddrType;

	if ( s.equals("*") || s.equals("*@*") )
	    return anyAddrType;

	if ( s.equals("@") )
	    return serverAddrType;

	return new AddrType(s);
    }

    public String getAddrTypeString()
    {
	return addr_type;
    }

    public String getName()
    {
	return this.name;
    }

    public String getAddrTypeId()
    {
	return this.id;
    }

    @Override
    public String toString()
    {
	return "AddrType{" + getName() + ',' + getAddrTypeId() + ',' + getAddrTypeString() + "}";
    }

    @Override
    public boolean equals(Object at)
    {
	return at instanceof AddrType ? getAddrTypeString().equals(((AddrType)at).getAddrTypeString()) : false;
    }
}