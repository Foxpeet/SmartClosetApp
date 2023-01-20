package com.example.grupo1_1.smartclosset.activities;

import static java.util.Locale.getDefault;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.grupo1_1.smartclosset.R;

import java.util.Locale;

public class cambiarIdioma extends AppCompatActivity {

    ImageView esp;
    ImageView ing;

    ImageView espSelected;
    ImageView ingSelected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cambiar_idioma);

        esp = findViewById(R.id.espanyol);
        ing = findViewById(R.id.ingles);

        espSelected = findViewById(R.id.espSelected);
        ingSelected = findViewById(R.id.ingSelected);

        espSelected.setAlpha(0.0f);
        ingSelected.setAlpha(0.0f);

        esp.getBackground().setAlpha(170);
        ing.getBackground().setAlpha(170);
    }

    private Locale locale;
    private Configuration config = new Configuration();
    String idioma;

    public void lanzarEspanyol(View v){
        espSelected.setAlpha(1.0f);
        ingSelected.setAlpha(0.0f);
        locale = new Locale("es");
        config.locale =locale;
        idioma = "es";
    }

    public void lanzarIngles(View v){
        espSelected.setAlpha(0.0f);
        ingSelected.setAlpha(1.0f);
        locale = new Locale("en");
        config.locale =locale;
        idioma = "en";
    }

    public void guardarIdioma(View v) {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.ConfiguracionIdioma_guardar_titulo))
                .setMessage(getResources().getString(R.string.ConfiguracionIdioma_guardar_mensaje))
                .setPositiveButton(getResources().getString(R.string.ConfiguracionIdioma_cancelar_salir), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        SharedPreferences sharPref = getSharedPreferences("IdiomaSmartClosset", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharPref.edit();
                        editor.putString("IdiomaApp", idioma);
                        editor.apply();
                        getResources().updateConfiguration(config, null);
                        Intent i = new Intent (cambiarIdioma.this, PerfilUsuarioActivity.class);
                        startActivity(i);
                        finish();
                    }})
                .setNegativeButton(getResources().getString(R.string.ConfiguracionIdioma_cancelar_continuar), null)
                .show();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.ConfiguracionIdioma_cancelar_titulo))
                .setMessage(getResources().getString(R.string.ConfiguracionIdioma_cancelar_mensaje))
                .setPositiveButton(getResources().getString(R.string.ConfiguracionIdioma_cancelar_salir), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent i = new Intent(cambiarIdioma.this, PerfilUsuarioActivity.class);
                        startActivity(i);
                    }})
                .setNegativeButton(getResources().getString(R.string.ConfiguracionIdioma_cancelar_continuar), null)
                .show();
    }
}
