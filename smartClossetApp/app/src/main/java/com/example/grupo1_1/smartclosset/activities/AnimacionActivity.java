package com.example.grupo1_1.smartclosset.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.grupo1_1.smartclosset.R;
import com.example.grupo1_1.smartclosset.pojos.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class AnimacionActivity extends AppCompatActivity {

    FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
    Usuario user = new Usuario(usuario);

    String sesionP;
    TextView errorTalla;
    TextView errorNombre;

    public void introducirCustomNombre() {
        EditText añadeNombre = findViewById(R.id.editTextIntNombre);

        String nuevoNombre = añadeNombre.getText().toString(); //las string de los datos

        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(nuevoNombre)
                .build(); // creamos una request de cambio de nombre con el nuevo nombre y construimos el objeto
        usuario.updateProfile(profileUpdate); //le pasamos el objeto request y el nuevo correo al "usuario" de firebase

        user.setNombre(nuevoNombre);
        Usuario.actualizarUsuario(user, usuario.getUid());
    }

    private void cerrarTeclado() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void introducirCustomTalla() {
        Spinner añadeTalla = findViewById(R.id.editTextIntTalla);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.Talla, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        String nuevaTalla = añadeTalla.getSelectedItem().toString();

        user.setTalla(nuevaTalla);
        Usuario.actualizarUsuario(user, usuario.getUid());
    }

    public void queSuba(View view, int tamaño) {
        int duracion = 1000;
        view.animate().translationY(tamaño).setDuration(duracion);
    }

    public void colocarAbajo(View view, int posicion) {
        view.setTranslationY(posicion);
    }

    public void mostrarError(TextView mensaje) {
        mensaje.setAlpha(1.0f);
        Handler s30 = new Handler();
        s30.postDelayed(new Runnable() {
            @Override
            public void run() {
                mensaje.startAnimation(fadeOut);
            }
        }, 1800);
        Handler s31 = new Handler();
        s31.postDelayed(new Runnable() {
            @Override
            public void run() {
                mensaje.setAlpha(0.0f);
            }
        }, 2800);
    }

    protected AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //función para quitar la Action Bar, hay que colocarla siempre
        //antes de declarar la activity_view
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_primera);

        TextView frase1 = findViewById(R.id.textView3);
        TextView frase2 = findViewById(R.id.textView20);
        TextView frase3 = findViewById(R.id.textViewF3);
        TextView frase4 = findViewById(R.id.textViewF4);
        TextView frase5 = findViewById(R.id.añadirNombre);
        EditText añadeNombre = findViewById(R.id.editTextIntNombre);
        Button guardarNombre = findViewById(R.id.guardarNombre);
        guardarNombre.setEnabled(false);

        errorNombre =findViewById(R.id.errorNombreAnimacion);
        errorNombre.setVisibility(View.INVISIBLE);

        errorTalla = findViewById(R.id.errorTallaAnimacion);
        errorTalla.setVisibility(View.INVISIBLE);

        TextView hola = findViewById(R.id.hola);
        //¿qué talla utilizas?
        TextView frase6 = findViewById(R.id.añadirTalla);
        Button btGuardarTalla = findViewById(R.id.guardarTalla);
        btGuardarTalla.setEnabled(false);

        Spinner añadeTalla = findViewById(R.id.editTextIntTalla);
        TextView genial = findViewById(R.id.genialTexto);
        TextView errorNombre = findViewById(R.id.textViewError);

        // inicio de la aplicación - las frases tienen que estar debajo
        queSuba(frase1, -200);
        colocarAbajo(frase2, 700);
        colocarAbajo(frase3, 700);
        colocarAbajo(frase4, 700);
        colocarAbajo(frase5, 700);
        colocarAbajo(añadeNombre, 950);
        colocarAbajo(guardarNombre, 1100);
        colocarAbajo(hola, 700);
        colocarAbajo(btGuardarTalla, 1100);

        //¿qué talla utilizas?
        colocarAbajo(frase6, 700);
        colocarAbajo(añadeTalla, 950);
        colocarAbajo(genial, 700);

        // las frases de abajo no se tienen que ver
        frase2.setAlpha(0.0f);
        frase3.setAlpha(0.0f);
        frase4.setAlpha(0.0f);
        frase5.setAlpha(0.0f);
        añadeNombre.setAlpha(0.0f);
        añadeTalla.setAlpha(0.0f);
        guardarNombre.setAlpha(0.0f);
        hola.setAlpha(0.0f);
        btGuardarTalla.setAlpha(0.0f);
        frase6.setAlpha(0.0f);
        genial.setAlpha(0.0f);
        errorNombre.setAlpha(0.0f);

        if (sesionP == "true") {
            Intent intent = new Intent(this, LandingPage.class);
            startActivity(intent);
        } else {
            // quiero que a los 3segs la frase uno se vaya
            Handler s1 = new Handler();
            s1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    queSuba(frase1, -700);
                    queSuba(frase2, -200);
                    frase2.setAlpha(1.0f);
                    frase1.startAnimation(fadeOut);
                    fadeOut.setDuration(1000);
                    fadeOut.setFillAfter(true);
                }
            }, 3000);

            // ahora quiero que se vaya la frase dos y aparezca la tres
            Handler s2 = new Handler();
            s2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    frase1.setAlpha(0.0f);
                    queSuba(frase2, -1000);
                    queSuba(frase3, -200);
                    frase3.setAlpha(1.0f);
                    frase2.startAnimation(fadeOut);
                }
            }, 7000);

            Handler s3 = new Handler();
            s3.postDelayed(new Runnable() {
                @Override
                public void run() {
                    frase2.setAlpha(0.0f);
                    queSuba(frase3, -1000);
                    queSuba(frase4, -200);
                    frase4.setAlpha(1.0f);
                    frase3.startAnimation(fadeOut);
                }
            }, 10000);

            Handler s4 = new Handler();
            // A partir de aquí esperamos a que añada el nombre y no avanza hasta que no le de a continuar.
            s4.postDelayed(new Runnable() {
                @Override
                public void run() {
                    queSuba(frase4, -570);
                    queSuba(frase5, -420);
                    queSuba(añadeNombre, -250);
                    queSuba(guardarNombre, -100);
                    frase5.setAlpha(1.0f);
                    añadeNombre.setAlpha(1.0f);
                    guardarNombre.setAlpha(1.0f);
                    guardarNombre.setEnabled(true);
                    frase3.setAlpha(0.0f);
                }
            }, 12500);

            // voy a crear una función para pasar a la parte dos de la animación
            // ahora que ya tenemos el nombre, vamos a introducir la talla
            guardarNombre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (añadeNombre.getText().toString().isEmpty()) {
                        errorNombre.setVisibility(View.VISIBLE);
                    } else {
                        errorNombre.setVisibility(View.INVISIBLE);
                        cerrarTeclado();
                        introducirCustomNombre();

                        queSuba(frase4, -1000);
                        frase4.startAnimation(fadeOut);
                        queSuba(frase5, -1000);
                        frase5.startAnimation(fadeOut);
                        queSuba(añadeNombre, -1000);
                        añadeNombre.startAnimation(fadeOut);
                        queSuba(guardarNombre, -1500);
                        guardarNombre.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
                        guardarNombre.setEnabled(false);

                        hola.setText(getResources().getString(R.string.PrimeraActivity_FraseHola_1) + user.getNombre() + "!");

                        Handler s5 = new Handler();
                        s5.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                frase5.setAlpha(0.0f);
                                añadeNombre.setAlpha(0.0f);
                                guardarNombre.setAlpha(0.0f);
                                queSuba(hola, -400);
                                hola.setAlpha(1.0f);
                                frase4.setAlpha(0.0f);
                            }
                        }, 2000);

                        Handler s6 = new Handler();
                        s6.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                queSuba(hola, -500);
                                queSuba(frase6, -370);
                                frase6.setAlpha(1.0f);
                            }
                        }, 3000);

                        Handler s7 = new Handler();
                        s7.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                queSuba(añadeTalla, -260);
                                queSuba(btGuardarTalla, -60);
                                añadeTalla.setAlpha(1.0f);
                                btGuardarTalla.setAlpha(1.0f);
                                btGuardarTalla.setEnabled(true);
                            }
                        }, 3700);
                    }
                }
            });

            btGuardarTalla.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String a = añadeTalla.getSelectedItem().toString();
                    if (a.equals("Talla")) {
                        errorTalla.setVisibility(View.VISIBLE);
                    } else {
                        errorTalla.setVisibility(View.INVISIBLE);
                        queSuba(hola, -1000);
                        hola.startAnimation(fadeOut);
                        queSuba(frase6, -1000);
                        frase6.startAnimation(fadeOut);
                        queSuba(añadeTalla, -1000);
                        añadeTalla.startAnimation(fadeOut);
                        queSuba(btGuardarTalla, -1000);
                        btGuardarTalla.startAnimation(fadeOut);
                        btGuardarTalla.setEnabled(false);

                        cerrarTeclado();
                        introducirCustomTalla();

                        Handler s8 = new Handler();
                        s8.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                btGuardarTalla.setAlpha(0.0f);
                                añadeTalla.setAlpha(0.0f);
                                frase6.setAlpha(0.0f);
                                hola.setAlpha(0.0f);

                                queSuba(genial, -300);
                                genial.setAlpha(1.0f);
                            }
                        }, 1000);

                        Handler s9 = new Handler();
                        s9.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                queSuba(genial, -1000);
                                genial.startAnimation(fadeOut);
                            }
                        }, 3200);
                        Handler s10 = new Handler();
                        s10.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                abrirLandingPage();
                            }
                        }, 4000);
                    }
                }
            });
        }
    }

    public void abrirLandingPage() {
        Intent i = new Intent(this, LandingDemo.class);
        this.startActivity(i);
    }
    @Override
    public void onBackPressed() {}
}

