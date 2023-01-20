package com.example.grupo1_1.smartclosset.activities;

import static com.example.grupo1_1.comun.MQTT.TAG;
import static com.example.grupo1_1.comun.MQTT.broker;
import static com.example.grupo1_1.comun.MQTT.clientId;
import static com.example.grupo1_1.comun.MQTT.qos;
import static com.example.grupo1_1.comun.MQTT.topicRoot;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.grupo1_1.smartclosset.R;
import com.example.grupo1_1.smartclosset.adaptadores.AdaptadorPrendasFirestoreUIL;
import com.example.grupo1_1.smartclosset.databinding.ActivityLandingBinding;
import com.example.grupo1_1.smartclosset.pojos.Prenda;
import com.example.grupo1_1.smartclosset.pojos.Usuario;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.HashMap;
import java.util.Objects;

public class LandingPage extends AppCompatActivity implements MqttCallback {

    // para la luz
    String control;
    public static MqttClient client;

    // para la temepratura
    String dTemperatura = null;
    String dHumedad = null;
    long datoTemperatura = 0;
    long datoHumedad = 0;

    // para el detector de RFID
    String idDetectado = null;
    Prenda prendaAuxiliar;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
    Usuario user = new Usuario(usuario);

    // declaramos aquí las ImageView de los botones que salen del "+"
    ImageView btnAnyadirConjunto;
    ImageView btnAnyadirPrenda;

    boolean menuDesplegable = false;

    private ActivityLandingBinding binding;
    public static AdaptadorPrendasFirestoreUIL adaptador;

    // -------------------------------------------------------------------------------------
    // linea que hay bajo la casa en el menú inferior de la landing
    ImageView lineaMarcaLanding;
    // -------------------------------------------------------------------------------------
    Button buttonVerTodasPrendas;

    // -------------------------------------------------------------------------------------
    // TODAS LAS COSAS DE LA ANIMACIÓN DEL MENÚ INFERIOR
    // -------------------------------------------------------------------------------------
    // elementos del menú inferior desplegable para los ajustes del armario
    // -------------------------------------------------------------------------------------
    ImageView iconoBombilla;
    ImageView settings;
    ImageView cerrarSettings;
    ImageView iconoLanding;
    ImageView iconoPaginaUser;
    ImageView iconoAnyadirPrendaMenuInferior;
    ImageView btnIconoTodasLasPrendas;
    ImageView indicadorBombillaEncendida;
    ImageView barraSeparadora;

    TextView noTienesPrendas;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //función para quitar la Action Bar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        binding = ActivityLandingBinding.inflate(getLayoutInflater()); //F
        setContentView(binding.getRoot()); //F

        // MQTT y luz
        conectarMqtt();
        suscribirMqtt("luz", this);
        // Temperatura
        suscribirMqtt("temperatura", this);
        // Humedad
        suscribirMqtt("humedad", this);
        // detector RFID
        suscribirMqtt("rfid", this);

        // para controlar el encendido o apagado de las luces
        String controlAux;
        controlAux = (String) getIntent().getSerializableExtra("luz");
        if (controlAux != null) {
            control = controlAux;
            controlLuces();
        }

        // -------------------------------------------------------------------------------------
        // elementos del menú inferior desplegable para los ajustes del armario
        // -------------------------------------------------------------------------------------
        iconoBombilla = findViewById(R.id.btnIconoBombilla);
        settings = findViewById(R.id.btnIconoSettings);
        cerrarSettings = findViewById(R.id.btnIconoSettingsArmarioCerrar);
        indicadorBombillaEncendida = findViewById(R.id.bombillaEncendida);
        indicadorBombillaEncendida = findViewById(R.id.bombillaEncendida);

        barraSeparadora = findViewById(R.id.barraSeparadora);

        // inicialmente tienen que estar transparentes
        cerrarSettings.setAlpha(0.0f);
        indicadorBombillaEncendida.setAlpha(0.0f);
        iconoBombilla.setAlpha(0.0f);

        // el texto de que no tienes prendas al principio esta invisible
        noTienesPrendas = findViewById(R.id.textoNoTienesPrendas);
        noTienesPrendas.setAlpha(0.0f);

