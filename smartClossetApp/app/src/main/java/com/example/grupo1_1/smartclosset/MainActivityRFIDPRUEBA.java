package com.example.grupo1_1.smartclosset;

import static com.example.grupo1_1.comun.MQTT.TAG;
import static com.example.grupo1_1.comun.MQTT.broker;
import static com.example.grupo1_1.comun.MQTT.clientId;
import static com.example.grupo1_1.comun.MQTT.qos;
import static com.example.grupo1_1.comun.MQTT.topicRoot;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.grupo1_1.smartclosset.activities.NuevaPrenda;
import com.example.grupo1_1.smartclosset.activities.PaginaPrenda;
import com.example.grupo1_1.smartclosset.pojos.Prenda;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kotlin.reflect.KFunction;


public class MainActivityRFIDPRUEBA extends AppCompatActivity implements MqttCallback {

    static MqttClient client;
    // creamos una variable para detectar cuando envía datos
    boolean indicadorDeReceptorDatos = false;
    // en esta variable vamos a guardar el id para comprobarlo
    String idDetectado = null;
    FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
    Prenda prendaAuxiliar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba);
        conectarMqtt();
        // en el topic pongo lo de la pagina
        suscribirMqtt("rfid", this);
    }

    public static void conectarMqtt() {
        try {
            Log.i(TAG, "Conectando al broker " + broker);
            client = new MqttClient(broker, clientId, new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setKeepAliveInterval(60);
            connOpts.setWill(topicRoot + "WillTopic", "App desconectada".getBytes(), qos, false);
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
        //indicadorDeReceptorDatos = true;
        String payload = new String(message.getPayload());
        //el id detectado se tendría que coger así:
        idDetectado = payload;
        detectarPrenda(idDetectado);
        Log.d("MQTT", "Recibiendo: " + topic + "->" + payload);
    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void abrirPaginaNueva(String idDetectado) {
        Intent i = new Intent(this, NuevaPrenda.class);
        i.putExtra("idRFID", idDetectado);
        this.startActivity(i);
    }

    public void abrirPaginaPrenda(Prenda prenda) {
        Intent i = new Intent(this, PaginaPrenda.class);
        i.putExtra("prenda", prenda);
        i.putExtra("place", "rfid");
        this.startActivity(i);
    }

    public void detectarPrenda(String idPrendaDetectada) {
        db.collection("usuarios").document(usuario.getUid()).collection("prendas").whereEqualTo("idRFID", idPrendaDetectada)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // si se ha encontrado alguna prenda, se procesa
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                HashMap<String, Object> prendaMap = (HashMap<String, Object>) doc.getData();
                                prendaAuxiliar = new Prenda(prendaMap);
                                abrirPaginaPrenda(prendaAuxiliar);
                            }
                        }
                        // si no se ha encontrado ninguna prenda se abre una pagina nueva
                        else {
                            if(idPrendaDetectada.length() == 18) {
                                new AlertDialog.Builder(MainActivityRFIDPRUEBA.this)
                                        .setTitle("Hemos detectado una nueva ID")
                                        .setMessage("¿Quieres añadir una nueva prenda ahora?")
                                        .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                abrirPaginaNueva(idPrendaDetectada);
                                            }})
                                        .setNegativeButton("Cancelar", null)
                                        .show();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error al obtener prendas", e);
                    }
                });
    }
}