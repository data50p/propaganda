package com.femtioprocent.propaganda.server;

import com.femtioprocent.fpd.appl.Appl;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import com.femtioprocent.propaganda.client.PropagandaClient;
import com.femtioprocent.propaganda.connector.Connector_Queue;
import com.femtioprocent.propaganda.connector.PropagandaConnector;
import com.femtioprocent.propaganda.connector.PropagandaConnectorFactory;
import com.femtioprocent.propaganda.data.AddrType;
import com.femtioprocent.propaganda.data.Datagram;
import static com.femtioprocent.propaganda.server.HttpWSServer.server;

public class MQTTServer {

    static int cnt = 1;
    static PropagandaServer server;
    static public Endpoint e;
    Connector_Queue connector;
    AtomicInteger ws_cnt = new AtomicInteger(1000);
    MQTT mqtt = new MQTT();
    BlockingConnection connection;

    static class MqttClient extends PropagandaClient {

	public MqttClient() {
	    super("Mqtt");
	}
    }
    MqttClient mqttClient;

    public MQTTServer(PropagandaServer server) {
	this.server = server;
	startMqttBroker();
	mqttClient = new MqttClient();
	connector = (Connector_Queue) PropagandaConnectorFactory.create("Queue", "MQTT", server, mqttClient);
    }

    public void processMqttPayload() {
	try {
	    for (;;) {
		final Message receive = connection.receive(100, TimeUnit.MILLISECONDS);
		if (receive != null) {
		    List<Datagram> dgrList = createDatagrams(receive);
		    System.out.println("MQTTServer: got(mqtt) " + dgrList);
		    for (Datagram dgr : dgrList) {
			server.dispatcher.dispatchMsg(connector, dgr, null);
		    }
		    receive.ack();
		}
		final Datagram recvMsg = connector.recvMsg(100);
		if (recvMsg != null) {
		    final AddrType r = recvMsg.getReceiver();
		    System.out.println("MQTTServer: got(p) " + "propaganda-" + r.getId() + ' ' + recvMsg);
		    connection.publish("propaganda-" + r.getId(), recvMsg.getMessage().getText().getBytes("utf-8"), QoS.AT_MOST_ONCE, true);
		}
	    }
	} catch (Exception ex) {
	    Logger.getLogger(MQTTServer.class.getName()).log(Level.SEVERE, null, ex);
	}
	try {
	    connection.disconnect();
	} catch (Exception ex) {
	}
    }

    public void start(PropagandaServer server) throws Exception {
	try {
	    String mqConnectUrl = "tcp://localhost:1883";
	    System.out.println("MQTTServer: connecting to " + mqConnectUrl);
	    mqtt.setHost(mqConnectUrl);
	    mqtt.setClientId(new UTF8Buffer("propaganda"));
	    connection = mqtt.blockingConnection();
	    connection.connect();
	    //connection.publish("propaganda-io", ". @ register;request-id mqtt-1@MQTT".getBytes("utf-8"), QoS.AT_MOST_ONCE, true);
	    //connection.publish("propaganda-register", "mqtt-1".getBytes("utf-8"), QoS.AT_MOST_ONCE, true);
	    connection.subscribe(new Topic[]{new Topic("propaganda", QoS.AT_MOST_ONCE), new Topic("propaganda-i", QoS.AT_MOST_ONCE), new Topic("propaganda-i", QoS.AT_MOST_ONCE), new Topic("propaganda-io", QoS.AT_MOST_ONCE)});
	    System.out.println("MQTTServer, MQTT bridge: subscribe topic propaganda");
	} catch (URISyntaxException ex) {
	    Logger.getLogger(MQTTServer.class.getName()).log(Level.SEVERE, null, ex);
	}
    }
    AtomicInteger i_cnt = new AtomicInteger();

    private String autoRegister(PropagandaConnector connector, List<String> hm) {
//        String myId = "" + connector.so.getLocalAddress().getHostAddress() + '-' + connector.so.getPort()+ '-' + this.hashCode() + '@' + "AUTOREGISTRED";
	String myId = "" + "MQTT" + '-' + connector.name + '-' + i_cnt.incrementAndGet() + '@' + "AUTOREGISTRED" + '-' + this.hashCode();
	hm.add(". @ register;request-id " + myId);
	return myId;
    }

    private String autoUnRegister(String autoAddr) {
	return autoAddr + " @ unregister;";
    }

    private List<Datagram> createDatagrams(Message receive) {
	List<Datagram> list = new ArrayList<Datagram>();
	try {
	    String s = new String(receive.getPayload(), "utf-8");
	    String[] sa = s.split(" ", 2);
	    String uniqId = sa[0];
	    Datagram d = new Datagram(". @ register; request-id " + uniqId + "@MQTT");
	    Datagram d2 = new Datagram("" + uniqId + "@MQTT " + sa[1]);
	    list.add(d);
	    list.add(d2);
	} catch (UnsupportedEncodingException ex) {
	    Logger.getLogger(MQTTServer.class.getName()).log(Level.SEVERE, null, ex);
	}
	return list;
    }

    void startMqttBroker() {
	if (false && Appl.flags.containsKey("MB")) {
	    try {
		String[] args = {};
		// unsupported org.dna.mqtt.moquette.server.Server.main(args);
		//System.out.println("MQTTServer: moquette started");
		//Thread.sleep(2000);

//            } catch (IOException ex) {
//                Logger.getLogger(MQTTServer.class.getId()).log(Level.SEVERE, null, ex);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(MQTTServer.class.getId()).log(Level.SEVERE, null, ex);
	    } finally {
	    }
	} else {
	    System.out.println("MQTTServer: no moquette broker here");
	}
    }
}
