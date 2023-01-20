package com.example.grupo1_1.smartclosset.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.grupo1_1.smartclosset.R;
import com.example.grupo1_1.smartclosset.pojos.Prenda;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class PaginaPrenda extends AppCompatActivity {

    FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
    public StorageReference storageRef;
    public File localFile;
    public StorageReference ficheroRef;

    ProgressBar bar;
    ImageView btn1;
    ImageView btn2;
    ImageView btn3;

    public ImageView foto;
    TextView texto;
    TextView dia;
    TextView mes;
    TextView anyo;
    TextView nombrePrenda;
    TextView viewTalla;
    ImageView viewCalidez;
    ImageView color;
    ImageView nofavoritos;
    ImageView favoritos;
    ImageView lavadora;
    ImageView color1;
    ImageView color4;
    ImageView color3;
    ImageView color5;
    ImageView ajustes;

    ImageView botonInfo;

    //prenda con la que estamos trabajando y que queremos mostrar
    Prenda prendaActual;
    public Uri uriData;

    // lo usamos para comprobar si el usuario viene del detector RFID o de otra página
    String procedencia = null;

    long usosRestantes;

    // para la animación del menú de información
    View menuPrenda;
    ImageView btnDesplegable;
    boolean desplegado = false;
    ImageView fondoNegro;
    ImageView rectangulo;
    ImageView recuadroLavadora;
    ImageView recuadroFav;

    TextView textoCombina;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //función para quitar la Action Bar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_prenda);

        foto = findViewById(R.id.fotoPrenda);
        ajustes = findViewById(R.id.ajustesPrenda);

        botonInfo = findViewById(R.id.botonDesplegablePrenda);

        rectangulo = findViewById(R.id.rectanguloCircularPrendas);
        recuadroLavadora = findViewById(R.id.recuadroLavadoras);
        recuadroFav = findViewById(R.id.recuadrofavoritos);
        menuPrenda = findViewById(R.id.menu_info_prenda);
        menuPrenda.setTranslationY(470);
        btnDesplegable = findViewById(R.id.botonDesplegablePrenda);
        btnDesplegable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(desplegado) {
                    plegarMenuInfoPrenda();
                }
                else {
                    desplegarMenuInfoPrenda();
                }
            }
        });

        //mediate el intent para abrir la página, obtendremos el objeto
        prendaActual = (Prenda) getIntent().getSerializableExtra("prenda");

        // intent para ver de donde viene el usuario
        // si viene desde el rfid se tendrá que producir el pop up
        procedencia = (String) getIntent().getSerializableExtra("place");
        if(procedencia!=null) {
            Log.i("MQTT", "ha entrado en el if");
            determinarLimpieza();
        }

        if(getIntent().getSerializableExtra("FotoData") != null){
            uriData = Uri.parse(getIntent().getStringExtra("FotoData"));
        }

        try {
            localFile = File.createTempFile("image", "jpg"); //nombre y extensión
        } catch (IOException e) {
            e.printStackTrace(); //Si hay problemas detenemos la app
        }
        final String path = localFile.getAbsolutePath();

        storageRef = FirebaseStorage.getInstance().getReference();
        ficheroRef = storageRef.child(prendaActual.getUrlFoto());

        descargarYMostrarFoto(path);
        //progessbar
        bar = (ProgressBar) findViewById(R.id.progressBarPrendas);
        //ahora le damos un valor fijo, pero más adelante cogerá ese valor de la
        //base de datos en función del RDIF
        bar.setProgress(20);

        //declaramos los botones del menú desplegable animado
        btn1 = findViewById(R.id.btn1menu);
        btn1.setVisibility(View.INVISIBLE);
        btn2 = findViewById(R.id.btn2menu);
        btn2.setVisibility(View.INVISIBLE);
        btn3 = findViewById(R.id.btn3menu);
        btn3.setVisibility(View.INVISIBLE);

        // Ahora mismo es un valor fijo -------------------------------------------
        //cogemos la lista de usos para obtener el total de usos y hacer el cálculo
        //hacemos la resta para ver los usos restantes
        usosRestantes = 30 - prendaActual.getContadorUsos();
        //progessbar
        bar = findViewById(R.id.progressBarPrendas);
        bar.setProgress((int) prendaActual.getContadorUsos());

        //añadimos el texto para controlar cuantos usos le quedan a la prenda
        texto = findViewById(R.id.usosSostenibilidadPrenda2);
        texto.setText(usosRestantes + getResources().getString(R.string.PaginaPrenda_usosSostenible));

        textoCombina = findViewById(R.id.combinaPrendas);

        //añadimos el texto de la fecha
        dia = findViewById(R.id.diaPrenda);
        mes = findViewById(R.id.mesPrenda);
        anyo = findViewById(R.id.anyoPrenda);

        //Convertimos la fecha en una lista para poder jugar con ella
        String a = prendaActual.getFechaCompra();
        char[] caracteres = a.toCharArray();

        //Para indicar el dia
        String b = String.valueOf(caracteres[0]);
        String c = String.valueOf(caracteres[1]);
        String d= b+c;
        dia.setText(d);

        // Para indicar el mes
        String f = String.valueOf(caracteres[3]);
        String e =String.valueOf(caracteres[4]);
        String g = f+e;

        switch (g) {
            case "01":
                mes.setText("JAN");
                break;
            case "02":
                mes.setText("FEB");
                break;
            case "03":
                mes.setText("MAR");
                break;
            case "04":
                mes.setText("APR");
                break;
            case "05":
                mes.setText("MAY");
                break;
            case "06":
                mes.setText("JUN");
                break;
            case "07":
                mes.setText("JUL");
                break;
            case "08":
                mes.setText("AG");
                break;
            case "09":
                mes.setText("SEP");
                break;
            case "10":
                mes.setText("OCT");
                break;
            case "11":
                mes.setText("NOV");
                break;
            case "12":
                mes.setText("DIC");
                break;
        }

        //Para indicar el año
        String h = String.valueOf(caracteres[6]);
        String i =String.valueOf(caracteres[7]);
        String j = String.valueOf(caracteres[8]);
        String k = String.valueOf(caracteres[9]);
        String l =h+i+j+k;

        anyo.setText(l);

        //Para indicar el color
        color= findViewById(R.id.colorPrenda);
        String color2 = prendaActual.getColor();

        switch (color2) {
            case "Rojo":
                color.getBackground().setColorFilter(Color.rgb(184, 55, 39), PorterDuff.Mode.SRC_IN);
                break;
            case "Azul":
                color.getBackground().setColorFilter(Color.rgb(85, 131, 237), PorterDuff.Mode.SRC_IN);
                break;
            case "Amarillo":
                color.getBackground().setColorFilter(Color.rgb(237, 222, 85), PorterDuff.Mode.SRC_IN);
                break;
            case "Verde":
                color.getBackground().setColorFilter(Color.rgb(20, 110, 54), PorterDuff.Mode.SRC_IN);
                break;
            case "Naranja":
                color.getBackground().setColorFilter(Color.rgb(242, 149, 68), PorterDuff.Mode.SRC_IN);
                break;
            case "Violeta":
                color.getBackground().setColorFilter(Color.rgb(190, 68, 242), PorterDuff.Mode.SRC_IN);
                break;
            case "Gris":
                color.getBackground().setColorFilter(Color.rgb(178, 177, 179), PorterDuff.Mode.SRC_IN);
                break;
            case "Beige":
                color.getBackground().setColorFilter(Color.rgb(236, 226, 198), PorterDuff.Mode.SRC_IN);
                break;
            case "Blanco":
                color.getBackground().setColorFilter(Color.rgb(255, 255, 255), PorterDuff.Mode.SRC_IN);
                break;
            case "Negro":
                color.getBackground().setColorFilter(Color.rgb(0, 0, 0), PorterDuff.Mode.SRC_IN);
                break;
        }

        //colores recomendados
        color1= findViewById(R.id.color1Prenda);
        color1.getBackground().setColorFilter(Color.rgb(255, 255, 255), PorterDuff.Mode.SRC_IN);

        color4 = findViewById(R.id.color2Prenda);
        color4.getBackground().setColorFilter(Color.rgb(0, 0, 0), PorterDuff.Mode.SRC_IN);

        color3 =findViewById(R.id.color3Prenda);
        color5 = findViewById(R.id.color4Prenda);

        switch (color2) {
            case "Rojo":
                color3.getBackground().setColorFilter(Color.rgb(50, 168, 102), PorterDuff.Mode.SRC_IN);
                color5.getBackground().setColorFilter(Color.rgb(178, 177, 179), PorterDuff.Mode.SRC_IN);
                break;
            case "Azul":
                color3.getBackground().setColorFilter(Color.rgb(234, 242, 126), PorterDuff.Mode.SRC_IN);
                color5.getBackground().setColorFilter(Color.rgb(250, 165, 247), PorterDuff.Mode.SRC_IN);
                break;
            case "Amarillo":
                color3.getBackground().setColorFilter(Color.rgb(168, 206, 255), PorterDuff.Mode.SRC_IN);
                color5.getBackground().setColorFilter(Color.rgb(168, 255, 174), PorterDuff.Mode.SRC_IN);
                break;
            case "Verde":
                color3.getBackground().setColorFilter(Color.rgb(250, 187, 237), PorterDuff.Mode.SRC_IN);
                color5.getBackground().setColorFilter(Color.rgb(173, 28, 42), PorterDuff.Mode.SRC_IN);
                break;
            case "Naranja":
                color3.getBackground().setColorFilter(Color.rgb(236, 226, 198), PorterDuff.Mode.SRC_IN);
                color5.getBackground().setColorFilter(Color.rgb(239, 184, 16), PorterDuff.Mode.SRC_IN);
                break;
            case "Violeta":
                color3.getBackground().setColorFilter(Color.rgb(172, 204, 252), PorterDuff.Mode.SRC_IN);
                color5.getBackground().setColorFilter(Color.rgb(189, 189, 189), PorterDuff.Mode.SRC_IN);
                break;
            case "Gris":
                color3.getBackground().setColorFilter(Color.rgb(215, 159, 237), PorterDuff.Mode.SRC_IN);
                color5.getBackground().setColorFilter(Color.rgb(184, 29, 55), PorterDuff.Mode.SRC_IN);
                break;
            case "Beige":
                color3.getBackground().setColorFilter(Color.rgb(240, 177, 235), PorterDuff.Mode.SRC_IN);
                color5.getBackground().setColorFilter(Color.rgb(173, 28, 42), PorterDuff.Mode.SRC_IN);
                break;
            case "Blanco":
            case "Negro":
                color1.getBackground().setColorFilter(Color.rgb(173, 28, 42), PorterDuff.Mode.SRC_IN);
                color3.getBackground().setColorFilter(Color.rgb(242, 148, 223), PorterDuff.Mode.SRC_IN);
                color4.getBackground().setColorFilter(Color.rgb(103, 187, 240), PorterDuff.Mode.SRC_IN);
                color5.getBackground().setColorFilter(Color.rgb(247, 238, 101), PorterDuff.Mode.SRC_IN);
                break;
        }

        //le doy el valor al textView del nombre de la prenda para que no ponga algo random
        nombrePrenda = findViewById(R.id.nombrePrenda);
        nombrePrenda.setText(prendaActual.getNombrePrenda());

        //para saber si está en favoritos o no
        favoritos = findViewById(R.id.favoritosPrendas);
        nofavoritos = findViewById(R.id.nofavoritosPrenda);

        if(!prendaActual.getFavoritos()){
            favoritos.setVisibility(View.INVISIBLE);
            nofavoritos.setVisibility(View.VISIBLE);
        }else{
            favoritos.setVisibility(View.VISIBLE);
            nofavoritos.setVisibility(View.VISIBLE);
        }

        //para saber si tiene RFID o no
        lavadora=findViewById(R.id.lavadoraPrenda);

        if(prendaActual.isLimpieza()){
            lavadora.getDrawable().setColorFilter(Color.rgb(9, 153, 6), PorterDuff.Mode.SRC_IN);
        }else{
            lavadora.getDrawable().setColorFilter(Color.rgb(189, 23, 23), PorterDuff.Mode.SRC_IN);
        }

        //primero el círculo de la talla
        viewTalla = findViewById(R.id.tallaPrenda); //esta id está cambiada en el layout, pero si lo modifico ahí se descuadra todo
        viewTalla.setText(prendaActual.getTalla());

        //ahora vamos con el círculo de la calidez
        viewCalidez = findViewById(R.id.calidezPrenda);
        //la función copos() coloca el drawable en función de la calidez que tenga marcada la prenda
        copos();

        //les voy a bajar a todos la opacidad
        viewTalla.getBackground().setAlpha(160);
        viewCalidez.getBackground().setAlpha(160);

        lavadora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PaginaPrenda.this);
                builder.setTitle(getResources().getString(R.string.PaginaPrenda_info_titulo))
                        .setMessage( "Su prenda se encuentra: " + prendaSucia())
                        .show();
            }
        });


        cerrarMenuPrenda();

        // menu inicialmente plegado
        texto.setAlpha(0.0f);
        nofavoritos.setAlpha(0.0f);
        favoritos.setAlpha(0.0f);
        lavadora.setAlpha(0.0f);
        color1.setAlpha(0.0f);
        color4.setAlpha(0.0f);
        color3.setAlpha(0.0f);
        color5.setAlpha(0.0f);
        ajustes.setAlpha(0.0f);
        bar.setAlpha(0.0f);
        textoCombina.setAlpha(0.0f);
        recuadroFav.setAlpha(0.0f);
        recuadroLavadora.setAlpha(0.0f);
    }

    //función para determinar los copos de la calidez
    public void copos() {
        switch (prendaActual.getEstacionDeUso()) {
            case "muy calido":
                //este if coloca "cero" copos
                viewCalidez.setImageResource(R.drawable.icono_sol);
                break;
            case "calido":
                //este if coloca 1 copo
                viewCalidez.setImageResource(R.drawable.copito_uno);
                break;
            case "frio":
                //este if coloca dos copos
                viewCalidez.setImageResource(R.drawable.copito_two);
                break;
            case "muy frio":
                //este último coloca los tres copos
                viewCalidez.setImageResource(R.drawable.copito_three);
                break;
        }
    }
    public String prendaSucia(){
        String a = "";
        if(prendaActual.isLimpieza()){
            a= "limpia";
        }else{
            a="sucia";
        }
        return a;
    }

    public void descargarYMostrarFoto(String path){
        if(uriData != null){
            foto.setImageURI(uriData);
        } else{
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
                            if(prendaActual.getTipo().equals("parte de arriba")){
                                foto.setImageResource(R.drawable.camiseta);
                                return;
                            }
                            if(prendaActual.getTipo().equals("parte de abajo")){
                                foto.setImageResource(R.drawable.icono_pantalones);
                                return;
                            }
                            if(prendaActual.getTipo().equals("accesorios")){
                                foto.setImageResource(R.drawable.bufanda);
                            }
                        }
                    });
        }
    }

    //función para producir la animación y desplegar el menú
    public void abrirMenuPrenda(View view) {
        ajustes.animate().rotation(-90).setDuration(100);
        btn1.setVisibility(View.VISIBLE);
        btn2.setVisibility(View.VISIBLE);
        btn3.setVisibility(View.VISIBLE);
        btn1.animate().alpha(1).rotation(90).scaleX(2).scaleY(2).setDuration(100);
        ajustes.setVisibility(View.INVISIBLE);
        btn1.getBackground().setAlpha(0);
        btn2.animate().alpha(1).translationX(0).translationY(200).scaleX(2).scaleY(2).setDuration(400);
        btn3.animate().alpha(1).translationX(0).translationY(400).scaleX(2).scaleY(2).setDuration(400);
        btn1.setEnabled(true);
        btn2.setEnabled(true);
        btn3.setEnabled(true);
        // cerramos el menú con el btn1 solo cuando está desplegado
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cerrarMenuPrenda();
            }
        });
        // abrimos editar una prenda con el btn2 solo cuando está desplegado
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirPaginaEditarPrenda();
            }
        });
        // borramos una prenda con el btn3 solo cuando está desplegado
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PaginaPrenda.this);
                builder.setTitle(getResources().getString(R.string.PaginaPrenda_cancelar_titulo))
                        .setMessage(getResources().getString(R.string.PaginaPrenda_cancelar_mensaje))
                        .setPositiveButton(getResources().getString(R.string.Comunes_guardar), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Prenda.borrarPrenda(usuario.getUid(), prendaActual.getDocId());
                                onBackPressed();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.Comunes_cancelar), null)
                        .show();
            }
        });
    }

    public void cerrarMenuPrenda() {
        btn1.animate().alpha(0).rotation(-90).translationX(0).translationY(0).scaleX(1).scaleY(1).setDuration(400);
        btn2.animate().alpha(0).translationX(0).translationY(0).scaleX(1).scaleY(1).setDuration(400);
        btn3.animate().alpha(0).translationX(0).translationY(0).scaleX(1).scaleY(1).setDuration(400);
        btn1.setEnabled(false);
        btn2.setEnabled(false);
        btn3.setEnabled(false);
        ajustes.animate().rotation(90).setDuration(100);
        ajustes.setVisibility(View.VISIBLE);
    }

    public void lanzarInformacion(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(PaginaPrenda.this);
        builder.setTitle(getResources().getString(R.string.PaginaPrenda_info_titulo))
                .setMessage(getResources().getString(R.string.PaginaPrenda_info_calidez_mensaje) + "\r\n" + prendaActual.getEstacionDeUso())
                .show();
    }

    public void lanzarInfo2(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(PaginaPrenda.this);
        builder.setTitle(getResources().getString(R.string.PaginaPrenda_info_titulo))
                .setMessage(getResources().getString(R.string.PaginaPrenda_info_fecha_mensaje))
                .show();
    }

    public void lanzarfavs(View view){
        if(prendaActual.getFavoritos()==false){
            AlertDialog.Builder builder = new AlertDialog.Builder(PaginaPrenda.this);
            builder.setTitle(getResources().getString(R.string.PaginaPrenda_fav_titulo))
                    .setMessage(getResources().getString(R.string.PaginaPrenda_fav_mensaje_anyadir))
                    .setPositiveButton(getResources().getString(R.string.PaginaPrenda_aceptar), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            prendaActual.setFavoritos(true);
                            nofavoritos.setVisibility(View.INVISIBLE);
                            favoritos.setVisibility(View.VISIBLE);
                            Prenda.actualizarPrenda(prendaActual, prendaActual.getDocId(), usuario.getUid());
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.Comunes_cancelar), null)
                    .show();
        }else if(prendaActual.getFavoritos()==true){
            AlertDialog.Builder builder = new AlertDialog.Builder(PaginaPrenda.this);
            builder.setTitle(getResources().getString(R.string.PaginaPrenda_fav_titulo))
                    .setMessage(getResources().getString(R.string.PaginaPrenda_fav_mensaje_borrar))
                    .setPositiveButton(getResources().getString(R.string.PaginaPrenda_aceptar), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            prendaActual.setFavoritos(false);
                            favoritos.setVisibility(View.INVISIBLE);
                            nofavoritos.setVisibility(View.VISIBLE);
                            Prenda.actualizarPrenda(prendaActual, prendaActual.getDocId(), usuario.getUid());
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.Comunes_cancelar), null)
                    .show();
        }
    }

    public void abrirPaginaEditarPrenda() {
        Intent i = new Intent(this, PaginaEditarPrenda.class);
        i.putExtra("prenda", prendaActual);
        this.startActivity(i);
    }


    // funciones para marcar como limpia o no una prenda cuando me la reconoce el RFID
    public void determinarLimpieza() {
        Log.i("MQTT", "determinarLimpieza");
        // primero comprobamos en que estado se encontraba
        // como hemos guardado este campo como boolean, no se usa get(), si no que usamos is()
        if(prendaActual.isLimpieza()) {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.PaginaPrenda_Rfid_sacar_titulo))
                    .setMessage(getResources().getString(R.string.PaginaPrenda_Rfid_sacar_mensaje))
                    .setPositiveButton(getResources().getString(R.string.PaginaPrenda_Rfid_sacar_confirmar), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            prendaActual.setLimpieza(false);
                            Log.i("MQTT", "false");
                            //List<Long> listadoUsos = new ArrayList<>();
                            //listadoUsos = prendaActual.getContadorUsos();
                            //listadoUsos.add(Long.parseLong(fechaFormateada));
                            //prendaActual.setContadorUsos(listadoUsos);
                            Log.i("MQTT", prendaActual.getDocId());
                            Prenda.actualizarPrenda(prendaActual, prendaActual.getDocId(), usuario.getUid());
                            refrescar(prendaActual);
                        }})
                    .setNegativeButton(getResources().getString(R.string.PaginaPrenda_Rfid_sacar_cancelar), null)
                    .show();
        }
        if(!prendaActual.isLimpieza()) {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.PaginaPrenda_Rfid_meter_titulo))
                    .setMessage(getResources().getString(R.string.PaginaPrenda_Rfid_meter_mensaje))
                    .setPositiveButton(getResources().getString(R.string.PaginaPrenda_Rfid_meter_confirmar), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            prendaActual.setLimpieza(true);
                            long contadorUsosPrendaAcual = prendaActual.getContadorUsos();
                            contadorUsosPrendaAcual ++;
                            prendaActual.setContadorUsos(contadorUsosPrendaAcual);
                            Prenda.actualizarPrenda(prendaActual, prendaActual.getDocId(), usuario.getUid());
                            refrescar(prendaActual);
                        }})
                    .setNegativeButton(getResources().getString(R.string.PaginaPrenda_Rfid_meter_cancelar), null)
                    .show();
        }
    }

    public void desplegarMenuInfoPrenda() {
        menuPrenda.animate().translationY(0).setDuration(1000);
        rectangulo.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();

        // menu inicialmente plegado
        texto.setAlpha(1.0f);
        nofavoritos.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
        favoritos.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
        lavadora.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
        color1.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
        color4.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
        color3.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
        color5.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
        ajustes.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
        bar.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
        textoCombina.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
        recuadroFav.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
        recuadroLavadora.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
        botonInfo.animate().rotationX(180).setDuration(1000);
        desplegado = true;
    }

    public void plegarMenuInfoPrenda() {
        menuPrenda.animate().translationY(480).setDuration(1000);
        rectangulo.animate().alpha(0.8f).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
        texto.animate().alpha(0).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
        nofavoritos.animate().alpha(0).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
        favoritos.animate().alpha(0).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
        lavadora.animate().alpha(0).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
        color1.animate().alpha(0).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
        color4.animate().alpha(0).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
        color3.animate().alpha(0).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
        color5.animate().alpha(0).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
        ajustes.animate().alpha(0).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
        bar.animate().alpha(0).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
        textoCombina.animate().alpha(0).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
        recuadroFav.animate().alpha(0).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
        recuadroLavadora.animate().alpha(0).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
        botonInfo.animate().rotationX(0).setDuration(1000);
        desplegado = false;
    }

    // no refresca como tal, pero recarga de nuevo la página
    private void refrescar(Prenda prendaActual) {
        Intent intent = new Intent(this, PaginaPrenda.class);
        intent.putExtra("prenda", prendaActual);
        this.startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}