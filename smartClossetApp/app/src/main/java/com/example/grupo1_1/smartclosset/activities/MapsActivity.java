package com.example.grupo1_1.smartclosset.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.grupo1_1.smartclosset.R;
import com.example.grupo1_1.smartclosset.databinding.ActivityMapsBinding;
import com.example.grupo1_1.smartclosset.pojos.Tiendas;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private GoogleApiClient googleApiClient;
    private com.google.android.gms.location.LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_Code = 99;

    static String COMERCIO[]={"Zara", "Bershka", "Pull and Bear", "Velvet", "Lalola", "Blanc du Nill", "Choc", " Camiña", "Sekoya", "Diez Euro", "Lukini", "Faustino Salcedo S L", "CUPLÉ ", "Coco", "Tejidos Barcelona", "Mumbai Clothing", "Carolina Sierra Boutique", "Pronovias Gandía", "Surfers Company", "Zeeman", "Pantys y Medias Irene", "Vestimoda", "Creaciones Aitana", "Calzedonia", "Fashion Ropa"};
    static double LATITUD[]={ 38.96907692678537, 38.969726738255524, 38.969675336525945, 38.96766486181403, 38.969675336525945 , 38.994774700513695, 38.99391696146632 ,38.99827119480312, 38.999048284872835, 38.970370886539094, 38.96783367550455 , 38.967017823250934, 38.96695194234013, 38.9672915943874, 38.96684983869408, 38.966275937396766, 38.966489186141, 38.96682146486913, 38.96337174264142, 38.96673792685668, 38.96550963922185, 38.96484333783227, 38.966682370452695, 38.968067578448384, 38.972809382838435};
    static double LONGITUD[]={ -0.16917261695167768,-0.168806243313667,-0.17017470876707388 , -0.1796952937155137 , -0.17017470876707388,-0.16035905355754124 ,-0.16197733263028233, -0.15782953539623246, -0.158370442319182, -0.17809013139914742, -0.18147675224060844, -0.1811578965425129, -0.18119367285614585, -0.18102608906996395, -0.18257681646718024, -0.18307953896448778, -0.181403153185224, -0.1810114282037971, -0.18556206036798836, -0.1894494572488566, -0.19184827099419186, -0.18886406041547127, -0.18673582649226905, -0.18435311415901162, -0.18403011262307564};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkUserLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    //Muestra el mapa con mi ubicación si acepto el permiso
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        ArrayList <Tiendas> todasTiendas = new ArrayList<>();

        for(int i = 0; i<COMERCIO.length; i++) {
            String comercios = COMERCIO[i];
            double lat = LATITUD[i];
            double lon = LONGITUD[i];
            LatLng posicion = new LatLng(lat, lon);
            Tiendas tiendas = new Tiendas(posicion, comercios);
            todasTiendas.add(tiendas);
        }

        for(int j=0; j<todasTiendas.size();j++){
            Tiendas aux;
            aux = todasTiendas.get(j);
            mMap.addMarker(new MarkerOptions()
                    .position(aux.getPosicion())
                    .title(aux.getNombre())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .anchor(0.5f, 0.5f));
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    //permisos
    public boolean checkUserLocationPermission(){//Si tiene el permiso deja, si no, devuelve el permiso denegado
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            }
            return false;
        }else{
            return true;
        }
    }

    //permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Request_User_Location_Code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "Permission Denied...", Toast.LENGTH_SHORT).show();
                }
        }
    }

    //mostrar mapa
    protected  synchronized void buildGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    //marcador de localizacion del usuario
    @Override
    public void onLocationChanged(@NonNull Location location) {
        lastLocation = location;

        if(currentUserLocationMarker != null){
            currentUserLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(getResources().getString(R.string.Mapa_ubicacionUsuario));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)); //color de los marcadores (cambiar en un futuro)

        currentUserLocationMarker = mMap.addMarker(markerOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));//para tocar lo cerca que esta

        if(googleApiClient != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new com.google.android.gms.location.LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, LandingPage.class);
        this.startActivity(i);
    }
}