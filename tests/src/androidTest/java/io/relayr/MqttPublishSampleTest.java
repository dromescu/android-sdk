package io.relayr;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class MqttPublishSampleTest {

    @Test public void publishingTest() {
        //mqtt.relayr.io : (8883 with security) (1883 without security)

        String content = "{\"snd_level\":24}";
        String host = "mqtt.relayr.io";
        String transmitterId = "97228c3b-3b4f-4f88-a7b2-17d4ab8a7fff";
        String transmitterSecret = "ZuMfL6yDdxhk";
        String broker = "tcp://" + host + ":1883";
        String deviceId = "2a310028-3075-448b-8d88-7ab49ca4f32c"; // microphone
        String topic = "/v1/" + deviceId + "/data";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient sampleClient = new MqttClient(broker, transmitterId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName(transmitterId);
            connOpts.setPassword(transmitterSecret.toCharArray());
            connOpts.setConnectionTimeout(10000);
            System.out.println("Connecting to broker: "+broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            System.out.println("Publishing message: "+ "test");
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(2);
            sampleClient.publish(topic, message);
            System.out.println("Message published");
            sampleClient.disconnect();
            System.out.println("Disconnected");
            //System.exit(0);
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }

}
