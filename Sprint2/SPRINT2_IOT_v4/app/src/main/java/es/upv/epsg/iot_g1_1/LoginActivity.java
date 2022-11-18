package es.upv.epsg.iot_g1_1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Arrays;
import java.util.List;

import es.upv.epsg.iot_g1_1.Int1.LandingPage_int1;
import es.upv.epsg.iot_g1_1.Int2.LandingPage_int2;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;


    public void loginInt1() {
        Intent i = new Intent(this, LandingPage_int1.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
    public void loginInt2() {
        Intent i = new Intent(this, LandingPage_int2.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        login();
    }


    private void primerLogin() {
        Intent i = new Intent(this, PrimeraActividad.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
    private void login() {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        //Usuario user =  new Usuario(usuario);

        if (usuario != null) {
            //Usuario.actualizarUsuario(new Usuario(usuario), usuario.getUid());
            Toast.makeText(this, "inicia sesión: " + usuario.getDisplayName() + " - " + usuario.getEmail(), Toast.LENGTH_LONG).show();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("usuarios").document(usuario.getUid()).get()
                    .addOnCompleteListener(
                            new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task){
                                    if (task.isSuccessful()) {
                                        double interfazz = task.getResult().getDouble("interfaz");
                                        if(interfazz == 1) {
                                            loginInt1();
                                        }
                                        else {
                                            loginInt2();
                                        }
                                    } else {
                                        Log.e("Firebase", "Error…", task.getException());
                                    }
                                }
                            });
        } else {
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build());
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setTheme(R.style.FirebaseUITema)
                            .setAvailableProviders(providers)
                            .setIsSmartLockEnabled(false)
                            .build(),
                    RC_SIGN_IN);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                primerLogin();

            } else {
                String s = "";
                IdpResponse response = IdpResponse.fromResultIntent(data);
                if (response == null) s = "Cancelado";
                else switch (response.getError().getErrorCode()) {
                    case ErrorCodes.NO_NETWORK:
                        s = "Sin conexión a Internet";
                        break;
                    case ErrorCodes.PROVIDER_ERROR:
                        s = "Error en proveedor";
                        break;
                    case ErrorCodes.DEVELOPER_ERROR:
                        s = "Error desarrollador";
                        break;
                    default:
                        s = "Otros errores de autentificación";
                }
                Toast.makeText(this, s, Toast.LENGTH_LONG).show();
            }
        }
    }
}