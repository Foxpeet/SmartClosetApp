package es.upv.epsg.iot_g1_1;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

import es.upv.epsg.iot_g1_1.Int1.CambioDatos_int1;
import es.upv.epsg.iot_g1_1.Int1.LandingPage_int1;
import es.upv.epsg.iot_g1_1.Int1.NuevaPrenda_int1;
import es.upv.epsg.iot_g1_1.Int1.PaginaPrenda_int1;
import es.upv.epsg.iot_g1_1.databinding.ActivityMainInt1Binding;

public class MainActivity extends AppCompatActivity {

    ArrayList<Prenda> listaPrendas;
    private ActivityMainInt1Binding binding;
    public static AdaptadorPrendasFirestoreUI adaptador;
    private RecyclerView recyclerView;
    FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
    public String uId = "mB42Axfe32NBSRf0abLjCo2naNx1"; // "Prueba con el ID de un usuario determinado para ver que funcione"
    public String filtroActivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //función para quitar la Action Bar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        binding = ActivityMainInt1Binding.inflate(getLayoutInflater()); //F
        setContentView(binding.getRoot()); //F

        //filtro desactivado
        filtroActivo = "ninguno";

        Query query = FirebaseFirestore.getInstance()
                 //.collection("Usuarios").document(uId).collection("prendas") //"Prueba para comprobar que funciona"
                 .collection("usuarios").document(usuario.getUid()).collection("prendas")
                .limit(50);
        FirestoreRecyclerOptions<Prenda> options = new FirestoreRecyclerOptions
                .Builder<Prenda>().setQuery(query, Prenda.class).build();
        adaptador = new AdaptadorPrendasFirestoreUI(options, this);
        binding.recyclerView.setAdapter(adaptador);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setHasFixedSize(true);

        adaptador.startListening();


        //meterle la funcion del filtro como onclick en las imageview
        ImageView CamisetaImage = binding.imageViewCamiseta;
        ImageView PantalonImage = binding.imageViewPantalon;
        ImageView AccesorioImage = binding.imageViewAccesorios;

        CamisetaImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aplicarFiltro("parte de arriba");
            }
        });
        PantalonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aplicarFiltro("parte de abajo");
            }
        });
        AccesorioImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aplicarFiltro("accesorios");
            }
        });

        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View m) {
                int pos= recyclerView.getChildAdapterPosition(m);
                abrirPaginaPrenda(pos);
            }
        });

    }

    public void abrirPaginaPrenda(int pos) {
        Intent i = new Intent(this, PaginaPrenda_int1.class);
        i.putExtra("pos", pos);
        this.startActivity(i);
    }

    //funcion que aplica o lo quita segun si "filtroactivo" contiene el texto del filtro activado o no, si lo tiene, desactiva el filtro y muestra todas las prendas
    public void aplicarFiltro(String tipo){
        if(tipo.equals(filtroActivo)){
            Query query = FirebaseFirestore.getInstance()
                    .collection("usuarios").document(usuario.getUid()).collection("prendas") //"Prueba para comprobar que funciona"
                    //.collection("Usuarios").document(usuario.getUid()).collection("prendas")
                    .limit(50);
            FirestoreRecyclerOptions<Prenda> options = new FirestoreRecyclerOptions
                    .Builder<Prenda>().setQuery(query, Prenda.class).build();
            adaptador = new AdaptadorPrendasFirestoreUI(options, this);
            binding.recyclerView.setAdapter(adaptador);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerView.setHasFixedSize(true);

            adaptador.startListening();
            filtroActivo = "ninguno";

        } else{
            Query query = FirebaseFirestore.getInstance()
                    .collection("usuarios").document(usuario.getUid()).collection("prendas") //"Prueba para comprobar que funciona"
                    //.collection("Usuarios").document(usuario.getUid()).collection("prendas")
                    .limit(50).whereEqualTo("tipo", tipo);
            FirestoreRecyclerOptions<Prenda> options = new FirestoreRecyclerOptions
                    .Builder<Prenda>().setQuery(query, Prenda.class).build();
            adaptador = new AdaptadorPrendasFirestoreUI(options, this);
            binding.recyclerView.setAdapter(adaptador);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerView.setHasFixedSize(true);

            adaptador.startListening();
            filtroActivo=tipo;
        }
        //Toast.makeText(this, tipo + " " + filtroActivo, Toast.LENGTH_LONG).show(); //para comprobar que los ifs funcionan
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adaptador.stopListening();
    }

    public void volverLandingDesdeTodasP (View view) {
        Intent intent = new Intent(this, LandingPage_int1.class);
        startActivity(intent);
    }

    public void paginaCambioDatosDesdeTodasP (View view) {
        Intent intent = new Intent(this, CambioDatos_int1.class);
        startActivity(intent);
    }

    public void irPaginaPrenda (View view) {
        Intent intent = new Intent(this, PaginaPrenda_int1.class    );
        startActivity(intent);
    }

    public void abrirNuevaPrenda(View view) {
        Intent intent = new Intent(this, NuevaPrenda_int1.class    );
        startActivity(intent);
    }

    public void lanzarAcercaDe(View view){
        Intent i = new Intent(this, NoDisponibleActivity.class);
        startActivity(i);
    }


}
