package es.upv.epsg.iot_g1_1.Int1;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import es.upv.epsg.iot_g1_1.Prenda;
import es.upv.epsg.iot_g1_1.R;
import es.upv.epsg.iot_g1_1.Usuario;

public class PaginaPrenda_int1 extends AppCompatActivity {

    FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
    Prenda ropa = new Prenda();

    ImageView cero = findViewById(R.id.copito0);
    ImageView uno = findViewById(R.id.copito1);
    ImageView dos = findViewById(R.id.copito2);
    ImageView tres = findViewById(R.id.copito3);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //función para quitar la Action Bar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_prenda);

        cero.setAlpha(0.0f);
        uno.setAlpha(0.0f);
        dos.setAlpha(0.0f);
        tres.setAlpha(0.0f);

        /*FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(usuario.getUid()).collection("prendas").whereEqualTo("nombrePrenda", ).get()
                .addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task){
                                if (task.isSuccessful()) {
                                    String interr = task.getResult().getString("estacionDeUso");
                                    if(interr == "muy calido") {
                                        cero.setAlpha(1.0f);
                                    }
                                    if(interr == "calido") {
                                        uno.setAlpha(1.0f);
                                    }
                                    if(interr == "frio") {
                                        dos.setAlpha(1.0f);
                                    }
                                    if(interr == "muy frio") {
                                        tres.setAlpha(1.0f);
                                    }
                                } else {
                                    Log.e("Firebase", "Error…", task.getException());
                                }
                            }
                        }); */

    }




}
