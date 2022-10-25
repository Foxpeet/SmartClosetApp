package es.upv.epsg.iot_g1_1;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import es.upv.epsg.iot_g1_1.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Toolbar toolbar = binding.toolbar;
        //setSupportActionBar(toolbar);
        /* CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(getTitle()); */

        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        TextView nombreLanding = findViewById(R.id.textoNombreUsuario);
        nombreLanding.setText(usuario.getDisplayName());

        // FloatingActionButton fab = binding.fab;
        /* fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }); */
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_user) {
            Intent intent = new Intent(this, UsuarioActivity.class); startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
    public void abrirPerfil (View view) {
        Intent intent = new Intent(this, UsuarioActivity.class); startActivity(intent);
    }
    public void abrirCambiarDatos (View view) {
        Intent intent = new Intent(this, CambioDatos.class); startActivity(intent);
    }
    public void lanzarAcercaDe(View view){
        Intent i = new Intent(this, AcercaDeActivity.class);
        startActivity(i);
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

}