        iconoLanding = findViewById(R.id.imageView6);
        iconoPaginaUser = findViewById(R.id.lineaMenuPerfil);
        iconoPaginaUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirPaginaUser(view);
            }
        });

        iconoAnyadirPrendaMenuInferior = findViewById(R.id.imageView5);
        iconoAnyadirPrendaMenuInferior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirPaginaNuevaPrenda(v);
            }
        });

        btnIconoTodasLasPrendas = findViewById(R.id.btnIconoTodasLasPrendas);
        btnIconoTodasLasPrendas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirPaginaTodasLasPrendas(view);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirSettingsArmario(view);
            }
        });

        cerrarSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vm) {
                cerrarSettingsArmario(vm);
            }
        });

        // -------------------------------------------------------------------------------------
        // -------------------------------------------------------------------------------------
        // Hacemos que la línea que marca la landing solo se muestre en esta página
        // Inicialmente -> la línea tendrá la opacidad al cero
        lineaMarcaLanding = findViewById(R.id.lineaMenuLanding);
        lineaMarcaLanding.setAlpha(0.7f);
        // -------------------------------------------------------------------------------------
        // -------------------------------------------------------------------------------------

        buttonVerTodasPrendas = findViewById(R.id.buttonVerTodasPrendas);

        // -----------------------------------------------------------
        TextView temperatura = findViewById(R.id.temperatura);
        TextView humedad = findViewById(R.id.humedad);

        setRecyclerView();
        comprobarSiHayPrendas();

        // delay para darle tiempo a la temperatura a cargar
        Handler s3 = new Handler();
        s3.postDelayed(new Runnable() {
            @Override
            public void run() {
                String t = dTemperatura;
                temperatura.setText(t);
                String h = dHumedad;
                humedad.setText(h);
            }
        }, 3000);

        TextView nombre = findViewById(R.id.nombreUsuarioLanding);
        nombre.setText(user.getNombre());

    }

    public void animacionAnyadir(View view) {
        if (menuDesplegable == false) {
            btnAnyadirConjunto.animate().translationY(-190).translationX(120).scaleY(4).scaleX(4).setDuration(1000);
            btnAnyadirPrenda.animate().translationY(-190).translationX(-120).scaleY(4).scaleX(4).setDuration(1000);
            btnAnyadirPrenda.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
            btnAnyadirConjunto.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
            menuDesplegable = true;

            // si los botones han sido desplegados, entonces se puede clickar
            btnAnyadirConjunto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    abrirPaginaNuevoConjunto();
                }
            });

            btnAnyadirPrenda.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //abrirPaginaNuevaPrenda();
                }
            });
        } else {
            btnAnyadirConjunto.animate().translationY(0).translationX(0).scaleY(1).scaleX(1).setDuration(1000);
            btnAnyadirPrenda.animate().translationY(0).translationX(0).scaleY(1).scaleX(1).setDuration(1000);
            btnAnyadirPrenda.animate().alpha(0).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
            btnAnyadirConjunto.animate().alpha(0).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
            menuDesplegable = false;
        }
    }

    public void setRecyclerView() {
        // inicializamos la variable y luego, en función de los datos le damos valores
        Query query = null;

        boolean noEmpty = true;
        noEmpty = comprobarSiHayRecomendados();
        if (noEmpty) {
            query = FirebaseFirestore.getInstance()
                    .collection("usuarios").document(usuario.getUid()).collection("prendas").whereLessThan("contadorUsos", 18);

        } else {
            query = FirebaseFirestore.getInstance()
                    .collection("usuarios").document(usuario.getUid()).collection("prendas")
                    .limit(8);

        }

        FirestoreRecyclerOptions<Prenda> options = new FirestoreRecyclerOptions
                .Builder<Prenda>().setQuery(query, Prenda.class).build();
        adaptador = new AdaptadorPrendasFirestoreUIL(options, this);
        binding.recyclerView.setAdapter(adaptador);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerView.setHasFixedSize(true);

        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = binding.recyclerView.getChildAdapterPosition(v);
                abrirPaginaPrenda(options.getSnapshots().get(pos));
            }
        });
        adaptador.startListening();
    }

    public void abrirPaginaPrenda(Prenda prenda) {
        Intent i = new Intent(this, PaginaPrenda.class);
        i.putExtra("prenda", prenda);
        this.startActivity(i);
    }

    public void abrirPaginaNuevoConjunto() {
        Intent i = new Intent(this, NuevoConjuntoActivity.class);
        this.startActivity(i);
    }

    public void abrirMapa(View view) {
        Intent i = new Intent(this, MapsActivity.class);
        this.startActivity(i);
    }

    public void abrirPaginaCalendario(View view) {
        Intent i = new Intent(this, PaginaCalendario.class);
        this.startActivity(i);
    }


    public void abrirPaginaUser(View view) {
        Intent i = new Intent(this, PerfilUsuarioActivity.class);
        this.startActivity(i);
    }

    public void abrirPaginaNuevaPrenda(View view) {
        Intent i = new Intent(this, NuevaPrenda.class);
        this.startActivity(i);
    }

    public void abrirPaginaTodasLasPrendas(View view) {
        Intent i = new Intent(this, MainActivity.class);
        this.startActivity(i);
    }

    public void abrirSettingsArmario(View view) {
        // esto aparece
        iconoBombilla.animate().alpha(1).setDuration(1200).setInterpolator(new DecelerateInterpolator()).start();
        cerrarSettings.animate().alpha(1).setDuration(1200).setInterpolator(new DecelerateInterpolator()).start();
        // onClick para encender la luz
        if (control == "OFF") {
            indicadorBombillaEncendida.setAlpha(0.0f);
        }
        if (control == "ON") {
            indicadorBombillaEncendida.animate().alpha(1).setDuration(1200).setInterpolator(new DecelerateInterpolator()).start();
        }
        iconoBombilla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlLuces();
                publicarMqtt("luz", control);
                Log.i("MQTT", "luz");
                Log.i("MQTT", control);
            }
        });
        // queremos que bajen las otras cosas
        settings.animate().translationY(400).setDuration(500);
        btnIconoTodasLasPrendas.animate().translationY(400).setDuration(540);
        iconoAnyadirPrendaMenuInferior.animate().translationY(400).setDuration(580);
        iconoLanding.animate().translationY(400).setDuration(620);
        lineaMarcaLanding.animate().translationY(400).setDuration(660);
        iconoPaginaUser.animate().translationY(400).setDuration(700);
        // en la página de user aquí hay que nombrarlo tmb
    }

    public void cerrarSettingsArmario(View view) {
        iconoBombilla.animate().alpha(0).setDuration(550).setInterpolator(new DecelerateInterpolator()).start();
        cerrarSettings.animate().alpha(0).setDuration(550).setInterpolator(new DecelerateInterpolator()).start();
        indicadorBombillaEncendida.animate().alpha(0).setDuration(550).setInterpolator(new DecelerateInterpolator()).start();
        // queremos que bajen las otras cosas
        settings.animate().translationY(0).setDuration(600);
        btnIconoTodasLasPrendas.animate().translationY(0).setDuration(560);
        iconoAnyadirPrendaMenuInferior.animate().translationY(0).setDuration(520);
        iconoLanding.animate().translationY(0).setDuration(480);
        lineaMarcaLanding.animate().translationY(0).setDuration(440);
        iconoPaginaUser.animate().translationY(0).setDuration(400);
    }

    // MQTT, luces y temepratura e humedad
    public void controlLuces() {
        if (control == "ON") {
            control = "OFF";
        } else {
            control = "ON";
        }
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

    public static void publicarMqtt(String topic, String mensageStr) {
        try {
            MqttMessage message = new MqttMessage(mensageStr.getBytes());
            message.setQos(qos);
            message.setRetained(false);
            client.publish(topicRoot + topic, message);
            Log.i(TAG, "Publicando mensaje: " + topic + "->" + mensageStr);
        } catch (MqttException e) {
            Log.e(TAG, "Error al publicar." + e);
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
        String payload = new String(message.getPayload());
        funcionRecepcion(topic, payload);
        Log.d("MQTT", "Recibiendo: " + topic + "->" + payload);
    }

    public void funcionRecepcion(String topic, String message) {
        if(Objects.equals(topic, "ioT1/sensores/rfid")) {
            detectarPrenda(message);
        }
        if(Objects.equals(topic, "ioT1/sensores/temperatura")) {
            guardarValoresTemperatura(topic, message);
        }
        if(Objects.equals(topic, "ioT1/sensores/humedad")) {
            guardarValoresHumedad(topic, message);
        }
    }

    public void abrirPaginaNuevaMqtt(String idDetectado) {
        Intent i = new Intent(this, NuevaPrenda.class);
        i.putExtra("idRFID", idDetectado);
        this.startActivity(i);
    }

    public void abrirPaginaPrendaMqtt(Prenda prenda) {
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
                                Log.i("MQTT", "prueba");
                                HashMap<String, Object> prendaMap = (HashMap<String, Object>) doc.getData();
                                prendaAuxiliar = new Prenda(prendaMap);
                                abrirPaginaPrendaMqtt(prendaAuxiliar);
                            }
                        }
                        // si no se ha encontrado ninguna prenda se abre una pagina nueva
                        else {
                            if (idPrendaDetectada.length() > 5) {
                                new AlertDialog.Builder(LandingPage.this)
                                        .setTitle(getResources().getString(R.string.Rfid_titulo))
                                        .setMessage(getResources().getString(R.string.Rfid_mensaje))
                                        .setPositiveButton(getResources().getString(R.string.Rfid_continuar), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                abrirPaginaNuevaMqtt(idPrendaDetectada);
                                            }
                                        })
                                        .setNegativeButton(getResources().getString(R.string.Comunes_cancelar), null)
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

    public void comprobarSiHayPrendas() {
        db.collection("usuarios").document(usuario.getUid()).collection("prendas")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            noTienesPrendas.setAlpha(1.0f);

                        } else {
                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                noTienesPrendas.setAlpha(0.0f);
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

    private boolean resultado;

    public boolean comprobarSiHayRecomendados() {
        db.collection("usuarios").document(usuario.getUid()).collection("prendas").whereLessThan("contadorUsos", 18)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            resultado = false;

                        } else {
                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                resultado = true;

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
        return resultado;
    }

    public void guardarValoresTemperatura(String topic, String message) {
        if (Objects.equals(topic, "ioT1/sensores/temperatura")) {
            dTemperatura = message;
        }
    }

    public void guardarValoresHumedad(String topic, String message) {
        if (Objects.equals(topic, "ioT1/sensores/humedad")) {
            dHumedad = message;
        }
    }

    public void lanzarCrearConjunto(View view) {
        Intent i = new Intent(this, NuevoConjuntoActivity.class);
        this.startActivity(i);
    }
    @Override
    public void onBackPressed() {

    }
}
