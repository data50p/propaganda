package com.femtioprocent.propaganda.client;

import static com.femtioprocent.propaganda.context.Config.getLogger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

public class PropagandaClientFactory {

    @SuppressWarnings("unchecked")
    public static PropagandaClient create(String class_name, String name) {
	try {
	    Class cls = Class.forName("com.femtioprocent.propaganda.client.Client_" + class_name);
	    getLogger().finest("class: " + cls);
	    Constructor<PropagandaClient> constructor = cls.getConstructor(String.class);
	    PropagandaClient client = constructor.newInstance(name);
	    getLogger().finest("client: " + client + ' ' + name);
	    return client;
	} catch (ClassNotFoundException ex) {
	    getLogger().log(Level.SEVERE, "crate: ", ex);
	} catch (NoSuchMethodException ex) {
	    getLogger().log(Level.SEVERE, "crate: ", ex);
	} catch (InvocationTargetException ex) {
	    getLogger().log(Level.SEVERE, "crate: ", ex);
	    ex.printStackTrace();
	} catch (IllegalAccessException ex) {
	    getLogger().log(Level.SEVERE, "crate: ", ex);
	} catch (InstantiationException ex) {
	    getLogger().log(Level.SEVERE, "crate: ", ex);
	}
	return new Client_Null();
    }
}
