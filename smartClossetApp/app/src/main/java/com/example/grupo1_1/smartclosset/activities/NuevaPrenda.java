package com.example.grupo1_1.smartclosset.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.grupo1_1.smartclosset.DatePickerFragment;

import com.example.grupo1_1.smartclosset.R;
import com.example.grupo1_1.smartclosset.pojos.Prenda;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.eclipse.paho.client.mqttv3.MqttClient;

import java.io.File;
import java.io.IOException;

public class NuevaPrenda extends AppCompatActivity {

    static MqttClient client;
    FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
    Prenda prendaNueva = new Prenda();

    String idNuevo = " ";

    EditText recuadroFecha;
    EditText recuadroNombre;
    ImageView favoritos;
    TextView errNom;
    TextView errFecha;
    TextView errColor;
    TextView errTipo;
    TextView errCalidez;
    TextView errTalla;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //función para quitar la Action Bar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_anyadir_prenda);

        idNuevo = (String) getIntent().getSerializableExtra("idRFID");
        prendaNueva.setIdRFID(idNuevo);

        recuadroFecha = findViewById(R.id.fechaCompraAnyadirPrenda);
        recuadroNombre = findViewById(R.id.nombreAnyadirPrenda);

        nuevaFotoPrenda = findViewById(R.id.botonFotoAnyadirPrenda);

        favoritos= findViewById(R.id.favoritosAnyadirPrendaTrue);

        errNom = findViewById(R.id.textoErrorNomPrenda);
        errNom.setVisibility(View.INVISIBLE);

        errFecha = findViewById(R.id.textoErrorFechPrenda);
        errFecha.setVisibility(View.INVISIBLE);

        errColor = findViewById(R.id.textoErrColPrenda);
        errColor.setVisibility(View.INVISIBLE);

        errTipo = findViewById(R.id.textoErrCatPrenda);
        errTipo.setVisibility(View.INVISIBLE);

        errCalidez = findViewById(R.id.textoErrCalidezPrenda);
        errCalidez.setVisibility(View.INVISIBLE);

        errTalla = findViewById(R.id.textoErrTallaPrenda);
        errTalla.setVisibility(View.INVISIBLE);

        favoritos.setVisibility(View.INVISIBLE);

        if(getIntent().getStringExtra("fecha") != null){
            recuadroFecha.setText(getIntent().getStringExtra("fecha"));
        }

        Button guardar = findViewById(R.id.guardarAnyadirPrendas);

        try {
            localFile = File.createTempFile("image", "jpg"); //nombre y extensión
        } catch (IOException e) {
            e.printStackTrace(); //Si hay problemas detenemos la app
        }

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int contador=0;

                EditText editTextName = findViewById(R.id.nombreAnyadirPrenda);
                String prendaName = editTextName.getText().toString();

                Spinner psTalla = findViewById(R.id.tallaAnyadirPrenda);
                String prendaTalla = psTalla.getSelectedItem().toString();

                Spinner psCategoria = findViewById(R.id.tipoAnyadirPrenda);
                String prendaCategoria = psCategoria.getSelectedItem().toString();

                Spinner psCalidez = findViewById(R.id.calidezAnyadirPrenda);
                String prendaCalidez = psCalidez.getSelectedItem().toString();

                Spinner psColor = findViewById(R.id.colorAnyadirPrenda);
                String prendaColor = psColor.getSelectedItem().toString();

                EditText fechacompra = findViewById(R.id.fechaCompraAnyadirPrenda);
                String prendafecha = fechacompra.getText().toString();

                prendaNueva.setNombrePrenda(prendaName);
                prendaNueva.setTalla(prendaTalla);
                prendaNueva.setTipo(prendaCategoria);
                prendaNueva.setEstacionDeUso(prendaCalidez);
                prendaNueva.setColor(prendaColor);
                prendaNueva.setFechaCompra(prendafecha);

                // inicialmente todas las prendas van a estar limpias
                prendaNueva.setLimpieza(true);

                if(prendaNueva.getNombrePrenda().isEmpty()){
                    errNom.setVisibility(View.VISIBLE);
                    contador++;
                }else{
                    errNom.setVisibility(View.INVISIBLE);
                }

                if(prendaNueva.getFechaCompra().isEmpty()){
                    errFecha.setVisibility(View.VISIBLE);
                    contador++;
                }else{
                    errFecha.setVisibility(View.INVISIBLE);
                }

                if (prendaNueva.getTalla().equals("Talla")){
                    errTalla.setVisibility(View.VISIBLE);
                    contador++;
                }else{
                    errTalla.setVisibility(View.INVISIBLE);
                }

                if(prendaNueva.getTipo().equals("Categoria")){
                    errTipo.setVisibility(View.VISIBLE);
                    contador++;
                }else{
                    errTipo.setVisibility(View.INVISIBLE);
                }

                if(prendaNueva.getEstacionDeUso().equals("Calidez")){
                    errCalidez.setVisibility(View.VISIBLE);
                    contador++;
                }else{
                    errCalidez.setVisibility(View.INVISIBLE);
                }

                if(prendaNueva.getColor().equals("Color")){
                    errColor.setVisibility(View.VISIBLE);
                    contador++;
                }else {
                    errColor.setVisibility(View.INVISIBLE);
                }

                if(contador==0) {
                    Toast.makeText(NuevaPrenda.this, getResources().getString(R.string.NuevaPrenda_datosCorrectos), Toast.LENGTH_SHORT).show();
                    Prenda.crearPrenda(prendaNueva, usuario.getUid(), uriData);
                    irPagTodasPrendas();
                }
            }
        });

        recuadroFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cerrarTeclado();
                switch (view.getId()) {
                    case R.id.fechaCompraAnyadirPrenda:
                        showDatePickerDialog();
                        break;
                }
            }
        });
    }
    private void cerrarTeclado()
    {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 porque Enero es 0
                // vamos a colocar un 0 delante de "day" si es >10
                String selectedDate;
                if(day<10) {
                    if(month<10) {
                        selectedDate = "0"+day + "/" + 0+(month+1) + "/" + year;
                    }
                    else {
                        selectedDate = "0"+day + "/" + (month+1) + "/" + year;
                    }
                }
                else {
                    if(month<10) {
                        selectedDate = day + "/" + 0+(month+1) + "/" + year;
                    }
                    else {
                        selectedDate = day + "/" + (month+1) + "/" + year;
                    }
                }
                prendaNueva.setFechaCompra(selectedDate);
                recuadroFecha.setText(selectedDate);
            }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void irPagTodasPrendas () {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void lanzarFavoritos(View view) {
        ImageView nofavoritos = findViewById(R.id.favoritosAnyadirPrenda);
        nofavoritos.setVisibility(View.INVISIBLE);
        favoritos.setVisibility(view.VISIBLE);
        prendaNueva.setFavoritos(true);
    }

    public void lanzarFavoritosTrue(View view) {
        ImageView nofavoritos = findViewById(R.id.favoritosAnyadirPrenda);
        favoritos.setVisibility(view.INVISIBLE);
        nofavoritos.setVisibility(View.VISIBLE);
        prendaNueva.setFavoritos(false);
    }

    // Codigo de las fotos -------------------------------------------------------------------------
    Uri uriData;
    File localFile;
    ImageView nuevaFotoPrenda;

    public void ponerFoto(View v){
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, 1234);
    }

    @Override
    protected void onActivityResult(final int requestCode,
                                    final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            nuevaFotoPrenda.setImageURI(data.getData());
            uriData = data.getData();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}