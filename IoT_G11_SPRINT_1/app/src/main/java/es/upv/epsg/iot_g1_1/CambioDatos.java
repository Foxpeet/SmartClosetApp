package es.upv.epsg.iot_g1_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class CambioDatos extends AppCompatActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiodatos);
    }

    public void cambiarDatos(View view) {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        EditText nuevoNombreEditText = findViewById(R.id.cambioNombreN); //los edittext donse se escriben los datos nuevos
        EditText nuevoEmailEditText = findViewById(R.id.cambioEmailN);


        String nuevoNombre = nuevoNombreEditText.getText().toString(); //las string de los datos
        String nuevoEmail = nuevoEmailEditText.getText().toString();



        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(nuevoNombre)
                .build(); // creamos una request de cambio de nombre con el nuevo nombre y construimos el objeto
        usuario.updateProfile(profileUpdate); //le pasamos el objeto request y el nuevo correo al "usuario" de firebase
        usuario.updateEmail(nuevoEmail);
/*
        TextView nombre = (TextView) findViewById(R.id.nombre);
        nombre.setText(usuario2.getDisplayName());

        TextView correo = (TextView) findViewById(R.id.email);
        correo.setText(usuario2.getEmail());*/

        //Intent i = new Intent(getApplicationContext (), MainActivity.class);
        //startActivity(i);

        Toast.makeText(this, "Se han actualizado los datos", Toast.LENGTH_LONG).show(); //mensaje para el usuario
    }

    public void volverLandingFCD (View view) {
        Intent intent = new Intent(this, MainActivity.class); startActivity(intent);
    }


}
