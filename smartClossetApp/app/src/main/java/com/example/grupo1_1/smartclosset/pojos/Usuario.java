package com.example.grupo1_1.smartclosset.pojos;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class Usuario {
    /**
     * Clase Pojo del {@link Usuario} que estara usando la app
     */
    private String nombre;
    private String correo;
    private String urlFoto;
    private String talla;

    //--------------------------------------------------------- Constructores de la clase -----------------------
    public Usuario() {
    }

    public Usuario(FirebaseUser firebaseUser) {
        nombre = firebaseUser.getDisplayName();
        correo = firebaseUser.getEmail();
        if(firebaseUser.getPhotoUrl()!=null) {
            urlFoto = firebaseUser.getPhotoUrl().toString();
        }else {
            urlFoto = "";
        }
    }

    //---------------------------------------------------------------- CrearUsuario ------------------------------
    /**
     * Este metodo crea el documento del {@link Usuario} en Firestore Database
     *
     * @param usuario Objeto de la clase {@link Usuario} que contiene los datos del usuario que se guardaran
     * @param UserId Id del {@link Usuario} que esta utilizando la app en ese momento
     */
    public static void crearUsuario(Usuario usuario, String UserId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(UserId).set(usuario);
    }

    //---------------------------------------------------------------- ActualizarUsuario -------------------------
    /**
     * Este metodo actualiza el documento del {@link Usuario}. Usamos el {@link SetOptions}.merge()
     * para obligar a que no se guarden los datos del objeto {@link Usuario} que no se han modificado o estan vacios
     *
     * @param usuario Objeto de la clase {@link Usuario} que contiene los datos del usuario que actualizan
     * @param UserId Id del {@link Usuario} que esta utilizando la app en ese momento
     */
    public static void actualizarUsuario(Usuario usuario, String UserId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(UserId).set(usuario, SetOptions.merge());
    }

    // ----------------------------------------------------------------- getters y setters -------------------------

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public String getTalla() { return talla; }

    public void setTalla(String talla) { this.talla = talla; }

}

