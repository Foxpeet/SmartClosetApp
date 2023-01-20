package com.example.grupo1_1.smartclosset.adaptadores;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grupo1_1.smartclosset.R;
import com.example.grupo1_1.smartclosset.databinding.ElementoListaConjuntoBinding;
import com.example.grupo1_1.smartclosset.pojos.Conjunto;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AdaptadorConjuntos extends RecyclerView.Adapter<AdaptadorConjuntos.ViewHolder> {

    ArrayList<Conjunto> listaConjuntos;
    private LayoutInflater inflater;
    private Context context;

    public AdaptadorConjuntos(ArrayList<Conjunto> listaConjuntos, Context context) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.listaConjuntos = listaConjuntos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.elemento_lista, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.personaliza(listaConjuntos.get(position));
    }

    @Override
    public int getItemCount() {
        return listaConjuntos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView fotoSuperior;
        public ImageView fotoInferior;

        public StorageReference storageRef;

        public FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        public ViewHolder(ElementoListaConjuntoBinding itemView) {
            super(itemView.getRoot());
            //nombre = itemView.nombreElementoLista;
            fotoSuperior = itemView.fotoElementoListaSuperior;
            fotoInferior = itemView.fotoElementoListaInferior;

            storageRef = FirebaseStorage.getInstance().getReference();
        }

        public ViewHolder(View itemView) {
            super(itemView);

            fotoSuperior = itemView.findViewById(R.id.fotoElementoListaSuperior);
            fotoInferior = itemView.findViewById(R.id.fotoElementoListaInferior);
        }

        public void personaliza(final Conjunto conjunto) {
            //para poner la imagen
            File localFile = null;
            File localFileInf = null;
            try {
                localFile = File.createTempFile("image", "jpg"); //nombre y extensión
                localFileInf = File.createTempFile("image", "jpg"); //nombre y extensión
            } catch (IOException e) {
                e.printStackTrace(); //Si hay problemas detenemos la app
            }
            final String path = localFile.getAbsolutePath();
            final String pathInf = localFileInf.getAbsolutePath();

            //foto parte de arriba
            StorageReference ficheroRef = storageRef.child("Prendas/" + usuario.getUid() + "/" + conjunto.getParteArribaId());
            ficheroRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>(){
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot){
                            Log.d("Almacenamiento", "Fichero bajado" + path);
                            fotoSuperior.setImageBitmap(BitmapFactory.decodeFile(path));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.e("Almacenamiento", "ERROR: bajando fichero superior" + conjunto.getParteArribaId());
                            fotoSuperior.setImageResource(R.drawable.camiseta);
                        }
                    });
            //foto parte de abajo
            StorageReference ficheroRefInf = storageRef.child("Prendas/" + usuario.getUid() + "/" + conjunto.getParteAbajoId());
            ficheroRefInf.getFile(localFileInf)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>(){
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot){
                            Log.d("Almacenamiento", "Fichero bajado" + pathInf);
                            fotoInferior.setImageBitmap(BitmapFactory.decodeFile(pathInf));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.e("Almacenamiento", "ERROR: bajando fichero Inferior");
                            fotoInferior.setImageResource(R.drawable.icono_pantalones);
                        }
                    });
        }
    }
}