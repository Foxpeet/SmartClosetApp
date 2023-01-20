package com.example.grupo1_1.smartclosset;

import static com.example.grupo1_1.comun.MQTT.TAG;
import static com.example.grupo1_1.comun.MQTT.broker;
import static com.example.grupo1_1.comun.MQTT.clientId;
import static com.example.grupo1_1.comun.MQTT.qos;
import static com.example.grupo1_1.comun.MQTT.topicRoot;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.example.grupo1_1.smartclosset.pojos.Prenda;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class paginaAdicionRFID extends AppCompatActivity implements MqttCallback{

    Prenda prenda = new Prenda();
    static MqttClient client;
    String payload = null;
    String idRFID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //función para quitar la Action Bar, hay que colocarla siempre
        //antes de declarar la activity_view
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_anyadir_rfid);

        prenda.setNombrePrenda("Camiseta");

        conectarMqtt();
        suscribirMqtt("hola", this);

    }

    public static void publicarMqtt(String topic, String mensageStr) {
        try {
            MqttMessage message = new MqttMessage(mensageStr.getBytes()); message.setQos(qos);
            message.setRetained(false);
            client.publish(topicRoot + topic, message);
            Log.i(TAG, "Publicando mensaje: " + topic+ "->"+mensageStr); } catch (MqttException e) {
            Log.e(TAG, "Error al publicar." + e); }
    }

    public static void conectarMqtt() {
        try {
            Log.i(TAG, "Conectando al broker " + broker);
            client = new MqttClient(broker, clientId, new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setKeepAliveInterval(60);
            connOpts.setWill(topicRoot+"WillTopic","App desconectada".getBytes(), qos, false);
            client.connect(connOpts);
        } catch (MqttException e) {
            Log.e(TAG, "Error al conectar.", e);
        }
    }

    public static void suscribirMqtt(String topic, MqttCallback listener) {
        try {
            Log.i(TAG, "Suscrito a " + topicRoot + topic);
            client.subscribe(topicRoot + topic, qos);
            client.setCallback(listener);
        } catch (MqttException e) {
            Log.e(TAG, "Error al suscribir.", e);
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(TAG, "Conexión perdida");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(TAG, "Entrega completa");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message)
            throws Exception {
        payload = new String(message.getPayload());
        Log.d(TAG, "Recibiendo: " + topic + "->" + payload);
        Log.i("prendaPrueba", prenda.getIdRFID());
    }

}
