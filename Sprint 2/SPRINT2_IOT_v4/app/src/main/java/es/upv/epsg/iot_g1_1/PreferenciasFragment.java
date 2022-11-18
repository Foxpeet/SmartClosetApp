package es.upv.epsg.iot_g1_1;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

public class PreferenciasFragment extends PreferenceFragmentCompat {
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreatePreferences(Bundle savedInstanceState,
                                    String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        //permisos gps
        SwitchPreferenceCompat gpsSwitch = (SwitchPreferenceCompat) findPreference("gpsSwitch");

        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,false);
                            if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                            } else {
                                // No location access granted.
                                gpsSwitch.setChecked(false);
                            }
                        }
                );
        gpsSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if(!((Boolean) newValue)) {
                    //Log.i("Preferencias", "NO ACTIVADO.");

                } else {
                    //Log.i("Preferencias", "ACTIVADO.");
                    locationPermissionRequest.launch(new String[] {
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    });


                }
                return true;
            }
        });
    }
}

