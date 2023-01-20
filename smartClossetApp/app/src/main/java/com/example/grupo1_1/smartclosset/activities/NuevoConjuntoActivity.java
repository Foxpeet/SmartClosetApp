package com.example.grupo1_1.smartclosset.activities;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.grupo1_1.smartclosset.R;
import com.example.grupo1_1.smartclosset.adaptadores.AdaptadorPrendasFirestoreUINuevoConjunto;
import com.example.grupo1_1.smartclosset.databinding.CrearConjuntoBinding;
import com.example.grupo1_1.smartclosset.pojos.Conjunto;
import com.example.grupo1_1.smartclosset.pojos.Prenda;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class NuevoConjuntoActivity extends AppCompatActivity {

    private CrearConjuntoBinding binding;
    public StorageReference storageRef;
    public StorageReference ficheroRef;
    public AdaptadorPrendasFirestoreUINuevoConjunto adaptador;
    FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

    public File localFileParteArriba;
    public File localFileParteAbajo;

    Conjunto conjuntoNuevo = new Conjunto();

    ImageView fotoParteArriba;
    ImageView fotoParteAbajo;

    public Prenda prenda;

    public String idParteArriba = "";
    public String idParteAbajo = "";

    // -------------------------------------
    // declaro los elementos para la animaicón de la página
    ImageView rectanguloBase;
    ImageView img1;
    ImageView img2;
    ImageView barraSeparadora;
    ImageView btnBottom;
    ImageView btnTop;
    RecyclerView recycler;
    ImageView base;
    Button boton;

    TextView nombrePrendaArriba;
    TextView nombrePrendaAbajo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //función para quitar la Action Bar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        binding = CrearConjuntoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storageRef = FirebaseStorage.getInstance().getReference();

        boton = findViewById(R.id.guardarConjunto);

        fotoParteArriba = binding.fotoConjunto2;
        fotoParteAbajo = binding.fotoConjunto;
        nombrePrendaArriba = binding.textViewParteArriba;
        nombrePrendaAbajo = binding.textViewParteAbajo;

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!idParteArriba.isEmpty() && !idParteAbajo.isEmpty()){
                    conjuntoNuevo.setParteArribaId(idParteArriba);
                    conjuntoNuevo.setParteAbajoId(idParteAbajo);

                    Conjunto.crearConjunto(conjuntoNuevo, usuario.getUid());

                    Toast.makeText(NuevoConjuntoActivity.this, getResources().getString(R.string.CrearConjunto_exito), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(NuevoConjuntoActivity.this, MainActivity.class);
                    startActivity(intent);
                }else if(idParteAbajo.isEmpty()){
                    Toast.makeText(NuevoConjuntoActivity.this, getResources().getString(R.string.CrearConjunto_error_inferior), Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(NuevoConjuntoActivity.this, getResources().getString(R.string.CrearConjunto_error_superior), Toast.LENGTH_SHORT).show();
                }
            }
        });

        try {
            localFileParteArriba = File.createTempFile("image", "jpg");
            localFileParteAbajo = File.createTempFile("image", "jpg");
        } catch (IOException e) {
            e.printStackTrace(); //Si hay problemas detenemos la app
        }

        // -------------------------------------
        // damos valores a los elementos para la animación
        rectanguloBase = findViewById(R.id.rectanguloConjunto);
        img1 = findViewById(R.id.fotoConjunto);
        img2 = findViewById(R.id.fotoConjunto2);
        barraSeparadora = findViewById(R.id.barraConjunto);
        btnBottom = findViewById(R.id.anyadirConjunto);
        btnTop = findViewById(R.id.anyadirConjunto2);
        recycler = findViewById(R.id.recyclerView);
        base = findViewById(R.id.rectanguloCircularConjunto);

        // inicialmente solo se tienen que ver centrados los elementos centrales
        // quitamos opacidad a lo que no queremos
        base.setAlpha(0.0f);
        recycler.setAlpha(0.0f);
        boton.setAlpha(0.0f);

        // bajamos las cosas invisibles para que luego suban
        base.setTranslationY(700);
        recycler.setTranslationY(700);
        boton.setTranslationY(700);

        // bajamos y escalamos el resto
        rectanguloBase.animate().scaleX(1.15F).scaleY(1.15F).translationY(120);
        img1.animate().scaleX(1.15F).scaleY(1.15F).translationY(120);
        img2.animate().scaleX(1.15F).scaleY(1.15F).translationY(120);
        nombrePrendaArriba.animate().scaleX(1.15F).scaleY(1.15F).translationY(120);
        nombrePrendaAbajo.animate().scaleX(1.15F).scaleY(1.15F).translationY(120);
        barraSeparadora.animate().scaleX(1.15F).scaleY(1.15F).translationY(120);
        btnBottom.animate().scaleX(1.15F).scaleY(1.15F).translationY(120);
        btnTop.animate().scaleX(1.15F).scaleY(1.15F).translationY(120);
    }

    // -------------------------------------
    // animación página crear conjuntos
    public void producirAnimacion() {
        rectanguloBase.animate().scaleX(1).scaleY(1).translationY(0).setDuration(1000);
        img1.animate().scaleX(1).scaleY(1).translationY(0).setDuration(1000);
        img2.animate().scaleX(1).scaleY(1).translationY(0).setDuration(1000);
        nombrePrendaArriba.animate().scaleX(1).scaleY(1).translationY(0).setDuration(1000);
        nombrePrendaAbajo.animate().scaleX(1).scaleY(1).translationY(0).setDuration(1000);
        barraSeparadora.animate().scaleX(1).scaleY(1).translationY(0).setDuration(1000);
        btnBottom.animate().scaleX(1).scaleY(1).translationY(0).setDuration(1000);
        btnTop.animate().scaleX(1).scaleY(1).translationY(0).setDuration(1000);
        base.setAlpha(0.4f);
        recycler.setAlpha(1.0f);
        boton.setAlpha(1.0f);
        base.animate().translationY(0).setDuration(1000);
        recycler.animate().translationY(0).setDuration(1000);
        boton.animate().translationY(0).setDuration(1000);
    }

    public void seleccionarParteArriba(View v) {
        producirAnimacion();
        seleccionarPrendaConFiltro("parte de arriba");
    }

    public void seleccionarParteAbajo(View v) {
        producirAnimacion();
        seleccionarPrendaConFiltro("parte de abajo");
    }

    public void cambiaFotoPrendaSeleccionada(Prenda prenda1) {
        if (prenda1.getTipo().equals("parte de arriba")) {
            nombrePrendaArriba.setVisibility(View.VISIBLE);
            nombrePrendaArriba.setText(prenda1.getNombrePrenda());
            idParteArriba = prenda1.getDocId();
            ficheroRef = storageRef.child(prenda1.getUrlFoto());
            ficheroRef.getFile(localFileParteArriba)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Log.d("Almacenamiento", "Fichero bajado");
                            fotoParteArriba.setImageBitmap(BitmapFactory.decodeFile(localFileParteArriba.getAbsolutePath()));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.e("Almacenamiento", "ERROR: bajando fichero");
                        }
                    });
        }
        if (prenda1.getTipo().equals("parte de abajo")) {
            nombrePrendaAbajo.setVisibility(View.VISIBLE);
            nombrePrendaAbajo.setText(prenda1.getNombrePrenda());
            idParteAbajo = prenda1.getDocId();
            ficheroRef = storageRef.child(prenda1.getUrlFoto());
            ficheroRef.getFile(localFileParteAbajo)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Log.d("Almacenamiento", "Fichero bajado");
                            fotoParteAbajo.setImageBitmap(BitmapFactory.decodeFile(localFileParteAbajo.getAbsolutePath()));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.e("Almacenamiento", "ERROR: bajando fichero");
                        }
                    });
        }
    }

    public void seleccionarPrendaConFiltro(String tipo) {
        Query query = FirebaseFirestore.getInstance()
                .collection("usuarios").document(usuario.getUid()).collection("prendas")
                .limit(50).whereEqualTo("tipo", tipo);
        FirestoreRecyclerOptions<Prenda> options = new FirestoreRecyclerOptions
                .Builder<Prenda>().setQuery(query, Prenda.class).build();
        adaptador = new AdaptadorPrendasFirestoreUINuevoConjunto(options, this);
        binding.recyclerView.setAdapter(adaptador);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerView.setHasFixedSize(true);

        binding.recyclerView.setVisibility(View.VISIBLE);

        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = binding.recyclerView.getChildAdapterPosition(v);
                cambiaFotoPrendaSeleccionada(options.getSnapshots().get(pos));
                binding.recyclerView.setVisibility(View.GONE);
            }
        });
        adaptador.startListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}