package com.example.grupo1_1.smartclosset.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.grupo1_1.smartclosset.DatePickerFragment;
import com.example.grupo1_1.smartclosset.R;
import com.example.grupo1_1.smartclosset.pojos.Prenda;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;


public class PaginaEditarPrenda extends AppCompatActivity{

    FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

    Prenda prendaActual;

    EditText nombrePrenda;
    EditText fechaCompra;
    Spinner tallass;
    Spinner colores;
    Spinner calideces;
    Spinner tiposs;

    TextView errNom;
    TextView errFecha;
    TextView errColor;
    TextView errTipo;
    TextView errCalidez;
    TextView errTalla;

    public ImageView foto;
    public ImageView editarFoto;

    public StorageReference storageRef;
    public File localFile;
    public StorageReference ficheroRef;
    public Uri uriData;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //función para quitar la Action Bar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_editar_prenda);

        prendaActual = (Prenda) getIntent().getSerializableExtra("prenda");

        foto = findViewById(R.id.fotoEditarprenda);
        editarFoto = findViewById(R.id.EditarEditarPrenda);

        try {
            localFile = File.createTempFile("image", "jpg"); //nombre y extensión
        } catch (IOException e) {
            e.printStackTrace(); //Si hay problemas detenemos la app
        }
        final String path = localFile.getAbsolutePath();

        storageRef = FirebaseStorage.getInstance().getReference();
        ficheroRef = storageRef.child(prendaActual.getUrlFoto());

        descargarYMostrarFoto(path);

        nombrePrenda= findViewById(R.id.nombreEditarPrenda);
        nombrePrenda.setText(prendaActual.getNombrePrenda());

        fechaCompra = findViewById(R.id.fechaCompraEditarPrenda);
        fechaCompra.setText(prendaActual.getFechaCompra());

        errNom = findViewById(R.id.textoErrorNomPrenda2);
        errNom.setVisibility(View.INVISIBLE);
        errFecha = findViewById(R.id.textoErrorFechPrenda2);
        errFecha.setVisibility(View.INVISIBLE);
        errColor = findViewById(R.id.textoErrColPrenda2);
        errColor.setVisibility(View.INVISIBLE);
        errTipo = findViewById(R.id.textoErrCatPrenda2);
        errTipo.setVisibility(View.INVISIBLE);
        errCalidez = findViewById(R.id.textoErrCalidezPrenda2);
        errCalidez.setVisibility(View.INVISIBLE);
        errTalla = findViewById(R.id.textoErrTallaPrenda2);
        errTalla.setVisibility(View.INVISIBLE);

        fechaCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cerrarTeclado();
                switch (view.getId()) {
                    case R.id.fechaCompraEditarPrenda:
                        showDatePickerDialog();
                        break;
                }
            }
        });

        tallass = findViewById(R.id.tallaEditarPrenda);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.Talla, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tallass.setAdapter(adapter);
        tallass.setSelection(adapter.getPosition(prendaActual.getTalla()));

        colores = findViewById(R.id.colorEditarPrenda);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                this, R.array.Color, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colores.setAdapter(adapter2);
        colores.setSelection(adapter2.getPosition(prendaActual.getColor()));

        calideces = findViewById(R.id.calidezEditarPrenda);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(
                this, R.array.calidez, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        calideces.setAdapter(adapter3);
        calideces.setSelection(adapter3.getPosition(prendaActual.getEstacionDeUso()));

        tiposs = findViewById(R.id.tipoEditarPrenda);
        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(
                this, R.array.categoria, android.R.layout.simple_spinner_item);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tiposs.setAdapter(adapter4);
        tiposs.setSelection(adapter4.getPosition(prendaActual.getTipo()));
    }

    public void lanzarGuardarEditarPrenda(View view) {
        int contador=0;

        String nuevoNombrePrendaStr = nombrePrenda.getText().toString();
        String nuevaFechaStr = fechaCompra.getText().toString();
        String nuevaTallaStr = tallass.getSelectedItem().toString();
        String nuevaColorStr = colores.getSelectedItem().toString();
        String nuevaCalidezStr = calideces.getSelectedItem().toString();
        String nuevaTipoStr = tiposs.getSelectedItem().toString();

        prendaActual.setNombrePrenda(nuevoNombrePrendaStr);
        prendaActual.setFechaCompra(nuevaFechaStr);
        prendaActual.setTalla(nuevaTallaStr);
        prendaActual.setColor(nuevaColorStr);
        prendaActual.setEstacionDeUso(nuevaCalidezStr);
        prendaActual.setTipo(nuevaTipoStr);

        if(prendaActual.getNombrePrenda().isEmpty()){
            errNom.setVisibility(View.VISIBLE);
            contador++;
        }else{
            errNom.setVisibility(View.INVISIBLE);
        }

        if(prendaActual.getFechaCompra().isEmpty()){
            errFecha.setVisibility(View.VISIBLE);
            contador++;
        }else{
            errFecha.setVisibility(View.INVISIBLE);
        }

        if (prendaActual.getTalla().equals("Talla")){
            errTalla.setVisibility(View.VISIBLE);
            contador++;
        }else{
            errTalla.setVisibility(View.INVISIBLE);
        }

        if(prendaActual.getTipo().equals("Categoria")){
            errTipo.setVisibility(View.VISIBLE);
            contador++;
        }else{
            errTipo.setVisibility(View.INVISIBLE);
        }

        if(prendaActual.getEstacionDeUso().equals("Calidez")){
            errCalidez.setVisibility(View.VISIBLE);
            contador++;
        }else{
            errCalidez.setVisibility(View.INVISIBLE);
        }

        if(prendaActual.getColor().equals("Color")){
            errColor.setVisibility(View.VISIBLE);
            contador++;
        }else {
            errColor.setVisibility(View.INVISIBLE);
        }

        if(contador==0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PaginaEditarPrenda.this);
            builder.setTitle(getResources().getString(R.string.EditarPrenda_guardar_titulo))
                    .setMessage(getResources().getString(R.string.EditarPrenda_guardar_mensaje))
                    .setPositiveButton(getResources().getString(R.string.Comunes_guardar), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Prenda.actualizarPrenda(prendaActual, prendaActual.getDocId(), usuario.getUid(), uriData);
                            Intent intent = new Intent(PaginaEditarPrenda.this, PaginaPrenda.class);
                            intent.putExtra("prenda", prendaActual);
                            if(uriData != null){
                                intent.putExtra("FotoData", uriData.toString());
                            }
                            startActivity(intent);
                        }})
                    .setNegativeButton(getResources().getString(R.string.Comunes_cancelar), null)
                    .show();
        }
    }

    private void cerrarTeclado() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 porque Enero es 0
                String selectedDate = day + " / " + (month+1) + " / " + year;
                prendaActual.setFechaCompra(selectedDate);
                fechaCompra.setText(selectedDate);
            }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PaginaEditarPrenda.this);
        builder.setTitle(getResources().getString(R.string.EditarPrenda_cancelar_titulo))
                .setMessage(getResources().getString(R.string.EditarPrenda_cancelar_mensaje))
                .setPositiveButton(getResources().getString(R.string.EditarPrenda_cancelar_salir), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Prenda.actualizarPrenda(prendaActual, prendaActual.getDocId(), usuario.getUid());
                        onBackPressed();
                        Intent intent = new Intent(PaginaEditarPrenda.this, PaginaPrenda.class);
                        intent.putExtra("prenda", prendaActual);
                        startActivity(intent);
                    }})
                .setNegativeButton(getResources().getString(R.string.EditarPrenda_cancelar_seguirEditando), null)
                .show();
    }

    //imagen
    public void descargarYMostrarFoto(String path){
        ficheroRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>(){
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot){
                        Log.d("Almacenamiento", "Fichero bajado" + path);
                        foto.setImageBitmap(BitmapFactory.decodeFile(path));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("Almacenamiento", "ERROR: bajando fichero");
                        if(prendaActual.getTipo().equals("parte de arriba")){
                            foto.setImageResource(R.drawable.camiseta);
                            return;
                        }
                        if(prendaActual.getTipo().equals("parte de abajo")){
                            foto.setImageResource(R.drawable.icono_pantalones);
                            return;
                        }
                        if(prendaActual.getTipo().equals("accesorios")){
                            foto.setImageResource(R.drawable.bufanda);
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(final int requestCode,
                                    final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            foto.setImageURI(data.getData());
            uriData = data.getData();
        }
    }

    public void abrirGaleriaFotoEditarPrenda(View view) {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, 1234);
    }
}