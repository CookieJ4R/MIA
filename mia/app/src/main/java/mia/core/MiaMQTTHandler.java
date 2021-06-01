package mia.core;

import org.eclipse.paho.client.mqttv3.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/***
 * This class handles everything MQTT related.
 * The handler listens on every topic and will emit a command on the CommandBus when a message gets received
 * These emits will have the following form: "mqtt:TOPIC" and will have the received message split at " " as data
 * Modules can publish to the mqtt broker via 'publish()'
 */
public final class MiaMQTTHandler implements IMiaShutdownable{

    //Standard-Values can be overwritten by overloading the constructor
    private String broker = "tcp://127.0.0.1";
    private String clientId = "MiaMqttClient";

    private MqttClient miaClient;

    public MiaMQTTHandler(String brokerIP){
        this.broker = "tcp://"+brokerIP;
        init();
    }

    public MiaMQTTHandler(String brokerIP, String clientId){
        this.broker = "tcp://"+brokerIP;
        this.clientId = clientId;
        init();
    }

    public MiaMQTTHandler(){
        init();
    }

    /***
     * Initializes the MqttHandler
     */
    private void init(){
        try {
            miaClient = new MqttClient(broker, clientId);
            Mia.getLogger().logInfo("Connecting to broker: "+ broker);
            miaClient.connect();
            Mia.getLogger().logInfo("Connected to broker!");
            miaClient.subscribe("#");
            miaClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Mia.getLogger().logError("Lost connection to mqtt broker", false);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message){
                    Mia.getLogger().logInfo("Received message '" + message.toString().trim() + "' on topic '" + topic + "'");
                    String[] messageParts = message.toString().trim().split(" ");
                    List<String> additionalData = new ArrayList<>();
                    additionalData.addAll(Arrays.asList(messageParts).subList(1, messageParts.length));
                    Mia.getCommandBus().emit("mqtt:" + topic, messageParts[0], additionalData);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Mia.getLogger().logInfo("Successfully published message");
                }
            });
        } catch(MqttException me) {
            me.printStackTrace();
            Mia.getLogger().logError("Error while connecting to mqtt-broker", true);
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
