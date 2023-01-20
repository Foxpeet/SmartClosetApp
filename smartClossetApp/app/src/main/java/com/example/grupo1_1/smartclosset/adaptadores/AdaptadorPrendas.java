package com.example.grupo1_1.smartclosset.adaptadores;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grupo1_1.smartclosset.R;
import com.example.grupo1_1.smartclosset.databinding.ElementoListaBinding;
import com.example.grupo1_1.smartclosset.pojos.Prenda;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AdaptadorPrendas extends RecyclerView.Adapter<AdaptadorPrendas.ViewHolder> {

    ArrayList<Prenda> listaPrendas;
    private LayoutInflater inflater;
    private Context context;

    public AdaptadorPrendas(ArrayList<Prenda> listaPrendas, Context context) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.listaPrendas = listaPrendas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.elemento_lista, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.personaliza(listaPrendas.get(position));
    }

    @Override
    public int getItemCount() {
        return listaPrendas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nombre;
        public ImageView foto;

        public StorageReference storageRef;

        public ViewHolder(ElementoListaBinding itemView) {
            super(itemView.getRoot());
            nombre = itemView.nombreElementoLista;
            foto = itemView.fotoElementoLista;

            storageRef = FirebaseStorage.getInstance().getReference();
        }

        public ViewHolder(View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.nombre);
            foto = itemView.findViewById(R.id.foto);
        }

        public void personaliza(final Prenda prenda) {
            nombre.setText(prenda.getNombrePrenda());

            //para poner la imagen
            File localFile = null;
            try {
                localFile = File.createTempFile("image", "jpg"); //nombre y extensión
            } catch (IOException e) {
                e.printStackTrace(); //Si hay problemas detenemos la app
            }
            final String path = localFile.getAbsolutePath();
            Log.d("Fotos", "test: " + prenda.getUrlFoto());

            StorageReference ficheroRef = storageRef.child(prenda.getUrlFoto());
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
                            if(prenda.getTipo().equals("parte de arriba")){
                                foto.setImageResource(R.drawable.camiseta);
                                return;
                            }
                            if(prenda.getTipo().equals("parte de abajo")){
                                foto.setImageResource(R.drawable.icono_pantalones);
                                return;
                            }
                            if(prenda.getTipo().equals("accesorios")){
                                foto.setImageResource(R.drawable.bufanda);
                                return;
                            }
                        }
                    });
        }
    }
}