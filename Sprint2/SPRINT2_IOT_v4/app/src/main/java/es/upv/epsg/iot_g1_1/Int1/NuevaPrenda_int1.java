package es.upv.epsg.iot_g1_1.Int1;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.upv.epsg.iot_g1_1.DatePickerFragment;
import es.upv.epsg.iot_g1_1.MainActivity;
import es.upv.epsg.iot_g1_1.Prenda;
import es.upv.epsg.iot_g1_1.R;

public class NuevaPrenda_int1 extends AppCompatActivity {

    FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
    private void cerrarTeclado()
    {

        View view = this.getCurrentFocus();

        if (view != null) {

            InputMethodManager manager
                    = (InputMethodManager)
                    getSystemService(
                            Context.INPUT_METHOD_SERVICE);
            manager
                    .hideSoftInputFromWindow(
                            view.getWindowToken(), 0);
        }
    }
    public void volverLandingNuevaPrenda (View view) {
        Intent intent = new Intent(this, LandingPage_int1.class);
        startActivity(intent);
    }

    public void irPagTodasPrendas () {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    EditText recuadroFecha;
    EditText recuadroNombre;
    Prenda prendaNueva = new Prenda();


    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                String selectedDate = day + " / " + (month+1) + " / " + year;
                prendaNueva.setFechaCompra(selectedDate);
                recuadroFecha.setText(selectedDate);
            }
        });

        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //función para quitar la Action Bar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_nuevaprenda_int1);
        recuadroFecha = findViewById(R.id.editTextTextPersonName2);
        recuadroNombre = findViewById(R.id.editTextTextPersonName);


        Button guardar = findViewById(R.id.button2);

        TextView prueba = findViewById(R.id.textView10);


        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText editTextName = findViewById(R.id.editTextTextPersonName);
                String prendaName = editTextName.getText().toString();

                Spinner psTalla = findViewById(R.id.spinner2);
                String prendaTalla = psTalla.getSelectedItem().toString();

                Spinner psCategoria = findViewById(R.id.spinner5);
                String prendaCategoria = psCategoria.getSelectedItem().toString();

                Spinner psCalidez = findViewById(R.id.spinner4);
                String prendaCalidez = psCalidez.getSelectedItem().toString();

                Spinner psColor = findViewById(R.id.spinner3);
                String prendaColor = psColor.getSelectedItem().toString();


                prendaNueva.setNombrePrenda(prendaName);
                prendaNueva.setTalla(prendaTalla);
                prendaNueva.setTipo(prendaCategoria);
                prendaNueva.setEstacionDeUso(prendaCalidez);
                prendaNueva.setColor(prendaColor);

                Prenda.crearPrenda(prendaNueva, usuario.getUid());

                irPagTodasPrendas();

            }
        });



        recuadroFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cerrarTeclado();
                switch (view.getId()) {
                    case R.id.editTextTextPersonName2:
                        showDatePickerDialog();
                        break;
                }
            }

        });


    }





}


