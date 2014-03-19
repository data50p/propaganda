package com.femtioprocent.propaganda.connector;

import static com.femtioprocent.propaganda.context.Config.getLogger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import com.femtioprocent.propaganda.client.PropagandaClient;
import com.femtioprocent.propaganda.server.PropagandaServer;
import java.util.concurrent.atomic.AtomicInteger;

public class PropagandaConnectorFactory {

    static AtomicInteger connectionCnt = new AtomicInteger();

    private static PropagandaConnector create(String class_id, String namePrefix) {
        try {
            Class cls = Class.forName("com.femtioprocent.propaganda.connector.Connector_" + class_id);
            getLogger().finest("class: " + cls);
            @SuppressWarnings("unchecked")
            Constructor<PropagandaConnector> constructor = (Constructor<PropagandaConnector>) cls.getConstructor(String.class);
            String name = (namePrefix == null ? class_id.toLowerCase() : namePrefix) + '-' + getNextSequence();
            PropagandaConnector con = constructor.newInstance(name);
            getLogger().finest("client: " + con);
            return con;
        } catch (ClassNotFoundException ex) {
            getLogger().log(Level.SEVERE, "crate: ", ex);
        } catch (NoSuchMethodException ex) {
            getLogger().log(Level.SEVERE, "crate: ", ex);
        } catch (InvocationTargetException ex) {
            getLogger().log(Level.SEVERE, "crate: ", ex);
        } catch (IllegalAccessException ex) {
            getLogger().log(Level.SEVERE, "crate: ", ex);
        } catch (InstantiationException ex) {
            getLogger().log(Level.SEVERE, "crate: ", ex);
        }
        return null;
    }

    public static int getNextSequence() {
        return connectionCnt.addAndGet(1);
    }

    public static PropagandaConnector create(String class_id, String namePrefix, PropagandaServer server, PropagandaClient client) {
        PropagandaConnector connector = create(class_id, namePrefix);
        if (connector != null) {
            if (client != null) {
                connector.attachClient(client);
                client.setConnector(connector);
            }
            if (server != null) {
                connector.attachServer(server);
            }
            return connector;
        }
        return null;
    }
}
