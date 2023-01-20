package com.example.grupo1_1.smartclosset.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.grupo1_1.smartclosset.R;
import com.example.grupo1_1.smartclosset.pojos.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class PerfilEditarActivity extends AppCompatActivity{

    FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
    Usuario user = new Usuario(usuario);

    EditText nuevoNombre;
    Spinner nuevaTallaSpinner;
    String nuevoNombreStr;

    TextView nombreError;
    TextView tallaError;
    String almacenarTalla;

    ImageView fotoUser;
    public Uri uriData;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //función para quitar la Action Bar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_perfil_editar);

        storageRef = FirebaseStorage.getInstance().getReference();

        Spinner spinner = findViewById(R.id.tallaEditarPerfil);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.Talla, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        cargarDatos(spinner, adapter);

        fotoUser=findViewById(R.id.botonFotoEditarPerfil);
        nuevoNombre = findViewById(R.id.nombreEditarPerfil);

        nuevaTallaSpinner = findViewById(R.id.tallaEditarPerfil);

        tallaError = findViewById(R.id.textoErrorTallaPerfil);
        tallaError.setVisibility(View.INVISIBLE);
        nombreError =findViewById(R.id.textoErrorNombrePerfil);
        nombreError.setVisibility(View.INVISIBLE);
    }

    // Guardar y cargar datos del usuario -------------------------------------------------------------------------------
    public void guardarDatos() {
        //strings de los datos
        nuevoNombreStr = nuevoNombre.getText().toString();
        String nuevaTallaStr = nuevaTallaSpinner.getSelectedItem().toString();

        //cambiar datos Firebase Autentification
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(nuevoNombreStr)
                .build(); // creamos una request de cambio de nombre con el nuevo nombre y construimos el objeto
        usuario.updateProfile(profileUpdate); //le pasamos el objeto request y el nuevo correo al "usuario" de firebase

        user.setNombre(nuevoNombreStr);
        user.setTalla(nuevaTallaStr);

        //cambiar datos FirebaseDatabase
        Usuario.actualizarUsuario(user, usuario.getUid());

        Toast.makeText(this, R.string.EditarPerfil_datosActualizados, Toast.LENGTH_LONG).show(); //mensaje para el usuario
        Intent intent = new Intent(this, PerfilUsuarioActivity.class);
        if(uriData != null){
            subirFichero(uriData, "FotosUsuarios" + "/" + usuario.getUid());
            intent.putExtra("fotoData", uriData.toString());
        }
        startActivity(intent);
    }
    public void lanzarGuardarEditarPerfil(View view){
        int contador =0;

        almacenarTalla = nuevaTallaSpinner.getSelectedItem().toString();
        if(nuevoNombre.getText().toString().isEmpty()) {
            nombreError.setVisibility(View.VISIBLE);
            contador++;
        }else{
            nombreError.setVisibility(View.INVISIBLE);
        }

        if (almacenarTalla.equals("Talla")){
            tallaError.setVisibility(View.VISIBLE);
            contador++;
        }else{
            tallaError.setVisibility(View.INVISIBLE);
        }

        if(contador==0){
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.EditarPerfil_guardar_titulo))
                    .setMessage(getResources().getString(R.string.EditarPerfil_guardar_mensaje))
                    .setPositiveButton(R.string.Comunes_guardar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            guardarDatos();
                        }})
                    .setNegativeButton(getResources().getString(R.string.Comunes_cancelar), null)
                    .show();
        }
    }

    public void cargarDatos(Spinner nuevaTalla, ArrayAdapter<CharSequence> adapter){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        EditText nuevoNombre = findViewById(R.id.nombreEditarPerfil);

        db.collection("usuarios").document(usuario.getUid()).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        nuevoNombre.setText(task.getResult().getString("nombre"));
                        nuevaTalla.setSelection(adapter.getPosition(task.getResult().getString("talla")));
                    }
                }
        );
        descargarYMostrarFotoPerfil();
    }

    // Intent cambiar contraseña ---------------------------------------------------------------------------------------------------------
    public void lanzarCambiarContrasenya(View view) {
        openDialog();
    }
    public void openDialog(){
        CambiarContrasenya cambiarContrasenya = new CambiarContrasenya();
        cambiarContrasenya.show(getSupportFragmentManager(), "cambiar contraseña");
    }

    // A partir de aqui es codigo para la foto ----------------------------------------------------------------------------------
    private StorageReference storageRef;

    public void descargarYMostrarFotoPerfil(){
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
                        ImageView imageView = findViewById(R.id.botonFotoEditarPerfil);
                        imageView.setImageBitmap(BitmapFactory.decodeFile(path));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("Almacenamiento", "ERROR: bajando fichero");
                    }
                });
    }

    public void subirFotoRequest(View v){
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, 1234);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            uriData = data.getData();
            fotoUser.setImageURI(uriData);
        }
    }

    public void subirFichero(Uri uri, String route){
        StorageReference ficheroRef = storageRef.child(route);
        ficheroRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d("Almacenamiento", "Fichero subido");
                        PerfilEditarActivity.super.recreate();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("Almacenamiento", "ERROR: subiendo fichero");
                    }
                });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.EditarPerfil_cancelar_titulo))
                .setMessage(getResources().getString(R.string.EditarPerfil_cancelar_mensaje) + "\n" + getResources().getString(R.string.EditarPerfil_cancelar_mensaje2))
                .setPositiveButton(getResources().getString(R.string.EditarPerfil_cancelar_salir), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent intent = new Intent(PerfilEditarActivity.this, PerfilUsuarioActivity.class);
                        startActivity(intent);
                    }})
                .setNegativeButton(getResources().getString(R.string.EditarPerfil_cancelar_seguirEditando), null)
                .show();
    }
}