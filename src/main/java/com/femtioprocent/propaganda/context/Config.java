package com.femtioprocent.propaganda.context;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Config
{
    static HashMap<String, Log> logs = new HashMap<String, Log>();

    static Log log = new Log("default");

    public static void setLogLevel(String level)
    {
	level = "ALL";
	log.getLogger().setLevel(Level.parse(level));
    }

    public static Logger getLogger()
    {
	return log.getLogger();
    }

    public static Logger getLogger(String name)
    {
 	Log log = logs.get(name);
 	if ( log == null ) {
 	    log = new Log(name);
 	    logs.put(name, log);
 	}
	return log.getLogger();
    }
}
