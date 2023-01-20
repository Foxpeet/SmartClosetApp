package com.example.grupo1_1.smartclosset.activities;

import static com.example.grupo1_1.comun.MQTT.TAG;
import static com.example.grupo1_1.comun.MQTT.broker;
import static com.example.grupo1_1.comun.MQTT.clientId;
import static com.example.grupo1_1.comun.MQTT.qos;
import static com.example.grupo1_1.comun.MQTT.topicRoot;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.grupo1_1.smartclosset.MainActivityRFIDPRUEBA;
import com.example.grupo1_1.smartclosset.R;
import com.example.grupo1_1.smartclosset.pojos.Prenda;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class PerfilUsuarioActivity extends AppCompatActivity implements MqttCallback {


    static MqttClient client;
    FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
    String idDetectado = null;
    Prenda prendaAuxiliar;

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
    ImageView lineaMarcaPaginaUser;
    ImageView btn1;
    ImageView btn2;
    ImageView btn3;
    ImageView btn4;
    ImageView btn5;
    ImageView ajustes;

    Uri uriData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_perfil);

        conectarMqtt();
        // en el topic pongo lo de la pagina
        suscribirMqtt("rfid", this);

        storageRef = FirebaseStorage.getInstance().getReference();
        if(getIntent().getStringExtra("fotoData") != null){
            uriData = Uri.parse(getIntent().getStringExtra("fotoData"));
        }

        cargarDatos();

        iconoBombilla = findViewById(R.id.btnIconoBombilla);
        settings = findViewById(R.id.btnIconoSettings);
        cerrarSettings = findViewById(R.id.btnIconoSettingsArmarioCerrar);
        indicadorBombillaEncendida = findViewById(R.id.bombillaEncendida);
        indicadorBombillaEncendida = findViewById(R.id.bombillaEncendida);
        lineaMarcaPaginaUser = findViewById(R.id.imageView11);

        ajustes = findViewById(R.id.ajustesPerfil);

        //declaramos los botones del menú desplegable animado
        btn1 = findViewById(R.id.btnCerrarMenuPerfil);
        btn1.setVisibility(View.INVISIBLE);
        btn2 = findViewById(R.id.btnEditarPerfil);
        btn2.setVisibility(View.INVISIBLE);
        btn3 = findViewById(R.id.btncambiarIdiomas);
        btn3.setVisibility(View.INVISIBLE);
        btn4 = findViewById(R.id.btnAcercaDePerfil);
        btn4.setVisibility(View.INVISIBLE);
        btn5 = findViewById(R.id.btnCerrarSesion);
        btn5.setVisibility(View.INVISIBLE);

        // inicialmente tienen que estar transparentes
        cerrarSettings.setAlpha(0.0f);
        indicadorBombillaEncendida.setAlpha(0.0f);
        iconoBombilla.setAlpha(0.0f);

        lineaMarcaPaginaUser.setAlpha(1.0f);

        iconoLanding = findViewById(R.id.imageView6);
        iconoLanding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirLandingPage(view);
            }
        });
        iconoPaginaUser = findViewById(R.id.lineaMenuPerfil);

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

        cerrarMenuPerfil();
    }

    public void cargarDatos() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        TextView nuevoNombre = findViewById(R.id.nombrePerfil);

        TextView nuevaTalla = findViewById(R.id.tallaPerfil);
        TextView correo = findViewById(R.id.correoPerfil);

        db.collection("usuarios").document(usuario.getUid()).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        nuevoNombre.setText(task.getResult().getString("nombre"));
                        nuevaTalla.setText(task.getResult().getString("talla"));
                        correo.setText(task.getResult().getString("correo"));
                    }
                }
        );
        descargarYMostrarFotoPerfil();
    }

    //función para producir la animación y desplegar el menú
    public void abrirMenuPerfil(View view) {
        ajustes.animate().setDuration(100);
        btn1.setVisibility(View.VISIBLE);
        btn2.setVisibility(View.VISIBLE);
        btn3.setVisibility(View.VISIBLE);
        btn4.setVisibility(View.VISIBLE);
        btn5.setVisibility(View.VISIBLE);
        btn1.animate().alpha(1).rotation(90).scaleX(2).scaleY(2).setDuration(100);
        ajustes.setVisibility(View.INVISIBLE);
        btn1.getBackground().setAlpha(0);
        btn2.animate().alpha(1).translationX(0).translationY(200).scaleX(2).scaleY(2).setDuration(400);
        btn3.animate().alpha(1).translationX(0).translationY(400).scaleX(2).scaleY(2).setDuration(400);
        btn4.animate().alpha(1).translationX(0).translationY(600).scaleX(2).scaleY(2).setDuration(400);
        btn5.animate().alpha(1).translationX(0).translationY(600).scaleX(2).scaleY(2).setDuration(400);
        btn1.setEnabled(true);
        btn2.setEnabled(true);
        btn3.setEnabled(true);
        btn4.setEnabled(true);
        btn5.setEnabled(true);
        // cerramos el menú con el btn1 solo cuando está desplegado
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cerrarMenuPerfil();
            }
        });
        // abrimos editar una prenda con el btn2 solo cuando está desplegado
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirEditarPerfil();
            }
        });
        // borramos una prenda con el btn3 solo cuando está desplegado
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirIdiomas();
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirAcercaDe();
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(PerfilUsuarioActivity.this)
                        .setTitle(getResources().getString(R.string.Perfil_cerrarSesion_titulo))
                        .setMessage(getResources().getString(R.string.Perfil_cerrarSesion_mensaje))
                        .setPositiveButton(getResources().getString(R.string.Perfil_cerrarSesion_cerrar), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                AuthUI.getInstance().signOut(getApplicationContext())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Intent i = new Intent(getApplicationContext(), InicioSesion.class);
                                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                        | Intent.FLAG_ACTIVITY_NEW_TASK
                                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(i);
                                                finish();
                                            }
                                        });
                            }})
                        .setNegativeButton(getResources().getString(R.string.Comunes_cancelar), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                cerrarMenuPerfil();
                            }})
                        .show();
            }
        });
    }
    public void cerrarMenuPerfil() {
        btn1.animate().alpha(0).rotation(-90).translationX(0).translationY(0).scaleX(1).scaleY(1).setDuration(400);
        btn2.animate().alpha(0).translationX(0).translationY(0).scaleX(1).scaleY(1).setDuration(400);
        btn3.animate().alpha(0).translationX(0).translationY(0).scaleX(1).scaleY(1).setDuration(400);
        btn4.animate().alpha(0).translationX(0).translationY(0).scaleX(1).scaleY(1).setDuration(400);
        btn5.animate().alpha(0).translationX(0).translationY(0).scaleX(1).scaleY(1).setDuration(400);
        btn1.setEnabled(false);
        btn2.setEnabled(false);
        btn3.setEnabled(false);
        btn4.setEnabled(false);
        btn5.setEnabled(false);
        ajustes.animate().setDuration(100);
        ajustes.setVisibility(View.VISIBLE);
    }

    // Para mostrar la foto del usuario -------------------------------------------------------------------------------------------
    private StorageReference storageRef;

    public void descargarYMostrarFotoPerfil() {
        if(uriData != null){
            ImageView imageView = findViewById(R.id.fotoPerfil);
            imageView.setImageURI(uriData);
        } else{
            File localFile = null;
            try {
                localFile = File.createTempFile("image", "jpg"); //nombre y extensión
            } catch (IOException e) {
                e.printStackTrace(); //Si hay problemas detenemos la app
            }
            final String path = localFile.getAbsolutePath();
            Log.d("Almacenamiento", "creando fichero: " + path);

            StorageReference ficheroRef = storageRef.child("FotosUsuarios" + "/" + usuario.getUid());
            ficheroRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>(){
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot){
                            Log.d("Almacenamiento", "Fichero bajado" + path);
                            ImageView imageView = findViewById(R.id.fotoPerfil);
                            imageView.setImageBitmap(BitmapFactory.decodeFile(path));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.e("Almacenamiento", "ERROR: bajando fichero");
                        }
                    });
        }
    }
    public void abrirEditarPerfil() {
        Intent i = new Intent(this, PerfilEditarActivity.class);
        this.startActivity(i);
    }
    public void abrirIdiomas() {
        Intent i = new Intent(this, cambiarIdioma.class);
        this.startActivity(i);
    }
    public void abrirAcercaDe() {
        Intent i = new Intent(this, AcercaDeActivity.class);
        this.startActivity(i);
        cerrarMenuPerfil();
    }

    public void abrirSettingsArmario(View view) {
        // esto aparece
        iconoBombilla.animate().alpha(1).setDuration(1200).setInterpolator(new DecelerateInterpolator()).start();
        cerrarSettings.animate().alpha(1).setDuration(1200).setInterpolator(new DecelerateInterpolator()).start();
        indicadorBombillaEncendida.animate().alpha(1).setDuration(1200).setInterpolator(new DecelerateInterpolator()).start();
        // queremos que bajen las otras cosas
        settings.animate().translationY(400).setDuration(500);
        btnIconoTodasLasPrendas.animate().translationY(400).setDuration(540);
        iconoAnyadirPrendaMenuInferior.animate().translationY(400).setDuration(580);
        iconoLanding.animate().translationY(400).setDuration(620);
        //lineaMarcaLanding.animate().translationY(400).setDuration(660);
        iconoPaginaUser.animate().translationY(400).setDuration(700);
        lineaMarcaPaginaUser.animate().translationY(400).setDuration(700);
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
        //lineaMarcaLanding.animate().translationY(0).setDuration(440);
        iconoPaginaUser.animate().translationY(0).setDuration(400);
        lineaMarcaPaginaUser.animate().translationY(0).setDuration(400);
    }

    // Funciones que nos hacen falta para el menú inferior
    public void abrirLandingPage(View view) {
        Intent i = new Intent(this, LandingPage.class);
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
    @Override
    public void onBackPressed() {}

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
                                new AlertDialog.Builder(PerfilUsuarioActivity.this)
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
