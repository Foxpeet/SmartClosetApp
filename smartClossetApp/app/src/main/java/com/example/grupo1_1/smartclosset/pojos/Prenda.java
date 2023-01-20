package com.example.grupo1_1.smartclosset.pojos;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class Prenda implements Serializable {
    private String nombrePrenda;
    private String idRFID;
    private String talla;
    private String tipo;
    private String color;
    private String urlFoto;
    private boolean favoritos;
    private long contadorUsos = 0;
    private String estacionDeUso;
    private String fechaCompra;
    private String docId;
    private boolean limpieza;

    //--------------------------------------------------------- Constructores de la clase -----------------------
    public Prenda() {
    }

    // constructor a partir de una lista Map, hace falta para el algoritmo del rfid
    public Prenda(HashMap<String, Object> prendaMap) {
        this.idRFID = (String) prendaMap.get("idRFID");
        this.nombrePrenda = (String) prendaMap.get("nombrePrenda");
        this.urlFoto = (String) prendaMap.get("urlFoto");
        this.favoritos = (Boolean) prendaMap.get("favoritos");
        this.estacionDeUso = (String) prendaMap.get("estacionDeUso");
        this.fechaCompra = (String) prendaMap.get("fechaCompra");
        this.limpieza = (Boolean) prendaMap.get("limpieza");
        this.talla = (String) prendaMap.get("talla");
        this.tipo = (String) prendaMap.get("tipo");
        this.color = (String) prendaMap.get("color");
        this.contadorUsos = (Long) prendaMap.get("contadorUsos");
        this.docId = (String) prendaMap.get("docId");
    }

    //---------------------------------------------------------------- CrearPrenda ------------------------------
    /**
     * Este metodo guarda la informacion guardada en el objeto {@link Prenda} para subirlo a Firestore Database
     * con "UserId".
     * Tras crear el documento de la prenda en Firebase, es imposible obtener su id,
     * así que se adjunta un {@link OnCompleteListener} para que, una vez creado el documento, nos dé
     * automaticamente la id y actualizamos ese mismo documento con {@link #actualizarPrenda(Prenda, String, String)}
     * para subir la id y tenerla almacenada en Firestore Database
     *
     * @param prenda Objeto de tipo {@link Prenda} que contiene los datos a guardar
     * @param UserId Id del usuario que esta, en ese momento, usando la app
     * @param uriData la imagen de la prenda en tipo {@link Uri}
     */
    public static void crearPrenda(Prenda prenda, String UserId, Uri uriData){
        final String[] idResult = {"null"};
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        prenda.setUrlFoto("r");
        db.collection("usuarios").document(UserId).collection("prendas").add(prenda).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    DocumentReference document = task.getResult();
                    if (document != null) {
                        String id = document.getId();
                        //se usa un array para evitar posibles errores de tipo
                        idResult[0] = id;
                        prenda.setDocId(idResult[0]);
                        prenda.setUrlFoto("Prendas/" + UserId + "/" + id);
                        if(uriData == null){
                            actualizarPrenda(prenda, idResult[0], UserId);
                        } else{
                            StorageReference ficheroRef = storageRef.child(prenda.getUrlFoto());
                            ficheroRef.putFile(uriData);
                            actualizarPrenda(prenda, idResult[0], UserId);
                        }
                    }
                }
            }
        });
    }

    //---------------------------------------------------------------- ActualizarPrenda ------------------------------
    /**
     * Este metodo actualiza el documento de la {@link Prenda}, al usar {@link SetOptions}.merge()
     * obligamos a que se guarden solo los campos de la {@link Prenda} que han sido alterados
     * o son nulos
     *
     * @param prenda Objeto de tipo {@link Prenda} que contiene los datos a guardar
     * @param PrendaId Id del documento de la {@link Prenda} que se pretende actualizar
     * @param UserId Id del usuario que esta, en ese momento, usando la app
     */
    public static void actualizarPrenda(Prenda prenda, String PrendaId, String UserId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(UserId).collection("prendas").document(PrendaId).set(prenda, SetOptions.merge());
    }

    public static void actualizarPrenda(Prenda prenda, String PrendaId, String UserId, Uri uriData){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        db.collection("usuarios").document(UserId).collection("prendas").document(PrendaId).set(prenda, SetOptions.merge());
        if(uriData == null){
            actualizarPrenda(prenda, PrendaId, UserId);
        } else{
            StorageReference ficheroRef = storageRef.child(prenda.getUrlFoto());
            ficheroRef.putFile(uriData);
            actualizarPrenda(prenda, PrendaId, UserId);
        }
    }

    //---------------------------------------------------------------- BorrarPrenda ------------------------------
    /**
     * Este metodo borra el documento y la informacion de la {@link Prenda} con la id "PrendaId"
     *
     * @param UserId Id del usuario que esta, en ese momento, usando la app
     * @param PrendaId Id del documento de la {@link Prenda} a borrar
     */
    public static void borrarPrenda(String UserId, String PrendaId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(UserId).collection("prendas").document(PrendaId).delete();
    }

    //--------------------------------------------------------------- Getters y Setters --------------------------
    public String getNombrePrenda() { return nombrePrenda; }

    public void setNombrePrenda(String nombrePrenda) { this.nombrePrenda = nombrePrenda; }

    public String getTalla() { return talla; }

    public void setTalla(String talla) { this.talla = talla; }

    public String getTipo() { return tipo; }

    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getColor() { return color; }

    public void setColor(String color) { this.color = color; }

    public String getUrlFoto() { return urlFoto; }

    public void setUrlFoto(String urlFoto) { this.urlFoto = urlFoto; }

    public boolean getFavoritos() {
        return favoritos;
    }

    public void setFavoritos(boolean favoritos) {
        this.favoritos = favoritos;
    }

    public long getContadorUsos() { return contadorUsos; }

    public void setContadorUsos(long contadorUsos) { this.contadorUsos = contadorUsos; }

    public String getEstacionDeUso() { return estacionDeUso; }

    public void setEstacionDeUso(String estacionDeUso) { this.estacionDeUso = estacionDeUso; }

    public String getFechaCompra() {return fechaCompra;}

    public void setFechaCompra(String fechaCompra) {this.fechaCompra = fechaCompra;}

    public String getDocId() {return docId;}

    public void setDocId(String docId) {this.docId = docId;}

    public String getIdRFID() {
        return idRFID;
    }

    public void setIdRFID(String idRFID) { this.idRFID = idRFID; }

    public boolean isLimpieza() { return limpieza; }

    public void setLimpieza(boolean limpieza) { this.limpieza = limpieza; }
}