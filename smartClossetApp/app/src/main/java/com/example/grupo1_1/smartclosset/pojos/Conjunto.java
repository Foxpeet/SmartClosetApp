package com.example.grupo1_1.smartclosset.pojos;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class Conjunto {
    /**
     * Este objeto {@link Conjunto} contiene todos estos datos donde "[algo]Id"
     *  corresponde con la Id de documento de una {@link Prenda} de Firestore Database
     */
    private String id;
    private String ParteArribaId;
    private String ParteAbajoId;

    //Constructores -----------------------------------------------------------------------------------------------------
    public Conjunto(){
    }

    // crar conjunto-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    /**
     * Este metodo recibe el objeto {@link Conjunto} y el String "UserId", utiliza el "UserId" para
     * la ruta de Firestore Database donde se guardara este objeto en
     * [ usuarios->UserId->conjuntos-> ].
     *
     * Despues de crearse un documento de conjunto es imposible obtener su id,
     * asi que se le adjunta un {@link OnCompleteListener} para que cuando termine
     * obtenga esa id y la guarde dentro del propio objeto {@link Conjunto}.
     * Una vez guardada la id en el objeto se llama a {@link #actualizarConjunto(Conjunto, String)} para que actualice el
     * objeto que se acaba de crear y le añada la id que se acaba de obtener
     *
     * @param conjunto Objeto de tipo {@link Conjunto} con los datos que se subiran a Firebstore Database
     * @param UserId Id de Firebase del usuario actual que esta usando la app
     */
    public static void crearConjunto(Conjunto conjunto, String UserId){
        final String[] idResult = {"null"};
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(UserId).collection("conjuntos").add(conjunto).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    DocumentReference document = task.getResult();
                    if (document != null) {
                        String id = document.getId();
                        //Se accede mediante un array para evitar posibles errores de tipos
                        idResult[0] = id;
                        conjunto.setId(idResult[0]);
                        actualizarConjunto(conjunto, UserId);
                    }
                }
            }
        });
    }

    // actualizar conjunto -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    /**
     * Este metodo actualiza el Firestore Database con el {@link Conjunto}.
     * Se usa el {@link SetOptions}.merge() para evitar reescribir los datos que en
     * el objeto {@link Conjunto} no se hayan rellenado o permanezcan iguales
     *
     * @param conjunto Objeto de tipo {@link Conjunto} que contiene los datos a actualizar
     * @param UserId Id de Firebase
     */
    public static void actualizarConjunto(Conjunto conjunto, String UserId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(UserId).collection("conjuntos").document(conjunto.getId()).set(conjunto, SetOptions.merge());
    }

    // getters y setters ---------------------------------------------------------------------------------------------------
    public String getParteArribaId() {
        return ParteArribaId;
    }

    public void setParteArribaId(String parteArribaId) {
        ParteArribaId = parteArribaId;
    }

    public String getParteAbajoId() {
        return ParteAbajoId;
    }

    public void setParteAbajoId(String parteAbajoId) {
        ParteAbajoId = parteAbajoId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}