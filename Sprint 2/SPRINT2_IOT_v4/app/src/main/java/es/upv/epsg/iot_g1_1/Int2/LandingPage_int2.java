package es.upv.epsg.iot_g1_1.Int2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.upv.epsg.iot_g1_1.AcercaDeActivity;
import es.upv.epsg.iot_g1_1.Int1.CambioDatos_int1;
import es.upv.epsg.iot_g1_1.Int1.NuevaPrenda_int1;
import es.upv.epsg.iot_g1_1.PreferenciasActivity;
import es.upv.epsg.iot_g1_1.R;
import es.upv.epsg.iot_g1_1.Usuario;

public class LandingPage_int2 extends AppCompatActivity {

    FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
    Usuario user = new Usuario(usuario);

    public String theme;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //aplicar el tema segun la configuracion (este codigo deberia repetirse en cada activity que queramos cambiar el tema)
        /*
        theme = PreferenceManager.getDefaultSharedPreferences(this).getString("tema", "0");
        if(theme.equals("1")){
            Toast.makeText(this, "bool 1", Toast.LENGTH_LONG).show();
            super.setTheme(R.style.Theme_Prueba);
        } if(theme.equals("0")){
            Toast.makeText(this, "bool 2", Toast.LENGTH_LONG).show();
            super.setTheme(R.style.Theme_ProyectoPrueba);
        }
         */

        //función para quitar la Action Bar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_landing_int2);



        TextView nombre = findViewById(R.id.textView4);
        nombre.setText(user.getNombre());

    }

    public void paginaNuevaPrendaInt2(View view) {
        Intent intent = new Intent(this, NuevaPrenda_int2.class);
        startActivity(intent);
    }

    public void paginaTodasPrendasInt2(View view) {
        Intent intent = new Intent(this, MainActivity_int2.class);
        startActivity(intent);
    }

    public void paginaCambioDatosInt2(View view) {
        Intent intent = new Intent(this, CambioDatos_int2.class);
        startActivity(intent);
    }

    public void paginaConfigPref(View view) {
        Intent intent = new Intent(this, PreferenciasActivity.class);
        startActivity(intent);
    }

    //para poder recargar el tema (este codigo deberia repetirse en cada activity que queramos cambiar el tema)
    /*
    @Override protected void onPause() {
        Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
        super.onPause();
        super.onStop();
    }
    @Override protected void onRestart() {
        super.onRestart();
        Toast.makeText(this, "onRestart", Toast.LENGTH_SHORT).show();
        super.recreate();
    }
     */

    public void lanzarAcercaDe(View view){
        Intent i = new Intent(this, AcercaDeActivity.class);
        startActivity(i);
    }
}
