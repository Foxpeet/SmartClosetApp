package com.example.grupo1_1.smartclosset.activities;


import static com.example.grupo1_1.comun.MQTT.TAG;
import static com.example.grupo1_1.comun.MQTT.broker;
import static com.example.grupo1_1.comun.MQTT.clientId;
import static com.example.grupo1_1.comun.MQTT.qos;
import static com.example.grupo1_1.comun.MQTT.topicRoot;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.grupo1_1.smartclosset.R;
import com.example.grupo1_1.smartclosset.adaptadores.AdaptadorConjuntosFirestoreUI;
import com.example.grupo1_1.smartclosset.adaptadores.AdaptadorPrendasFirestoreUI;
import com.example.grupo1_1.smartclosset.databinding.ActivityMainBinding;
import com.example.grupo1_1.smartclosset.pojos.Conjunto;
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

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements MqttCallback {

    static MqttClient client;
    String idDetectado = null;
    FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
    Prenda prendaAuxiliar;

    private ActivityMainBinding binding;
    public AdaptadorPrendasFirestoreUI adaptador;
    public AdaptadorConjuntosFirestoreUI adaptadorConjuntos;

    public String filtroActivo;
    public boolean filtroActivo2;
    public boolean filtroConjuntoActivo = false;
    ImageView CamisetaImage;
    ImageView PantalonImage;
    ImageView AccesorioImage;
    ImageView FavoritosImage;
    ImageView ConjuntosImage;

    boolean activoParteDeArriba;
    boolean activoParteDeAbajo;
    boolean activoAccesorios;
    boolean activoFavoritos;

    // marcador para hacer funcional o no el fondo cuando el menú esté desplegado
    // inicialmente -> menú plegado
    boolean enabledMenu = false;

    ImageView menuIcono;

    // menu lateral incluido
    View menuLateral;
    // el inferior quiere Paula que se esconda cuando se despliega el lateral
    View menuInferior;

    ImageView blackBackground;

    RecyclerView recycler;

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
    ImageView lineaMarcadorTodasPrendas;
    ImageView cerrarMenuLateral;
    TextView  noTienesPrenda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //función para quitar la Action Bar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        binding = ActivityMainBinding.inflate(getLayoutInflater()); //F
        setContentView(binding.getRoot()); //F

        comprobarSiHayPrendas();

        conectarMqtt();
        // en el topic pongo lo de la pagina
        suscribirMqtt("rfid", this);

        //filtro desactivado
        filtroActivo = "ninguno";

        setRecyclerView();

        menuIcono = findViewById(R.id.menuIconoMain);

        //meterle la funcion del filtro como onclick en las imageview
        CamisetaImage = findViewById(R.id.camisetasTab);
        PantalonImage = findViewById(R.id.pantalonesTab);
        AccesorioImage = findViewById(R.id.completosTab);
        FavoritosImage = findViewById(R.id.favoritosTab);
        ConjuntosImage = findViewById(R.id.conjuntosTab);
        noTienesPrenda = findViewById(R.id.textView10);

        // menu lateral incluido
        menuLateral = findViewById(R.id.menu_lateral_mainActivity);
        cerrarMenuLateral=findViewById(R.id.CancelarTab);
        menuInferior = findViewById(R.id.menu_inferior_main);
        blackBackground = findViewById(R.id.black_background_main);
        recycler = findViewById(R.id.recyclerView);

        // inicialmente escondido
        menuLateral.setTranslationX(300);
        blackBackground.setAlpha(0.0f);

        // -------------------------------------------------------------------------------------
        // elementos del menú inferior desplegable para los ajustes del armario
        // -------------------------------------------------------------------------------------
        iconoBombilla = findViewById(R.id.btnIconoBombilla);
        settings = findViewById(R.id.btnIconoSettings);
        cerrarSettings = findViewById(R.id.btnIconoSettingsArmarioCerrar);
        indicadorBombillaEncendida = findViewById(R.id.bombillaEncendida);
        indicadorBombillaEncendida = findViewById(R.id.bombillaEncendida);
        lineaMarcadorTodasPrendas = findViewById(R.id.lineaMarcadorTodasLasPrendas);

        lineaMarcadorTodasPrendas.setAlpha(1.0f);

        barraSeparadora = findViewById(R.id.barraSeparadora);

        // inicialmente tienen que estar transparentes
        cerrarSettings.setAlpha(0.0f);
        indicadorBombillaEncendida.setAlpha(0.0f);
        iconoBombilla.setAlpha(0.0f);

        iconoLanding = findViewById(R.id.imageView6);
        iconoLanding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirLandingPage(view);
            }
        });
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

        cerrarMenuLateral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcionCerrarMenuLateral();
            }
        });
    }

    public void setRecyclerView() {
        Query query = FirebaseFirestore.getInstance()
                .collection("usuarios").document(usuario.getUid()).collection("prendas")
                .limit(50);
        FirestoreRecyclerOptions<Prenda> options = new FirestoreRecyclerOptions
                .Builder<Prenda>().setQuery(query, Prenda.class).build();
        adaptador = new AdaptadorPrendasFirestoreUI(options, this);
        binding.recyclerView.setAdapter(adaptador);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setHasFixedSize(true);

        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!enabledMenu) {
                    int pos = binding.recyclerView.getChildAdapterPosition(v);
                    abrirPaginaPrenda(options.getSnapshots().get(pos));
                }
            }
        });
        adaptador.startListening();
    }

    public void abrirPaginaPrenda(Prenda prenda) {
        Intent i = new Intent(this, PaginaPrenda.class);
        i.putExtra("prenda", prenda);
        this.startActivity(i);
    }

    //funcion que aplica o lo quita segun si "filtroactivo" contiene el texto del filtro activado o no,
    // si lo tiene, desactiva el filtro y muestra todas las prendas
    public void aplicarFiltro(String tipo) {
        if (tipo.equals(filtroActivo)) {
            Query query = FirebaseFirestore.getInstance()
                    .collection("usuarios").document(usuario.getUid()).collection("prendas") //"Prueba para comprobar que funciona"
                    .limit(50);
            FirestoreRecyclerOptions<Prenda> options = new FirestoreRecyclerOptions
                    .Builder<Prenda>().setQuery(query, Prenda.class).build();
            adaptador = new AdaptadorPrendasFirestoreUI(options, this);
            binding.recyclerView.setAdapter(adaptador);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerView.setHasFixedSize(true);

            adaptador.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = binding.recyclerView.getChildAdapterPosition(v);
                    abrirPaginaPrenda(options.getSnapshots().get(pos));
                }
            });
            adaptador.startListening();
            filtroActivo = "ninguno";
        } else {
            Query query = FirebaseFirestore.getInstance()
                    .collection("usuarios").document(usuario.getUid()).collection("prendas") //"Prueba para comprobar que funciona"
                    .limit(50).whereEqualTo("tipo", tipo);
            FirestoreRecyclerOptions<Prenda> options = new FirestoreRecyclerOptions
                    .Builder<Prenda>().setQuery(query, Prenda.class).build();
            adaptador = new AdaptadorPrendasFirestoreUI(options, this);
            binding.recyclerView.setAdapter(adaptador);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerView.setHasFixedSize(true);

            adaptador.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = binding.recyclerView.getChildAdapterPosition(v);
                    abrirPaginaPrenda(options.getSnapshots().get(pos));
                }
            });
            adaptador.startListening();
            filtroActivo = tipo;
        }
    }

    public void aplicarFiltro2(boolean tipo2) {
        if (tipo2 == filtroActivo2) {
            Query query = FirebaseFirestore.getInstance()
                    .collection("usuarios").document(usuario.getUid()).collection("prendas") //"Prueba para comprobar que funciona"
                    .limit(50);
            FirestoreRecyclerOptions<Prenda> options = new FirestoreRecyclerOptions
                    .Builder<Prenda>().setQuery(query, Prenda.class).build();
            adaptador = new AdaptadorPrendasFirestoreUI(options, this);
            binding.recyclerView.setAdapter(adaptador);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerView.setHasFixedSize(true);

            adaptador.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = binding.recyclerView.getChildAdapterPosition(v);
                    abrirPaginaPrenda(options.getSnapshots().get(pos));
                }
            });
            adaptador.startListening();
            filtroActivo2 = false;
        } else {
            Query query = FirebaseFirestore.getInstance()
                    .collection("usuarios").document(usuario.getUid()).collection("prendas") //"Prueba para comprobar que funciona"
                    .limit(50).whereEqualTo("favoritos", tipo2);
            FirestoreRecyclerOptions<Prenda> options = new FirestoreRecyclerOptions
                    .Builder<Prenda>().setQuery(query, Prenda.class).build();
            adaptador = new AdaptadorPrendasFirestoreUI(options, this);
            binding.recyclerView.setAdapter(adaptador);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerView.setHasFixedSize(true);

            adaptador.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = binding.recyclerView.getChildAdapterPosition(v);
                    abrirPaginaPrenda(options.getSnapshots().get(pos));
                }
            });
            adaptador.startListening();
            filtroActivo2 = tipo2;
        }
    }

    public void mostrarConjuntos(View v) {
        CamisetaImage.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        PantalonImage.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        AccesorioImage.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        FavoritosImage.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        activoParteDeArriba = false;
        activoParteDeAbajo = false;
        activoAccesorios = false;
        activoFavoritos = false;
        if(filtroConjuntoActivo){
            ConjuntosImage.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            filtroConjuntoActivo = false;
            Query query = FirebaseFirestore.getInstance()
                    .collection("usuarios").document(usuario.getUid()).collection("prendas") //"Prueba para comprobar que funciona"
                    .limit(50);
            FirestoreRecyclerOptions<Prenda> options = new FirestoreRecyclerOptions
                    .Builder<Prenda>().setQuery(query, Prenda.class).build();
            adaptador = new AdaptadorPrendasFirestoreUI(options, this);
            binding.recyclerView.setAdapter(adaptador);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerView.setHasFixedSize(true);
            adaptador.startListening();
        }else{
            ConjuntosImage.getBackground().setColorFilter(Color.rgb(181, 162, 138), PorterDuff.Mode.SRC_IN);
            filtroConjuntoActivo = true;
            Query query = FirebaseFirestore.getInstance()
                    .collection("usuarios").document(usuario.getUid()).collection("conjuntos")
                    .limit(50);
            FirestoreRecyclerOptions<Conjunto> options = new FirestoreRecyclerOptions
                    .Builder<Conjunto>().setQuery(query, Conjunto.class).build();
            adaptadorConjuntos = new AdaptadorConjuntosFirestoreUI(options, this);
            binding.recyclerView.setAdapter(adaptadorConjuntos);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerView.setHasFixedSize(true);
            adaptadorConjuntos.startListening();
        }
    }

    // --------------------------------------------------
    public void funcionAbrirMenuLateral(View view) {
        //procedemos a desplegar el menú porque está escondido
        menuLateral.animate().translationX(0).setDuration(700);
        menuInferior.animate().alpha(0).setDuration(500).setInterpolator(new AccelerateInterpolator()).start();
        menuIcono.animate().alpha(0).setDuration(500).setInterpolator(new AccelerateInterpolator()).start();
        blackBackground.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
        recycler.animate().translationX(-160).setDuration(700);
        enabledMenu = true;
        iconoLanding.setEnabled(false);
        iconoPaginaUser.setEnabled(false);
        iconoAnyadirPrendaMenuInferior.setEnabled(false);
        settings.setEnabled(false);
    }

    public void funcionCerrarMenuLateral() {
        menuLateral.animate().translationX(300).setDuration(500);
        menuInferior.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
        menuIcono.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
        blackBackground.animate().alpha(0).setDuration(500).setInterpolator(new AccelerateInterpolator()).start();
        recycler.animate().translationX(0).setDuration(400);
        enabledMenu = false;
        iconoLanding.setEnabled(true);
        iconoPaginaUser.setEnabled(true);
        iconoAnyadirPrendaMenuInferior.setEnabled(true);
        settings.setEnabled(true);
    }

    // --------------------------------------------------
    // funciones para colocar en el xml para el filtro lateral
    public void filtroParteDeArriba(View view) {
        PantalonImage.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        AccesorioImage.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        FavoritosImage.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        ConjuntosImage.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        activoParteDeAbajo = false;
        activoAccesorios = false;
        activoFavoritos = false;
        filtroConjuntoActivo = false;
        if (activoParteDeArriba) {
            CamisetaImage.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            activoParteDeArriba = false;
        } else {
            CamisetaImage.getBackground().setColorFilter(Color.rgb(181, 162, 138), PorterDuff.Mode.SRC_IN);
            activoParteDeArriba = true;
        }
        aplicarFiltro("parte de arriba");
    }

    public void filtroParteDeAbajo(View view) {
        CamisetaImage.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        AccesorioImage.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        FavoritosImage.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        ConjuntosImage.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        activoParteDeArriba = false;
        activoAccesorios = false;
        activoFavoritos = false;
        filtroConjuntoActivo = false;
        if (activoParteDeAbajo){
            PantalonImage.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            activoParteDeAbajo = false;
        } else{
            PantalonImage.getBackground().setColorFilter(Color.rgb(181, 162, 138), PorterDuff.Mode.SRC_IN);
            activoParteDeAbajo = true;
        }
        aplicarFiltro("parte de abajo");
    }

    public void filtroAccesorios(View view) {
        CamisetaImage.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        PantalonImage.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        FavoritosImage.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        ConjuntosImage.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        activoParteDeArriba = false;
        activoParteDeAbajo = false;
        activoFavoritos = false;
        filtroConjuntoActivo = false;
        if (activoAccesorios){
            AccesorioImage.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            activoAccesorios = false;
        } else {
            AccesorioImage.getBackground().setColorFilter(Color.rgb(181, 162, 138), PorterDuff.Mode.SRC_IN);
            activoAccesorios = true;
        }
        aplicarFiltro("accesorios");
    }

    public void filtroFavoritos(View view) {
        CamisetaImage.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        PantalonImage.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        AccesorioImage.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        ConjuntosImage.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        activoParteDeArriba = false;
        activoParteDeAbajo = false;
        activoAccesorios = false;
        filtroConjuntoActivo = false;
        if (activoFavoritos){
            FavoritosImage.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            activoFavoritos = false;
        } else {
            FavoritosImage.getBackground().setColorFilter(Color.rgb(181, 162, 138), PorterDuff.Mode.SRC_IN);
            activoFavoritos = true;
        }
        aplicarFiltro2(true);
    }

    // Funciones que nos hacen falta para el menú inferior
    public void abrirLandingPage(View view) {
        Intent i = new Intent(this, LandingPage.class);
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

    public void abrirSettingsArmario(View view) {
        // esto aparece
        iconoBombilla.animate().alpha(1).setDuration(1200).setInterpolator(new DecelerateInterpolator()).start();
        cerrarSettings.animate().alpha(1).setDuration(1200).setInterpolator(new DecelerateInterpolator()).start();
        indicadorBombillaEncendida.animate().alpha(1).setDuration(1200).setInterpolator(new DecelerateInterpolator()).start();
        // queremos que bajen las otras cosas
        settings.animate().translationY(400).setDuration(500);
        btnIconoTodasLasPrendas.animate().translationY(400).setDuration(540);
        lineaMarcadorTodasPrendas.animate().translationY(400).setDuration(530);
        iconoAnyadirPrendaMenuInferior.animate().translationY(400).setDuration(580);
        iconoLanding.animate().translationY(400).setDuration(620);
        //lineaMarcaLanding.animate().translationY(400).setDuration(660);
        iconoPaginaUser.animate().translationY(400).setDuration(700);
        // el menú está desplegado
    }

    public void cerrarSettingsArmario(View view) {
        iconoBombilla.animate().alpha(0).setDuration(550).setInterpolator(new DecelerateInterpolator()).start();
        cerrarSettings.animate().alpha(0).setDuration(550).setInterpolator(new DecelerateInterpolator()).start();
        indicadorBombillaEncendida.animate().alpha(0).setDuration(550).setInterpolator(new DecelerateInterpolator()).start();
        // queremos que bajen las otras cosas
        settings.animate().translationY(0).setDuration(600);
        btnIconoTodasLasPrendas.animate().translationY(0).setDuration(560);
        lineaMarcadorTodasPrendas.animate().translationY(0).setDuration(550);
        iconoAnyadirPrendaMenuInferior.animate().translationY(0).setDuration(520);
        iconoLanding.animate().translationY(0).setDuration(480);
        //lineaMarcaLanding.animate().translationY(0).setDuration(440);
        iconoPaginaUser.animate().translationY(0).setDuration(400);
        // el menú pasa a estar plegado
    }
    @Override
    public void onBackPressed() {
        funcionCerrarMenuLateral();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adaptador.stopListening();
    }

    // funciones del MQTT
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
                                HashMap<String, Object> prendaMap = (HashMap<String, Object>) doc.getData();
                                prendaAuxiliar = new Prenda(prendaMap);
                                abrirPaginaPrendaMqtt(prendaAuxiliar);
                            }
                        }
                        // si no se ha encontrado ninguna prenda se abre una pagina nueva
                        else {
                            if(idPrendaDetectada.length() > 5) {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle(getResources().getString(R.string.Rfid_titulo))
                                        .setMessage(getResources().getString(R.string.Rfid_mensaje))
                                        .setPositiveButton(getResources().getString(R.string.Rfid_continuar), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                abrirPaginaNuevaMqtt(idPrendaDetectada);
                                            }})
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
                            noTienesPrenda.setAlpha(1.0f);

                        } else {
                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                noTienesPrenda.setAlpha(0.0f);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure (@NonNull Exception e){
                        Log.e("Firestore", "Error al obtener prendas", e);
                    }
                });
    }

}