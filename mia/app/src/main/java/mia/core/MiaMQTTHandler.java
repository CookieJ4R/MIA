package mia.core;

import org.eclipse.paho.client.mqttv3.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/***
 * This class handles everything MQTT related.
 * The handler listens on every topic and will emit a command on the CommandBus when a message gets received
 * These emits will have the following form: "mqtt:TOPIC" and will have the received message split at " " as data
 * Modules can publish to the mqtt broker via 'publish()'
 */
public final class MiaMQTTHandler implements IMiaShutdownable{

    private final String broker = "tcp://192.168.2.128";
    private final String clientId = "MiaMqttClient";

    private MqttClient miaClient;

    public MiaMQTTHandler(){
        init();
    }

    /***
     * Initializes the MqttHandler
     */
    private void init(){
        try {
            miaClient = new MqttClient(broker, clientId);
            System.out.println("Connecting to broker: "+ broker);
            miaClient.connect();
            System.out.println("Connected");
            miaClient.subscribe("#");
            miaClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Lost connection to broker");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message){
                    System.out.println("MQTT Message received");
                    Mia.getCommandBus().emit("mqtt:" + topic, Arrays.stream(message.toString().trim().split(" ")).toList());
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("Successfully published message");
                }
            });
        } catch(MqttException me) {
            me.printStackTrace();
        }
    }

    /***
     * Publish a message to the connected mqtt broker
     * @param topic the topic to publish to
     * @param message the message to publish
     */
    void publish(String topic, String message){
        try {
            miaClient.publish(topic, new MqttMessage(message.getBytes(StandardCharsets.UTF_8)));
        }catch(MqttException e){
            e.printStackTrace();
        }
    }

    public void shutdown(){
        try {
            miaClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}
