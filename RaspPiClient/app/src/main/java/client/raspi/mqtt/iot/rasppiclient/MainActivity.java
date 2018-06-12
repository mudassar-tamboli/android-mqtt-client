package client.raspi.mqtt.iot.rasppiclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import client.raspi.mqtt.iot.R;


public class MainActivity extends AppCompatActivity {

    final String Tag = "mqttclient";
    JSONObject redLedJson = new JSONObject();
    MqttAndroidClient mqttAndroidClient;

    //final String serverUri = "tcp://iot.eclipse.org:1883";
    final String mqttBroker   = "tcp://m14.cloudmqtt.com:14205";
    final String mqttUsername = "htmbxcyz";  // TODO
    final String mqttPassword = "rH2_IZj43nDy"; //TODO

    String clientId = "rasp-pi-android";
    final String subscriptionTopicGreenLed = "plain/led/status/green";
    final String subscriptionTopicBlueLed  = "secure/led/status/blue";
    final String subscriptionTopicRedLed   = "secure/led/status/red";
    final String publishTopic              = "secure/led/action/red";
    final String publishMessage = "{\"LED1\" : \"OFF\"}";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((ImageView) findViewById(R.id.greenled1)).setImageResource(R.drawable.offled);
        ((ImageView) findViewById(R.id.greenled2)).setImageResource(R.drawable.offled);
        ((ImageView) findViewById(R.id.greenled3)).setImageResource(R.drawable.offled);

        ((ImageView) findViewById(R.id.blueled1)).setImageResource(R.drawable.offled);
        ((ImageView) findViewById(R.id.blueled2)).setImageResource(R.drawable.offled);
        ((ImageView) findViewById(R.id.blueled3)).setImageResource(R.drawable.offled);

