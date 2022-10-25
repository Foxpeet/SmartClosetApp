package es.upv.epsg.iot_g1_1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.LruCache;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UsuarioActivity extends AppCompatActivity {

    //----------borrar(no se usan)-------------
    //private EditText nombreCambiado;
    //private String nombree;
    //private EditText emailCambiado;
    //------------------------------------------

    FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

    @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_usuario);
        // Inicialización Volley (Hacer solo una vez en Singleton o Applicaction)
        RequestQueue colaPeticiones = Volley.newRequestQueue(this);
        ImageLoader lectorImagenes = new ImageLoader(colaPeticiones,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> cache =
                            new LruCache<String, Bitmap>(10);
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }
                });

    //todo esto comentado podria borrarse

        //nombreCambiado = (EditText)findViewById(R.id.cambioNombre);
        //String nombrecito = nombreCambiado.getText().toString();
        //nombree = "no va";
        //nombree = nombrecito;
        



        //UserProfileChangeRequest perfil = new UserProfileChangeRequest.Builder()
        //        .setDisplayName(nombrecito)
        //        .build();
        //usuario.updateProfile(perfil);


        TextView nombre = findViewById(R.id.nombre);
        nombre.setText(usuario.getDisplayName());

        TextView correo = findViewById(R.id.email);
        correo.setText(usuario.getEmail());


    }


    public void cerrarSesion(View view) { AuthUI.getInstance().signOut(getApplicationContext())
            .addOnCompleteListener(new OnCompleteListener<Void>() {

                @Override public void onComplete(@NonNull Task<Void> task) {
                Intent i = new Intent(
                    getApplicationContext (),LoginActivity.class); i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK); startActivity(i);
                finish(); }
            });
    }

    // ---------- parte de diego --------------------------------------------


//-----------------Fin de diego-------------------------------------------------------
    public void lanzarAcercaDe(View view){
        Intent i = new Intent(this, AcercaDeActivity.class);
        startActivity(i);
    }

    public void volverLandingDU (View view) {
        Intent intent = new Intent(this, MainActivity.class); startActivity(intent);
    }
}
