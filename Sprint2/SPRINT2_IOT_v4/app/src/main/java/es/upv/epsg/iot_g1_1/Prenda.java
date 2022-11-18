package es.upv.epsg.iot_g1_1;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.List;

public class Prenda {
    private String nombrePrenda;
    private String talla;
    private String tipo;
    private String color;
    private String urlFoto;
    private List<Long> contadorUsos; //lista que contiene los usos de la prenda con sus fechas
    private String estacionDeUso;
    private String fechaCompra;

    //--------------------------------------------------------- Constructores de la clase -----------------------
    // (puedes crear los que necesites, ej: un constructor con solo talla y nombrePrenda, etc)

    public Prenda() {
    }

    public Prenda(String nombrePrenda, String talla, String tipo, String color, String urlFoto, List<Long> contadorUsos, String estacionDeUso, String fechaCompra) {
        this.nombrePrenda = nombrePrenda;
        this.talla = talla;
        this.tipo = tipo;
        this.color = color;
        this.urlFoto = urlFoto;
        this.contadorUsos = contadorUsos;
        this.estacionDeUso = estacionDeUso;
        this.fechaCompra = fechaCompra;
    }


    //---------------------------------------------------------------- CrearPrenda ------------------------------
    /* crear un documento prenda con los datos dados

    Prenda prenda tiene los datos a guardar en el nuevo documento
    String UserId es para identificar el usuario ya que la clase Prenda no guarda la id del usuario
    el metodo add añade el objeto a la coleccion con un nombre de id automatico, se puede acceder a este id
    con el metodo .getId() sobre el documento (o eso dice la documentacion xd)

    **CUIDADO** si se usa para actualizar los datos de un documento y no para crearlo,
    borrara los campos que no esten definidos en este objeto, podria borrar los campos que no hubieran sido rellenados

    ***RECOMENDACION*** al crear una prenda, dadle todos los datos para que al actualizar ya tenga todos los campos creados y puedan ser manipulados
     */

    public static void crearPrenda(Prenda prenda, String UserId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(UserId).collection("prendas").add(prenda);
    }

    //---------------------------------------------------------------- ActualizarPrenda ------------------------------
    /* actualiza un docuemnto prenda con los datos dados

    Prenda prenda tiene los datos a guardar en el documento
    String UserId es para identificar el usuario ya que la clase Prenda no guarda la id del usuario
    String PrendaId contiene la id del documento de la prenda que queremos actualizar (se obtiene con .getId() sobre el documento)
    el "SetOptions.merge()" es para ue solo se sobreescribasn los campos no nulos

    **CUIDADO** si se usa para crear un documento y no para actualizarlo,
    solo rellenara los datos de aquellos campos que hayan sido creados antes con el metodo de crear

    ***RECOMENDACION*** al crear una prenda, dadle todos los datos para que al actualizar ya tenga todos los campos creados y puedan ser manipulados
     */

    public static void actualizarPrenda(Prenda prenda, String PrendaId, String UserId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(UserId).collection("prendas").document(PrendaId).set(prenda, SetOptions.merge());
    }

    //---------------------------------------------------------------- BorrarPrenda ------------------------------
    /* elimina el documento prenda con la id dada

    PrendaId es la id del documento prenda a eliminar
    String UserId es para identificar el usuario ya que la clase Prenda no guarda la id del usuario

    **CUIDADO** borrara el documento entero, pero si tuviera una coleccion o subcoleccion no la borra
    y solo se puede llamar una vez, no hay forma de recuperar los datos borrados con este metodo
     */

    public static void borrarPrenda(String UserId, String PrendaId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(UserId).collection("prendas").document(PrendaId).delete();
    }

    //---------------------------------------------------------------- AnyadirUso -------------------------
    /* anyade un nuevo uso/fecha a la lista guardando el momento en el que se llama a la funcion en milisegundos y siendo la posicion en la lista la cantidad de usos

    String UserId es como el metodo anterior, para identificar el usuario
    el "SetOptions.merge()" sirve para ACTUALIZAR los datos, en este caso la lista de favoritos

    **CUIDADO** este metodo solo sirve para anyadir UN uso, no crea ni actualiza
     */

    public static void anyadirUso(String UserId, String PrendaId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(UserId).collection("prendas").document(PrendaId).get()
                .addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task){
                                if (task.isSuccessful()) {
                                    Prenda prenda = task.getResult().toObject(Prenda.class);
                                    List<Long> contUsos = prenda.getContadorUsos();
                                    contUsos.add(System.currentTimeMillis());
                                    prenda.setContadorUsos(contUsos);
                                    db.collection("usuarios").document(UserId).collection("prendas").document(PrendaId).set(prenda, SetOptions.merge());
                                } else {
                                    Log.e("Firebase", "Error…", task.getException());
                                }
                            }
                        });
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


    public List<Long> getContadorUsos() { return contadorUsos; }

    public void setContadorUsos(List<Long> contadorUsos) { this.contadorUsos = contadorUsos; }


    public String getEstacionDeUso() { return estacionDeUso; }

    public void setEstacionDeUso(String estacionDeUso) { this.estacionDeUso = estacionDeUso; }

    public String getFechaCompra() {return fechaCompra;}

    public void setFechaCompra(String fechaCompra) {this.fechaCompra = fechaCompra;}
}