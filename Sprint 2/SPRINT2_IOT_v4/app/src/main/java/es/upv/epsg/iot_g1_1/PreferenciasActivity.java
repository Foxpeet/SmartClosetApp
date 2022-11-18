package es.upv.epsg.iot_g1_1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class PreferenciasActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    SharedPreferences pref;
    String theme;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //función para quitar la Action Bar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

       /* theme = PreferenceManager.getDefaultSharedPreferences(this).getString("tema", "0");
        if(theme.equals("1")){
            Toast.makeText(this, "bool 1", Toast.LENGTH_LONG).show();
            super.setTheme(R.style.Theme_Prueba);
        } if(theme.equals("0")){
            Toast.makeText(this, "bool 2", Toast.LENGTH_LONG).show();
            super.setTheme(R.style.Theme_ProyectoPrueba);
        } */

        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferenciasFragment())
                .commit();
        pref = PreferenceManager.getDefaultSharedPreferences(this);

    }

    public void onSharedPreferenceChanged (SharedPreferences sharedPreferences, String key)
    {
        super.onRestart();
    }

    @Override protected void onRestart() {
        super.onRestart();
        Toast.makeText(this, "onRestart", Toast.LENGTH_SHORT).show();
        super.recreate();
    }
/*
    @Override
    public void onResume() {
        super.onResume();
        // Registrar escucha
        pref.getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Eliminar registro de la escucha
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }*/
}
