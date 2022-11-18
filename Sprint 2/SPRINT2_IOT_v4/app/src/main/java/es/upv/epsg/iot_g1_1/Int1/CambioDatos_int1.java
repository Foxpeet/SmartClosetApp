package es.upv.epsg.iot_g1_1.Int1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import es.upv.epsg.iot_g1_1.Int1.LandingPage_int1;
import es.upv.epsg.iot_g1_1.R;
import es.upv.epsg.iot_g1_1.Usuario;

public class CambioDatos_int1 extends AppCompatActivity{

    FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //función para quitar la Action Bar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_cambiodatos_int1);

        Spinner spinner = findViewById(R.id.nuevaTallaSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.Talla, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        cargarDatos(spinner, adapter);

    }

    public void cambiarDatos(View view) {
        EditText nuevoNombre = findViewById(R.id.nuevoNombreEditText);
        EditText nuevoEmail = findViewById(R.id.nuevoEmailEditText);
        EditText nuevoTelefono = findViewById(R.id.nuevoTelEditText);

        Spinner nuevaTallaSpinner = findViewById(R.id.nuevaTallaSpinner);

        //strings de los datos
        String nuevoNombreStr = nuevoNombre.getText().toString();
        String nuevoEmailStr = nuevoEmail.getText().toString();
        int nuevoTelefonoInt = 0;
        if(!nuevoTelefono.getText().toString().isEmpty()){
            nuevoTelefonoInt = Integer.parseInt(nuevoTelefono.getText().toString());
        }
        String nuevaTallaStr = nuevaTallaSpinner.getSelectedItem().toString();


        //cambiar datos Firebase Autentification
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(nuevoNombreStr)
                .build(); // creamos una request de cambio de nombre con el nuevo nombre y construimos el objeto
        usuario.updateProfile(profileUpdate); //le pasamos el objeto request y el nuevo correo al "usuario" de firebase
        usuario.updateEmail(nuevoEmailStr);

        Usuario user = new Usuario(usuario);

        user.setTelefono(nuevoTelefonoInt);
        user.setNombre(nuevoNombreStr);
        user.setCorreo(nuevoEmailStr);
        user.setTalla(nuevaTallaStr);
        user.setInterfaz(1);

        //cambiar datos FirebaseDatabase
        Usuario.actualizarUsuario(user, usuario.getUid());

        Toast.makeText(this, "Se han actualizado los datos", Toast.LENGTH_LONG).show(); //mensaje para el usuario
    }

    /* public void volverLandingFCD (View view) {
        Intent intent = new Intent(this, ScrollingActivity.class); startActivity(intent);
    }
*/
    public void cargarDatos(Spinner nuevaTalla, ArrayAdapter<CharSequence> adapter){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        EditText nuevoNombre = findViewById(R.id.nuevoNombreEditText);
        EditText nuevoEmail = findViewById(R.id.nuevoEmailEditText);
        EditText nuevoTelefono = findViewById(R.id.nuevoTelEditText);

        db.collection("usuarios").document(usuario.getUid()).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        nuevoNombre.setText(task.getResult().getString("nombre"));
                        nuevoEmail.setText(task.getResult().getString("correo"));
                        if(!task.getResult().getLong("telefono").toString().equals("0")){
                            nuevoTelefono.setText(task.getResult().getLong("telefono").toString());
                        }
                        nuevaTalla.setSelection(adapter.getPosition(task.getResult().getString("talla")));
                    }
                }
        );
    }

    public void volverLandigDesdeCambiarD(View view) {
        Intent intent = new Intent(this, LandingPage_int1.class);
        startActivity(intent);
    }

}