        ((ImageView) findViewById(R.id.redled1)).setImageResource(R.drawable.offled);
        ((ImageView) findViewById(R.id.redled2)).setImageResource(R.drawable.offled);
        ((ImageView) findViewById(R.id.redled3)).setImageResource(R.drawable.offled);
        try {
            redLedJson.put("LED1", "OFF");
            redLedJson.put("LED2", "OFF");
            redLedJson.put("LED3", "OFF");
        } catch (Exception ex) {
            Log.e(Tag, ex.getMessage());
        }
        ((ImageView) findViewById(R.id.redled1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (redLedJson.getString("LED1").equals("ON")) {
                        redLedJson.put("LED1", "OFF");
                        ((ImageView) findViewById(R.id.redled1)).setImageResource(R.drawable.offled);
                    } else {
                        redLedJson.put("LED1", "ON");
                        ((ImageView) findViewById(R.id.redled1)).setImageResource(R.drawable.redled);
                    }
                    publishMessageRedLedAction();
                } catch (Exception ex) {
                    Log.e(Tag, ex.getMessage());
                }
            }
        });

        ((ImageView) findViewById(R.id.redled2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (redLedJson.getString("LED2").equals("ON")) {
                        redLedJson.put("LED2", "OFF");
                        ((ImageView) findViewById(R.id.redled2)).setImageResource(R.drawable.offled);
                    } else {
                        redLedJson.put("LED2", "ON");
                        ((ImageView) findViewById(R.id.redled2)).setImageResource(R.drawable.redled);
                    }
                    publishMessageRedLedAction();
                } catch (Exception ex) {
                    Log.e(Tag, ex.getMessage());
                }
            }
        });

        ((ImageView) findViewById(R.id.redled3)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (redLedJson.getString("LED3").equals("ON")) {
                        redLedJson.put("LED3", "OFF");
                        ((ImageView) findViewById(R.id.redled3)).setImageResource(R.drawable.offled);
                    } else {
                        redLedJson.put("LED3", "ON");
                        ((ImageView) findViewById(R.id.redled3)).setImageResource(R.drawable.redled);
                    }
                    publishMessageRedLedAction();
                } catch (Exception ex) {
                    Log.e(Tag, ex.getMessage());
                }

            }
        });


        clientId = clientId + System.currentTimeMillis();

        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), mqttBroker, clientId);

        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

                if (reconnect) {
                    Log.e(Tag,"Reconnected to : " + serverURI);
                    // Because Clean Session is true, we need to re-subscribe
                    subscribeToTopic();
                } else {
                    Log.e(Tag, "Connected to: " + serverURI);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.e(Tag, "The Connection was lost.");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.e(Tag, "Incoming message: " + new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setUserName(mqttUsername);
        mqttConnectOptions.setPassword(mqttPassword.toCharArray());

        try {
            //addToHistory("Connecting to " + serverUri);
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(Tag,"Failed to connect to: " + mqttBroker);
                }
            });


        } catch (MqttException ex){
            ex.printStackTrace();
        }
    }

    public void subscribeToTopic(){
        try {
            mqttAndroidClient.subscribe(subscriptionTopicGreenLed, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.e(Tag,"Green Led : Subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(Tag,"Green Led : Failed to subscribe");
                }
            });

            mqttAndroidClient.subscribe(subscriptionTopicBlueLed, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.e(Tag,"Blue Led : Subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(Tag,"Blue Led : Failed to subscribe");
                }
            });


            mqttAndroidClient.subscribe(subscriptionTopicRedLed, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.e(Tag,"Red Led : Subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(Tag,"Red Led : Failed to subscribe");
                }
            });


            mqttAndroidClient.subscribe(subscriptionTopicGreenLed, 0, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // message Arrived!
                    Log.e(Tag, "Message: " + topic + " : " + new String(message.getPayload()));
                    final JSONObject jsonObect = new JSONObject(new String(message.getPayload()));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadGreenLedImage(jsonObect);
                        }
                    });

                }
            });


            mqttAndroidClient.subscribe(subscriptionTopicBlueLed, 0, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // message Arrived!
                    Log.e(Tag, "Message: " + topic + " : " + new String(message.getPayload()));
                    final JSONObject jsonObect = new JSONObject(new String(message.getPayload()));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadBlueLedImage(jsonObect);
                        }
                    });

                }
            });

            mqttAndroidClient.subscribe(subscriptionTopicRedLed, 0, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // message Arrived!
                    Log.e(Tag, "Message: " + topic + " : " + new String(message.getPayload()));
                    final JSONObject jsonObect = new JSONObject(new String(message.getPayload()));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadRedLedImage(jsonObect);
                        }
                    });

                }
            });


        } catch (MqttException ex){
            System.err.println("Exception whilst subscribing");
            ex.printStackTrace();
        }
    }

    public void publishMessageRedLedAction(){

        try {
            final String publishMessage = redLedJson.toString();
            MqttMessage message = new MqttMessage();
            message.setPayload(publishMessage.getBytes());
            mqttAndroidClient.publish(publishTopic, message);
            Log.e(Tag, "Message Published");
            if(!mqttAndroidClient.isConnected()){
                Log.e(Tag, mqttAndroidClient.getBufferedMessageCount() + " messages in buffer.");
            }
        } catch (MqttException e) {
            System.err.println("Error Publishing: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void loadGreenLedImage(JSONObject jsonObect)
    {
        String greenleduri = "@drawable/greenled";
        String offleduri = "@drawable/offled";

        int ledGreenResource = getResources().getIdentifier(greenleduri, null, getPackageName());
        int ledOffResource = getResources().getIdentifier(offleduri, null, getPackageName());
        ((ImageView) findViewById(R.id.greenled1)).setImageResource(R.drawable.offled);
        try {

            ((ImageView) findViewById(R.id.greenled1)).setImageDrawable(getResources().getDrawable(ledOffResource));
            ((ImageView) findViewById(R.id.greenled2)).setImageDrawable(getResources().getDrawable(ledOffResource));
            ((ImageView) findViewById(R.id.greenled3)).setImageDrawable(getResources().getDrawable(ledOffResource));

            if (jsonObect.getString("LED1").equals("ON")) {
                ((ImageView) findViewById(R.id.greenled1)).setImageDrawable(getResources().getDrawable(ledGreenResource));
            }
            if (jsonObect.getString("LED2").equals("ON")) {
                ((ImageView) findViewById(R.id.greenled2)).setImageDrawable(getResources().getDrawable(ledGreenResource));
            }
            if (jsonObect.getString("LED3").equals("ON")) {
                ((ImageView) findViewById(R.id.greenled3)).setImageDrawable(getResources().getDrawable(ledGreenResource));
            }

        } catch (Exception ex) {
            Log.e(Tag, ex.getMessage());
        }

    }

    public void loadBlueLedImage(JSONObject jsonObect)
    {
        String blueleduri = "@drawable/blueled";
        String offleduri = "@drawable/offled";

        int ledBlueResource = getResources().getIdentifier(blueleduri, null, getPackageName());
        int ledOffResource = getResources().getIdentifier(offleduri, null, getPackageName());
        ((ImageView) findViewById(R.id.blueled1)).setImageResource(R.drawable.offled);
        try {

            ((ImageView) findViewById(R.id.blueled1)).setImageDrawable(getResources().getDrawable(ledOffResource));
            ((ImageView) findViewById(R.id.blueled2)).setImageDrawable(getResources().getDrawable(ledOffResource));
            ((ImageView) findViewById(R.id.blueled3)).setImageDrawable(getResources().getDrawable(ledOffResource));

            if (jsonObect.getString("LED1").equals("ON")) {
                ((ImageView) findViewById(R.id.blueled1)).setImageDrawable(getResources().getDrawable(ledBlueResource));
            }
            if (jsonObect.getString("LED2").equals("ON")) {
                ((ImageView) findViewById(R.id.blueled2)).setImageDrawable(getResources().getDrawable(ledBlueResource));
            }
            if (jsonObect.getString("LED3").equals("ON")) {
                ((ImageView) findViewById(R.id.blueled3)).setImageDrawable(getResources().getDrawable(ledBlueResource));
            }

        } catch (Exception ex) {
            Log.e(Tag, ex.getMessage());
        }

    }

    public void loadRedLedImage(JSONObject jsonObect)
    {
        String redleduri = "@drawable/redled";
        String offleduri = "@drawable/offled";

        int ledRedResource = getResources().getIdentifier(redleduri, null, getPackageName());
        int ledOffResource = getResources().getIdentifier(offleduri, null, getPackageName());
        ((ImageView) findViewById(R.id.redled1)).setImageResource(R.drawable.offled);
        try {

            ((ImageView) findViewById(R.id.redled1)).setImageDrawable(getResources().getDrawable(ledOffResource));
            ((ImageView) findViewById(R.id.redled2)).setImageDrawable(getResources().getDrawable(ledOffResource));
            ((ImageView) findViewById(R.id.redled3)).setImageDrawable(getResources().getDrawable(ledOffResource));

            if (jsonObect.getString("LED1").equals("ON")) {
                redLedJson.put("LED1", "ON");
                ((ImageView) findViewById(R.id.redled1)).setImageDrawable(getResources().getDrawable(ledRedResource));
            } else {
                redLedJson.put("LED1", "OFF");
            }
            if (jsonObect.getString("LED2").equals("ON")) {
                redLedJson.put("LED2", "ON");
                ((ImageView) findViewById(R.id.redled2)).setImageDrawable(getResources().getDrawable(ledRedResource));
            } else {
                redLedJson.put("LED2", "OFF");
            }
            if (jsonObect.getString("LED3").equals("ON")) {
                redLedJson.put("LED3", "ON");
                ((ImageView) findViewById(R.id.redled3)).setImageDrawable(getResources().getDrawable(ledRedResource));
            } else {
                redLedJson.put("LED3", "OFF");
            }

        } catch (Exception ex) {
            Log.e(Tag, ex.getMessage());
        }
    }

}
