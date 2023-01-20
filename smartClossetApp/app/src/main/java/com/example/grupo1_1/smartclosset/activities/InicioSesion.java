package com.example.grupo1_1.smartclosset.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.grupo1_1.smartclosset.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class InicioSesion extends AppCompatActivity {

    Button accedebtn;
    EditText email;
    EditText password;

    TextView error;
    TextView error1;

    ImageView ojoPassword;
    boolean passwordShowing;

    public FirebaseUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //idioma
        SharedPreferences sharPref = getSharedPreferences("IdiomaSmartClosset", MODE_PRIVATE);
        String idioma = sharPref.getString("IdiomaApp", "es");
        Locale locale = new Locale(idioma);
        Configuration config = new Configuration();
        config.locale =locale;
        getResources().updateConfiguration(config, null);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            iniciarLanding();
        }
        //función para quitar la Action Bar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //hacemos que la actividad ocupe absolutamente toda la pantalla
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_iniciosesion);

        accedebtn = findViewById(R.id.button3);
        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        ojoPassword = findViewById(R.id.iconoOjoPassword);
        passwordShowing = false;

        error = findViewById(R.id.errorIniciarSesion);
        error.setAlpha(0.0f);

        error1 = findViewById(R.id.error2IniciarSesion);
        error1.setAlpha(0.0f);

        accedebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accederConUsuario(view);
            }
        });

        ojoPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(passwordShowing){
                    password.setTransformationMethod(new PasswordTransformationMethod());
                    passwordShowing = false;
                    ojoPassword.setImageDrawable(getResources().getDrawable(R.drawable.icono_ojo_hide));
                } else{
                    password.setTransformationMethod(null);
                    passwordShowing = true;
                    ojoPassword.setImageDrawable(getResources().getDrawable(R.drawable.icono_ojo_show));
                }
            }
        });

        // --------------------------------------------------------------
        // A partir de aquí empieza la animación
        // --------------------------------------------------------------
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(200);
        fadeIn.setFillBefore(true);
        fadeIn.setFillAfter(true);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setDuration(1000);
        fadeOut.setFillAfter(true);

        AnimationSet animationIn = new AnimationSet(false);
        animationIn.addAnimation(fadeIn);

        AnimationSet animationOut = new AnimationSet(true);
        animationOut.addAnimation(fadeOut);

        // Declaro los elementos que faltaban
        ImageView logo = findViewById(R.id.logo_inicioSesion);
        TextView texto1 = findViewById(R.id.textView2);
        TextView texto2 = findViewById(R.id.textView3);
        TextView texto3 =findViewById(R.id.textView5);
        TextView texto4 =findViewById(R.id.textView6);
        TextView texto5 =findViewById(R.id.textView16);
        TextView texto6 =findViewById(R.id.textView17);
        TextView texto7 =findViewById(R.id.textView15);

        // lo hacemos inicialmente invisible
        logo.setAlpha(0.0f);
        email.setAlpha(0.0f);
        password.setAlpha(0.0f);
        ojoPassword.setAlpha(0.0f);
        accedebtn.setAlpha(0.0f);
        accedebtn.setEnabled(false);

        texto1.setAlpha(0.0f);
        texto2.setAlpha(0.0f);
        texto3.setAlpha(0.0f);
        texto4.setAlpha(0.0f);
        texto5.setAlpha(0.0f);
        texto6.setAlpha(0.0f);
        texto7.setAlpha(0.0f);

        // colocamos abajo los textos para poder subirlos
        texto1.setTranslationY(800);
        texto2.setTranslationY(800);

        // hacemos que el texto aparezca progresivamente
        texto1.animate().translationY(0).setDuration(1000);
        texto1.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();

        Handler s1 = new Handler();
        s1.postDelayed(new Runnable() {
            @Override
            public void run() {
                texto1.animate().alpha(0).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
                texto1.animate().translationY(-1200).setDuration(1000);

                texto2.animate().translationY(0).setDuration(1000);
                texto2.setAlpha(1.0f);
            }
        }, 2500);

        Handler s2 = new Handler();
        s2.postDelayed(new Runnable() {
            @Override
            public void run() {
                texto2.animate().translationY(-1200).setDuration(1000);
                texto2.animate().alpha(0).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
            }
        }, 6000);

        // El logo vendrá de arriba asi que vamos a subirlo primero
        logo.setTranslationY(1200);

        // Los editText y el botón vienen de abajo, vamos a bajarlos
        email.setTranslationY(1200);
        password.setTranslationY(1200);
        ojoPassword.setTranslationY(1200);
        texto3.setTranslationY(1200);
        texto4.setTranslationY(1200);
        texto5.setTranslationY(1200);
        texto6.setTranslationY(1200);
        texto7.setTranslationY(1200);

        Handler s3 = new Handler();
        s3.postDelayed(new Runnable() {
            @Override
            public void run() {
                logo.animate().alpha(1).setDuration(1500).setInterpolator(new DecelerateInterpolator()).start();
                logo.animate().translationY(0).setDuration(1000);
                texto3.animate().translationY(0).setDuration(1000);
                email.animate().translationY(0).setDuration(1000);
                password.animate().translationY(0).setDuration(1000);
                ojoPassword.animate().translationY(0).setDuration(1000);
                texto4.animate().translationY(0).setDuration(1000);
                texto5.animate().translationY(0).setDuration(1000);
                texto6.animate().translationY(0).setDuration(1000);
                texto7.animate().translationY(0).setDuration(1000);
                texto3.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
                email.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
                password.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
                ojoPassword.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
                texto4.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
                texto5.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
                texto6.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
                texto7.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
            }
        }, 6500);

        Handler s4 = new Handler();
        s4.postDelayed(new Runnable() {
            @Override
            public void run() {
                accedebtn.animate().alpha(1).setDuration(2000).setInterpolator(new DecelerateInterpolator()).start();
                accedebtn.setEnabled(true);
            }
        }, 7500);
    }

    public void accederConUsuario(View v) {
        FirebaseAuth autentificacion = FirebaseAuth.getInstance();

        if (!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
            error.setAlpha(0.0f);
            if (user == null) {
                autentificacion.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            error1.setAlpha(0.0f);
                            iniciarLanding();
                        } else {
                            error1.setAlpha(1.0f);
                        }
                    }
                });
            } else {
                Toast.makeText(InicioSesion.this, getResources().getString(R.string.InicioSesion_error_usuarioActivo), Toast.LENGTH_LONG).show();
            }
        } else {
            error.setAlpha(1.0f);
        }
    }

    public void iniciarLanding() {
        Intent i = new Intent(this, LandingPage.class);
        startActivity(i);
    }
    public void lanzarCrearCuenta(View view){
        Intent intent = new Intent(this, CrearCuenta.class);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {

    }
}
