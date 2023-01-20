package com.example.grupo1_1.smartclosset.pojos;

import com.google.android.gms.maps.model.LatLng;

public class Tiendas {
    /**
     * Clase {@link Tiendas}, se usa en la pagina del mapa para crear las tiendas de
     * ropa en Playa de gandia, puesto que la Api de rutas de Google es de pago
     */
    final LatLng posicion;
    final String nombre;

    public Tiendas(LatLng posicion, String nombre) {
        this.posicion = posicion;
        this.nombre = nombre;
    }

    public LatLng getPosicion() {
        return posicion;
    }

    public String getNombre() {
        return nombre;
    }
}