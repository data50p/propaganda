/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.femtioprocent.propaganda.appl;

import com.femtioprocent.fpd.sundry.Appl;
import static com.femtioprocent.fpd.sundry.Appl.decodeArgs;
import static com.femtioprocent.fpd.sundry.Appl.main;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

/**
 *
 * @author lars
 */
public class MqttDemo extends Appl {

    @Override
    public void main() {
        try {
            String myUniqId = "12345mqtt6789";

            MQTT mqtt = new MQTT();
            BlockingConnection connection;
            mqtt.setHost("tcp://localhost:1883");
            mqtt.setClientId(new UTF8Buffer(myUniqId));
            System.out.println("MqttDemo: " + mqtt + ' ' + "created");
            connection = mqtt.blockingConnection();
            connection.connect();
            connection.subscribe(new Topic[]{new Topic("propaganda-" + myUniqId, QoS.AT_MOST_ONCE)});
            System.out.println("MqttDemo: " + connection + ' ' + "subscribe ");

            String msg = "" + myUniqId + " *@CCFSERVER;{\"Q\":\"lookup\",\"ref\":\"REF\",\"seq\":12345, \"word\":\"komma med\",\"langIn\":\"sv\",\"langOut\":\"bliss,aras\"}";
            connection.publish("propaganda", msg.getBytes("utf-8"), QoS.AT_MOST_ONCE, true);
            System.out.println("MqttDemo: " + "published propaganda: " + msg);

            //msg = "" + myUniqId + " @; list";
            //connection.publish("propaganda", msg.getBytes("utf-8"), QoS.AT_MOST_ONCE, true);
            //System.out.println("" + "published: " + msg);
            
            int cnt = 0;
            for(int i = 0; i < 5; i++) {
                cnt++;
                final Message receive = connection.receive(1000, TimeUnit.MILLISECONDS);
                if ( receive != null )
                    System.out.println("MqttDemo: GOT " + cnt + ": " + new String(receive.getPayload(), "utf-8"));
            }
        } catch (URISyntaxException ex) {
            Logger.getLogger(MqttDemo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(MqttDemo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        decodeArgs(args);
        main(new MqttDemo());
    }
}
