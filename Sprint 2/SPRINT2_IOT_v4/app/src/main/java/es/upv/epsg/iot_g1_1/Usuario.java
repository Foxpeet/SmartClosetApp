package es.upv.epsg.iot_g1_1;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private String nombre;
    private String correo;
    private int telefono;
    private String urlFoto;
    private List<String> favoritos; //lista que contiene las IDs de las prendas favoritas del usuario
    private String talla;
    private double interfaz;
    private double inicioSesion;


    //--------------------------------------------------------- Constructores de la clase -----------------------
    // (puedes crear los que necesites, ej: un constructor con solo correo y telefono, etc)

    public Usuario() {
    }

    public Usuario(String nombre, String correo, int telefono, String urlFoto, List<String> favoritos, String talla) {
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.urlFoto = urlFoto;
        this.favoritos = favoritos;
        this.talla = talla;
    }

    public Usuario(FirebaseUser firebaseUser) {
        nombre = firebaseUser.getDisplayName();
        correo = firebaseUser.getEmail();
        if(firebaseUser.getPhotoUrl()!=null)
            urlFoto = firebaseUser.getPhotoUrl().toString();
        else
            urlFoto="";
        favoritos = new ArrayList<>();

    }

    //---------------------------------------------------------------- CrearUsuario ------------------------------
    /* crear un usuario con los datos dados

    Usuario usuario tiene los datos a guardar en el nuevo documento
    String UserId es para identificar el usuario ya que la clase Usuario no guarda la id

    **CUIDADO** si se usa para actualizar los datos de un documento y no para crearlo,
    borrara los campos que no esten definidos en este objeto, podria borrar las prendas, los conjuntos
    o los campos que no hubieran sido rellenados

    ***RECOMENDACION*** al crear un usuario, dadle todos los datos para que al actualizar ya tenga todos los campos creados y puedan ser manipulados
     */

    public static void crearUsuario(Usuario usuario, String UserId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(UserId).set(usuario);
    }

    //---------------------------------------------------------------- ActualizarUsuario -------------------------
    /* actualizar con el usuario dado

    Usuario usuario tiene los datos a cambiar en el documento
    String UserId es como el metodo anterior, para identificar el usuario
    el "SetOptions.merge()" sirve para ACTUALIZAR los datos, es decir, aunque no esten todos los datos
    en el objeto Usuario, los vacios no los borrara, simplemente
    los que no esten definidos o esten vacios no se actualizaran

    **CUIDADO** usar solo para actualizar, no sirve para crear un usuario nuevo
    y solo rellena los datos que han sido anteriormente creados con el metodo de crear

    ***RECOMENDACION*** al crear un usuario, dadle todos los datos para que al actualizar ya tenga todos los campos creados y puedan ser manipulados
     */

    public static void actualizarUsuario(Usuario usuario, String UserId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(UserId).set(usuario, SetOptions.merge());
    }

    //---------------------------------------------------------------- AnyadirFavorito -------------------------
    /* anyade un nuevo favorito a la lista

    String UserId es como el metodo anterior, para identificar el usuario
    el "SetOptions.merge()" sirve para ACTUALIZAR los datos, en este caso la lista de favoritos

    **CUIDADO** este metodo solo sirve para anyadir UN favorito, no crea ni actualiza
     */

    public static void anyadirFavorito(String UserId, String FavId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(UserId).get()
                .addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task){
                                if (task.isSuccessful()) {
                                    Usuario user = task.getResult().toObject(Usuario.class);
                                    List<String> favlist = user.getFavoritos();
                                    favlist.add(FavId);
                                    user.setFavoritos(favlist);
                                    db.collection("usuarios").document(UserId).set(user, SetOptions.merge());
                                } else {
                                    Log.e("Firebase", "Error…", task.getException());
                                }
                            }
                        });
    }

    //---------------------------------------------------------------- BorrarFavorito -------------------------
    /* elimina un favorito de la lista dada la id de la prenda a eliminar de la lista

    String UserId es como el metodo anterior, para identificar el usuario
    el "SetOptions.merge()" sirve para ACTUALIZAR los datos, en este caso la lista de favoritos

    **CUIDADO** este metodo solo sirve para borrar UN favorito dada la id, no crea ni actualiza
     */

    public static void borrarFavorito(String UserId, String FavId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(UserId).get()
                .addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task){
                                if (task.isSuccessful()) {
                                    Usuario user = task.getResult().toObject(Usuario.class);
                                    List<String> favlist = user.getFavoritos();
                                    favlist.remove(FavId);
                                    user.setFavoritos(favlist);
                                    db.collection("usuarios").document(UserId).set(user, SetOptions.merge());
                                } else {
                                    Log.e("Firebase", "Error…", task.getException());
                                }
                            }
                        });
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


    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }


    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }


    public List<String> getFavoritos() {
        return favoritos;
    }

    public void setFavoritos(List<String> favoritos) {
        this.favoritos = favoritos;
    }

    public String getTalla() { return talla; }

    public void setTalla(String talla) { this.talla = talla; }


    public double getInterfaz() {
        return interfaz;
    }

    public void setInterfaz(double interfaz) {
        this.interfaz = interfaz;
    }




}

