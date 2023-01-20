package com.example.grupo1_1.smartclosset.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.grupo1_1.smartclosset.R;
import com.example.grupo1_1.smartclosset.databinding.ActivityLandingDemoBinding;
import com.example.grupo1_1.smartclosset.pojos.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LandingDemo extends AppCompatActivity {


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
    Usuario user = new Usuario(usuario);

    // imagenes negras
    ImageView negroTemperatura;
    ImageView negroCalendario;
    ImageView negroMas;
    ImageView negroBombilla;
    ImageView negroArmario;
    ImageView negroEmpezar;

    // circulitos
    ImageView circuloMas;
    ImageView circuloBombilla;
    ImageView circuloArmario;

    ImageView iconoCalendario;

    // textos
    TextView textoTemp;
    TextView textoCalendario1;
    TextView textoCalendario2;
    TextView textoMas;
    TextView textoBombilla;
    TextView textoArmario1;
    TextView textoArmario2;
    TextView textoEmpezar;


    private ActivityLandingDemoBinding binding;

    // linea que hay bajo la casa en el menú inferior de la landing
    ImageView lineaMarcaLanding;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //función para quitar la Action Bar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = ActivityLandingDemoBinding.inflate(getLayoutInflater()); //F
        setContentView(binding.getRoot()); //F

        // -------------------------------------------------------------------------------------
        // Hacemos que la línea que marca la landing solo se muestre en esta página
        // Inicialmente -> la línea tendrá la opacidad al cero
       // lineaMarcaLanding = findViewById(R.id.lineaMenuLanding);
//        lineaMarcaLanding.setAlpha(0.7f);
        // -------------------------------------------------------------------------------------


        negroTemperatura = findViewById(R.id.demoTemp);
        negroCalendario = findViewById(R.id.demoCalend);
        negroMas = findViewById(R.id.demoMas);
        negroBombilla = findViewById(R.id.demoBombilla);
        negroArmario = findViewById(R.id.demoArmario);
        negroEmpezar = findViewById(R.id.negroDemoEmpezar);

        circuloMas = findViewById(R.id.circulo_mas_demo);
        circuloBombilla = findViewById(R.id.circulo_mas_bombilla);
        circuloArmario = findViewById(R.id.circulo_mas_armario);

        iconoCalendario = findViewById(R.id.icono_calendario_landing);

        iconoCalendario.setElevation(3);

        textoTemp = findViewById(R.id.textoDemoTemp);
        textoCalendario1 = findViewById(R.id.textoDemoCalendario1);
        textoCalendario2 = findViewById(R.id.textoDemoCalendario2);
        textoMas = findViewById(R.id.textoDemoMas);
        textoBombilla = findViewById(R.id.textoDemoLuces);

        textoEmpezar = findViewById(R.id.textoDemoEmpezar);


        // iconoMenu.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
        //   iconoMenu.animate().alpha(0).setDuration(500).setInterpolator(new AccelerateInterpolator()).start();

        Handler s1 = new Handler();
        s1.postDelayed(new Runnable() {
            @Override
            public void run() {
                negroTemperatura.animate().alpha(0.8f).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
                textoTemp.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
            }
        }, 400);

        Handler s2 = new Handler();
        s2.postDelayed(new Runnable() {
            @Override
            public void run() {
                textoTemp.animate().alpha(0).setDuration(200).setInterpolator(new AccelerateInterpolator()).start();
                iconoCalendario.setElevation(68);
                negroCalendario.setElevation(67);
                negroTemperatura.animate().alpha(0).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
                textoCalendario1.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
                negroCalendario.animate().alpha(0.8f).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
            }
        }, 4500);

        Handler s3 = new Handler();
        s3.postDelayed(new Runnable() {
            @Override
            public void run() {
                textoCalendario2.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
                textoCalendario1.animate().alpha(0).setDuration(200).setInterpolator(new AccelerateInterpolator()).start();
                textoTemp.animate().alpha(0).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
            }
        }, 9500);


        Handler s4 = new Handler();
        s4.postDelayed(new Runnable() {
            @Override
            public void run() {
                textoCalendario2.animate().alpha(0).setDuration(200).setInterpolator(new AccelerateInterpolator()).start();
                negroCalendario.animate().alpha(0).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
                negroMas.animate().alpha(0.8f).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
                circuloMas.animate().alpha(0.8f).setDuration(900).setInterpolator(new DecelerateInterpolator()).start();
                textoMas.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
            }
        }, 14700);

        Handler s5 = new Handler();
        s5.postDelayed(new Runnable() {
            @Override
            public void run() {
                textoMas.animate().alpha(0).setDuration(200).setInterpolator(new AccelerateInterpolator()).start();
                negroMas.animate().alpha(0).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
                circuloMas.animate().alpha(0).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
                negroBombilla.animate().alpha(0.8f).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
                circuloBombilla.animate().alpha(0.8f).setDuration(900).setInterpolator(new DecelerateInterpolator()).start();
                textoBombilla.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();

            }
        }, 19900);

        Handler s6 = new Handler();
        s6.postDelayed(new Runnable() {
            @Override
            public void run() {
                textoBombilla.animate().alpha(0).setDuration(200).setInterpolator(new AccelerateInterpolator()).start();
                negroBombilla.animate().alpha(0).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
                circuloBombilla.animate().alpha(0).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
                negroEmpezar.animate().alpha(0.9f).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
                textoEmpezar.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
            }
        }, 24400);

        Handler s7 = new Handler();
        s7.postDelayed(new Runnable() {
            @Override
            public void run() {
                textoEmpezar.animate().alpha(0).setDuration(200).setInterpolator(new AccelerateInterpolator()).start();
                negroEmpezar.animate().alpha(0).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();

            }
        }, 32400);

        Handler s8 = new Handler();
        s8.postDelayed(new Runnable() {
            @Override
            public void run() {
                abrirLandingPage();
            }
        }, 33400);


        // aquí añadimos el nombre customizado del usuario
        TextView nombre = findViewById(R.id.nombreUsuarioLanding);
        nombre.setText(user.getNombre());

    }

    public void abrirLandingPage() {
        Intent i = new Intent(this, LandingPage.class);
        this.startActivity(i);
    }



}

