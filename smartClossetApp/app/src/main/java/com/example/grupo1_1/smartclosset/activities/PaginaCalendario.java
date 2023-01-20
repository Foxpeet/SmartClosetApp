package com.example.grupo1_1.smartclosset.activities;

import static com.example.grupo1_1.comun.MQTT.TAG;
import static com.example.grupo1_1.comun.MQTT.broker;
import static com.example.grupo1_1.comun.MQTT.clientId;
import static com.example.grupo1_1.comun.MQTT.qos;
import static com.example.grupo1_1.comun.MQTT.topicRoot;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.grupo1_1.smartclosset.MainActivityRFIDPRUEBA;
import com.example.grupo1_1.smartclosset.adaptadores.AdaptadorPrendasFirestoreUICalendario;
import com.example.grupo1_1.smartclosset.databinding.CalendarioBinding;
import com.example.grupo1_1.smartclosset.pojos.Prenda;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class PaginaCalendario extends AppCompatActivity implements MqttCallback{

    static MqttClient client;
    String idDetectado = null;
    Prenda prendaAuxiliar;

    Context context;

    private CalendarioBinding binding;
    public FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
    public static AdaptadorPrendasFirestoreUICalendario adaptador;

    public String fechaCompra;
    public CalendarView calendario;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        binding = CalendarioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = binding.getRoot().getContext();

        conectarMqtt();
        // en el topic pongo lo de la pagina
        suscribirMqtt("rfid", this);

        calendario = binding.calendarView;
        calendario.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                fechaCompra = dayOfMonth + " / " + (month + 1) + " / " + year;
                mostrarPrendasPorFecha(fechaCompra);
            }
        });
        Date fecha = new Date(calendario.getDate());
        //el formato en el que tenemos guardadas las fechas en las prendas
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        fechaCompra = dateFormat.format(fecha);
        mostrarPrendasPorFecha(fechaCompra);
    }

    public void mostrarPrendasPorFecha(String fechaCompra){
        Query query = FirebaseFirestore.getInstance()
                .collection("usuarios").document(usuario.getUid()).collection("prendas")
                .limit(50).whereEqualTo("fechaCompra", fechaCompra);
        FirestoreRecyclerOptions<Prenda> options = new FirestoreRecyclerOptions
                .Builder<Prenda>().setQuery(query, Prenda.class).build();
        adaptador = new AdaptadorPrendasFirestoreUICalendario(options, this);
        binding.recyclerView.setAdapter(adaptador);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerView.setHasFixedSize(true);
        adaptador.startListening();
    }

    public void lanzarAnyadirPrendaDesdeCalendario(View v){
        Intent i = new Intent(this, NuevaPrenda.class);
        i.putExtra("fecha", fechaCompra);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, LandingPage.class);
        this.startActivity(i);
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
                            if(idPrendaDetectada.length() == 18 && idPrendaDetectada.length()==17) {
                                new AlertDialog.Builder(context)
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
