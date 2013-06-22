package propaganda.connector;

import static propaganda.context.Config.getLogger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import propaganda.client.PropagandaClient;
import propaganda.server.PropagandaServer;

public class PropagandaConnectorFactory {

    public static PropagandaConnector create(String class_id, String name) {
        try {
            Class cls = Class.forName("propaganda.connector.Connector_" + class_id);
            getLogger().finest("class: " + cls);
            @SuppressWarnings("unchecked")
            Constructor<PropagandaConnector> constructor = (Constructor<PropagandaConnector>) cls.getConstructor(String.class);
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

    public static PropagandaConnector create(String class_id, String name, PropagandaServer server, PropagandaClient client) {
        PropagandaConnector connector = create(class_id, name);
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
