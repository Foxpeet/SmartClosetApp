package com.example.grupo1_1.smartclosset.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.grupo1_1.smartclosset.R;
import com.example.grupo1_1.smartclosset.pojos.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;



public class CrearCuenta extends AppCompatActivity {

    Button registrarbtn;
    EditText email;
    EditText password;
    EditText confirmarPassword;
    ImageView ojoPassword;
    ImageView ojoPassword2;
    boolean passwordShowing;
    boolean confirmarPasswordShowing;

    TextView err;
    TextView err2;
    TextView err3;
    TextView err4;
    TextView err5;

    public FirebaseUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = FirebaseAuth.getInstance().getCurrentUser();

        //función para quitar la Action Bar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        //hacemos que la actividad ocupe absolutamente toda la pantalla
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_crear_cuenta);

        registrarbtn = findViewById(R.id.button3);
        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        confirmarPassword = findViewById(R.id.editTextTextPassword3);
        ojoPassword = findViewById(R.id.iconoOjoPassword);
        ojoPassword2 = findViewById(R.id.iconoOjoPassword2);
        passwordShowing = false;
        confirmarPasswordShowing = false;

        err= findViewById(R.id.textoErr2);
        err.setVisibility(View.INVISIBLE);

        err2= findViewById(R.id.textoErr3);
        err2.setVisibility(View.INVISIBLE);

        err3= findViewById(R.id.textoErr4);
        err3.setVisibility(View.INVISIBLE);

        err4= findViewById(R.id.textoErr5);
        err4.setVisibility(View.INVISIBLE);

        err5 = findViewById(R.id.textoErr);
        err5.setVisibility(View.INVISIBLE);

        registrarbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarUsuario(view);
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

        ojoPassword2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(confirmarPasswordShowing){
                    confirmarPassword.setTransformationMethod(new PasswordTransformationMethod());
                    confirmarPasswordShowing = false;
                    ojoPassword2.setImageDrawable(getResources().getDrawable(R.drawable.icono_ojo_hide));
                } else{
                    confirmarPassword.setTransformationMethod(null);
                    confirmarPasswordShowing = true;
                    ojoPassword2.setImageDrawable(getResources().getDrawable(R.drawable.icono_ojo_show));
                }
            }
        });
    }

    //función que ocurre al hacer click en el botón de registrarse
    public void registrarUsuario(View view) {
        FirebaseAuth autentificacion = FirebaseAuth.getInstance();
        confirmarPassword.setVisibility(View.VISIBLE);
        if (!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
            err.setVisibility(View.INVISIBLE);
                if(confirmarPassword.getText().toString().equals(password.getText().toString())) {
                    err2.setVisibility(View.INVISIBLE);
                    if (user == null) {
                        err3.setVisibility(View.INVISIBLE);
                        autentificacion.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    err4.setVisibility(View.INVISIBLE);
                                    user = FirebaseAuth.getInstance().getCurrentUser();
                                    Usuario usuario = new Usuario(user);
                                    Usuario.crearUsuario(usuario, user.getUid());
                                    iniciarPrimeraActividad();
                                }
                                else {
                                    err4.setVisibility(View.VISIBLE);
                                    err3.setVisibility(View.INVISIBLE);
                                    err.setVisibility(View.INVISIBLE);
                                    err2.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    }
                    else {
                        err3.setVisibility(View.VISIBLE);
                        err.setVisibility(View.INVISIBLE);
                        err2.setVisibility(View.INVISIBLE);
                        err4.setVisibility(View.INVISIBLE);
                    }
                }
                else {
                    err2.setVisibility(View.VISIBLE);
                    err.setVisibility(View.INVISIBLE);
                    err3.setVisibility(View.INVISIBLE);
                    err4.setVisibility(View.INVISIBLE);
                }
        }
        else {
            err.setVisibility(View.VISIBLE);
            err2.setVisibility(View.INVISIBLE);
            err3.setVisibility(View.INVISIBLE);
            err4.setVisibility(View.INVISIBLE);
        }
    }

    public void iniciarPrimeraActividad() {
        Intent i = new Intent(this, AnimacionActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, InicioSesion.class);
        this.startActivity(i);
    }
}
